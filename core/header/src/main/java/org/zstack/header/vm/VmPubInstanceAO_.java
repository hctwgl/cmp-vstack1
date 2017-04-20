package org.zstack.header.vm;

import javax.persistence.Column;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(VmInstanceAO.class)
public class VmPubInstanceAO_ {
    public static volatile SingularAttribute<VmPubInstanceAO, String> uuid;
    public static volatile SingularAttribute<VmPubInstanceAO, String> name;
    public static volatile SingularAttribute<VmPubInstanceAO, String> accesskeyID;
    public static volatile SingularAttribute<VmPubInstanceAO, String> accesskeyKey;
    public static volatile SingularAttribute<VmPubInstanceAO, Timestamp> createDate;
    public static volatile SingularAttribute<VmPubInstanceAO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<VmPubInstanceAO, VmInstanceState> state;
}
 