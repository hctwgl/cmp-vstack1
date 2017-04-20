package org.zstack.xen;

import org.zstack.header.errorcode.ErrorCode;
import org.zstack.header.vm.VmInstanceInventory;

public interface KVMStopVmExtensionPoint {
    void beforeStopVmOnKvm(XenHostInventory host, VmInstanceInventory vm) throws KVMException;
    
    void stopVmOnKvmSuccess(XenHostInventory host, VmInstanceInventory vm);
    
    void stopVmOnKvmFailed(XenHostInventory host, VmInstanceInventory vm, ErrorCode err);
}
