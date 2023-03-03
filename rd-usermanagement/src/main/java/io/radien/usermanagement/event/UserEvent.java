package io.radien.usermanagement.event;

import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface UserEvent {
    record UserCreatedEvent(
            UUID userId,
            String firstName,
            String lastName,
            String userName,
            String userEmail
    ) implements UserEvent {}

    record UserFirstNameUpdated(
            UUID userId,
            String firstName
    ) implements UserEvent {}

    record UserLastNameUpdated(
            UUID userId,
            String lastName
    ) implements UserEvent {}

    record UserUserNameUpdated(
            UUID userId,
            String userName
    ) implements UserEvent {}

    record UserUserEmailUpdated(
            UUID userId,
            String userEmail
    ) implements UserEvent {}

    record UserAccountDeleted(
            UUID userId,
            LocalDateTime teminationDate
    ) implements UserEvent {}
}
