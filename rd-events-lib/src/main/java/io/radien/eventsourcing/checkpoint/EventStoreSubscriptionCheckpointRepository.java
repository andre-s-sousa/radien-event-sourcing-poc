package io.radien.eventsourcing.checkpoint;

import com.eventstore.dbclient.AppendToStreamOptions;
import com.eventstore.dbclient.ExpectedRevision;
import com.eventstore.dbclient.ReadStreamOptions;
import com.eventstore.dbclient.StreamMetadata;
import com.eventstore.dbclient.StreamNotFoundException;
import com.eventstore.dbclient.WrongExpectedVersionException;
import io.radien.eventsourcing.client.EventStoreClientWrapper;
import io.radien.eventsourcing.core.CheckpointStored;
import io.radien.eventsourcing.utils.EventSerializer;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EventStoreSubscriptionCheckpointRepository implements EventCheckpointRepository {
    @Inject
    EventStoreClientWrapper eventStore;
    private final Logger logger = LoggerFactory.getLogger(EventStoreSubscriptionCheckpointRepository.class);

    public Optional<Long> load(String subscriptionId) {
        var streamName = getCheckpointStreamName(subscriptionId);

        var readOptions = ReadStreamOptions.get()
                .backwards()
                .fromEnd();

        try {
            return eventStore.getClient().readStream(streamName, readOptions)
                    .get()
                    .getEvents()
                    .stream()
                    .map(e -> EventSerializer.<CheckpointStored>deserialize(e).map(CheckpointStored::position))
                    .findFirst()
                    .orElse(Optional.empty());

        } catch (Exception e) {
            Throwable innerException = e.getCause();

            if (!(innerException instanceof StreamNotFoundException)) {
                logger.error("Failed to load checkpoint", e);
                //TODO: ex
                throw new RuntimeException(e);
            }
            return Optional.empty();
        }
    }

    public void store(String subscriptionId, long position) {
        var event = EventSerializer.serialize(
                new CheckpointStored(subscriptionId, position, LocalDateTime.now())
        );

        var streamName = getCheckpointStreamName(subscriptionId);

        try {
            // store new checkpoint expecting stream to exist
            eventStore.getClient().appendToStream(
                    streamName,
                    AppendToStreamOptions.get().expectedRevision(ExpectedRevision.streamExists()),
                    event
            ).get();
        } catch (Exception e) {
            if (!(e.getCause() instanceof WrongExpectedVersionException)) {
                //TODO: ex
                throw new RuntimeException(e);
            }

            // WrongExpectedVersionException means that stream did not exist
            // Set the checkpoint stream to have at most 1 event
            // using stream metadata $maxCount property

            var keepOnlyLastEvent = new StreamMetadata();
            keepOnlyLastEvent.setMaxCount(1);

            try {
                eventStore.getClient().setStreamMetadata(
                        streamName,
                        AppendToStreamOptions.get().expectedRevision(ExpectedRevision.noStream()),
                        keepOnlyLastEvent
                ).get();

                // append event again expecting stream to not exist
                eventStore.getClient().appendToStream(
                        streamName,
                        AppendToStreamOptions.get().expectedRevision(ExpectedRevision.noStream()),
                        event
                ).get();
            } catch (Exception exception) {
                //TODO: ex
                throw new RuntimeException(e);
            }
        }
    }

    private static String getCheckpointStreamName(String subscriptionId) {
        return "checkpoint_%s".formatted(subscriptionId);
    }
}
