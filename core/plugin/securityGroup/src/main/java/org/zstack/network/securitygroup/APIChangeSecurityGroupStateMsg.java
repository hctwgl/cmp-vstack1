package org.zstack.network.securitygroup;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * @api
 * change state of security group
 *
 * .. note:: meanings of security group state are undefined yet, these states are reserved for future use.
 *
 * @category security group
 *
 * @since 0.1.0
 *
 * @cli
 *
 * @httpMsg
 * {
"org.zstack.network.securitygroup.APIChangeSecurityGroupStateMsg": {
"securityGroupUuid": "6a6eb010bdcb4b6296ea1972c437c459",
"stateEvent": "enable",
"session": {
"uuid": "8a90d901c3da4182becfbbceeaa5c236"
}
}
}
 *
 * @msg
 * {
"org.zstack.network.securitygroup.APIChangeSecurityGroupStateMsg": {
"securityGroupUuid": "6a6eb010bdcb4b6296ea1972c437c459",
"stateEvent": "enable",
"session": {
"uuid": "8a90d901c3da4182becfbbceeaa5c236"
},
"timeout": 1800000,
"id": "c81269c2558a43868893431398024c23",
"serviceId": "api.portal"
}
}
 *
 * @result
 * see :ref:`APIChangeSecurityGroupStateEvent`
 */
@Action(category = SecurityGroupConstant.ACTION_CATEGORY)
public class APIChangeSecurityGroupStateMsg extends APIMessage {
    @APIParam(resourceType = SecurityGroupVO.class, checkAccount = true, operationTarget = true)
    private String uuid;
    @APIParam(validValues = {"enable", "disable"})
    private String stateEvent;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String securityGroupUuid) {
        this.uuid = securityGroupUuid;
    }

    public String getStateEvent() {
        return stateEvent;
    }

    public void setStateEvent(String stateEvent) {
        this.stateEvent = stateEvent;
    }
}
