package org.zstack.header.vm;

import org.zstack.header.message.APIReply;
import org.zstack.header.query.APIQueryReply;

import java.util.List;

public class APIQueryPubVmInstanceReply extends APIQueryReply {
    private List<PubVmInstanceInventory> inventories;

    public List<PubVmInstanceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<PubVmInstanceInventory> inventories) {
        this.inventories = inventories;
    }
}
