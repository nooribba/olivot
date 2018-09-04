package com.noori.olivot.service;

import javax.servlet.ServletContext;

/**
 * Defines the structure of a chatbot
 */
public interface ChatBot {
    /**
     * Handles incoming chatbot activities
     *
     * @param context Conversation context for the current incoming activity
     */
    void handle(ConversationContext context);

    /**
     * Initializes the bot
     *
     * @param context Servlet context in which the bot is used
     */
    void init(ServletContext context);
    
    String olivotHandle(ConversationContext context);
}
