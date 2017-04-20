package org.zstack.header.vm;

import java.util.List;

import org.zstack.header.message.MessageReply;

/**
 * Created by david on 8/4/16.
 */
public class QueryPubVmInstanceReply extends MessageReply {
    private List<PubVmInstanceEO> pubvms;

    public List<PubVmInstanceEO> getPubvms() {
		return pubvms;
	}

	public void setPubvms(List<PubVmInstanceEO> pubvms) {
		this.pubvms = pubvms;
	}

}
