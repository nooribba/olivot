package com.noori.olivot;

import org.deeplearning4j.bagofwords.vectorizer.BagOfWordsVectorizer;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.File;
import java.io.IOException;

public class QuestionVectorizerFactory {
    /**
     * Creates a new preconfigured question vectorizer
     *
     * @return A new instance of {@link TextVectorizer}
     */
    public static TextVectorizer create(File inputFile) throws Exception {
        TokenizerFactory tokenizerFactory = createTokenizerFactory();

        // This vectorizer uses the TF-IDF algorithm to produce
        // a unique fingerprint for every question we feed it.
        BagOfWordsVectorizer vectorizer = new BagOfWordsVectorizer.Builder()
                .setTokenizerFactory(tokenizerFactory)
                .setIterator(new CSVSentenceIterator(inputFile))
                .build();

        return new TextVectorizer(vectorizer);
    }

    /**
     * Restores the vectorizer by loading the vocabulary cache from file
     *
     * @param inputFile Input file containing a serialized vocabulary cache
     * @return Returns the restored vectorizer
     */
    public static TextVectorizer restore(File inputFile) throws IOException {
        TokenizerFactory tokenizerFactory = createTokenizerFactory();

        BagOfWordsVectorizer vectorizer = new BagOfWordsVectorizer.Builder()
                .setTokenizerFactory(tokenizerFactory)
                .setVocab(loadVocabulary(inputFile))
                .build();

        return new TextVectorizer(vectorizer);
    }

    /**
     * Creates the tokenizer factory for the vectorizer
     * @return  Returns a new instance of {@link TokenizerFactory}
     */
    private static TokenizerFactory createTokenizerFactory() {
        // The tokenizer factory will use the preprocessor to make everything lower-case
        // and remove any punctuation in the text.
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        return tokenizerFactory;
    }

    /**
     * Reads the vocabulary cache from file
     *
     * @param inputFile Input file containing the serialized cache
     * @return Returns the restored vocabulary cache
     * @throws IOException Gets thrown when the input file could not be read
     */
    private static VocabCache<VocabWord> loadVocabulary(File inputFile) throws IOException {
        return WordVectorSerializer.readVocabCache(inputFile);
    }
}
