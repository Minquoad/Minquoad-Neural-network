package interfaces;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.JTextPane;

import gClasses.gInterfaces.gPanel.GPanel;
import utilities.Preferences;

public class MainPan extends GPanel {

	private PerceptronEditingPan perceptronEditingPan = null;
	private PerceptronDisplayer perceptronDisplayer = null;
	private DataPan dataPan = null;
	private GPanel modePan = null;

	public MainPan() {
		this.setBackground(Preferences.BACKGROUND);
	}

	public void setModePan(GPanel modePan) {
		this.setComponentIfChanged(modePan, this.modePan, 0f, 0f, 0.5f, 0.6f);
		this.modePan = modePan;
	}

	public void setPerceptronEditingPan(PerceptronEditingPan perceptronEditingPan) {
		this.setComponentIfChanged(perceptronEditingPan, this.perceptronEditingPan, 0.5f, 0f, 0.5f, 0.3f);
		this.perceptronEditingPan = perceptronEditingPan;
	}

	public void setDataPan(DataPan dataPan) {
		this.setComponentIfChanged(dataPan, this.dataPan, 0f, 0.6f, 0.5f, 0.5f);
		this.dataPan = dataPan;
	}

	public void setPerceptronDisplayer(PerceptronDisplayer perceptronDisplayer) {
		this.setComponentIfChanged(perceptronDisplayer, this.perceptronDisplayer, 0.5f, 0.3f, 0.5f, 0.7f);
		this.perceptronDisplayer = perceptronDisplayer;
	}

	private void setComponentIfChanged(Component settedComp, Component memberComp, float x, float y, float w, float h) {
		if (settedComp != memberComp) {
			if (memberComp != null) {
				this.remove(memberComp);
			}
			if (settedComp != null) {
				this.add(settedComp, x, y, w, h);
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		int i = (int) ((float) this.getWidth() / 2f + 0.5f);
		g.setColor(new Color(44, 44, 43));
		g.drawLine(i, 0, i, this.getHeight());
		i++;
		g.setColor(new Color(55, 56, 51));
		g.drawLine(i, 0, i, this.getHeight());
		i++;
		g.setColor(new Color(44, 44, 43));
		g.drawLine(i, 0, i, this.getHeight());
	}

	public static JTextPane creadStandartJTextPane() {
		JTextPane taxtPane = new JTextPane();
		taxtPane.setEditable(false);
		taxtPane.setOpaque(false);
		taxtPane.setForeground(Preferences.FOREGROUND);
		return taxtPane;
	}

	public static JTextField getDoubleField() {
		JTextField dtf = new JTextField();
		dtf.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				String text = dtf.getText();

				String newText = formatDoubleString(text);

				newText = newText.substring(0, Math.min(newText.length(), 10));

				if (text != newText) {
					dtf.setText(newText);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {}
		});

		return dtf;
	}

	public static String formatDoubleString(String str) {
		int firstDot = str.indexOf('.');
		int lastDot = str.lastIndexOf('.');
		while (lastDot != firstDot) {

			str = str.substring(0, lastDot) + str.substring(lastDot + 1, str.length());

			firstDot = str.indexOf('.');
			lastDot = str.lastIndexOf('.');
		}

		int i = 0;
		while (i < str.length()) {
			char c = str.charAt(i);
			boolean charValid = false;
			charValid |= c == '.';
			for (int j = 0; j <= 9; j++) {
				charValid |= c == Integer.toString(j).charAt(0);
			}
			if (!charValid) {
				str = str.substring(0, i) + str.substring(i + 1, str.length());
			} else {
				i++;
			}
		}

		return str;
	}

	public static JTextField getIntegerField() {
		JTextField dtf = new JTextField();
		dtf.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				String text = dtf.getText();

				String newText = formatIntegerString(text);

				newText = newText.substring(0, Math.min(newText.length(), 10));

				if (text != newText) {
					dtf.setText(newText);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {}
		});

		return dtf;
	}

	public static String formatIntegerString(String str) {
		int i = 0;
		while (i < str.length()) {
			char c = str.charAt(i);
			boolean charValid = false;
			for (int j = 0; j <= 9; j++) {
				charValid |= c == Integer.toString(j).charAt(0);
			}
			if (!charValid) {
				str = str.substring(0, i) + str.substring(i + 1, str.length());
			} else {
				i++;
			}
		}

		return str;
	}

}
