package interfaces.modPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import utilities.Controler;
import utilities.PreferencesHelper;
import utilities.Starter;

public class LearningPanel extends ModPanel {

	private final int maxMaxIter = 10_000;
	private final double sliderprecision = 20;

	private int maxIter;
	private int multiThreading;

	private JTextArea learningInfoArea;
	private boolean learning = false;

	private MainButton runButton;
	private MainButton stopButton;
	private JSlider maxIterSlider;
	private JSlider multiThreadingSlider;
	private JButton unlearnButton;

	public LearningPanel(Controler controler) {

		int cores = Runtime.getRuntime().availableProcessors();

		maxIter = Math.min(PreferencesHelper.getSavedIter(), maxMaxIter);
		multiThreading = Math.min(PreferencesHelper.getSavedMultiThreading(), cores);

		JTextPane maxIterLabel = Starter.getCenteredTextZone("Max iterations : " + maxIter);

		double sliderPower = Math.pow(10d, 1d / sliderprecision);

		double sliderValue = -1;
		if (maxIter != 0) {
			sliderValue = Math.log(maxIter) / Math.log(sliderPower);
		}
		double sliderMaxValue = Math.log(maxMaxIter) / Math.log(sliderPower);

		maxIterSlider = new JSlider(-1, (int) (0.5d + sliderMaxValue), (int) (0.5d + sliderValue));
		maxIterSlider.setOpaque(false);
		maxIterSlider.addChangeListener((e) -> {

			int value = maxIterSlider.getValue();
			if (value == -1) {
				maxIter = 0;
			} else {
				maxIter = (int) (0.5d + Math.pow(sliderPower, (double) value));
			}

			maxIterLabel.setText("<body style='text-align: center;font-family: arial;color: rgb(204, 204, 204);'>"
					+ "Max iterations : " + maxIter + "</body>");
		});

		JTextPane multiThreadingLabel = Starter.getCenteredTextZone("Thread used : " + multiThreading);
		multiThreadingSlider = new JSlider(1, cores, multiThreading);
		multiThreadingSlider.setOpaque(false);
		multiThreadingSlider.addChangeListener((e) -> {
			multiThreading = multiThreadingSlider.getValue();
			multiThreadingLabel
					.setText("<body style='text-align: center;font-family: arial;color: rgb(204, 204, 204);'>"
							+ "Thread used : " + multiThreading + "</body>");
		});

		learningInfoArea = new JTextArea();
		learningInfoArea.setEditable(false);
		learningInfoArea.setMargin(new Insets(0, 3, 0, 3));
		learningInfoArea.setFont(new Font("monospaced", Font.PLAIN, 12));
		learningInfoArea.setBackground(new Color(39, 40, 34));
		learningInfoArea.setForeground(new Color(204, 204, 204));
		JScrollPane scrPan = new JScrollPane(learningInfoArea);

		JButton clearButton = new JButton("Clear");

		runButton = new MainButton("resources/pictures/computing.jpg");

		stopButton = new MainButton("resources/pictures/stopLearning.jpg");

		unlearnButton = new JButton("UNLEARN");

		maxIterLabel.setPreferredSize(new Dimension(160, 26));
		this.add(maxIterLabel, 0, 0);
		maxIterSlider.setPreferredSize(new Dimension(280, 26));
		this.addToRight(maxIterSlider, maxIterLabel, 8);
		multiThreadingLabel.setPreferredSize(new Dimension(160, 26));
		this.addToBottom(multiThreadingLabel, maxIterLabel, 8);
		multiThreadingSlider.setPreferredSize(new Dimension(160, 26));
		this.addToRight(multiThreadingSlider, multiThreadingLabel, 8);

		this.add(runButton, 0, 500, 200, 500);
		this.add(stopButton, 0, 500, 200, 500);
		stopButton.setVisible(false);
		this.add(scrPan, 200, 500, 800, 500);
		this.add(clearButton, 880, 450, 120, 50);
		this.add(unlearnButton);

		runButton.addActionListener((e) -> controler.startLearning());
		stopButton.addActionListener((e) -> controler.endLearning());
		clearButton.addActionListener((e) -> learningInfoArea.setText(""));
		unlearnButton.addActionListener((e) -> controler.unlearn());

	}

	public int getMaxIter() {
		return maxIter;
	}

	public int getMultiThreading() {
		return multiThreading;
	}

	public void appendText(String str) {
		learningInfoArea.append(str);
		learningInfoArea.setCaretPosition(learningInfoArea.getDocument().getLength());
	}

	public void setLearning(boolean learning) {
		if (learning != this.learning) {
			this.learning = learning;

			runButton.setVisible(!learning);
			stopButton.setVisible(learning);

			maxIterSlider.setEnabled(!learning);
			multiThreadingSlider.setEnabled(!learning);
			unlearnButton.setEnabled(!learning);
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		unlearnButton.setBounds(0, this.getHeight() / 2 - 26, 200 * this.getWidth() / 1000, 26);
	}

}
