package io.radien.eventsourcing.store;

import com.eventstore.dbclient.AppendToStreamOptions;
import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.ExpectedRevision;
import com.eventstore.dbclient.ReadResult;
import com.eventstore.dbclient.ReadStreamOptions;
import com.eventstore.dbclient.StreamNotFoundException;
import io.radien.eventsourcing.core.ETag;
import io.radien.eventsourcing.utils.EventSerializer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class EntityStore<T, E> {
    private final EventStoreDBClient eventStore;
    private final BiFunction<T, E, T> when;
    private final Function<UUID, String> mapToStreamId;
    private final Supplier<T> getDefault;

    public EntityStore(
            EventStoreDBClient eventStore,
            BiFunction<T, E, T> when,
            Function<UUID, String> mapToStreamId,
            Supplier<T> getDefault
    ) {

        this.eventStore = eventStore;
        this.when = when;
        this.mapToStreamId = mapToStreamId;
        this.getDefault = getDefault;
    }

    public Optional<T> get(UUID id) {
        var streamId = mapToStreamId.apply(id);

        var events = getEvents(streamId);

        if (events.isEmpty())
            return Optional.empty();

        var current = getDefault.get();

        for (var event : events.get()) {
            current = when.apply(current, event);
        }

        return Optional.of(current);
    }

    public ETag add(
            Supplier<Object> handle,
            UUID id
    ) {
        var streamId = mapToStreamId.apply(id);
        var event = handle.get();

        try {
            var result =
                    eventStore.appendToStream(
                            streamId,
                            AppendToStreamOptions.get().expectedRevision(ExpectedRevision.noStream()),
                            EventSerializer.serialize(event)
                    ).get();

            return ETag.weak(result.getNextExpectedRevision());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public ETag getAndUpdate(
            Function<T, E> handle,
            UUID id,
            long expectedRevision
    ) {

        var streamId = mapToStreamId.apply(id);
        var entity = get(id).orElseThrow(
                //TODO: ex
                () -> new RuntimeException("Stream with id %s was not found".formatted(streamId))
        );

        var event = handle.apply(entity);

        try {
            var result = eventStore.appendToStream(
                    streamId,
                    AppendToStreamOptions.get().expectedRevision(expectedRevision),
                    EventSerializer.serialize(event)
            ).get();

            return ETag.weak(result.getNextExpectedRevision());
        } catch (Exception e) {
            //TODO: ex
            throw new RuntimeException(e);
        }
    }

    private Optional<List<E>> getEvents(String streamId) {
        ReadResult result;
        ReadStreamOptions options = ReadStreamOptions.get().fromStart().forwards();
        try {
            result = eventStore.readStream(streamId, options).get();
        } catch (Exception e) {
            //TODO: ex
            Throwable innerException = e.getCause();

            if (innerException instanceof StreamNotFoundException) {
                return Optional.empty();
            }
            throw new RuntimeException(e);
        }

        var events = result.getEvents().stream()
                .map(EventSerializer::<E>deserialize)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        return Optional.of(events);
    }
}
