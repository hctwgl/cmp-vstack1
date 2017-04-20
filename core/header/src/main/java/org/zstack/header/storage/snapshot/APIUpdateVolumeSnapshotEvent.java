package org.zstack.header.storage.snapshot;

import org.zstack.header.message.APIEvent;

/**
 * Created by frank on 6/14/2015.
 */
public class APIUpdateVolumeSnapshotEvent extends APIEvent {
    private VolumeSnapshotInventory inventory;

    public APIUpdateVolumeSnapshotEvent() {
    }

    public APIUpdateVolumeSnapshotEvent(String apiId) {
        super(apiId);
    }

    public VolumeSnapshotInventory getInventory() {
        return inventory;
    }

    public void setInventory(VolumeSnapshotInventory inventory) {
        this.inventory = inventory;
    }
}
