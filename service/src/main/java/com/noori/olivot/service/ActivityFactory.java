package com.noori.olivot.service;

import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;
import org.joda.time.DateTime;

/**
 * Factory class to produce various activities
 */
public final class ActivityFactory {
    /**
     * Creates a new instance of {@link ActivityFactory}
     */
    private ActivityFactory() {

    }

    /**
     * Creates a reply for an activity
     *
     * @param activity Activity to create a reply for
     * @param text     Text for the reply
     * @return Returns the new activity
     */
    public static Activity createReply(Activity activity, String text) {
        Activity reply = new Activity();

        reply.withFrom(activity.recipient())
                .withRecipient(activity.from())
                .withConversation(activity.conversation())
                .withChannelId(activity.channelId())
                .withReplyToId(activity.id())
                .withServiceUrl(activity.serviceUrl())
                .withTimestamp(new DateTime())
                .withType(ActivityTypes.MESSAGE);

        if (text != null && !text.isEmpty()) {
            reply.withText(text);
        }

        return reply;
    }


}
