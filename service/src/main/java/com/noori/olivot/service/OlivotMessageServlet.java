package com.noori.olivot.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

//import com.noori.common.OlivotUtil;
//import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
//import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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


@RestController
public final class OlivotMessageServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(OlivotMessageServlet.class.getName());

    private final ObjectMapper objectMapper;
    private final CredentialProvider credentialProvider;
    private final ServiceClientCredentials clientCredentials;
    private final ChatBot bot;
    private String requestStr;

    /**
     * Initializes new instance of {@link OlivotCustomServlet}
     */
    public OlivotMessageServlet() {
        this(new OlivotImpl());
    }

    public OlivotMessageServlet(ChatBot bot) {
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
    @RequestMapping(value = "/message", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
    //public void message(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject resObj) {
        try {
        	logger.info("##### /message doPost request content length : "+request.getContentLength());
        	request.setCharacterEncoding("UTF-8");
        	request.setAttribute("serviceUrl", "http://localhost:63651");
            String authorizationHeader = request.getHeader("Authorization");
            
            //StringBuffer sb = new StringBuffer();
            //Activity activity = deserializeActivity(request);
            BufferedReader reader = request.getReader();
            Activity activity = deserializeActivity(reader);
            JwtTokenValidation.assertValidActivity(activity, authorizationHeader, credentialProvider);
            ConnectorClient connectorInstance = new ConnectorClientImpl(activity.serviceUrl(), clientCredentials);
            ConversationContext context = new ConversationContextImpl(connectorInstance, activity);
            logger.info("##### context.activity.text : "+context.activity().text());
            //logger.info("##### message : "+resObj.get("content"));
            //logger.info("##### test message : "+request.getReader().lines().collect(Collectors.joining()));
            
            /*BufferedReader input = new BufferedReader(new InputStreamReader(request.getInputStream()));
    		StringBuilder builder = new StringBuilder();
    		String buffer;
    		while ((buffer = input.readLine()) != null) {
    			if (builder.length() > 0) {
    				builder.append("\n");
    			}
    			builder.append(buffer);
    		}*/
            //logger.info("##### message : "+resObj.get("content"));
            
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
     * @throws ParseException 
     */
//    private Activity deserializeActivity(HttpServletRequest request) throws IOException {
//    	String line = null;
//        BufferedReader reader = request.getReader();
//        BufferedReader readerRtr = new BufferedReader(reader);
//        logger.info("rb1-1 : "+reader.readLine());
//    	//Reader reader = request.getReader();
//        while ((line = reader.readLine()) != null) {
//        	sb.append(line);
//        }
//        logger.info("##### sb : "+sb.toString());
//        logger.info("rb1-2 : "+reader.readLine());
//        logger.info("rb2-2 : "+readerRtr.readLine());
//        //return objectMapper.readValue(request.getReader(), Activity.class);
//        return objectMapper.readValue(readerRtr, Activity.class);
//    }
//    private Activity deserializeActivity(HttpServletRequest request) throws IOException {
//        return objectMapper.readValue(request.getReader(), Activity.class);
//    }
    private Activity deserializeActivity(BufferedReader request) throws IOException, ParseException {
    	String line = null;
    	StringBuffer sb = new StringBuffer();
    	while ((line = request.readLine()) != null) {
        	sb.append(line);
        }
    	requestStr = sb.toString();
    	logger.info("##### requestStr : "+requestStr);
    	//requestStr.replaceAll("\"type\":\"text\"", "\"type\":\"message\"");
    	
    	//json형태로 바꿔서 "content":"나좀 도와줘.." 를 복사해서 "text":"나좀 도와줘.." 로 추가세팅
    	JSONParser parser = new JSONParser();
    	Object obj = parser.parse(requestStr);
    	JSONObject jsonObj = (JSONObject) obj;
    	jsonObj.put("type", "message");
    	jsonObj.put("text", jsonObj.get("content"));
    	requestStr = jsonObj.toJSONString();
    	logger.info("##### requestStr af setting : "+requestStr);
    	
    	InputStream is = new ByteArrayInputStream(requestStr.getBytes());
    	BufferedReader br = new BufferedReader(new InputStreamReader(is));

    	return objectMapper.readValue(br, Activity.class);
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
