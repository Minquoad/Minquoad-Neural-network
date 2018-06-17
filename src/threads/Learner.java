package threads;

import java.util.ArrayList;

import entities.neuralNetwork.Nerve;
import entities.neuralNetwork.Perceptron;
import entities.neuralNetwork.Sample;
import utilities.Configuration;
import utilities.Controller;

public class Learner extends Thread {

	private Controller controler;
	private Perceptron per;
	private Sample[] samples;

	// to be setted
	private int maxIterations = 1;
	private boolean unlimitedIterations = false;
	private LearningMode learningMode = LearningMode.SIMPLE;
	private double minimumProgressionPerIteration = 0.01d;
	private int multiThreading = 1;

	// used by the algorithme
	private boolean learningEnded = false;
	private int iterationCount = 0;
	private int insufficientProgressionCount = 0;
	private double evolutionInLastIteration = 0d;
	private double currentSquareError;
	private double squareErrorAfterLastIteration;
	private int learnedSamplesCount = 0;

	private ArrayList<LearningStateListener> learningStateListeners = new ArrayList<LearningStateListener>();

	public Learner(Controller controler, Perceptron per, Sample[] samples) {
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
			
			int controlSamplesCount = (int) ((double) (samples.length) * Configuration.CONTROL_SAMPLES_SIZE);
			Sample[] controlSamples = new Sample[controlSamplesCount];
			for (int i = 0; i < controlSamplesCount; i++) {
				controlSamples[i] = samples[i];
			}
			Sample[] learningSamples = new Sample[samples.length - controlSamplesCount];
			for (int i = controlSamplesCount; i < samples.length; i++) {
				learningSamples[i] = samples[i];
			}

			this.learn(learningSamples);

			
			double controlSamplesWeightedSquareError = per.getWeightedSquareError(controlSamples);
			
			double randomPerceptronWeightedSquareErrorMean = 0;
			Perceptron randomPer = per.duplicate();
			int randomPerTested = 16;
			for (int i = 0; i < randomPerTested; i++) {
				randomPer.validate();
				randomPerceptronWeightedSquareErrorMean += randomPer.getWeightedSquareError(controlSamples);
			}
			randomPerceptronWeightedSquareErrorMean /= randomPerTested;
			
			double a = 1d / (getWeightedSquareErrorAfterLastIteration() - randomPerceptronWeightedSquareErrorMean);
			double b = -a * randomPerceptronWeightedSquareErrorMean;
			double neuralNetworkPortability = a * controlSamplesWeightedSquareError + b;

			controler.appendLearningInfo(
					"\n" + "-> Square error mean without learning : " + randomPerceptronWeightedSquareErrorMean);
			controler.appendLearningInfo(
					"\n" + "-> Square error on learned data : " + getWeightedSquareErrorAfterLastIteration());
			controler.appendLearningInfo(
					"\n" + "-> Square error on control data : " + controlSamplesWeightedSquareError);
			controler
					.appendLearningInfo("\n" + "-> neural network portability (%) : " + neuralNetworkPortability * 100);

			break;
		}

		for (LearningStateListener learningStateListener : learningStateListeners) {
			learningStateListener.learningEnded(this);
		}

		controler.learningEnded();
	}

	private void learn(Sample[] samples) {
		learnedSamplesCount = samples.length;
		per.cleenInfinits(samples);
		currentSquareError = per.getSquareErrorSum(samples);
		squareErrorAfterLastIteration = currentSquareError;
		multiThreading = Math.min(multiThreading, samples.length);

		for (LearningStateListener learningStateListener : learningStateListeners) {
			learningStateListener.learningStarting(this);
		}

		if (multiThreading == 1) {
			leanMonoThread(samples);
		} else {
			try {
				leanMultiThread(samples);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		controler.appendLearningInfo("\n" + "Learning ended");
		if (isMaxInsufficientProgressionsReached()) {
			controler.appendLearningInfo("\n" + "-> no longer progressing");
		}
		if (isMaxIterationsReached()) {
			controler.appendLearningInfo("\n" + "-> reached maximum number of iterations");
		}
	}

	private void leanMonoThread(Sample[] samples) {
		ArrayList<Nerve> nerves = per.getAllNerve();

		for (updateLearningEnded(); !learningEnded; peroformeIterationEnded()) {

			for (Nerve nerve : nerves) {
				nerve.evolve();

				double newSquareError = per.getSquareErrorSum(samples);

				if (newSquareError < currentSquareError && Double.isFinite(newSquareError)) {
					nerve.reactToProgression();
					currentSquareError = newSquareError;
				} else {
					nerve.reactToRegression();
				}
			}

		}
	}

	private void leanMultiThread(Sample[] samplesToLearn) throws InterruptedException {
		Sample[][] samplesArray = Sample.splitSamples(samplesToLearn, multiThreading);

		Perceptron[] perceptrons = new Perceptron[multiThreading];

		perceptrons[0] = per;
		for (int i = 1; i < perceptrons.length; i++) {
			perceptrons[i] = per.duplicate();
		}

		ArrayList<ArrayList<Nerve>> nervesList = new ArrayList<ArrayList<Nerve>>();
		for (int i = 0; i < multiThreading; i++) {
			nervesList.add(perceptrons[i].getAllNerve());
		}

		for (updateLearningEnded(); !learningEnded; peroformeIterationEnded()) {

			for (int i = 0; i < nervesList.get(0).size(); i++) {

				for (int j = 0; j < multiThreading; j++) {
					nervesList.get(j).get(i).evolve();
				}
				Thread[] treads = new Thread[multiThreading];
				double[] treadSquareErrors = new double[multiThreading];

				for (int j = 0; j < multiThreading; j++) {
					final int k = j;
					treads[j] = new Thread(
							() -> treadSquareErrors[k] = perceptrons[k].getSquareErrorSum(samplesArray[k]));
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
					for (ArrayList<Nerve> nerves : nervesList) {
						nerves.get(i).reactToProgression();
					}
					currentSquareError = newSquareError;
				} else {
					for (ArrayList<Nerve> nerves : nervesList) {
						nerves.get(i).reactToRegression();
					}
				}
			}

		}
	}

	private void peroformeIterationEnded() {
		iterationCount++;
		if (squareErrorAfterLastIteration == 0) {
			evolutionInLastIteration = 0;
		} else {
			evolutionInLastIteration = 1 - currentSquareError / squareErrorAfterLastIteration;
		}
		squareErrorAfterLastIteration = currentSquareError;

		if (evolutionInLastIteration < minimumProgressionPerIteration) {
			insufficientProgressionCount++;
		} else {
			insufficientProgressionCount = 0;
		}

		updateLearningEnded();
	}

	private void updateLearningEnded() {
		learningEnded |= isMaxInsufficientProgressionsReached() || isMaxIterationsReached();
	}

	private boolean isMaxInsufficientProgressionsReached() {
		return insufficientProgressionCount == Configuration.INSUFFICIENT_PROGRESSIONS_NEEDED_TO_STOP;
	}

	private boolean isMaxIterationsReached() {
		return !unlimitedIterations && iterationCount == maxIterations;
	}

	public void endLearning() {
		learningEnded = true;
	}

	public boolean isLearningNotEnded() {
		return !learningEnded;
	}

	public int getIterations() {
		return iterationCount;
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
