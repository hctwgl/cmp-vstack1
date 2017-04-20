package org.zstack.xen;

import org.zstack.xen.XenAgentCommands.AttachIsoCmd;

/**
 * Created by xing5 on 2016/5/27.
 */
public interface KVMPreAttachIsoExtensionPoint {
    void preAttachIsoExtensionPoint(XenHostInventory host, AttachIsoCmd cmd);
}
