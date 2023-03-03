package io.radien.projections.data.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.radien.eventsourcing.core.EventEnvelope;
import io.radien.projections.data.model.VersionedView;
import io.smallrye.mutiny.Uni;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Repository<View, Id> implements PanacheRepositoryBase<View, Id> {
    private static final Logger logger = LoggerFactory.getLogger(Repository.class);

    protected <Event> Uni<View> add(EventEnvelope<Event> eventEnvelope, Supplier<View> handle) {
        var result = handle.get();

        if(result instanceof VersionedView versionedView){
            versionedView.setMetadata(eventEnvelope.metadata());
        }
        return this.persistAndFlush(result);
    }

    protected <Event> Uni<View> getAndUpdate(
            Id viewId,
            EventEnvelope<Event> eventEnvelope,
            Function<View, View> handle
    ) {
        return this.findById(viewId)
                .ifNoItem().after(Duration.of(200, ChronoUnit.MILLIS)).fail()
                .onItem()
                .transformToUni(result -> {
                    if (result instanceof VersionedView versionedView && wasAlreadyApplied(versionedView, eventEnvelope)) {
                        logger.warn("View with id %s was already applied for event %s".formatted(viewId, eventEnvelope.metadata().eventType()));
                        return Uni.createFrom().item(result);
                    } else {
                        var appliedResult = handle.apply(result);
                        if(appliedResult instanceof VersionedView versionedView){
                            versionedView.setMetadata(eventEnvelope.metadata());
                        }
                        return this.persistAndFlush(appliedResult);
                    }
                });
    }

    private static boolean wasAlreadyApplied(VersionedView view, EventEnvelope<?> eventEnvelope) {
        return view.getLastProcessedPosition() >= eventEnvelope.metadata().logPosition();
    }
}
