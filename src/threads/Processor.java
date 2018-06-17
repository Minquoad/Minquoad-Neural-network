package threads;

import java.util.Arrays;

import entities.neuralNetwork.Perceptron;
import entities.neuralNetwork.Sample;
import utilities.Controller;

public class Processor extends Thread {

	private Controller controler;
	private Perceptron per;
	private Sample[] samples;

	private int valueExtendedCount = 1;
	private boolean curveData = false;

	public Processor(Controller controler, Perceptron per, Sample[] samples) {
		this.controler = controler;
		this.per = per;
		this.samples = samples;
	}

	public void run() {
		double[][] results = null;

		controler.appendProcessingInfo("\n" + "Processing started");

		if (curveData) {

			double[][] data = new double[samples.length][1];
			for (int i = 0; i < data.length; i++) {
				data[i][0] = samples[i].inputs[0];
			}
			
			double[] buff = new double[data.length + valueExtendedCount];
			for (int i = 0; i < data.length; i++) {
				buff[i] = data[i][0];
			}
			
			for (int i = data.length; i < buff.length; i++) {
				double[] inputs = Arrays.copyOfRange(buff, i - per.getInputCount(), i);
				Sample sample = new Sample(inputs);
				per.updateOutputs(sample);
				double[] outputs = sample.outputs;
				buff[i] = outputs[0];
			}

			double[] curveResults = Arrays.copyOfRange(buff, data.length, buff.length);
			results = new double[curveResults.length][1];
			for (int i = 0; i < curveResults.length; i++) {
				results[i][0] = curveResults[i];
			}
			
		} else {
			per.updateOutputs(samples);
			results = new double[samples.length][];
			for (int i = 0; i < results.length; i++) {
				results[i] = samples[i].outputs;
			}
		}


		controler.appendProcessingInfo("\n" + "Processing ended");
		controler.processingEnded(results);
	}

	public void setCurveData(boolean curveData) {
		this.curveData = curveData;
	}

	public boolean getCurveData() {
		return curveData;
	}

	public int getValueExtendedCount() {
		return valueExtendedCount;
	}

	public void setValueExtendedCount(int valueExtendedCount) {
		this.valueExtendedCount = valueExtendedCount;
	}

}
