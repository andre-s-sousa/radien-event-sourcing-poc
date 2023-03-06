package io.radien.tenantmanagement.handler.activetenant;

import io.radien.tenantmanagement.domain.SystemActiveTenant;
import io.radien.tenantmanagement.event.ActiveTenantEvent;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeleteActiveTenant(
        UUID activeTenantId,
        LocalDateTime terminationDate,
        Long expectedVersion
) {
    public static ActiveTenantEvent.ActiveTenantDeleted handle(DeleteActiveTenant command, SystemActiveTenant activeTenant) {
        if(activeTenant.isDeleted()) {
            throw new IllegalStateException("Active tenant already deleted");
        }
        return new ActiveTenantEvent.ActiveTenantDeleted(
                command.activeTenantId(), command.terminationDate()
        );
    }
}
