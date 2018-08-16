package com.noori.olivot.service;

import org.deeplearning4j.bagofwords.vectorizer.BagOfWordsVectorizer;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;

import java.io.File;
import java.io.IOException;

public class TextClassifier {
    static BagOfWordsVectorizer loadVectorizer(File path) throws IOException {
        return new BagOfWordsVectorizer.Builder()
                .setVocab(WordVectorSerializer.readVocabCache(path))
                .setTokenizerFactory(new DefaultTokenizerFactory())
                .setMinWordFrequency(1)
                .build();
    }
}
