package entities.neuralNetwork.neurons;

import entities.neuralNetwork.Perceptron;
import gClasses.DataAssociator;

public class BlankNeuron extends Neuron {

	public BlankNeuron(Perceptron per) {
		super(per);
	}

	public BlankNeuron(DataAssociator da, Perceptron per) {
		super(da, per);
	}

	public void proceed() {}

	public void linkTo(Neuron predecessorNeuron) {}

}
