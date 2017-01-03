package org.zstack.header.vm;

import java.util.ArrayList;
import java.util.List;

import org.zstack.header.message.MessageReply;

public class GetPubVmInstanceListReply extends MessageReply {
	
	private List<PubVmInstanceEO> vms;
	private List<PubVmInstanceEO> pubVo;
	private String cloudType;
	
	public String getCloudType() {
		return cloudType;
	}

	public void setCloudType(String cloudType) {
		this.cloudType = cloudType;
	}

	public List<PubVmInstanceEO> getVms() {
		return vms;
	}

	public void setVms(List<PubVmInstanceEO> vms) {
		this.vms = vms;
	}

	public GetPubVmInstanceListReply(){
		vms = new ArrayList<PubVmInstanceEO>();
	}
	
	 
	
}
