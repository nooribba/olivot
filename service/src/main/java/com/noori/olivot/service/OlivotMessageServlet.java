package com.noori.olivot.service;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.aad.adal4j.AuthenticationException;

//import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;


/**
 * HTTP servlet that handles incoming HTTP messages by directing them to the chatbot implementation.
 */
@RestController
public final class OlivotMessageServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(OlivotMessageServlet.class.getName());
    
    /**
     * Handles HTTP POST requests
     *
     * @param request  Incoming HTTP request
     * @param response Outgoing HTTP response
     */
    @Override
    @RequestMapping(value = "/message", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
        	logger.log(Level.INFO,"##### doPost Olivot message request content length : "+request.getContentLength());

            String message = request.getReader().lines().collect(Collectors.joining());
            //bot : {"type":"message","text":"hi","from":{"id":"default-user","name":"User"},"locale":"ko","textFormat":"plain","timestamp":"2018-08-14T07:26:41.219Z","channelData":{"clientActivityId":"1534231500919.5028884381920204.2"},"channelId":"emulator","conversation":{"id":"27a8e870-9f93-11e8-8a34-b9d802d44081|livechat"},"id":"63717930-9f93-11e8-9c99-7b4d01c695af","localTimestamp":"2018-08-14T16:26:41+09:00","recipient":{"id":"1460feb0-9f93-11e8-9c99-7b4d01c695af","name":"Bot","role":"bot"},"serviceUrl":"http://localhost:63651"}
            //kakao : {"user_key":"cSsCbNZ7n1ba","type":"text","content":"하이하이"}
            logger.log(Level.INFO,"##### Olivot request message : "+message);
            
            String returnStr = "input message length is 0";
            if(message.length()>0) {
            	JSONParser parser = new JSONParser();
            	Object obj = parser.parse(message);
            	JSONObject jsonObj = (JSONObject) obj;
            	
            	message = (String) jsonObj.get("content");
            	String temp = (String) jsonObj.get("text");
            	logger.log(Level.INFO,"##### Olivot request message(text_bot) : "+temp);
            	logger.log(Level.INFO,"##### Olivot request message(content_kakao) : "+message);
            	
            	CharSequence contentNormalize = TwitterKoreanProcessorJava.normalize(message);
            	String normalizedMessage; 
            	normalizedMessage = contentNormalize.toString();
            	
            	JSONObject jobjRes = new JSONObject();
            	JSONObject jobjText = new JSONObject();
            	HashMap<String, Object> forJsonObj = new HashMap<String, Object>();
            	HashMap<String, Object> forJsonRes = new HashMap<String, Object>();
            	
            	// 사용자 구현
            	if(normalizedMessage.contains("안녕")){
            		forJsonObj.put("text","안녕 하세요");
            	} else if(normalizedMessage.contains("사랑")){
            		forJsonObj.put("text","나도 너무너무 사랑해");
            	} else if(normalizedMessage.contains("잘자")){
            		forJsonObj.put("text","굿밤!");
            	} else if(normalizedMessage.contains("졸려")){
            		forJsonObj.put("text","졸리면 언능 세수하러 가요!");
            	} else if(normalizedMessage.contains("시간")||normalizedMessage.contains("몇시")||normalizedMessage.contains("몇 시")){
            		forJsonObj.put("text","몹시 광분");
            	}else if((normalizedMessage.contains("너")||normalizedMessage.contains("넌"))&&normalizedMessage.contains("누구")){
            		forJsonObj.put("text","난 올리봇이야 ^0^");
            	}else if(normalizedMessage.contains("개새끼")){
            		forJsonObj.put("text","왈!왈!");
            	} else {
            		forJsonObj.put("text","학습중이라 아직 모르는게 많아요 ㅠ^ㅠ");
            	}
            	jobjText = new JSONObject(forJsonObj);
            	forJsonRes.put("message", jobjText);
            	jobjRes = new JSONObject(forJsonRes);
            	returnStr = jobjRes.toJSONString();
            }
            
            response.setStatus(202);
            response.setContentLength(0);
            
            logger.log(Level.INFO,"##### Response Message : "+returnStr);
			//response.getWriter().write(returnStr);
            //PrintWriter writer = response.getWriter();
            //writer.append(returnStr);
            //writer.print(returnStr);
            //writer.write(returnStr);
            //response.getWriter().append(returnStr);
            //response.getWriter().print(returnStr);
            response.getWriter().write(returnStr);
        } catch (AuthenticationException ex) {
            logger.log(Level.WARNING, "User is not authenticated", ex);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Failed to process incoming activity", ex);
        }
    }
    
    /*@ResponseBody
    private void returnPostResponse(String returnStr, HttpServletResponse response) {
    	logger.log(Level.INFO,"##### returnPostResponse");

        try {
        	logger.log(Level.INFO,"##### Response Message : "+returnStr);
			response.getWriter().write(returnStr);
		} catch (IOException e) {
			System.out.println(e.toString());
			logger.log(Level.SEVERE,e.toString());
		}
	}*/
    
}