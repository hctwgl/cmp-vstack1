package org.zstack.network.service.eip;

import org.zstack.header.message.APIEvent;

/**
 * Created by frank on 6/15/2015.
 */
public class APIUpdateEipEvent extends APIEvent {
    private EipInventory inventory;

    public APIUpdateEipEvent() {
    }

    public APIUpdateEipEvent(String apiId) {
        super(apiId);
    }

    public EipInventory getInventory() {
        return inventory;
    }

    public void setInventory(EipInventory inventory) {
        this.inventory = inventory;
    }
}
