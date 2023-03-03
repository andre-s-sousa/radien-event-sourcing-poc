package io.radien.usermanagement.handler;

import io.radien.usermanagement.domain.SystemUser;
import io.radien.usermanagement.event.UserEvent;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeleteUserAccount(UUID userId, LocalDateTime terminationDate, Long expectedVersion) {
    public static UserEvent.UserAccountDeleted handle(DeleteUserAccount command, SystemUser user) {
        if(user.isDeleted()) {
            throw new IllegalStateException("User account is already deleted");
        }
        return new UserEvent.UserAccountDeleted(
                command.userId(), command.terminationDate()
        );
    }
}
