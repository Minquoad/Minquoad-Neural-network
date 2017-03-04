package threads;

import entities.neuralNetwork.Perceptron;
import utilities.Controler;

public class Processor extends Thread {

	private Controler controler;
	private Perceptron per;
	private double[][] data;

	public Processor(Controler controler, Perceptron per, double[][] data) {
		this.controler = controler;
		this.per = per;
		this.data = data;
	}

	public void run() {
		controler.appendProcessingInfo("\n" + "Processing started");
		double[][] results = per.getResults(data);
		controler.appendProcessingInfo("\n" + "Processing ended");

		controler.processingEnded(results);
	}

}
