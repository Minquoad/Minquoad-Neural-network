package entities.neuralNetwork.neurons;

import entities.neuralNetwork.Perceptron;
import gClasses.DataAssociator;

public class SigNeuron extends Neuron {

	public SigNeuron(Perceptron per) {
		super(per);
	}

	public SigNeuron(DataAssociator da, Perceptron per) {
		super(da, per);
	}

	public void proceed() {
		super.proceed();

		charge = 1 / (1 + Math.exp(-charge));
	}
}
