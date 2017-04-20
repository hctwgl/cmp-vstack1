package org.zstack.header.image;

import org.zstack.header.message.APIReply;
import org.zstack.header.storage.backup.BackupStorageInventory;

import java.util.List;

/**
 * Created by xing5 on 2016/8/30.
 */
public class APIGetCandidateBackupStorageForCreatingImageReply extends APIReply {
    private List<BackupStorageInventory> inventories;

    public List<BackupStorageInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<BackupStorageInventory> inventories) {
        this.inventories = inventories;
    }
}
