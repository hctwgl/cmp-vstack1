package org.zstack.header.vm;

import org.zstack.header.message.MessageReply;

/**
 * Created by david on 8/4/16.
 */
public class CreatePubVmInstanceReply extends MessageReply {
    private PubVmInstanceInventory inventory;

    public PubVmInstanceInventory getInventory() {
        return inventory;
    }

    public void setInventory(PubVmInstanceInventory inventory) {
        this.inventory = inventory;
    }
}
