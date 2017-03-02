package interfaces;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import entities.neuralNetwork.Perceptron;
import gClasses.gInterfaces.gPanel.GPanel;
import utilities.Controler;
import utilities.Preferences;

public class PerceptronEditingPan extends GPanel {

	// meta
	private Controler controler;
	private boolean occupied = false;

	// grapicals
	private NeuronTypeSelecter neuTypSel = new NeuronTypeSelecter();
	private GPanel perceptronAdaptablePan;
	private JButton validateButton;
	private JTextPane inputCountLabel;
	private JButton addInputButton;
	private JButton removeInputButton;
	private JButton resetButton;
	private JTextField neronCountToAddField;
	private JTextPane neronCountToAddLabel;
	private JPanel neronCountToAddFieldPanel;

	public PerceptronEditingPan(Controler controler) {
		this.controler = controler;

		inputCountLabel = MainPan.creadStandartJTextPane();
		inputCountLabel.setText("Input Neurones :");
		addInputButton = new JButton("+");
		removeInputButton = new JButton("-");
		validateButton = new JButton("Validate");
		validateButton.setFocusPainted(false);
		resetButton = new JButton("Reset");
		perceptronAdaptablePan = new GPanel();

		neronCountToAddLabel = MainPan.creadStandartJTextPane();
		neronCountToAddLabel.setText("Neurons added or removed : ");
		neronCountToAddField = MainPan.getIntegerField();
		neronCountToAddField.setText("1");
		neronCountToAddField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				String text = neronCountToAddField.getText();

				String newText = text.substring(0, Math.min(text.length(), 3));

				if (newText.length() != 0) {
					boolean containsOnly0s = true;
					for (int i = 0; i < newText.length(); i++) {
						containsOnly0s &= newText.charAt(i) == '0';
					}
					if (containsOnly0s) {
						newText = "1";
					}
				}

				if (!text.equals(newText)) {
					neronCountToAddField.setText(newText);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {}
		});
		neronCountToAddFieldPanel = MainPan.getFieldPanel();
		neronCountToAddFieldPanel.add(neronCountToAddField);
		neronCountToAddFieldPanel.setPreferredSize(new Dimension(64, 26));

		this.add(neuTypSel, 0, 0, 1, 1f / 3f);
		inputCountLabel.setPreferredSize(new Dimension(100, 26));
		this.addAnchoredToBottom(inputCountLabel, neuTypSel, 4, 4);
		this.addAnchoredToRight(addInputButton, inputCountLabel, 3, 0);
		this.addAnchoredToRight(removeInputButton, addInputButton, 3, 0);
		this.addAnchored(neronCountToAddLabel, perceptronAdaptablePan, 4, -30);
		this.addAnchoredToRight(neronCountToAddFieldPanel, neronCountToAddLabel, 3, 0);
		this.add(resetButton, 0.75f, 0.4f, 0.2f, 0.2f);
		this.add(validateButton, 0.5f, 0.4f, 0.2f, 0.2f);

		addInputButton.addActionListener((e) -> controler.incrementInputCount());
		removeInputButton.addActionListener((e) -> controler.decrementInputCount());
		validateButton.addActionListener((e) -> controler.togglePerceptronValidation());
		resetButton.addActionListener((e) -> controler.resetPerceptron());
	}

	public int getNeronCountToAdd() {
		if (this.neronCountToAddField.getText().length() == 0) {
			this.neronCountToAddField.setText("1");
		}
		String str = MainPan.formatIntegerString(this.neronCountToAddField.getText());
		int neronCountToAdd = Integer.parseInt(str);
		return neronCountToAdd;
	}

	public void regen(Perceptron per) {

		boolean perValide = per.isValid();

		inputCountLabel.setVisible(!perValide);
		addInputButton.setVisible(!perValide);
		removeInputButton.setVisible(!perValide);
		resetButton.setVisible(!perValide);
		perceptronAdaptablePan.setVisible(!perValide);
		neronCountToAddLabel.setVisible(!perValide);
		neronCountToAddFieldPanel.setVisible(!perValide);
		neuTypSel.setVisible(!perValide);

		if (perValide) {
			validateButton.setBackground(Preferences.HIGHLIGHTING);
			validateButton.setForeground(Preferences.FOREGROUND);
			validateButton.setText("Is valide");

			validateButton.setEnabled(true);
		} else {
			validateButton.setBackground(new JButton().getBackground());
			validateButton.setForeground(new JButton().getForeground());
			validateButton.setText("Validate");

			validateButton.setEnabled(per.isValidable());

			perceptronAdaptablePan.removeAll();

			int layerCount = per.getLayerCount();
			for (int i = 0; i < layerCount; i++) {

				JButton addNeuron = new JButton("N++");
				JButton removeNeuron = new JButton("N--");

				float addLayerButtonWidth = 0.3f;

				if (i == 0) {
					perceptronAdaptablePan.add(addNeuron, (float) i / (float) layerCount, 0f,
							1f / (float) layerCount - (float) addLayerButtonWidth / (float) layerCount
									+ (float) addLayerButtonWidth / (float) layerCount / 2f,
							1f / 3f);
				} else if (i == layerCount - 1) {
					perceptronAdaptablePan.add(addNeuron,
							(float) i / (float) layerCount + (float) addLayerButtonWidth / (float) layerCount / 2f,
							0f,
							1f / (float) layerCount - (float) addLayerButtonWidth / (float) layerCount
									+ (float) addLayerButtonWidth / (float) layerCount / 2f,
							1f / 3f);
				} else {
					perceptronAdaptablePan.add(addNeuron,
							(float) i / (float) layerCount + (float) addLayerButtonWidth / (float) layerCount / 2f,
							0f, 1f / (float) layerCount - (float) addLayerButtonWidth / (float) layerCount, 1f / 3f);
				}
				final int j = i;

				addNeuron.addActionListener(e -> controler.addNeuron(neuTypSel.getSelectedType(), j));

				if (j == 0) {
					perceptronAdaptablePan.add(removeNeuron, (float) j / (float) layerCount, 1f / 3f,
							1f / (float) layerCount - (float) addLayerButtonWidth / (float) layerCount
									+ (float) addLayerButtonWidth / (float) layerCount / 2f,
							1f / 3f);
				} else if (j == layerCount - 1) {
					perceptronAdaptablePan.add(removeNeuron,
							(float) j / (float) layerCount + (float) addLayerButtonWidth / (float) layerCount / 2f,
							1f / 3f,
							1f / (float) layerCount - (float) addLayerButtonWidth / (float) layerCount
									+ (float) addLayerButtonWidth / (float) layerCount / 2f,
							1f / 3f);
				} else {
					perceptronAdaptablePan.add(removeNeuron,
							(float) j / (float) layerCount + (float) addLayerButtonWidth / (float) layerCount / 2f,
							1f / 3f,
							1f / (float) layerCount - (float) addLayerButtonWidth / (float) layerCount, 1f / 3f);
				}
				removeNeuron.addActionListener(e -> controler.removeNeuron(j));

				if (per.getLayer(j).getNeuroneCount() == 0) {
					removeNeuron.setEnabled(false);
				}
				if (j != layerCount - 1) {
					JButton addLayer = new JButton("L++");

					perceptronAdaptablePan
							.add(addLayer,
									(float) j / (float) layerCount + 1f / (float) layerCount
											- (float) addLayerButtonWidth / (float) layerCount
											+ (float) addLayerButtonWidth / (float) layerCount / 2f,
									0f, (float) addLayerButtonWidth / (float) layerCount, 1f);

					addLayer.addActionListener(e -> controler.addLayer(j + 1));
				}

				JButton removeLayer = new JButton("L--");

				if (j == 0) {
					perceptronAdaptablePan.add(removeLayer, (float) j / (float) layerCount, 2f / 3f,
							1f / (float) layerCount - (float) addLayerButtonWidth / (float) layerCount
									+ (float) addLayerButtonWidth / (float) layerCount / 2f,
							1f / 3f);
				} else if (j == layerCount - 1) {
					perceptronAdaptablePan.add(removeLayer,
							(float) j / (float) layerCount + (float) addLayerButtonWidth / (float) layerCount / 2f,
							2f / 3f,
							1f / (float) layerCount - (float) addLayerButtonWidth / (float) layerCount
									+ (float) addLayerButtonWidth / (float) layerCount / 2f,
							1f / 3f);
				} else {
					perceptronAdaptablePan.add(removeLayer,
							(float) j / (float) layerCount + (float) addLayerButtonWidth / (float) layerCount / 2f,
							2f / 3f,
							1f / (float) layerCount - (float) addLayerButtonWidth / (float) layerCount, 1f / 3f);
				}
				removeLayer.addActionListener(e -> controler.removeLayer(j));
				if (j == 0 || j == layerCount - 1) {
					removeLayer.setEnabled(false);
				}
			}

			this.add(perceptronAdaptablePan, 0, 2f / 3f, 1, 1f / 3f);

		}
	}

	public void setOccupied(boolean occupied) {
		if (occupied != this.occupied) {
			this.occupied = occupied;
			validateButton.setEnabled(!occupied);
		}
	}

}
