package org.zstack.xen;

import org.zstack.header.core.NoErrorCompletion;

/**
 * Created by xing5 on 2016/8/6.
 */
public interface KVMPingAgentNoFailureExtensionPoint {
    void kvmPingAgentNoFailure(XenHostInventory host, NoErrorCompletion completion);
}
