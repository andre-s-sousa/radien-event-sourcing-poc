package io.radien.usermanagement.datalayer;

import com.eventstore.dbclient.EventStoreDBClient;
import io.radien.eventsourcing.client.EventStoreClientWrapper;
import io.radien.eventsourcing.store.EntityStore;
import io.radien.usermanagement.domain.SystemUser;
import io.radien.usermanagement.event.UserEvent;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class UserEventEntityStore {

    @Inject
    EventStoreClientWrapper dbClient;

    @ApplicationScoped
    EntityStore<SystemUser, UserEvent> userEntityStore() {
        return new EntityStore<>(
                dbClient.getClient(),
                SystemUser::when,
                SystemUser::mapToStreamId,
                SystemUser::empty
        );
    }
}
