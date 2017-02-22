package interfaces.modPanel;

import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

import gClasses.GRessourcesCollector;
import gClasses.gInterfaces.gPanel.GPanel;
import interfaces.MainPan;

public class ModPanel extends GPanel {

	protected class MainButton extends JButton {

		private String picturePath;

		public MainButton(String picturePath) {
			this.picturePath = picturePath;
		}

		public void paintComponent(Graphics g) {

			super.paintComponent(g);

			BufferedImage fond = GRessourcesCollector.getBufferedImage(picturePath);

			float rate = Math.max((float) this.getWidth() / (float) fond.getWidth(),
					(float) (this.getHeight()) / (float) fond.getHeight());
			int imW = (int) ((float) fond.getWidth() * rate);
			int imH = (int) ((float) fond.getHeight() * rate);

			BufferedImage scaled = MainPan.getScaledInstance(fond, imW, imH,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);

			g.drawImage(scaled, (this.getWidth() - imW) / 2, (this.getHeight() - imH) / 2, imW, imH, this);
		}
	};

}
