package org.zstack.network.service.lb;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APICreateMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.tag.TagResourceType;
import org.zstack.network.service.vip.VipVO;

import java.util.List;

/**
 * Created by frank on 8/8/2015.
 */
@TagResourceType(LoadBalancerVO.class)
@Action(category = LoadBalancerConstants.ACTION_CATEGORY)
public class APICreateLoadBalancerMsg extends APICreateMessage {
    @APIParam(maxLength = 255)
    private String name;
    @APIParam(maxLength = 2048, required = false)
    private String description;
    @APIParam(resourceType = VipVO.class, checkAccount = true)
    private String vipUuid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVipUuid() {
        return vipUuid;
    }

    public void setVipUuid(String vipUuid) {
        this.vipUuid = vipUuid;
    }
}
