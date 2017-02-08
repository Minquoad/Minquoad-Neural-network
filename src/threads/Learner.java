package threads;

import java.util.ArrayList;
import java.util.List;

import entities.neuralNetwork.Nerve;
import entities.neuralNetwork.Perceptron;

public class Learner extends Thread {

	private Perceptron per;
	private double[][] samples;

	private boolean continueLearning = true;
	private int maxIterations = 1;
	private int iterations = 0;
	private int multiThreading = 1;
	private double mse;

	private ArrayList<LearningStateListener> learningStateListeners = new ArrayList<LearningStateListener>();

	public Learner(Perceptron per, double[][] samples) {

		this.per = per;
		this.samples = samples;

		if (per.isValid()) {
			per.cleenInfinits(samples);
		}
		mse = per.getMse(samples);
	}

	public void run() {

		for (LearningStateListener learningStateListener : learningStateListeners) {
			learningStateListener.learningStarted(this);
		}
		if (multiThreading == 1) {
			leaningMonoThread();
		} else {
			leaningMultiThread(multiThreading);
		}
		for (LearningStateListener learningStateListener : learningStateListeners) {
			learningStateListener.learningEnded(this);
		}
	}

	private void leaningMonoThread() {

		ArrayList<Nerve> nerves = per.getAllNerve();

		continueLearning &= iterations < maxIterations;
		while (continueLearning) {

			for (Nerve nerve : nerves) {

				nerve.evolve();

				double newMse = per.getMse(samples);

				if (newMse < mse && Double.isFinite(newMse)) {
					nerve.reactToProgression();
					mse = newMse;
				} else {
					nerve.reactToRegression();
				}

			}

			iterations++;

			continueLearning &= iterations < maxIterations;
		}
	}

	private void leaningMultiThread(int multiThreading) {

		ArrayList<double[][]> samplesList = separatSamples(samples, multiThreading);

		ArrayList<Perceptron> perceptronList = new ArrayList<Perceptron>();

		perceptronList.add(per);
		for (int i = 1; i < multiThreading; i++) {
			perceptronList.add(per.duplicate());
		}
		ArrayList<ArrayList<Nerve>> nervesList = new ArrayList<ArrayList<Nerve>>();
		for (int i = 0; i < multiThreading; i++) {
			nervesList.add(perceptronList.get(i).getAllNerve());
		}

		try {

			continueLearning &= iterations < maxIterations;
			while (continueLearning) {

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

					if (newMse < mse && Double.isFinite(newMse)) {
						for (int j = 0; j < multiThreading; j++) {
							nervesList.get(j).get(i).reactToProgression();
						}
						mse = newMse;
					} else {
						for (int j = 0; j < multiThreading; j++) {
							nervesList.get(j).get(i).reactToRegression();
						}
					}
				}

				iterations++;

				continueLearning &= iterations < maxIterations;
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
		continueLearning = false;
	}

	public int getIterations() {
		return iterations;
	}

	public double getMse() {
		return mse;
	}

	public void setMaxIterations(int i) {
		this.maxIterations = i;
	}

	public int getMaxIterations() {
		return maxIterations;
	}

	public void setMultiThreading(int i) {
		this.multiThreading = i;
	}

	public void addLearningStateListener(LearningStateListener lsl) {
		learningStateListeners.add(lsl);
	}

	public interface LearningStateListener {
		public void learningStarted(Learner source);
		public void learningEnded(Learner source);
	}

}
