package io.radien.tenantmanagement.command;

import io.radien.tenantmanagement.domain.TenantType;
import java.time.LocalDateTime;
import java.util.UUID;

public final class TenantCommands {
    public record CreateRoot(
            String tenantKey,
            String tenantName
    ) {}

    public record CreateClient(
            String tenantKey,
            String tenantName,
            String clientAddress,
            String clientZipCode,
            String clientCity,
            String clientCountry,
            String clientPhoneNumber,
            String clientEmail,
            UUID parentId
    ) {}

    public record CreateSub(
            String tenantKey,
            String tenantName,
            UUID parentId
    ) {}

    public record Update(
            UUID id,
            String tenantKey,
            String tenantName,
            String clientAddress,
            String clientZipCode,
            String clientCity,
            String clientCountry,
            String clientPhoneNumber,
            String clientEmail,
            UUID parentId,
            Long expectedVersion
    ) {}

    public record Delete(
            UUID id,
            LocalDateTime terminationDate,
            Long expectedVersion
    ) {}
}
