package org.zstack.xen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;
import org.zstack.compute.host.HostGlobalConfig;
import org.zstack.core.CoreGlobalProperty;
import org.zstack.core.ansible.AnsibleFacade;
import org.zstack.core.cloudbus.*;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigException;
import org.zstack.core.config.GlobalConfigUpdateExtensionPoint;
import org.zstack.core.config.GlobalConfigValidatorExtensionPoint;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.thread.AsyncThread;
import org.zstack.header.AbstractService;
import org.zstack.header.Component;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.host.*;
import org.zstack.header.managementnode.ManagementNodeReadyExtensionPoint;
import org.zstack.header.message.Message;
import org.zstack.header.message.MessageReply;
import org.zstack.header.message.NeedReplyMessage;
import org.zstack.header.network.l2.L2NetworkType;
import org.zstack.header.rest.RESTFacade;
import org.zstack.header.rest.SyncHttpCallHandler;
import org.zstack.header.volume.MaxDataVolumeNumberExtensionPoint;
import org.zstack.header.volume.VolumeConstant;
import org.zstack.header.volume.VolumeFormat;
import org.zstack.utils.CollectionUtils;
import org.zstack.utils.SizeUtils;
import org.zstack.utils.Utils;
import org.zstack.utils.function.Function;
import org.zstack.utils.logging.CLogger;
import org.zstack.xen.XenAgentCommands.ReconnectMeCmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XenHostFactory extends AbstractService implements HypervisorFactory, Component,
        ManagementNodeReadyExtensionPoint, MaxDataVolumeNumberExtensionPoint {
    private static final CLogger logger = Utils.getLogger(XenHostFactory.class);

    public static final HypervisorType hypervisorType = new HypervisorType(XenConstant.KVM_HYPERVISOR_TYPE);
    public static final VolumeFormat QCOW2_FORMAT = new VolumeFormat(VolumeConstant.VOLUME_FORMAT_QCOW2, hypervisorType);
    public static final VolumeFormat RAW_FORMAT = new VolumeFormat(VolumeConstant.VOLUME_FORMAT_RAW, hypervisorType);
    private List<XenHostConnectExtensionPoint> connectExtensions = new ArrayList<XenHostConnectExtensionPoint>();
    private Map<L2NetworkType, XenCompleteNicInformationExtensionPoint> completeNicInfoExtensions = new HashMap<L2NetworkType, XenCompleteNicInformationExtensionPoint>();
    private int maxDataVolumeNum;

    static {
        RAW_FORMAT.newFormatInputOutputMapping(hypervisorType, QCOW2_FORMAT.toString());
        QCOW2_FORMAT.setFirstChoice(hypervisorType);
    }

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private AnsibleFacade asf;
    @Autowired
    private ResourceDestinationMaker destMaker;
    @Autowired
    private CloudBus bus;
    @Autowired
    private RESTFacade restf;

    @Override
    public HostVO createHost(HostVO vo, AddHostMessage msg) {
        APIAddXenHostMsg amsg = (APIAddXenHostMsg) msg;
        XenHostVO kvo = new XenHostVO(vo);
        kvo.setUsername(amsg.getUsername());
        kvo.setPassword(amsg.getPassword());
        kvo.setPort(amsg.getSshPort());
        kvo = dbf.persistAndRefresh(kvo);
        return kvo;
    }

    @Override
    public Host getHost(HostVO vo) {
        XenHostVO kvo = dbf.findByUuid(vo.getUuid(), XenHostVO.class);
        XenHostContext context = getHostContext(vo.getUuid());
        if (context == null) {
            context = createHostContext(kvo);
        }
        return new XenHost(kvo, context);
    }

    private List<String> getHostManagedByUs() {
        int qun = 10000;
        long amount = dbf.count(HostVO.class);
        int times = (int)(amount / qun) + (amount % qun != 0 ? 1 : 0);
        List<String> hostUuids = new ArrayList<String>();
        int start = 0;
        for (int i=0; i<times; i++) {
            SimpleQuery<XenHostVO> q = dbf.createQuery(XenHostVO.class);
            q.select(HostVO_.uuid);
            // disconnected host will be handled by HostManager
            q.add(HostVO_.status, SimpleQuery.Op.EQ, HostStatus.Connected);
            q.setLimit(qun);
            q.setStart(start);
            List<String> lst = q.listValue();
            start += qun;
            for (String huuid : lst) {
                if (!destMaker.isManagedByUs(huuid)) {
                    continue;
                }
                hostUuids.add(huuid);
            }
        }

        return hostUuids;
    }

    @Override
    public HypervisorType getHypervisorType() {
        return hypervisorType;
    }

    @Override
    public HostInventory getHostInventory(HostVO vo) {
        XenHostVO kvo = vo instanceof XenHostVO ? (XenHostVO) vo : dbf.findByUuid(vo.getUuid(), XenHostVO.class);
        return XenHostInventory.valueOf(kvo);
    }

    @Override
    public HostInventory getHostInventory(String uuid) {
        XenHostVO vo = dbf.findByUuid(uuid, XenHostVO.class);
        return vo == null ? null : XenHostInventory.valueOf(vo);
    }

    private void populateExtensions() {
        connectExtensions = pluginRgty.getExtensionList(XenHostConnectExtensionPoint.class);
        for (XenCompleteNicInformationExtensionPoint ext : pluginRgty.getExtensionList(XenCompleteNicInformationExtensionPoint.class)) {
        	XenCompleteNicInformationExtensionPoint old = completeNicInfoExtensions.get(ext.getL2NetworkTypeVmNicOn());
            if (old != null) {
                throw new CloudRuntimeException(String.format("duplicate KVMCompleteNicInformationExtensionPoint[%s, %s] for type[%s]",
                        old.getClass().getName(), ext.getClass().getName(), ext.getL2NetworkTypeVmNicOn()));
            }
        	completeNicInfoExtensions.put(ext.getL2NetworkTypeVmNicOn(), ext);
        }
    }

    public XenCompleteNicInformationExtensionPoint getCompleteNicInfoExtension(L2NetworkType type) {
    	XenCompleteNicInformationExtensionPoint extp = completeNicInfoExtensions.get(type);
    	if (extp == null) {
    		throw new IllegalArgumentException(String.format("unble to fine KVMCompleteNicInformationExtensionPoint supporting L2NetworkType[%s]", type));
    	}
    	return extp;
    }


    private void deployAnsibleModule() {
        if (CoreGlobalProperty.UNIT_TEST_ON) {
            return;
        }

        asf.deployModule(XenConstant.ANSIBLE_MODULE_PATH, XenConstant.ANSIBLE_PLAYBOOK_NAME);
    }

    @Override
    public boolean start() {
        deployAnsibleModule();
        populateExtensions();

        maxDataVolumeNum = KVMGlobalConfig.MAX_DATA_VOLUME_NUM.value(int.class);
        KVMGlobalConfig.MAX_DATA_VOLUME_NUM.installUpdateExtension(new GlobalConfigUpdateExtensionPoint() {
            @Override
            public void updateGlobalConfig(GlobalConfig oldConfig, GlobalConfig newConfig) {
                maxDataVolumeNum = newConfig.value(int.class);
            }
        });
        KVMGlobalConfig.RESERVED_MEMORY_CAPACITY.installValidateExtension(new GlobalConfigValidatorExtensionPoint() {
            @Override
            public void validateGlobalConfig(String category, String name, String oldValue, String value) throws GlobalConfigException {
                if (!SizeUtils.isSizeString(value)) {
                    throw new GlobalConfigException(String.format("%s only allows a size string. A size string is a number with suffix 'T/t/G/g/M/m/K/k/B/b' or without suffix, but got %s",
                            KVMGlobalConfig.RESERVED_MEMORY_CAPACITY.getCanonicalName(), value));
                }
            }
        });

        restf.registerSyncHttpCallHandler(XenConstant.KVM_RECONNECT_ME, ReconnectMeCmd.class, new SyncHttpCallHandler<ReconnectMeCmd>() {
            @Override
            public String handleSyncHttpCall(ReconnectMeCmd cmd) {
                //TODO
                logger.warn(String.format("the kvm host[uuid:%s] asks the mgmt server to reconnect it for %s", cmd.hostUuid, cmd.reason));
                ReconnectHostMsg msg = new ReconnectHostMsg();
                msg.setHostUuid(cmd.hostUuid);
                bus.makeTargetServiceIdByResourceUuid(msg, HostConstant.SERVICE_ID, cmd.hostUuid);
                bus.send(msg);
                return null;
            }
        });

        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    public List<XenHostConnectExtensionPoint> getConnectExtensions() {
        return connectExtensions;
    }

    XenHostContext createHostContext(XenHostVO vo) {
        UriComponentsBuilder ub = UriComponentsBuilder.newInstance();
        ub.scheme(KVMGlobalProperty.AGENT_URL_SCHEME);
        ub.host(vo.getManagementIp());
        ub.port(KVMGlobalProperty.AGENT_PORT);
        if (!"".equals(KVMGlobalProperty.AGENT_URL_ROOT_PATH)) {
            ub.path(KVMGlobalProperty.AGENT_URL_ROOT_PATH);
        }
        String baseUrl = ub.build().toUriString();

        XenHostContext context = new XenHostContext();
        context.setInventory(XenHostInventory.valueOf(vo));
        context.setBaseUrl(baseUrl);
        return context;
    }

    public XenHostContext getHostContext(String hostUuid) {
        XenHostVO kvo = dbf.findByUuid(hostUuid, XenHostVO.class);
        return createHostContext(kvo);
    }

    @Override
    public String getHypervisorTypeForMaxDataVolumeNumberExtension() {
        return XenConstant.KVM_HYPERVISOR_TYPE;
    }

    @Override
    public int getMaxDataVolumeNumber() {
        return maxDataVolumeNum;
    }

    @Override
    @AsyncThread
    public void managementNodeReady() {
        if (CoreGlobalProperty.UNIT_TEST_ON) {
            return;
        }

        if (!asf.isModuleChanged(XenConstant.ANSIBLE_PLAYBOOK_NAME)) {
            return;
        }

        // KVM hosts need to deploy new agent
        // connect hosts even if they are ConnectionState is Connected

        List<String> hostUuids = getHostManagedByUs();
        if (hostUuids.isEmpty()) {
            return;
        }

        logger.debug(String.format("need to connect kvm hosts because kvm agent changed, uuids:%s", hostUuids));

        List<ConnectHostMsg> msgs = new ArrayList<ConnectHostMsg>();
        for (String huuid : hostUuids) {
            ConnectHostMsg msg = new ConnectHostMsg();
            msg.setNewAdd(false);
            msg.setUuid(huuid);
            bus.makeTargetServiceIdByResourceUuid(msg, HostConstant.SERVICE_ID, huuid);
            msgs.add(msg);
        }

        bus.send(msgs, HostGlobalConfig.HOST_LOAD_PARALLELISM_DEGREE.value(Integer.class), new CloudBusSteppingCallback() {
            @Override
            public void run(NeedReplyMessage msg, MessageReply reply) {
                ConnectHostMsg cmsg = (ConnectHostMsg)msg;
                if (!reply.isSuccess()) {
                    logger.warn(String.format("failed to connect kvm host[uuid:%s], %s", cmsg.getHostUuid(), reply.getError()));
                } else {
                    logger.debug(String.format("successfully to connect kvm host[uuid:%s]", cmsg.getHostUuid()));
                }
            }
        });
    }

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIXenRunShellMsg) {
            handle((APIXenRunShellMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(final APIXenRunShellMsg msg) {
        final APIXenRunShellEvent evt = new APIXenRunShellEvent(msg.getId());

        final List<KvmRunShellMsg> kmsgs = CollectionUtils.transformToList(msg.getHostUuids(), new Function<KvmRunShellMsg, String>() {
            @Override
            public KvmRunShellMsg call(String arg) {
                KvmRunShellMsg kmsg = new KvmRunShellMsg();
                kmsg.setHostUuid(arg);
                kmsg.setScript(msg.getScript());
                bus.makeTargetServiceIdByResourceUuid(kmsg, HostConstant.SERVICE_ID, arg);
                return kmsg;
            }
        });

        bus.send(kmsgs, new CloudBusListCallBack(msg) {
            @Override
            public void run(List<MessageReply> replies) {
                for (MessageReply r : replies) {
                    String hostUuid = kmsgs.get(replies.indexOf(r)).getHostUuid();

                    APIXenRunShellEvent.ShellResult result = new APIXenRunShellEvent.ShellResult();
                    if (!r.isSuccess()) {
                        result.setErrorCode(r.getError());
                    } else {
                        KvmRunShellReply kr = r.castReply();
                        result.setReturnCode(kr.getReturnCode());
                        result.setStderr(kr.getStderr());
                        result.setStdout(kr.getStdout());
                    }

                    evt.getInventory().put(hostUuid, result);
                }

                bus.publish(evt);
            }
        });
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(XenConstant.SERVICE_ID);
    }
}
