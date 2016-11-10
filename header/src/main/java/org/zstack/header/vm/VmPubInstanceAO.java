package org.zstack.header.vm;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

/**
 */
@MappedSuperclass
public class VmPubInstanceAO  {
	@Id
    @Column
	private String uuid;
	
	@Column
	     private String name;
	@Column
	     private String accesskeyID;
	@Column
	     private String accesskeyKey;
	@Column
	     private String state;
	@Column
	     private Timestamp createDate;
	
	@Column
	     private Timestamp lastOpDate;
	
	
	
	
	 public VmPubInstanceAO() {
	    }

	    public VmPubInstanceAO(VmPubInstanceAO other) {
	        this.uuid = other.uuid;
	        this.name = other.name;
	        this.accesskeyID = other.accesskeyID;
	        this.accesskeyKey = other.accesskeyKey;
	        this.state = other.state;
	        this.createDate = other.createDate;
	        this.lastOpDate = other.lastOpDate;
	        
	    }

	
	
	

	    public String getUuid() {
			return uuid;
		}

		public void setUuid(String setUuid) {
			this.uuid = setUuid;
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

		public Timestamp getCreateDate() {
			return createDate;
		}

		public void setCreateDate(Timestamp createDate) {
			this.createDate = createDate;
		}

		public Timestamp getLastOpDate() {
			return lastOpDate;
		}

		public void setLastOpDate(Timestamp lastOpDate) {
			this.lastOpDate = lastOpDate;
		}
		
		
		

}
