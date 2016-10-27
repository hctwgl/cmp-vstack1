package org.zstack.header.vm;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VmPubInstanceType {
    private static Map<String, VmPubInstanceType> types = Collections.synchronizedMap(new HashMap<String, VmPubInstanceType>());
    private final String typeName;
    
    public VmPubInstanceType(String typeName) {
        this.typeName = typeName;
        types.put(typeName, this);
    }
    
    public static VmPubInstanceType valueOf(String typeName) {
        VmPubInstanceType type = types.get(typeName);
        if (type == null) {
            throw new IllegalArgumentException("VmInstanceType type: " + typeName + " was not registered by any VmInstanceFactory");
        }
        return type;
    }
    
    @Override
    public String toString() {
        return typeName;
    }
    
    @Override
    public boolean equals(Object t) {
        if (t == null || !(t instanceof VmPubInstanceType)) {
            return false;
        }
        
        VmPubInstanceType type = (VmPubInstanceType)t;
        return type.toString().equals(typeName);
    }
    
    @Override
    public int hashCode() {
        return typeName.hashCode();
    }
}
