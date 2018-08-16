package com.noori.olivot.service;

import com.microsoft.bot.connector.ConnectorClient;
import com.microsoft.bot.schema.models.Activity;

/**
 * Conversation context provides information about the context, the current acitvity and some tools
 * to send activities to the user.
 */
public interface ConversationContext {
    /**
     * Gets the outgoing bot connection
     *
     * @return The bot connector client
     */
    ConnectorClient connector();

    /**
     * Gets the current incoming activity
     *
     * @return An instance of {@link Activity} for the incoming chat data
     */
    Activity activity();

    /**
     * Sends an activity
     *
     * @param activity Activity to send
     */
    void sendActivity(Activity activity);
}
