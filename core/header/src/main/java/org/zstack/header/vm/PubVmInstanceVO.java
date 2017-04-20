package org.zstack.header.vm;

import org.zstack.header.tag.AutoDeleteTag;
import org.zstack.header.vo.EO;
import javax.persistence.*;

@Entity
@Table
@EO(EOClazz = PubVmInstanceEO.class)
@AutoDeleteTag
public class PubVmInstanceVO extends PubVmInstanceAO{

}
