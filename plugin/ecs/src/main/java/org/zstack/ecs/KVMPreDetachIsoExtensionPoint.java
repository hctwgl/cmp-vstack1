package org.zstack.ecs;

import org.zstack.ecs.KVMAgentCommands.DetachIsoCmd;

/**
 * Created by xing5 on 2016/5/27.
 */
public interface KVMPreDetachIsoExtensionPoint {
    void preDetachIsoExtensionPoint(KVMHostInventory host, DetachIsoCmd cmd);
}
