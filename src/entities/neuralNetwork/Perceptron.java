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

	private void proceed() {
		for (int i = 1; i < layers.size(); i++) {
			layers.get(i).proceed();
		}
	}

	private void proceed(Sample sample) {
		setInputs(sample);
		proceed();
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

	public void setInputs(Sample sample) {
		getFirstLayer().setCarges(sample.inputs);
	}

	public void updateOutputs(Sample sample) {
		proceed(sample);
		sample.outputs = getOutputs();
	}

	public void updateOutputs(Sample[] samples) {
		for (Sample sample : samples) {
			updateOutputs(sample);
		}
	}

	public double[] getOutputs() {

		Layer lastLayer = getLastLayer();

		int outputCount = lastLayer.getNeuroneCount();

		double[] outputs = new double[outputCount];

		for (int i = 0; i < outputCount; i++) {
			outputs[i] = lastLayer.getNeurone(i).getCharge();
		}
		
		return outputs;
	}

	private Layer getFirstLayer() {
		return layers.get(0);
	}

	private Layer getLastLayer() {
		return layers.get(layers.size() - 1);
	}

	public double getSquareError(Sample sample) {
		proceed(sample);

		Layer lastLayer = getLastLayer();
		int lastLayerNeuronesCount = lastLayer.getNeuroneCount();
		double squareError = 0;
		double[] sampleOutputs = sample.outputs;
		
		for (int i = 0; i < lastLayerNeuronesCount; i++) {
			double error = lastLayer.getNeurone(i).getCharge() - sampleOutputs[i];
			squareError += error*error;
		}
		
		return squareError;
	}

	public double getSquareErrorSum(Sample[] samples) {
		double sum = 0;
		for (Sample sample : samples) {
			sum += this.getSquareError(sample);
		}
		return sum;
	}

	public double getWeightedSquareError(Sample[] samples) {
		double sum = 0;
		for (Sample sample : samples) {
			sum += this.getSquareError(sample);
		}
		return sum / (double) samples.length;
	}
	
	public void cleenInfinits(Sample[] samples) {
		double maxOutput = Math.sqrt(Double.MAX_VALUE / this.getOutputCount() / samples.length);
		for (Sample currentSample : samples) {
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
		return getLastLayer().getNeuroneCount();
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
