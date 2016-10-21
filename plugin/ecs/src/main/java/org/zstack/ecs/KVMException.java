package org.zstack.ecs;

public class KVMException extends Exception {
	public KVMException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public KVMException(Throwable t) {
		super(t);
	}
	
	public KVMException(String msg) {
		super(msg);
	}
}
