package com.noori.olivot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;
import com.microsoft.bot.schema.models.ChannelAccount;
import com.microsoft.bot.schema.models.ConversationAccount;
import com.noori.olivot.service.ChatBot;
import com.noori.olivot.service.ChatBotServlet;
import com.noori.olivot.service.ConversationContext;
import com.noori.olivot.service.ObjectMapperFactory;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class ChatBotServletTests {
    private ChatBot bot;
    private ChatBotServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    public void beforeTest() {
        response = mock(HttpServletResponse.class);
        request = mock(HttpServletRequest.class);

        bot = mock(ChatBot.class);
        servlet = new ChatBotServlet(bot);
    }

    @Test
    public void shouldReturnAcceptedStatus() throws Exception {
        when(request.getReader()).thenReturn(stubbedRequestStream(
                TestActivityFactory.createMessageActivity("Test message")));

        servlet.doPost(request, response);

        verify(response).setStatus(202);
    }

    @Test
    public void shouldInvokeBotInstance() throws Exception {
        when(request.getReader()).thenReturn(stubbedRequestStream(
                TestActivityFactory.createMessageActivity("Test message")));

        servlet.doPost(request, response);

        verify(bot).handle(any(ConversationContext.class));
    }

    private BufferedReader stubbedRequestStream(Activity activity) throws Exception {
        ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();

        String serializedValue = mapper.writeValueAsString(activity);
        InputStream stream = new ByteArrayInputStream(serializedValue.getBytes());

        return new BufferedReader(new InputStreamReader(stream));
    }


}
