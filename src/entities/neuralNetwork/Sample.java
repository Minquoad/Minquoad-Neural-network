package entities.neuralNetwork;

import java.util.LinkedList;

public class Sample {

	public double[] inputs = null;
	public double[] outputs = null;

	public Sample() {
	}

	public Sample(double[] inputs) {
		this();
		this.inputs = inputs;
	}

	public Sample(double[] inputs, double[] outputs) {
		this(inputs);
		this.outputs = outputs;
	}

	public boolean hasInputs() {
		return this.inputs != null;
	}

	public boolean hasOutputs() {
		return this.outputs != null;
	}

	static public Sample toSample(double[] rawData, int inputCount) {
		double[] inputs = new double[inputCount];
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = rawData[i];
		}
		return new Sample(inputs);
	}

	static public Sample toSample(double[] rawData, int inputCount, int outputCounts) {
		double[] inputs = new double[inputCount];
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = rawData[i];
		}
		double[] outputs = new double[outputCounts];
		for (int i = 0; i < outputs.length; i++) {
			outputs[i] = rawData[inputs.length + i];
		}
		return new Sample(inputs, outputs);
	}

	static public Sample[] toSample(double[][] rawData, int inputCount) {
		Sample[] samples = new Sample[rawData.length];
		for (int i = 0; i < samples.length; i++) {
			samples[i] = Sample.toSample(rawData[i], inputCount);
		}
		return samples;
	}

	static public Sample[] toSample(double[][] rawData, int inputCount, int outputCounts) {
		Sample[] samples = new Sample[rawData.length];
		for (int i = 0; i < samples.length; i++) {
			samples[i] = Sample.toSample(rawData[i], inputCount, outputCounts);
		}
		return samples;
	}

	static public Sample[] randomizeSamplesOrder(Sample[] samples) {
		SampleList sampleList = new SampleList();
		for (Sample sample : samples) {
			sampleList.add(sample);
		}

		Sample[] samplesInRandomOrder = new Sample[samples.length];
		for (int i = 0; i < samplesInRandomOrder.length; i++) {
			int randomSampleIndex = (int) (Math.random() * sampleList.size());
			samplesInRandomOrder[i] = sampleList.get(randomSampleIndex);
			sampleList.remove(randomSampleIndex);
		}
		return samplesInRandomOrder;
	}

	static public Sample[][] splitSamples(Sample[] samples, int partsCount) {

		Sample[][] samplesArray = new Sample[partsCount][];

		SampleList[] sampleLists = new SampleList[partsCount];
		for (int i = 0; i < partsCount; i++) {
			sampleLists[i] = new SampleList();
		}

		for (int i = 0; i < samples.length; i++) {
			sampleLists[i % sampleLists.length].add(samples[i]);
		}

		for (int i = 0; i < partsCount; i++) {
			samplesArray[i] = sampleLists[i].toArray();
		}

		return samplesArray;
	}

	private static class SampleList extends LinkedList<Sample> {
		@Override
		public Sample[] toArray() {
			Sample[] array = new Sample[this.size()];
			for (int i = 0; i < array.length; i++) {
				array[i] = this.get(i);
			}
			return array;
		}
	}

}
