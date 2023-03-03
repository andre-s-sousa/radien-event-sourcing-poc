package io.radien.eventsourcing.checkpoint;

import com.eventstore.dbclient.SubscribeToAllOptions;
import com.eventstore.dbclient.SubscriptionFilter;

public record EventStoreDBSubscriptionOptions(
        String subscriptionId,
        boolean ignoreDeserializationErrors,
        SubscribeToAllOptions subscribeToAllOptions
) {
    public static EventStoreDBSubscriptionOptions getDefault() {
        SubscriptionFilter filterOutSystemEvents = SubscriptionFilter.newBuilder()
                .withEventTypeRegularExpression("^[^\\$].*")
                .build();

        SubscribeToAllOptions options = SubscribeToAllOptions.get()
                .fromStart()
                .filter(filterOutSystemEvents);

        return new EventStoreDBSubscriptionOptions("default", true, options);
    }

    public static EventStoreDBSubscriptionOptions getByStreamPrefix(String prefix) {
        SubscriptionFilter filterOutSystemEvents = SubscriptionFilter.newBuilder()
                .addStreamNamePrefix(prefix)
                .build();

        SubscribeToAllOptions options = SubscribeToAllOptions.get()
                .fromStart()
                .filter(filterOutSystemEvents);

        return new EventStoreDBSubscriptionOptions("prefix-%s".formatted(prefix), true, options);
    }
}
