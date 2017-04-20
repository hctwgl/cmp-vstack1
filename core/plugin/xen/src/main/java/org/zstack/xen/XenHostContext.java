package org.zstack.xen;

import org.springframework.web.util.UriComponentsBuilder;

public class XenHostContext {
    private XenHostInventory inventory;
    private String baseUrl;

    public XenHostInventory getInventory() {
        return inventory;
    }
    public void setInventory(XenHostInventory inventory) {
        this.inventory = inventory;
    }
    public String getBaseUrl() {
        return baseUrl;
    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String buildUrl(String...path) {
        UriComponentsBuilder ub = UriComponentsBuilder.fromHttpUrl(baseUrl);
        for (String p : path) {
            ub.path(p);
        }
        return ub.build().toString();
    }
}
