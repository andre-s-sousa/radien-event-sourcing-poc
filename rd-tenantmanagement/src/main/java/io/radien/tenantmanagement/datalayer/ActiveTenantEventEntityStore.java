package io.radien.tenantmanagement.datalayer;

import io.radien.eventsourcing.client.EventStoreClientWrapper;
import io.radien.eventsourcing.store.EntityStore;
import io.radien.tenantmanagement.domain.SystemActiveTenant;
import io.radien.tenantmanagement.domain.SystemTenant;
import io.radien.tenantmanagement.event.ActiveTenantEvent;
import io.radien.tenantmanagement.event.TenantEvent;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@ApplicationScoped
public class ActiveTenantEventEntityStore {
    @Inject
    EventStoreClientWrapper dbClient;

    @ApplicationScoped
    @Named("activeTenantEntityStore")
    EntityStore<SystemActiveTenant, ActiveTenantEvent> userEntityStore() {
        return new EntityStore<>(
                dbClient.getClient(),
                SystemActiveTenant::when,
                SystemActiveTenant::mapToStreamId,
                SystemActiveTenant::empty
        );
    }
}
