package org.zstack.header.vm;

public interface VmPubInstanceFactory {
	VmInstanceType getType();
    
    VmECSInstanceVO createVmInstance(VmECSInstanceVO vo, CreatePubVMInstanceMsg msg);
    
    VmECSInstanceVO getVmInstance(VmECSInstanceVO vo);
}
