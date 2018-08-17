package com.noori.olivot.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RestController;



/**
 * HTTP servlet that handles incoming HTTP messages by directing them to the chatbot implementation.
 */
@RestController
public final class OlivotKeyboardServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(OlivotKeyboardServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException ,IOException {
    	logger.log(Level.INFO,"##### doGet Olivot message request content length : "+request.getContentLength());
    	logger.info("/keyboard (olivot)");
        JSONObject jobjBtn = new JSONObject();
        HashMap<String, Object> forJsonObj = new HashMap<String, Object>();
        forJsonObj.put("type", "text");
        jobjBtn = new JSONObject(forJsonObj);
        
        returnGetResponse(jobjBtn.toJSONString(), response);
    };

    private void returnGetResponse(String jsonString, HttpServletResponse response) {
		try {
			response.getWriter().write(jsonString);
			logger.info("##### return jsonString:"+jsonString);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
     
}
