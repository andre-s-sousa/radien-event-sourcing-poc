package io.radien.eventsourcing.client;

import com.eventstore.dbclient.ConnectionStringParsingException;
import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.EventStoreDBClientSettings;
import com.eventstore.dbclient.EventStoreDBConnectionString;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EventStoreClientWrapper {
    private static final Logger logger = LoggerFactory.getLogger(EventStoreClientWrapper.class);

    @ConfigProperty(name = "io.radien.eventstore.db.connection_string")
    String connectionString;

    private EventStoreDBClient client;

    @PostConstruct
    public void getEventStoreDbClient() {
        try {
            EventStoreDBClientSettings settings = EventStoreDBConnectionString.parse(connectionString);

            client =  EventStoreDBClient.create(settings);
        } catch (ConnectionStringParsingException e) {
            //TODO: ex
            throw new RuntimeException(e);
        }
    }

    public EventStoreDBClient getClient() {
        return client;
    }
}
