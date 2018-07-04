package com.noori.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


/**
 * noori
 */
@RestController
public class OlivotController {
	
    // 키보드
	@RequestMapping(value = "/keyboard", method = RequestMethod.GET)
    public String keyboard() {

        System.out.println("/keyboard");

        JSONObject jobjBtn = new JSONObject();
        HashMap<String, Object> forJsonObj = new HashMap<String, Object>();
        forJsonObj.put("type", "text");
        //jobjBtn.put("type", "text");
        jobjBtn = new JSONObject(forJsonObj);

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
        	forJsonObj.put("text","꿈 속에서도 너를 볼꺼야");
        } else if(content.contains("졸려")){
        	forJsonObj.put("text","졸리면 언능 세수하러 가용!");
        } else if(content.contains("시간")||content.contains("몇시")||content.contains("몇 시")){
        	forJsonObj.put("text","몹시 광분");
        }else if((content.contains("너")||content.contains("넌"))&&content.contains("누구")){
        	forJsonObj.put("text","난 올리봇이야 ^0^");
        } else if(content.contains("자연어 처리:")){
        	Komoran komoran = new Komoran("WEB-INF/models_full");
        	komoran.setFWDic("WEB-INF/user_data/fwd.user");
        	komoran.setUserDic("WEB-INF/user_data/dic.user");
        	
        	//String input = "밀리언 달러 베이비랑 바람과 함께 사라지다랑 뭐가 더 재밌었어?";
        	//String input = "올리브영에선 뭐가 제일 핫해?";
        	String input = content.substring(7);
        	KomoranResult analyzeResultList = komoran.analyze(input);
        	
        	/*List<Token> tokenList = analyzeResultList.getTokenList();
        	System.out.println("==========print 'getTokenList()'==========");
        	for (Token token : tokenList) {
        		System.out.println(token);
        		System.out.println(token.getMorph()+"/"+token.getPos()+"("+token.getBeginIndex()+","+token.getEndIndex()+")");
        		System.out.println();
        	}*/
            
        	forJsonObj.put("text", analyzeResultList.getList().toString());
        }else {
        	forJsonObj.put("text","흠... 아직 지정해 두지 않은 말인걸.");
        }
        jobjText = new JSONObject(forJsonObj);
        //jobjRes.put("message", jobjText);
        // jobjRes.put("message", forJsonObj);
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
        //forJsonObj.put("type","text");
        //jobjBtn.put("type", "text");
        //jobjBtn = new JSONObject(forJsonObj);
        
        String userBaseDir = System.getProperty("user.dir");
	System.out.println("##### userBaseDir:"+userBaseDir);
        //Komoran komoran = new Komoran("models_full");
        Komoran komoran = new Komoran("WEB-INF/models_light");
    	komoran.setFWDic("WEB-INF/user_data/fwd.user");
    	komoran.setUserDic("WEB-INF/user_data/dic.user");

    	String input = "밀리언 달러 베이비랑 바람과 함께 사라지다랑 뭐가 더 재밌었어?";
    	//String input = "올리브영에선 뭐가 제일 핫해?";
    	KomoranResult analyzeResultList = komoran.analyze(input);
    	List<Token> tokenList = analyzeResultList.getTokenList();

    	//print each tokens by getTokenList()
    	System.out.println("==========print 'getTokenList()'==========");
    	for (Token token : tokenList) {
    		System.out.println(token);
    		System.out.println(token.getMorph()+"/"+token.getPos()+"("+token.getBeginIndex()+","+token.getEndIndex()+")");
    		System.out.println();
    	}
    	//print nouns
    	System.out.println("==========print 'getNouns()'==========");
    	System.out.println(analyzeResultList.getNouns());
    	System.out.println();
    	System.out.println("==========print 'getPlainText()'==========");
    	System.out.println(analyzeResultList.getPlainText());
    	System.out.println();
    	System.out.println("==========print 'getList()'==========");
    	System.out.println(analyzeResultList.getList());
        
    	forJsonObj.put("ANALYZE",analyzeResultList.getList());
    	//jobjBtn.put("ANALYZE", analyzeResultList.getList());
    	jobjBtn = new JSONObject(forJsonObj);

        return jobjBtn.toJSONString();
    }
    
    // POST
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/komoranpost", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public String komoranpost(@RequestBody JSONObject resObj) {

        System.out.println("/komoranpost");
        System.out.println(resObj.toJSONString());

        String content;
        content = (String) resObj.get("content");
        JSONObject jobjRes = new JSONObject();
        JSONObject jobjText = new JSONObject();

        // 사용자 구현
        if(content.contains("안녕")){
            jobjText.put("text","안녕 하세요");
        } else if(content.contains("사랑")){
            jobjText.put("text","나도 너무너무 사랑해");
        } else if(content.contains("잘자")){
            jobjText.put("text","꿈 속에서도 너를 볼꺼야");
        } else if(content.contains("졸려")){
            jobjText.put("text","졸리면 언능 세수하러 가용!");
        } else if(content.contains("시간")||content.contains("몇 시")){
            jobjText.put("text","섹시");
        } else {
            jobjText.put("text","흠... 아직 지정해 두지 않은 말인걸.");
        }

        jobjRes.put("message", jobjText);
        
        
        Komoran komoran = new Komoran("models_full");
    	komoran.setFWDic("user_data/fwd.user");
    	komoran.setUserDic("user_data/dic.user");

    	String input = "밀리언 달러 베이비랑 바람과 함께 사라지다랑 뭐가 더 재밌었어?";
    	//String input = "올리브영에선 뭐가 제일 핫해?";
    	KomoranResult analyzeResultList = komoran.analyze(input);
    	List<Token> tokenList = analyzeResultList.getTokenList();

    	//print each tokens by getTokenList()
    	System.out.println("==========print 'getTokenList()'==========");
    	for (Token token : tokenList) {
    		System.out.println(token);
    		System.out.println(token.getMorph()+"/"+token.getPos()+"("+token.getBeginIndex()+","+token.getEndIndex()+")");
    		System.out.println();
    	}
    	//print nouns
    	System.out.println("==========print 'getNouns()'==========");
    	System.out.println(analyzeResultList.getNouns());
    	System.out.println();
    	System.out.println("==========print 'getPlainText()'==========");
    	System.out.println(analyzeResultList.getPlainText());
    	System.out.println();
    	System.out.println("==========print 'getList()'==========");
    	System.out.println(analyzeResultList.getList());

    	jobjRes.put("ANALYZE", analyzeResultList.getList());
        System.out.println(jobjRes.toJSONString());

        return  jobjRes.toJSONString();
    }
    
    
    
}
