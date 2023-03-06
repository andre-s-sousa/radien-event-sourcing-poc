package io.radien.tenantmanagement.service;

import io.radien.eventsourcing.core.ETag;
import io.radien.eventsourcing.store.EntityStore;
import io.radien.tenantmanagement.domain.SystemTenant;
import io.radien.tenantmanagement.event.TenantEvent;
import io.radien.tenantmanagement.handler.CreateClientTenant;
import io.radien.tenantmanagement.handler.CreateRootTenant;
import io.radien.tenantmanagement.handler.CreateSubTenant;
import io.radien.tenantmanagement.handler.DeleteTenant;
import io.radien.tenantmanagement.handler.UpdateTenantClientAddress;
import io.radien.tenantmanagement.handler.UpdateTenantClientCity;
import io.radien.tenantmanagement.handler.UpdateTenantClientCountry;
import io.radien.tenantmanagement.handler.UpdateTenantClientEmail;
import io.radien.tenantmanagement.handler.UpdateTenantClientPhoneNumber;
import io.radien.tenantmanagement.handler.UpdateTenantClientZipCode;
import io.radien.tenantmanagement.handler.UpdateTenantKey;
import io.radien.tenantmanagement.handler.UpdateTenantName;
import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class TenantCommandBusinessService {
    @Inject
    EntityStore<SystemTenant, TenantEvent> store;

    public ETag createTenant(CreateRootTenant command) {
        return store.add(
                () -> CreateRootTenant.handle(command),
                command.tenantId()
        );
    }

    public ETag createTenant(CreateClientTenant command) {
        Optional<SystemTenant> parent = store.get(command.parentId());
        if(parent.isPresent() && !parent.get().isDeleted()) {
            return store.add(
                    () -> CreateClientTenant.handle(command),
                    command.tenantId()
            );
        }
        throw new IllegalStateException("Unable to validate parent tenant");
    }

    public ETag createTenant(CreateSubTenant command) {
        Optional<SystemTenant> parent = store.get(command.parentId());
        if(parent.isPresent() && !parent.get().isDeleted()) {
            return store.add(
                    () -> CreateSubTenant.handle(command),
                    command.tenantId()
            );
        }
        throw new IllegalStateException("Unable to validate parent tenant");
    }

    public ETag updateTenant(UpdateTenantKey command) {
        return store.getAndUpdate(
                current -> UpdateTenantKey.handle(command, current),
                command.tenantId(),
                command.expectedVersion()
        );
    }

    public ETag updateTenant(UpdateTenantName command) {
        return store.getAndUpdate(
                current -> UpdateTenantName.handle(command, current),
                command.tenantId(),
                command.expectedVersion()
        );
    }

    public ETag updateTenant(UpdateTenantClientAddress command) {
        return store.getAndUpdate(
                current -> UpdateTenantClientAddress.handle(command, current),
                command.tenantId(),
                command.expectedVersion()
        );
    }

    public ETag updateTenant(UpdateTenantClientCity command) {
        return store.getAndUpdate(
                current -> UpdateTenantClientCity.handle(command, current),
                command.tenantId(),
                command.expectedVersion()
        );
    }

    public ETag updateTenant(UpdateTenantClientCountry command) {
        return store.getAndUpdate(
                current -> UpdateTenantClientCountry.handle(command, current),
                command.tenantId(),
                command.expectedVersion()
        );
    }

    public ETag updateTenant(UpdateTenantClientEmail command) {
        return store.getAndUpdate(
                current -> UpdateTenantClientEmail.handle(command, current),
                command.tenantId(),
                command.expectedVersion()
        );
    }

    public ETag updateTenant(UpdateTenantClientPhoneNumber command) {
        return store.getAndUpdate(
                current -> UpdateTenantClientPhoneNumber.handle(command, current),
                command.tenantId(),
                command.expectedVersion()
        );
    }

    public ETag updateTenant(UpdateTenantClientZipCode command) {
        return store.getAndUpdate(
                current -> UpdateTenantClientZipCode.handle(command, current),
                command.tenantId(),
                command.expectedVersion()
        );
    }

    public ETag deleteTenant(DeleteTenant command) {
        return store.getAndUpdate(
                current -> DeleteTenant.handle(command, current),
                command.tenantId(),
                command.expectedVersion()
        );
    }
}
