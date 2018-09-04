package com.noori.olivot.service;

import com.microsoft.bot.connector.ConnectorClient;
import com.microsoft.bot.connector.Conversations;
import com.microsoft.bot.schema.models.Activity;
import com.noori.olivot.service.ChatBot;
import com.noori.olivot.service.MsBotImpl;
import com.noori.olivot.service.ConversationContextImpl;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class ChatBotImplTests {
    @Test
    public void handleSendReply() {
        ChatBot bot = new MsBotImpl();
        ConnectorClient connector = mock(ConnectorClient.class);
        Conversations conversations = mock(Conversations.class);

        when(connector.conversations()).thenReturn(conversations);

        bot.handle(new ConversationContextImpl(connector,
                TestActivityFactory.createMessageActivity("Test Message")));

        verify(conversations).sendToConversation(any(String.class), any(Activity.class));
    }
}
