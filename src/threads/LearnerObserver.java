package threads;

import java.math.BigDecimal;
import java.math.MathContext;

import threads.Learner.LearningStateListener;
import utilities.Controler;

public class LearnerObserver extends Thread implements LearningStateListener {

	private Controler controler;
	private Learner lea;
	private int lastIteration = -1;
	private boolean learning;
	private static final long fps = 8;
	private long startTime;
	private double firstMse0s = -1;
	private double maxIter0s = -1;

	public LearnerObserver(Controler controler, Learner lea) {
		this.controler = controler;
		this.lea = lea;
		lea.addLearningStateListener(this);
	}

	@Override
	public void learningStarted(Learner source) {
		learning = true;

		maxIter0s = lea.getMaxIterations();
		maxIter0s = Math.max(maxIter0s, 1);
		maxIter0s = Math.log10(maxIter0s);
		maxIter0s -= maxIter0s % 1;
		maxIter0s = Math.pow(10, maxIter0s);
		maxIter0s = Math.min(maxIter0s, 1000000);

		startTime = System.currentTimeMillis();
		printInfo();
		this.start();
	}

	@Override
	public void run() {
		try {
			Thread.sleep(1000 / fps);
			while (learning) {
				printInfo();
				Thread.sleep(1000 / fps);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void learningEnded(Learner source) {
		learning = false;
		printInfo();
		controler.appendLearningInfo("\n");
	}

	public synchronized void printInfo() {

		long time = System.currentTimeMillis();
		int iter = lea.getIterations();
		double mse = lea.getMse();
		time -= startTime;

		if (iter != lastIteration) {
			lastIteration = iter;

			String str = "Iter. : ";

			int i = iter;
			i = Math.max(i, 1);
			while (i < maxIter0s) {
				str += " ";
				i *= 10;
			}
			str += Integer.toString(iter);

			while (str.length() < 13) {
				str += " ";
			}
			str += " > MSE = ";

			if (firstMse0s == -1) {
				firstMse0s = mse;
				firstMse0s = Math.max(firstMse0s, 1);
				firstMse0s = Math.log10(firstMse0s);
				firstMse0s -= firstMse0s % 1;
				firstMse0s = Math.pow(10, firstMse0s);
				firstMse0s = Math.min(firstMse0s, 1000000);
			}

			double i2 = mse;
			i2 = Math.max(i2, 1);
			while (i2 < firstMse0s) {
				str += " ";
				i2 *= 10;
			}

			if (Double.isFinite(mse)) {
				str += Double.toString(new BigDecimal(mse).round(new MathContext(5)).doubleValue());
			} else {
				str += Double.toString(mse);
			}
			while (str.length() < 43) {
				str += " ";
			}
			str += " > time (ms) : ";

			str += time;

			str += "\n";

			controler.appendLearningInfo(str);

		}
	}

}
