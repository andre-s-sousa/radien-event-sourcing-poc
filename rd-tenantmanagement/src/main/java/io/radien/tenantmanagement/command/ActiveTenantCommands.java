package io.radien.tenantmanagement.command;

import java.time.LocalDateTime;
import java.util.UUID;

public final class ActiveTenantCommands {
    public record Create(
            UUID tenantId,
            UUID userId
    ) {}

    public record Update(
            UUID id,
            UUID tenantId,
            Long expectedVersion
    ) {}

    public record Delete(
            UUID id,
            LocalDateTime terminationDate,
            Long expectedVersion
    ) {}
}
