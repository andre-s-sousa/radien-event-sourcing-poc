package io.radien.eventsourcing.core;

public record EventMetadata(
        String eventId,
        long streamPosition,
        long logPosition,
        String eventType
) { }
