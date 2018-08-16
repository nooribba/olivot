package com.noori.olivot;

import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A wrapper around the reader logic in DeepLearning4J to make it easier to work with our dataset.
 * This data source reads batches of records and encodes them automatically to a format the neural network
 * will understand.
 */
public class QuestionDataSource {
    private final File sourceFile;
    private final TextVectorizer vectorizer;
    private final int batchSize;
    private final int possibleAnswers;

    private CSVRecordReader reader;

    /**
     * Initializes a new instance of {@link QuestionDataSource}
     *
     * @param sourceFile      Source file containing the data to read
     * @param vectorizer      Vectorizer to encode the question
     * @param batchSize       Number of records to read in each batch
     * @param possibleAnswers The number of possible answers that can be given
     */
    public QuestionDataSource(File sourceFile, TextVectorizer vectorizer, int batchSize, int possibleAnswers) {
        this.sourceFile = sourceFile;
        this.vectorizer = vectorizer;
        this.batchSize = batchSize;
        this.possibleAnswers = possibleAnswers;
    }

    /**
     * Checks for the availability of more records in the data source
     *
     * @return Returns true when there are more records available; Otherwise false
     * @throws IOException          Gets thrown when the input file could not be reader
     * @throws InterruptedException Gets thrown when the program is interrupted
     */
    public boolean hasNext() throws IOException, InterruptedException {
        ensureRecordReader();

        return reader.hasNext();
    }

    /**
     * Resets the data source to its starting position
     *
     * @throws IOException          Gets thrown when the input file could not be reader
     * @throws InterruptedException Gets thrown when the program is interrupted
     */
    public void reset() throws IOException, InterruptedException {
        ensureRecordReader();

        reader.reset();
    }

    /**
     * Gets the next batch from the data source
     *
     * @return The next batch of data
     * @throws IOException          Gets thrown when the input file could not be reader
     * @throws InterruptedException Gets thrown when the program is interrupted
     */
    public Batch next() throws IOException, InterruptedException {
        ensureRecordReader();

        List<List<Writable>> records = reader.next(batchSize);

        // Questions are encoded using the question vectorizer.
        List<INDArray> featureData = records.stream()
                .map(record -> vectorizer.transform(records.get(1).toString()))
                .collect(Collectors.toList());

        // Answers are encoded as one-hot vectors.
        List<INDArray> labelData = records.stream()
                .map(record -> oneHotEncode(record.get(2).toInt() - 1))
                .collect(Collectors.toList());

        int[] featureShape = {records.size(), vectorizer.vocabularySize()};
        int[] labelShape = {records.size(), possibleAnswers};

        return new Batch(
                Nd4j.create(featureData, featureShape),
                Nd4j.create(labelData, labelShape)
        );
    }

    /**
     * Ensures that we have a valid record reader to read from.
     *
     * @throws IOException          Gets thrown when the input file could not be read
     * @throws InterruptedException Gets thrown when the program is interrupted
     */
    private void ensureRecordReader() throws IOException, InterruptedException {
        if (reader != null) {
            return;
        }

        reader = new CSVRecordReader(1, ',');
        reader.initialize(new FileSplit(sourceFile));
    }

    /**
     * Performs one-hot encoding of the answer index.
     *
     * @param value The input value to encode
     * @return A vector where the element representing the value is set to one while the rest remains zero.
     */
    private INDArray oneHotEncode(int value) {
        INDArray output = Nd4j.create(possibleAnswers);
        output.put(0, value, 1.0f);

        return output;
    }
}
