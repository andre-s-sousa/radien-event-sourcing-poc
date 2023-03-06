package io.radien.tenantmanagement.handler;

import io.radien.tenantmanagement.domain.SystemTenant;
import io.radien.tenantmanagement.event.TenantEvent;
import java.util.UUID;

public record UpdateTenantClientCountry(UUID tenantId, String clientCountry, Long expectedVersion) {
    public static TenantEvent.TenantClientCountryUpdated handle(UpdateTenantClientCountry command, SystemTenant tenant) {
        if(!(tenant instanceof SystemTenant.ClientTenant)) {
            throw new IllegalStateException("Provided tenant is not of type Client");
        }
        if(tenant.isDeleted()) {
            throw new IllegalStateException("Tenant is already deleted");
        }
        return new TenantEvent.TenantClientCountryUpdated(command.tenantId(), command.clientCountry());
    }
}
