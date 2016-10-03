package org.zstack.xen;

import org.zstack.header.errorcode.ErrorCode;
import org.zstack.header.vm.VmInstanceInventory;

public interface KVMRebootVmExtensionPoint {
    void beforeRebootVmOnKvm(XenHostInventory host, VmInstanceInventory vm) throws KVMException;
    
    void rebootVmOnKvmSuccess(XenHostInventory host, VmInstanceInventory vm);
    
    void rebootVmOnKvmFailed(XenHostInventory host, VmInstanceInventory vm, ErrorCode err);
}
