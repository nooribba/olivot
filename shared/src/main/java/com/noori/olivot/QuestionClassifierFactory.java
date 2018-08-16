package com.noori.olivot;

import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * This factory can be used to create preconfigured instances of the {@link QuestionClassifier}.
 * It will create a new multi-layer neural network and produce a new instance of {@link QuestionClassifier} with it.
 */
public class QuestionClassifierFactory {
    /**
     * Creates a new instance of the question classifier based on the provided parameters
     *
     * @param vectorizer The question vectorizer to use
     * @param answers    The set of possible answers
     * @return Returns a new instance of {@link QuestionClassifier}
     */
    public static QuestionClassifier create(
            TextVectorizer vectorizer,
            Map<Integer, String> answers) {

        MultiLayerNetwork network = createNeuralNetwork(
                vectorizer.vocabularySize(),
                answers.size());

        return new QuestionClassifier(network, vectorizer, answers);
    }

    /**
     * Restores the question classifier from file
     *
     * @param inputFile  File containing the serialized neural network
     * @param vectorizer Vectorizer to use in combination with the classifier
     * @param answers    Answer mapping for decoding the answers given by the neural network
     * @return Returns the restored question classifier
     * @throws IOException Gets thrown when the input file could not be read
     */
    public static QuestionClassifier restore(File inputFile,
                                             TextVectorizer vectorizer,
                                             Map<Integer, String> answers) throws IOException {

        MultiLayerNetwork network = ModelSerializer.restoreMultiLayerNetwork(inputFile);

        return new QuestionClassifier(network, vectorizer, answers);
    }

    private static MultiLayerNetwork createNeuralNetwork(int inputLayerSize, int outputLayerSize) {
        MultiLayerConfiguration networkConfiguration = new NeuralNetConfiguration.Builder()
                .seed(1337)
                .updater(new RmsProp(0.01))
                .list()
                .layer(0, new VariationalAutoencoder.Builder()
                        .nIn(inputLayerSize).nOut(1024)
                        .encoderLayerSizes(1024, 512, 256, 128)
                        .decoderLayerSizes(128, 256, 512, 1024)
                        .lossFunction(Activation.RELU, LossFunctions.LossFunction.MSE)
                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                        .dropOut(0.8)
                        .build())
                .layer(1, new OutputLayer.Builder()
                        .nIn(1024).nOut(outputLayerSize)
                        .activation(Activation.SOFTMAX)
                        .lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .build())
                .pretrain(true)
                .backprop(true)
                .build();

        MultiLayerNetwork network = new MultiLayerNetwork(networkConfiguration);

        network.setListeners(new ScoreIterationListener(1));

        network.init();

        return network;
    }
}
