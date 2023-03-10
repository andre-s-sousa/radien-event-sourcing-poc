package io.radien.tenantmanagement.handler.tenant;

import io.radien.tenantmanagement.domain.SystemTenant;
import io.radien.tenantmanagement.event.TenantEvent;
import java.util.UUID;

public record UpdateTenantClientAddress(UUID tenantId, String clientCity, Long expectedVersion) {
    public static TenantEvent.TenantClientCityUpdated handle(UpdateTenantClientAddress command, SystemTenant tenant) {
        if(!(tenant instanceof SystemTenant.ClientTenant)) {
            throw new IllegalStateException("Provided tenant is not of type Client");
        }
        if(tenant.isDeleted()) {
            throw new IllegalStateException("Tenant is already deleted");
        }
        return new TenantEvent.TenantClientCityUpdated(command.tenantId(), command.clientCity());
    }
}
