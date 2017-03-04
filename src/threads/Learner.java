package threads;

import java.util.ArrayList;
import java.util.List;

import entities.neuralNetwork.Nerve;
import entities.neuralNetwork.Perceptron;
import utilities.Controler;
import utilities.Preferences;

public class Learner extends Thread {

	private Controler controler;
	private Perceptron per;
	private double[][] samples;

	// to be setted
	private int maxIterations = 1;
	private boolean unlimitedIterations = false;
	private LearningMode learningMode = LearningMode.SIMPLE;
	private double minimumProgressionPerIteration = 0.01d;
	private int multiThreading = 1;

	// used by the algorithme
	private boolean learningNotEnded = true;
	private int iterations = 0;
	private int insufficientProgressions = 0;
	private double evolutionInLastIteration = 0d;
	private double currentSquareError;
	private double squareErrorAfterLastIteration;
	private int learnedSamplesCount = 0;

	private ArrayList<LearningStateListener> learningStateListeners = new ArrayList<LearningStateListener>();

	public Learner(Controler controler, Perceptron per, double[][] samples) {
		this.controler = controler;
		this.per = per;
		this.samples = samples;
	}

	public void run() {
		switch (learningMode) {
		case SIMPLE:

			this.learn(samples);

			break;
		case WITH_CONTROL_SAMPLE:

			ArrayList<double[][]> samplesList = splitSamples(samples, 2);

			double[][] samplesToLearn = samplesList.get(0);

			this.learn(samplesToLearn);

			double weightedSquareErrorOnUnlearnedData = per.getSquareError(samplesList.get(1))
					/ (double) samplesList.get(1).length;
			double unlearnedPerceptronWeightedSquareErrorMean = this.comuteUnlearnedPerceptronSquareErrorMean(samples)
					/ (double) samples.length;

			double a = 1d / (getWeightedSquareErrorAfterLastIteration() - unlearnedPerceptronWeightedSquareErrorMean);
			double b = -a * unlearnedPerceptronWeightedSquareErrorMean;
			double neuralNetworkPortability = a * weightedSquareErrorOnUnlearnedData + b;

			controler.appendLearningInfo(
					"\n" + "-> Square error mean without learning : " + unlearnedPerceptronWeightedSquareErrorMean);
			controler.appendLearningInfo(
					"\n" + "-> Square error on learned data : " + getWeightedSquareErrorAfterLastIteration());
			controler.appendLearningInfo(
					"\n" + "-> Square error on control data : " + weightedSquareErrorOnUnlearnedData);
			controler.appendLearningInfo(
					"\n" + "-> neural network portability (%) : " + neuralNetworkPortability * 100);

			break;
		}

		for (LearningStateListener learningStateListener : learningStateListeners) {
			learningStateListener.learningEnded(this);
		}
		
		controler.learningEnded();
	}

	private void learn(double[][] samplesToLearn) {
		learnedSamplesCount = samplesToLearn.length;
		per.cleenInfinits(samplesToLearn);
		currentSquareError = per.getSquareError(samplesToLearn);
		squareErrorAfterLastIteration = currentSquareError;
		multiThreading = Math.min(multiThreading, samplesToLearn.length);

		for (LearningStateListener learningStateListener : learningStateListeners) {
			learningStateListener.learningStarting(this);
		}

		if (multiThreading == 1) {
			leanMonoThread(samplesToLearn);
		} else {
			try {
				leanMultiThread(samplesToLearn);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		controler.appendLearningInfo("\n" + "Learning ended");
		if (!isMaxInsufficientProgressionsUnreached()) {
			controler.appendLearningInfo("\n" + "-> no longer progressing");
		}
		if (!isMaxIterationsUnreached()) {
			controler.appendLearningInfo("\n" + "-> reached maximum number of iterations");
		}
	}

	private void leanMonoThread(double[][] samplesToLearn) {
		ArrayList<Nerve> nerves = per.getAllNerve();

		for (updateLearningNotEnded(); learningNotEnded; peroformeIterationEnded()) {

			for (Nerve nerve : nerves) {
				nerve.evolve();

				double newSquareError = per.getSquareError(samplesToLearn);
				if (newSquareError < currentSquareError && Double.isFinite(newSquareError)) {
					nerve.reactToProgression();
					currentSquareError = newSquareError;
				} else {
					nerve.reactToRegression();
				}
			}
		}
	}

	private void leanMultiThread(double[][] samplesToLearn) throws InterruptedException {
		ArrayList<double[][]> samplesList = splitSamples(samplesToLearn, multiThreading);

		ArrayList<Perceptron> perceptronList = new ArrayList<Perceptron>();

		perceptronList.add(per);
		for (int i = 1; i < multiThreading; i++) {
			perceptronList.add(per.duplicate());
		}
		ArrayList<ArrayList<Nerve>> nervesList = new ArrayList<ArrayList<Nerve>>();
		for (int i = 0; i < multiThreading; i++) {
			nervesList.add(perceptronList.get(i).getAllNerve());
		}

		for (updateLearningNotEnded(); learningNotEnded; peroformeIterationEnded()) {

			for (int i = 0; i < nervesList.get(0).size(); i++) {

				for (int j = 0; j < multiThreading; j++) {
					nervesList.get(j).get(i).evolve();
				}
				Thread[] treads = new Thread[multiThreading];
				double[] treadSquareErrors = new double[multiThreading];

				for (int j = 0; j < multiThreading; j++) {
					final int k = j;
					treads[j] = new Thread(
							() -> treadSquareErrors[k] = perceptronList.get(k).getSquareError(samplesList.get(k)));
					treads[j].start();
				}

				for (Thread thread : treads) {
					thread.join();
				}

				double newSquareError = 0;
				for (double treadSquareError : treadSquareErrors) {
					newSquareError += treadSquareError;
				}

				if (newSquareError < currentSquareError && Double.isFinite(newSquareError)) {
					for (int j = 0; j < multiThreading; j++) {
						nervesList.get(j).get(i).reactToProgression();
					}
					currentSquareError = newSquareError;
				} else {
					for (int j = 0; j < multiThreading; j++) {
						nervesList.get(j).get(i).reactToRegression();
					}
				}
			}

		}
	}

	private void peroformeIterationEnded() {
		iterations++;
		evolutionInLastIteration = 1 - currentSquareError / squareErrorAfterLastIteration;
		squareErrorAfterLastIteration = currentSquareError;

		if (evolutionInLastIteration < minimumProgressionPerIteration) {
			insufficientProgressions++;
		} else {
			insufficientProgressions = 0;
		}

		updateLearningNotEnded();
	}

	private void updateLearningNotEnded() {
		learningNotEnded &= isMaxInsufficientProgressionsUnreached() && isMaxIterationsUnreached();
	}

	private boolean isMaxInsufficientProgressionsUnreached() {
		return insufficientProgressions != Preferences.INSUFFICIENT_PROGRESSIONS_NEEDED_TO_STOP;
	}

	private boolean isMaxIterationsUnreached() {
		return unlimitedIterations || iterations != maxIterations;
	}

	private double comuteUnlearnedPerceptronSquareErrorMean(double[][] unlearnedSamples) {
		Perceptron randomPer = per.duplicate();

		int randomPerTested = 16;
		double squareErrorSum = 0;

		for (int i = 0; i < randomPerTested; i++) {
			randomPer.validate();
			squareErrorSum += randomPer.getSquareError(unlearnedSamples);
		}

		return squareErrorSum / (double) randomPerTested;
	}

	private static ArrayList<double[][]> splitSamples(double[][] samples, int tableCount) {

		ArrayList<ArrayList<double[]>> sampleListList = new ArrayList<ArrayList<double[]>>();

		for (int i = 0; i < tableCount; i++) {
			sampleListList.add(new ArrayList<double[]>());
		}
		for (int i = 0; i < samples.length; i++) {
			sampleListList.get(i * tableCount / samples.length).add(samples[i]);
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
	}

	public boolean isLearningNotEnded() {
		return learningNotEnded;
	}

	public int getIterations() {
		return iterations;
	}

	public double getSquareError() {
		return currentSquareError;
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

	public double getSquareErrorAfterLastIteration() {
		return squareErrorAfterLastIteration;
	}

	public double getWeightedSquareErrorAfterLastIteration() {
		return squareErrorAfterLastIteration / (double) learnedSamplesCount;
	}

	public LearningMode getLearningMode() {
		return learningMode;
	}

	public void setLearningMode(LearningMode learningMode) {
		this.learningMode = learningMode;
	}

	public interface LearningStateListener {
		public void learningStarting(Learner source);

		public void learningEnded(Learner source);
	}

}
