package io.radien.tenantmanagement.handler;

import io.radien.tenantmanagement.domain.SystemTenant;
import io.radien.tenantmanagement.event.TenantEvent;
import java.util.UUID;

public record UpdateTenantName(UUID tenantId, String tenantName, Long expectedVersion) {
    public static TenantEvent.TenantNameUpdated handle(UpdateTenantName command, SystemTenant tenant) {
        if(tenant.isDeleted()) {
            throw new IllegalStateException("Tenant is already deleted");
        }
        return new TenantEvent.TenantNameUpdated(command.tenantId(), command.tenantName());
    }
}
