package org.zstack.ldap;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.errorcode.OperationFailureException;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.identity.*;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.identity.AccountManager;
import org.zstack.identity.IdentityGlobalConfig;
import org.zstack.tag.TagManager;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by miao on 16-9-6.
 */
public class LdapManagerImpl extends AbstractService implements LdapManager {
    private static final CLogger logger = Utils.getLogger(LdapManagerImpl.class);

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private AccountManager acntMgr;
    @Autowired
    private TagManager tagMgr;
    @Autowired
    private CloudBus bus;
    @Autowired
    private ErrorFacade errf;

    class LdapTemplateContextSource {
        private LdapTemplate ldapTemplate;
        private LdapContextSource ldapContextSource;

        LdapTemplateContextSource(LdapTemplate ldapTemplate, LdapContextSource ldapContextSource) {
            this.ldapTemplate = ldapTemplate;
            this.ldapContextSource = ldapContextSource;
        }


        public LdapTemplate getLdapTemplate() {
            return ldapTemplate;
        }

        public LdapContextSource getLdapContextSource() {
            return ldapContextSource;
        }
    }

    @Transactional(readOnly = true)
    public LdapServerVO getLdapServer() {
        SimpleQuery<LdapServerVO> sq = dbf.createQuery(LdapServerVO.class);
        List<LdapServerVO> ldapServers = sq.list();
        if (ldapServers.isEmpty()) {
            throw new CloudRuntimeException("No ldap server record in database.");
        }
        if (ldapServers.size() > 1) {
            throw new CloudRuntimeException("More than one ldap server record in database.");
        }
        return ldapServers.get(0);
    }

    public LdapTemplateContextSource readLdapServerConfiguration() {
        LdapServerVO ldapServer = getLdapServer();

        LdapContextSource ldapContextSource;
        ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl(ldapServer.getUrl());
        ldapContextSource.setBase(ldapServer.getBase());
        ldapContextSource.setUserDn(ldapServer.getUsername());
        ldapContextSource.setPassword(ldapServer.getPassword());

        LdapTemplate ldapTemplate;
        ldapTemplate = new LdapTemplate();
        ldapTemplate.setContextSource(ldapContextSource);

        try {
            ldapContextSource.afterPropertiesSet();
            logger.info("LDAP Context Source loaded ");
        } catch (Exception e) {
            logger.error("LDAP Context Source not loaded ", e);
            throw new CloudRuntimeException("LDAP Context Source not loaded", e);
        }

        return new LdapTemplateContextSource(ldapTemplate, ldapContextSource);
    }

    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg) {


        bus.dealWithUnknownMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {

        if (msg instanceof APILogInByLdapMsg) {
            handle((APILogInByLdapMsg) msg);
        } else if (msg instanceof APIAddLdapServerMsg) {
            handle((APIAddLdapServerMsg) msg);
        } else if (msg instanceof APIDeleteLdapServerMsg) {
            handle((APIDeleteLdapServerMsg) msg);
        } else if (msg instanceof APIBindLdapAccountMsg) {
            handle((APIBindLdapAccountMsg) msg);
        } else if (msg instanceof APIUnbindLdapAccountMsg) {
            handle((APIUnbindLdapAccountMsg) msg);
        } else if (msg instanceof APITestAddLdapServerConnectionMsg) {
            handle((APITestAddLdapServerConnectionMsg) msg);
        } else if (msg instanceof APIUpdateLdapServerMsg) {
            handle((APIUpdateLdapServerMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }


    public boolean isValid(String uid, String password) {
        LdapTemplateContextSource ldapTemplateContextSource = readLdapServerConfiguration();
        try {
            AndFilter filter = new AndFilter();
            filter.and(new EqualsFilter("uid", uid));
            boolean valid = ldapTemplateContextSource.getLdapTemplate().
                    authenticate(getDnByUid(ldapTemplateContextSource, uid), filter.toString(), password);
            logger.info(String.format("isValid success userName:%s, isValid:%s", uid, valid));
            return valid;
        } catch (NamingException e) {
            logger.info("isValid fail userName:" + uid, e);
            return false;
        } catch (Exception e) {
            logger.info("isValid error userName:" + uid, e);
            return false;
        }
    }

    @Transactional
    private LdapAccountRefInventory bindLdapAccount(String accountUuid, String ldapUid) {
        LdapAccountRefVO ref = new LdapAccountRefVO();
        ref.setUuid(Platform.getUuid());
        ref.setAccountUuid(accountUuid);
        ref.setLdapServerUuid(getLdapServer().getUuid());
        ref.setLdapUid(ldapUid);
        ref = dbf.persistAndRefresh(ref);
        return LdapAccountRefInventory.valueOf(ref);
    }

    public String getDnByUid(LdapTemplateContextSource ldapTemplateContextSource, String uid) {
        return getUserDn(ldapTemplateContextSource.getLdapTemplate(), "uid", uid).
                replace("," + ldapTemplateContextSource.getLdapContextSource().getBaseLdapPathAsString(), "");
    }

    public String getUserDn(LdapTemplate ldapTemplate, String key, String val) {
        String dn = "";
        try {
            EqualsFilter f = new EqualsFilter(key, val);
            List<Object> result = ldapTemplate.search("", f.toString(), new AbstractContextMapper<Object>() {
                @Override
                protected Object doMapFromContext(DirContextOperations ctx) {
                    return ctx.getNameInNamespace();
                }
            });
            if (result.size() == 1) {
                dn = result.get(0).toString();
            } else if (result.size() > 1) {
                throw new CloudRuntimeException("More than one ldap search result");
            } else {
                throw new CloudRuntimeException("No ldap search result");
            }
            logger.info(String.format("getDn success key:%s, val:%s, dn:%s", key, val, dn));
        } catch (NamingException e) {
            logger.error(String.format("getDn error key:%s, val:%s", key, val), e);
        } catch (Exception e) {
            logger.error(String.format("getDn error key:%s, val:%s", key, val), e);
        }
        return dn;
    }

    public String getId() {
        return bus.makeLocalServiceId(LdapConstant.SERVICE_ID);
    }

    private SessionInventory getSession(String accountUuid, String userUuid) {
        int maxLoginTimes = org.zstack.identity.IdentityGlobalConfig.MAX_CONCURRENT_SESSION.value(Integer.class);
        SimpleQuery<SessionVO> query = dbf.createQuery(SessionVO.class);
        query.add(SessionVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
        query.add(SessionVO_.userUuid, SimpleQuery.Op.EQ, userUuid);
        long count = query.count();
        if (count >= maxLoginTimes) {
            String err = String.format("Login sessions hit limit of max allowed concurrent login sessions, max allowed: %s", maxLoginTimes);
            throw new BadCredentialsException(err);
        }

        int sessionTimeout = IdentityGlobalConfig.SESSION_TIMEOUT.value(Integer.class);
        SessionVO svo = new SessionVO();
        svo.setUuid(Platform.getUuid());
        svo.setAccountUuid(accountUuid);
        svo.setUserUuid(userUuid);
        long expiredTime = getCurrentSqlDate().getTime() + TimeUnit.SECONDS.toMillis(sessionTimeout);
        svo.setExpiredDate(new Timestamp(expiredTime));
        svo = dbf.persistAndRefresh(svo);
        SessionInventory session = SessionInventory.valueOf(svo);
        return session;
    }

    @Transactional(readOnly = true)
    private Timestamp getCurrentSqlDate() {
        Query query = dbf.getEntityManager().createNativeQuery("select current_timestamp()");
        return (Timestamp) query.getSingleResult();
    }

    public boolean start() {
        return true;
    }

    public boolean stop() {
        return true;
    }

    private void handle(APILogInByLdapMsg msg) {
        APILogInByLdapReply reply = new APILogInByLdapReply();

        SimpleQuery<LdapAccountRefVO> q = dbf.createQuery(LdapAccountRefVO.class);
        q.add(LdapAccountRefVO_.ldapUid, SimpleQuery.Op.EQ, msg.getUid());
        LdapAccountRefVO vo = q.find();
        if (vo == null) {
            reply.setError(errf.instantiateErrorCode(IdentityErrors.AUTHENTICATION_ERROR,
                    "No LdapAccountRef Exist."));
            bus.reply(msg, reply);
            return;
        }
        if (isValid(vo.getLdapUid(), msg.getPassword())) {
            reply.setInventory(getSession(vo.getAccountUuid(), vo.getAccountUuid()));

            SimpleQuery<AccountVO> sq = dbf.createQuery(AccountVO.class);
            sq.add(AccountVO_.uuid, SimpleQuery.Op.EQ, vo.getAccountUuid());
            AccountVO avo = sq.find();
            if (avo == null) {
                throw new CloudRuntimeException(String.format("Account[uuid:%s] Not Found!!!", vo.getAccountUuid()));
            }
            reply.setAccountInventory(AccountInventory.valueOf(avo));
        } else {
            reply.setError(errf.instantiateErrorCode(IdentityErrors.AUTHENTICATION_ERROR,
                    "Login Failed."));
        }
        bus.reply(msg, reply);
    }

    private void handle(APIAddLdapServerMsg msg) {
        APIAddLdapServerEvent evt = new APIAddLdapServerEvent(msg.getId());

        SimpleQuery<LdapServerVO> sq = dbf.createQuery(LdapServerVO.class);
        List<LdapServerVO> ldapServers = sq.list();
        if (ldapServers.isEmpty()) {
            LdapServerVO ldapServerVO = new LdapServerVO();
            ldapServerVO.setUuid(Platform.getUuid());
            ldapServerVO.setName(msg.getName());
            ldapServerVO.setDescription(msg.getDescription());
            ldapServerVO.setUrl(msg.getUrl());
            ldapServerVO.setBase(msg.getBase());
            ldapServerVO.setUsername(msg.getUsername());
            ldapServerVO.setPassword(msg.getPassword());

            ldapServerVO = dbf.persistAndRefresh(ldapServerVO);
            LdapServerInventory inv = LdapServerInventory.valueOf(ldapServerVO);
            evt.setInventory(inv);
        } else {
            evt.setErrorCode(errf.instantiateErrorCode(LdapErrors.MORE_THAN_ONE_LDAP_SERVER,
                    "There has been a ldap server record. " +
                            "You'd better remove it before adding a new one!"));
        }


        bus.publish(evt);
    }

    private void handle(APIDeleteLdapServerMsg msg) {
        APIDeleteLdapServerEvent evt = new APIDeleteLdapServerEvent(msg.getId());

        dbf.removeByPrimaryKey(msg.getUuid(), LdapServerVO.class);

        bus.publish(evt);
    }

    private void handle(APIBindLdapAccountMsg msg) {
        APIBindLdapAccountEvent evt = new APIBindLdapAccountEvent(msg.getId());

        // account check
        SimpleQuery<AccountVO> sq = dbf.createQuery(AccountVO.class);
        sq.add(AccountVO_.uuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        AccountVO avo = sq.find();
        if (avo == null) {
            evt.setErrorCode(errf.instantiateErrorCode(LdapErrors.CANNOT_FIND_ACCOUNT,
                    String.format("cannot find the specified account[uuid:%s]", msg.getAccountUuid())));
            bus.publish(evt);
            return;
        }
        if (avo.getType().equals(AccountType.SystemAdmin)) {
            evt.setErrorCode(errf.instantiateErrorCode(LdapErrors.CANNOT_BIND_ADMIN_ACCOUNT,
                    "cannot bind ldap uid to admin account."));
            bus.publish(evt);
            return;
        }

        // bind op
        LdapTemplateContextSource ldapTemplateContextSource = readLdapServerConfiguration();
        if (getDnByUid(ldapTemplateContextSource, msg.getLdapUid()).equals("")) {
            throw new OperationFailureException(errf.instantiateErrorCode(LdapErrors.UNABLE_TO_GET_SPECIFIED_LDAP_UID,
                    "cannot find uid on ldap server."));
        }
        try {
            evt.setInventory(bindLdapAccount(msg.getAccountUuid(), msg.getLdapUid()));
        } catch (JpaSystemException e) {
            if (e.getRootCause() instanceof MySQLIntegrityConstraintViolationException) {
                evt.setErrorCode(errf.instantiateErrorCode(LdapErrors.BIND_SAME_LDAP_UID_TO_MULTI_ACCOUNT,
                        "The ldap uid has been bound to an account. "));
            } else {
                throw e;
            }
        }
        bus.publish(evt);
    }

    private void handle(APIUnbindLdapAccountMsg msg) {
        APIUnbindLdapAccountEvent evt = new APIUnbindLdapAccountEvent(msg.getId());

        dbf.removeByPrimaryKey(msg.getUuid(), LdapAccountRefVO.class);

        bus.publish(evt);
    }


    private void handle(APITestAddLdapServerConnectionMsg msg) {
        APITestAddLdapServerConnectionEvent evt = new APITestAddLdapServerConnectionEvent(msg.getId());

        LdapServerInventory inv = new LdapServerInventory();
        inv.setName(msg.getName());
        inv.setDescription(msg.getDescription());
        inv.setUrl(msg.getUrl());
        inv.setBase(msg.getBase());
        inv.setUsername(msg.getUsername());
        inv.setPassword(msg.getPassword());
        evt.setInventory(inv);
        boolean success = testAddLdapServerConnection(inv);
        evt.setSuccess(success);
        if (!success) {
            evt.setErrorCode(errf.instantiateErrorCode(LdapErrors.TEST_LDAP_CONNECTION_FAILED,
                    "Test ldap server connection failed. "));
        }

        bus.publish(evt);
    }

    private boolean testAddLdapServerConnection(LdapServerInventory inv) {
        LdapContextSource testLdapContextSource;
        testLdapContextSource = new LdapContextSource();
        testLdapContextSource.setUrl(inv.getUrl());
        testLdapContextSource.setBase(inv.getBase());
        testLdapContextSource.setUserDn(inv.getUsername());
        testLdapContextSource.setPassword(inv.getPassword());
        testLdapContextSource.setDirObjectFactory(DefaultDirObjectFactory.class);
        testLdapContextSource.setPooled(false);

        LdapTemplate testLdapTemplate;
        testLdapTemplate = new LdapTemplate();
        testLdapTemplate.setContextSource(testLdapContextSource);

        try {
            testLdapContextSource.afterPropertiesSet();
            logger.info("Test LDAP Context Source loaded ");
        } catch (Exception e) {
            logger.error("Test LDAP Context Source not loaded ", e);
            throw new CloudRuntimeException("Test LDAP Context Source not loaded", e);
        }

        try {
            AndFilter filter = new AndFilter();
            filter.and(new EqualsFilter("uid", ""));
            testLdapTemplate.authenticate("", filter.toString(), "");
            logger.info("LDAP connection was successful");
        } catch (Exception e) {
            logger.info("Cannot connect to LDAP server");
            logger.debug(e.toString());
            return false;
        }


        return true;
    }


    private void handle(APIUpdateLdapServerMsg msg) {
        APIUpdateLdapServerEvent evt = new APIUpdateLdapServerEvent(msg.getId());

        LdapServerVO ldapServerVO = dbf.findByUuid(msg.getLdapServerUuid(), LdapServerVO.class);
        if (msg.getName() != null) {
            ldapServerVO.setName(msg.getName());
        }
        if (msg.getDescription() != null) {
            ldapServerVO.setDescription(msg.getDescription());
        }
        if (msg.getUrl() != null) {
            ldapServerVO.setUrl(msg.getUrl());
        }
        if (msg.getBase() != null) {
            ldapServerVO.setBase(msg.getBase());
        }
        if (msg.getUsername() != null) {
            ldapServerVO.setUsername(msg.getUsername());
        }
        if (msg.getPassword() != null) {
            ldapServerVO.setPassword(msg.getPassword());
        }

        ldapServerVO = dbf.updateAndRefresh(ldapServerVO);
        evt.setInventory(LdapServerInventory.valueOf(ldapServerVO));

        bus.publish(evt);
    }

}
