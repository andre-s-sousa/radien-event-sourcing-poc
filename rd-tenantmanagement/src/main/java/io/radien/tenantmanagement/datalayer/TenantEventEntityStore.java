package io.radien.tenantmanagement.datalayer;

import io.radien.eventsourcing.client.EventStoreClientWrapper;
import io.radien.eventsourcing.store.EntityStore;
import io.radien.tenantmanagement.domain.SystemTenant;
import io.radien.tenantmanagement.event.TenantEvent;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@ApplicationScoped
public class TenantEventEntityStore {
    @Inject
    EventStoreClientWrapper dbClient;

    @ApplicationScoped
    @Named("tenantEntityStore")
    EntityStore<SystemTenant, TenantEvent> userEntityStore() {
        return new EntityStore<>(
                dbClient.getClient(),
                SystemTenant::when,
                SystemTenant::mapToStreamId,
                SystemTenant::empty
        );
    }
}
