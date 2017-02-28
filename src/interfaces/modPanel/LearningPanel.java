package interfaces.modPanel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import interfaces.MainPan;
import threads.LearningMode;
import utilities.Controler;
import utilities.Preferences;

public class LearningPanel extends ModPanel {

	private LearningInfoPanel learningInfoPanel;
	private boolean occupied = false;

	private MainButton runButton;
	private MainButton stopButton;
	private JSlider multiThreadingSlider;
	private JButton unlearnButton;
	private JButton randomizeSamplesOrderButton;
	private JButton clearButton;
	private JTextField maxIterField;
	private JTextField minProgressionField;
	private JButton infinitModButton;
	private boolean unlimitedIterations;
	private JTextArea learningInfoText;
	private JScrollPane learningInfoTextScroll;
	private JComboBox<LearningMode> modComboBox;

	public LearningPanel(Controler controler) {

		// multithreading

		int cores = Runtime.getRuntime().availableProcessors();
		int multiThreading = Math.min(Preferences.getMultiThreading(), cores);

		JTextPane multiThreadingLabel = MainPan.creadStandartJTextPane();
		multiThreadingLabel.setText("Thread used : " + multiThreading);
		multiThreadingSlider = new JSlider(1, cores, multiThreading);
		multiThreadingSlider.setOpaque(false);
		multiThreadingSlider.addChangeListener((e) -> {
			multiThreadingLabel.setText("Thread used : " + multiThreadingSlider.getValue());
		});

		// max iterration

		JTextPane maxIterLabel = MainPan.creadStandartJTextPane();
		maxIterLabel.setText("Max number of iterations : ");

		unlimitedIterations = Preferences.isInterationsUnlimited();

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

		maxIterField.setCaretColor(Preferences.FOREGROUND);
		if (unlimitedIterations) {
			maxIterField.setText("\u221E");
		} else {
			maxIterField.setText(Integer.toString(Preferences.getMaxIter()));
		}
		JPanel maxIterFieldPanel = LearningPanel.getFieldPanel();
		maxIterFieldPanel.add(maxIterField);

		infinitModButton = new JButton("\u221E");
		infinitModButton.addActionListener(e -> {
			this.maxIterField.setText("\u221E");
			unlimitedIterations = true;
		});

		// minimum progression

		JTextPane minProgressionLabel = MainPan.creadStandartJTextPane();
		minProgressionLabel.setText("Minimum progression per iterations (%) : ");

		minProgressionField = MainPan.getDoubleField();
		minProgressionField.setCaretColor(Preferences.FOREGROUND);
		minProgressionField.setText(Double.toString(Preferences.getMinimumProgressionPerIteration() * 100d));
		JPanel minProgressionFieldPanel = LearningPanel.getFieldPanel();
		minProgressionFieldPanel.add(minProgressionField);

		// learning mode

		JTextPane modeLabel = MainPan.creadStandartJTextPane();
		modeLabel.setText("Learning mode : ");
		modComboBox = new JComboBox<LearningMode>(LearningMode.values());
		modComboBox.setSelectedItem(Preferences.getLearningMode());

		// info displayer

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

		// action buttons

		clearButton = new JButton("Clear");
		runButton = new MainButton("resources/pictures/computing.jpg");
		stopButton = new MainButton("resources/pictures/stopLearning.jpg");
		unlearnButton = new JButton("UNLEARN");
		randomizeSamplesOrderButton = new JButton("Randomize samples order");

		// sizing

		multiThreadingLabel.setPreferredSize(new Dimension(multiThreadingLabel.getPreferredSize().width, 26));
		minProgressionLabel.setPreferredSize(new Dimension(minProgressionLabel.getPreferredSize().width, 26));
		maxIterLabel.setPreferredSize(new Dimension(maxIterLabel.getPreferredSize().width, 26));
		modeLabel.setPreferredSize(new Dimension(modeLabel.getPreferredSize().width, 26));
		Dimension fieldDimension = new Dimension(160, 26);
		minProgressionFieldPanel.setPreferredSize(fieldDimension);
		maxIterFieldPanel.setPreferredSize(fieldDimension);
		multiThreadingSlider.setPreferredSize(fieldDimension);
		randomizeSamplesOrderButton.setPreferredSize(new Dimension(185, fieldDimension.height));
		modComboBox.setPreferredSize(new Dimension(195, fieldDimension.height));
		infinitModButton.setPreferredSize(new Dimension(48, 26));

		// placing

		this.add(multiThreadingLabel, 0f, 0f);
		if (cores != 1) {
			this.addAnchoredToRight(multiThreadingSlider, multiThreadingLabel, 3, 0);
		}

		this.addAnchoredToBottom(maxIterLabel, multiThreadingLabel, 0, 8);
		this.addAnchoredToRight(maxIterFieldPanel, maxIterLabel, 3, -2);
		this.addAnchoredToRight(infinitModButton, maxIterFieldPanel, 3, 0);

		this.addAnchoredToBottom(minProgressionLabel, maxIterLabel, 0, 8);
		this.addAnchoredToRight(minProgressionFieldPanel, minProgressionLabel, 3, -2);

		this.addAnchoredToBottom(modeLabel, minProgressionLabel, 0, 8);
		this.addAnchoredToRight(modComboBox, modeLabel, 3, -2);
		modComboBox.getModel().addListDataListener(new ListDataListener() {
			@Override
			public void intervalRemoved(ListDataEvent arg0) {}

			@Override
			public void intervalAdded(ListDataEvent arg0) {}

			@Override
			public void contentsChanged(ListDataEvent arg0) {
				randomizeSamplesOrderButton.setVisible(
						modComboBox.getSelectedItem() == LearningMode.WITH_CONTROL_SAMPLE);
			}
		});
		this.addAnchoredToRight(randomizeSamplesOrderButton, modComboBox, 3, 0);
		randomizeSamplesOrderButton.setVisible(
				modComboBox.getSelectedItem() == LearningMode.WITH_CONTROL_SAMPLE);

		stopButton.setVisible(false);
		this.add(learningInfoPanel, 0.2f, 0.5f, 0.8f, 0.25f);
		this.add(learningInfoTextScroll, 0.2f, 0.75f, 0.8f, 0.25f);
		this.add(runButton);
		this.add(stopButton);
		this.add(unlearnButton);
		this.add(clearButton);

		this.addComponentBoundsSetter(thisLp -> {
			float xThreadButtonsRectangle = 0f;
			float yThreadButtonsRectangle = 0.5f;
			float wThreadButtonsRectangle = 0.2f;
			float hThreadButtonsRectangle = 0.5f;
			runButton.setBounds(
					(int) ((float) thisLp.getWidth() * xThreadButtonsRectangle + 0.5f),
					(int) ((float) thisLp.getHeight() * yThreadButtonsRectangle + 0.5f),
					(int) ((float) thisLp.getWidth() * wThreadButtonsRectangle + 0.5f),
					(int) ((float) thisLp.getHeight() * hThreadButtonsRectangle + 0.5f) - 26);
			stopButton.setBounds(
					(int) ((float) thisLp.getWidth() * xThreadButtonsRectangle + 0.5f),
					(int) ((float) thisLp.getHeight() * yThreadButtonsRectangle + 0.5f),
					(int) ((float) thisLp.getWidth() * wThreadButtonsRectangle + 0.5f),
					(int) ((float) thisLp.getHeight() * hThreadButtonsRectangle + 0.5f) - 26);
			unlearnButton.setBounds(
					0,
					thisLp.getHeight() - 26,
					(int) ((float) thisLp.getWidth() * wThreadButtonsRectangle + 0.5f),
					26);
			clearButton.setBounds(
					(int) ((float) thisLp.getWidth() - (0.2f * (float) thisLp.getWidth())
							+ 0.5),
					(int) ((float) thisLp.getHeight() / 2f + 0.5f) - 26,
					(int) (0.2f * (float) thisLp.getWidth() + 0.5f),
					26);
		});

		// action performed

		runButton.addActionListener((e) -> controler.startLearning());
		stopButton.addActionListener((e) -> controler.handleUserRequestLearningEnd());
		clearButton.addActionListener((e) -> {
			learningInfoPanel.clear();
			learningInfoText.setText("");
			LearningPanel.this.validate();
		});
		unlearnButton.addActionListener((e) -> controler.unlearn());
		randomizeSamplesOrderButton.addActionListener((e) -> controler.randomizeSamplesOrder());

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

	public LearningMode getLearningMode() {
		return (LearningMode) modComboBox.getSelectedItem();
	}

	public void appendInfo(int iter, double squareError, double lastEvolution, long duration) {
		learningInfoPanel.appendInfo(iter, squareError, lastEvolution, duration);
	}

	public void appendInfo(String str) {
		if (learningInfoText.getText().length() == 0) {
			while (str.indexOf("\n") == 0) {
				str = str.replaceFirst("\n", "");
			}
		}
		learningInfoText.append(str);
		learningInfoText.setCaretPosition(learningInfoText.getDocument().getLength());
	}

	public void setOccupied(boolean occupied) {
		if (occupied != this.occupied) {
			this.occupied = occupied;

			runButton.setVisible(!occupied);
			stopButton.setVisible(occupied);

			maxIterField.setEditable(!occupied);
			minProgressionField.setEditable(!occupied);

			multiThreadingSlider.setEnabled(!occupied);
			unlearnButton.setEnabled(!occupied);
			infinitModButton.setEnabled(!occupied);

			modComboBox.setEnabled(!occupied);
			randomizeSamplesOrderButton.setEnabled(!occupied);
		}
	}

	public void startNewLearning() {
		learningInfoPanel.startNewLearning();
		if (learningInfoText.getText().length() != 0) {
			learningInfoText.append("\n");
		}
	}

	public static JPanel getFieldPanel() {
		JPanel fp = new JPanel();
		fp.setLayout(new GridLayout());
		fp.setOpaque(false);
		fp.setBorder(BorderFactory.createLineBorder(Preferences.HIGHLIGHTING));
		return fp;
	}

}
