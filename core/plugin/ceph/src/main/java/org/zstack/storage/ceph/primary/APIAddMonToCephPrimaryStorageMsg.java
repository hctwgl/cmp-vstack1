package org.zstack.storage.ceph.primary;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.storage.primary.PrimaryStorageMessage;

import java.util.List;

/**
 * Created by frank on 8/6/2015.
 */
public class APIAddMonToCephPrimaryStorageMsg extends APIMessage implements PrimaryStorageMessage {
    @APIParam(resourceType = CephPrimaryStorageVO.class)
    private String uuid;
    @APIParam(nonempty = true)
    private List<String> monUrls;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<String> getMonUrls() {
        return monUrls;
    }

    public void setMonUrls(List<String> monUrls) {
        this.monUrls = monUrls;
    }

    @Override
    public String getPrimaryStorageUuid() {
        return uuid;
    }
}
