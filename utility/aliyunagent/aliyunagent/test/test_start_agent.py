'''

@author: frank
'''
import unittest
import time
from aliyunagent import aliyunagent


class TestStartAgent(unittest.TestCase):


    def test_start(self):
        service = aliyunagent.new_rest_service()
        service.start()
        time.sleep(2)
        service.stop()


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()