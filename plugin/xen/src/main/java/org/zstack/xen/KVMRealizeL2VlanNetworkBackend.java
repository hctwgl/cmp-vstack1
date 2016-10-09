package org.zstack.xen;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.CloudBusCallBack;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.timeout.ApiTimeoutManager;
import org.zstack.header.core.Completion;
import org.zstack.header.errorcode.ErrorCode;
import org.zstack.header.host.HostConstant;
import org.zstack.header.host.HypervisorType;
import org.zstack.header.message.MessageReply;
import org.zstack.header.network.l2.*;
import org.zstack.header.vm.VmNicInventory;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;
import org.zstack.xen.XenAgentCommands.CheckVlanBridgeResponse;
import org.zstack.xen.XenAgentCommands.CreateVlanBridgeCmd;
import org.zstack.xen.XenAgentCommands.CreateVlanBridgeResponse;
import org.zstack.xen.XenAgentCommands.NicTO;

import static org.zstack.utils.CollectionDSL.e;
import static org.zstack.utils.CollectionDSL.map;

public class KVMRealizeL2VlanNetworkBackend implements L2NetworkRealizationExtensionPoint, XenCompleteNicInformationExtensionPoint {
    private static final CLogger logger = Utils.getLogger(KVMRealizeL2VlanNetworkBackend.class);

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private CloudBus bus;
    @Autowired
    private ApiTimeoutManager timeoutMgr;

    private String makeBridgeName(String physicalInterfaceName, int vlan) {
        physicalInterfaceName = physicalInterfaceName.substring(0, Math.min(physicalInterfaceName.length(), 7));
        return String.format("br_%s_%s", physicalInterfaceName, vlan);
    }

    public void realize(final L2NetworkInventory l2Network, final String hostUuid, boolean noStatusCheck, final Completion completion) {
        final L2VlanNetworkInventory l2vlan = (L2VlanNetworkInventory) l2Network;
        final CreateVlanBridgeCmd cmd = new CreateVlanBridgeCmd();
        cmd.setPhysicalInterfaceName(l2Network.getPhysicalInterface());
        cmd.setBridgeName(makeBridgeName(l2vlan.getPhysicalInterface(), l2vlan.getVlan()));
        cmd.setVlan(l2vlan.getVlan());

        XenHostAsyncHttpCallMsg msg = new XenHostAsyncHttpCallMsg();
        msg.setHostUuid(hostUuid);
        msg.setCommand(cmd);
        msg.setCommandTimeout(timeoutMgr.getTimeout(cmd.getClass(), "5m"));
        msg.setNoStatusCheck(noStatusCheck);
        msg.setPath(XenConstant.KVM_REALIZE_L2VLAN_NETWORK_PATH);
        bus.makeTargetServiceIdByResourceUuid(msg, HostConstant.SERVICE_ID, hostUuid);
        bus.send(msg, new CloudBusCallBack(completion) {
            @Override
            public void run(MessageReply reply) {
                if (!reply.isSuccess()) {
                    completion.fail(reply.getError());
                    return;
                }

                XenHostAsyncHttpCallReply hreply = reply.castReply();
                CreateVlanBridgeResponse rsp = hreply.toResponse(CreateVlanBridgeResponse.class);
                if (!rsp.isSuccess()) {
                    String err = String.format(
                            "failed to create bridge[%s] for l2Network[uuid:%s, type:%s, vlan:%s] on kvm host[uuid:%s], because %s", cmd
                                    .getBridgeName(), l2Network.getUuid(), l2Network.getType(), l2vlan.getVlan(), hostUuid, rsp.getError());
                    logger.warn(err);
                    completion.fail(errf.stringToOperationError(err));
                    return;
                }

                String info = String.format(
                        "successfully realize bridge[%s] for l2Network[uuid:%s, type:%s, vlan:%s] on kvm host[uuid:%s]", cmd
                                .getBridgeName(), l2Network.getUuid(), l2Network.getType(), l2vlan.getVlan(), hostUuid);
                logger.debug(info);
                if (!KVMSystemTags.L2_BRIDGE_NAME.hasTag(l2Network.getUuid())) {
                    KVMSystemTags.L2_BRIDGE_NAME.createInherentTag(l2Network.getUuid(),
                            map(e(KVMSystemTags.L2_BRIDGE_NAME_TOKEN, cmd.getBridgeName())));
                }
                completion.success();
            }
        });
    }

    @Override
    public void realize(final L2NetworkInventory l2Network, final String hostUuid, final Completion completion) {
        realize(l2Network, hostUuid, false, completion);
    }

    public void check(final L2NetworkInventory l2Network, final String hostUuid, boolean noStatusCheck, final Completion completion) {
        final L2VlanNetworkInventory l2vlan = (L2VlanNetworkInventory) l2Network;
        final XenAgentCommands.CheckVlanBridgeCmd cmd = new XenAgentCommands.CheckVlanBridgeCmd();
        cmd.setPhysicalInterfaceName(l2Network.getPhysicalInterface());
        cmd.setBridgeName(makeBridgeName(l2vlan.getPhysicalInterface(), l2vlan.getVlan()));
        cmd.setVlan(l2vlan.getVlan());

        XenHostAsyncHttpCallMsg msg = new XenHostAsyncHttpCallMsg();
        msg.setHostUuid(hostUuid);
        msg.setCommand(cmd);
        msg.setCommandTimeout(timeoutMgr.getTimeout(cmd.getClass(), "5m"));
        msg.setPath(XenConstant.KVM_CHECK_L2VLAN_NETWORK_PATH);
        msg.setNoStatusCheck(noStatusCheck);
        bus.makeTargetServiceIdByResourceUuid(msg, HostConstant.SERVICE_ID, hostUuid);
        bus.send(msg, new CloudBusCallBack(completion) {
            @Override
            public void run(MessageReply reply) {
                if (!reply.isSuccess()) {
                    completion.fail(reply.getError());
                    return;
                }

                XenHostAsyncHttpCallReply hreply = reply.castReply();
                CheckVlanBridgeResponse rsp = hreply.toResponse(CheckVlanBridgeResponse.class);
                if (!rsp.isSuccess()) {
                    ErrorCode err = errf.stringToOperationError(
                            String.format("failed to check bridge[%s] for l2VlanNetwork[uuid:%s, name:%s] on kvm host[uuid:%s], %s",
                                    cmd.getBridgeName(), l2vlan.getUuid(), l2vlan.getName(), hostUuid, rsp.getError()));
                    completion.fail(err);
                    return;
                }

                String info = String.format("successfully checked bridge[%s] for l2VlanNetwork[uuid:%s, name:%s] on kvm host[uuid:%s]",
                        cmd.getBridgeName(), l2vlan.getUuid(), l2vlan.getName(), hostUuid);
                logger.debug(info);
                completion.success();
            }
        });
    }

    @Override
    public void check(final L2NetworkInventory l2Network, final String hostUuid, final Completion completion) {
        check(l2Network, hostUuid, false, completion);
    }

    @Override
    public L2NetworkType getSupportedL2NetworkType() {
        return L2NetworkType.valueOf(L2NetworkConstant.L2_VLAN_NETWORK_TYPE);
    }

    @Override
    public HypervisorType getSupportedHypervisorType() {
        return HypervisorType.valueOf(XenConstant.KVM_HYPERVISOR_TYPE);
    }

	@Override
	public L2NetworkType getL2NetworkTypeVmNicOn() {
		return getSupportedL2NetworkType();
	}

	@Override
	public NicTO completeNicInformation(L2NetworkInventory l2Network, VmNicInventory nic) {
	    L2VlanNetworkVO vo = dbf.findByUuid(l2Network.getUuid(), L2VlanNetworkVO.class);
		NicTO to = new NicTO();
		to.setMac(nic.getMac());
        to.setUuid(nic.getUuid());
		to.setBridgeName(makeBridgeName(l2Network.getPhysicalInterface(), vo.getVlan()));
		to.setDeviceId(nic.getDeviceId());
		to.setNicInternalName(nic.getInternalName());
		to.setMetaData(String.valueOf(vo.getVlan()));
		return to;
	}

}
