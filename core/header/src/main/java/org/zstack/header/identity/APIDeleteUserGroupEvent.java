package org.zstack.header.identity;

import org.zstack.header.message.APIEvent;

/**
 * Created by frank on 7/9/2015.
 */
public class APIDeleteUserGroupEvent extends APIEvent {
    public APIDeleteUserGroupEvent() {
    }

    public APIDeleteUserGroupEvent(String apiId) {
        super(apiId);
    }
}
