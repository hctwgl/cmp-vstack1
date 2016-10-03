package org.zstack.xen;

import org.zstack.header.core.workflow.Flow;

public interface XenHostConnectExtensionPoint {
    Flow createKvmHostConnectingFlow(KVMHostConnectedContext context);
}
