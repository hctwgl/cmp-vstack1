package org.zstack.ecs;

import org.zstack.ecs.KVMAgentCommands.AttachDataVolumeCmd;
import org.zstack.header.errorcode.ErrorCode;
import org.zstack.header.vm.VmInstanceInventory;
import org.zstack.header.volume.VolumeInventory;

/**
 * Created by frank on 4/24/2015.
 */
public interface KVMAttachVolumeExtensionPoint {
    void beforeAttachVolume(KVMHostInventory host, VmInstanceInventory vm, VolumeInventory volume, AttachDataVolumeCmd cmd);

    void afterAttachVolume(KVMHostInventory host, VmInstanceInventory vm, VolumeInventory volume, AttachDataVolumeCmd cmd);

    void attachVolumeFailed(KVMHostInventory host, VmInstanceInventory vm, VolumeInventory volume, AttachDataVolumeCmd cmd, ErrorCode err);
}
