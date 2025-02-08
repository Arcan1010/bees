package srebnikm.bee;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import srebnikm.bee.model.Input;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static final String DATA_DIR_PATH = "C:\\Users\\srebnikm\\studia\\repo\\bees\\beeCounter\\data\\";
    private static final String RESULTS_DIR_PATH = "C:\\Users\\srebnikm\\studia\\repo\\bees\\beeCounter\\result\\";
    private static final List<Integer> TRAINING_INDEXES = Arrays.asList(2, 3, 4, 5, 6, 8, 9, 13, 14, 15, 16, 17, 18, 20);
    private static final List<Integer> TESTING_INDEXES = Arrays.asList(0, 1, 7, 10, 12, 19);

    private static final int INPUT_NEURON_COUNT = 10000;
    private static final int OUTPUT_NEURON_COUNT = INPUT_NEURON_COUNT/2;
    private static final int HIDDEN_LAYERS_COUNT = 1;
    private static final int NEURONS_IN_HIDDEN_LAYER_COUNT = INPUT_NEURON_COUNT;

    public static void main(String[] args) {
        NeuralNetwork neuralNetwork = new NeuralNetwork(
                INPUT_NEURON_COUNT, OUTPUT_NEURON_COUNT, HIDDEN_LAYERS_COUNT, NEURONS_IN_HIDDEN_LAYER_COUNT, false);
        neuralNetwork.learn(0.2, 1, getTrainingSetInput(), getTrainingSetOutput());
        getTestingSet().forEach(set -> {
            System.out.println("Testing result");
            neuralNetwork.compute(set);
        });
    }

    public static List<List<Double>> getTrainingSetInput() {
        return TRAINING_INDEXES.stream()
                .map(index -> DATA_DIR_PATH + "data" + index + ".json")
                .map(Main::readFile)
                .map(Main::mapToInput)
//                .map(Input::getCollected)
                .map(input -> input.getCollectedCut(INPUT_NEURON_COUNT/2)) // TODO take whole files?
                .toList();
    }

    public static List<List<Double>> getTrainingSetOutput() {
        return TRAINING_INDEXES.stream()
                .map(index -> RESULTS_DIR_PATH + index + "_result.json")
                .map(Main::readFile)
                .map(Main::mapToList)
                .map(list -> list.subList(0, OUTPUT_NEURON_COUNT)) // TODO take whole files?
                .toList();
    }

    public static List<List<Double>> getTestingSet() {
        return TESTING_INDEXES.stream()
                .map(index -> DATA_DIR_PATH + "data" + index + ".json")
                .map(Main::readFile)
                .map(Main::mapToInput)
//                .map(Input::getCollected)
                .map(input -> input.getCollectedCut(INPUT_NEURON_COUNT/2)) // TODO take whole files?
                .toList();
    }

    private static List<Double> mapToList(byte[] inputFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            double[] array = objectMapper.readValue(inputFile, double[].class);
            return Arrays.stream(array).boxed().toList();
        } catch (IOException exception) {
            System.out.println("Something went wrong with json mapping");
            throw new RuntimeException(exception);
        }
    }

    private static Input mapToInput(byte[] inputFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(inputFile, Input.class);
        } catch (IOException exception) {
            System.out.println("Something went wrong with json mapping");
            throw new RuntimeException(exception);
        }
    }

    private static byte[] readFile(String path) {
        try(InputStream inputStream = new FileInputStream(path)) {
            return IOUtils.toByteArray(inputStream);
        } catch (Exception exception) {
            System.out.println("Something went wrong with reading file: " + path);
            throw new RuntimeException(exception);
        }
    }
}