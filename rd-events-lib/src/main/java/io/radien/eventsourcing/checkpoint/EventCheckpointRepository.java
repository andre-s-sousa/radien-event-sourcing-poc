package io.radien.eventsourcing.checkpoint;

import java.util.Optional;

public interface EventCheckpointRepository {
    Optional<Long> load(String subscriptionId);

    void store(String subscriptionId, long position);
}
