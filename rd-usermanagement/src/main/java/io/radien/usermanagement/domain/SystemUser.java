package io.radien.usermanagement.domain;

import io.radien.usermanagement.event.UserEvent;
import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface SystemUser {
    UUID id();
    String firstName();
    String lastName();
    String userName();
    String userEmail();

    record ActiveUserAccount(
            UUID id,
            String firstName,
            String lastName,
            String userName,
            String userEmail
    ) implements SystemUser {}

    record DeletedUserAccount(
            UUID id,
            String firstName,
            String lastName,
            String userName,
            String userEmail,
            LocalDateTime terminationDate
    ) implements SystemUser {}

    default boolean isDeleted() {
        return this instanceof DeletedUserAccount;
    }

    static String mapToStreamId(UUID userId) {
        return "User-%s".formatted(userId.toString());
    }

    static SystemUser empty() {
        return new ActiveUserAccount(null, null, null, null, null);
    }

    static SystemUser when(SystemUser current, UserEvent event) {
        if (event instanceof UserEvent.UserCreatedEvent createUserEvent) {
            return new ActiveUserAccount(
                    createUserEvent.userId(),
                    createUserEvent.firstName(),
                    createUserEvent.lastName(),
                    createUserEvent.userName(),
                    createUserEvent.userEmail()
            );
        } else if (event instanceof UserEvent.UserFirstNameUpdated firstNameUpdated) {
            return new ActiveUserAccount(
                    current.id(),
                    firstNameUpdated.firstName(),
                    current.lastName(),
                    current.userName(),
                    current.userEmail()
            );
        } else if(event instanceof UserEvent.UserLastNameUpdated lastNameUpdated) {
            return new ActiveUserAccount(
                    current.id(),
                    current.firstName(),
                    lastNameUpdated.lastName(),
                    current.userName(),
                    current.userEmail()
            );
        } else if(event instanceof UserEvent.UserUserNameUpdated userNameUpdated) {
            return new ActiveUserAccount(
                    current.id(),
                    current.firstName(),
                    current.lastName(),
                    userNameUpdated.userName(),
                    current.userEmail()
            );
        } else if(event instanceof UserEvent.UserUserEmailUpdated userEmailUpdated) {
            return new ActiveUserAccount(
                    current.id(),
                    current.firstName(),
                    current.lastName(),
                    current.userName(),
                    userEmailUpdated.userEmail()
            );
        } else if(event instanceof UserEvent.UserAccountDeleted userAccountDeleted) {
            return new DeletedUserAccount(
                    current.id(),
                    current.firstName(),
                    current.lastName(),
                    current.userName(),
                    current.userEmail(),
                    userAccountDeleted.teminationDate()
            );
        }
        //TODO: ex
        throw new RuntimeException("Invalid event provided");
    }
}