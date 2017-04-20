package org.zstack.header.vm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.zstack.header.identity.PubAccountAO;

/**
 */
@Entity
@Table
public class PubVmInstanceEO extends PubVmInstanceAO {
    @Column
    private String deleted;

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
}
