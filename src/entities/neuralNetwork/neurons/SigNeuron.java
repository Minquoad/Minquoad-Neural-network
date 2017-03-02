package entities.neuralNetwork.neurons;

public class SigNeuron extends Neuron {

	public void proceed() {
		super.proceed();

		charge = 1 / (1 + Math.exp(-charge));
	}
}
