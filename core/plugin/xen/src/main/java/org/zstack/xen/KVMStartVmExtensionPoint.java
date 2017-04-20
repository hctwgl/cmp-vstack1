package org.zstack.xen;

import org.zstack.header.errorcode.ErrorCode;
import org.zstack.header.vm.VmInstanceSpec;
import org.zstack.xen.XenAgentCommands.StartVmCmd;

public interface KVMStartVmExtensionPoint {
	void beforeStartVmOnKvm(XenHostInventory host, VmInstanceSpec spec, StartVmCmd cmd) throws KVMException;
	
	void startVmOnKvmSuccess(XenHostInventory host, VmInstanceSpec spec);
	
	void startVmOnKvmFailed(XenHostInventory host, VmInstanceSpec spec, ErrorCode err);
}
