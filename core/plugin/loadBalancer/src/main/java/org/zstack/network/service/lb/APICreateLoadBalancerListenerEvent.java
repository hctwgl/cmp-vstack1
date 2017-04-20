package org.zstack.network.service.lb;

import org.zstack.header.message.APIEvent;

/**
 * Created by frank on 8/8/2015.
 */
public class APICreateLoadBalancerListenerEvent extends APIEvent {
    private LoadBalancerListenerInventory inventory;

    public APICreateLoadBalancerListenerEvent() {
    }

    public APICreateLoadBalancerListenerEvent(String apiId) {
        super(apiId);
    }

    public void setInventory(LoadBalancerListenerInventory inventory) {
        this.inventory = inventory;
    }

    public LoadBalancerListenerInventory getInventory() {
        return inventory;
    }
}
