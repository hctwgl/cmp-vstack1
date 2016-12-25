package org.zstack.header.vm;

import org.zstack.header.core.ApiTimeout;
import org.zstack.header.message.NeedReplyMessage;

import java.util.List;

/**
 * Created by david on 8/4/16.
 */
@ApiTimeout(apiClasses = {APICreateVmInstanceMsg.class})
public class CreatePubVMInstanceMsg extends NeedReplyMessage     {
    private String pubAccountUuid;
	private String description;
    private String cloudType;
    private String hostname;
    private String memorySize;
    private String cpuInfo;
    private String image;
    private String accountUuid;
    private String diskSize;
    private String region;
    public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	private String instanceOferringUUid;
	 

	public String getInstanceOferringUUid() {
		return instanceOferringUUid;
	}

	public void setInstanceOferringUUid(String instanceOferringUUid) {
		this.instanceOferringUUid = instanceOferringUUid;
	}

	public String getDiskSize() {
		return diskSize;
	}

	public void setDiskSize(String diskSize) {
		this.diskSize = diskSize;
	}

	public String getAccountUuid() {
		return accountUuid;
	}

	public void setAccountUuid(String accountUuid) {
		this.accountUuid = accountUuid;
	}

	public String getPubAccountUuid() {
		return pubAccountUuid;
	}

	public void setPubAccountUuid(String pubAccountUuid) {
		this.pubAccountUuid = pubAccountUuid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCloudType() {
		return cloudType;
	}

	public void setCloudType(String cloudType) {
		this.cloudType = cloudType;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(String memorySize) {
		this.memorySize = memorySize;
	}

	public String getCpuInfo() {
		return cpuInfo;
	}

	public void setCpuInfo(String cpuInfo) {
		this.cpuInfo = cpuInfo;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

	public static CreatePubVMInstanceMsg valueOf(final APICreatePublicVmInstanceMsg msg) {
		
        CreatePubVMInstanceMsg cmsg = new CreatePubVMInstanceMsg();
        cmsg.setAccountUuid(msg.getSession().getAccountUuid());
        cmsg.setCloudType(msg.getCloudType());
        cmsg.setCpuInfo(msg.getCpuInfo());
        cmsg.setDescription(msg.getDescription());
        cmsg.setHostname(msg.getHostname());
        cmsg.setMemorySize(msg.getMemorySize());
        cmsg.setImage(msg.getImage());
        return cmsg;
    }
}
