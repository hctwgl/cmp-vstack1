package org.zstack.ecs;

import org.zstack.ecs.KVMAgentCommands.StartVmCmd;
import org.zstack.header.errorcode.ErrorCode;
import org.zstack.header.vm.VmInstanceSpec;

public interface KVMStartVmExtensionPoint {
	void beforeStartVmOnKvm(KVMHostInventory host, VmInstanceSpec spec, StartVmCmd cmd) throws KVMException;
	
	void startVmOnKvmSuccess(KVMHostInventory host, VmInstanceSpec spec);
	
	void startVmOnKvmFailed(KVMHostInventory host, VmInstanceSpec spec, ErrorCode err);
}
