package org.zstack.compute.vm;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.vm.*;

public class ECSVmFactory implements VmPubInstanceFactory {
    private static final VmPubInstanceType type = new VmPubInstanceType("ECS");
    
    @Autowired
    private DatabaseFacade dbf;
    
    @Override
    public VmInstance getVmInstance(VmInstanceVO vo) {
        return new VmInstanceBase(vo);
    }

	@Override
	public VmPubInstanceType getType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public VmPubInstanceEO createVmInstance(VmPubInstanceEO vo, CreateECSInstanceMsg msg) {
		// TODO Auto-generated method stub
	        vo = dbf.persistAndRefresh(vo);
	        return vo;
	}

}
