package entities.neuralNetwork;

import java.util.ArrayList;

import entities.neuralNetwork.neurons.Neuron;
import gClasses.DataAssociator;

public class Perceptron {

	private ArrayList<Layer> layers = new ArrayList<Layer>();

	private boolean valid = false;

	private int inputCount = 0;

	public Perceptron() {}

	public Perceptron(DataAssociator da) throws Exception {
		if (da != null && da.exists("inputCount") && da.exists("valid") && da.exists("layerCount")) {
			inputCount = da.getValueInt("inputCount");

			int layerCount = da.getValueInt("layerCount");
			for (int i = 0; i < layerCount; i++) {
				layers.add(new Layer(da.getValueDataAssociator(i), this));
			}

			valid = Boolean.parseBoolean(da.getValueString("valid"));
		} else {
			throw new Exception("DataAssociator does not contains the required feilds");
		}
	}

	public ArrayList<Nerve> getAllNerve() {
		ArrayList<Nerve> nerves = new ArrayList<Nerve>();
		for (Layer layer : layers) {
			nerves.addAll(layer.getAllNervs());
		}
		return nerves;
	}

	public boolean isValidable() {
		boolean hasOnlyValidableLayer = true;
		for (int i = 0; i < layers.size(); i++) {
			hasOnlyValidableLayer &= layers.get(i).isValidable();
		}
		return layers.size() >= 2 && hasOnlyValidableLayer;
	}

	public void validate() {
		if (this.isValidable()) {

			for (Layer layer : layers) {
				layer.setPerceptron(this);
			}
			
			for (int i = 1; i < layers.size(); i++) {
				Layer iLayer = layers.get(i);
				for (int j = 0; j < iLayer.getNeuroneCount(); j++) {
					iLayer.getNeurone(j).removeAllNerves();
				}
			}

			for (int i = 1; i < layers.size(); i++) {
				for (int j = 0; j < layers.get(i).getNeuroneCount(); j++) {
					layers.get(i).getNeurone(j).linkTo(layers.get(i - 1));
				}
			}
			
			valid = true;
		}
	}

	public void addLayer() {
		this.addLayer(new Layer());
	}

	public void addLayer(int i) {
		this.addLayer(i, new Layer());
	}

	public void addLayer(Layer newLayer) {
		addLayer(this.getLayerCount(), newLayer);
	}

	public void addLayer(int i, Layer newLayer) {
		invalidate();
		layers.add(i, newLayer);
		newLayer.setPerceptron(this);
	}

	public void removeLayer(int i) {
		invalidate();
		layers.remove(i);
	}

	public void proceed() {
		for (int i = 1; i < layers.size(); i++) {
			layers.get(i).proceed();
		}
	}

	private void proceedLimitNeuronOutput(double maxOutput) {
		for (int i = 1; i < layers.size(); i++) {
			layers.get(i).proceedLimitNeuronOutput(maxOutput);
		}
	}

	public void removeAllLayer() {
		invalidate();
		while (!layers.isEmpty()) {
			layers.remove(0);
		}
	}

	public void setInputs(double[] inputs) {

		Layer firstLayer = layers.get(0);

		for (int i = 0; i < inputCount; i++) {
			firstLayer.getNeurone(i).setCharge(inputs[i]);
		}
	}

	public double[][] getResults(double[][] samples) {
		double[][] results = new double[samples.length][this.getOutputCount()];

		for (int i = 0; i < samples.length; i++) {
			results[i] = this.getOutputs(samples[i]);
		}

		return results;
	}

	public double[] getOutputs(double[] sample) {
		this.setInputs(sample);
		this.proceed();
		return this.getOutputs();
	}

	public double[] getOutputs() {

		Layer lastLayer = layers.get(layers.size() - 1);

		int outputCount = lastLayer.getNeuroneCount();

		double[] outputs = new double[outputCount];

		for (int i = 0; i < outputCount; i++) {
			outputs[i] = lastLayer.getNeurone(i).getCharge();
		}
		return outputs;
	}

	public double getSquareError(double[][] samples) {

		double squareError = 0;

		Layer lastLayer = layers.get(layers.size() - 1);
		int lastLayerNeuronesCount = lastLayer.getNeuroneCount();

		for (double[] currentSample : samples) {

			this.setInputs(currentSample);

			this.proceed();

			for (int j = 0; j < lastLayerNeuronesCount; j++) {
				squareError += Math.pow(lastLayer.getNeurone(j).getCharge() - currentSample[j + inputCount], 2);
			}
		}

		return squareError;
	}

	public void cleenInfinits(double[][] samples) {
		double maxOutput = Math.sqrt(Double.MAX_VALUE / this.getOutputCount() / samples.length);
		for (double[] currentSample : samples) {
			this.setInputs(currentSample);
			this.proceedLimitNeuronOutput(maxOutput);
		}
	}

	public void setInputCount(int inputCount) {
		invalidate();
		this.inputCount = inputCount;
	}

	public int getInputCount() {
		return inputCount;
	}

	public Layer getLayer(int i) {
		return layers.get(i);
	}

	public int getLayerCount() {
		return layers.size();
	}

	public int getOutputCount() {
		return layers.get(layers.size() - 1).getNeuroneCount();
	}

	public boolean isValid() {
		return valid;
	}

	public void invalidate() {
		valid = false;
	}

	public DataAssociator toDataAssociator() {
		DataAssociator da = new DataAssociator();

		int lastId = 0;

		for (Layer layer : layers) {
			for (int i = 0; i < layer.getNeuroneCount(); i++) {
				layer.getNeurone(i).id = ++lastId;
			}
		}

		da.addValue("inputCount", inputCount);
		da.addValue("valid", Boolean.toString(valid));

		da.addValue("layerCount", getLayerCount());
		for (int i = 0; i < getLayerCount(); i++) {
			da.addValue(i, getLayer(i).toDataAssociator());
		}
		return da;
	}

	public Neuron getNeuronById(int id) {
		Neuron neu = null;
		for (Layer layer : layers) {
			for (int i = 0; i < layer.getNeuroneCount(); i++) {
				if (layer.getNeurone(i).id == id) {
					neu = layer.getNeurone(i);
				}
			}
		}
		return neu;
	}

	public Perceptron duplicate() {
		try {
			return new Perceptron(this.toDataAssociator());
		} catch (Exception e) {
			return null;
		}
	}

}
