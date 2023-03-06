package io.radien.tenantmanagement.service;

import io.radien.eventsourcing.core.ETag;
import io.radien.eventsourcing.store.EntityStore;
import io.radien.tenantmanagement.domain.SystemActiveTenant;
import io.radien.tenantmanagement.event.ActiveTenantEvent;
import io.radien.tenantmanagement.handler.activetenant.CreateActiveTenant;
import io.radien.tenantmanagement.handler.activetenant.DeleteActiveTenant;
import io.radien.tenantmanagement.handler.activetenant.UpdateActiveTenant;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@RequestScoped
public class ActiveTenantCommandBusinessService {
    @Inject
    @Named("activeTenantEntityStore")
    EntityStore<SystemActiveTenant, ActiveTenantEvent> store;

    public ETag createActiveTenant(CreateActiveTenant command) {
        return store.add(
                () -> CreateActiveTenant.handle(command),
                command.activeTenantId()
        );
    }

    public ETag updateActiveTenant(UpdateActiveTenant command) {
        return store.getAndUpdate(
                current -> UpdateActiveTenant.handle(command, current),
                command.activeTenantId(),
                command.expectedVersion()
        );
    }

    public ETag deleteActiveTenant(DeleteActiveTenant command) {
        return store.getAndUpdate(
                current -> DeleteActiveTenant.handle(command, current),
                command.activeTenantId(),
                command.expectedVersion()
        );
    }
}
