package com.noori.olivot.service;

import com.microsoft.bot.connector.ConnectorClient;
import com.microsoft.bot.connector.Conversations;
import com.microsoft.bot.schema.models.Activity;
import com.noori.olivot.service.ActivityFactory;
import com.noori.olivot.service.ConversationContext;
import com.noori.olivot.service.ConversationContextImpl;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class ConversationContextImplTests {
    @Test
    public void sendActivityShouldInvokeConnectorClient() {
        ConnectorClient connector = mock(ConnectorClient.class);
        Conversations conversations = mock(Conversations.class);

        when(connector.conversations()).thenReturn(conversations);

        Activity testMessage = TestActivityFactory.createMessageActivity("Test message");
        ConversationContext context = new ConversationContextImpl(connector, testMessage);

        context.sendActivity(ActivityFactory.createReply(testMessage, "Test"));

        verify(conversations).sendToConversation(any(String.class), any(Activity.class));
    }
}
