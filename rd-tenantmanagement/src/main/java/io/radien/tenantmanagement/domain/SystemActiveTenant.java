package io.radien.tenantmanagement.domain;

import io.radien.tenantmanagement.event.ActiveTenantEvent;
import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface SystemActiveTenant {
    UUID id();
    UUID tenantId();
    UUID userId();

    record ActiveTenant(
            UUID id,
            UUID tenantId,
            UUID userId
    ) implements SystemActiveTenant {}

    record DeletedActiveTenant(
            UUID id,
            UUID tenantId,
            UUID userId,
            LocalDateTime terminationDate
    ) implements SystemActiveTenant {}

    default boolean isDeleted() { return this instanceof DeletedActiveTenant; }

    static String mapToStreamId(UUID activeTenantId) { return "ActiveTenant-%s".formatted(activeTenantId.toString()); }

    static SystemActiveTenant empty() { return new ActiveTenant(null, null, null); }

    static SystemActiveTenant when(SystemActiveTenant current, ActiveTenantEvent event) {
        if(event instanceof ActiveTenantEvent.ActiveTenantCreated activeTenantCreated) {
            return new ActiveTenant(
                    activeTenantCreated.activeTenantId(),
                    activeTenantCreated.tenantId(),
                    activeTenantCreated.userId()
            );
        } else if(event instanceof ActiveTenantEvent.ActiveTenantTenantUpdated tenantUpdatedEvent) {
            return new ActiveTenant(
                    current.id(),
                    tenantUpdatedEvent.tenantId(),
                    current.userId()
            );
        } else if(event instanceof ActiveTenantEvent.ActiveTenantDeleted deletedActiveTenant) {
            return new DeletedActiveTenant(
                    current.id(),
                    current.tenantId(),
                    current.userId(),
                    deletedActiveTenant.terminationDate()
            );
        }
        //TODO: ex
        throw new RuntimeException("Invalid event provided");
    }

}
