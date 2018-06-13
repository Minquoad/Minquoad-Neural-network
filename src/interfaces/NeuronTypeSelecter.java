package interfaces;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JTextPane;

import entities.neuralNetwork.neurons.NeuronType;
import gClasses.gInterfaces.gPanel.GPanel;
import utilities.Configuration;

public class NeuronTypeSelecter extends GPanel {

	private NeuronType selectedType;
	private GPanel buttonPan = new GPanel();
	private GPanel labelPan = new GPanel();
	private ArrayList<JButton> buttonList = new ArrayList<JButton>();

	public NeuronTypeSelecter() {

		this.add(buttonPan);
		this.add(labelPan);

		boolean isFirst = true;
		NeuronType[] neuronTypes = NeuronType.values();
		for (int i = 0; i < neuronTypes.length; i++) {
			NeuronType neuronType = neuronTypes[i];
			
			NeuTypeButton neuTypeButton = new NeuTypeButton(neuronType);
			
			buttonPan.add(neuTypeButton, (float) i / (float) neuronTypes.length, 0f, 1f / (float) neuronTypes.length, 1f);
			buttonList.add(neuTypeButton);
			neuTypeButton.addActionListener(e -> {
				selectedType = neuronType;
				for (int j = 0; j < buttonList.size(); j++) {
					buttonList.get(j).setEnabled(true);
					buttonList.get(j).setBackground(null);
				}
				neuTypeButton.setEnabled(false);
				neuTypeButton.setBackground(Configuration.HIGHLIGHTING_COLOR);
			});

			JTextPane textPane = new JTextPane();
			textPane.setEditable(false);
			textPane.setBackground(new Color(238, 238, 238));
			textPane.setContentType("text/html");
			textPane.setText("<body style='text-align: center;font-family: arial;'>" + neuronType.toString()
					+ "</body>");
			labelPan.add(textPane, (float) i / (float) neuronTypes.length, 0f, 1f / (float) neuronTypes.length, 1f);

			textPane.addMouseListener(new MouseListener() {
				public void mouseReleased(MouseEvent arg0) {}

				public void mousePressed(MouseEvent arg0) {
					neuTypeButton.doClick();
				}

				public void mouseExited(MouseEvent arg0) {}

				public void mouseEntered(MouseEvent arg0) {}

				public void mouseClicked(MouseEvent arg0) {}
			});

			if (isFirst) {
				neuTypeButton.doClick();
				isFirst = false;
			}
		}
		
		this.addComponentBoundsSetter(thisNts -> {
			int labelPanHeight = 25;
			buttonPan.setBounds(0, 0, thisNts.getWidth(),
					thisNts.getHeight() - labelPanHeight);
			labelPan.setBounds(0, thisNts.getHeight() - labelPanHeight,
					thisNts.getWidth(), labelPanHeight);
		});
	}

	public NeuronType getSelectedType() {
		return selectedType;
	}

	private class NeuTypeButton extends JButton {

		public NeuTypeButton(NeuronType neuronType) {
			GPanel picturePanel = new GPanel();
			picturePanel.setBackgroundPicture(neuronType.getPicture(), GPanel.BackgroundDisplayType.FIT);
			this.add(picturePanel);
		}
	}

}
