package org.zstack.xen;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.componentloader.PluginExtension;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.header.Component;
import org.zstack.header.errorcode.ErrorCode;
import org.zstack.header.vm.VmInstanceInventory;
import org.zstack.header.vm.VmInstanceSpec;
import org.zstack.header.volume.VolumeInventory;
import org.zstack.utils.CollectionUtils;
import org.zstack.utils.Utils;
import org.zstack.utils.function.ForEachFunction;
import org.zstack.utils.logging.CLogger;
import org.zstack.xen.XenAgentCommands.AttachDataVolumeCmd;
import org.zstack.xen.XenAgentCommands.DetachDataVolumeCmd;
import org.zstack.xen.XenAgentCommands.StartVmCmd;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KVMExtensionEmitter implements Component {
    private static final CLogger logger = Utils.getLogger(KVMExtensionEmitter.class);

    @Autowired
    private PluginRegistry pluginRgty;

    private List<KVMStartVmExtensionPoint> startVmExts = new ArrayList<KVMStartVmExtensionPoint>();
    private List<KVMDestroyVmExtensionPoint> destroyVmExts = new ArrayList<KVMDestroyVmExtensionPoint>();
    private List<KVMStopVmExtensionPoint> stopVmExts = new ArrayList<KVMStopVmExtensionPoint>();
    private List<KVMRebootVmExtensionPoint> rebootVmExts = new ArrayList<KVMRebootVmExtensionPoint>();
    private List<KVMStartVmAddonExtensionPoint> addonsExts = new ArrayList<KVMStartVmAddonExtensionPoint>();
    private List<KVMAttachVolumeExtensionPoint> attachVolumeExts = new ArrayList<KVMAttachVolumeExtensionPoint>();
    private List<KVMDetachVolumeExtensionPoint> detachVolumeExts = new ArrayList<KVMDetachVolumeExtensionPoint>();

    private void populateExtensions() {
        startVmExts = pluginRgty.getExtensionList(KVMStartVmExtensionPoint.class);
        destroyVmExts = pluginRgty.getExtensionList(KVMDestroyVmExtensionPoint.class);
        stopVmExts = pluginRgty.getExtensionList(KVMStopVmExtensionPoint.class);
        rebootVmExts = pluginRgty.getExtensionList(KVMRebootVmExtensionPoint.class);
        addonsExts = pluginRgty.getExtensionList(KVMStartVmAddonExtensionPoint.class);
        attachVolumeExts = pluginRgty.getExtensionList(KVMAttachVolumeExtensionPoint.class);
        detachVolumeExts = pluginRgty.getExtensionList(KVMDetachVolumeExtensionPoint.class);
    }

    public void beforeStartVmOnKvm(final XenHostInventory host, final VmInstanceSpec spec, final StartVmCmd cmd) throws KVMException {
        for (KVMStartVmExtensionPoint extp : startVmExts) {
            extp.beforeStartVmOnKvm(host, spec, cmd);
        }
    }

    public void startVmOnKvmSuccess(final XenHostInventory host, final VmInstanceSpec spec) {
        CollectionUtils.safeForEach(startVmExts, new ForEachFunction<KVMStartVmExtensionPoint>() {
            @Override
            public void run(KVMStartVmExtensionPoint arg) {
                arg.startVmOnKvmSuccess(host, spec);
            }
        });
    }

    public void startVmOnKvmFailed(final XenHostInventory host, final VmInstanceSpec spec, final ErrorCode err) {
        CollectionUtils.safeForEach(startVmExts, new ForEachFunction<KVMStartVmExtensionPoint>() {
            @Override
            public void run(KVMStartVmExtensionPoint arg) {
                arg.startVmOnKvmFailed(host, spec, err);
            }
        });
    }

    public void beforeDestroyVmOnKvm(XenHostInventory host, VmInstanceInventory vm) throws KVMException {
        for (KVMDestroyVmExtensionPoint extp : destroyVmExts) {
            extp.beforeDestroyVmOnKvm(host, vm);
        }
    }

    public void destroyVmOnKvmSuccess(final XenHostInventory host, final VmInstanceInventory vm) {
        CollectionUtils.safeForEach(destroyVmExts, new ForEachFunction<KVMDestroyVmExtensionPoint>() {
            @Override
            public void run(KVMDestroyVmExtensionPoint arg) {
                arg.destroyVmOnKvmSuccess(host, vm);
            }
        });
    }

    public void destroyVmOnKvmFailed(final XenHostInventory host, final VmInstanceInventory vm, final ErrorCode err) {
        CollectionUtils.safeForEach(destroyVmExts, new ForEachFunction<KVMDestroyVmExtensionPoint>() {
            @Override
            public void run(KVMDestroyVmExtensionPoint arg) {
                arg.destroyVmOnKvmFailed(host, vm, err);
            }
        });
    }

    public void beforeStopVmOnKvm(XenHostInventory host, VmInstanceInventory vm) throws KVMException {
        for (KVMStopVmExtensionPoint extp : stopVmExts) {
            extp.beforeStopVmOnKvm(host, vm);
        }
    }

    public void stopVmOnKvmSuccess(final XenHostInventory host, final VmInstanceInventory vm) {
        CollectionUtils.safeForEach(stopVmExts, new ForEachFunction<KVMStopVmExtensionPoint>() {
            @Override
            public void run(KVMStopVmExtensionPoint arg) {
                arg.stopVmOnKvmSuccess(host, vm);
            }
        });
    }

    public void stopVmOnKvmFailed(final XenHostInventory host, final VmInstanceInventory vm, final ErrorCode err) {
        CollectionUtils.safeForEach(stopVmExts, new ForEachFunction<KVMStopVmExtensionPoint>() {
            @Override
            public void run(KVMStopVmExtensionPoint arg) {
                arg.stopVmOnKvmFailed(host, vm, err);
            }
        });
    }

    public void beforeRebootVmOnKvm(XenHostInventory host, VmInstanceInventory vm) throws KVMException {
        for (KVMRebootVmExtensionPoint extp : rebootVmExts) {
            extp.beforeRebootVmOnKvm(host, vm);
        }
    }

    public void rebootVmOnKvmSuccess(final XenHostInventory host, final VmInstanceInventory vm) {
        CollectionUtils.safeForEach(rebootVmExts, new ForEachFunction<KVMRebootVmExtensionPoint>() {
            @Override
            public void run(KVMRebootVmExtensionPoint arg) {
                arg.rebootVmOnKvmSuccess(host, vm);
            }
        });
    }

    public void rebootVmOnKvmFailed(final XenHostInventory host, final VmInstanceInventory vm, final ErrorCode err) {
        CollectionUtils.safeForEach(rebootVmExts, new ForEachFunction<KVMRebootVmExtensionPoint>() {
            @Override
            public void run(KVMRebootVmExtensionPoint arg) {
                arg.rebootVmOnKvmFailed(host, vm, err);
            }
        });
    }

    public void addOn(final XenHostInventory host, final VmInstanceSpec spec, final StartVmCmd cmd) {
        CollectionUtils.safeForEach(addonsExts, new ForEachFunction<KVMStartVmAddonExtensionPoint>() {
            @Override
            public void run(KVMStartVmAddonExtensionPoint extp) {
                if (spec.getVmInventory().getType().equals(extp.getVmTypeForAddonExtension().toString())) {
                    extp.addAddon(host, spec, cmd);
                }
            }
        });
    }

    public void beforeAttachVolume(XenHostInventory host, VmInstanceInventory vm, VolumeInventory vol, AttachDataVolumeCmd cmd) {
        for (KVMAttachVolumeExtensionPoint ext : attachVolumeExts) {
            ext.beforeAttachVolume(host, vm, vol, cmd);
        }
    }

    public void afterAttachVolume(XenHostInventory host, VmInstanceInventory vm, VolumeInventory vol, AttachDataVolumeCmd cmd) {
        for (KVMAttachVolumeExtensionPoint ext : attachVolumeExts) {
            ext.afterAttachVolume(host, vm, vol, cmd);
        }
    }

    public void attachVolumeFailed(XenHostInventory host, VmInstanceInventory vm, VolumeInventory vol, AttachDataVolumeCmd cmd, ErrorCode err) {
        for (KVMAttachVolumeExtensionPoint ext : attachVolumeExts) {
            ext.attachVolumeFailed(host, vm, vol, cmd, err);
        }
    }

    public void beforeDetachVolume(XenHostInventory host, VmInstanceInventory vm, VolumeInventory vol, DetachDataVolumeCmd cmd) {
        for (KVMDetachVolumeExtensionPoint ext : detachVolumeExts) {
            ext.beforeDetachVolume(host, vm, vol, cmd);
        }
    }

    public void afterDetachVolume(XenHostInventory host, VmInstanceInventory vm, VolumeInventory vol, DetachDataVolumeCmd cmd) {
        for (KVMDetachVolumeExtensionPoint ext : detachVolumeExts) {
            ext.afterDetachVolume(host, vm, vol, cmd);
        }
    }

    public void detachVolumeFailed(XenHostInventory host, VmInstanceInventory vm, VolumeInventory vol, DetachDataVolumeCmd cmd, ErrorCode err) {
        for (KVMDetachVolumeExtensionPoint ext : detachVolumeExts) {
            ext.detachVolumeFailed(host, vm, vol, cmd, err);
        }
    }


    @Override
    public boolean start() {
        populateExtensions();
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
