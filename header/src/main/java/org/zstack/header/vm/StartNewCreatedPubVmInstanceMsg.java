package org.zstack.header.vm;

import org.zstack.header.core.ApiTimeout;
import org.zstack.header.message.NeedReplyMessage;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;

@ApiTimeout(apiClasses = { APICreateVmInstanceMsg.class })
public class StartNewCreatedPubVmInstanceMsg extends NeedReplyMessage implements VmInstanceMessage {
	private String Uuid;
	private String hostname;
	private String description;
	private String pubAccountUuid;
	private String image;
	private String cloudType;
	private String cpuInfo;
	private String memorySize;
	private String otherInfo;
	private String region;
	private String instanceOfferringUuid;
	public String getInstanceOfferringUuid() {
		return instanceOfferringUuid;
	}

	public void setInstanceOfferringUuid(String instanceOfferringUuid) {
		this.instanceOfferringUuid = instanceOfferringUuid;
	}

	private Timestamp createDate;
	private Timestamp lastOpDate;
	private String state;

	public String getUuid() {
		return Uuid;
	}

	public void setUuid(String uuid) {
		Uuid = uuid;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
 

	public String getPubAccountUuid() {
		return pubAccountUuid;
	}

	public void setPubAccountUuid(String pubAccountUuid) {
		this.pubAccountUuid = pubAccountUuid;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCloudType() {
		return cloudType;
	}

	public void setCloudType(String cloudType) {
		this.cloudType = cloudType;
	}

	public String getCpuInfo() {
		return cpuInfo;
	}

	public void setCpuInfo(String cpuInfo) {
		this.cpuInfo = cpuInfo;
	}

	public String getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(String memorySize) {
		this.memorySize = memorySize;
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public Timestamp getLastOpDate() {
		return lastOpDate;
	}

	public void setLastOpDate(Timestamp lastOpDate) {
		this.lastOpDate = lastOpDate;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String getVmInstanceUuid() {
		// TODO Auto-generated method stub
		return Uuid;
	}

}
