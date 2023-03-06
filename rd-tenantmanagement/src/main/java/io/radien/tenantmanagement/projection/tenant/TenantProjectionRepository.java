package io.radien.tenantmanagement.projection.tenant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.vertx.ConsumeEvent;
import io.radien.eventsourcing.core.EventEnvelope;
import io.radien.eventsourcing.core.EventEnvelopeWrapper;
import io.radien.projections.data.repository.Repository;
import io.radien.tenantmanagement.domain.TenantType;
import io.radien.tenantmanagement.event.TenantEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class TenantProjectionRepository extends Repository<Tenant, String> {
    private final Logger logger = LoggerFactory.getLogger(TenantProjectionRepository.class);

    @Inject
    EventBus eventBus;

    @ConsumeEvent(value = "root-tenant-created", ordered = true)
    Uni<Void> processRootTenantCreatedEvent(EventEnvelopeWrapper<EventEnvelope<TenantEvent.RootTenantCreated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    TenantEvent.RootTenantCreated event = new ObjectMapper().convertValue(msg.envelope().data(), TenantEvent.RootTenantCreated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onFailure().invoke(err -> logger.info("Error createing tenant projection", err))
                .onItem()
                .transformToUni(event ->
                        add(event, () ->
                                new Tenant(event.data().tenantId().toString(),
                                        event.data().tenantKey(), event.data().tenantName(),
                                        TenantType.ROOT, null, null, null, null, null, null, null,
                                        envelope.envelope().metadata().streamPosition(),
                                        envelope.envelope().metadata().logPosition()
                                )
                        ).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("Tenant Projection created %s".formatted(user.getTenantId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "client-tenant-created", ordered = true)
    Uni<Void> processClientTenantCreatedEvent(EventEnvelopeWrapper<EventEnvelope<TenantEvent.ClientTenantCreated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    TenantEvent.ClientTenantCreated event = new ObjectMapper().convertValue(msg.envelope().data(), TenantEvent.ClientTenantCreated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onFailure().invoke(err -> logger.info("Error createing tenant projection", err))
                .onItem()
                .transformToUni(event ->
                        add(event, () ->
                                new Tenant(event.data().tenantId().toString(),
                                        event.data().tenantKey(), event.data().tenantName(),
                                        TenantType.CLIENT,
                                        event.data().clientAddress(), event.data().clientZipCode(),
                                        event.data().clientCity(), event.data().clientCountry(),
                                        event.data().clientPhoneNumber(), event.data().clientEmail(),
                                        event.data().parentId().toString(),
                                        envelope.envelope().metadata().streamPosition(),
                                        envelope.envelope().metadata().logPosition()
                                )
                        ).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("Tenant Projection created %s".formatted(user.getTenantId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "sub-tenant-created", ordered = true)
    Uni<Void> processSubTenantCreatedEvent(EventEnvelopeWrapper<EventEnvelope<TenantEvent.SubTenantCreated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    TenantEvent.SubTenantCreated event = new ObjectMapper().convertValue(msg.envelope().data(), TenantEvent.SubTenantCreated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onFailure().invoke(err -> logger.info("Error createing tenant projection", err))
                .onItem()
                .transformToUni(event ->
                        add(event, () ->
                                new Tenant(event.data().tenantId().toString(),
                                        event.data().tenantKey(), event.data().tenantName(),
                                        TenantType.CLIENT,
                                        null, null, null, null, null, null,
                                        event.data().parentId().toString(),
                                        envelope.envelope().metadata().streamPosition(),
                                        envelope.envelope().metadata().logPosition()
                                )
                        ).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("Tenant Projection created %s".formatted(user.getTenantId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "tenant-name-updated", ordered = true)
    Uni<Void> processTenantNameUpdated(EventEnvelopeWrapper<EventEnvelope<TenantEvent.TenantNameUpdated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    TenantEvent.TenantNameUpdated event = new ObjectMapper().convertValue(msg.envelope().data(), TenantEvent.TenantNameUpdated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        getAndUpdate(
                                event.data().tenantId().toString(),
                                event,
                                view -> {
                                    view.setTenantName(event.data().tenantName());
                                    return view;
                                }).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("Tenant Projection updated %s".formatted(user.getTenantId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "tenant-key-updated", ordered = true)
    Uni<Void> processTenantKeyUpdated(EventEnvelopeWrapper<EventEnvelope<TenantEvent.TenantKeyUpdated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    TenantEvent.TenantKeyUpdated event = new ObjectMapper().convertValue(msg.envelope().data(), TenantEvent.TenantKeyUpdated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        getAndUpdate(
                                event.data().tenantId().toString(),
                                event,
                                view -> {
                                    view.setTenantKey(event.data().tenantKey());
                                    return view;
                                }).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("Tenant Projection updated %s".formatted(user.getTenantId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "tenant-client-address-updated", ordered = true)
    Uni<Void> processTenantClientAddressUpdated(EventEnvelopeWrapper<EventEnvelope<TenantEvent.TenantClientAddressUpdated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    TenantEvent.TenantClientAddressUpdated event = new ObjectMapper().convertValue(msg.envelope().data(), TenantEvent.TenantClientAddressUpdated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        getAndUpdate(
                                event.data().tenantId().toString(),
                                event,
                                view -> {
                                    view.setClientAddress(event.data().clientAddress());
                                    return view;
                                }).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("Tenant Projection updated %s".formatted(user.getTenantId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "tenant-client-zipcode-updated", ordered = true)
    Uni<Void> processTenantClientZipCodeUpdated(EventEnvelopeWrapper<EventEnvelope<TenantEvent.TenantClientZipCodeUpdated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    TenantEvent.TenantClientZipCodeUpdated event = new ObjectMapper().convertValue(msg.envelope().data(), TenantEvent.TenantClientZipCodeUpdated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        getAndUpdate(
                                event.data().tenantId().toString(),
                                event,
                                view -> {
                                    view.setClientZipCode(event.data().clientZipCode());
                                    return view;
                                }).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("Tenant Projection updated %s".formatted(user.getTenantId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "tenant-client-city-updated", ordered = true)
    Uni<Void> processTenantClientCityUpdated(EventEnvelopeWrapper<EventEnvelope<TenantEvent.TenantClientCityUpdated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    TenantEvent.TenantClientCityUpdated event = new ObjectMapper().convertValue(msg.envelope().data(), TenantEvent.TenantClientCityUpdated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        getAndUpdate(
                                event.data().tenantId().toString(),
                                event,
                                view -> {
                                    view.setClientCity(event.data().clientCity());
                                    return view;
                                }).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("Tenant Projection updated %s".formatted(user.getTenantId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "tenant-client-country-updated", ordered = true)
    Uni<Void> processTenantClientCountryUpdated(EventEnvelopeWrapper<EventEnvelope<TenantEvent.TenantClientCountryUpdated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    TenantEvent.TenantClientCountryUpdated event = new ObjectMapper().convertValue(msg.envelope().data(), TenantEvent.TenantClientCountryUpdated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        getAndUpdate(
                                event.data().tenantId().toString(),
                                event,
                                view -> {
                                    view.setClientCountry(event.data().clientCountry());
                                    return view;
                                }).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("Tenant Projection updated %s".formatted(user.getTenantId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "tenant-client-phonenumber-updated", ordered = true)
    Uni<Void> processTenantClientPhoneNumberUpdated(EventEnvelopeWrapper<EventEnvelope<TenantEvent.TenantClientPhoneNumberUpdated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    TenantEvent.TenantClientPhoneNumberUpdated event = new ObjectMapper().convertValue(msg.envelope().data(), TenantEvent.TenantClientPhoneNumberUpdated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        getAndUpdate(
                                event.data().tenantId().toString(),
                                event,
                                view -> {
                                    view.setClientPhoneNumber(event.data().clientPhoneNumber());
                                    return view;
                                }).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("Tenant Projection updated %s".formatted(user.getTenantId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "tenant-client-email-updated", ordered = true)
    Uni<Void> processTenantClientEmailUpdated(EventEnvelopeWrapper<EventEnvelope<TenantEvent.TenantClientEmailUpdated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    TenantEvent.TenantClientEmailUpdated event = new ObjectMapper().convertValue(msg.envelope().data(), TenantEvent.TenantClientEmailUpdated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        getAndUpdate(
                                event.data().tenantId().toString(),
                                event,
                                view -> {
                                    view.setClientEmail(event.data().clientEmail());
                                    return view;
                                }).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("Tenant Projection updated %s".formatted(user.getTenantId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "tenant-deleted", ordered = true)
    Uni<Void> processTenantDeleted(EventEnvelopeWrapper<EventEnvelope<TenantEvent.TenantDeleted>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    TenantEvent.TenantDeleted event = new ObjectMapper()
                            .registerModule(new JavaTimeModule())
                            .convertValue(msg.envelope().data(), TenantEvent.TenantDeleted.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        deleteById(event.data().tenantId().toString())
                )
                .onItem()
                .invoke(res -> eventBus.send("deleteSqsEvent", envelope.receiptHandle()))
                .onItem()
                .transformToUni(result -> flush());
    }
}
