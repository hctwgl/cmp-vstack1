package org.zstack.header.vm;

public interface VmPubInstanceFactory {
	VmInstanceType getType();
    
    VmECSInstanceVO createVmInstance(VmECSInstanceVO vo, CreateECSInstanceMsg msg);
    
    VmECSInstanceVO getVmInstance(VmECSInstanceVO vo);
}
