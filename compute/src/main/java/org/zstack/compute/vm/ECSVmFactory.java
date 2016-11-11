package org.zstack.compute.vm;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.vm.*;

public class ECSVmFactory implements VmPubInstanceFactory {
    private static final VmInstanceType type = new VmInstanceType("ECS");
    
    @Autowired
    private DatabaseFacade dbf;
    
    @Override
    public VmECSInstanceVO getVmInstance(VmECSInstanceVO vo) {
        return vo;
    }

	@Override
	public VmECSInstanceVO createVmInstance(VmECSInstanceVO vo, CreateECSInstanceMsg msg) {
		// TODO Auto-generated method stub
	        vo = dbf.persistAndRefresh(vo);
	        return vo;
	}

	@Override
	public VmInstanceType getType() {
		// TODO Auto-generated method stub
		return type;
	}

}
