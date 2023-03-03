package io.radien.usermanagement.command;

import java.time.LocalDateTime;
import java.util.UUID;

public final class UserCommands {
    public record Create(
            String firstName,
            String lastName,
            String userName,
            String userEmail
    ) {}

    public record Update(
            UUID userId,
            String firstName,
            String lastName,
            String userName,
            String userEmail,
            Long expectedVersion
    ) {}

    public record Delete(
            UUID userId,
            LocalDateTime terminationDate,
            Long expectedVersion
    ) {}
}
