package io.radien.tenantmanagement.handler;

import io.radien.tenantmanagement.domain.SystemTenant;
import io.radien.tenantmanagement.event.TenantEvent;
import java.util.UUID;

public record UpdateTenantClientZipCode(UUID tenantId, String clientZipCode, Long expectedVersion) {
    public static TenantEvent.TenantClientZipCodeUpdated handle(UpdateTenantClientZipCode command, SystemTenant tenant) {
        if(!(tenant instanceof SystemTenant.ClientTenant)) {
            throw new IllegalStateException("Provided tenant is not of type Client");
        }
        if(tenant.isDeleted()) {
            throw new IllegalStateException("Tenant is already deleted");
        }
        return new TenantEvent.TenantClientZipCodeUpdated(command.tenantId(), command.clientZipCode());
    }
}
