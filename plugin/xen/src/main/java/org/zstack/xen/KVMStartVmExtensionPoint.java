package org.zstack.xen;

import org.zstack.header.errorcode.ErrorCode;
import org.zstack.header.vm.VmInstanceSpec;
import org.zstack.xen.KVMAgentCommands.StartVmCmd;

public interface KVMStartVmExtensionPoint {
	void beforeStartVmOnKvm(KVMHostInventory host, VmInstanceSpec spec, StartVmCmd cmd) throws KVMException;
	
	void startVmOnKvmSuccess(KVMHostInventory host, VmInstanceSpec spec);
	
	void startVmOnKvmFailed(KVMHostInventory host, VmInstanceSpec spec, ErrorCode err);
}
