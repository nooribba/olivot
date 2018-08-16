package com.noori.olivot;

import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.noori.olivot.QuestionVectorizerFactory;
import com.noori.olivot.TextVectorizer;

import java.io.File;

import static org.assertj.core.api.Assertions.*;

public class QuestionVectorizerFactoryTests {
    @Test
    public void createReturnsValidVectorizer() throws Exception {
        TextVectorizer questionVectorizer = QuestionVectorizerFactory.create(new File("../data/questions_train.csv"));
        questionVectorizer.fit();

        INDArray output = questionVectorizer.transform("How can I register?");

        assertThat(Nd4j.zeros(1,output.size(1))).isNotEqualTo(output);
    }
}
