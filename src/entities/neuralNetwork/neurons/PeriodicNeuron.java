package entities.neuralNetwork.neurons;

public class PeriodicNeuron extends Neuron {

	public void proceed() {
		super.proceed();

		charge = Math.sin(charge);
	}
}
