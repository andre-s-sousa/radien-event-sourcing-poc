package io.radien.usermanagement.handler;

import io.radien.usermanagement.domain.SystemUser;
import io.radien.usermanagement.event.UserEvent;
import java.util.UUID;

public record UpdateUserEmail(UUID userId, String userEmail, Long expectedVersion) {
    public static UserEvent.UserUserEmailUpdated handle(UpdateUserEmail command, SystemUser user) {
        if(user.isDeleted()) {
            throw new IllegalStateException("User account is deleted");
        }
        return new UserEvent.UserUserEmailUpdated(
                command.userId(), command.userEmail()
        );
    }
}
