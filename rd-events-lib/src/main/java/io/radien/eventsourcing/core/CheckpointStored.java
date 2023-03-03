package io.radien.eventsourcing.core;

import java.time.LocalDateTime;

public record CheckpointStored(
        String subscriptionId,
        long position,
        LocalDateTime checkpointedAt
) {
}
