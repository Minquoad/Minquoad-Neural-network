package interfaces;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JTextField;
import javax.swing.JTextPane;

import gClasses.gInterfaces.GPanel;
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
		this.setComponentIfChanged(modePan, this.modePan, 0, 0, 500, 600);
		this.modePan = modePan;
	}

	public void setPerceptronEditingPan(PerceptronEditingPan perceptronEditingPan) {
		this.setComponentIfChanged(perceptronEditingPan, this.perceptronEditingPan, 500, 0, 500, 300);
		this.perceptronEditingPan = perceptronEditingPan;
	}

	public void setDataPan(DataPan dataPan) {
		this.setComponentIfChanged(dataPan, this.dataPan, 0, 600, 500, 500);
		this.dataPan = dataPan;
	}

	public void setPerceptronDisplayer(PerceptronDisplayer perceptronDisplayer) {
		this.setComponentIfChanged(perceptronDisplayer, this.perceptronDisplayer, 500, 300, 500, 700);
		this.perceptronDisplayer = perceptronDisplayer;
	}

	private void setComponentIfChanged(Component settedComp, Component memberComp, int x, int y, int w, int h) {
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

		int i = this.getWidth() / 2;
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

	public static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint,
			boolean higherQuality) {
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage) img;
		int w, h;
		if (higherQuality) {
			w = img.getWidth();
			h = img.getHeight();
		} else {
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}
			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}
			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
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
