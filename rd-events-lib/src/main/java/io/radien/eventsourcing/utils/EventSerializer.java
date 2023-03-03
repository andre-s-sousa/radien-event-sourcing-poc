package io.radien.eventsourcing.utils;

import com.eventstore.dbclient.EventData;
import com.eventstore.dbclient.EventDataBuilder;
import com.eventstore.dbclient.ResolvedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSerializer {
    private static final Logger logger = LoggerFactory.getLogger(EventSerializer.class);
    public static final ObjectMapper mapper =
            new JsonMapper()
                    .registerModule(new JavaTimeModule())
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static EventData serialize(Object event) {
        try {
            return EventDataBuilder.json(
                    UUID.randomUUID(),
                    EventTypeMapper.toName(event.getClass()),
                    mapper.writeValueAsBytes(event)
            ).build();
        } catch (JsonProcessingException e) {
            //TODO: ex
            throw new RuntimeException(e);
        }
    }

    public static <T> Optional<T> deserialize(ResolvedEvent resolvedEvent) {
        var eventClass = EventTypeMapper.toClass(resolvedEvent.getEvent().getEventType());

        if (eventClass.isEmpty())
            return Optional.empty();

        return deserialize(eventClass.get(), resolvedEvent);
    }

    public static <T> Optional<T> deserialize(Class<T> eventClass, ResolvedEvent resolvedEvent) {
        try {
            T result = mapper.readValue(resolvedEvent.getEvent().getEventData(), eventClass);
            return Optional.ofNullable(result);
        } catch (IOException e) {
            logger.warn("Error deserializing event %s".formatted(resolvedEvent.getEvent().getEventType()), e);
            return Optional.empty();
        }
    }
}
