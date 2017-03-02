package entities.neuralNetwork.neurons;

public class LnNeuron extends Neuron {

	public void proceed() {
		super.proceed();

		charge = Math.log(Math.abs(charge));
	}
}
