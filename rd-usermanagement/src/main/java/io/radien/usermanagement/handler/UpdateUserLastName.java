package io.radien.usermanagement.handler;

import io.radien.usermanagement.domain.SystemUser;
import io.radien.usermanagement.event.UserEvent;
import java.util.UUID;

public record UpdateUserLastName(UUID userId, String lastName, Long expectedVersion) {
    public static UserEvent.UserLastNameUpdated handle(UpdateUserLastName command, SystemUser user) {
        if(user.isDeleted()) {
            throw new IllegalStateException("User account is deleted");
        }
        return new UserEvent.UserLastNameUpdated(
                command.userId(), command.lastName()
        );
    }
}
