package org.zstack.network.service.portforwarding;

import org.zstack.header.message.APIEvent;

/**
 * Created by frank on 6/15/2015.
 */
public class APIUpdatePortForwardingRuleEvent extends APIEvent {
    private PortForwardingRuleInventory inventory;

    public APIUpdatePortForwardingRuleEvent() {
    }

    public APIUpdatePortForwardingRuleEvent(String apiId) {
        super(apiId);
    }

    public PortForwardingRuleInventory getInventory() {
        return inventory;
    }

    public void setInventory(PortForwardingRuleInventory inventory) {
        this.inventory = inventory;
    }
}
