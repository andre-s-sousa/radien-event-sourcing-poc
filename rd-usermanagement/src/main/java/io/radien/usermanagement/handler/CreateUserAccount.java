package io.radien.usermanagement.handler;

import io.radien.usermanagement.event.UserEvent;
import java.util.UUID;

public record CreateUserAccount(
        UUID userId,
        String firstName,
        String lastName,
        String userName,
        String userEmail
) {
    public static UserEvent.UserCreatedEvent handle(CreateUserAccount command) {
        return new UserEvent.UserCreatedEvent(
                command.userId(),
                command.firstName(),
                command.lastName(),
                command.userName(),
                command.userEmail()
        );
    }
}
