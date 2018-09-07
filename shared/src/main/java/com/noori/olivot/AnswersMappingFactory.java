package com.noori.olivot;

import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswersMappingFactory {
    public static Map<Integer, String> create(File inputFile) throws IOException, InterruptedException {
        try (CSVRecordReader reader = new CSVRecordReader(1, ',')) {
            reader.initialize(new FileSplit(inputFile));

            Map<Integer, String> answers = new HashMap<>();
            
            while(reader.hasNext()) {
                List<Writable> record = reader.next();

                // Note: The answer index needs a -1, so that we get an offset mapping.
                // this is required by the neural network. But instead of fixing it there, we're fixing it here.
                String anStr = new String(record.get(1).toString().getBytes(),"UTF-8");
                System.out.println("##### AnswersMappingFactory answer : "+anStr);
                answers.put(record.get(0).toInt() - 1, anStr);
            }
            
            return answers;
        }
    }
}
