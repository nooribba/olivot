package com.noori.olivot;

import org.nd4j.linalg.api.ndarray.INDArray;

public class Batch {
    private final INDArray features;
    private final INDArray labels;

    public Batch(INDArray features, INDArray labels) {
        this.features = features;
        this.labels = labels;
    }

    public INDArray getFeatures() {
        return features;
    }

    public INDArray getLabels() {
        return labels;
    }
}
