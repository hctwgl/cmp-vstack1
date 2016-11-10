package org.zstack.header.vm;

import org.zstack.header.core.ApiTimeout;
import org.zstack.header.message.NeedReplyMessage;

import java.util.List;

/**
 * Created by david on 8/4/16.
 */
@ApiTimeout(apiClasses = {APICreateVmInstanceMsg.class})
public class CreateECSInstanceMsg extends NeedReplyMessage     {
    private String accountUuid;
	private String name;
    private String type;
    private String consolePassword;
    private String accesskeyID;
    private String accesskeyKey;
    private String region;
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getAccountUuid() {
		return accountUuid;
	}

	public void setAccountUuid(String accountUuid) {
		this.accountUuid = accountUuid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getConsolePassword() {
		return consolePassword;
	}

	public void setConsolePassword(String consolePassword) {
		this.consolePassword = consolePassword;
	}

	public String getAccesskeyID() {
		return accesskeyID;
	}

	public void setAccesskeyID(String accesskeyID) {
		this.accesskeyID = accesskeyID;
	}

	public String getAccesskeyKey() {
		return accesskeyKey;
	}

	public void setAccesskeyKey(String accesskeyKey) {
		this.accesskeyKey = accesskeyKey;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}


	public static CreateECSInstanceMsg valueOf(final APICreatePublicVmInstanceMsg msg) {
        CreateECSInstanceMsg cmsg = new CreateECSInstanceMsg();
        cmsg.setAccesskeyID(msg.getAccesskeyID());
        cmsg.setAccesskeyKey(msg.getAccesskeyKey());
        cmsg.setAccountUuid(msg.getSession().getAccountUuid());
        cmsg.setName(msg.getName());
        cmsg.setType(msg.getType());
        return cmsg;
    }
}
