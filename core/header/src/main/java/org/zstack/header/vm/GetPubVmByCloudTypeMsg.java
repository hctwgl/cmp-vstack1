package org.zstack.header.vm;

import org.zstack.header.core.ApiTimeout;
import org.zstack.header.identity.PubAccountVO;
import org.zstack.header.message.NeedReplyMessage;

import java.util.List;

/**
 * Created by david on 8/4/16.
 */
@ApiTimeout(apiClasses = {APICreateVmInstanceMsg.class})
public class GetPubVmByCloudTypeMsg extends NeedReplyMessage   {
    private List<PubAccountVO> accounts;
    private String accountUUID;
    private String UUID;
     
    public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}

	public String getAccountUUID() {
		return accountUUID;
	}

	public void setAccountUUID(String accountUUID) {
		this.accountUUID = accountUUID;
	}

	public List<PubAccountVO> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<PubAccountVO> accounts) {
		this.accounts = accounts;
	}

	public static GetPubVmByCloudTypeMsg valueOf(final APICreateVmInstanceMsg msg) {
        GetPubVmByCloudTypeMsg cmsg = new GetPubVmByCloudTypeMsg();
 
        return cmsg;
    }
}
