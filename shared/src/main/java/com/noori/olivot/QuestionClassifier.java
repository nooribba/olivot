package com.noori.olivot;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Classifies questions to possible answers.
 */
public final class QuestionClassifier {
    private final MultiLayerNetwork network;
    private final TextVectorizer vectorizer;
    private final Map<Integer, String> answers;
    private static Logger logger = Logger.getLogger(QuestionClassifier.class.getName());

    /**
     * Initializes a new instance of {@link QuestionClassifier}
     *
     * @param network Neural network to use for classification
     */
    public QuestionClassifier(MultiLayerNetwork network,
                              TextVectorizer vectorizer,
                              Map<Integer, String> answers) {
        this.network = network;
        this.vectorizer = vectorizer;
        this.answers = answers;
    }

    /**
     * Trains the classifier on questions in the specified input file
     *
     * @param inputFile Input file to use
     */
    public void fit(File inputFile) throws IOException, InterruptedException {
        QuestionDataSource dataSource = new QuestionDataSource(
                inputFile, vectorizer, 32, answers.size());

        for (int epoch = 0; epoch < 500; epoch++) {
            while (dataSource.hasNext()) {
                Batch nextBatch = dataSource.next();
                network.fit(nextBatch.getFeatures(), nextBatch.getLabels());
            }

            dataSource.reset();
        }
    }

    /**
     * Scores the classifier
     *
     * @param inputFile The input file to use for loading the validation set
     * @return Returns the overall accuracy for the classifier
     * @throws IOException          Gets thrown when the input file could not be read
     * @throws InterruptedException Gets thrown when validation is interrupted
     */
    public double score(File inputFile) throws IOException, InterruptedException {
        QuestionDataSource dataSource = new QuestionDataSource(inputFile, vectorizer, 32, answers.size());

        INDArray predictions = null;
        INDArray actuals = null;

        while (dataSource.hasNext()) {
            Batch batch = dataSource.next();

            INDArray predictedOutput = network.output(batch.getFeatures());

            if (predictions != null) {
                predictions = Nd4j.concat(0, predictions, predictedOutput);
            } else {
                predictions = predictedOutput;
            }

            if (actuals != null) {
                actuals = Nd4j.concat(0, actuals, batch.getLabels());
            } else {
                actuals = batch.getLabels();
            }
        }

        Evaluation evaluation = new Evaluation(answers.size());
        evaluation.eval(actuals, predictions);

        return evaluation.accuracy();
    }

    /**
     * Makes a prediction of the possible answer based on the given input
     *
     * @param text The question the user asked
     * @return The highest ranking answer
     */
    public String predict(String text) {
<<<<<<< HEAD
=======
    	logger.info("##### text : "+text);
>>>>>>> 6dce4099da59e36f4706001e422291bd9bd00dad
        INDArray prediction = network.output(vectorizer.transform(text));
    	INDArray prediction_test = vectorizer.transform(text);
        //logger.info("##### vectorizer.transform : "+vectorizer.transform(text).toString());
        int answerIndex = prediction.argMax(1).getInt(0,0);
        
<<<<<<< HEAD
        logger.info("##### prediction getrow(0):"+prediction.getRow(0));
        logger.info("##### prediction:"+prediction);
        logger.info("##### prediction_test:"+prediction_test);
        
        logger.info("##### answerIndex : "+answerIndex);
        logger.info("##### test answerIndex : "+prediction_test.argMax(1).getInt(0,0));
        String result = answers.get(answerIndex);
        if(result == null ) {
        	result = "I should study more. TT.TT Please ask again.";
        }else {
        	result = setEncoding(result,"UTF-8");
        }
        
        return result;
=======
        logger.info("##### answerIndex : "+answerIndex);
        logger.info("##### prediction getrow(0):"+prediction.getRow(0));
        logger.info("##### answers.get("+answerIndex+"):"+answers.get(answerIndex));
        logger.info("##### answers.get(0):"+answers.get(0));
        logger.info("##### answers.get(1):"+answers.get(1));
        logger.info("##### answers.get(2):"+answers.get(2));
        logger.info("##### answers.get(3):"+answers.get(3));
        logger.info("##### answers.get(4):"+answers.get(4));
        logger.info("##### answers.get(5):"+answers.get(5));
        return answers.get(answerIndex);
>>>>>>> 6dce4099da59e36f4706001e422291bd9bd00dad
    }

    /**
     * Saves the trained neural network to disk
     *
     * @param outputFile Output file to store the model to
     */
    public void save(File outputFile) throws IOException {
        ModelSerializer.writeModel(this.network, outputFile, false);
    }
    
    public String setEncoding(String text, String enc) {
    	String result = text;
    	try {
			result = new String(text.getBytes(), enc);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return result;
    }
    
    public void checkNetworkOutput(INDArray arr) {
    	INDArray output = this.network.output(arr);
    	System.out.println("##### network output : "+output);
    	//logger.info("##### network output : "+output);
    	int answerIndex = output.argMax(1).getInt(0,0);
        System.out.println("##### network output answerIndex : "+answerIndex);
        logger.info("##### network output answerIndex : "+answerIndex);
        logger.info("##### network answer text : "+setEncoding(answers.get(answerIndex),"UTF-8"));
    }
}
