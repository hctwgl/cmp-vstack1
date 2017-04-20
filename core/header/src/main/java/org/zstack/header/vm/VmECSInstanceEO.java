package org.zstack.header.vm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 */
@Entity
@Table
public class VmECSInstanceEO extends VmPubInstanceAO {
	
	@Column
    private String deleted;
	
	@Column
    private String ECSId;

    public String getECSId() {
		return ECSId;
	}

	public void setECSId(String eCSId) {
		ECSId = eCSId;
	}

	public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
    
}
