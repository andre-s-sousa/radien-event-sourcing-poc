package io.radien.tenantmanagement.handler.tenant;

import io.radien.tenantmanagement.event.TenantEvent;
import java.util.UUID;

public record CreateRootTenant(
        UUID tenantId,
        String tenantKey,
        String tenantName
) {
    public static TenantEvent.RootTenantCreated handle(CreateRootTenant command) {
        return new TenantEvent.RootTenantCreated(
                command.tenantId(),
                command.tenantKey(),
                command.tenantName()
        );
    }
}
