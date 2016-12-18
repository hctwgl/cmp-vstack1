package org.zstack.header.vm;

import org.zstack.header.vo.Index;
import javax.persistence.*;
import java.sql.Timestamp;

@MappedSuperclass
public class PubVmInstanceAO {
    @Id
    @Column
    private String uuid;
    
    @Column
    private String hostname;
    
	@Column
    private String description;
    
    @Column
    private String pubAccountUuid;
    
    @Column
    private String image;
 
   
	@Column
    private String cloudType;
    
    @Column
    private String cpuInfo;


    @Column
    private String memorySize;

    @Column
    private String otherInfo;

    @Column
    private String region;

    @Column
    private Timestamp createDate;
    
    @Column
    private Timestamp lastOpDate;
    
    @Column
    private String state;
    
    public PubVmInstanceAO() {
    }

    public PubVmInstanceAO(PubVmInstanceAO other) {
        this.uuid = other.uuid;
        this.hostname = other.hostname;
        this.region = other.region;
        this.pubAccountUuid = other.pubAccountUuid;
        this.description = other.description;
        this.image = other.image;
        this.cloudType = other.cloudType;
        this.otherInfo = other.otherInfo;
        this.cpuInfo = other.cpuInfo;
        this.memorySize = other.memorySize;
        this.createDate = other.createDate;
        this.lastOpDate = other.lastOpDate;
        this.state = other.state;
    }

    
    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	
	 public String getHostname() {
			return hostname;
		}

		public void setHostname(String hostname) {
			this.hostname = hostname;
		}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	 
	 

	public String getPubAccountUuid() {
		return pubAccountUuid;
	}

	public void setPubAccountUuid(String pubAccountUuid) {
		this.pubAccountUuid = pubAccountUuid;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	 
	public String getCloudType() {
		return cloudType;
	}

	public void setCloudType(String cloudType) {
		this.cloudType = cloudType;
	}

	public String getCpuInfo() {
		return cpuInfo;
	}

	public void setCpuInfo(String cpuInfo) {
		this.cpuInfo = cpuInfo;
	}

	 

	public String getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(String memorySize) {
		this.memorySize = memorySize;
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
 

   
}
