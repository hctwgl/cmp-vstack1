package org.zstack.header.configuration;

import org.zstack.header.message.APIEvent;

/**
 * Created by frank on 6/15/2015.
 */
public class APIUpdateInstanceOfferingEvent extends APIEvent {
    private InstanceOfferingInventory inventory;

    public APIUpdateInstanceOfferingEvent() {
    }

    public APIUpdateInstanceOfferingEvent(String apiId) {
        super(apiId);
    }

    public InstanceOfferingInventory getInventory() {
        return inventory;
    }

    public void setInventory(InstanceOfferingInventory inventory) {
        this.inventory = inventory;
    }
}
