package com.noori.olivot;

import org.deeplearning4j.bagofwords.vectorizer.BagOfWordsVectorizer;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;
import java.io.IOException;

/**
 * Converts questions to a correct representation for the question classifier
 */
public class TextVectorizer {
    private final BagOfWordsVectorizer vectorizer;

    /**
     * Initializes a new instance of {@link TextVectorizer}
     *
     * @param vectorizer Bag of words vectorizer to use for vectorization
     */
    public TextVectorizer(BagOfWordsVectorizer vectorizer) {
        this.vectorizer = vectorizer;
    }

    /**
     * Transforms questions to a vector representation suitable for our model
     *
     * @param question Questions to transform
     * @return Returns a 2D-matrix with the correct representation for the neural network
     */
    public INDArray transform(String question) {
        return vectorizer.transform(question);
    }

    /**
     * Gets the size of the vocabulary
     *
     * @return Returns the number of elements in the vocabulary
     */
    public int vocabularySize() {
        return vectorizer.getVocabCache().numWords();
    }

    /**
     * Trains the vectorizer
     */
    public void fit() {
        vectorizer.fit();
    }

    /**
     * Saves the tokenizer to disk
     *
     * @param file File to save the tokenizer to
     */
    public void save(File file) throws IOException {
        WordVectorSerializer.writeVocabCache(vectorizer.getVocabCache(), file);
    }
}
