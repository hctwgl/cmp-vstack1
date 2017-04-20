package org.zstack.xen;

/**
 */
public class XenHostConnectedContext {
    private XenHostInventory inventory;
    private boolean newAddedHost;

    public boolean isNewAddedHost() {
        return newAddedHost;
    }

    public void setNewAddedHost(boolean newAddedHost) {
        this.newAddedHost = newAddedHost;
    }

    public XenHostInventory getInventory() {
        return inventory;
    }

    public void setInventory(XenHostInventory inventory) {
        this.inventory = inventory;
    }
}
