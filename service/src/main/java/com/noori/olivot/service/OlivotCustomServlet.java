package com.noori.olivot.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

//import com.noori.common.OlivotUtil;
//import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
//import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.aad.adal4j.AuthenticationException;
import com.microsoft.bot.connector.ConnectorClient;
import com.microsoft.bot.connector.customizations.CredentialProvider;
import com.microsoft.bot.connector.customizations.CredentialProviderImpl;
import com.microsoft.bot.connector.customizations.JwtTokenValidation;
import com.microsoft.bot.connector.customizations.MicrosoftAppCredentials;
import com.microsoft.bot.connector.implementation.ConnectorClientImpl;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.rest.credentials.ServiceClientCredentials;


/**
 * HTTP servlet that handles incoming HTTP messages by directing them to the chatbot implementation.
 */
public final class OlivotCustomServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(OlivotCustomServlet.class.getName());

    private final ObjectMapper objectMapper;
    private final CredentialProvider credentialProvider;
    private final ServiceClientCredentials clientCredentials;
    private final ChatBot bot;

    /**
     * Initializes new instance of {@link OlivotCustomServlet}
     */
    public OlivotCustomServlet() {
        this(new OlivotImpl());
    }

    public OlivotCustomServlet(ChatBot bot) {
        this.credentialProvider = new CredentialProviderImpl(getAppId(), getKey());
        this.objectMapper = ObjectMapperFactory.createObjectMapper();
        this.clientCredentials = new MicrosoftAppCredentials(getAppId(), getKey());
        this.bot = bot;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        bot.init(config.getServletContext());
    }

    /**
     * Handles HTTP POST requests
     *
     * @param request  Incoming HTTP request
     * @param response Outgoing HTTP response
     */
    @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
        	logger.info("##### doPost request content length : "+request.getContentLength());
        	request.setCharacterEncoding("UTF-8");
            String authorizationHeader = request.getHeader("Authorization");
            Activity activity = deserializeActivity(request);

            // Make sure that the request has a proper authorization header
            // If not, this raises an authentication exception.
            JwtTokenValidation.assertValidActivity(activity, authorizationHeader, credentialProvider);

            // The outgoing messages are not sent as a reply to the incoming HTTP request.
            // Instead you create a separate channel for them.
            ConnectorClient connectorInstance = new ConnectorClientImpl(activity.serviceUrl(), clientCredentials);
            ConversationContext context = new ConversationContextImpl(connectorInstance, activity);
            logger.info("##### context.activity.text : "+context.activity().text());
            
            //bot.handle(context);
            String predictMsg = bot.olivotHandle(context);
            
            JSONObject jobjRes = new JSONObject();
        	JSONObject jobjText = new JSONObject();
        	HashMap<String, Object> forJsonObj = new HashMap<String, Object>();
        	HashMap<String, Object> forJsonRes = new HashMap<String, Object>();
        	String returnStr = "input message length is 0";
            forJsonObj.put("text",predictMsg);
        	jobjText = new JSONObject(forJsonObj);
        	forJsonRes.put("message", jobjText);
        	jobjRes = new JSONObject(forJsonRes);
        	returnStr = jobjRes.toJSONString();

            // Always send a HTTP 202 notifying the bot framework channel that we've handled the incoming request.
            /*response.setStatus(202);
            response.setContentLength(0);*/
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            
            logger.log(Level.INFO,"##### Response Message : "+returnStr);
            response.getWriter().write(returnStr);
        } catch (AuthenticationException ex) {
            logger.log(Level.WARNING, "User is not authenticated", ex);
            writeJsonResponse(response, 401, new ApplicationError("Unauthenticated request"));
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Failed to process incoming activity", ex);
            writeJsonResponse(response, 500, new ApplicationError("Failed to process request"));
        }
    }

    /**
     * Writes a JSON response
     *
     * @param response   Response object to write to
     * @param statusCode Status code for the request
     * @param value      Value to write
     */
    private void writeJsonResponse(HttpServletResponse response, int statusCode, Object value) {
        response.setContentType("application/json");
        response.setStatus(statusCode);

        try (PrintWriter writer = response.getWriter()) {
            objectMapper.writeValue(writer, value);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to serialize object to output stream", ex);
        }
    }

    /**
     * Deserializes the request body to a chatbot activity
     *
     * @param request Request object to read from
     * @return Returns the deserialized request
     * @throws IOException Gets thrown when the activity could not be deserialized
     */
    private Activity deserializeActivity(HttpServletRequest request) throws IOException {
        return objectMapper.readValue(request.getReader(), Activity.class);
    }

    /**
     * Gets the bot app ID
     *
     * @return The bot app ID
     */
    private String getAppId() {
        String appId = System.getenv("BOT_APPID");

        if (appId == null) {
            return "";
        }

        return appId;
    }

    /**
     * Gets the bot password
     *
     * @return The bot password
     */
    private String getKey() {
        String key = System.getenv("BOT_KEY");

        if (key == null) {
            return "";
        }

        return key;
    }
    
}
