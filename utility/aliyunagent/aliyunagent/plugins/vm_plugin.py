'''
@author: Frank
'''
from aliyunagent import ecsagent

from zstacklib.utils import jsonobject
from zstacklib.utils import xmlobject
from zstacklib.utils import http
from zstacklib.utils import log
from zstacklib.utils import shell
from zstacklib.utils import lichbd
from zstacklib.utils import sizeunit
from zstacklib.utils import uuidhelper
from zstacklib.utils import linux
import zstacklib.utils.lock as lock
from zstacklib.utils import thread
from libcloud.compute.providers import get_driver
from libcloud.compute.types import Provider
from libcloud.common.exceptions import BaseHTTPError
from libcloud.common.types import LibcloudError
from libcloud.compute.base import NodeAuthPassword
import functools
import zstacklib.utils.iptables as iptables
import os.path
import re
import xml.etree.ElementTree as etree
import traceback
import Queue
import sys
import time
# from gtk._gtk import Item

logger = log.get_logger(__name__)

_conn = None

class NicTO(object):
    def __init__(self):
        self.mac = None
        self.bridgeName = None
        self.deviceId = None
        
class SystemDiskTO(object):
    def __init__(self):
        self.category = None
        self.disk_name = None
        self.description = None
        
class DataDiskTO(object):
    def __init__(self):
        self.size = None
        self.category = None
        self.snapshot_id = None
        self.disk_name = None
        self.description = None
        self.device = None
        self.delete_with_instance = None

class StartVmCmd(ecsagent.AgentCommand):
    def __init__(self):
        super(StartVmCmd, self).__init__()
        self.vmInstanceUuid = None
        self.name = None
        self.image = None
        self.size = None
        self.auth = None
        self.ex_security_group_id = None
        self.ex_description = None
        self.ex_internet_charge_type = None
        self.ex_internet_max_bandwidth_out = None
        self.ex_internet_max_bandwidth_in = None
        self.ex_hostname = None
        self.ex_io_optimized = None
        self.ex_system_disk = {}
        self.ex_data_disks = {}
        self.ex_vswitch_id = None
        self.ex_private_ip_address = None
        self.ex_client_token = None

class StartVmResponse(ecsagent.AgentResponse):
    def __init__(self):
        super(StartVmResponse, self).__init__()
        
class StartVmPubResponse(ecsagent.AgentResponse):
    def __init__(self):
        super(StartVmPubResponse, self).__init__()
        self.vmUuid = None

class GetVncPortCmd(ecsagent.AgentCommand):
    def __init__(self):
        super(GetVncPortCmd, self).__init__()
        self.vmUuid = None

class GetVncPortResponse(ecsagent.AgentResponse):
    def __init__(self):
        super(GetVncPortResponse, self).__init__()
        self.port = None
        self.protocol = None

class StopVmCmd(ecsagent.AgentCommand):
    def __init__(self):
        super(StopVmCmd, self).__init__()
        self.uuid = None
        self.force = None

class StopVmResponse(ecsagent.AgentResponse):
    def __init__(self):
        super(StopVmResponse, self).__init__()

class RebootVmCmd(ecsagent.AgentCommand):
    def __init__(self):
        super(RebootVmCmd, self).__init__()
        self.uuid = None
        self.force = None

class RebootVmResponse(ecsagent.AgentResponse):
    def __init__(self):
        super(RebootVmResponse, self).__init__()
        
        
class GetPubVmResponse(ecsagent.AgentResponse):
    def __init__(self):
        super(GetPubVmResponse, self).__init__()
        self.nodes = None

class DestroyVmCmd(ecsagent.AgentCommand):
    def __init__(self):
        super(DestroyVmCmd, self).__init__()
        self.uuid = None

class DestroyVmResponse(ecsagent.AgentResponse):
    def __init__(self):
        super(DestroyVmResponse, self).__init__()

class VmSyncCmd(ecsagent.AgentCommand):
    def __init__(self):
        super(VmSyncCmd, self).__init__()

class VmSyncResponse(ecsagent.AgentResponse):
    def __init__(self):
        super(VmSyncResponse, self).__init__()
        self.states = None

class AttachDataVolumeCmd(ecsagent.AgentCommand):
    def __init__(self):
        super(AttachDataVolumeCmd, self).__init__()
        self.volume = None
        self.uuid = None

class AttachDataVolumeResponse(ecsagent.AgentResponse):
    def __init__(self):
        super(AttachDataVolumeResponse, self).__init__()

class DetachDataVolumeCmd(ecsagent.AgentCommand):
    def __init__(self):
        super(DetachDataVolumeCmd, self).__init__()
        self.volume = None
        self.uuid = None

class DetachDataVolumeResponse(ecsagent.AgentResponse):
    def __init__(self):
        super(DetachDataVolumeResponse, self).__init__()

class MigrateVmResponse(ecsagent.AgentResponse):
    def __init__(self):
        super(MigrateVmResponse, self).__init__()

class TakeSnapshotResponse(ecsagent.AgentResponse):
    def __init__(self):
        super(TakeSnapshotResponse, self).__init__()
        self.newVolumeInstallPath = None
        self.snapshotInstallPath = None
        self.size = None

class MergeSnapshotRsp(ecsagent.AgentResponse):
    def __init__(self):
        super(MergeSnapshotRsp, self).__init__()

class LogoutIscsiTargetRsp(ecsagent.AgentResponse):
    def __init__(self):
        super(LogoutIscsiTargetRsp, self).__init__()

class LoginIscsiTargetRsp(ecsagent.AgentResponse):
    def __init__(self):
        super(LoginIscsiTargetRsp, self).__init__()

class ReportVmStateCmd(object):
    def __init__(self):
        self.hostUuid = None
        self.vmUuid = None
        self.vmState = None

class CheckVmStateRsp(ecsagent.AgentResponse):
    def __init__(self):
        super(CheckVmStateRsp, self).__init__()
        self.states = {}

class ReconnectMeCmd(object):
    def __init__(self):
        self.hostUuid = None
        self.reason = None

class VncPortIptableRule(object):
    def __init__(self):
        self.host_ip = None
        self.port = None
        self.vm_internal_id = None

    def _make_chain_name(self):
        return "vm-%s-vnc" % self.vm_internal_id

    @lock.file_lock('iptables')
    def apply(self):
        assert self.host_ip is not None
        assert self.port is not None
        assert self.vm_internal_id is not None

        ipt = iptables.from_iptables_save()
        chain_name = self._make_chain_name()
        ipt.add_rule('-A INPUT -p tcp -m tcp --dport %s -j %s' % (self.port, chain_name))
        ipt.add_rule('-A %s -d %s/32 -j ACCEPT' % (chain_name, self.host_ip))
        ipt.add_rule('-A %s ! -d %s/32 -j REJECT --reject-with icmp-host-prohibited' % (chain_name, self.host_ip))
        ipt.iptable_restore()

    @lock.file_lock('iptables')
    def delete(self):
        assert self.vm_internal_id is not None

        ipt = iptables.from_iptables_save()
        chain_name = self._make_chain_name()
        ipt.delete_chain(chain_name)
        ipt.iptable_restore()

    @lock.file_lock('iptables')
    def delete_stale_chains(self):
        vms = get_running_vms()
        ipt = iptables.from_iptables_save()
        tbl = ipt.get_table()

        internal_ids = []
        for vm in vms:
            if not vm.domain_xmlobject.has_element('metadata.internalId'):
                continue

            id = vm.domain_xmlobject.metadata.internalId.text_
            if id:
                internal_ids.append(id)

        # delete all vnc chains
        for chain in tbl.children:
            if 'vm' in chain.name and 'vnc' in chain.name:
                vm_internal_id = chain.name.split('-')[1]
                if vm_internal_id not in internal_ids:
                    chain.delete()
                    logger.debug('deleted a stale VNC iptable chain[%s]' % chain.name)

        ipt.iptable_restore()


def e(parent, tag, value=None, attrib={}):
    el = etree.SubElement(parent, tag, attrib)
    if value:
        el.text = value
    return el

class AliyunEventManager(object):
    EVENT_STARTED = "Starting"
    EVENT_RUNNING = "Running"
    EVENT_STOPING = "Stopping"
    EVENT_STOPPED = "Stopped"

    event_strings = (
        EVENT_STARTED,
        EVENT_RUNNING,
        EVENT_STOPING,
        EVENT_STOPPED,
    )

    def __init__(self):
        self.stopped = False
#         libvirt.virEventRegisterDefaultImpl()
# 
#         @thread.AsyncThread
#         def run():
#             logger.debug("virEventRunDefaultImpl starts")
#             while not self.stopped:
#                 try:
#                     if libvirt.virEventRunDefaultImpl() < 0:
#                         logger.warn("virEventRunDefaultImpl quit with error")
#                 except:
#                     content = traceback.format_exc()
#                     logger.warn(content)
# 
#             logger.debug("virEventRunDefaultImpl stopped")
# 
#         run()

    def stop(self):
        self.stopped = True

    @staticmethod
    def event_to_string(index):
#         return LibvirtEventManager.event_strings[index]
        return

class AliyunAutoReconnect(object):
    
    global _conn

    evtMgr = AliyunEventManager()

    aliyun_event_callbacks = {}

    def __init__(self, func):
        self.func = func
        self.exception = None

#     @staticmethod
#     def add_aliyun_callback(id, cb):
#         cbs = AliyunAutoReconnect.aliyun_event_callbacks.get(id, None)
#         if cbs is None:
#             cbs = []
#             AliyunAutoReconnect.aliyun_event_callbacks[id] = cbs
#         cbs.append(cb)

#     @staticmethod
#     def register_libvirt_callbacks():
#         def reboot_callback(conn, dom, opaque):
#             cbs = AliyunAutoReconnect.libvirt_event_callbacks.get(libvirt.VIR_DOMAIN_EVENT_ID_REBOOT)
#             if not cbs:
#                 return
# 
#             for cb in cbs:
#                 try:
#                     cb(conn, dom, opaque)
#                 except:
#                     content = traceback.format_exc()
#                     logger.warn(content)
# 
#         AliyunAutoReconnect.conn.domainEventRegisterAny(None, libvirt.VIR_DOMAIN_EVENT_ID_REBOOT, reboot_callback, None)
# 
#         def lifecycle_callback(conn, dom, event, detail, opaque):
#             cbs = AliyunAutoReconnect.libvirt_event_callbacks.get(libvirt.VIR_DOMAIN_EVENT_ID_LIFECYCLE)
#             if not cbs:
#                 return
# 
#             for cb in cbs:
#                 try:
#                     cb(conn, dom, event, detail, opaque)
#                 except:
#                     content = traceback.format_exc()
#                     logger.warn(content)
# 
#         AliyunAutoReconnect.conn.domainEventRegisterAny(None, libvirt.VIR_DOMAIN_EVENT_ID_LIFECYCLE, lifecycle_callback, None)

        # NOTE: the keepalive doesn't work on some libvirtd even the versions are the same
        # the error is like "the caller doesn't support keepalive protocol; perhaps it's missing event loop implementation"

        # def start_keep_alive(_):
        #     try:
        #         _conn.setKeepAlive(5, 3)
        #         return True
        #     except Exception as e:
        #         logger.warn('unable to start libvirt keep-alive, %s' % str(e))
        #         return False
        #
        # if not linux.wait_callback_success(start_keep_alive, timeout=5, interval=0.5):
        #     raise Exception('unable to start libvirt keep-alive after 5 seconds, see the log for detailed error')

    @lock.lock('aliyun-reconnect')
    def _reconnect(self):
        def test_connection():
            try:
                _conn.list_nodes()
                return None
            except BaseHTTPError as ex:
                return ex

        ex = test_connection()
        if not ex:
            # the connection is ok
            return

        logger.warn("the aliyun connection is broken, there is no safeway to auto-reconnect without fd leak, we"
                    " will ask the mgmt server to reconnect us after self quit")
        VmPlugin.queue.put("exit")

        # old_conn = _conn
        # _conn = libvirt.open('qemu:///system')
        # if not _conn:
        #     raise Exception('unable to get a libvirt connection')
        #
        # for cid in AliyunAutoReconnect.callback_id:
        #     logger.debug("remove libvirt event callback[id:%s]" % cid)
        #     old_conn.domainEventDeregisterAny(cid)
        #
        # # stop old event manager
        # AliyunAutoReconnect.evtMgr.stop()
        # # create a new event manager
        # AliyunAutoReconnect.evtMgr = AliyunEventManager()
        # AliyunAutoReconnect.register_libvirt_callbacks()
        #
        # # try to close the old connection anyway
        # try:
        #     old_conn.close()
        # except Exception as ee:
        #     logger.warn('unable to close an old libvirt exception, %s' % str(ee))
        # finally:
        #     del old_conn
        #
        # ex = test_connection()
        # if ex:
        #     # unable to reconnect, raise the error
        #     raise Exception('unable to get a libvirt connection, %s' % str(ex))
        #
        # logger.debug('successfully reconnected to the libvirt')

    def __call__(self, *args, **kwargs):
        try:
            if not _conn:
                logger.warn('unable to get aliyun connection')
                return
            return self.func(_conn)
        except BaseHTTPError as ex:
            err = str(ex)
            if 'InvalidAccessKeyId.NotFound' in err:
                logger.debug('The Access Key ID provided does not exist in our records[%s].' % err)
                self._reconnect()
                return self.func(_conn)
            else:
                raise
            
def _init_aliyun_connection(cmd):
    ECSDriver = get_driver(Provider.ALIYUN_ECS)
    region = cmd.region
    access_key_id = cmd.access_key_id
    access_key_secret = cmd.access_key_secret
    global _conn
    _conn = ECSDriver(access_key_id, access_key_secret, region=region)

def get_vm_by_uuid(uuid, exception_if_not_existing=True):
    try:
        # libvirt may not be able to find a VM when under a heavy workload, we re-try here
        @AliyunAutoReconnect
        def call_aliyun(conn):
            nodes = conn.list_nodes(ex_node_ids=[uuid])
            if not nodes:
                raise LibcloudError('could not find the node with id %s.'
                                    % uuid)                
            if len(nodes) != 1 and uuid != nodes[0].id:
                raise LibcloudError('could not find the node with id %s.'
                                    % uuid)
            return nodes[0]

        @linux.retry(times=3, sleep_time=1)
        def retry_call_aliyun():
            return call_aliyun()

        vm = Vm.from_virt_domain(retry_call_aliyun())
        return vm
    except LibcloudError as e:
        raise ecsagent.AliyunError('unable to find node[uuid:%s]' % uuid)

 


def get_vms(exception_if_not_existing=True):
    try:
        # libvirt may not be able to find a VM when under a heavy workload, we re-try here
        @AliyunAutoReconnect
        def call_aliyun(conn):
            nodes = conn.list_nodes()
            return nodes

        @linux.retry(times=3, sleep_time=1)
        def retry_call_aliyun():
            return call_aliyun()

        vms = retry_call_aliyun();
        restlts = [];
        if (len(vms)>0):
            for tmp in vms :
                vm = Vm.from_virt_domain_withoutNode(tmp);
                restlts.append(vm.__dict__);
        
        return restlts
    except LibcloudError as e:
        raise ecsagent.AliyunError('unable get list of VMs')


def get_running_vm_uuids():
    @AliyunAutoReconnect
    def call_libvirt(conn):
        return conn.listDomainsID()
    ids = call_libvirt()
    uuids = []

    @AliyunAutoReconnect
    def get_domain(conn):
        # i is for..loop's control variable
        # it's Python's local scope tricky
        return conn.lookupByID(i)

    for i in ids:
        domain = get_domain()
        uuids.append(domain.name())
    return uuids

def get_all_vm_states():
    ret = {}
    running = get_running_vm_uuids()
    for r in running:
        ret[r] = Vm.VM_STATE_RUNNING
    return ret

#GOOD

def get_running_vms():
    @AliyunAutoReconnect
    def get_all_ids(conn):
        return conn.listDomainsID()
    ids = get_all_ids()
    vms = []

    @AliyunAutoReconnect
    def get_domain(conn):
        return conn.lookupByID(i)

    for i in ids:
        vm = Vm.from_virt_domain(get_domain())
        vms.append(vm)
    return vms

def get_cpu_memory_used_by_running_vms():
    runnings = get_running_vms()
    used_cpu = 0
    used_memory = 0
    for vm  in runnings:
        used_cpu += vm.get_cpu_num()
        used_memory += vm.get_memory()

    return (used_cpu, used_memory)

def cleanup_stale_vnc_iptable_chains():
    VncPortIptableRule().delete_stale_chains()

class VmOperationJudger(object):
    def __init__(self, op):
        self.op = op
        self.expected_events = {}

        if self.op == VmPlugin.VM_OP_START:
            self.expected_events[AliyunEventManager.EVENT_STARTED] = AliyunEventManager.EVENT_STARTED
        elif self.op == VmPlugin.VM_OP_MIGRATE:
            self.expected_events[AliyunEventManager.EVENT_STOPPED] = AliyunEventManager.EVENT_STOPPED
        elif self.op == VmPlugin.VM_OP_STOP:
            self.expected_events[AliyunEventManager.EVENT_STOPPED] = AliyunEventManager.EVENT_STOPPED
        elif self.op == VmPlugin.VM_OP_DESTROY:
            self.expected_events[AliyunEventManager.EVENT_STOPPED] = AliyunEventManager.EVENT_STOPPED
        elif self.op == VmPlugin.VM_OP_REBOOT:
            self.expected_events[AliyunEventManager.EVENT_STARTED] = AliyunEventManager.EVENT_STARTED
            self.expected_events[AliyunEventManager.EVENT_STOPPED] = AliyunEventManager.EVENT_STOPPED
        else:
            raise Exception('unknown vm operation[%s]' % self.op)

    def remove_expected_event(self, evt):
        del self.expected_events[evt]
        return len(self.expected_events)

    def ignore_libvirt_events(self):
        if self.op == VmPlugin.VM_OP_START:
            return [AliyunEventManager.EVENT_STARTED]
        elif self.op == VmPlugin.VM_OP_MIGRATE:
            return [AliyunEventManager.EVENT_STOPPED, AliyunEventManager.EVENT_UNDEFINED]
        elif self.op == VmPlugin.VM_OP_STOP:
            return [AliyunEventManager.EVENT_STOPPED, AliyunEventManager.EVENT_SHUTDOWN]
        elif self.op == VmPlugin.VM_OP_DESTROY:
            return [AliyunEventManager.EVENT_STOPPED, AliyunEventManager.EVENT_SHUTDOWN, AliyunEventManager.EVENT_UNDEFINED]
        elif self.op == VmPlugin.VM_OP_REBOOT:
            return [AliyunEventManager.EVENT_STARTED, AliyunEventManager.EVENT_STOPPED]
        else:
            raise Exception('unknown vm operation[%s]' % self.op)

class Vm(object):
    VIR_DOMAIN_NOSTATE = 0
    VIR_DOMAIN_RUNNING = 1
    VIR_DOMAIN_BLOCKED = 2
    VIR_DOMAIN_PAUSED = 3
    VIR_DOMAIN_SHUTDOWN = 4
    VIR_DOMAIN_SHUTOFF = 5
    VIR_DOMAIN_CRASHED = 6
    VIR_DOMAIN_PMSUSPENDED = 7

    VM_STATE_NO_STATE = 'NoState'
    VM_STATE_RUNNING = 'Running'
    VM_STATE_PAUSED = 'Paused'
    VM_STATE_SHUTDOWN = 'Shutdown'
    VM_STATE_CRASHED = 'Crashed'
    VM_STATE_SUSPENDED = 'Suspended'

    power_state = {
        VIR_DOMAIN_NOSTATE:VM_STATE_NO_STATE,
        VIR_DOMAIN_RUNNING:VM_STATE_RUNNING,
        VIR_DOMAIN_BLOCKED:VM_STATE_RUNNING,
        VIR_DOMAIN_PAUSED:VM_STATE_PAUSED,
        VIR_DOMAIN_SHUTDOWN:VM_STATE_SHUTDOWN,
        VIR_DOMAIN_SHUTOFF:VM_STATE_SHUTDOWN,
        VIR_DOMAIN_CRASHED:VM_STATE_CRASHED,
        VIR_DOMAIN_PMSUSPENDED:VM_STATE_SUSPENDED,
    }
    
    ECS_T1_XSMALL = 0
    ECS_T1_SMALL = 1
    ECS_S2_SMALL = 2
    ECS_S3_MEDIUM = 3
    ECS_C1_SMALL = 4
    ECS_C2_MEDIUM = 5
    ECS_S1_SMALL = 6
    ECS_S2_LARGE = 7
    ECS_S3_LARGE = 8
    ECS_C1_LARGE = 9
    ECS_C2_LARGE = 10
    ECS_S1_MEDIUM = 11
    ECS_S2_XLARGE = 12
    ECS_N1_TINY = 20
    ECS_N1_SMALL = 21
    ECS_N1_MEDIUM = 22
    ECS_N1_LARGE = 23
    ECS_N1_XLARGE = 24
    
    VM_SIZE_T1_XSMALL = "ecs.t1.xsmall"
    VM_SIZE_T1_SMALL = "ecs.t1.small"
    VM_SIZE_S2_SMALL = "ecs.s2.small"
    VM_SIZE_S3_MEDIUM = "ecs.s3.medium"
    VM_SIZE_C1_SMALL = "ecs.c1.small"
    VM_SIZE_C2_MEDIUM = "ecs.c2.medium"
    VM_SIZE_S1_SMALL = "ecs.s1.small"
    VM_SIZE_S2_LARGE = "ecs.s2.large"
    VM_SIZE_S3_LARGE = "ecs.s3.large"
    VM_SIZE_C1_LARGE = "ecs.c1.large"
    VM_SIZE_C2_LARGE = "ecs.c2.large"
    VM_SIZE_S1_MEDIUM = "ecs.s1.medium"
    VM_SIZE_S2_XLARGE = "ecs.s2.xlarge"
    VM_SIZE_N1_TINY = "ecs.n1.tiny"
    VM_SIZE_N1_SMALL = "ecs.n1.small"
    VM_SIZE_N1_MEDIUM = "ecs.n1.medium"
    VM_SIZE_N1_LARGE = "ecs.n1.large"
    VM_SIZE_N1_XLARGE = "ecs.n1.xlarge"
    
    vm_size = {
        VM_SIZE_T1_XSMALL : ECS_T1_XSMALL,
        VM_SIZE_T1_SMALL : ECS_T1_SMALL,
        VM_SIZE_S2_SMALL : ECS_S2_SMALL,
        VM_SIZE_S3_MEDIUM : ECS_S3_MEDIUM,
        VM_SIZE_C1_SMALL : ECS_C1_SMALL,
        VM_SIZE_C2_MEDIUM : ECS_C2_MEDIUM,
        VM_SIZE_S1_SMALL : ECS_S1_SMALL,
        VM_SIZE_S2_LARGE : ECS_S2_LARGE,
        VM_SIZE_S3_LARGE : ECS_S3_LARGE,
        VM_SIZE_C1_LARGE : ECS_C1_LARGE,
        VM_SIZE_C2_LARGE : ECS_C2_LARGE,
        VM_SIZE_S1_MEDIUM : ECS_S1_MEDIUM,
        VM_SIZE_S2_XLARGE : ECS_S2_XLARGE,
        VM_SIZE_N1_TINY : ECS_N1_TINY,
        VM_SIZE_N1_SMALL : ECS_N1_SMALL,
        VM_SIZE_N1_MEDIUM : ECS_N1_MEDIUM,
        VM_SIZE_N1_LARGE : ECS_N1_LARGE,
        VM_SIZE_N1_XLARGE : ECS_N1_XLARGE,
    }
    
    ECS_IMAGE_UBUNTU1404_64 = 0
    ECS_IMAGE_UBUNTU1404_32 = 1
    ECS_IMAGE_UBUNTU1204_64 = 2
    ECS_IMAGE_UBUNTU1204_32 = 3
    ECS_IMAGE_SUSE12SP1_64 = 4
    ECS_IMAGE_SUSE11SP3_64 = 5
    ECS_IMAGE_SUSE11SP2_64 = 6
    ECS_IMAGE_SUSE11SP1_64 = 7
    ECS_IMAGE_OPENSUSE1301_64 = 8
    ECS_IMAGE_OPENSUSE1301_32 = 9
    ECS_IMAGE_DEBIAN804_64 = 10
    ECS_IMAGE_DEBIAN750_64 = 11
    ECS_IMAGE_DEBIAN750_32 = 12
    ECS_IMAGE_DEBIAN609_64 = 13
    ECS_IMAGE_DEBIAN609_32 = 14
    ECS_IMAGE_CENTOS7u2_64 = 15
    ECS_IMAGE_CENTOS6u5_64 = 16
    ECS_IMAGE_CENTOS6u5_32 = 17
    ECS_IMAGE_CENTOS5u8_64 = 18
    ECS_IMAGE_CENTOS5u10_64 = 19
    ECS_IMAGE_CENTOS5u10_32 = 20
    ECS_IMAGE_ALIYUN1501_64 = 21
    ECS_IMAGE_ALIYUN1501_32 = 22
    ECS_IMAGE_WIN2008_32_STAND_SP2_CN = 23
    ECS_IMAGE_WIN2008_64_ENT_R2_EN = 24
    ECS_IMAGE_WIN2008_64_ENT_R2_CN = 25
    ECS_IMAGE_WIN2012_64_DATACTR_R2_CN = 26
    ECS_IMAGE_WIN2012_64_DATACTR_R2_EN = 27
    ECS_IMAGE_GENTOO13_64 = 28
    ECS_IMAGE_COREOS681_64 = 29
    ECS_IMAGE_FREEBSD1001_64 = 30
    ECS_IMAGE_CENTOS7u0_64 = 31
    ECS_IMAGE_WIN2012_64_STAND_CN = 32
    ECS_IMAGE_WIN2012_64_STAND_EN = 33
    ECS_IMAGE_WIN2008_64_STAND_R2_EN = 34
    ECS_IMAGE_WIN2008_64_STAND_R2_CN = 35
    
    VM_IMAGE_UBUNTU1404_64 = "ecs.image.ubuntu1404.64"
    VM_IMAGE_UBUNTU1404_32 = "ecs.image.ubuntu1404.32"
    VM_IMAGE_UBUNTU1204_64 = "ecs.image.ubuntu1204.64"
    VM_IMAGE_UBUNTU1204_32 = "ecs.image.ubuntu1204.32"
    VM_IMAGE_SUSE12SP1_64 = "ecs.image.suse12sp1.64"
    VM_IMAGE_SUSE11SP3_64 = "ecs.image.suse11sp3.64"
    VM_IMAGE_SUSE11SP2_64 = "ecs.image.suse11sp2.64"
    VM_IMAGE_SUSE11SP1_64 = "ecs.image.suse11sp1.64"
    VM_IMAGE_OPENSUSE1301_64 = "ecs.image.opensuse1301.64"
    VM_IMAGE_OPENSUSE1301_32 = "ecs.image.opensuse1301.32"
    VM_IMAGE_DEBIAN804_64 = "ecs.image.debian804.64"
    VM_IMAGE_DEBIAN750_64 = "ecs.image.debian750.64"
    VM_IMAGE_DEBIAN750_32 = "ecs.image.debian750.32"
    VM_IMAGE_DEBIAN609_64 = "ecs.image.debian609.64"
    VM_IMAGE_DEBIAN609_32 = "ecs.image.debian609.32"
    VM_IMAGE_CENTOS7u2_64 = "ecs.image.centos7u2.64"
    VM_IMAGE_CENTOS6u5_64 = "ecs.image.centos6u5.64"
    VM_IMAGE_CENTOS6u5_32 = "ecs.image.centos6u5.32"
    VM_IMAGE_CENTOS5u8_64 = "ecs.image.centos5u8.64"
    VM_IMAGE_CENTOS5u10_64 = "ecs.image.centos5u10.64"
    VM_IMAGE_CENTOS5u10_32 = "ecs.image.centos5u10.32"
    VM_IMAGE_ALIYUN1501_64 = "ecs.image.aliyun1501.64"
    VM_IMAGE_ALIYUN1501_32 = "ecs.image.aliyun1501.32"
    VM_IMAGE_WIN2008_32_STAND_SP2_CN = "ecs.image.win2008.32.stand.sp2.cn"
    VM_IMAGE_WIN2008_64_ENT_R2_EN = "ecs.image.win2008.64.ent.r2.en"
    VM_IMAGE_WIN2008_64_ENT_R2_CN = "ecs.image.win2008.64.ent.r2.cn"
    VM_IMAGE_WIN2012_64_DATACTR_R2_CN = "ecs.image.win2012.64.datactr.r2.cn"
    VM_IMAGE_WIN2012_64_DATACTR_R2_EN = "ecs.image.win2012.64.datactr.r2.en"
    VM_IMAGE_GENTOO13_64 = "ecs.image.gentoo13.64"
    VM_IMAGE_COREOS681_64 = "ecs.image.coreos681.64"
    VM_IMAGE_FREEBSD1001_64 = "ecs.image.freebsd1001.64"
    VM_IMAGE_CENTOS7u0_64 = "ecs.image.centos7u0.64"
    VM_IMAGE_WIN2012_64_STAND_CN = "ecs.image.win2012.64.stand.cn"
    VM_IMAGE_WIN2012_64_STAND_EN = "ecs.image.win2012.64.stand.en"
    VM_IMAGE_WIN2008_64_STAND_R2_EN = "ecs.image.win2008.64.stand.r2.en"
    VM_IMAGE_WIN2008_64_STAND_R2_CN = "ecs.image.win2008.64.stand.r2.cn"   
    
    vm_image = {
        VM_IMAGE_UBUNTU1404_64 : ECS_IMAGE_UBUNTU1404_64,
        VM_IMAGE_UBUNTU1404_32 : ECS_IMAGE_UBUNTU1404_32,
        VM_IMAGE_UBUNTU1204_64 : ECS_IMAGE_UBUNTU1204_64,
        VM_IMAGE_UBUNTU1204_32 : ECS_IMAGE_UBUNTU1204_32,
        VM_IMAGE_SUSE12SP1_64 : ECS_IMAGE_SUSE12SP1_64,
        VM_IMAGE_SUSE11SP3_64 : ECS_IMAGE_SUSE11SP3_64,
        VM_IMAGE_SUSE11SP2_64 : ECS_IMAGE_SUSE11SP2_64,
        VM_IMAGE_SUSE11SP1_64 : ECS_IMAGE_SUSE11SP1_64,
        VM_IMAGE_OPENSUSE1301_64 : ECS_IMAGE_OPENSUSE1301_64,
        VM_IMAGE_OPENSUSE1301_32 : ECS_IMAGE_OPENSUSE1301_32,
        VM_IMAGE_DEBIAN804_64 : ECS_IMAGE_DEBIAN804_64,
        VM_IMAGE_DEBIAN750_64 : ECS_IMAGE_DEBIAN750_64,
        VM_IMAGE_DEBIAN750_32 : ECS_IMAGE_DEBIAN750_32,
        VM_IMAGE_DEBIAN609_64 : ECS_IMAGE_DEBIAN609_64,
        VM_IMAGE_DEBIAN609_32 : ECS_IMAGE_DEBIAN609_32,
        VM_IMAGE_CENTOS7u2_64 : ECS_IMAGE_CENTOS7u2_64,
        VM_IMAGE_CENTOS6u5_64 : ECS_IMAGE_CENTOS6u5_64,
        VM_IMAGE_CENTOS6u5_32 : ECS_IMAGE_CENTOS6u5_32,
        VM_IMAGE_CENTOS5u8_64 : ECS_IMAGE_CENTOS5u8_64,
        VM_IMAGE_CENTOS5u10_64 : ECS_IMAGE_CENTOS5u10_64,
        VM_IMAGE_CENTOS5u10_32 : ECS_IMAGE_CENTOS5u10_32,
        VM_IMAGE_ALIYUN1501_64 : ECS_IMAGE_ALIYUN1501_64,
        VM_IMAGE_ALIYUN1501_32 : ECS_IMAGE_ALIYUN1501_32,
        VM_IMAGE_WIN2008_32_STAND_SP2_CN : ECS_IMAGE_WIN2008_32_STAND_SP2_CN,
        VM_IMAGE_WIN2008_64_ENT_R2_EN : ECS_IMAGE_WIN2008_64_ENT_R2_EN,
        VM_IMAGE_WIN2008_64_ENT_R2_CN : ECS_IMAGE_WIN2008_64_ENT_R2_CN,
        VM_IMAGE_WIN2012_64_DATACTR_R2_CN : ECS_IMAGE_WIN2012_64_DATACTR_R2_CN,
        VM_IMAGE_WIN2012_64_DATACTR_R2_EN : ECS_IMAGE_WIN2012_64_DATACTR_R2_EN,
        VM_IMAGE_GENTOO13_64 : ECS_IMAGE_GENTOO13_64,
        VM_IMAGE_COREOS681_64 : ECS_IMAGE_COREOS681_64,
        VM_IMAGE_FREEBSD1001_64 : ECS_IMAGE_FREEBSD1001_64,
        VM_IMAGE_CENTOS7u0_64 : ECS_IMAGE_CENTOS7u0_64,
        VM_IMAGE_WIN2012_64_STAND_CN : ECS_IMAGE_WIN2012_64_STAND_CN,
        VM_IMAGE_WIN2012_64_STAND_EN : ECS_IMAGE_WIN2012_64_STAND_EN,
        VM_IMAGE_WIN2008_64_STAND_R2_EN : ECS_IMAGE_WIN2008_64_STAND_R2_EN,
        VM_IMAGE_WIN2008_64_STAND_R2_CN : ECS_IMAGE_WIN2008_64_STAND_R2_CN,
    } 
    
    # letter 'c' is reserved for cdrom
    DEVICE_LETTERS = 'abdefghijklmnopqrstuvwxyz'

    timeout_object = linux.TimeoutObject()

    def __init__(self):
        self.uuid = None
        self.name = None
        self.public_ips = None
        self.private_ips = None
        self.driver = None
        self.state = None
        self.configuration = None
        self.node = None
        
    def create(self):
        #TODO: 1. enbale hair_pin mode
        logger.debug('creating vm:\n%s' % self.configuration)
        name = self.configuration.get('name', None)
        size = self.configuration.get('size', None)
        image = self.configuration.get('image', None)
        auth = self.configuration.get('auth', None)
        if auth:
            auth = NodeAuthPassword(auth)
        ex_security_group_id = self.configuration.get('ex_security_group_id', None)
        ex_data_disks = self.configuration.get('ex_data_disks', {})
        ex_system_disk = self.configuration.get('ex_system_disk', {})
        ex_internet_charge_type = self.configuration.get('ex_internet_charge_type', None)
        ex_internet_max_bandwidth_out = self.configuration.get('ex_internet_max_bandwidth_out', None)
        ex_internet_max_bandwidth_in = self.configuration.get('ex_internet_max_bandwidth_in', None)
        ex_vswitch_id = self.configuration.get('ex_vswitch_id', None)
        ex_private_ip_address = self.configuration.get('ex_private_ip_address', None)
        ex_io_optimized = self.configuration.get('ex_io_optimized', None)
        ex_description = self.configuration.get('ex_description', None)
        ex_hostname = self.configuration.get('ex_hostname', None)
        ex_client_token = self.configuration.get('ex_client_token', None)

        @AliyunAutoReconnect
        def create_node(conn):
            return conn.create_node(name, size, image, auth=auth, \
                                    ex_security_group_id=ex_security_group_id, ex_description=ex_description, \
                                    ex_internet_charge_type=ex_internet_charge_type, \
                                    ex_internet_max_bandwidth_out=ex_internet_max_bandwidth_out, \
                                    ex_internet_max_bandwidth_in=ex_internet_max_bandwidth_in, \
                                    ex_hostname=ex_hostname, ex_io_optimized=ex_io_optimized, \
                                    ex_system_disk=ex_system_disk, ex_data_disks=ex_data_disks, \
                                    ex_vswitch_id=ex_vswitch_id, ex_private_ip_address=ex_private_ip_address, \
                                    ex_client_token=ex_client_token)

        node = create_node()
        self.node = node
        return node.id
        
    def start(self):
        @AliyunAutoReconnect
        def ex_start_node(conn):
            return conn.ex_start_node(self.node)
        ex_start_node()
        
    def stop(self, force_stop=False):
        @AliyunAutoReconnect
        def ex_stop_node(conn):
            return conn.ex_stop_node(self.node, force_stop)
        ex_stop_node()
        
    def reboot(self, force_stop=False):
        @AliyunAutoReconnect
        def reboot_node(conn):
            return conn.reboot_node(self.node, force_stop)
        reboot_node()
        
    def destroy(self):
        @AliyunAutoReconnect
        def destroy_node(conn):
            return conn.destroy_node(self.node)
        destroy_node()

    @staticmethod
    def from_virt_domain(domain):
        vm = Vm()
        vm.name = domain.name
        vm.state = domain.state
        vm.public_ips = domain.public_ips
        vm.private_ips = domain.private_ips
        vm.driver = domain.driver
        vm.uuid = domain.uuid
        vm.node = domain

        return vm
    
    @staticmethod
    def from_virt_domain_withoutNode(domain):
        vm = Vm()
        vm.name = domain.name
        vm.state = domain.state
        vm.public_ips = domain.public_ips
        vm.private_ips = domain.private_ips
        vm.id = domain.id

        return vm

    @staticmethod
    def from_StartVmCmd(cmd):
        configuration = {}
        
        def make_name():
            configuration['name'] = cmd.name
        
        def make_size():
            @AliyunAutoReconnect
            def aliyun_list_sizes(conn):
                return conn.list_sizes()
            size = cmd.size
            size_num = Vm.vm_size.get(size)
            configuration['size'] = aliyun_list_sizes()[size_num]
            
        def make_images():
            @AliyunAutoReconnect
            def aliyun_list_images(conn):
                return conn.list_images()
            image = cmd.image
            image_num = Vm.vm_image.get(image)
            configuration['image'] = aliyun_list_images()[image_num]
            
        def make_password():
            configuration['auth'] = cmd.auth
            
        def make_security_group():
            @AliyunAutoReconnect
            def aliyun_create_security_group(conn):
                return conn.ex_create_security_group()
            
            @AliyunAutoReconnect
            def aliyun_get_security_groups(conn):
                return conn.ex_get_security_groups()
            configuration['ex_security_group_id'] = cmd.ex_security_group_id
            
        def make_disks():
            if cmd.ex_data_disks and cmd.ex_data_disks['size'] and cmd.ex_data_disks['category'] and cmd.ex_data_disks['device']:
                configuration['ex_data_disks'] = [{ 'size':cmd.ex_data_disks['size'], \
                                                   'category':cmd.ex_data_disks['category'], \
                                                   'snapshot_id':cmd.ex_data_disks['snapshot_id'], \
                                                   'disk_name':cmd.ex_data_disks['disk_name'], \
                                                   'description':cmd.ex_data_disks['description'], \
                                                   'device':cmd.ex_data_disks['device']}]
                if cmd.ex_data_disks['category'] in ['cloud', 'cloud_efficiency', 'cloud_ssd']:
                    configuration['ex_data_disks']['delete_with_instance'] = cmd.ex_data_disks['delete_with_instance']
            else:
                configuration['ex_data_disks'] = []
            if cmd.ex_system_disk and cmd.ex_system_disk['category']:
                configuration['ex_system_disk'] = {'category':cmd.ex_system_disk['category'], \
                                                   'disk_name':cmd.ex_system_disk['disk_name'], \
                                                   'description':cmd.ex_system_disk['description']}
            else:
                configuration['ex_system_disk'] = {}
            
        def make_internet_settings():
            configuration['ex_internet_charge_type'] = cmd.ex_internet_charge_type
            configuration['ex_internet_max_bandwidth_out'] = cmd.ex_internet_max_bandwidth_out
            configuration['ex_internet_max_bandwidth_in'] = cmd.ex_internet_max_bandwidth_in
            configuration['ex_vswitch_id'] = cmd.ex_vswitch_id
            configuration['ex_private_ip_address'] = cmd.ex_private_ip_address
            configuration['ex_io_optimized'] = cmd.ex_io_optimized
            
        def make_extra_settings():
            configuration['ex_description'] = cmd.ex_description
            configuration['ex_hostname'] = cmd.ex_hostname
            configuration['ex_client_token'] = cmd.ex_client_token
            
        make_name()
        make_size()
        make_images()
        make_password()
        make_security_group()
        make_disks()
        make_internet_settings()
        make_extra_settings()
        
        vm = Vm()
        vm.configuration = configuration
          
        return vm

class VmPlugin(ecsagent.AliyunAgent):
    ALIYUN_START_VM_PATH = "/aliyun/vm/start"
    ALIYUN_GET_LIST_VM_PATH = "/aliyun/vm/getListPubVm"
    ALIYUN_STOP_VM_PATH = "/aliyun/vm/stop"
    ALIYUN_REBOOT_VM_PATH = "/aliyun/vm/reboot"
    ALIYUN_DESTROY_VM_PATH = "/aliyun/vm/destroy"
    ALIYUN_GET_CONSOLE_PORT_PATH = "/aliyun/vm/getvncport"
    ALIYUN_VM_SYNC_PATH = "/aliyun/vm/aliyun/vmsync"
    ALIYUN_ATTACH_VOLUME = "/aliyun/vm/attachdatavolume"
    ALIYUN_DETACH_VOLUME = "/aliyun/vm/detachdatavolume"
    ALIYUN_MIGRATE_VM_PATH = "/aliyun/vm/migrate"
    ALIYUN_TAKE_VOLUME_SNAPSHOT_PATH = "/aliyun/vm/volume/takesnapshot"
    ALIYUN_MERGE_SNAPSHOT_PATH = "/aliyun/vm/volume/mergesnapshot"
    ALIYUN_ATTACH_NIC_PATH = "/aliyun/vm/attachnic"
    ALIYUN_DETACH_NIC_PATH = "/aliyun/vm/detachnic"
    ALIYUN_CREATE_SECRET = "/aliyun/vm/createcephsecret"
    ALIYUN_ATTACH_ISO_PATH = "/aliyun/vm/iso/attach"
    ALIYUN_DETACH_ISO_PATH = "/aliyun/vm/iso/detach"
    ALIYUN_VM_CHECK_STATE = "/aliyun/vm/checkstate"
    ALIYUN_HARDEN_CONSOLE_PATH = "/aliyun/vm/console/harden"
    ALIYUN_DELETE_CONSOLE_FIREWALL_PATH = "/aliyun/vm/console/deletefirewall"

    VM_OP_START = "start"
    VM_OP_STOP = "stop"
    VM_OP_REBOOT = "reboot"
    VM_OP_MIGRATE = "migrate"
    VM_OP_DESTROY = "destroy"

    timeout_object = linux.TimeoutObject()
    queue = Queue.Queue()

    def _record_operation(self, uuid, op):
        j = VmOperationJudger(op)
        self.timeout_object.put(uuid, j, 300)

    def _remove_operation(self, uuid):
        self.timeout_object.remove(uuid)

    def _get_operation(self, uuid):
        o = self.timeout_object.get(uuid)
        if not o:
            return None
        return o[0]

    def _start_vm(self, cmd):
        try:
            vm = get_vm_by_uuid(cmd.vmInstanceUuid)
        except ecsagent.AliyunError:
            vm = None
    
        try:
            if vm:
                if vm.state == Vm.VM_STATE_RUNNING:
                    raise ecsagent.AliyunError('vm[uuid:%s, name:%s] is already running' % (cmd.vmInstanceUuid, vm.get_name()))
                else:
                    vm.start()
                return vm.uuid
            else:
                vm = Vm.from_StartVmCmd(cmd)
                return vm.create()
        except LibcloudError as e:
            logger.warn(linux.get_exception_stacktrace())
            raise ecsagent.AliyunError('unable to start vm[uuid:%s, name:%s], libvirt error: %s' % (cmd.vmInstanceUuid, cmd.vmName, str(e)))

    def _cleanup_iptable_chains(self, chain, data):
        if 'vnic' not in chain.name:
            return False

        vnic_name = chain.name.split('-')[0]
        if vnic_name not in data:
            logger.debug('clean up defunct vnic chain[%s]' % chain.name)
            return True
        return False

    @ecsagent.replyerror
    def attach_iso(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        rsp = ecsagent.AgentResponse()

        vm = get_vm_by_uuid(cmd.vmUuid)
        vm.attach_iso(cmd)
        return jsonobject.dumps(rsp)

    @ecsagent.replyerror
    def detach_iso(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        rsp = ecsagent.AgentResponse()

        vm = get_vm_by_uuid(cmd.vmUuid)
        vm.detach_iso(cmd)
        return jsonobject.dumps(rsp)

    @ecsagent.replyerror
    def attach_nic(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        rsp = ecsagent.AgentResponse()

        vm = get_vm_by_uuid(cmd.vmUuid)
        vm.attach_nic(cmd)

        return jsonobject.dumps(rsp)

    @ecsagent.replyerror
    def detach_nic(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        rsp = ecsagent.AgentResponse()

        vm = get_vm_by_uuid(cmd.vmUuid)
        vm.detach_nic(cmd)

        return jsonobject.dumps(rsp)

    @ecsagent.replyerror
    def start_vm(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        rsp = StartVmPubResponse()
        try:
            logger.debug("start_vm++++++++++++++++++++")
            _init_aliyun_connection(cmd)
            
            self._record_operation(cmd.vmInstanceUuid, self.VM_OP_START)

            rsp.vmUuid = self._start_vm(cmd)
            logger.debug('successfully started vm[uuid:%s, name:%s]' % (rsp.vmUuid, cmd.vmName))
        except ecsagent.AliyunError as e:
            logger.warn(linux.get_exception_stacktrace())
            rsp.error = str(e)
            rsp.success = False

        return jsonobject.dumps(rsp)

    @ecsagent.replyerror
    def check_vm_state(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        states = get_all_vm_states()
        rsp = CheckVmStateRsp()
        for uuid in cmd.vmUuids:
            s = states.get(uuid)
            if not s:
                s = Vm.VM_STATE_SHUTDOWN
            rsp.states[uuid] = s
        return jsonobject.dumps(rsp)

    @ecsagent.replyerror
    def harden_console(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        rsp = ecsagent.AgentResponse()

        vm = get_vm_by_uuid(cmd.vmUuid)
        vm.harden_console(cmd.hostManagementIp)

        return jsonobject.dumps(rsp)

    @ecsagent.replyerror
    def vm_sync(self, req):
        rsp = VmSyncResponse()
        # vms = get_running_vms()
        # running_vms = {}
        # for vm in vms:
        #     if vm.state == Vm.VM_STATE_RUNNING:
        #         running_vms[vm.uuid] = Vm.VM_STATE_RUNNING
        #     else:
        #         try:
        #             logger.debug('VM[uuid:%s] is in state of %s, destroy it' % (vm.uuid, vm.state))
        #             vm.destroy()
        #         except:
        #             logger.warn(linux.get_exception_stacktrace())
        #
        # rsp.states = running_vms
        rsp.states = get_all_vm_states()
        return jsonobject.dumps(rsp)

    @ecsagent.replyerror
    def get_console_port(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        rsp = GetVncPortResponse()
        try:
            vm = get_vm_by_uuid(cmd.vmUuid)
            port = vm.get_console_port()
            rsp.port = port
            rsp.protocol = vm.get_console_protocol()
            logger.debug('successfully get vnc port[%s] of vm[uuid:%s]' % (port, cmd.uuid))
        except ecsagent.AliyunError as e:
            logger.warn(linux.get_exception_stacktrace())
            rsp.error = str(e)
            rsp.success = False

        return jsonobject.dumps(rsp)

    def _stop_vm(self, cmd):
        try:
            vm = get_vm_by_uuid(cmd.vmUuid)
        except ecsagent.AliyunError as e:
            logger.debug(linux.get_exception_stacktrace())
            logger.debug('however, the stop operation is still considered as success')
            return

        vm.stop(force_stop=cmd.force)

    @ecsagent.replyerror
    def stop_vm(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        rsp = StopVmResponse()
        try:
            self._record_operation(cmd.uuid, self.VM_OP_STOP)

            self._stop_vm(cmd)
            logger.debug("successfully stopped vm[uuid:%s]" % cmd.uuid)
        except ecsagent.AliyunError as e:
            logger.warn(linux.get_exception_stacktrace())
            rsp.error = str(e)
            rsp.success = False

        return jsonobject.dumps(rsp)

    @ecsagent.replyerror
    def reboot_vm(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        rsp = RebootVmResponse()
        try:
            self._record_operation(cmd.vmUuid, self.VM_OP_REBOOT)

            vm = get_vm_by_uuid(cmd.vmUuid)
            vm.reboot(force_stop=cmd.force)
            logger.debug('successfully, reboot vm[uuid:%s]' % cmd.vmUuid)
        except ecsagent.AliyunError as e:
            logger.warn(linux.get_exception_stacktrace())
            rsp.error = str(e)
            rsp.success = False

        return jsonobject.dumps(rsp)
    
    
    
    @ecsagent.replyerror
    def getPubList_vm(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        rsp = GetPubVmResponse()
        try:
            self._record_operation(cmd.vmUuid, self.VM_OP_REBOOT)
            vms = get_vms()
            rsp.nodes = vms;
            logger.debug('successfully get lists %s' % vms)
        except ecsagent.AliyunError as e:
            logger.warn(linux.get_exception_stacktrace())
            rsp.error = str(e)
            rsp.success = False
        result = jsonobject.dumps(rsp)  
        logger.debug('--------return  pub lists %s' % result) 
        return result

    @ecsagent.replyerror
    def destroy_vm(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        rsp = DestroyVmResponse()
        try:
            self._record_operation(cmd.vmUuid, self.VM_OP_DESTROY)

            vm = get_vm_by_uuid(cmd.vmUuid, False)
            if vm:
                vm.destroy()
                logger.debug('successfully destroyed vm[uuid:%s]' % cmd.uuid)
        except ecsagent.AliyunError as e:
            logger.warn(linux.get_exception_stacktrace())
            rsp.error = str(e)
            rsp.success = False

        return jsonobject.dumps(rsp)

    @ecsagent.replyerror
    def attach_data_volume(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        rsp = AttachDataVolumeResponse()
        try:
            volume = cmd.volume
            vm = get_vm_by_uuid(cmd.vmInstanceUuid)
            if vm.state != Vm.VM_STATE_RUNNING:
                raise ecsagent.AliyunError('unable to attach volume[%s] to vm[uuid:%s], vm must be running' % (volume.installPath, vm.uuid))
            vm.attach_data_volume(cmd.volume, cmd.addons)
        except ecsagent.AliyunError as e:
            logger.warn(linux.get_exception_stacktrace())
            rsp.error = str(e)
            rsp.success = False

        return jsonobject.dumps(rsp)

    @ecsagent.replyerror
    def detach_data_volume(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        rsp = DetachDataVolumeResponse()
        try:
            volume = cmd.volume
            vm = get_vm_by_uuid(cmd.vmInstanceUuid)
            if vm.state != Vm.VM_STATE_RUNNING:
                raise ecsagent.AliyunError('unable to detach volume[%s] to vm[uuid:%s], vm must be running' % (volume.installPath, vm.uuid))
            vm.detach_data_volume(volume)
        except ecsagent.AliyunError as e:
            logger.warn(linux.get_exception_stacktrace())
            rsp.error = str(e)
            rsp.success = False

        return jsonobject.dumps(rsp)

    @ecsagent.replyerror
    def migrate_vm(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        rsp = MigrateVmResponse()
        try:
            self._record_operation(cmd.vmUuid, self.VM_OP_MIGRATE)

            vm = get_vm_by_uuid(cmd.vmUuid)
            vm.migrate(cmd)
        except ecsagent.AliyunError as e:
            logger.warn(linux.get_exception_stacktrace())
            rsp.error = str(e)
            rsp.success = False

        return jsonobject.dumps(rsp)

    @ecsagent.replyerror
    def merge_snapshot_to_volume(self, req):
        rsp = MergeSnapshotRsp()
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        vm = get_vm_by_uuid(cmd.vmUuid, exception_if_not_existing=True)

        if vm.state != vm.VM_STATE_RUNNING:
            rsp.error = 'vm[uuid:%s] is not running, cannot do live snapshot chain merge' % vm.uuid
            rsp.success = False
            return jsonobject.dumps(rsp)

        vm.merge_snapshot(cmd)
        return jsonobject.dumps(rsp)


    @ecsagent.replyerror
    def take_volume_snapshot(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        rsp = TakeSnapshotResponse()

        def makedir_if_need(new_path):
            dirname = os.path.dirname(new_path)
            if not os.path.exists(dirname):
                os.makedirs(dirname, 0755)

        def take_full_snapshot_by_qemu_img_convert(previous_install_path, install_path):
            makedir_if_need(install_path)
            linux.qcow2_create_template(previous_install_path, install_path)
            new_volume_path = os.path.join(os.path.dirname(install_path), '{0}.qcow2'.format(uuidhelper.uuid()))
            makedir_if_need(new_volume_path)
            linux.qcow2_clone(install_path, new_volume_path)
            return install_path, new_volume_path

        def take_delta_snapshot_by_qemu_img_convert(previous_install_path, install_path):
            new_volume_path = os.path.join(os.path.dirname(install_path), '{0}.qcow2'.format(uuidhelper.uuid()))
            makedir_if_need(new_volume_path)
            linux.qcow2_clone(previous_install_path, new_volume_path)
            return previous_install_path, new_volume_path

        try:
            if not cmd.vmUuid:
                if cmd.fullSnapshot:
                    rsp.snapshotInstallPath, rsp.newVolumeInstallPath = take_full_snapshot_by_qemu_img_convert(cmd.volumeInstallPath, cmd.installPath)
                else:
                    rsp.snapshotInstallPath, rsp.newVolumeInstallPath = take_delta_snapshot_by_qemu_img_convert(cmd.volumeInstallPath, cmd.installPath)

            else:
                vm = get_vm_by_uuid(cmd.vmUuid, exception_if_not_existing=False)

                if vm and vm.state != vm.VM_STATE_RUNNING and vm.state != vm.VM_STATE_SHUTDOWN:
                    raise ecsagent.AliyunError('unable to take snapshot on vm[uuid:{0}] volume[id:{1}], because vm is not Running or Stopped, current state is {2}'.format(vm.uuid, cmd.deviceId, vm.state))

                if vm and vm.state == vm.VM_STATE_RUNNING:
                    rsp.snapshotInstallPath, rsp.newVolumeInstallPath = vm.take_volume_snapshot(cmd.deviceId, cmd.installPath, cmd.fullSnapshot)
                else:
                    if cmd.fullSnapshot:
                        rsp.snapshotInstallPath, rsp.newVolumeInstallPath = take_full_snapshot_by_qemu_img_convert(cmd.volumeInstallPath, cmd.installPath)
                    else:
                        rsp.snapshotInstallPath, rsp.newVolumeInstallPath = take_delta_snapshot_by_qemu_img_convert(cmd.volumeInstallPath, cmd.installPath)


                if cmd.fullSnapshot:
                    logger.debug('took full snapshot on vm[uuid:{0}] volume[id:{1}], snapshot path:{2}, new volulme path:{3}'.format(cmd.vmUuid, cmd.deviceId, rsp.snapshotInstallPath, rsp.newVolumeInstallPath))
                else:
                    logger.debug('took delta snapshot on vm[uuid:{0}] volume[id:{1}], snapshot path:{2}, new volulme path:{3}'.format(cmd.vmUuid, cmd.deviceId, rsp.snapshotInstallPath, rsp.newVolumeInstallPath))

            rsp.size = os.path.getsize(rsp.snapshotInstallPath)
        except ecsagent.AliyunError as e:
            logger.warn(linux.get_exception_stacktrace())
            rsp.error = str(e)
            rsp.success = False

        return jsonobject.dumps(rsp)

    @ecsagent.replyerror
    def delete_console_firewall_rule(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        vir = VncPortIptableRule()
        vir.vm_internal_id = cmd.vmInternalId
        vir.host_ip = cmd.hostManagementIp
        vir.delete()

        return jsonobject.dumps(ecsagent.AgentResponse())

    @ecsagent.replyerror
    def create_ceph_secret_key(self, req):
        cmd = jsonobject.loads(req[http.REQUEST_BODY])
        sh_cmd = shell.ShellCmd('virsh secret-list | grep %s > /dev/null' % cmd.uuid)
        sh_cmd(False)
        if sh_cmd.return_code == 0:
            return jsonobject.dumps(ecsagent.AgentResponse())

        # for some reason, ceph doesn't work with the secret created by libvirt
        # we have to use the command line here
        content = '''
<secret ephemeral='yes' private='no'>
    <uuid>%s</uuid>
    <usage type='ceph'>
        <name>%s</name>
    </usage>
</secret>
    ''' % (cmd.uuid, cmd.uuid)

        spath = linux.write_to_temp_file(content)
        try:
            o = shell.call("virsh secret-define %s" % spath)
            o = o.strip(' \n\t\r')
            _, uuid, _ = o.split()
            shell.call('virsh secret-set-value %s %s' % (uuid, cmd.userKey))
        finally:
            os.remove(spath)

        return jsonobject.dumps(ecsagent.AgentResponse())

    def start(self):
        http_server = ecsagent.get_http_server()

        http_server.register_async_uri(self.ALIYUN_START_VM_PATH, self.start_vm)
        http_server.register_async_uri(self.ALIYUN_GET_LIST_VM_PATH, self.getPubList_vm)
        http_server.register_async_uri(self.ALIYUN_STOP_VM_PATH, self.stop_vm)
        http_server.register_async_uri(self.ALIYUN_REBOOT_VM_PATH, self.reboot_vm)
        http_server.register_async_uri(self.ALIYUN_DESTROY_VM_PATH, self.destroy_vm)
        http_server.register_async_uri(self.ALIYUN_GET_CONSOLE_PORT_PATH, self.get_console_port)
        http_server.register_async_uri(self.ALIYUN_VM_SYNC_PATH, self.vm_sync)
        http_server.register_async_uri(self.ALIYUN_ATTACH_VOLUME, self.attach_data_volume)
        http_server.register_async_uri(self.ALIYUN_DETACH_VOLUME, self.detach_data_volume)
        http_server.register_async_uri(self.ALIYUN_ATTACH_ISO_PATH, self.attach_iso)
        http_server.register_async_uri(self.ALIYUN_DETACH_ISO_PATH, self.detach_iso)
        http_server.register_async_uri(self.ALIYUN_MIGRATE_VM_PATH, self.migrate_vm)
        http_server.register_async_uri(self.ALIYUN_TAKE_VOLUME_SNAPSHOT_PATH, self.take_volume_snapshot)
        http_server.register_async_uri(self.ALIYUN_MERGE_SNAPSHOT_PATH, self.merge_snapshot_to_volume)
        http_server.register_async_uri(self.ALIYUN_ATTACH_NIC_PATH, self.attach_nic)
        http_server.register_async_uri(self.ALIYUN_DETACH_NIC_PATH, self.detach_nic)
        http_server.register_async_uri(self.ALIYUN_CREATE_SECRET, self.create_ceph_secret_key)
        http_server.register_async_uri(self.ALIYUN_VM_CHECK_STATE, self.check_vm_state)
        http_server.register_async_uri(self.ALIYUN_HARDEN_CONSOLE_PATH, self.harden_console)
        http_server.register_async_uri(self.ALIYUN_DELETE_CONSOLE_FIREWALL_PATH, self.delete_console_firewall_rule)

#         self.register_libvirt_event()

        @thread.AsyncThread
        def wait_end_signal():
            while True:
                try:
                    self.queue.get(True)

                    # the libvirt has been stopped or restarted
                    # to prevent fd leak caused by broken libvirt connection
                    # we have to ask mgmt server to reboot the agent
                    url = self.config.get(ecsagent.SEND_COMMAND_URL)
                    if not url:
                        logger.warn('cannot find SEND_COMMAND_URL, unable to ask the mgmt server to reconnect us')
                        os._exit(1)

                    host_uuid = self.config.get(ecsagent.HOST_UUID)
                    if not host_uuid:
                        logger.warn('cannot find HOST_UUID, unable to ask the mgmt server to reconnect us')
                        os._exit(1)

                    logger.warn("libvirt has been rebooted or stopped, ask the mgmt server to reconnt us")
                    cmd = ReconnectMeCmd()
                    cmd.hostUuid = host_uuid
                    cmd.reason = "libvirt rebooted or stopped"
                    http.json_dump_post(url, cmd, {'commandpath':'/kvm/reconnectme'})
                    os._exit(1)
                except:
                    content = traceback.format_exc()
                    logger.warn(content)

        wait_end_signal()

        @thread.AsyncThread
        def monitor_libvirt():
            while True:
                if shell.run('pid=$(cat /var/run/libvirtd.pid); ps -p $pid > /dev/null') != 0:
                    logger.warn("cannot find the libvirt process, assume it's dead, ask the mgmt server to reconnect us")
                    self.queue.put("exit")

                time.sleep(20)

        monitor_libvirt()

    def _vm_lifecycle_event(self, conn, dom, event, detail, opaque):
        try:
            evstr = AliyunEventManager.event_to_string(event)
            vm_uuid = dom.name()
            if evstr not in (AliyunEventManager.EVENT_STARTED, AliyunEventManager.EVENT_STOPPED):
                logger.debug("ignore event[%s] of the vm[uuid:%s]" % (evstr, vm_uuid))
                return

            vm_op_judger = self._get_operation(vm_uuid)
            if vm_op_judger and evstr in vm_op_judger.ignore_libvirt_events():
                # this is an operation originated from ZStack itself
                logger.debug('ignore event[%s] for the vm[uuid:%s], this operation is from ZStack itself' % (evstr, vm_uuid))

                if vm_op_judger.remove_expected_event(evstr) == 0:
                    self._remove_operation(vm_uuid)
                    logger.debug('events happened of the vm[uuid:%s] meet the expectation, delete the the operation judger' % vm_uuid)

                return

            # this is an operation outside zstack, report it
            url = self.config.get(ecsagent.SEND_COMMAND_URL)
            if not url:
                logger.warn('cannot find SEND_COMMAND_URL, unable to report abnormal operation[vm:%s, op:%s]' % (vm_uuid, evstr))
                return

            host_uuid = self.config.get(ecsagent.HOST_UUID)
            if not host_uuid:
                logger.warn('cannot find HOST_UUID, unable to report abnormal operation[vm:%s, op:%s]' % (vm_uuid, evstr))
                return

            @thread.AsyncThread
            def report_to_management_node():
                cmd = ReportVmStateCmd()
                cmd.vmUuid = vm_uuid
                cmd.hostUuid = host_uuid
                if evstr == AliyunEventManager.EVENT_STARTED:
                    cmd.vmState = Vm.VM_STATE_RUNNING
                elif evstr == AliyunEventManager.EVENT_STOPPED:
                    cmd.vmState = Vm.VM_STATE_SHUTDOWN

                logger.debug('detected an abnormal vm operation[uuid:%s, op:%s], report it to %s' % (vm_uuid, evstr, url))
                http.json_dump_post(url, cmd, {'commandpath':'/kvm/reportvmstate'})

            report_to_management_node()
        except:
            content = traceback.format_exc()
            logger.warn(content)

    def _vm_reboot_event(self, conn, dom, opaque):
        try:
            domain_xml = dom.XMLDesc(0)
            domain_xmlobject = xmlobject.loads(domain_xml)
            vm_uuid = dom.name()
            boot_dev = domain_xmlobject.os.get_child_node_as_list('boot')[0]
            if boot_dev.dev_ != 'cdrom':
                logger.debug("the vm[uuid:%s]'s boot device is %s, nothing to do, skip this reboot event" % (vm_uuid, boot_dev))
                return

            logger.debug('the vm[uuid:%s] is set to boot from the cdrom, for the policy[bootFromHardDisk], the reboot will'
                         ' boot from hdd' % vm_uuid)

            self._record_operation(vm_uuid, VmPlugin.VM_OP_REBOOT)
            boot_dev = xmlobject.XmlObject('boot')
            boot_dev.put_attr('dev', 'hd')
            domain_xmlobject.os.replace_node('boot', boot_dev)
            dom.destroy()

            xml = domain_xmlobject.dump()
            domain = conn.defineXML(xml)
            domain.createWithFlags(0)
        except:
            content = traceback.format_exc()
            logger.warn(content)

    def _set_vnc_port_iptable_rule(self, conn, dom, event, detail, opaque):
        try:
            event = AliyunEventManager.event_to_string(event)
            if event not in (AliyunEventManager.EVENT_STARTED, AliyunEventManager.EVENT_STOPPED):
                return

            domain_xml = dom.XMLDesc(0)
            domain_xmlobject = xmlobject.loads(domain_xml)
            vm_uuid = dom.name()
            if not xmlobject.has_element(domain_xmlobject, 'metadata.internalId'):
                logger.debug('vm[uuid:%s] is not managed by zstack,  do not configure the vnc iptables rules' % vm_uuid)
                return

            id = domain_xmlobject.metadata.internalId.text_
            vir = VncPortIptableRule()
            if AliyunEventManager.EVENT_STARTED == event:
                vir.host_ip = domain_xmlobject.metadata.hostManagementIp.text_

                if shell.run('ip addr | grep -w %s > /dev/null' % vir.host_ip) != 0:
                    logger.debug('the vm is migrated from another host, we do not need to set the console firewall, as '
                                 'the management node will take care')
                    return

                for g in domain_xmlobject.devices.get_child_node_as_list('graphics'):
                    if g.type_ == 'vnc' or g.type_ == 'spice':
                        vir.port = g.port_
                        break

                vir.vm_internal_id = id
                vir.apply()
            elif AliyunEventManager.EVENT_STOPPED == event:
                vir.vm_internal_id = id
                vir.delete()

        except:
            content = traceback.format_exc()
            logger.warn(content)

#     def register_libvirt_event(self):
#         AliyunAutoReconnect.add_aliyun_callback(libvirt.VIR_DOMAIN_EVENT_ID_LIFECYCLE, self._vm_lifecycle_event)
#         AliyunAutoReconnect.add_aliyun_callback(libvirt.VIR_DOMAIN_EVENT_ID_LIFECYCLE, self._set_vnc_port_iptable_rule)
#         AliyunAutoReconnect.add_aliyun_callback(libvirt.VIR_DOMAIN_EVENT_ID_REBOOT, self._vm_reboot_event)
#         AliyunAutoReconnect.register_libvirt_callbacks()

    def stop(self):
        pass

    def configure(self, config):
        self.config = config
