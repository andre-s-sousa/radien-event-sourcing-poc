package io.radien.eventsourcing.core;

public record EventEnvelopeWrapper<T>(
        EventEnvelope<T> envelope,
        String receiptHandle
) { }
