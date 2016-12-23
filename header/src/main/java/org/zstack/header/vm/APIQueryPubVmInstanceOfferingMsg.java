package org.zstack.header.vm;

import org.zstack.header.message.APISyncCallMessage;

/**
 * @api
 *
 * @since 0.1.0
 *
 * @cli
 *
 * @httpMsg
 * {
"org.zstack.header.host.APIGetHypervisorTypesMsg": {
"session": {
"uuid": "c58ec5b783ea458a8c2234c5130b7299"
}
}
}
 *
 * @msg
 * {
"org.zstack.header.host.APIGetHypervisorTypesMsg": {
"session": {
"uuid": "c58ec5b783ea458a8c2234c5130b7299"
},
"timeout": 1800000,
"serviceId": "api.portal"
}
}
 *
 * @result
 *
 * see :ref:`APIGetHypervisorTypesReply`
 */
public class APIQueryPubVmInstanceOfferingMsg extends APISyncCallMessage {
	private String cloudType;

	public String getCloudType() {
		return cloudType;
	}

	public void setCloudType(String cloudType) {
		this.cloudType = cloudType;
	}
	
}
