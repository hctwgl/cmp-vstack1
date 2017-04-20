package org.zstack.xen;

import org.zstack.header.host.HostVO_;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 */
@StaticMetamodel(XenHostVO.class)
public class XenHostVO_ extends HostVO_ {
    public static volatile SingularAttribute<XenHostVO, String> username;
    public static volatile SingularAttribute<XenHostVO, String> password;
    public static volatile SingularAttribute<XenHostVO, Integer> port;
}
