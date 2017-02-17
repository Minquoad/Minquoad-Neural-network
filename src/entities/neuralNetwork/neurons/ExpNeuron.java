package entities.neuralNetwork.neurons;

import entities.neuralNetwork.Nerve;
import entities.neuralNetwork.Perceptron;
import gClasses.DataAssociator;

public class ExpNeuron extends Neuron {

	public ExpNeuron(Perceptron per) {
		super(per);
	}

	public ExpNeuron(DataAssociator da, Perceptron per) {
		super(da, per);
	}

	public void proceed() {
		super.proceed();

		charge = Math.exp(charge);

	}

	public void proceedLimitNeuronOutput(double maxOutput) {

		for (Nerve nerve : nerves) {
			nerve.cleanInfinits();
		}

		this.proceed();

		while (charge > maxOutput || charge == 0) {
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

}
