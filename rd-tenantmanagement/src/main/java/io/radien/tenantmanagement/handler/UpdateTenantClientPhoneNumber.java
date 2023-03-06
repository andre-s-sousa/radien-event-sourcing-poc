package io.radien.tenantmanagement.handler;

import io.radien.tenantmanagement.domain.SystemTenant;
import io.radien.tenantmanagement.event.TenantEvent;
import java.util.UUID;

public record UpdateTenantClientPhoneNumber(UUID tenantId, String clientPhoneNumber, Long expectedVersion) {
    public static TenantEvent.TenantClientPhoneNumberUpdated handle(UpdateTenantClientPhoneNumber command, SystemTenant tenant) {
        if(!(tenant instanceof SystemTenant.ClientTenant)) {
            throw new IllegalStateException("Provided tenant is not of type Client");
        }
        if(tenant.isDeleted()) {
            throw new IllegalStateException("Tenant is already deleted");
        }
        return new TenantEvent.TenantClientPhoneNumberUpdated(command.tenantId(), command.clientPhoneNumber());
    }
}
