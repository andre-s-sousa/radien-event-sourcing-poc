package io.radien.eventsourcing.core;

import com.eventstore.dbclient.ResolvedEvent;
import io.radien.eventsourcing.utils.EventSerializer;
import java.util.Optional;

public record EventEnvelope<T>(
        T data,
        EventMetadata metadata
) {
    public static <T> Optional<EventEnvelope<T>> of(final Class<T> type, ResolvedEvent resolvedEvent) {
        if (type == null)
            return Optional.empty();

        Optional<T> eventData = EventSerializer.deserialize(type, resolvedEvent);

        return eventData.map(t -> new EventEnvelope<>(
                t,
                new EventMetadata(
                        resolvedEvent.getEvent().getEventId().toString(),
                        resolvedEvent.getEvent().getRevision(),
                        resolvedEvent.getEvent().getPosition().getCommitUnsigned(),
                        resolvedEvent.getEvent().getEventType()
                )
        ));

    }
}
