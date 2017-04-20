package org.zstack.header.network.l3;

import org.zstack.header.identity.AccountConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * @api
 * add dns to L3Network
 *
 * @category l3network
 *
 * @since 0.1.0
 *
 * @cli
 *
 * @httpMsg
 *
 * {
"org.zstack.header.network.l3.APIAddDnsToL3NetworkMsg": {
"session": {
"uuid": "b80d10a710f540528a0488f70cc18871"
},
"l3NetworkUuid": "564b95ad36b546a5ae9608e269cf1ac3",
"dns": "8.8.8.8"
}
}
 *
 * @msg
 * {
"org.zstack.header.network.l3.APIAddDnsToL3NetworkMsg": {
"session": {
"uuid": "b80d10a710f540528a0488f70cc18871"
},
"l3NetworkUuid": "564b95ad36b546a5ae9608e269cf1ac3",
"dns": "8.8.8.8",
"timeout": 1800000,
"id": "cffeac3ad46d4fffa4262ecc8aaaa699",
"serviceId": "api.portal"
}
}
 *
 * @result
 * see :ref:`APIAddDnsToL3NetworkEvent`
 */
@Action(category = L3NetworkConstant.ACTION_CATEGORY)
public class APIAddDnsToL3NetworkMsg extends APIMessage implements L3NetworkMessage {
    /**
     * @desc l3Network uuid
     */
	@APIParam(resourceType = L3NetworkVO.class, checkAccount = true, operationTarget = true)
	private String l3NetworkUuid;
    /**
     * @desc dns in IPv4
     */
	@APIParam
	private String dns;

	public String getL3NetworkUuid() {
		return l3NetworkUuid;
	}
	public void setL3NetworkUuid(String l3NetworkUuid) {
		this.l3NetworkUuid = l3NetworkUuid;
	}
	public String getDns() {
		return dns;
	}
	public void setDns(String dns) {
		this.dns = dns;
	}
}
