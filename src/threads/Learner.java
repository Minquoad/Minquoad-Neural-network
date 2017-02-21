package threads;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import entities.neuralNetwork.Nerve;
import entities.neuralNetwork.Perceptron;
import utilities.Controler;
import utilities.Preferences;

public class Learner extends Thread {

	public enum LearningMode {
		SIMPLE, WITH_INDICE_COMPUTING
	}

	private Controler controler;
	private Perceptron per;
	private double[][] samples;

	private boolean learningNotEnded = true;
	private int maxIterations = 1;
	private boolean unlimitedIterations = false;
	private LearningMode learningMode = LearningMode.SIMPLE;
	private int iterations = 0;
	private int multiThreading = 1;
	private double minimumProgressionPerIteration = 0.01d;
	private int insufficientProgressions = 0;
	private double evolutionInLastIteration = 0d;
	private double currentMse;
	private double mseAfterLastIteration;

	private ArrayList<LearningStateListener> learningStateListeners = new ArrayList<LearningStateListener>();

	public Learner(Controler controler, Perceptron per, double[][] samples) {
		this.controler = controler;
		this.per = per;
		this.samples = samples;

		if (per.isValid()) {
			per.cleenInfinits(samples);
		}
		currentMse = per.getMse(samples);
		mseAfterLastIteration = currentMse;
	}

	public void run() {

		for (LearningStateListener learningStateListener : learningStateListeners) {
			learningStateListener.learningStarted(this);
		}

		switch (learningMode) {
		case SIMPLE:

			if (multiThreading == 1) {
				leanMonoThread(samples);
			} else {
				leanMultiThread(samples);
			}

			break;
		case WITH_INDICE_COMPUTING:

			per.validate();
			
			double[][] samplesInRandomOrder = randomizeSampleOrder(samples);
			
			ArrayList<double[][]> samplesList = separatSamples(samplesInRandomOrder, 2);

			double[][] samplesToLearn = samplesList.get(0);
			if (multiThreading == 1) {
				leanMonoThread(samplesToLearn);
			} else {
				leanMultiThread(samplesToLearn);
			}

			double mseOnUnlearnedData = per.getMse(samplesList.get(1));

			controler.appendLearningInfo("-----currentMse : " + currentMse + "\n");
			controler.appendLearningInfo("-----mseOnUnlearnedData : " + mseOnUnlearnedData + "\n");

			break;
		}

		for (LearningStateListener learningStateListener : learningStateListeners) {
			learningStateListener.learningEnded(this);
		}
	}

	private interface IterationPerformer {
		public void performeIteration();
	}

	private void iterate(IterationPerformer iterationPerformer) {

		if (!unlimitedIterations && maxIterations == 0) {
			learningNotEnded = false;
			controler.appendLearningInfo("Learning reached the maximum number of iterations\n");
		}
		while (learningNotEnded) {

			iterationPerformer.performeIteration();

			iterations++;
			evolutionInLastIteration = 1 - currentMse / mseAfterLastIteration;
			mseAfterLastIteration = currentMse;

			if (evolutionInLastIteration < minimumProgressionPerIteration) {
				insufficientProgressions++;
			} else {
				insufficientProgressions = 0;
			}

			boolean maxInsufficientProgressionsReached = insufficientProgressions == Preferences.INSUFFICIENT_PROGRESSIONS_NEEDED_TO_STOP;
			boolean maxIterationsReached = !unlimitedIterations && iterations == maxIterations;
			if (maxInsufficientProgressionsReached || maxIterationsReached) {

				learningNotEnded = false;

				controler.appendLearningInfo("Learning stopped:\n");
				if (maxInsufficientProgressionsReached) {
					controler.appendLearningInfo("\tno longer progressing\n");
				}
				if (maxIterationsReached) {
					controler.appendLearningInfo("\treached maximum number of iterations\n");
				}
			}
		}

	}

	private void leanMonoThread(double[][] samplesToLearn) {
		ArrayList<Nerve> nerves = per.getAllNerve();

		this.iterate(() -> {

			for (Nerve nerve : nerves) {

				nerve.evolve();

				double newMse = per.getMse(samplesToLearn);

				if (newMse < currentMse && Double.isFinite(newMse)) {
					nerve.reactToProgression();
					currentMse = newMse;
				} else {
					nerve.reactToRegression();
				}
			}

		});

	}

	private void leanMultiThread(double[][] samplesToLearn) {
		ArrayList<double[][]> samplesList = separatSamples(samplesToLearn, multiThreading);

		ArrayList<Perceptron> perceptronList = new ArrayList<Perceptron>();

		perceptronList.add(per);
		for (int i = 1; i < multiThreading; i++) {
			perceptronList.add(per.duplicate());
		}
		ArrayList<ArrayList<Nerve>> nervesList = new ArrayList<ArrayList<Nerve>>();
		for (int i = 0; i < multiThreading; i++) {
			nervesList.add(perceptronList.get(i).getAllNerve());
		}

		this.iterate(() -> {

			try {
				for (int i = 0; i < nervesList.get(0).size(); i++) {

					for (int j = 0; j < multiThreading; j++) {
						nervesList.get(j).get(i).evolve();
					}
					Thread[] treads = new Thread[multiThreading];
					double[] treadMses = new double[multiThreading];

					for (int j = 0; j < multiThreading; j++) {
						final int k = j;
						treads[j] = new Thread(() -> treadMses[k] = perceptronList.get(k).getMse(samplesList.get(k)));
						treads[j].start();
					}

					for (Thread thread : treads) {
						thread.join();
					}

					double newMse = 0;
					for (double treadMse : treadMses) {
						newMse += treadMse;
					}

					if (newMse < currentMse && Double.isFinite(newMse)) {
						for (int j = 0; j < multiThreading; j++) {
							nervesList.get(j).get(i).reactToProgression();
						}
						currentMse = newMse;
					} else {
						for (int j = 0; j < multiThreading; j++) {
							nervesList.get(j).get(i).reactToRegression();
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		});
	}

	public static double[][] randomizeSampleOrder(double[][] samples) {
		double[][] randomizedSamples = new double[samples.length][samples[0].length];

		List<double[]> sampleList = new LinkedList<double[]>();
		for (double[] sample : samples) {
			sampleList.add(sample);
		}
		Random rand = new Random();
		for (int i = 0; i < randomizedSamples.length; i++) {
			int j = rand.nextInt(sampleList.size());
			randomizedSamples[i] = sampleList.get(j);
			sampleList.remove(j);
		}

		return randomizedSamples;
	}

	private static ArrayList<double[][]> separatSamples(double[][] samples, int tableCount) {

		ArrayList<ArrayList<double[]>> sampleListList = new ArrayList<ArrayList<double[]>>();

		for (int i = 0; i < tableCount; i++) {
			sampleListList.add(new ArrayList<double[]>());
		}
		for (int i = 0; i < samples.length; i++) {
			sampleListList.get(i % tableCount).add(samples[i]);
		}
		ArrayList<double[][]> samplesList = new ArrayList<double[][]>();

		for (ArrayList<double[]> sampleList : sampleListList) {
			samplesList.add(toTable(sampleList));
		}

		return samplesList;
	}

	private static double[][] toTable(List<double[]> list) {
		double[][] table = new double[list.size()][list.get(0).length];
		for (int i = 0; i < list.size(); i++) {
			double[] line = list.get(i);
			for (int j = 0; j < line.length; j++) {
				table[i][j] = line[j];
			}
		}
		return table;
	}

	public void endLearning() {
		learningNotEnded = false;
		controler.appendLearningInfo("Learning stopped:\n"
				+ "\tstoped by user\n");
	}

	public boolean isLearningNotEnded() {
		return learningNotEnded;
	}

	public int getIterations() {
		return iterations;
	}

	public double getMse() {
		return currentMse;
	}

	public double getEvolutionInLastIteration() {
		return evolutionInLastIteration;
	}

	public int getMaxIterations() {
		return maxIterations;
	}

	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}

	public int getMultiThreading() {
		return multiThreading;
	}

	public void setMultiThreading(int multiThreading) {
		this.multiThreading = multiThreading;
	}

	public double getMinimumProgressionPerIteration() {
		return minimumProgressionPerIteration;
	}

	public void setMinimumProgressionPerIteration(double minimumProgressionPerIteration) {
		this.minimumProgressionPerIteration = minimumProgressionPerIteration;
	}

	public void addLearningStateListener(LearningStateListener lsl) {
		learningStateListeners.add(lsl);
	}

	public void removeLearningStateListener(LearningStateListener lsl) {
		learningStateListeners.remove(lsl);
	}

	public boolean isUnlimitedIterations() {
		return unlimitedIterations;
	}

	public void setUnlimitedIterations(boolean unlimitedIterations) {
		this.unlimitedIterations = unlimitedIterations;
	}

	public double getMseAfterLastIteration() {
		return mseAfterLastIteration;
	}

	public LearningMode getLearningMode() {
		return learningMode;
	}

	public void setLearningMode(LearningMode learningMode) {
		this.learningMode = learningMode;
	}

	public interface LearningStateListener {
		public void learningStarted(Learner source);

		public void learningEnded(Learner source);
	}

}
