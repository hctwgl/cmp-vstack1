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
    private String name;
    @APIParam(validValues = {"UserVm", "ApplianceVm","ECS"}, required = false)
    private String type;
    @APIParam(required = false)
    private String accesskeyID;
    private String accesskeyKey;
    
    
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
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

	 
}
