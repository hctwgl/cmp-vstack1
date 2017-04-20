package org.zstack.header.vm;

import org.zstack.header.message.MessageReply;

public class StartNewCreatedPubVmInstanceReply extends MessageReply {
    private PubVmInstanceInventory inventory;

	public PubVmInstanceInventory getInventory() {
		return inventory;
	}

	public void setInventory(PubVmInstanceInventory inventory) {
		this.inventory = inventory;
	}

   
}
