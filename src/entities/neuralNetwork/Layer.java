package entities.neuralNetwork;

import java.util.ArrayList;

import entities.neuralNetwork.neurons.BlankNeuron;
import entities.neuralNetwork.neurons.ExpNeuron;
import entities.neuralNetwork.neurons.LnNeuron;
import entities.neuralNetwork.neurons.Neuron;
import entities.neuralNetwork.neurons.PeriodicNeuron;
import entities.neuralNetwork.neurons.SigNeuron;
import gClasses.DataAssociator;

public class Layer {

	private Perceptron per;

	private ArrayList<Neuron> neurons = new ArrayList<Neuron>();

	public Layer(Perceptron per) {
		this.per = per;
	}

	public Layer(DataAssociator da, Perceptron per) {

		this.per = per;

		int neuroneCount = da.getValueInt("neuroneCount");
		for (int i = 0; i < neuroneCount; i++) {

			DataAssociator neuronDataAssociator = da.getValueDataAssociator(i);

			String neuClass = neuronDataAssociator.getValueString("class");

			if (Neuron.class.toString().equals(neuClass))
				neurons.add(new Neuron(neuronDataAssociator, per));
			if (SigNeuron.class.toString().equals(neuClass))
				neurons.add(new SigNeuron(neuronDataAssociator, per));
			if (LnNeuron.class.toString().equals(neuClass))
				neurons.add(new LnNeuron(neuronDataAssociator, per));
			if (ExpNeuron.class.toString().equals(neuClass))
				neurons.add(new ExpNeuron(neuronDataAssociator, per));
			if (BlankNeuron.class.toString().equals(neuClass))
				neurons.add(new BlankNeuron(neuronDataAssociator, per));
			if (PeriodicNeuron.class.toString().equals(neuClass)) {
				neurons.add(new PeriodicNeuron(neuronDataAssociator, per));
			}

		}
	}

	public Neuron getNeurone(int i) {
		return neurons.get(i);
	}

	public int getNeuroneCount() {
		return neurons.size();
	}

	public void proceed() {
		for (Neuron neuron : neurons) {
			neuron.proceed();
		}
	}

	public void proceedLimitNeuronOutput(double maxOutput) {
		for (Neuron neuron : neurons) {
			neuron.proceedLimitNeuronOutput(maxOutput);
		}
	}

	public void addNeuron(Neuron newNeuron) {
		per.invalidate();
		neurons.add(newNeuron);
	}

	public void addNeuron(int i, Neuron newNeuron) {
		per.invalidate();
		neurons.add(i, newNeuron);
	}

	public void removeNeuron(int i) {
		per.invalidate();
		neurons.remove(i);
	}

	public DataAssociator toDataAssociator() {
		DataAssociator da = new DataAssociator();

		da.addValue("neuroneCount", getNeuroneCount());
		for (int i = 0; i < getNeuroneCount(); i++) {
			da.addValue(i, getNeurone(i).toDataAssociator());
		}
		return da;
	}

}
