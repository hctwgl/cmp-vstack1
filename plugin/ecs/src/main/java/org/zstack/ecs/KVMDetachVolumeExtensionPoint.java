package org.zstack.ecs;

import org.zstack.ecs.KVMAgentCommands.AttachDataVolumeCmd;
import org.zstack.ecs.KVMAgentCommands.DetachDataVolumeCmd;
import org.zstack.header.errorcode.ErrorCode;
import org.zstack.header.vm.VmInstanceInventory;
import org.zstack.header.volume.VolumeInventory;

/**
 * Created by frank on 4/24/2015.
 */
public interface KVMDetachVolumeExtensionPoint {
    void beforeDetachVolume(KVMHostInventory host, VmInstanceInventory vm, VolumeInventory volume, DetachDataVolumeCmd cmd);

    void afterDetachVolume(KVMHostInventory host, VmInstanceInventory vm, VolumeInventory volume, DetachDataVolumeCmd cmd);

    void detachVolumeFailed(KVMHostInventory host, VmInstanceInventory vm, VolumeInventory volume, DetachDataVolumeCmd cmd, ErrorCode err);
}
