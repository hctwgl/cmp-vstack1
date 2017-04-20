package org.zstack.header.network.l3;

import org.zstack.header.message.APIEvent;

/**
 * Created by frank on 6/15/2015.
 */
public class APIUpdateL3NetworkEvent extends APIEvent {
    private L3NetworkInventory inventory;

    public APIUpdateL3NetworkEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateL3NetworkEvent() {
    }

    public L3NetworkInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3NetworkInventory inventory) {
        this.inventory = inventory;
    }
}
