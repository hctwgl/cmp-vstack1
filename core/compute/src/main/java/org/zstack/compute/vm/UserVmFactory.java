package org.zstack.compute.vm;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.vm.*;
import org.zstack.utils.ObjectUtils;

public class UserVmFactory implements VmInstanceFactory {
    private static final VmInstanceType type = new VmInstanceType(VmInstanceConstant.USER_VM_TYPE);
    
    @Autowired
    private DatabaseFacade dbf;
    
    @Override
    public VmInstanceType getType() {
        return type;
    }

    @Override
    public VmInstanceVO createVmInstance(VmInstanceVO vo, CreateVmInstanceMsg msg) {
        vo.setType(type.toString());
        VmInstanceEO eo = new VmInstanceEO();
        ObjectUtils.copy(eo, vo);
        eo = dbf.persistAndRefresh(eo);
        return vo;
    }

    @Override
    public VmInstance getVmInstance(VmInstanceVO vo) {
        return new VmInstanceBase(vo);
    }

}
