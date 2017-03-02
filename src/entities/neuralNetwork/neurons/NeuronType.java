/**
 * 
 */
package entities.neuralNetwork.neurons;

import java.awt.image.BufferedImage;

import gClasses.GRessourcesCollector;

public enum NeuronType {

	LINEARE(Neuron.class, "Lineare", "lin.png"),
	CONSANT(BlankNeuron.class, "Consant", "blank.png"),
	SIGMOID(SigNeuron.class, "Sigmoid", "sig.png"),
	LOGARITHMIC(LnNeuron.class, "Logarithmic", "ln.png"),
	EXPONENTIAL(ExpNeuron.class, "Exponential", "exp.png"),
	SINUSOIDAL(PeriodicNeuron.class, "Sinusoidal", "sin.png");

	private Class<? extends Neuron> associateClass;
	private String name;
	private BufferedImage picture;

	private NeuronType(Class<? extends Neuron> associateClass, String name, String pictureFileName) {
		this.associateClass = associateClass;
		this.name = name;
		picture = GRessourcesCollector.getBufferedImage("resources/pictures/neurons/" + pictureFileName);
	}

	@Override
	public String toString() {
		return name;
	}

	public BufferedImage getPicture() {
		return picture;
	}

	public static NeuronType getEnumFromSting(String name) {
		NeuronType foundedNeuronType = null;
		for (NeuronType neuronType : NeuronType.values()) {
			if (neuronType.toString().equals(name)) {
				foundedNeuronType = neuronType;
			}
		}
		return foundedNeuronType;
	}

	public static NeuronType getEnumFromInstance(Neuron neuron) {
		NeuronType foundedNeuronType = null;
		for (NeuronType neuronType : NeuronType.values()) {
			if (neuron.getClass() == neuronType.associateClass) {
				foundedNeuronType = neuronType;
			}
		}
		return foundedNeuronType;
	}

	public Neuron getNewInstance() {
		Neuron newNeuron = null;

		try {
			newNeuron = associateClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			e1.printStackTrace();
		}

		return newNeuron;
	}

}
