package org.zstack.header.vm;

import org.zstack.header.core.ApiTimeout;
import org.zstack.header.host.HostMessage;
import org.zstack.header.identity.PubAccountEO;
import org.zstack.header.message.NeedReplyMessage;

public class QueryPubVmMsg extends NeedReplyMessage implements HostMessage {
    public String getAccessKeyID() {
		return accessKeyID;
	}

	public void setAccessKeyID(String accessKeyID) {
		this.accessKeyID = accessKeyID;
	}

	public String getAccessKeyKey() {
		return accessKeyKey;
	}

	public void setAccessKeyKey(String accessKeyKey) {
		this.accessKeyKey = accessKeyKey;
	}

	private VmPubInstanceSpec vmSpec;
   private String Uuid;
private String accessKeyID;
private String accessKeyKey;
    
    
    public String getUuid() {
	return Uuid;
}

public void setUuid(String uuid) {
	Uuid = uuid;
}

	private String cloudType;
    private PubAccountEO accountEo;
     
 

	public String getCloudType() {
		return cloudType;
	}

	public void setCloudType(String cloudType) {
		this.cloudType = cloudType;
	}

	public PubAccountEO getAccountEo() {
		return accountEo;
	}

	public void setAccountEo(PubAccountEO accountEo) {
		this.accountEo = accountEo;
	}

	@Override
    public String getHostUuid() {
        return vmSpec.getUuid();
    }

	public VmPubInstanceSpec getVmSpec() {
		return vmSpec;
	}

	public void setVmSpec(VmPubInstanceSpec vmSpec) {
		this.vmSpec = vmSpec;
	}
}
