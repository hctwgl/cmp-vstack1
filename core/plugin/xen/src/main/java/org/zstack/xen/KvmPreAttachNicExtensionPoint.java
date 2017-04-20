package org.zstack.xen;

import org.zstack.xen.XenAgentCommands.AttachNicCommand;

/**
 * Created by xing5 on 2016/5/26.
 */
public interface KvmPreAttachNicExtensionPoint {
    void preAttachNicExtensionPoint(XenHostInventory host, AttachNicCommand cmd);
}
