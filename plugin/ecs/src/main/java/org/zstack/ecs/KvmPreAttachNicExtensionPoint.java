package org.zstack.ecs;

import org.zstack.ecs.KVMAgentCommands.AttachNicCommand;

/**
 * Created by xing5 on 2016/5/26.
 */
public interface KvmPreAttachNicExtensionPoint {
    void preAttachNicExtensionPoint(KVMHostInventory host, AttachNicCommand cmd);
}
