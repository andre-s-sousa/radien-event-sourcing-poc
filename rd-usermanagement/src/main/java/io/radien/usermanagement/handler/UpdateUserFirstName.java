package io.radien.usermanagement.handler;

import io.radien.usermanagement.domain.SystemUser;
import io.radien.usermanagement.event.UserEvent;
import java.util.UUID;

public record UpdateUserFirstName(UUID userId, String firstName, Long expectedVersion) {
    public static UserEvent.UserFirstNameUpdated handle(UpdateUserFirstName command, SystemUser user) {
        if(user.isDeleted()) {
            throw new IllegalStateException("User account is deleted");
        }
        return new UserEvent.UserFirstNameUpdated(
                command.userId(), command.firstName()
        );
    }
}
