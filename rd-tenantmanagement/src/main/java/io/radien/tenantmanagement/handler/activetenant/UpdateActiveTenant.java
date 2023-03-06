package io.radien.tenantmanagement.handler.activetenant;

import io.radien.tenantmanagement.domain.SystemActiveTenant;
import io.radien.tenantmanagement.event.ActiveTenantEvent;
import java.util.UUID;

public record UpdateActiveTenant(
        UUID activeTenantId,
        UUID tenantId,
        Long expectedVersion
) {
    public static ActiveTenantEvent.ActiveTenantTenantUpdated handle(UpdateActiveTenant command, SystemActiveTenant activeTenant) {
        if(activeTenant.isDeleted()) {
            throw new IllegalStateException("Active Tenant already deleted");
        }
        return new ActiveTenantEvent.ActiveTenantTenantUpdated(
                command.activeTenantId(), command.tenantId()
        );
    }
}
