package org.zstack.xen;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.logging.Log;
import org.zstack.header.allocator.HostAllocatorConstant;
import org.zstack.header.cluster.ReportHostCapacityMessage;
import org.zstack.header.core.workflow.Flow;
import org.zstack.header.core.workflow.FlowTrigger;
import org.zstack.header.core.workflow.NoRollbackFlow;
import org.zstack.header.errorcode.OperationFailureException;
import org.zstack.header.host.*;
import org.zstack.header.message.MessageReply;
import org.zstack.xen.XenAgentCommands.HostCapacityCmd;
import org.zstack.xen.XenAgentCommands.HostCapacityResponse;

import java.util.Map;

public class XenHostCapacityExtension implements XenHostConnectExtensionPoint, HostConnectionReestablishExtensionPoint {
    @Autowired
    private CloudBus bus;
    @Autowired
    private ErrorFacade errf;

    private void reportCapacity(HostInventory host) {
        XenHostSyncHttpCallMsg msg = new XenHostSyncHttpCallMsg();
        msg.setHostUuid(host.getUuid());
        msg.setPath(XenConstant.KVM_HOST_CAPACITY_PATH);
        msg.setNoStatusCheck(true);
        msg.setCommand(new HostCapacityCmd());
        bus.makeTargetServiceIdByResourceUuid(msg, HostConstant.SERVICE_ID, host.getUuid());
        MessageReply reply = bus.call(msg);
        if (!reply.isSuccess()) {
            throw new OperationFailureException(reply.getError());
        }

        KVMHostSyncHttpCallReply r = reply.castReply();
        HostCapacityResponse rsp = r.toResponse(HostCapacityResponse.class);
        if (!rsp.isSuccess()) {
            throw new OperationFailureException(errf.stringToOperationError(rsp.getError()));
        }
        ReportHostCapacityMessage rmsg = new ReportHostCapacityMessage();
        rmsg.setHostUuid(host.getUuid());
        rmsg.setCpuNum((int) rsp.getCpuNum());
        rmsg.setUsedCpu(rsp.getUsedCpu());
        rmsg.setTotalMemory(rsp.getTotalMemory());
        rmsg.setUsedMemory(rsp.getUsedMemory());
        rmsg.setServiceId(bus.makeLocalServiceId(HostAllocatorConstant.SERVICE_ID));
        bus.send(rmsg);
    }


    @Override
    public void connectionReestablished(HostInventory inv) throws HostException {
        reportCapacity(inv);
    }

    @Override
    public HypervisorType getHypervisorTypeForReestablishExtensionPoint() {
        return HypervisorType.valueOf(XenConstant.KVM_HYPERVISOR_TYPE);
    }

    @Override
    public Flow createKvmHostConnectingFlow(final XenHostConnectedContext context) {
        return new NoRollbackFlow() {
            String __name__ = "sync-host-capacity";

            @Override
            public void run(FlowTrigger trigger, Map data) {
                new Log(context.getInventory().getUuid()).log(XenHostLabel.SYNC_HOST_CAPACITY);

                reportCapacity(context.getInventory());
                trigger.next();
            }
        };
    }
}
