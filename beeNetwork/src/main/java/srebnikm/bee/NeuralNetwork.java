package srebnikm.bee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NeuralNetwork {

    private final List<Neuron> inputLayer = new ArrayList<>();
    private final List<List<Neuron>> hiddenLayers = new ArrayList<>();
    private final List<Neuron> outputLayer = new ArrayList<>();
    private final boolean bias;

    public NeuralNetwork(int inputNeuronCount, int outputNeuronCount, int hiddenLayersCount, int neuronsInHiddenLayerCount,
                         boolean bias) {
        this.bias = bias;
        initializeInputLayer(inputNeuronCount);
        initializeHiddenLayers(hiddenLayersCount, neuronsInHiddenLayerCount);
        initializeOutputLayer(outputNeuronCount, hiddenLayers.get(hiddenLayers.size() - 1), bias);
    }

    private void initializeOutputLayer(int outputNeuronCount, List<Neuron> lastHiddenLayer, boolean bias) {
        addNeuronsToLayer(outputNeuronCount, outputLayer, lastHiddenLayer);
    }

    private void initializeHiddenLayers(int hiddenLayersCount, int neuronsInHiddenLayerCount) {
        for(int i = 0; i< hiddenLayersCount; i++)
        {
            List<Neuron> hiddenLayer = getHiddenLayer(i, neuronsInHiddenLayerCount);
            hiddenLayers.add(hiddenLayer);
        }
    }

    private void initializeInputLayer(int inputNeuronCount) {
        for(int i = 0; i< inputNeuronCount; i++)
        {
            inputLayer.add(new Neuron(0));
        }
    }

    private List<Neuron> getHiddenLayer(int layerIndex, int neuronsInHiddenLayerCount) {
        List<Neuron> hiddenLayer = new ArrayList<>();
        if (layerIndex == 0) {
            addNeuronsToLayer(neuronsInHiddenLayerCount, hiddenLayer, inputLayer);
        } else {
            addNeuronsToLayer(neuronsInHiddenLayerCount, hiddenLayer, hiddenLayers.get(layerIndex -1));
        }
        return hiddenLayer;
    }

    private void addNeuronsToLayer(int count, List<Neuron> targetLayer, List<Neuron> parentNeurons) {
        for(int j = 0; j< count - 1; j++) {
            Neuron newNeuron = new Neuron(parentNeurons, bias);
            targetLayer.add(newNeuron);
        }
    }

    public void compute(List<Double> inputToCompute) {
        System.out.println("Network is computing...");
        for(int i=0; i<inputLayer.size(); i++)
        {
            inputLayer.get(i).setValue(inputToCompute.get(i));
        }
        //System.out.println("Input: " + Arrays.toString(inputLayer.stream().map(Neuron::getValue).toArray()));
        //System.out.println("Output: " + Arrays.toString(outputLayer.stream().map(Neuron::getValue).map(o -> o * 2).toArray()));
    }

    public void learn(double learningRate, int numberOfEras,
                      List<List<Double>> trainingInput, List<List<Double>> trainingOutput) {
        System.out.println("*** Network is learning...");
        for(int i=0; i<numberOfEras; i++)
        {
            processEra(i, learningRate, trainingInput, trainingOutput);
        }
    }

    private void processEra(int currentEra, double learningRate, List<List<Double>> trainingInput, List<List<Double>> trainingOutput) {
        System.out.println("Era nr." + currentEra);

        List<Integer> shuffleList = new ArrayList<>();
        for(int j=0; j<trainingInput.size(); j++)
        {
            shuffleList.add(j);
        }
        Collections.shuffle(shuffleList);

        for(int j=0; j<trainingInput.size(); j++)
        {
            trainSingleInput(j, shuffleList.get(j), learningRate, trainingInput, trainingOutput);
        }
    }

    private void trainSingleInput(int iteration, int pickedInput, double learningRate, List<List<Double>> trainingInput, List<List<Double>> trainingOutput) {
        System.out.println("Iteration nr." + iteration);

        compute(trainingInput.get(pickedInput));

        double sum = 0;
        for(int i=0; i<outputLayer.size(); i++)
        {
            sum = sum + (outputLayer.get(i).getValue() - trainingOutput.get(pickedInput).get(i)) *
                    (outputLayer.get(i).getValue() - trainingOutput.get(pickedInput).get(i));
        }
        System.out.println("Average cost: " + sum);

        for(int k=0; k<outputLayer.size(); k++)
        {
            outputLayer.get(k).giveExpected(trainingOutput.get(pickedInput).get(k));
        }

        inputLayer.forEach(Neuron::updateError);

        if(bias)
        {
            throw new RuntimeException("No bias for now");
            // TODO here add bias error processing
        }

        outputLayer.forEach(neuron -> neuron.updateWeights(learningRate));
    }
}
