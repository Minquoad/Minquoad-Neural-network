package entities.neuralNetwork;

import java.util.ArrayList;

import entities.neuralNetwork.neurons.Neuron;
import entities.neuralNetwork.neurons.NeuronType;
import gClasses.DataAssociator;

public class Layer {

	private Perceptron per = null;

	private ArrayList<Neuron> neurons = new ArrayList<Neuron>();

	public Layer() {}

	public Layer(DataAssociator da, Perceptron per) {
		this.setPerceptron(per);

		int neuroneCount = da.getValueInt("neuroneCount");
		for (int i = 0; i < neuroneCount; i++) {
			DataAssociator neuronDataAssociator = da.getValueDataAssociator(i);

			String neuTypeName = neuronDataAssociator.getValueString("type");

			Neuron newNeuron = NeuronType.getEnumFromSting(neuTypeName).getNewInstance();
			newNeuron.buildFromDataAssociator(neuronDataAssociator, per);

			this.addNeuron(newNeuron);
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
		this.addNeuron(this.getNeuroneCount(), newNeuron);
	}

	public void addNeuron(int i, Neuron neuron) {
		invalidate();
		neurons.add(i, neuron);
		neuron.setPerceptron(per);
	}

	public void removeNeuron(int i) {
		invalidate();
		neurons.remove(i);
	}

	public void removeNeuron() {
		this.removeNeuron(this.getNeuroneCount()-1);
	}

	public DataAssociator toDataAssociator() {
		DataAssociator da = new DataAssociator();

		da.addValue("neuroneCount", getNeuroneCount());
		for (int i = 0; i < getNeuroneCount(); i++) {
			da.addValue(i, getNeurone(i).toDataAssociator());
		}
		return da;
	}

	public void setPerceptron(Perceptron per) {
		invalidate();
		this.per = per;
		for (Neuron neuron : neurons) {
			neuron.setPerceptron(per);
		}
	}

	public void invalidate() {
		if (per != null) {
			per.invalidate();
		}
	}

	public boolean isValidable() {
		return this.getNeuroneCount() != 0;
	}

	public ArrayList<Nerve> getAllNervs() {
		ArrayList<Nerve> nerves = new ArrayList<Nerve>();
		for (Neuron neuron : neurons) {
			nerves.addAll(neuron.getAllNervs());
		}
		return nerves;
	}

}
