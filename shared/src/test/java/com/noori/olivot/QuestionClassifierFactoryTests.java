package com.noori.olivot;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.noori.olivot.QuestionClassifier;
import com.noori.olivot.QuestionClassifierFactory;
import com.noori.olivot.TextVectorizer;

import java.util.HashMap;
import java.util.Map;

public class QuestionClassifierFactoryTests {
    @Test
    public void createReturnsNeuralNetwork() throws Exception {
        Map<Integer, String> answerMapping = new HashMap<>();

        answerMapping.put(0, "Test answer 1");
        answerMapping.put(1, "Test answer 2");
        answerMapping.put(2, "Test answer 3");

        TextVectorizer vectorizer = mock(TextVectorizer.class);

        when(vectorizer.vocabularySize()).thenReturn(10);

        QuestionClassifier classifier = QuestionClassifierFactory.create(vectorizer, answerMapping);

        assertThat(classifier).isNotNull();
    }
}
