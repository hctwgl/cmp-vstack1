package org.zstack.header.vm;

import org.zstack.header.identity.APIQueryPubAccountReply;
import org.zstack.header.identity.Action;
import org.zstack.header.identity.PubAccountInventory;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;


@AutoQuery(replyClass = APIQueryPubVmInstanceReply.class, inventoryClass = PubVmInstanceInventory.class)
public class APIQueryPubVmInstanceMsg extends APIQueryMessage {
}
 