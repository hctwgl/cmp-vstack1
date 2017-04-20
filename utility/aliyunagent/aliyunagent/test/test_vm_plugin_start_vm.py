'''

@author: Frank
'''
import unittest
from aliyunagent import ecsagent
from aliyunagent.plugins import vm_plugin
from zstacklib.utils import http
from zstacklib.utils import jsonobject
from zstacklib.utils import log
from zstacklib.utils import uuidhelper
from zstacklib.utils import linux
import time

class Test(unittest.TestCase):
    
    CALLBACK_URL = 'http://localhost:7070/testcallback'
    
    def callback(self, req):
        rsp = jsonobject.loads(req[http.REQUEST_BODY])
        print jsonobject.dumps(rsp)
        
    def setUp(self):
        self.service = ecsagent.new_rest_service()
        ecsagent.get_http_server().register_sync_uri('/testcallback', self.callback)
        self.service.start(True)
        time.sleep(1)

    def testName(self):
        cmd = vm_plugin.StartVmCmd()
        cmd.region = 'cn-beijing'
        cmd.access_key_id = 'LTAIrgvAPlmGRPQY'
        cmd.access_key_secret = 'oAIuo2xWVqnWOmrsoXLcLEFYvNCRr0'
        cmd.vmInstanceUuid = uuidhelper.uuid()
        cmd.name = 'test'
        cmd.image = uuidhelper.uuid()
        cmd.size = "ecs.t1.xsmall"
        cmd.auth = "Test1234"
        cmd.ex_security_group_id = "sg-2ze56hvvjveewzm12jar"
        cmd.ex_description = 'PayByTraffic'
        cmd.ex_internet_charge_type = None
        cmd.ex_internet_max_bandwidth_out = 1
        cmd.ex_internet_max_bandwidth_in = None
        cmd.ex_hostname = None
        cmd.ex_io_optimized = None
        cmd.ex_system_disk = {}
        cmd.ex_data_disks = {}
        cmd.ex_vswitch_id = None
        cmd.ex_private_ip_address = None
        cmd.ex_client_token = None
        print 'xxxxxxxxxxxxxxxxxxx %s' % cmd.name
        url = ecsagent._build_url_for_test([vm_plugin.VmPlugin().ALIYUN_START_VM_PATH])
        rsp = http.json_dump_post(url, cmd, headers={http.TASK_UUID:uuidhelper.uuid(), http.CALLBACK_URI:self.CALLBACK_URL})
        time.sleep(30)
        self.service.stop()


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()