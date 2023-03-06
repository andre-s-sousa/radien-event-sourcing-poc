package io.radien.tenantmanagement.handler.tenant;

import io.radien.tenantmanagement.domain.SystemTenant;
import io.radien.tenantmanagement.event.TenantEvent;
import java.util.UUID;

public record UpdateTenantKey(UUID tenantId, String tenantKey, Long expectedVersion) {
    public static TenantEvent.TenantKeyUpdated handle(UpdateTenantKey command, SystemTenant tenant) {
        if(tenant.isDeleted()) {
            throw new IllegalStateException("Tenant is already deleted");
        }
        return new TenantEvent.TenantKeyUpdated(command.tenantId(), command.tenantKey());
    }
}
