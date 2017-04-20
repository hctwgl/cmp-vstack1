package org.zstack.header.network.l2;

import org.zstack.header.message.APIEvent;

/**
 * Created by frank on 6/14/2015.
 */
public class APIUpdateL2NetworkEvent extends APIEvent {
    private L2NetworkInventory inventory;

    public L2NetworkInventory getInventory() {
        return inventory;
    }

    public void setInventory(L2NetworkInventory inventory) {
        this.inventory = inventory;
    }

    public APIUpdateL2NetworkEvent() {
    }

    public APIUpdateL2NetworkEvent(String apiId) {
        super(apiId);
    }
}
