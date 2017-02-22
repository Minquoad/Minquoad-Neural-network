package interfaces.modPanel;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import gClasses.gInterfaces.gPanel.GLayout;
import interfaces.MainPan;
import utilities.Controler;
import utilities.Preferences;

public class LearningPanel extends ModPanel {

	private LearningInfoPanel learningInfoPanel;
	private boolean learning = false;

	private MainButton runButton;
	private MainButton stopButton;
	private JSlider multiThreadingSlider;
	private JButton unlearnButton;
	private JButton clearButton;
	private JTextField maxIterField;
	private JTextField minProgressionField;
	private JButton infinitModButton;
	private boolean unlimitedIterations;
	private JTextArea learningInfoText;
	private JScrollPane learningInfoTextScroll;

	public LearningPanel(Controler controler) {

		this.setLayout(new LearningPanelLayout());

		unlimitedIterations = Preferences.isInterationsUnlimited();

		int cores = Runtime.getRuntime().availableProcessors();
		int multiThreading = Math.min(Preferences.getMultiThreading(), cores);

		JTextPane multiThreadingLabel = MainPan.creadStandartJTextPane();
		multiThreadingLabel.setText("Thread used : " + multiThreading);
		multiThreadingSlider = new JSlider(1, cores, multiThreading);
		multiThreadingSlider.setOpaque(false);
		multiThreadingSlider.addChangeListener((e) -> {
			multiThreadingLabel.setText("Thread used : " + multiThreadingSlider.getValue());
		});

		JTextPane maxIterLabel = MainPan.creadStandartJTextPane();
		maxIterLabel.setText("Max number of iterations : ");

		maxIterField = MainPan.getIntegerField();
		maxIterField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				unlimitedIterations = false;
			}
		});
		;
		maxIterField.setCaretColor(Preferences.FOREGROUND);
		if (unlimitedIterations) {
			maxIterField.setText("\u221E");
		} else {
			maxIterField.setText(Integer.toString(Preferences.getMaxIter()));
		}
		JPanel maxIterPanel = new JPanel();
		maxIterPanel.setLayout(new GridLayout());
		maxIterPanel.add(maxIterField);
		maxIterPanel.setOpaque(false);
		maxIterField.setOpaque(false);
		maxIterField.setForeground(Preferences.FOREGROUND);
		maxIterField.setBorder(BorderFactory.createEmptyBorder());
		maxIterPanel.setBorder(BorderFactory.createLineBorder(Preferences.HIGHLIGHTING));

		infinitModButton = new JButton("\u221E");
		infinitModButton.addActionListener((e) -> {
			this.maxIterField.setText("\u221E");
			unlimitedIterations = true;
		});

		JTextPane minProgressionLabel = MainPan.creadStandartJTextPane();
		minProgressionLabel.setText("Minimum progression per iterations (%) : ");

		minProgressionField = MainPan.getDoubleField();
		minProgressionField.setCaretColor(Preferences.FOREGROUND);
		minProgressionField.setText(Double.toString(Preferences.getMinimumProgressionPerIteration() * 100d));
		JPanel minProgressionPanel = new JPanel();
		minProgressionPanel.setLayout(new GridLayout());
		minProgressionPanel.add(minProgressionField);
		minProgressionPanel.setOpaque(false);
		minProgressionField.setOpaque(false);
		minProgressionField.setForeground(Preferences.FOREGROUND);
		minProgressionField.setBorder(BorderFactory.createEmptyBorder());
		minProgressionPanel.setBorder(BorderFactory.createLineBorder(Preferences.HIGHLIGHTING));

		learningInfoPanel = new LearningInfoPanel();

		learningInfoText = new JTextArea();
		learningInfoText.setEditable(false);
		learningInfoText.setTabSize(4);
		learningInfoText.setMargin(new Insets(0, 3, 0, 3));
		learningInfoText.setFont(new Font("monospaced", Font.BOLD, 12));
		learningInfoText.setBackground(Preferences.CONTENT_BACKGROUND);
		learningInfoText.setForeground(Preferences.FOREGROUND);
		learningInfoTextScroll = new JScrollPane(learningInfoText);
		learningInfoTextScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		learningInfoTextScroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Preferences.BORDER));

		learningInfoText.setText("");

		clearButton = new JButton("Clear");
		runButton = new MainButton("resources/pictures/computing.jpg");
		stopButton = new MainButton("resources/pictures/stopLearning.jpg");
		unlearnButton = new JButton("UNLEARN");

		multiThreadingLabel.setPreferredSize(new Dimension(multiThreadingLabel.getPreferredSize().width, 26));
		minProgressionLabel.setPreferredSize(new Dimension(minProgressionLabel.getPreferredSize().width, 26));
		multiThreadingSlider.setPreferredSize(new Dimension(160, 26));
		minProgressionPanel.setPreferredSize(new Dimension(160, 23));
		maxIterPanel.setPreferredSize(new Dimension(160, 23));
		infinitModButton.setPreferredSize(new Dimension(48, 23));

		this.add(multiThreadingLabel, 0, 0);
		if (cores != 1) {
			this.addAnchoredToRight(multiThreadingSlider, multiThreadingLabel, 3, 0);
		}

		this.addAnchoredToBottom(maxIterLabel, multiThreadingLabel, 0, 8);
		this.addAnchoredToRight(maxIterPanel, maxIterLabel, 3, 0);
		this.addAnchoredToRight(infinitModButton, maxIterPanel, 3, 0);

		this.addAnchoredToBottom(minProgressionLabel, maxIterLabel, 0, 8);
		this.addAnchoredToRight(minProgressionPanel, minProgressionLabel, 3, 0);

		float xThreadButtonsRectangle = 0f;
		float yThreadButtonsRectangle = 0.5f;
		float wThreadButtonsRectangle = 0.2f;
		float hThreadButtonsRectangle = 0.5f;
		this.add(runButton,
				xThreadButtonsRectangle,
				yThreadButtonsRectangle,
				wThreadButtonsRectangle,
				hThreadButtonsRectangle);
		this.add(stopButton,
				xThreadButtonsRectangle,
				yThreadButtonsRectangle,
				wThreadButtonsRectangle,
				hThreadButtonsRectangle);
		stopButton.setVisible(false);
		this.add(learningInfoPanel, 0.2f, 0.5f, 0.8f, 0.3f);
		this.add(learningInfoTextScroll, 0.2f, 0.8f, 0.8f, 0.2f);
		this.add(unlearnButton);
		this.add(clearButton);

		runButton.addActionListener((e) -> controler.startLearning());
		stopButton.addActionListener((e) -> controler.endLearning());
		clearButton.addActionListener((e) -> {
			learningInfoPanel.clear();
			learningInfoText.setText("");
			LearningPanel.this.validate();
		});
		unlearnButton.addActionListener((e) -> controler.unlearn());

	}

	private class LearningPanelLayout extends GLayout {
		@Override
		public void layoutContainer(Container parent) {
			super.layoutContainer(parent);

			unlearnButton.setBounds(
					0,
					(int) ((float) parent.getHeight() / 2f + 0.5f) - 26,
					(int) (0.2f * (float) parent.getWidth() + 0.5f),
					26);
			clearButton.setBounds(
					(int) ((float) parent.getWidth() - (0.2f * (float) parent.getWidth()) + 0.5),
					(int) ((float) parent.getHeight() / 2f + 0.5f) - 26,
					(int) (0.2f * (float) parent.getWidth() + 0.5f),
					26);
		}
	}

	public int getMaxIter() {
		if (unlimitedIterations) {
			return -1;
		} else {
			if (this.maxIterField.getText().length() == 0) {
				this.maxIterField.setText("0");
			}
			String str = MainPan.formatIntegerString(this.maxIterField.getText());
			int maxIter = Integer.parseInt(str);
			return maxIter;
		}
	}

	public int getMultiThreading() {
		return multiThreadingSlider.getValue();
	}

	public boolean isUnlimitedIterations() {
		return unlimitedIterations;
	}

	public double getMinimumProgressionPerIteration() {
		if (this.minProgressionField.getText().length() == 0) {
			this.minProgressionField.setText("0.0");
		}
		String str = MainPan.formatDoubleString(this.minProgressionField.getText());
		double mppi = Double.parseDouble(str) / 100d;
		return mppi;
	}

	public void appendInfo(int iter, double mse, double lastEvolution, long duration) {
		learningInfoPanel.appendInfo(iter, mse, lastEvolution, duration);
	}

	public void appendInfo(String str) {
		learningInfoText.append(str);
		learningInfoText.setCaretPosition(learningInfoText.getDocument().getLength());
	}

	public void setLearning(boolean learning) {
		if (learning != this.learning) {
			this.learning = learning;

			runButton.setVisible(!learning);
			stopButton.setVisible(learning);

			maxIterField.setEditable(!learning);
			minProgressionField.setEditable(!learning);

			multiThreadingSlider.setEnabled(!learning);
			unlearnButton.setEnabled(!learning);
			infinitModButton.setEnabled(!learning);
		}
	}

	public void startNewLearning() {
		learningInfoPanel.startNewLearning();
		if (learningInfoText.getText().length() != 0) {
			learningInfoText.append("\n");
		}
	}

}
