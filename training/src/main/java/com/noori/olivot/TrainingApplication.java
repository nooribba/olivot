package com.noori.olivot;

import java.io.File;
import java.util.Map;

import org.nd4j.linalg.api.ndarray.INDArray;

public class TrainingApplication {
    public static void main(String... args) throws Exception {
        Map<Integer, String> answers = AnswersMappingFactory.create(new File("data/answers.csv"));

        //TextVectorizer vectorizer = QuestionVectorizerFactory.create(new File("data/"));
        TextVectorizer vectorizer = QuestionVectorizerFactory.create(new File("data/questions_train.csv"));
        vectorizer.fit();
        vectorizer.save(new File("model/vectorizer.bin"));
        System.out.println("##### vectorizer.bin save");

        QuestionClassifier classifier = QuestionClassifierFactory.create(vectorizer, answers);
        System.out.println("##### QustionClassifierFactory Create");
        classifier.fit(new File("data/questions_train.csv"));
        System.out.println("##### question csv fit");
        classifier.save(new File("model/classifier.bin"));
        System.out.println("##### classifier.bin save");
        
        //Test
        INDArray output = vectorizer.transform("마스크팩 찾아줘");
        int answerIndex = output.argMax(1).getInt(0,0);
        System.out.println("##### output : "+output);
        System.out.println("##### output answerIndex : "+answerIndex);
        System.out.println("##### answer text : "+answers.get(answerIndex));
        classifier.checkNetworkOutput(output);
    }

	
}
