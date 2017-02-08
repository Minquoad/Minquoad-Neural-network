package entities.neuralNetwork.neurons;

import entities.neuralNetwork.Perceptron;
import gClasses.DataAssociator;

public class PeriodicNeuron extends Neuron {

	public PeriodicNeuron(Perceptron per) {
		super(per);
	}

	public PeriodicNeuron(DataAssociator da, Perceptron per) {
		super(da, per);
	}

	public void proceed() {
		super.proceed();

		charge = Math.sin(charge);
	}
}
