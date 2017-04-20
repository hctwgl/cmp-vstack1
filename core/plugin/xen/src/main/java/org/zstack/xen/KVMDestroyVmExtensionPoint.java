package org.zstack.xen;

import org.zstack.header.errorcode.ErrorCode;
import org.zstack.header.vm.VmInstanceInventory;

public interface KVMDestroyVmExtensionPoint {
    void beforeDestroyVmOnKvm(XenHostInventory host, VmInstanceInventory vm) throws KVMException;
    
    void destroyVmOnKvmSuccess(XenHostInventory host, VmInstanceInventory vm);
    
    void destroyVmOnKvmFailed(XenHostInventory host, VmInstanceInventory vm, ErrorCode err);
}
