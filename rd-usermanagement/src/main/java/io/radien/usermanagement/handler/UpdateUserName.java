package io.radien.usermanagement.handler;

import io.radien.usermanagement.domain.SystemUser;
import io.radien.usermanagement.event.UserEvent;
import java.util.UUID;

public record UpdateUserName(UUID userId, String userName, Long expectedVersion) {
    public static UserEvent.UserUserNameUpdated handle(UpdateUserName command, SystemUser user) {
        if(user.isDeleted()) {
            throw new IllegalStateException("User account is deleted");
        }
        return new UserEvent.UserUserNameUpdated(
                command.userId(), command.userName
        );
    }
}
