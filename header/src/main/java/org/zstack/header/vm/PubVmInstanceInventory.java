package org.zstack.header.vm;

import org.zstack.header.configuration.PythonClassInventory;
import org.zstack.header.search.Inventory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass =PubVmInstanceVO.class)
@PythonClassInventory
public class PubVmInstanceInventory {
	    private String uuid;
	    private String hostname;
	    private String description;
	    private String pubAccountUuid;
	    private String pubID;
		private String image;
	    private String cloudType;
	    private String cpuInfo;
	    private String memorySize;
	    private String otherInfo;
	    private String region;
	    private String state;
	    private Timestamp createDate;
	    private Timestamp lastOpDate;
	    
	    
    public static PubVmInstanceInventory valueOf(PubVmInstanceVO vo) {
    	PubVmInstanceInventory inv = new PubVmInstanceInventory();
        inv.setUuid(vo.getUuid());
        inv.setHostname(vo.getHostname());
        inv.setDescription(vo.getDescription());
        inv.setPubAccountUuid(vo.getPubAccountUuid());
        inv.setPubID(vo.getPubID());
        inv.setImage(vo.getImage());
        inv.setCloudType(vo.getCloudType());
        inv.setCpuInfo(vo.getCpuInfo());
        inv.setMemorySize(vo.getMemorySize());
        inv.setOtherInfo(vo.getOtherInfo());
        inv.setRegion(vo.getRegion());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setState(vo.getState());
        return inv;
    }
    
    public static List<PubVmInstanceInventory> valueOf(Collection<PubVmInstanceVO> vos) {
        List<PubVmInstanceInventory> lst = new ArrayList<PubVmInstanceInventory>(vos.size());
        for (PubVmInstanceVO vo : vos) {
            lst.add(PubVmInstanceInventory.valueOf(vo));
        }
        return lst;
    }
    
    
    public String getPubID() {
		return pubID;
	}

	public void setPubID(String pubID) {
		this.pubID = pubID;
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
