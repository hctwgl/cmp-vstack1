package org.zstack.storage.ceph.primary;

import org.zstack.header.message.APIEvent;

/**
 * Created by frank on 8/6/2015.
 */
public class APIRemoveMonFromCephPrimaryStorageEvent extends APIEvent {
    private CephPrimaryStorageInventory inventory;

    public APIRemoveMonFromCephPrimaryStorageEvent() {
    }

    public APIRemoveMonFromCephPrimaryStorageEvent(String apiId) {
        super(apiId);
    }

    public CephPrimaryStorageInventory getInventory() {
        return inventory;
    }

    public void setInventory(CephPrimaryStorageInventory inventory) {
        this.inventory = inventory;
    }
}
