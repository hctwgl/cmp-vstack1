package org.zstack.header.vm;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import org.zstack.header.identity.PubAccountEO;
import org.zstack.header.vo.ForeignKey;
import org.zstack.header.vo.Index;
import org.zstack.header.vo.ForeignKey.ReferenceOption;

import java.sql.Timestamp;

@StaticMetamodel(PubVmInstanceAO.class)
public class PubVmInstanceAO_ {
    public static volatile SingularAttribute<VmInstanceAO, String> uuid;
    public static volatile SingularAttribute<VmInstanceAO, String> name;
    public static volatile SingularAttribute<VmInstanceAO, String> description;
    public static volatile SingularAttribute<VmInstanceAO, String> accountUuid;
    public static volatile SingularAttribute<VmInstanceAO, String> image;
    public static volatile SingularAttribute<VmInstanceAO, String> deleted;
    public static volatile SingularAttribute<VmInstanceAO, String> cloudType;
    public static volatile SingularAttribute<VmInstanceAO, String> otherInfo;
    public static volatile SingularAttribute<VmInstanceAO, String> region;
    public static volatile SingularAttribute<VmInstanceAO, Long> memorySize;
    public static volatile SingularAttribute<VmInstanceAO, Integer> cpuInfo;
    public static volatile SingularAttribute<VmInstanceAO, Timestamp> createDate;
    public static volatile SingularAttribute<VmInstanceAO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<VmInstanceAO, VmInstanceState> state;
}


 
 