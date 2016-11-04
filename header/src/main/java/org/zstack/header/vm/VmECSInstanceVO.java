package org.zstack.header.vm;

import org.zstack.header.tag.AutoDeleteTag;
import org.zstack.header.vo.EO;
import org.zstack.header.vo.NoView;
import org.zstack.header.volume.VolumeVO;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@Inheritance(strategy=InheritanceType.JOINED)
@EO(EOClazz = VmPubInstanceEO.class)
@AutoDeleteTag
public class VmECSInstanceVO  extends VmPubInstanceAO{
     
	public VmECSInstanceVO() {
    }

     
}
