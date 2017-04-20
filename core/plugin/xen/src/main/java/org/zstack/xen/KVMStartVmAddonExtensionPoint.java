package org.zstack.xen;

import org.zstack.header.vm.VmInstanceSpec;
import org.zstack.header.vm.VmInstanceType;

/**
 */
public interface KVMStartVmAddonExtensionPoint {
    VmInstanceType getVmTypeForAddonExtension();

    void addAddon(XenHostInventory host, VmInstanceSpec spec, XenAgentCommands.StartVmCmd cmd);
}
