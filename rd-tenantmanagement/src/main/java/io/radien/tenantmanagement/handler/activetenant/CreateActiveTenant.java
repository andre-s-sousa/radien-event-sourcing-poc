package io.radien.tenantmanagement.handler.activetenant;

import io.radien.tenantmanagement.event.ActiveTenantEvent;
import java.util.UUID;

public record CreateActiveTenant(
        UUID activeTenantId,
        UUID tenantId,
        UUID userid
) {
    public static ActiveTenantEvent.ActiveTenantCreated handle(CreateActiveTenant command) {
        return new ActiveTenantEvent.ActiveTenantCreated(
                command.activeTenantId(),
                command.tenantId(), command.userid()
        );
    }
}
