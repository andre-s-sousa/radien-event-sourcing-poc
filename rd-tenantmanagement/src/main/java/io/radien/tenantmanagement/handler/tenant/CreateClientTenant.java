package io.radien.tenantmanagement.handler.tenant;

import io.radien.tenantmanagement.event.TenantEvent;
import java.util.UUID;

public record CreateClientTenant(
        UUID tenantId,
        String tenantKey,
        String tenantName,
        String clientAddress,
        String clientZipCode,
        String clientCity,
        String clientCountry,
        String clientPhoneNumber,
        String clientEmail,
        UUID parentId
) {
    public static TenantEvent.ClientTenantCreated handle(CreateClientTenant command) {
        return new TenantEvent.ClientTenantCreated(
                command.tenantId(),
                command.tenantKey(),
                command.tenantName(),
                command.clientAddress(),
                command.clientZipCode(),
                command.clientCity(),
                command.clientCountry(),
                command.clientPhoneNumber(),
                command.clientEmail(),
                command.parentId()
        );
    }
}
