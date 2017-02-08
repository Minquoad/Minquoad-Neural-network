package interfaces;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import gClasses.GRessourcesCollector;
import gClasses.gInterfaces.GPanel;
import utilities.Starter;

public class NeuronTypeSelecter extends JPanel {

	private Type selectedType;
	private static Color selectColor = new Color(19, 71, 84);

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

			buttonPan.add(neuTypeButton, 1000 * i / types.length, 0, 1000 / types.length, 1000);
			buttonList.add(neuTypeButton);
			neuTypeButton.addActionListener((e) -> {
				selectedType = type;
				for (int j = 0; j < buttonList.size(); j++) {
					buttonList.get(j).setEnabled(true);
					buttonList.get(j).setBackground(null);
				}
				neuTypeButton.setEnabled(false);
				neuTypeButton.setBackground(selectColor);
			});

			JTextPane textPane = new JTextPane();
			textPane.setEditable(false);
			textPane.setBackground(new Color(238, 238, 238));
			textPane.setContentType("text/html");
			textPane.setText("<body style='text-align: center;font-family: arial;'>" + typesDefinitions.get(type)[0]
					+ "</body>");
			labelPan.add(textPane, 1000 * i / types.length, 0, 1000 / types.length, 1000);

			textPane.addMouseListener(new MouseListener() {
				public void mouseReleased(MouseEvent arg0) {
				}

				public void mousePressed(MouseEvent arg0) {
					neuTypeButton.doClick();
				}

				public void mouseExited(MouseEvent arg0) {
				}

				public void mouseEntered(MouseEvent arg0) {
				}

				public void mouseClicked(MouseEvent arg0) {
				}
			});

			if (i == 0) {
				neuTypeButton.doClick();
			}
		}

	}

	public Type getSelectedType() {
		return selectedType;
	}

	public void paintComponent(Graphics g) {
		int labelPanHeight = 25;

		buttonPan.setBounds(0, 0, this.getWidth(), this.getHeight() - labelPanHeight);
		labelPan.setBounds(0, this.getHeight() - labelPanHeight, this.getWidth(), labelPanHeight);

		super.paintComponent(g);
	}

	public enum Type {
		LINEARE, CONSANT, SIGMOID, LOGARITHMIC, EXPONENTIAL, SINUSOIDAL;
	}

	private class NeuTypeButton extends JButton {

		private String picturePath;

		public NeuTypeButton(String picturePath) {
			this.picturePath = "resources/pictures/neurons/" + picturePath;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			BufferedImage fond = GRessourcesCollector.getBufferedImage(picturePath);

			float rate = Math.min((float) this.getWidth() / (float) fond.getWidth(),
					(float) (this.getHeight()) / (float) fond.getHeight());
			int imW = (int) ((float) fond.getWidth() * rate);
			int imH = (int) ((float) fond.getHeight() * rate);

			if (rate == 1) {
				g.drawImage(fond, (this.getWidth() - imW) / 2, (this.getHeight() - imH) / 2, imW, imH, this);
			} else if (rate < 1) {
				BufferedImage scaled = Starter.getScaledInstance(fond, imW, imH,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);

				g.drawImage(scaled, (this.getWidth() - imW) / 2, (this.getHeight() - imH) / 2, imW, imH, this);
			} else {
				BufferedImage scaled = Starter.getScaledInstance(fond, imW, imH,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR, false);

				g.drawImage(scaled, (this.getWidth() - imW) / 2, (this.getHeight() - imH) / 2, imW, imH, this);
			}

		}

	}

}
