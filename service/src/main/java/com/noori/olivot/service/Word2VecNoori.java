package com.noori.olivot.service;

import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.sentenceiterator.FileSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.ui.api.UIServer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.phrase_extractor.KoreanPhraseExtractor;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

import scala.collection.Seq;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Word2VecNoori {

	private static Logger log = LoggerFactory.getLogger(Word2VecNoori.class);
	private String inputFilePath = "input";
    private static String modelFilePath = "output/noori2vec.bin";
    private static String appendFilePath = "output/noori2vecAppend.bin";
    //private String modelFilePath = "output/GoogleNews-vectors-negative300.bin";

    public static void main(String[] args) throws IOException {
    	Word2VecNoori word2Vec = new Word2VecNoori();
    	
    	/*트레이닝*/
    	word2Vec.train();
    	
    	/*UI Server 시작*/
    	word2Vec.uiServer();
    	
    	/*벡터 모델파일 Append*/
    	//word2Vec.appendVector();
        
        /*학습된 모델 불러오기*/
    	File appendFile = new File(word2Vec.appendFilePath);//word2Vec.appendFilePath
        Word2Vec word2VecModel = WordVectorSerializer.readWord2VecModel(appendFile);
    	//Word2Vec word2VecModel = WordVectorSerializer.loadFullModel(word2Vec.appendFilePath);
        
        Collection<String> list = word2VecModel.wordsNearestSum("boy" , 5);
        log.info("##### boy: "+ list);
        list = word2VecModel.wordsNearestSum("girl" , 5);
        log.info("##### girl: " + list);
        Collection<String> stringList = word2VecModel.wordsNearestSum("day", 5);
        log.info("##### day: " + stringList);
        stringList = word2VecModel.wordsNearestSum("money", 5);
        log.info("##### money: " + stringList);
    	//king - queen + woman = man (+부호 단어 첫번째 입력변수, -부호 단어 두번째 입력변수)
    	//king - man + woman = queen
        //Collection<String> kingList = word2VecModel.wordsNearest(Arrays.asList("king", "woman"), Arrays.asList("queen"), 5); //결과에 가장 가까운 5개의 단어
        Collection<String> kingList = word2VecModel.wordsNearestSum(Arrays.asList("왕", "여자"), Arrays.asList("여왕"), 5);
        log.info("##### kingList : "+kingList);
        kingList = word2VecModel.wordsNearestSum(Arrays.asList("king", "woman"), Arrays.asList("man"), 5);
        log.info("##### kingList : "+kingList);
        INDArray word2VectorMatrix = word2VecModel.getWordVectorMatrix("king");
        log.info("##### Matrix(search word 'king' in model) :"+word2VectorMatrix);
        Collection<String> korList = word2VecModel.wordsNearest("한국" , 5);
        log.info("##### 한국 Nearest : "+ korList);
        korList = word2VecModel.wordsNearestSum("대한민국" , 5);
        log.info("##### 대한민국 NearestSum : "+ korList);
        korList = word2VecModel.similarWordsInVocabTo("대한한국", 5);
        log.info("##### '대한민국' Similar : "+ korList);
        Collection<String> testList = word2VecModel.wordsNearestSum("올리브영", 5);
        log.info("##### '올리브영' NearestSum : "+ testList);
        word2VectorMatrix = word2VecModel.getWordVectorMatrix("뷰티");
        log.info("##### search word '뷰티' in model :"+word2VectorMatrix);
        testList = word2VecModel.wordsNearest(word2VectorMatrix, 5);
        log.info("##### '뷰티' Nearest matrix : "+ testList);
        testList = word2VecModel.wordsNearestSum(word2VectorMatrix, 5);
        log.info("##### '뷰티' NearestSum matrix : "+ testList);
        testList = word2VecModel.similarWordsInVocabTo("부티", 5);
        log.info("##### '부티' similarword : "+ testList);
        log.info("##### VECTOR MODEL FILE LINES : "+getFileLines(appendFilePath));
        
        
//        log.info("##### 19508 row:"+word2VectorMatrix.getRow(19507));
//        log.info("##### 19509 row:"+word2VectorMatrix.getRow(19508));
//        log.info("##### 19510 row:"+word2VectorMatrix.getRow(19509));
//        log.info("##### 19511 row:"+word2VectorMatrix.getRow(19510));
//        log.info("##### 19512 row:"+word2VectorMatrix.getRow(19511));
    }

    private static int getFileLines(String appendFilePath) {
    	File fin = new File(appendFilePath);
		FileInputStream fis;
		int lineCnt = 0;
		try {
			fis = new FileInputStream(fin);
			BufferedReader in = new BufferedReader(new InputStreamReader(fis));
	 
			String aLine = null;
			while ((aLine = in.readLine()) != null) {
				lineCnt++;
			}
			in.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lineCnt;
	}

	private void appendVector() {
    	log.info("##### appendVector");
    	/*File dir = new File(".");
		String source = dir.getCanonicalPath() + File.separator + "Code.txt";
		String dest = dir.getCanonicalPath() + File.separator + "Dest.txt";*/
    	String source = "output/noori2vec.bin";
    	String dest = "output/noori2vecAppend.bin";
 
		File fin = new File(source);
		FileInputStream fis;
		try {
			log.info("##### TARGET FILE LINES : "+getFileLines(dest));
			fis = new FileInputStream(fin);
			BufferedReader in = new BufferedReader(new InputStreamReader(fis));
			
			 
			FileWriter fstream = new FileWriter(dest, true);
			BufferedWriter out = new BufferedWriter(fstream);
	 
			String aLine = null;
			int count = 0;
			while ((aLine = in.readLine()) != null) {
				if(count!=0){
					out.write(aLine);
					out.newLine();
				}
				count++;
			}
			log.info("##### SOURCE FILE LINES : "+count);
			in.close();
			out.close();
			fstream.close();
			fis.close();
			log.info("##### RESULT TARGET APPEND FILE LINES : "+getFileLines(dest));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public  void train() throws IOException {
    	log.info("##### read file");
    	SentenceIterator sentenceIterator = new FileSentenceIterator(new File(inputFilePath));
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
        
        InMemoryLookupCache cache = new InMemoryLookupCache();
        WeightLookupTable<VocabWord> table = new InMemoryLookupTable.Builder<VocabWord>()
        		.vectorLength(100)
        		.useAdaGrad(false)
        		.cache(cache)
        		.lr(0.025f).build();

        log.info("##### training...");
        Word2Vec vec = new Word2Vec.Builder()
        		.batchSize(1000)
        		.learningRate(0.025)
		        .minWordFrequency(3)
		        .iterations(1)
		        .layerSize(300)
		        .windowSize(5)
		        .seed(42)
		        .epochs(3)
		        .elementsLearningAlgorithm(new SkipGram<VocabWord>())
		        .iterate(sentenceIterator)
		        .tokenizerFactory(tokenizerFactory)
		        .lookupTable(table)
		        .vocabCache(cache)
		        .build();
                /*.minWordFrequency(2)
                .layerSize(300)
                .windowSize(5)
                .seed(42)
                .epochs(3)
                .elementsLearningAlgorithm(new SkipGram<VocabWord>())
                .iterate(sentenceIterator)
                .tokenizerFactory(tokenizerFactory)
                .build();*/
        vec.fit();

        WordVectorSerializer.writeWordVectors(vec, modelFilePath);
        log.info("##### TRAINING FILE LINES : "+getFileLines(modelFilePath));
    }
	
	public void uiServer() {
      UIServer server = UIServer.getInstance();
      System.out.println("##### Started on port " + server.getPort());
	}
	
	public void TwitterKoreanEx() {
		String text = "한국어를 처리하는 예시입니닼ㅋㅋㅋㅋㅋ #한국어";

	    // Normalize
	    CharSequence normalized = TwitterKoreanProcessorJava.normalize(text);
	    System.out.println(normalized);
	    // 한국어를 처리하는 예시입니다ㅋㅋ #한국어


	    // Tokenize
	    Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);
	    System.out.println(TwitterKoreanProcessorJava.tokensToJavaStringList(tokens));
	    // [한국어, 를, 처리, 하는, 예시, 입니, 다, ㅋㅋ, #한국어]
	    System.out.println(TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(tokens));
	    // [한국어(Noun: 0, 3), 를(Josa: 3, 1),  (Space: 4, 1), 처리(Noun: 5, 2), 하는(Verb: 7, 2),  (Space: 9, 1), 예시(Noun: 10, 2), 입니(Adjective: 12, 2), 다(Eomi: 14, 1), ㅋㅋ(KoreanParticle: 15, 2),  (Space: 17, 1), #한국어(Hashtag: 18, 4)]


	    // Stemming
	    Seq<KoreanTokenizer.KoreanToken> stemmed = TwitterKoreanProcessorJava.stem(tokens);
	    System.out.println(TwitterKoreanProcessorJava.tokensToJavaStringList(stemmed));
	    // [한국어, 를, 처리, 하다, 예시, 이다, ㅋㅋ, #한국어]
	    System.out.println(TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(stemmed));
	    // [한국어(Noun: 0, 3), 를(Josa: 3, 1),  (Space: 4, 1), 처리(Noun: 5, 2), 하다(Verb: 7, 2),  (Space: 9, 1), 예시(Noun: 10, 2), 이다(Adjective: 12, 3), ㅋㅋ(KoreanParticle: 15, 2),  (Space: 17, 1), #한국어(Hashtag: 18, 4)]


	    // Phrase extraction
	    List<KoreanPhraseExtractor.KoreanPhrase> phrases = TwitterKoreanProcessorJava.extractPhrases(tokens, true, true);
	    System.out.println(phrases);
	    // [한국어(Noun: 0, 3), 처리(Noun: 5, 2), 처리하는 예시(Noun: 5, 7), 예시(Noun: 10, 2), #한국어(Hashtag: 18, 4)]
	}
}
