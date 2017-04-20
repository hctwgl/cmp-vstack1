package org.zstack.storage.ceph.backup;

import org.zstack.header.message.APIEvent;

/**
 * Created by frank on 8/1/2015.
 */
public class APIRemoveMonFromCephBackupStorageEvent extends APIEvent {
    private CephBackupStorageInventory inventory;

    public APIRemoveMonFromCephBackupStorageEvent() {
    }

    public APIRemoveMonFromCephBackupStorageEvent(String apiId) {
        super(apiId);
    }

    public CephBackupStorageInventory getInventory() {
        return inventory;
    }

    public void setInventory(CephBackupStorageInventory inventory) {
        this.inventory = inventory;
    }
}
