package org.zstack.test.core.scheduler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.scheduler.APIQuerySchedulerMsg;
import org.zstack.core.scheduler.APIQuerySchedulerReply;
import org.zstack.core.scheduler.SchedulerFacade;
import org.zstack.header.core.scheduler.SchedulerInventory;
import org.zstack.header.core.scheduler.SchedulerVO;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.query.QueryOp;
import org.zstack.header.storage.snapshot.VolumeSnapshotVO;
import org.zstack.header.vm.VmInstanceInventory;
import org.zstack.simulator.kvm.VolumeSnapshotKvmSimulator;
import org.zstack.test.Api;
import org.zstack.test.ApiSenderException;
import org.zstack.test.DBUtil;
import org.zstack.test.WebBeanConstructor;
import org.zstack.test.deployer.Deployer;

import java.util.concurrent.TimeUnit;

/**
 * Created by root on 7/19/16.
 */
public class TestSchedulerCron {
    ComponentLoader loader;
    Api api;
    @Autowired
    SchedulerFacade scheduler;
    DatabaseFacade dbf;
    CloudBus bus;
    Deployer deployer;
    SessionInventory session;
    VolumeSnapshotKvmSimulator snapshotKvmSimulator;

    @Before
    public void setUp() throws Exception {
        DBUtil.reDeployDB();
        WebBeanConstructor con = new WebBeanConstructor();
        deployer = new Deployer("deployerXml/kvm/TestCreateVmOnKvm.xml", con);
        deployer.addSpringConfig("KVMRelated.xml");
        deployer.addSpringConfig("SchedulerFacade.xml");
        deployer.build();
        api = deployer.getApi();
        loader = deployer.getComponentLoader();
        bus = loader.getComponent(CloudBus.class);
        dbf = loader.getComponent(DatabaseFacade.class);
        scheduler = loader.getComponent(SchedulerFacade.class);
        snapshotKvmSimulator = loader.getComponent(VolumeSnapshotKvmSimulator.class);
        SessionInventory session = api.loginByAccount("test", "password");
    }

    @Test
    public void test() throws InterruptedException, ApiSenderException, SchedulerException {
        Assert.assertNotNull(scheduler);
        VmInstanceInventory vm = deployer.vms.get("TestVm");
        String volUuid = vm.getRootVolumeUuid();
        String type = "cron";
        String cronTask = "*/3 * * * * ?";
        api.createCronScheduler(volUuid, type, cronTask, session);
        TimeUnit.SECONDS.sleep(8);
        long counter = dbf.count(VolumeSnapshotVO.class);
        Assert.assertEquals(3,counter);
        SchedulerVO vo = dbf.listAll(SchedulerVO.class).get(0);
        SchedulerInventory inv = api.changeSchedulerState(vo.getUuid(),"disable",session);
        SchedulerVO changeRecord = dbf.listAll(SchedulerVO.class).get(0);
        Assert.assertEquals("Disabled", changeRecord.getState());
        Assert.assertEquals("Disabled", inv.getState());


        APIQuerySchedulerMsg msg = new APIQuerySchedulerMsg();
        msg.addQueryCondition("schedulerType", QueryOp.EQ, vo.getSchedulerType());
        APIQuerySchedulerReply reply = api.query(msg, APIQuerySchedulerReply.class);
        Assert.assertEquals(1, reply.getInventories().size());

    }
}
