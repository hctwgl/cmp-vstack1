package org.zstack.xen;

import org.zstack.header.core.Completion;

/**
 * Created by xing5 on 2016/8/6.
 */
public interface KVMPingAgentExtensionPoint {
    void kvmPingAgent(XenHostInventory host, Completion completion);
}
