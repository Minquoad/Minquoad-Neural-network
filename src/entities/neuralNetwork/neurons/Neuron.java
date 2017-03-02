package entities.neuralNetwork.neurons;

import java.util.ArrayList;

import entities.neuralNetwork.Layer;
import entities.neuralNetwork.Nerve;
import entities.neuralNetwork.Perceptron;
import gClasses.DataAssociator;

public class Neuron {

	public int id;

	protected Perceptron per = null;

	protected ArrayList<Nerve> nerves = new ArrayList<Nerve>();

	protected double charge = 1;

	public void buildFromDataAssociator(DataAssociator da, Perceptron per) {
		this.setPerceptron(per);
		
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
		invalidate();
		nerves.add(new Nerve(neu));
	}

	public void linkTo(Layer predecessorLayer) {
		for (int i = 0; i < predecessorLayer.getNeuroneCount(); i++) {
			linkTo(predecessorLayer.getNeurone(i));
		}
	}

	public void removeAllNerves() {
		invalidate();
		nerves.clear();
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

	public ArrayList<Nerve> getAllNervs() {
		ArrayList<Nerve> nerves = new ArrayList<Nerve>();
		for (Nerve nerve : this.nerves) {
			nerves.add(nerve);
		}
		return nerves;
	}
	
	public DataAssociator toDataAssociator() {
		DataAssociator da = new DataAssociator();

		da.addValue("id", id);

		da.addValue("type", NeuronType.getEnumFromInstance(this).toString());

		da.addValue("nerveCount", getNerveCount());
		for (int i = 0; i < getNerveCount(); i++) {
			da.addValue(i, getNerve(i).toDataAssociator());
		}
		return da;
	}

	public void setPerceptron(Perceptron per) {
		invalidate();
		this.per = per;
	}

	private void invalidate() {
		if (per != null) {
			per.invalidate();
		}
	}

}
