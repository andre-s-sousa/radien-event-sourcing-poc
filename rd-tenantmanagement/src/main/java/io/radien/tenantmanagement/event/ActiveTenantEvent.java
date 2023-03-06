package io.radien.tenantmanagement.event;

import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface ActiveTenantEvent {
    record ActiveTenantCreated(
            UUID activeTenantId,
            UUID tenantId,
            UUID userId
    ) implements ActiveTenantEvent {}

    record ActiveTenantTenantUpdated(
            UUID activeTenantId,
            UUID tenantId
    ) implements ActiveTenantEvent {}

    record ActiveTenantDeleted(
            UUID activeTenantId,
            LocalDateTime terminationDate
    ) implements ActiveTenantEvent {}
}
