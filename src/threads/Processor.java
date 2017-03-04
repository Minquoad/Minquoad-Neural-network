package threads;

import java.util.Arrays;

import entities.neuralNetwork.Perceptron;
import utilities.Controler;

public class Processor extends Thread {

	private Controler controler;
	private Perceptron per;
	private double[][] data;

	private int valueExtendedCount = 1;
	private boolean curveData = false;

	public Processor(Controler controler, Perceptron per, double[][] data) {
		this.controler = controler;
		this.per = per;
		this.data = data;
	}

	public void run() {
		double[][] results = null;

		controler.appendProcessingInfo("\n" + "Processing started");

		if (curveData) {

			double[] buff = new double[data.length + valueExtendedCount];
			for (int i = 0; i < data.length; i++) {
				buff[i] = data[i][0];
			}
			
			for (int i = data.length; i < buff.length; i++) {
				double[] inputs = Arrays.copyOfRange(buff, i - per.getInputCount(), i);
				double[] outputs = per.getOutputs(inputs);
				buff[i] = outputs[0];
			}

			double[] curveResults = Arrays.copyOfRange(buff, data.length, buff.length);
			results = new double[curveResults.length][1];
			for (int i = 0; i < curveResults.length; i++) {
				results[i][0] = curveResults[i];
			}
			
		} else {
			results = per.getResults(data);
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
