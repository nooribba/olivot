package com.noori.olivot;

import java.io.File;
import java.util.Map;

import com.noori.olivot.AnswersMappingFactory;
import com.noori.olivot.QuestionClassifier;
import com.noori.olivot.QuestionClassifierFactory;
import com.noori.olivot.QuestionVectorizerFactory;
import com.noori.olivot.TextVectorizer;

public class TrainingApplication {
    public static void main(String... args) throws Exception {
        Map<Integer, String> answers = AnswersMappingFactory.create(new File("data/answers.csv"));

        TextVectorizer vectorizer = QuestionVectorizerFactory.create(new File("data/"));
        vectorizer.fit();
        vectorizer.save(new File("model/vectorizer.bin"));

        QuestionClassifier classifier = QuestionClassifierFactory.create(vectorizer, answers);
        classifier.fit(new File("data/questions_train.csv"));
        classifier.save(new File("model/classifier.bin"));
    }
}