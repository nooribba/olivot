/*******************************************************************************
 * KOMORAN 3.0 - Korean Morphology Analyzer
 *
 * Copyright 2015 Shineware http://www.shineware.co.kr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 	
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.noori.komoran.test;

import com.noori.komoran.constant.DEFAULT_MODEL;
import com.noori.komoran.core.Komoran;
import com.noori.komoran.model.KomoranResult;
import com.noori.komoran.model.Token;
import com.noori.komoran.parser.KoreanUnitParser;
import com.noori.komoran.util.KomoranCallable;
import kr.co.shineware.util.common.file.FileUtil;
import kr.co.shineware.util.common.model.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.Before;
import org.junit.Test;

public class KomoranTest {
	
	private Komoran komoran;

    @Before
    public void init() {
    	this.komoran = new Komoran(DEFAULT_MODEL.LIGHT);
    }

    @Test
    public void notAnalyzeCombineTest() {
    	//String input = "밀리언 달러 베이비랑 바람과 함께 사라지다랑 뭐가 더 재밌었어?";
    	String input = "올리브영에선 뭐가 제일 핫해?";
        KomoranResult komoranResult = this.komoran.analyze(input);
        System.out.println(komoranResult.getPlainText());
        System.out.println(komoranResult.getList());
        System.out.println(komoranResult.getMorphesByTags("NA"));
        System.out.println(komoranResult.getTokenList());

        KoreanUnitParser koreanUnitParser = new KoreanUnitParser();
        System.out.println(koreanUnitParser.parseWithType("감사"));
        System.out.println(koreanUnitParser.combineWithType(koreanUnitParser.parseWithType("축하")));

    }

    @Test
    public void singleThreadSpeedTest() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("WEB-INF/analyze_result.txt"));

        List<String> lines = FileUtil.load2List("WEB-INF/user_data/wiki.titles");
        List<KomoranResult> komoranList = new ArrayList<>();

        long begin = System.currentTimeMillis();

        int count = 0;

        for (String line : lines) {

            komoranList.add(this.komoran.analyze(line));
            if (komoranList.size() == 1000) {
                for (KomoranResult komoranResult : komoranList) {
                    bw.write(komoranResult.getPlainText());
                    bw.newLine();
                }
                komoranList.clear();
            }
            count++;
            if (count % 10000 == 0) {
                System.out.println(count);
            }
        }

        for (KomoranResult komoranResult : komoranList) {
            bw.write(komoranResult.getPlainText());
            bw.newLine();
        }

        long end = System.currentTimeMillis();

        bw.close();

        System.out.println("Elapsed time : " + (end - begin));
    }

    @Test
    public void executorServiceTest() {

        long begin = System.currentTimeMillis();
        this.komoran.analyzeTextFile("WEB-INF/user_data/wiki.titles", "WEB-INF/analyze_result.txt", 2);
        long end = System.currentTimeMillis();

        System.out.println("Elapsed time : " + (end - begin));
    }

    @Test
    public void multiThreadSpeedTest() throws ExecutionException, InterruptedException, IOException {

        for (int i = 0; i < 10; i++) {

            BufferedWriter bw = new BufferedWriter(new FileWriter("WEB-INF/analyze_result.txt"));

            List<String> lines = FileUtil.load2List("WEB-INF/user_data/wiki.titles");

            long begin = System.currentTimeMillis();

            List<Future<KomoranResult>> komoranList = new ArrayList<>();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);


            for (String line : lines) {
                KomoranCallable komoranCallable = new KomoranCallable(this.komoran, line);
                komoranList.add(executor.submit(komoranCallable));
            }

            for (Future<KomoranResult> komoranResultFuture : komoranList) {
                KomoranResult komoranResult = komoranResultFuture.get();
                bw.write(komoranResult.getPlainText());
                bw.newLine();
            }


            long end = System.currentTimeMillis();

            bw.close();
            executor.shutdown();
            System.out.println("Elapsed time : " + (end - begin));
        }
    }

    @Test
    public void analyze() {
        KomoranResult komoranResult = this.komoran.analyze("네가 없는 거리에는 내가 할 일이 많아서 마냥 걷다보면 추억을 가끔 마주치지.");
        List<Pair<String, String>> pairList = komoranResult.getList();
        for (Pair<String, String> morphPosPair : pairList) {
            System.out.println(morphPosPair);
        }
        System.out.println();

        List<String> nounList = komoranResult.getNouns();
        for (String noun : nounList) {
            System.out.println(noun);
        }
        System.out.println();

        List<String> verbList = komoranResult.getMorphesByTags("VV", "NNG");
        for (String verb : verbList) {
            System.out.println(verb);
        }
        System.out.println();

        List<String> eomiList = komoranResult.getMorphesByTags("EC");
        for (String eomi : eomiList) {
            System.out.println(eomi);
        }

        System.out.println(komoranResult.getPlainText());

        List<Token> tokenList = komoranResult.getTokenList();
        for (Token token : tokenList) {
            System.out.println(token);
        }
    }

    @Test
    public void load() {
        this.komoran.load("WEB-INF/models_full");
    }

    @Test
    public void setFWDic() {
        this.komoran.setFWDic("WEB-INF/user_data/fwd.user");
        this.komoran.analyze("감사합니다! nice good!");
    }

    @Test
    public void setUserDic() {
        this.komoran.setUserDic("WEB-INF/user_data/dic.user");
        System.out.println(this.komoran.analyze("싸이는 가수다").getPlainText());
        System.out.println(this.komoran.analyze("센트롤이").getPlainText());
        System.out.println(this.komoran.analyze("센트롤이").getTokenList());
        System.out.println(this.komoran.analyze("감싼").getTokenList());
        System.out.println(this.komoran.analyze("싸").getTokenList());
        System.out.println(this.komoran.analyze("난").getTokenList());
        System.out.println(this.komoran.analyze("밀리언 달러 베이비랑").getTokenList());
        System.out.println(this.komoran.analyze("밀리언 달러 베이비랑 바람과 함께 사라지다랑 뭐가 더 재밌었어?").getTokenList());
    }

//	public static void main(String[] args) throws Exception {
//		Komoran komoran = new Komoran("WEB-INF/models_light");
//		komoran.setFWDic("WEB-INF/user_data/fwd.user");
//		komoran.setUserDic("WEB-INF/user_data/dic.user");
//
//		String input = "올리브영에선 뭐가 제일 핫해?";
//		KomoranResult analyzeResultList = komoran.analyze(input);
//		List<Token> tokenList = analyzeResultList.getTokenList();
//
//		System.out.println("==========print 'getTokenList()'==========");
//		for (Token token : tokenList) {
//			System.out.println(token);
//			System.out.println(token.getMorph()+"/"+token.getPos()+"("+token.getBeginIndex()+","+token.getEndIndex()+")");
//			System.out.println();
//		}
//		System.out.println("==========print 'getNouns()'==========");
//		System.out.println(analyzeResultList.getNouns());
//		System.out.println();
//		System.out.println("==========print 'getPlainText()'==========");
//		System.out.println(analyzeResultList.getPlainText());
//		System.out.println();
//		System.out.println("==========print 'getList()'==========");
//		System.out.println(analyzeResultList.getList());
//	}
}
