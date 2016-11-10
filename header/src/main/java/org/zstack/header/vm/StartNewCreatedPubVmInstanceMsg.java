package org.zstack.header.vm;

import org.zstack.header.core.ApiTimeout;
import org.zstack.header.message.NeedReplyMessage;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;

@ApiTimeout(apiClasses = {APICreateVmInstanceMsg.class})
public class StartNewCreatedPubVmInstanceMsg extends NeedReplyMessage implements VmInstanceMessage  { 
		private String Uuid;
		     private String name;
		     private String accesskeyID;
		     private String accesskeyKey;
		     private String state;
		     private Timestamp createData;
		     private Timestamp lastOpDate;
		
		
		 

		    public String getUuid() {
				return Uuid;
			}

			public void setUuid(String setUuid) {
				this.Uuid = setUuid;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getAccesskeyID() {
				return accesskeyID;
			}

			public void setAccesskeyID(String accesskeyID) {
				this.accesskeyID = accesskeyID;
			}

			public String getAccesskeyKey() {
				return accesskeyKey;
			}

			public void setAccesskeyKey(String accesskeyKey) {
				this.accesskeyKey = accesskeyKey;
			}

			public String getState() {
				return state;
			}

			public void setState(String state) {
				this.state = state;
			}

			public Timestamp getCreateData() {
				return createData;
			}

			public void setCreateData(Timestamp createData) {
				this.createData = createData;
			}

			public Timestamp getLastOpDate() {
				return lastOpDate;
			}

			public void setLastOpDate(Timestamp lastOpDate) {
				this.lastOpDate = lastOpDate;
			}

			@Override
			public String getVmInstanceUuid() {
				// TODO Auto-generated method stub
				return Uuid;
			}
			
}
