package entities.neuralNetwork;

import java.util.Random;

import entities.neuralNetwork.neurons.Neuron;
import gClasses.DataAssociator;

public class Nerve {

	private Neuron source;

	private double coefficient;

	private double evolution;

	private double previousCoefficient;

	public Nerve(Neuron source) {
		this.source = source;

		coefficient = new Random().nextDouble() * 4 - 2;
		evolution = new Random().nextDouble() * 4 - 2;
		previousCoefficient = coefficient;
	}

	public Nerve(DataAssociator da, Perceptron per) {
		source = per.getNeuronById(da.getValueInt("source"));

		coefficient = Double.parseDouble(da.getValueString("coefficient"));
		previousCoefficient = Double.parseDouble(da.getValueString("previousCoefficient"));
		evolution = Double.parseDouble(da.getValueString("evolution"));
	}

	public double getFlow() {
		return source.getCharge() * coefficient;
	}

	public void evolve() {
		previousCoefficient = coefficient;
		coefficient += evolution;
	}

	private boolean progressing = true;

	public void reactToProgression() {

		if (progressing) {
			evolution *= 2;
		} else {
			evolution /= 2;
		}
		progressing = true;
	}

	public void reactToRegression() {
		coefficient = previousCoefficient;

		if (progressing) {
			evolution /= 4d;
		} else {
			evolution /= -2d;
		}
		progressing = false;
	}

	public DataAssociator toDataAssociator() {
		DataAssociator da = new DataAssociator();

		da.addValue("coefficient", Double.toString(coefficient));
		da.addValue("evolution", Double.toString(evolution));
		da.addValue("previousCoefficient", Double.toString(previousCoefficient));
		da.addValue("source", source.id);

		return da;
	}

	public double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(double coefficient) {
		this.coefficient = coefficient;
	}

	public void cleanInfinits() {
		coefficient = Math.max(coefficient, -Double.MAX_VALUE);
		coefficient = Math.min(coefficient, Double.MAX_VALUE);
		previousCoefficient = coefficient;
		previousCoefficient = Math.max(previousCoefficient, -Double.MAX_VALUE);
		previousCoefficient = Math.min(previousCoefficient, Double.MAX_VALUE);
	}

}
