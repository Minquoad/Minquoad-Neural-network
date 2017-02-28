package interfaces;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JTextPane;

import gClasses.GRessourcesCollector;
import gClasses.gInterfaces.gPanel.GPanel;
import utilities.Preferences;

public class NeuronTypeSelecter extends GPanel {

	private Type selectedType;
	private GPanel buttonPan = new GPanel();
	private GPanel labelPan = new GPanel();
	private ArrayList<JButton> buttonList = new ArrayList<JButton>();

	public NeuronTypeSelecter() {

		this.setOpaque(false);

		Map<Type, String[]> typesDefinitions = new HashMap<Type, String[]>();
		typesDefinitions.put(Type.LINEARE, new String[] { "Lineare", "lin.png" });
		typesDefinitions.put(Type.CONSANT, new String[] { "Consant", "blank.png" });
		typesDefinitions.put(Type.SIGMOID, new String[] { "Sigmoid", "sig.png" });
		typesDefinitions.put(Type.LOGARITHMIC, new String[] { "Logarithmic", "ln.png" });
		typesDefinitions.put(Type.EXPONENTIAL, new String[] { "Exponential", "exp.png" });
		typesDefinitions.put(Type.SINUSOIDAL, new String[] { "Sinusoidal", "sin.png" });

		this.add(buttonPan);
		this.add(labelPan);

		Type[] types = Type.values();
		for (int i = 0; i < types.length; i++) {
			Type type = types[i];

			NeuTypeButton neuTypeButton = new NeuTypeButton(typesDefinitions.get(type)[1]);

			buttonPan.add(neuTypeButton, (float) i / (float) types.length, 0f, 1f / (float) types.length, 1f);
			buttonList.add(neuTypeButton);
			neuTypeButton.addActionListener((e) -> {
				selectedType = type;
				for (int j = 0; j < buttonList.size(); j++) {
					buttonList.get(j).setEnabled(true);
					buttonList.get(j).setBackground(null);
				}
				neuTypeButton.setEnabled(false);
				neuTypeButton.setBackground(Preferences.HIGHLIGHTING);
			});

			JTextPane textPane = new JTextPane();
			textPane.setEditable(false);
			textPane.setBackground(new Color(238, 238, 238));
			textPane.setContentType("text/html");
			textPane.setText("<body style='text-align: center;font-family: arial;'>" + typesDefinitions.get(type)[0]
					+ "</body>");
			labelPan.add(textPane, (float) i / (float) types.length, 0f, 1f / (float) types.length, 1f);

			textPane.addMouseListener(new MouseListener() {
				public void mouseReleased(MouseEvent arg0) {}

				public void mousePressed(MouseEvent arg0) {
					neuTypeButton.doClick();
				}

				public void mouseExited(MouseEvent arg0) {}

				public void mouseEntered(MouseEvent arg0) {}

				public void mouseClicked(MouseEvent arg0) {}
			});

			if (i == 0) {
				neuTypeButton.doClick();
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

	public Type getSelectedType() {
		return selectedType;
	}

	public enum Type {
		LINEARE, CONSANT, SIGMOID, LOGARITHMIC, EXPONENTIAL, SINUSOIDAL;
	}

	private class NeuTypeButton extends JButton {

		public NeuTypeButton(String picturePath) {
			BufferedImage fond = GRessourcesCollector.getBufferedImage("resources/pictures/neurons/" + picturePath);
			GPanel picturePanel = new GPanel();
			picturePanel.setBackgroundPicture(fond, GPanel.BackgroundDisplayType.FIT);
			this.add(picturePanel);
		}
	}

}
