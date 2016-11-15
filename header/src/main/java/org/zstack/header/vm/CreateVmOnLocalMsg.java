package org.zstack.header.vm;

import org.zstack.header.core.ApiTimeout;
import org.zstack.header.host.HostMessage;
import org.zstack.header.message.NeedReplyMessage;

@ApiTimeout(apiClasses = {APICreateVmInstanceMsg.class})
public class CreateVmOnLocalMsg extends NeedReplyMessage implements HostMessage {
    private VmPubInstanceSpec vmSpec;
   
    private String name;
	 private String uuid;
	    public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

		private String accesskeyKEY;
	    private String accesskeyID;

	    public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAccesskeyKEY() {
			return accesskeyKEY;
		}

		public void setAccesskeyKEY(String accesskeyKEY) {
			this.accesskeyKEY = accesskeyKEY;
		}

		public String getAccesskeyID() {
			return accesskeyID;
		}

		public void setAccesskeyID(String accesskeyID) {
			this.accesskeyID = accesskeyID;
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
