package org.zstack.header.vm;

import org.zstack.header.message.MessageReply;

public class RebootPubVmInstanceReply extends MessageReply {
    private PubVmInstanceInventory vmInventory;

    public PubVmInstanceInventory getVmInventory() {
        return vmInventory;
    }

    public void setVmInventory(PubVmInstanceInventory vmInventory) {
        this.vmInventory = vmInventory;
    }
}
