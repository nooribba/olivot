package com.noori.controller;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.noori.common.OlivotUtil;


/**
 * noori
 */
@RestController
public class OlivotController {
	private String userDicPath = null;
	private String modelsFullPath = null;
	private String modelsLightPath = null;
	private String osName = null;
	
    // 키보드
	@RequestMapping(value = "/keyboard", method = RequestMethod.GET)
    public String keyboard() {

        System.out.println("/keyboard");
        JSONObject jobjBtn = new JSONObject();
        HashMap<String, Object> forJsonObj = new HashMap<String, Object>();
        forJsonObj.put("type", "text");
        jobjBtn = new JSONObject(forJsonObj);
        
        if(osName == null){
        	osName = OlivotUtil.getOsName();
			System.out.println("##### osName:"+osName);
       }
       if(userDicPath == null || modelsFullPath == null || modelsLightPath == null){
    	   if(osName.contains("window")){
    		   userDicPath = "user_data"+File.separator;
			   modelsFullPath = "models_full";
			   modelsLightPath = "models_light";
    	   }else{
    		   userDicPath = "WEB-INF"+File.separator+"user_data"+File.separator;
    		   modelsFullPath = "WEB-INF"+File.separator+"models_full";
    		   modelsLightPath = "WEB-INF"+File.separator+"models_light";
    	   }
       }

        return jobjBtn.toJSONString();
    }

    // 메세지
	@RequestMapping(value = "/message", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public String message(@RequestBody JSONObject resObj) {

    	System.out.println("/message");
    	System.out.println(resObj.toJSONString());
        String content;
        content = (String) resObj.get("content");
        JSONObject jobjRes = new JSONObject();
        JSONObject jobjText = new JSONObject();
        HashMap<String, Object> forJsonObj = new HashMap<String, Object>();
        HashMap<String, Object> forJsonRes = new HashMap<String, Object>();
        
        // 사용자 구현
        if(content.contains("안녕")){
            forJsonObj.put("text","안녕 하세요");
        } else if(content.contains("사랑")){
        	forJsonObj.put("text","나도 너무너무 사랑해");
        } else if(content.contains("잘자")){
        	forJsonObj.put("text","굿밤!");
        } else if(content.contains("졸려")){
        	forJsonObj.put("text","졸리면 언능 세수하러 가요!");
        } else if(content.contains("시간")||content.contains("몇시")||content.contains("몇 시")){
        	forJsonObj.put("text","몹시 광분");
        }else if((content.contains("너")||content.contains("넌"))&&content.contains("누구")){
        	forJsonObj.put("text","난 올리봇이야 ^0^");
        }else if(content.contains("개새끼")){
        	forJsonObj.put("text","왈!왈!");
        } else if(content.contains("자연어 처리:")){
        	Komoran komoran = new Komoran(modelsFullPath);
        	komoran.setFWDic(userDicPath+"/fwd.user");
        	komoran.setUserDic(userDicPath+"/dic.user");

        	String input = content.substring(7);
        	KomoranResult analyzeResultList = komoran.analyze(input);
        	forJsonObj.put("text", analyzeResultList.getList().toString());
        }else {
        	forJsonObj.put("text","흠... 아직 지정해 두지 않은 말인걸.");
        }
        jobjText = new JSONObject(forJsonObj);
        forJsonRes.put("message", jobjText);
        jobjRes = new JSONObject(forJsonRes);
        System.out.println(jobjRes.toJSONString());

        return  jobjRes.toJSONString();
    }
    
    // GET
	@RequestMapping(value = "/komoranget", method = RequestMethod.GET)
    public String komoranget() {

        System.out.println("/komoranget");
        JSONObject jobjBtn = new JSONObject();
        HashMap<String, Object> forJsonObj = new HashMap<String, Object>();
        
        //String userBaseDir = System.getProperty("user.dir");
        Komoran komoran = new Komoran(modelsLightPath);
    	komoran.setFWDic(userDicPath+"/fwd.user");
    	komoran.setUserDic(userDicPath+"/dic.user");

    	String input = "밀리언 달러 베이비랑 바람과 함께 사라지다랑 뭐가 더 재밌었어?";
    	KomoranResult analyzeResultList = komoran.analyze(input);
    	List<Token> tokenList = analyzeResultList.getTokenList();

    	System.out.println("==========print 'getTokenList()'==========");
    	for (Token token : tokenList) {
    		System.out.println(token);
    		System.out.println(token.getMorph()+"/"+token.getPos()+"("+token.getBeginIndex()+","+token.getEndIndex()+")");
    		System.out.println();
    	}
    	System.out.println("==========print 'getNouns()'==========");
    	System.out.println(analyzeResultList.getNouns());
    	System.out.println();
    	System.out.println("==========print 'getPlainText()'==========");
    	System.out.println(analyzeResultList.getPlainText());
    	System.out.println();
    	System.out.println("==========print 'getList()'==========");
    	System.out.println(analyzeResultList.getList());
        
    	forJsonObj.put("ANALYZE",analyzeResultList.getList());
    	jobjBtn = new JSONObject(forJsonObj);

        return jobjBtn.toJSONString();
    }
    
}
