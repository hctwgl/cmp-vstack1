package org.zstack.header.host;

import org.zstack.header.message.NeedReplyMessage;

/**
 * Created by david on 9/12/16.
 */
public class AddLocalHostMsg extends NeedReplyMessage   {
    private String name;
    private String description;
    private String managementIp;
    private String accountUuid;
    private String username;
    private String password;
    private int sshPort = 22;

    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public AddLocalHostMsg() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManagementIp() {
        return managementIp;
    }

    public void setManagementIp(String managementIp) {
        this.managementIp = managementIp;
    }

    public int getSshPort() {
        return sshPort;
    }

    public void setSshPort(int sshPort) {
        this.sshPort = sshPort;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

     
    

    public static AddLocalHostMsg valueOf(final AddLocalHostMsg msg) {
        AddLocalHostMsg amsg = new AddLocalHostMsg();

        amsg.setAccountUuid(msg.getAccountUuid());
        amsg.setName(msg.getName());
        amsg.setDescription(msg.getDescription());
        amsg.setManagementIp(msg.getManagementIp());
        return amsg;
    }
}
