package komoran;
/*******************************************************************************
 * olivot komoran test class
 * 
 * mvn install 시 test 체크하려면 @Test 주석 해제
 * 
 * "DEFAULT_MODEL.LIGHT"과 "DEFAULT_MODEL.FULL"의 차이가 무엇인가요?
 * LIGHT 모델은 일반적으로 사용되는 문장들을 학습한 모델로 다양한 분야에서 사용하실 수 있는 기본 모델입니다.
 * FULL 모델은 LIGHT 모델에 위키피디아의 타이틀을 NNP(고유명사)로 포함해서 학습한 것이며 그러므로 
 * LIGHT 모델보다 상대적으로 용량이 큽니다.
 * 형태소 분석기의 결과가 그대로 서비스에 노출되는 어플리케이션이 아니라면 LIGHT 모델을 권장해 드립니다.
 * 
 * "setFWDDic"과 "setUserDic"은 무엇인가요?
 * "setFWDDic"은 파일 형태의 기분석 사전을 형태소 분석기에 적재하는 메소드입니다. 
 * 기분석 사전은 일종의 cache입니다. 어절 단위로 기분석 사전을 lookup하여 값이 있는 경우에만 
 * 형태소 분석 단계를 거치지 않고 lookup 된 값이 그대로 분석 결과에 반영됩니다.
 * "setUserDic"은 사용자 사전을 형태소 분석기에 적재하는 메소드입니다. 
 * 사용자 사전에 포함된 형태소들은 형태소 분석 단계에서 가장 높은 우선순위가 부여됩니다. 
 * 사용자 사전에 포함된 형태소가 분석 대상 문장 내에서 문법적인 위치만 일치한다면 사용자가 지정한 품사로 분석됩니다. 
 * 사이드 이펙트가 발생할 수 있으니 주의하여 사용하셔야 합니다.
 * 
 *******************************************************************************/

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import kr.co.shineware.nlp.komoran.parser.KoreanUnitParser;
import kr.co.shineware.nlp.komoran.util.KomoranCallable;
import kr.co.shineware.util.common.file.FileUtil;
import kr.co.shineware.util.common.model.Pair;

import org.junit.Before;
import org.junit.Test;

public class KomoranTest {
	
	private Komoran komoran;
	private static String userDicPath = null;
	private static String modelsFullPath = null;
	private static String modelsLightPath = null;
	private static String osName = null;

    @Before
    public void init() {
    	this.komoran = new Komoran(DEFAULT_MODEL.LIGHT);
    	
    	if(osName == null){
        	osName = System.getProperty("os.name").toLowerCase();
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
    }

    //@Test
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

    //@Test
    public void singleThreadSpeedTest() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("result/analyze_result.txt"));

        List<String> lines = FileUtil.load2List(userDicPath+"wiki.titles");
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

        System.out.println("Elapsed time : " + (end - begin) + "(singleThreadSpeedTest)");
    }

    //@Test
    public void executorServiceTest() {

        long begin = System.currentTimeMillis();
        this.komoran.analyzeTextFile(userDicPath+"wiki.titles", "result/analyze_result.txt", 2);
        long end = System.currentTimeMillis();

        System.out.println("Elapsed time : " + (end - begin) + "(executorServiceTest)");
    }

    //@Test
    public void multiThreadSpeedTest() throws ExecutionException, InterruptedException, IOException {
    	long begin = 0L;
    	long end = 0L;
        for (int i = 0; i < 10; i++) {

            BufferedWriter bw = new BufferedWriter(new FileWriter("result/analyze_result.txt"));

            List<String> lines = FileUtil.load2List(userDicPath+"wiki.titles");

            begin = System.currentTimeMillis();

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


            end = System.currentTimeMillis();

            bw.close();
            executor.shutdown();
        }
        System.out.println("Elapsed time : " + (end - begin) + "(multiThreadSpeedTest)");
    }

    //@Test
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

    //@Test
    public void load() {
        this.komoran.load(modelsFullPath);
    }

    //@Test
    public void setFWDic() {
        this.komoran.setFWDic(userDicPath+"fwd.user");
        this.komoran.analyze("감사합니다! nice good!");
    }

    //@Test
    public void setUserDic() {
        this.komoran.setUserDic(userDicPath+"dic.user");
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
//		if(osName == null){
//        	osName = System.getProperty("os.name").toLowerCase();
//			System.out.println("##### osName:"+osName);
//       }
//       if(userDicPath == null || modelsFullPath == null || modelsLightPath == null){
//    	   if(osName.contains("window")){
//    		   userDicPath = "user_data"+File.separator;
//			   modelsFullPath = "models_full";
//			   modelsLightPath = "models_light";
//    	   }else{
//    		   userDicPath = "WEB-INF"+File.separator+"user_data"+File.separator;
//    		   modelsFullPath = "WEB-INF"+File.separator+"models_full";
//    		   modelsLightPath = "WEB-INF"+File.separator+"models_light";
//    	   }
//       }
//		
//		Komoran komoran = new Komoran(modelsLightPath);
//		komoran.setFWDic(userDicPath+"fwd.user");
//		komoran.setUserDic(userDicPath+"dic.user");
//
//		String input = "뭐가 더 싼지 네가 말해주길 바랬어요";
//		KomoranResult analyzeResultList = komoran.analyze(input);
////		List<Token> tokenList = analyzeResultList.getTokenList();
////
////		System.out.println("==========print 'getTokenList()'==========");
////		for (Token token : tokenList) {
////			System.out.println(token);
////			System.out.println(token.getMorph()+"/"+token.getPos()+"("+token.getBeginIndex()+","+token.getEndIndex()+")");
////			System.out.println();
////		}
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
