package org.zstack.header.vm;

public interface VmPubInstanceFactory {
    VmPubInstanceType getType();
    
    VmPubInstanceEO createVmInstance(VmPubInstanceEO vo, CreateECSInstanceMsg msg);
    
    VmInstance getVmInstance(VmInstanceVO vo);
}
