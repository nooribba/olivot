package com.noori.olivot;

import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.text.sentenceiterator.BaseSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

public class CSVSentenceIterator extends BaseSentenceIterator {
    private final CSVRecordReader recordReader;

    public CSVSentenceIterator(File inputFile) throws Exception {
        recordReader = new CSVRecordReader(1, ',');
        recordReader.initialize(new FileSplit(inputFile));
    }

    @Override
    public String nextSentence() {
        if (!recordReader.hasNext()) {
            throw new NoSuchElementException();
        }

        String rawSentence = recordReader.next().get(1).toString();

        SentencePreProcessor preProcessor = getPreProcessor();

        if (preProcessor != null) {
            return preProcessor.preProcess(rawSentence);
        }

        return rawSentence;
    }

    @Override
    public boolean hasNext() {
        return recordReader.hasNext();
    }

    @Override
    public void reset() {
        recordReader.reset();
    }
}
