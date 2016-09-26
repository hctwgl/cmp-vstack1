package org.zstack.xen;

import org.zstack.xen.KVMAgentCommands.AttachIsoCmd;

/**
 * Created by xing5 on 2016/5/27.
 */
public interface KVMPreAttachIsoExtensionPoint {
    void preAttachIsoExtensionPoint(KVMHostInventory host, AttachIsoCmd cmd);
}
