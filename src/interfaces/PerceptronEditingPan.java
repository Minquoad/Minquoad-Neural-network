package interfaces;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JTextPane;

import entities.neuralNetwork.Perceptron;
import gClasses.gInterfaces.GPanel;
import utilities.Controler;
import utilities.Starter;

public class PerceptronEditingPan extends GPanel {

	// meta
	private Controler controler;
	private boolean learning = false;

	// grapicals
	private NeuronTypeSelecter neuTypSel = new NeuronTypeSelecter();
	private GPanel perceptronAdaptablePan;
	private JButton validateButton;
	private JTextPane inputCountLabel;
	private JButton addInputButton;
	private JButton removeInputButton;
	private JButton resetButton;

	public PerceptronEditingPan(Controler controler) {
		this.controler = controler;

		this.setOpaque(false);

		inputCountLabel = Starter.getCenteredTextZone("Input Neurones :");
		addInputButton = new JButton("+");
		removeInputButton = new JButton("-");
		validateButton = new JButton("Validate");
		resetButton = new JButton("Reset");
		perceptronAdaptablePan = new GPanel();

		validateButton.setFocusPainted(false);

		this.add(neuTypSel, 0, 0, 1000, 333);
		inputCountLabel.setPreferredSize(new Dimension(120, 26));
		this.add(inputCountLabel, 25, 400);
		this.addToRight(addInputButton, inputCountLabel, 3);
		this.addToRight(removeInputButton, addInputButton, 3);
		this.add(resetButton, 750, 400, 200, 200);
		this.add(validateButton, 500, 400, 200, 200);

		addInputButton.addActionListener((e) -> controler.incrementInputCount());
		removeInputButton.addActionListener((e) -> controler.decrementInputCount());
		validateButton.addActionListener((e) -> controler.togglePerceptronValidation());
		resetButton.addActionListener((e) -> controler.resetPerceptron());
	}

	public void regen(Perceptron per) {

		boolean perValide = per.isValid();

		inputCountLabel.setVisible(!perValide);
		addInputButton.setVisible(!perValide);
		removeInputButton.setVisible(!perValide);
		resetButton.setVisible(!perValide);
		perceptronAdaptablePan.setVisible(!perValide);

		if (perValide) {
			validateButton.setBackground(new Color(19, 71, 84));
			validateButton.setForeground(new Color(167, 236, 33));
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

				int addLayerButtonWidth = 300;

				if (i == 0) {
					perceptronAdaptablePan.add(addNeuron, 1000 * i / layerCount, 0,
							1000 / layerCount - addLayerButtonWidth / layerCount + addLayerButtonWidth / layerCount / 2,
							333);
				} else if (i == layerCount - 1) {
					perceptronAdaptablePan.add(addNeuron, 1000 * i / layerCount + addLayerButtonWidth / layerCount / 2,
							0,
							1000 / layerCount - addLayerButtonWidth / layerCount + addLayerButtonWidth / layerCount / 2,
							333);
				} else {
					perceptronAdaptablePan.add(addNeuron, 1000 * i / layerCount + addLayerButtonWidth / layerCount / 2,
							0, 1000 / layerCount - addLayerButtonWidth / layerCount, 333);
				}
				final int j = i;

				addNeuron.addActionListener(e -> controler.addNeuron(neuTypSel.getSelectedType(), j));

				if (j == 0) {
					perceptronAdaptablePan.add(removeNeuron, 1000 * j / layerCount, 333,
							1000 / layerCount - addLayerButtonWidth / layerCount + addLayerButtonWidth / layerCount / 2,
							333);
				} else if (j == layerCount - 1) {
					perceptronAdaptablePan.add(removeNeuron,
							1000 * j / layerCount + addLayerButtonWidth / layerCount / 2, 333,
							1000 / layerCount - addLayerButtonWidth / layerCount + addLayerButtonWidth / layerCount / 2,
							333);
				} else {
					perceptronAdaptablePan.add(removeNeuron,
							1000 * j / layerCount + addLayerButtonWidth / layerCount / 2, 333,
							1000 / layerCount - addLayerButtonWidth / layerCount, 333);
				}
				removeNeuron.addActionListener(e -> controler.removeNeuron(j));

				if (per.getLayer(j).getNeuroneCount() == 0) {
					removeNeuron.setEnabled(false);
				}
				if (j != layerCount - 1) {
					JButton addLayer = new JButton("L++");

					perceptronAdaptablePan
							.add(addLayer,
									1000 * j / layerCount + 1000 / layerCount - addLayerButtonWidth / layerCount
											+ addLayerButtonWidth / layerCount / 2,
									0, addLayerButtonWidth / layerCount, 1000);

					addLayer.addActionListener(e -> controler.addLayer(j+1));
				}

				JButton removeLayer = new JButton("L--");

				if (j == 0) {
					perceptronAdaptablePan.add(removeLayer, 1000 * j / layerCount, 666,
							1000 / layerCount - addLayerButtonWidth / layerCount + addLayerButtonWidth / layerCount / 2,
							333);
				} else if (j == layerCount - 1) {
					perceptronAdaptablePan.add(removeLayer,
							1000 * j / layerCount + addLayerButtonWidth / layerCount / 2, 666,
							1000 / layerCount - addLayerButtonWidth / layerCount + addLayerButtonWidth / layerCount / 2,
							333);
				} else {
					perceptronAdaptablePan.add(removeLayer,
							1000 * j / layerCount + addLayerButtonWidth / layerCount / 2, 666,
							1000 / layerCount - addLayerButtonWidth / layerCount, 333);
				}
				removeLayer.addActionListener(e -> controler.removeLayer(j));
				if (j == 0 || j == layerCount - 1) {
					removeLayer.setEnabled(false);
				}
			}

			this.add(perceptronAdaptablePan, 0, 666, 1000, 333);

		}
	}

	public void setLearning(boolean learning) {
		if (learning != this.learning) {
			this.learning = learning;
			validateButton.setEnabled(!learning);
		}
	}

}
