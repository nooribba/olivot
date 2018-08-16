package com.noori.olivot.service;

import com.microsoft.bot.connector.ConnectorClient;
import com.microsoft.bot.connector.models.ErrorResponseException;
import com.microsoft.bot.schema.models.Activity;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the conversation context
 */
public final class ConversationContextImpl implements ConversationContext {
    private static Logger logger = Logger.getLogger(ConversationContextImpl.class.getName());

    private final ConnectorClient connector;
    private final Activity activity;

    /**
     * Initializes a new instance of {@link ConversationContextImpl}
     * @param connector Connector client to use
     * @param activity  Incoming activity
     */
    public ConversationContextImpl(ConnectorClient connector, Activity activity) {
        this.connector = connector;
        this.activity = activity;
    }

    /**
     * Gets the outgoing bot connection
     *
     * @return The bot connector client
     */
    @Override
    public ConnectorClient connector() {
        return connector;
    }

    /**
     * Gets the current incoming activity
     *
     * @return An instance of {@link Activity} for the incoming chat data
     */
    @Override
    public Activity activity() {
        return activity;
    }

    /**
     * Sends an activity
     *
     * @param activity Activity to send
     */
    @Override
    public void sendActivity(Activity activity) {
        try {
            connector.conversations().sendToConversation(activity.conversation().id(), activity);
        } catch (ErrorResponseException ex) {
            logger.log(Level.SEVERE, "Failed to deliver activity to channel", ex);
        }
    }
}
