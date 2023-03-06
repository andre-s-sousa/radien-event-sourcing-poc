package io.radien.tenantmanagement.projection.activetenant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.vertx.ConsumeEvent;
import io.radien.eventsourcing.core.EventEnvelope;
import io.radien.eventsourcing.core.EventEnvelopeWrapper;
import io.radien.projections.data.repository.Repository;
import io.radien.tenantmanagement.domain.TenantType;
import io.radien.tenantmanagement.event.ActiveTenantEvent;
import io.radien.tenantmanagement.event.TenantEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ActiveTenantProjectionRepository extends Repository<ActiveTenant, String> {
    private final Logger logger = LoggerFactory.getLogger(ActiveTenantProjectionRepository.class);

    @Inject
    EventBus eventBus;

    @ConsumeEvent(value = "active-tenant-created", ordered = true)
    Uni<Void> processRootTenantCreatedEvent(EventEnvelopeWrapper<EventEnvelope<ActiveTenantEvent.ActiveTenantCreated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    ActiveTenantEvent.ActiveTenantCreated event = new ObjectMapper().convertValue(msg.envelope().data(), ActiveTenantEvent.ActiveTenantCreated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onFailure().invoke(err -> logger.info("Error createing tenant projection", err))
                .onItem()
                .transformToUni(event ->
                        add(event, () ->
                                new ActiveTenant(event.data().activeTenantId().toString(),
                                        event.data().tenantId().toString(),
                                        event.data().userId().toString(),
                                        envelope.envelope().metadata().streamPosition(),
                                        envelope.envelope().metadata().logPosition()
                                )
                        ).invoke(() -> eventBus.send("deleteSqsEvent_activeTenant", envelope.receiptHandle())))
                .invoke(user -> logger.info("Active Tenant Projection created %s".formatted(user.getActiveTenantId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "active-tenant-updated", ordered = true)
    Uni<Void> processTenantNameUpdated(EventEnvelopeWrapper<EventEnvelope<ActiveTenantEvent.ActiveTenantTenantUpdated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    ActiveTenantEvent.ActiveTenantTenantUpdated event = new ObjectMapper().convertValue(msg.envelope().data(), ActiveTenantEvent.ActiveTenantTenantUpdated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        getAndUpdate(
                                event.data().activeTenantId().toString(),
                                event,
                                view -> {
                                    view.setTenantId(event.data().tenantId().toString());
                                    return view;
                                }).invoke(() -> eventBus.send("deleteSqsEvent_activeTenant", envelope.receiptHandle())))
                .invoke(user -> logger.info("Tenant Projection updated %s".formatted(user.getTenantId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "active-tenant-deleted", ordered = true)
    Uni<Void> processTenantDeleted(EventEnvelopeWrapper<EventEnvelope<ActiveTenantEvent.ActiveTenantDeleted>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    ActiveTenantEvent.ActiveTenantDeleted event = new ObjectMapper()
                            .registerModule(new JavaTimeModule())
                            .convertValue(msg.envelope().data(), ActiveTenantEvent.ActiveTenantDeleted.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        deleteById(event.data().activeTenantId().toString())
                )
                .onItem()
                .invoke(res -> eventBus.send("deleteSqsEvent_activeTenant", envelope.receiptHandle()))
                .onItem()
                .transformToUni(result -> flush());
    }
}
