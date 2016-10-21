package org.zstack.ecs;

import org.zstack.header.errorcode.ErrorCode;

/**
 * Created by xing5 on 2016/4/19.
 */
public interface KvmCommandFailureChecker {
    ErrorCode getError(KvmResponseWrapper wrapper);
}
