package com.example.sweater.service;

import com.example.sweater.domain.Info;
import com.example.sweater.domain.IrisDto;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassificationService {
    private static final int CLASSES_COUNT = 3;
    private static final int FEATURES_COUNT = 4;
    private static final Logger logger = LoggerFactory.getLogger(ClassificationService.class);

    private DataSet allData;
    private MultiLayerNetwork model;


    public List<Info> trainNetwork() {
        try {
            try (RecordReader recordReader = new CSVRecordReader(0, ',')) {
                recordReader.initialize(new FileSplit(new ClassPathResource("iris.txt").getFile()));

                DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, 150, FEATURES_COUNT, CLASSES_COUNT);
                allData = iterator.next();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        allData.shuffle(42);
        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(allData);
        normalizer.transform(allData);

        SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.65);
        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .iterations(1000)
                .activation(Activation.TANH)
                .weightInit(WeightInit.XAVIER)
                .regularization(true)
                .learningRate(0.1).l2(0.0001)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(FEATURES_COUNT).nOut(3)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(3).nOut(3)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(3).nOut(CLASSES_COUNT).build())
                .backpropType(BackpropType.Standard).pretrain(false)
                .build();

        model = new MultiLayerNetwork(configuration);
        model.init();
        model.fit(trainingData);

        INDArray output = model.output(testData.getFeatures());

        Evaluation eval = new Evaluation(CLASSES_COUNT);
        eval.eval(testData.getLabels(), output);
        String[] labels = eval.stats().split("\n");

        return Arrays.stream(labels).map(Info::new).collect(Collectors.toList());
    }

    public List<Info> classifyFlower(IrisDto irisDto) {
        writeToFile(irisDto);

        try {
            try (RecordReader recordReader = new CSVRecordReader(0, ',')) {
                recordReader.initialize(new FileSplit(new ClassPathResource("example.txt").getFile()));

                DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, 1, FEATURES_COUNT, CLASSES_COUNT);
                allData = iterator.next();
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
        }

        allData.shuffle(42);
        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(allData);
        normalizer.transform(allData);

        DataSet testData = allData;
        INDArray output = model.output(testData.getFeatures());

        Evaluation eval = new Evaluation(CLASSES_COUNT);
        eval.eval(testData.getLabels(), output);
        String[] labels = eval.stats().split("\n");

        return Arrays.stream(labels).map(Info::new).collect(Collectors.toList());
    }

    private void writeToFile(IrisDto irisDto) {
        try (FileWriter writer = new FileWriter("example.txt", false)) {
            writer.write(irisDto.toString());
            writer.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
