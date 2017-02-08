package entities.neuralNetwork.neurons;

import java.util.ArrayList;

import entities.neuralNetwork.Layer;
import entities.neuralNetwork.Nerve;
import entities.neuralNetwork.Perceptron;
import gClasses.DataAssociator;

public class Neuron {

	public int id;

	protected Perceptron per;

	protected ArrayList<Nerve> nerves = new ArrayList<Nerve>();

	protected double charge = 1;

	public Neuron(Perceptron per) {
		this.per = per;
	}

	public Neuron(DataAssociator da, Perceptron per) {

		this.per = per;

		id = da.getValueInt("id");

		int nerveCount = da.getValueInt("nerveCount");
		for (int i = 0; i < nerveCount; i++) {
			nerves.add(new Nerve(da.getValueDataAssociator(i), per));
		}
	}

	public void proceed() {
		charge = 0;

		for (Nerve nerve : nerves) {
			charge += nerve.getFlow();
		}
	}

	public void proceedLimitNeuronOutput(double maxOutput) {

		for (Nerve nerve : nerves) {
			nerve.cleanInfinits();
		}

		this.proceed();

		while (Math.abs(charge) > maxOutput) {
			if (charge > maxOutput) {
				Nerve strongestNerve = this.getNerve(0);
				double strongestNerveFlow = this.getNerve(0).getFlow();

				int nervCount = this.getNerveCount();
				for (int i = 1; i < nervCount; i++) {
					double nerveFlow = this.getNerve(i).getFlow();
					if (nerveFlow > strongestNerveFlow) {
						strongestNerveFlow = nerveFlow;
						strongestNerve = this.getNerve(i);
					}
				}
				strongestNerve.setCoefficient(strongestNerve.getCoefficient() / 2);
			} else {
				Nerve strongestNerve = this.getNerve(0);
				double strongestNerveFlow = this.getNerve(0).getFlow();

				int nervCount = this.getNerveCount();
				for (int i = 1; i < nervCount; i++) {
					double nerveFlow = this.getNerve(i).getFlow();
					if (nerveFlow < strongestNerveFlow) {
						strongestNerveFlow = nerveFlow;
						strongestNerve = this.getNerve(i);
					}
				}
				strongestNerve.setCoefficient(strongestNerve.getCoefficient() / 2);
			}
			this.proceed();
		}

	}

	public void linkTo(Neuron neu) {
		per.invalidate();

		nerves.add(new Nerve(neu));
	}

	public void linkTo(Layer predecessorLayer) {
		for (int i = 0; i < predecessorLayer.getNeuroneCount(); i++) {
			linkTo(predecessorLayer.getNeurone(i));
		}
	}

	public void removeAllNerves() {
		per.invalidate();

		while (!nerves.isEmpty()) {
			nerves.remove(0);
		}
	}

	public double getCharge() {
		return charge;
	}

	public void setCharge(double charge) {
		this.charge = charge;
	}

	public Nerve getNerve(int i) {
		return nerves.get(i);
	}

	public int getNerveCount() {
		return nerves.size();
	}

	public DataAssociator toDataAssociator() {
		DataAssociator da = new DataAssociator();

		da.addValue("id", id);

		da.addValue("class", this.getClass().toString());

		da.addValue("nerveCount", getNerveCount());
		for (int i = 0; i < getNerveCount(); i++) {
			da.addValue(i, getNerve(i).toDataAssociator());
		}
		return da;
	}

}
