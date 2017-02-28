package threads;

import threads.Learner.LearningStateListener;
import utilities.Controler;

public class LearnerObserver extends Thread implements LearningStateListener {

	private Controler controler;
	private Learner lea;
	private boolean learning = false;
	private static final long fps = 8;
	private long startTime;
	private int lastDescibedIteration = -1;

	public LearnerObserver(Controler controler, Learner lea) {
		this.controler = controler;
		this.lea = lea;
		lea.addLearningStateListener(this);
	}

	@Override
	public void learningStarting(Learner source) {
		learning = true;
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
	}

	public synchronized void printInfo() {

		int iter = lea.getIterations();

		if (iter != lastDescibedIteration) {

			long time = System.currentTimeMillis();

			controler.appendLearningInfo(
					iter,
					lea.getWeightedSquareErrorAfterLastIteration(),
					lea.getEvolutionInLastIteration(),
					time - startTime);

			lastDescibedIteration = iter;
		}
	}

}
