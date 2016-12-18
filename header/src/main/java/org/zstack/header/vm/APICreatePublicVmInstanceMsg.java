package org.zstack.header.vm;

import org.zstack.header.cluster.ClusterVO;
import org.zstack.header.configuration.DiskOfferingVO;
import org.zstack.header.configuration.InstanceOfferingVO;
import org.zstack.header.host.HostVO;
import org.zstack.header.identity.Action;
import org.zstack.header.image.ImageVO;
import org.zstack.header.message.APICreateMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.network.l3.L3NetworkVO;
import org.zstack.header.tag.TagResourceType;
import org.zstack.header.zone.ZoneVO;

import java.util.List;
/**
 * @api
 *
 * create a new vm instance
 *
 * @since 0.1.0
 *
 * @cli
 *
 * @httpMsg
 *{
"org.zstack.header.vm.APICreateVmInstanceMsg": {
"name": "TestVm",
"instanceOfferingUuid": "1618154b462a48749ca9b114cf4a2979",
"imageUuid": "99a5eea648954ef7be2b8ede8f34fe26",
"l3NetworkUuids": [
"c4f6a370f80443798cc460ee07d56ff1",
"f5fbd96e0df745bdb7bc4f4c19febe65",
"c60285dca24d43a4b9a2e536674ddca1"
],
"type": "UserVm",
"dataDiskOfferingUuids": [],
"description": "Test",
"session": {
"uuid": "49c7e4c1fc18499a9477dd426436a8a4"
}
}
}
 *
 * @msg
 *
 * {
"org.zstack.header.vm.APICreateVmInstanceMsg": {
"name": "TestVm",
"instanceOfferingUuid": "1618154b462a48749ca9b114cf4a2979",
"imageUuid": "99a5eea648954ef7be2b8ede8f34fe26",
"l3NetworkUuids": [
"c4f6a370f80443798cc460ee07d56ff1",
"f5fbd96e0df745bdb7bc4f4c19febe65",
"c60285dca24d43a4b9a2e536674ddca1"
],
"type": "UserVm",
"dataDiskOfferingUuids": [],
"description": "Test",
"session": {
"uuid": "49c7e4c1fc18499a9477dd426436a8a4"
},
"timeout": 1800000,
"id": "add5fb2198f14980adf26db572d035c5",
"serviceId": "api.portal",
"creatingTime": 1398912618016
}
}
 *
 * @result
 *
 * See :ref:`APICreateVmInstanceEvent`
 */
@TagResourceType(VmInstanceVO.class)
@Action(category = VmInstanceConstant.ACTION_CATEGORY)
public class APICreatePublicVmInstanceMsg extends APICreateMessage {
   
    /**
     * @desc max length of 255 characters
     */
    @APIParam(maxLength = 255)
    private String hostname;
    
    @APIParam( required = true)
    private String cloudType;
    
    @APIParam(required = true)
    private String accountUuid;
    
    private String description;
    private String memorySize;
    private String cpuInfo;
    private String image;
      
    
    public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	 
	public String getAccountUuid() {
		return accountUuid;
	}
	public void setAccountUuid(String accountUuid) {
		this.accountUuid = accountUuid;
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
	

	 
}
