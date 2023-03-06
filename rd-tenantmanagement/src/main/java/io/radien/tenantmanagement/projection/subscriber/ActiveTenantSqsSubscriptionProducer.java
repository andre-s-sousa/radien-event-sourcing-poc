package io.radien.tenantmanagement.projection.subscriber;

import com.eventstore.dbclient.Position;
import com.eventstore.dbclient.ResolvedEvent;
import com.eventstore.dbclient.Subscription;
import com.eventstore.dbclient.SubscriptionListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.runtime.Startup;
import io.radien.eventsourcing.checkpoint.EventCheckpointRepository;
import io.radien.eventsourcing.checkpoint.EventStoreDBSubscriptionOptions;
import io.radien.eventsourcing.client.EventStoreClientWrapper;
import io.radien.eventsourcing.core.CheckpointStored;
import io.radien.eventsourcing.core.EventEnvelope;
import io.radien.eventsourcing.utils.EventTypeMapper;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;

@ApplicationScoped
@Startup
public class ActiveTenantSqsSubscriptionProducer {
    private static final Logger logger = LoggerFactory.getLogger(ActiveTenantSqsSubscriptionProducer.class);

    @Inject
    SqsClient sqs;
    @Inject
    EventStoreClientWrapper eventStoreClient;
    @Inject
    EventCheckpointRepository checkpointRepository;

    @ConfigProperty(name = "io.radien.sqs.queue.url.activeTenant")
    String queueUrl;

    private EventStoreDBSubscriptionOptions subscriptionOptions;
    private Subscription subscription;
    private boolean isRunning;

    static final ObjectWriter ENVELOPE_WRITER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .writerFor(EventEnvelope.class);

    private final SubscriptionListener listener = new SubscriptionListener() {
        @Override
        public void onEvent(Subscription subscription, ResolvedEvent event) {
            try {
                sendMessage(event);
            } catch(Exception e) {
                throw new RuntimeException("Error while handling event", e);
            }
        }

        @Override
        public void onError(Subscription subscription, Throwable throwable) {
            throw new RuntimeException("Subscription was dropped", throwable);
        }
    };

    @PostConstruct
    public void subscribeToAll() {
        subscribeToAll(EventStoreDBSubscriptionOptions.getByStreamPrefix("ActiveTenant"));
    }

    @PreDestroy
    public void stop() {
        if(isRunning) {
            isRunning = false;
            subscription.stop();
        }
    }

    void subscribeToAll(EventStoreDBSubscriptionOptions subscriptionOptions) {
        this.subscriptionOptions = subscriptionOptions;
        try {
            Optional<Long> checkpoint = checkpointRepository.load(subscriptionOptions.subscriptionId());

            if(checkpoint.isPresent()) {
                subscriptionOptions.subscribeToAllOptions().fromPosition(new Position(checkpoint.get(), checkpoint.get()));
            } else {
                subscriptionOptions.subscribeToAllOptions().fromStart();
            }

            logger.info("Subscribing to all '%s'".formatted(subscriptionOptions.subscriptionId()));
            subscription = eventStoreClient.getClient().subscribeToAll(
                    listener,
                    subscriptionOptions.subscribeToAllOptions()
            ).get();
        } catch(InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while starting subscription", e);
        }
    }

    public void sendMessage(ResolvedEvent event) {
        if(isEventWithEmptyData(event) || isCheckpointEvent(event)) {
            return;
        }

        Optional<Class> eventClass = EventTypeMapper.toClass(event.getEvent().getEventType());
        Optional<EventEnvelope<?>> streamEvent = eventClass.flatMap(c -> EventEnvelope.of(c, event));

        if (streamEvent.isEmpty()) {
            // That can happen if we're sharing database between modules.
            // If we're subscribing to all and not filtering out events from other modules,
            // then we might get events that are from other module, and we might not be able to deserialize them.
            // In that case it's safe to ignore deserialization error.
            // You may add more sophisticated logic checking if it should be ignored or not.
            logger.warn("Couldn't deserialize event with id: %s %s".formatted(event.getEvent().getEventId(), event.getEvent().getEventType()));

            if (!subscriptionOptions.ignoreDeserializationErrors())
                throw new IllegalStateException(
                        "Unable to deserialize event %s with id: %s"
                                .formatted(event.getEvent().getEventType(), event.getEvent().getEventId())
                );

            return;
        }

        sqs.sendMessage(m -> {
            try {
                m.queueUrl(queueUrl)
                        .messageBody(ENVELOPE_WRITER.writeValueAsString(streamEvent.get()));
            } catch (JsonProcessingException e) {
                //TODO: ex
                throw new RuntimeException(e);
            }
        });

        checkpointRepository.store(
                this.subscriptionOptions.subscriptionId(),
                event.getEvent().getPosition().getCommitUnsigned()
        );
    }

    private boolean isEventWithEmptyData(ResolvedEvent resolvedEvent) {
        if (resolvedEvent.getEvent().getEventData().length != 0) return false;

        logger.info("Event without data received");
        return true;
    }

    private boolean isCheckpointEvent(ResolvedEvent resolvedEvent) {
        if (!resolvedEvent.getEvent().getEventType().equals(EventTypeMapper.toName(CheckpointStored.class)))
            return false;

        logger.info("Checkpoint event - ignoring");
        return true;
    }
}
