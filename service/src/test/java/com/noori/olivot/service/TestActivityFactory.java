package com.noori.olivot.service;

import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;
import com.microsoft.bot.schema.models.ChannelAccount;
import com.microsoft.bot.schema.models.ConversationAccount;
import org.joda.time.DateTime;

import java.util.UUID;

public final class TestActivityFactory {
    private TestActivityFactory() {

    }

    public static Activity createMessageActivity(String text) {
        return new Activity()
                .withServiceUrl("http://localhost:9999/api/messages/")
                .withConversation(new ConversationAccount().withId(UUID.randomUUID().toString()))
                .withFrom(new ChannelAccount().withId(UUID.randomUUID().toString()))
                .withRecipient(new ChannelAccount().withId(UUID.randomUUID().toString()))
                .withText(text)
                .withTimestamp(new DateTime())
                .withChannelId(UUID.randomUUID().toString())
                .withType(ActivityTypes.MESSAGE);
    }
}
