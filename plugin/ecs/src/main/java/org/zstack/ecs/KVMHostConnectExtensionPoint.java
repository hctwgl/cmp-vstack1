package org.zstack.ecs;

import org.zstack.header.core.workflow.Flow;

public interface KVMHostConnectExtensionPoint {
    Flow createKvmHostConnectingFlow(KVMHostConnectedContext context);
}
