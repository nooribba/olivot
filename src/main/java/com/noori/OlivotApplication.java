package com.noori;

import java.util.List;

import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OlivotApplication {

	public static void main(String[] args) {
		SpringApplication.run(OlivotApplication.class, args);
		
//		Komoran komoran = new Komoran("models_full");
//    	komoran.setFWDic("user_data/fwd.user");
//    	komoran.setUserDic("user_data/dic.user");
//    	
//    	String content = "자연어 처리:밀리언 달러 베이비랑 바람과 함께 사라지다랑 뭐가 더 재밌었어?";
//    	//String input = "올리브영에선 뭐가 제일 핫해?";
//    	String input = content.substring(7);
//    	KomoranResult analyzeResultList = komoran.analyze(input);
//    	List<Token> tokenList = analyzeResultList.getTokenList();
//
//    	//print each tokens by getTokenList()
//    	System.out.println("==========print 'getTokenList()'==========");
//    	for (Token token : tokenList) {
//    		System.out.println(token);
//    		System.out.println(token.getMorph()+"/"+token.getPos()+"("+token.getBeginIndex()+","+token.getEndIndex()+")");
//    		System.out.println();
//    	}
//    	//print nouns
//    	System.out.println("==========print 'getNouns()'==========");
//    	System.out.println(analyzeResultList.getNouns());
//    	System.out.println();
//    	System.out.println("==========print 'getPlainText()'==========");
//    	System.out.println(analyzeResultList.getPlainText());
//    	System.out.println();
//    	System.out.println("==========print 'getList()'==========");
//    	System.out.println(analyzeResultList.getList());
	}
}
