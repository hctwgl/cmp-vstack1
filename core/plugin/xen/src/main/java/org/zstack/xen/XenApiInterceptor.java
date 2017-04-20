package org.zstack.xen;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.errorcode.SysErrors;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;

/**
 */
public class XenApiInterceptor implements ApiMessageInterceptor {
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private DatabaseFacade dbf;

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APIAddXenHostMsg) {
            validate((APIAddXenHostMsg) msg);
        }

        return msg;
    }

    private void validate(APIAddXenHostMsg msg) {
        SimpleQuery<XenHostVO> q = dbf.createQuery(XenHostVO.class);
        q.add(XenHostVO_.managementIp, Op.EQ, msg.getManagementIp());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(errf.instantiateErrorCode(SysErrors.INVALID_ARGUMENT_ERROR,
                    String.format("there has been a xen host having management ip[%s]", msg.getManagementIp())
            ));
        }
    }
}
