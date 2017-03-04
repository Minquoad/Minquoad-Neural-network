package utilities;

import java.awt.KeyboardFocusManager;
import java.io.BufferedInputStream;
import java.io.IOException;

import entities.neuralNetwork.Perceptron;
import entities.neuralNetwork.neurons.BlankNeuron;
import entities.neuralNetwork.neurons.Neuron;
import entities.neuralNetwork.neurons.NeuronType;
import gClasses.DataAssociator;
import gClasses.GRessourcesCollector;
import gClasses.gInterfaces.GChoixFichier;
import gClasses.gInterfaces.GDialog;
import interfaces.DataPan;
import interfaces.Frame;
import interfaces.MainPan;
import interfaces.PerceptronDisplayer;
import interfaces.PerceptronEditingPan;
import interfaces.ShortCutManager;
import interfaces.modPanel.LearningPanel;
import interfaces.modPanel.ProcessingPanel;
import interfaces.popup.ErrorCurvePerceptron;
import interfaces.popup.ErrorInFilePopup;
import threads.Learner;
import threads.LearnerObserver;
import threads.Processor;

public class Controler {

	public static void main(String[] args) {
		new Controler();
	}

	// repository
	private Perceptron per;
	private double[][] data = null;
	private double[][] dataForCurveLearning = null;
	private double[][] results = null;
	private Learner learner = null;
	private Processor processor = null;

	// meta
	private ApplicationMode mode = ApplicationMode.NONE;
	private boolean curveData = false;

	// grapicals
	private Frame frame = new Frame(this);
	private MainPan mainPan = new MainPan();
	private PerceptronDisplayer perceptronDisplayer = new PerceptronDisplayer();
	private PerceptronEditingPan perceptronEditingPan = new PerceptronEditingPan(this);
	private DataPan dataPan = new DataPan();
	private LearningPanel learningPan = new LearningPanel(this);
	private ProcessingPanel processingPan = new ProcessingPanel(this);
	private ShortCutManager shortCutManager = new ShortCutManager(this);

	public Controler() {
		this.resetPerceptron();

		mainPan.setPerceptronEditingPan(perceptronEditingPan);
		mainPan.setDataPan(dataPan);
		mainPan.setPerceptronDisplayer(perceptronDisplayer);
		frame.setContentPane(mainPan);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(shortCutManager);

		if (Preferences.isFirstRunning()) {
			help();
		}

		frame.setVisible(true);
	}

	public synchronized void updateMode() {

		if (data == null || !per.isValid()) {
			mode = ApplicationMode.NONE;
		} else if (learner != null) {
			mode = ApplicationMode.LEARNING;
		} else if (processor != null) {
			mode = ApplicationMode.PROCESSING;
		} else if (curveData) {
			if (per.getInputCount() < data.length) {
				if (mode == ApplicationMode.WILL_PROCEED || mode == ApplicationMode.PROCESSING) {
					mode = ApplicationMode.WILL_PROCEED;
				} else if (mode == ApplicationMode.WILL_LEARN || mode == ApplicationMode.LEARNING) {
					mode = ApplicationMode.WILL_LEARN;
				} else {
					mode = ApplicationMode.WILL_LEARN;
				}
			} else {
				mode = ApplicationMode.NONE;
			}
		} else {
			// auto detected mode
			int inputCount = per.getInputCount();
			int outputCount = per.getOutputCount();
			int columnCount = data[0].length;

			if (columnCount == inputCount + outputCount)
				mode = ApplicationMode.WILL_LEARN;
			else if (columnCount == inputCount)
				mode = ApplicationMode.WILL_PROCEED;
			else {
				mode = ApplicationMode.NONE;
			}
		}

		switch (mode) {
		case NONE:

			mainPan.setModePan(null);
			dataPan.setNoneMode(data);

			break;
		case WILL_LEARN:

			mainPan.setModePan(learningPan);
			if (curveData) {
				dataPan.setLearningMode(dataForCurveLearning, per.getInputCount());
			} else {
				dataPan.setLearningMode(data, per.getInputCount());
			}

			break;
		case LEARNING:

			mainPan.setModePan(learningPan);
			if (curveData) {
				dataPan.setLearningMode(dataForCurveLearning, per.getInputCount());
			} else {
				dataPan.setLearningMode(data, per.getInputCount());
			}

			break;
		case WILL_PROCEED:

			mainPan.setModePan(processingPan);
			if (curveData) {
				if (results == null) {
					dataPan.setCurveProcessingMode(data);
				} else {
					dataPan.setCurveProcessedMode(data, results);
				}
			} else {
				if (results == null) {
					dataPan.setProcessingMode(data, per.getOutputCount());
				} else {
					dataPan.setProcessedMode(data, results);
				}
			}

			break;
		case PROCESSING:

			mainPan.setModePan(processingPan);
			if (curveData) {
				dataPan.setCurveProcessingMode(data);
			} else {
				dataPan.setProcessingMode(data, per.getOutputCount());
			}

			break;
		}

		boolean impliesOccupation = mode.impliesOccupation();
		frame.setOccupied(impliesOccupation);
		perceptronEditingPan.setOccupied(impliesOccupation);
		learningPan.setOccupied(impliesOccupation);
		processingPan.setOccupied(impliesOccupation);

		processingPan.setCurveMode(curveData);
		learningPan.setCurveMode(curveData);

		boolean enableCsvSaving = mode == ApplicationMode.WILL_PROCEED && results != null;
		frame.enableCsvSaving(enableCsvSaving);
		shortCutManager.enableCsvSaving(enableCsvSaving);

		mainPan.validate();
		mainPan.repaint();
	}

	public void toggleCurveApplicationMode() {
		if (curveData) {
			switch (mode) {
			case WILL_LEARN:
				mode = ApplicationMode.WILL_PROCEED;
				break;
			case WILL_PROCEED:
				mode = ApplicationMode.WILL_LEARN;
				break;
			default:
				break;
			}
		}
		this.updateMode();
	}

	public void perceptronModified() {
		if (curveData && data != null && per.isValid()) {
			dataForCurveLearning = CsvFormatHelper.toSampleArray(data, per.getInputCount());
		}

		results = null;
		perceptronEditingPan.regen(per);
		perceptronDisplayer.setPerceptron(per);

		mainPan.validate();
		mainPan.repaint();
	}

	public void loadPer() {

		Preferences.selectFileAndPerforme(frame, GChoixFichier.Mode.OPENING, file -> {
			try {
				per = new Perceptron(new DataAssociator(file));
				if (curveData && per.isValid() && per.getOutputCount() > 1) {
					per.invalidate();
					new ErrorCurvePerceptron();
				}
				perceptronModified();
				updateMode();
			} catch (Exception e) {
				if (Preferences.PRINT_CAUGHT_EXCEPTION_STACK_TRACES) {
					e.printStackTrace();
				}
				new ErrorInFilePopup();
			}
		});
	}

	public void savePer() {
		Preferences.selectFileAndPerforme(frame, GChoixFichier.Mode.SAVING,
				file -> per.toDataAssociator().save(file));
	}

	public void loadCsv() {
		Preferences.selectFileAndPerforme(frame, GChoixFichier.Mode.OPENING, file -> {
			try {
				data = CsvFormatHelper.getData(file);
				curveData = CsvFormatHelper.isCurve(data);
				if (curveData) {
					data = CsvFormatHelper.toColumnIfNeeded(data);

					if (per.isValid()) {
						if (per.getOutputCount() == 1 && per.getInputCount() != 0) {
							dataForCurveLearning = CsvFormatHelper.toSampleArray(data, per.getInputCount());
						} else {
							per.invalidate();
							perceptronModified();
							new ErrorCurvePerceptron();
						}
					}
				}
				updateMode();
			} catch (Exception e) {
				if (Preferences.PRINT_CAUGHT_EXCEPTION_STACK_TRACES) {
					e.printStackTrace();
				}
				new ErrorInFilePopup();
			}
		});
	}

	public void saveCsv() {
		Preferences.selectFileAndPerforme(frame, GChoixFichier.Mode.SAVING,
				file -> {
					if (curveData) {
						CsvFormatHelper.save(file, CsvFormatHelper.concatColumnByColumn(data, results));
					} else {
						CsvFormatHelper.save(file, CsvFormatHelper.concatLineByLine(data, results));
					}
				});
	}

	public void togglePerceptronValidation() {
		if (per.isValid()) {
			per.invalidate();
		} else {
			if (curveData && per.getOutputCount() > 1) {
				new ErrorCurvePerceptron();
			} else {
				per.validate();
			}
		}
		perceptronModified();
		updateMode();
	}

	public void about() {
		new GDialog("About",
				"<br/>Software creator :<br/><br/>Guénaël Dequeker" + "<br/><br/><br/> v. : " + Preferences.VERSION,
				300,
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
		learningPan.startNewLearning();
		this.appendLearningInfo("\n" + "Learning starting");

		double[][] dataToLearn;
		if (curveData) {
			dataToLearn = dataForCurveLearning;
		} else {
			dataToLearn = data;
		}
		learner = new Learner(this, per, dataToLearn);
		learner.setMaxIterations(learningPan.getMaxIter());
		learner.setMultiThreading(learningPan.getMultiThreading());
		learner.setMinimumProgressionPerIteration(learningPan.getMinimumProgressionPerIteration());
		learner.setUnlimitedIterations(learningPan.isUnlimitedIterations());
		learner.setLearningMode(learningPan.getLearningMode());

		new LearnerObserver(this, learner);

		updateMode();

		learner.start();

	}

	public void learningEnded() {
		this.learner = null;
		updateMode();
	}

	public void handleUserRequestLearningEnd() {
		if (learner != null) {
			this.appendLearningInfo("\n" + "Learning ending request by user...");
			learner.endLearning();
		}
	}

	public void randomizeSamplesOrder() {
		if (data != null) {
			data = CsvFormatHelper.randomizeSampleOrder(data);
			updateMode();
		}
	}

	public void startProcessing() {
		results = null;
		processor = new Processor(this, per, data);
		processor.setCurveData(curveData);
		processor.setValueExtendedCount(processingPan.getValueExtendedCount());

		updateMode();

		processor.start();
	}

	public void processingEnded(double[][] results) {
		this.results = results;
		processor = null;

		updateMode();
	}

	public void appendLearningInfo(int iter, double squareError, double lastEvolution, long duration) {
		this.learningPan.appendInfo(iter, squareError, lastEvolution, duration);
	}

	public void appendLearningInfo(String str) {
		this.learningPan.appendInfo(str);
	}

	public void appendProcessingInfo(String str) {
		this.processingPan.appendInfo(str);
	}

	public void savePreferences() {
		Preferences.setMaxIter(this.learningPan.getMaxIter());
		Preferences.setMultiThreading(this.learningPan.getMultiThreading());
		Preferences.setMinimumProgressionPerIteration(this.learningPan.getMinimumProgressionPerIteration());
		Preferences.setInterationsUnlimited(this.learningPan.isUnlimitedIterations());
		Preferences.setLearningMode(this.learningPan.getLearningMode());

		Preferences.save();
	}

	public void help() {
		try {

			BufferedInputStream bis = GRessourcesCollector.getBufferedInputStream("resources/texts/help.html");
			byte[] ba = new byte[bis.available()];
			bis.read(ba);
			String helpText = new String(ba);

			helpText = helpText.replaceAll("_insufficientProgressionsNeededToStop",
					Integer.toString(Preferences.INSUFFICIENT_PROGRESSIONS_NEEDED_TO_STOP));

			GDialog helpDialog = new GDialog("Help", helpText, 800, 600, false);
			helpDialog.setAlwaysOnTop(false);
			helpDialog.setVisible(true);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void resetPerceptron() {
		if (per == null) {
			per = new Perceptron();
		} else {
			per.removeAllLayer();
			per.setInputCount(0);
		}
		per.addLayer();
		per.addLayer();

		perceptronModified();
	}

	public void addNeuron(NeuronType type, int layer) {
		for (int i = 0; i < perceptronEditingPan.getNeronCountToAdd(); i++) {
			if (layer == 0) {
				per.getLayer(layer).addNeuron(new BlankNeuron());
				this.incrementInputCount();
			} else if (!curveData || layer != per.getLayerCount() - 1 || per.getOutputCount() == 0) {
				Neuron newNeuron = type.getNewInstance();
				per.getLayer(layer).addNeuron(newNeuron);
			}
		}

		this.perceptronModified();
	}

	public void removeNeuron(int layer) {
		for (int i = 0; i < perceptronEditingPan.getNeronCountToAdd(); i++) {
			int neuronCountInLayer = per.getLayer(layer).getNeuroneCount();
			if (neuronCountInLayer != 0) {
				per.getLayer(layer).removeNeuron();
				if (layer == 0) {
					per.setInputCount(Math.min(per.getInputCount(), per.getLayer(0).getNeuroneCount()));
				}
			}
		}

		this.perceptronModified();
	}

	public void addLayer(int i) {
		per.addLayer(i);
		this.perceptronModified();
	}

	public void removeLayer(int layer) {
		per.removeLayer(layer);
		this.perceptronModified();
	}

	public boolean isCurveData() {
		return curveData;
	}

}
