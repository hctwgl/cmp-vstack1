package org.zstack.xen;

import org.zstack.header.network.l2.L2NetworkInventory;
import org.zstack.header.network.l2.L2NetworkType;
import org.zstack.header.vm.VmNicInventory;
import org.zstack.xen.XenAgentCommands.NicTO;

public interface XenCompleteNicInformationExtensionPoint {
	NicTO completeNicInformation(L2NetworkInventory l2Network, VmNicInventory nic);
	
	L2NetworkType getL2NetworkTypeVmNicOn();
}
