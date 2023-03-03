package io.radien.projections.data.model;

import io.radien.eventsourcing.core.EventMetadata;

public interface VersionedView {
    long getLastProcessedPosition();

    void setMetadata(EventMetadata eventMetadata);
}
