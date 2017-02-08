package utilities;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import entities.neuralNetwork.Layer;
import entities.neuralNetwork.Perceptron;
import gClasses.DataAssociator;
import gClasses.GRessourcesCollector;
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

public class Controler implements WindowListener, LearningStateListener {

	// repository
	private Perceptron per = new Perceptron();
	private double[][] data = null;
	private double[][] results = null;
	private Learner learner = null;

	// meta
	private Mode mode = Mode.NONE;

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

		frame.addWindowListener(this);
		frame.setVisible(true);
	}

	public void perceptronModified() {
		perceptronEditingPan.regen(per);
		perceptronDisplayer.setPerceptron(per);

		mainPan.repaint();
	}

	public synchronized void updateMode() {

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

		if (mode != Mode.HAS_PROCEED) {
			results = null;
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

		Starter.selectFileAndPerforme(frame, (file) -> {
			try {
				per = new Perceptron(new DataAssociator(file));
				perceptronModified();
				updateMode();
			} catch (Exception e2) {
				e2.printStackTrace();
				new ErrorInFilePopup();
			}
		});
	}

	public void savePer() {
		Starter.selectFileAndPerforme(frame, (file) -> per.toDataAssociator().save(file));
	}

	public void loadCsv() {
		Starter.selectFileAndPerforme(frame, (file) -> {
			try {
				data = getData(file);
				results = null;
				updateMode();
			} catch (IOException e) {
				e.printStackTrace();
				new ErrorInFilePopup();
			}
		});
	}

	public void saveCsv() {
		Starter.selectFileAndPerforme(frame, (file) -> {
			try {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				for (int i = 0; i < data.length; i++) {
					for (int j = 0; j < data[i].length; j++) {
						bos.write(Double.toString(data[i][j]).getBytes());
						if (j != data[i].length - 1) {
							bos.write(";".getBytes());
						}
					}
					bos.write(";".getBytes());
					for (int j = 0; j < results[i].length; j++) {
						bos.write(Double.toString(results[i][j]).getBytes());
						if (j != results[i].length - 1) {
							bos.write(";".getBytes());
						}
					}
					if (i != data.length - 1) {
						bos.write("\n".getBytes());
					}
				}
				bos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
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

	private double[][] getData(File fil) throws IOException {

		ArrayList<String> lineList = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new FileReader(fil));

		String line = br.readLine();
		while (line != null) {
			lineList.add(line);
			line = br.readLine();
		}

		br.close();

		double[][] data = new double[lineList.size()][getDoubleTable(lineList.get(0)).length];

		for (int i = 0; i < lineList.size(); i++) {

			double[] lineData = getDoubleTable(lineList.get(i));

			if (lineData.length == data[0].length) {
				data[i] = lineData;
			}
		}

		return data;
	}

	private double[] getDoubleTable(String str) {

		int valuesCount = 1;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == ';') {
				valuesCount++;
			}
		}

		double[] values = new double[valuesCount];

		for (int i = 0; i < values.length - 1; i++) {
			values[i] = Double.valueOf(str.substring(0, str.indexOf(';')));
			str = str.substring(str.indexOf(';') + 1, str.length());
		}

		values[values.length - 1] = Double.valueOf(str);

		return values;
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

		learner.addLearningStateListener(this);

		new LearnerObserver(this, learner);

		learner.start();

		updateMode();
	}

	@Override
	public void learningEnded(Learner source) {
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

	@Override
	public void windowClosing(WindowEvent arg0) {
		DataAssociator da = new DataAssociator(new File(Starter.def_dir + "/preferences"));

		da.setValue("maxIter", learningPan.getMaxIter());
		da.setValue("multiThreading", learningPan.getMultiThreading());

		da.save();
	}

	public enum Mode {
		NONE, WILL_LEARN, LEARNING, WILL_PROCEED, HAS_PROCEED;
	}

	@Override
	public void learningStarted(Learner source) {
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

}
