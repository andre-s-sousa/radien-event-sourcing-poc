package io.radien.tenantmanagement.handler;

import io.radien.tenantmanagement.domain.SystemTenant;
import io.radien.tenantmanagement.event.TenantEvent;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeleteTenant(UUID tenantId, LocalDateTime terminationDate, Long expectedVersion) {
    public static TenantEvent.TenantDeleted handle(DeleteTenant command, SystemTenant tenant) {
        if(tenant.isDeleted()) {
            throw new IllegalStateException("Tenant is already deleted");
        }
        return new TenantEvent.TenantDeleted(
                command.tenantId(), command.terminationDate()
        );
    }
}
