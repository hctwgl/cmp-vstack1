package org.zstack.header.vm;

import org.zstack.header.core.scheduler.SchedulerInventory;
import org.zstack.header.message.APIEvent;

/**
 * Created by root on 8/16/16.
 */
public class APICreateRebootVmInstanceSchedulerEvent extends APIEvent {
    private SchedulerInventory inventory;

    public SchedulerInventory getInventory() {
        return inventory;
    }

    public void setInventory(SchedulerInventory inventory) {
        this.inventory = inventory;
    }

    public APICreateRebootVmInstanceSchedulerEvent(String apiId) {
        super(apiId);
    }
    public APICreateRebootVmInstanceSchedulerEvent() {
        super(null);
    }
}
