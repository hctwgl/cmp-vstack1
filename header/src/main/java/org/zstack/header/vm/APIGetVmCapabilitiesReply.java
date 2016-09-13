package org.zstack.header.vm;

import org.zstack.header.message.APIReply;

import java.util.Map;

/**
 * Created by xing5 on 2016/5/17.
 */
public class APIGetVmCapabilitiesReply extends APIReply {
    private Map<String, Object> capabilities;

    public Map<String, Object> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Map<String, Object> capabilities) {
        this.capabilities = capabilities;
    }
}
