package io.radien.tenantmanagement.handler.tenant;

import io.radien.tenantmanagement.event.TenantEvent;
import java.util.UUID;

public record CreateSubTenant(
        UUID tenantId,
        String tenantKey,
        String tenantName,
        UUID parentId
        ) {
    public static TenantEvent.SubTenantCreated handle(CreateSubTenant command) {
        return new TenantEvent.SubTenantCreated(
                command.tenantId(),
                command.tenantKey(),
                command.tenantName(),
                command.parentId()
        );
    }
}
