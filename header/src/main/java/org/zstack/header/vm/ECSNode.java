package org.zstack.header.vm;

import org.zstack.header.tag.AutoDeleteTag;
import org.zstack.header.vo.EO;
import javax.persistence.*;
 
public class ECSNode extends PubVmInstanceAO{
	
	private String id;
	private String name;
	private String state;
	private String public_ips;
	private String private_ips;
	private String driver;
	private String size;
	private String created_at;
	private String image;
	private String extra;
	
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPublic_ips() {
		return public_ips;
	}
	public void setPublic_ips(String public_ips) {
		this.public_ips = public_ips;
	}
	public String getPrivate_ips() {
		return private_ips;
	}
	public void setPrivate_ips(String private_ips) {
		this.private_ips = private_ips;
	}
	
}
