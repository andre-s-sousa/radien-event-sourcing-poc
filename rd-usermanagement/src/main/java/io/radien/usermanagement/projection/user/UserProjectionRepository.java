package io.radien.usermanagement.projection.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.vertx.ConsumeEvent;
import io.radien.eventsourcing.core.EventEnvelope;
import io.radien.eventsourcing.core.EventEnvelopeWrapper;
import io.radien.projections.data.repository.Repository;
import io.radien.usermanagement.event.UserEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserProjectionRepository extends Repository<User, String> {
    private final Logger logger = LoggerFactory.getLogger(UserProjectionRepository.class);

    @Inject
    EventBus eventBus;

    @ConsumeEvent(value = "user-created", ordered = true)
    Uni<Void> processUserCreatedEvent(EventEnvelopeWrapper<EventEnvelope<UserEvent.UserCreatedEvent>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    UserEvent.UserCreatedEvent event = new ObjectMapper().convertValue(msg.envelope().data(), UserEvent.UserCreatedEvent.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onFailure().invoke(err -> logger.info("Error updating user projection", err))
                .onItem()
                .transformToUni(event ->
                        add(event, () ->
                                new User(event.data().userId().toString(),
                                        event.data().firstName(),
                                        event.data().lastName(),
                                        event.data().userName(),
                                        event.data().userEmail(),
                                        envelope.envelope().metadata().streamPosition(),
                                        envelope.envelope().metadata().logPosition()
                                )
                        ).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("User Projection updated %s".formatted(user.getUserId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "user-firstname-updated", ordered = true)
    Uni<Void> processFirstNameUpdated(EventEnvelopeWrapper<EventEnvelope<UserEvent.UserFirstNameUpdated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    UserEvent.UserFirstNameUpdated event = new ObjectMapper().convertValue(msg.envelope().data(), UserEvent.UserFirstNameUpdated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        getAndUpdate(
                                event.data().userId().toString(),
                                event,
                                view -> {
                                    view.setFirstName(event.data().firstName());
                                    return view;
                                }).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("User Projection updated %s".formatted(user.getUserId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "user-lastname-updated", ordered = true)
    Uni<Void> processLastNameUpdated(EventEnvelopeWrapper<EventEnvelope<UserEvent.UserLastNameUpdated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    UserEvent.UserLastNameUpdated event = new ObjectMapper().convertValue(msg.envelope().data(), UserEvent.UserLastNameUpdated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        getAndUpdate(
                                event.data().userId().toString(),
                                event,
                                view -> {
                                    view.setLastName(event.data().lastName());
                                    return view;
                                }).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("User Projection updated %s".formatted(user.getUserId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "user-name-updated", ordered = true)
    Uni<Void> processUserNameUpdated(EventEnvelopeWrapper<EventEnvelope<UserEvent.UserUserNameUpdated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    UserEvent.UserUserNameUpdated event = new ObjectMapper().convertValue(msg.envelope().data(), UserEvent.UserUserNameUpdated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        getAndUpdate(
                                event.data().userId().toString(),
                                event,
                                view -> {
                                    view.setUserName(event.data().userName());
                                    return view;
                                }).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("User Projection updated %s".formatted(user.getUserId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "user-email-updated", ordered = true)
    Uni<Void> processUserEmailUpdated(EventEnvelopeWrapper<EventEnvelope<UserEvent.UserUserEmailUpdated>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    UserEvent.UserUserEmailUpdated event = new ObjectMapper().convertValue(msg.envelope().data(), UserEvent.UserUserEmailUpdated.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        getAndUpdate(
                                event.data().userId().toString(),
                                event,
                                view -> {
                                    view.setUserEmail(event.data().userEmail());
                                    return view;
                                }).invoke(() -> eventBus.send("deleteSqsEvent", envelope.receiptHandle())))
                .invoke(user -> logger.info("User Projection updated %s".formatted(user.getUserId())))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "user-deleted", ordered = true)
    Uni<Void> processUserDeleted(EventEnvelopeWrapper<EventEnvelope<UserEvent.UserAccountDeleted>> envelope) {
        return Uni.createFrom()
                .item(envelope)
                .map(msg -> {
                    UserEvent.UserAccountDeleted event = new ObjectMapper()
                            .registerModule(new JavaTimeModule())
                            .convertValue(msg.envelope().data(), UserEvent.UserAccountDeleted.class);
                    return new EventEnvelope<>(event, msg.envelope().metadata());
                })
                .onItem()
                .transformToUni(event ->
                        deleteById(event.data().userId().toString())
                )
                .onItem()
                .invoke(res -> eventBus.send("deleteSqsEvent", envelope.receiptHandle()))
                .onItem()
                .transformToUni(result -> flush());
    }
}
