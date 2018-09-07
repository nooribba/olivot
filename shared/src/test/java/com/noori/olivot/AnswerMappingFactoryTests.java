package com.noori.olivot;

import org.junit.jupiter.api.Test;

import com.noori.olivot.AnswersMappingFactory;

import java.io.File;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class AnswerMappingFactoryTests {
    @Test
    public void createReturnsMapping() throws Exception {
        Map<Integer,String> mapping = AnswersMappingFactory.create(new File("../data/answers.csv"));

        assertThat(mapping.size()).isEqualTo(8);

        for(Integer key : mapping.keySet()) {
            assertThat(mapping.get(key)).isNotEmpty();
        }
    }
}
