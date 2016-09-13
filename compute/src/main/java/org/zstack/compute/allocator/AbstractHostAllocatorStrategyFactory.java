package org.zstack.compute.allocator;

import org.zstack.header.Component;
import org.zstack.header.allocator.*;

import java.util.List;

public abstract class AbstractHostAllocatorStrategyFactory implements HostAllocatorStrategyFactory, Component {
    private HostAllocatorChainBuilder builder;
    private List<String> allocatorFlowNames;
	
	@Override
	public HostAllocatorStrategy getHostAllocatorStrategy() {
        return builder.build();
	}

    public abstract HostAllocatorStrategyType getHostAllocatorStrategyType();

    public void setAllocatorFlowNames(List<String> allocatorFlowNames) {
        this.allocatorFlowNames = allocatorFlowNames;
    }

    public List<String> getAllocatorFlowNames() {
        return allocatorFlowNames;
    }

    @Override
    public boolean start() {
        builder = HostAllocatorChainBuilder.newBuilder().setFlowClassNames(allocatorFlowNames).construct();
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    public void marshalSpec(HostAllocatorSpec spec, AllocateHostMsg msg) {
    }
}
