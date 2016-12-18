package org.zstack.header.vm;

import java.util.ArrayList;
import java.util.List;

import org.zstack.header.message.MessageReply;

public class GetPubVmInstanceListReply extends MessageReply {
	
	private List<ECSNode> vms;
	private List<PubVmInstanceEO> pubVo;
	private String cloudType;
	
	public String getCloudType() {
		return cloudType;
	}

	public void setCloudType(String cloudType) {
		this.cloudType = cloudType;
	}

	public List<ECSNode> getVms() {
		return vms;
	}

	public void setVms(List<ECSNode> vms) {
		this.vms = vms;
	}

	public GetPubVmInstanceListReply(){
		vms = new ArrayList<ECSNode>();
	}
	
	public List<PubVmInstanceEO> getPubVmVOFromVms()
	{
		List<PubVmInstanceEO> pubVo = new ArrayList<PubVmInstanceEO>();
		for(ECSNode ecs :vms ){
			PubVmInstanceEO vo =new PubVmInstanceEO();
			vo.setHostname(ecs.getName());
			vo.setCloudType("ECS");
			vo.setImage(ecs.getImage());
			vo.setMemorySize(ecs.getSize());
			vo.setState(ecs.getState());
			vo.setCreateDate(ecs.getCreateDate());
			vo.setProviderUuid(ecs.getId());
			String otherInfo = "Public IP : " +ecs.getPublic_ips()+"  "+"Private IP : " + ecs.getPrivate_ips() +"   "+"Extra : " +ecs.getExtra();
			vo.setOtherInfo(otherInfo);
			pubVo.add(vo);
		}
		return pubVo;
		
	}
	
}
