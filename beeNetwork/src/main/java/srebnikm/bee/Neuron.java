package srebnikm.bee;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Neuron {

    private double value;
    private double error;
    private List<Neuron> parentNeurons;
    private final List<Neuron> childNeurons = new ArrayList<>();
    private final List<Double> factor = new ArrayList<>();

    public Neuron(double value) {
        this.value = value;
    }

    public Neuron(List<Neuron> parentNeurons, boolean isBias) {
        this.parentNeurons = parentNeurons;
        parentNeurons.forEach(parent -> {
            parent.addChildNeuron(this);
            factor.add(2 * new Random().nextDouble() - 1);
        });
        if(isBias)
        {
            parentNeurons.add(new Neuron(1));
            factor.add(2 * new Random().nextDouble() - 1);
        }
    }

    public double getValue() {
        if(parentNeurons == null || parentNeurons.isEmpty()) {
            return value;
        }
        double result = 0;
        for (int i = 0; i < factor.size(); i++)
        {
            result = result + parentNeurons.get(i).getValue() * factor.get(i);
        }
        this.value = activation(result);
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void giveExpected(double expected) {
        error = expected - value;
    }

    public void addChildNeuron(Neuron neuron) {
        childNeurons.add(neuron);
    }

    public double updateError() {
        if (childNeurons.isEmpty())
        {
            return error;
        }
        error = 0;
        childNeurons.forEach(childNeuron -> {
            int j = 0;
            while (childNeuron.parentNeurons.get(j) != this) {
                j++;
            }
            error = error + childNeuron.updateError() * childNeuron.factor.get(j);
        });
        return error;
    }

    public void updateWeights(double learningRate) {
        if(parentNeurons == null || parentNeurons.isEmpty())
        {
            return;
        }
        for(int i=0; i<parentNeurons.size(); i++)
        {
            factor.set(i, factor.get(i) + (learningRate * error * parentNeurons.get(i).value * dActivation(value)));
            parentNeurons.get(i).updateWeights(learningRate);
        }
    }

    public Neuron getBias() {
        return parentNeurons.get(parentNeurons.size()-1);
    }

    private double activation(double x) {
        return (1 / (1 + Math.exp((-1) * x)));
    }

    private double dActivation(double x) {
        return (x * (1.0 - x));
    }
}
