package entities.neuralNetwork.neurons;

import entities.neuralNetwork.Perceptron;
import gClasses.DataAssociator;

public class LnNeuron extends Neuron {

	public LnNeuron(Perceptron per) {
		super(per);
	}

	public LnNeuron(DataAssociator da, Perceptron per) {
		super(da, per);
	}

	public void proceed() {
		super.proceed();

		charge = Math.log(Math.abs(charge));
	}
}
