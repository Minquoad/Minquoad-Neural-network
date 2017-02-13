package utilities;

import java.io.BufferedInputStream;
import java.io.IOException;

import entities.neuralNetwork.Layer;
import entities.neuralNetwork.Perceptron;
import gClasses.DataAssociator;
import gClasses.GRessourcesCollector;
import gClasses.gInterfaces.GChoixFichier;
import gClasses.gInterfaces.GDialog;
import interfaces.DataPan;
import interfaces.ErrorInFilePopup;
import interfaces.Frame;
import interfaces.MainPan;
import interfaces.PerceptronDisplayer;
import interfaces.PerceptronEditingPan;
import interfaces.modPanel.LearningPanel;
import interfaces.modPanel.ProcessingPanel;
import threads.Learner;
import threads.Learner.LearningStateListener;
import threads.LearnerObserver;

public class Controler {

	// repository
	private Perceptron per = new Perceptron();
	private double[][] data = null;
	private double[][] results = null;
	private Learner learner = null;

	// meta
	private Mode mode = Mode.NONE;

	public enum Mode {
		NONE, WILL_LEARN, LEARNING, WILL_PROCEED, HAS_PROCEED;
	}

	// grapicals
	private Frame frame = new Frame(this);
	private MainPan mainPan = new MainPan();
	private PerceptronDisplayer perceptronDisplayer = new PerceptronDisplayer();
	private PerceptronEditingPan perceptronEditingPan = new PerceptronEditingPan(this);
	private DataPan dataPan = new DataPan();
	private LearningPanel learningPan = new LearningPanel(this);
	private ProcessingPanel processingPan = new ProcessingPanel(this);

	public Controler() {
		per = new Perceptron();
		per.setInputCount(0);
		per.addLayer(new Layer(per));
		per.addLayer(new Layer(per));

		frame.setContentPane(mainPan);
		mainPan.setPerceptronEditingPan(perceptronEditingPan);
		mainPan.setDataPan(dataPan);
		mainPan.setPerceptronDisplayer(perceptronDisplayer);

		perceptronDisplayer.setPerceptron(per);
		perceptronEditingPan.regen(per);

		frame.setVisible(true);
	}

	public void perceptronModified() {
		perceptronEditingPan.regen(per);
		perceptronDisplayer.setPerceptron(per);

		mainPan.repaint();
	}

	public synchronized void updateMode() {

		if (mode == Mode.HAS_PROCEED) {
			results = null;
		}

		if (learner != null) {
			mode = Mode.LEARNING;
		} else if (data == null || !per.isValid()) {
			mode = Mode.NONE;
		} else {
			int inputCount = per.getInputCount();
			int outputCount = per.getOutputCount();
			int columnCount = data[0].length;

			if (columnCount == inputCount + outputCount)
				mode = Mode.WILL_LEARN;
			else if (columnCount == inputCount && results == null)
				mode = Mode.WILL_PROCEED;
			else if (columnCount == inputCount && results != null)
				mode = Mode.HAS_PROCEED;
			else {
				mode = Mode.NONE;
			}
		}

		frame.enableCsvSaving(mode == Mode.HAS_PROCEED);
		frame.setLearning(mode == Mode.LEARNING);
		learningPan.setLearning(mode == Mode.LEARNING);
		perceptronEditingPan.setLearning(mode == Mode.LEARNING);

		switch (mode) {
		case NONE:
			mainPan.setModePan(null);
			dataPan.setNoneMode(data);
			break;

		case WILL_LEARN:
			mainPan.setModePan(learningPan);
			dataPan.setLearningMode(data, per.getInputCount());
			break;

		case LEARNING:
			mainPan.setModePan(learningPan);
			dataPan.setLearningMode(data, per.getInputCount());
			break;

		case WILL_PROCEED:
			mainPan.setModePan(processingPan);
			dataPan.setProcessingMode(data, per.getOutputCount());
			break;

		case HAS_PROCEED:
			mainPan.setModePan(null);
			dataPan.setProcessedMode(data, results);
			break;

		default:
			break;
		}

		mainPan.repaint();
	}

	public void loadPer() {

		Starter.selectFileAndPerforme(frame, GChoixFichier.Mode.OPENING, (file) -> {
			try {
				per = new Perceptron(new DataAssociator(file));
				perceptronModified();
				updateMode();
			} catch (Exception e) {
				if (Starter.printCaughtExceptionStackTraces) {
					e.printStackTrace();
				}
				new ErrorInFilePopup();
			}
		});
	}

	public void savePer() {
		Starter.selectFileAndPerforme(frame, GChoixFichier.Mode.SAVING, (file) -> per.toDataAssociator().save(file));
	}

	public void loadCsv() {
		Starter.selectFileAndPerforme(frame, GChoixFichier.Mode.OPENING, (file) -> {
			try {
				data = CsvFormatHelper.getData(file);
				updateMode();
			} catch (Exception e) {
				if (Starter.printCaughtExceptionStackTraces) {
					e.printStackTrace();
				}
				new ErrorInFilePopup();
			}
		});
	}

	public void saveCsv() {
		Starter.selectFileAndPerforme(frame, GChoixFichier.Mode.SAVING,
				(file) -> CsvFormatHelper.save(file, Starter.concatLineByLine(data, results)));
	}

	public void resetPerceptron() {
		per.removeAllLayer();
		per.setInputCount(0);
		per.addLayer(new Layer(per));
		per.addLayer(new Layer(per));

		perceptronModified();
	}

	public void validatePerceptron() {
		if (per.isValid()) {
			per.invalidate();
		} else {
			per.validate();
		}
		perceptronModified();

		updateMode();
	}

	public void about() {
		new GDialog("About",
				"<br/>Software creator :<br/><br/>Guénaël Dequeker" + "<br/><br/><br/> v. : " + Starter.version, 300,
				200, true).setVisible(true);
	}

	public void incrementInputCount() {

		int inputCount = per.getInputCount();

		inputCount++;
		if (inputCount <= per.getLayer(0).getNeuroneCount()) {
			per.setInputCount(inputCount);
		}

		perceptronModified();
	}

	public void decrementInputCount() {
		int inputCount = per.getInputCount();

		inputCount--;
		if (inputCount >= 1) {
			per.setInputCount(inputCount);
		}

		perceptronModified();
	}

	public void unlearn() {
		per.validate();
	}

	public void startLearning() {
		learner = new Learner(per, data);

		learner.setMaxIterations(learningPan.getMaxIter());
		learner.setMultiThreading(learningPan.getMultiThreading());

		learner.addLearningStateListener(new LearningStateListener() {
			@Override
			public void learningStarted(Learner source) {
			}

			@Override
			public void learningEnded(Learner source) {
				Controler.this.learningEnded();
			}
		});

		new LearnerObserver(this, learner);

		learner.start();

		updateMode();
	}

	public void learningEnded() {
		this.learner = null;
		updateMode();
	}

	public void endLearning() {
		if (learner != null) {
			learner.endLearning();
		}
	}

	public void startProcessing() {
		results = per.getResults(data);
		this.updateMode();
	}

	public void appendLearningInfo(String str) {
		this.learningPan.appendText(str);
	}

	public void savePreferences() {
		PreferencesHelper.savePreferences(learningPan.getMaxIter(), learningPan.getMultiThreading());
	}

	public void help() {
		try {

			BufferedInputStream bis = GRessourcesCollector.getBufferedInputStream("resources/texts/help.html");
			byte[] ba = new byte[bis.available()];
			bis.read(ba);
			String helpText = new String(ba);

			new GDialog("Help", helpText, 800, 600, false).setVisible(true);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
