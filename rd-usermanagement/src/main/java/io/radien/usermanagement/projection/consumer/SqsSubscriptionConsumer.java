package io.radien.usermanagement.projection.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.vertx.ConsumeEvent;
import io.radien.eventsourcing.core.EventEnvelope;
import io.radien.eventsourcing.core.EventEnvelopeWrapper;
import io.radien.eventsourcing.utils.EventTypeMapper;
import io.radien.usermanagement.event.UserEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@ApplicationScoped
public class SqsSubscriptionConsumer {
    private final Logger logger = LoggerFactory.getLogger(SqsSubscriptionConsumer.class);

    static final ObjectReader ENVELOPE_READER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .readerFor(EventEnvelope.class);

    @ConfigProperty(name = "sqs.consumer.maxFetchedMessages")
    int maxFetchedEvents;

    @ConfigProperty(name = "io.radien.sqs.queue.url")
    String queueUrl;
    @ConfigProperty(name = "io.radien.sqs.queue.wait_time", defaultValue = "10")
    Integer waitTime;

    @Inject
    EventBus eventBus;
    @Inject
    SqsAsyncClient client;

    @Scheduled(every = "{sqs.consumer.interval}")
    public void execute() {
        Uni.createFrom()
                .completionStage(fetchMessages(queueUrl))
                .subscribe()
                .with(
                        response -> {
                            int newEventsCount = response.messages().size();
                            if (newEventsCount > 0) {
                                logger.info("$newEventsCount message(s) fetched");
                                eventBus.send("sqsEvents", response);
                            }
                        },
                        err -> logger.error("Error fetching messages!", err)
                );
    }

    private CompletionStage<ReceiveMessageResponse> fetchMessages(String queueUrl)  {
        return client.receiveMessage(m -> m.queueUrl(queueUrl).maxNumberOfMessages(maxFetchedEvents).waitTimeSeconds(waitTime));
    }

    @ConsumeEvent(value = "sqsEvents", ordered = true)
    Uni<Void> processReceivedMessageResponse(ReceiveMessageResponse messageResponse) {
        return Uni.createFrom()
                .item(messageResponse)
                .map(messagesList -> messagesList.messages().stream().map(this::parseMessage))
                .invoke(messages -> messages.forEach(message -> {
                    Optional<Class> eventType = EventTypeMapper.toClass(message.envelope().metadata().eventType());
                    if(eventType.isPresent()) {
                        if(eventType.get() == UserEvent.UserCreatedEvent.class) {
                            eventBus.send("user-created", message);
                        } else if(eventType.get() == UserEvent.UserFirstNameUpdated.class) {
                            eventBus.send("user-firstname-updated", message);
                        } else if(eventType.get() == UserEvent.UserLastNameUpdated.class) {
                            eventBus.send("user-lastname-updated", message);
                        } else if(eventType.get() == UserEvent.UserUserNameUpdated.class) {
                            eventBus.send("user-name-updated", message);
                        } else if(eventType.get() == UserEvent.UserUserEmailUpdated.class) {
                            eventBus.send("user-email-updated", message);
                        } else if(eventType.get() == UserEvent.UserAccountDeleted.class) {
                            eventBus.send("user-deleted", message);
                        }
                    }
                }))
                .replaceWithVoid();
    }

    @ConsumeEvent(value = "deleteSqsEvent", ordered = true)
    Uni<Void> processDelete(String messageResponse) {
        return Uni.createFrom()
                .completionStage(client.deleteMessage(conf -> conf.queueUrl(queueUrl).receiptHandle(messageResponse)))
                .onItem().invoke(result -> logger.info("Message deleted %s".formatted(result.toString())))
                .replaceWithVoid();
    }


    EventEnvelopeWrapper<UserEvent> parseMessage(software.amazon.awssdk.services.sqs.model.Message msg) {
        try {
            return new EventEnvelopeWrapper<>(ENVELOPE_READER.readValue(msg.body()), msg.receiptHandle());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}