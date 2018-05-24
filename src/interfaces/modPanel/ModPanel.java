package interfaces.modPanel;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

import gClasses.GRessourcesCollector;
import gClasses.gInterfaces.gPanel.GPanel;
import utilities.Controller;

public abstract class ModPanel extends GPanel {

	private boolean curveMode = false;
	private JButton switchModeButton;
	private boolean occupied = false;

	public ModPanel(Controller controler) {

		JButton clearButton = new JButton("Clear");
		switchModeButton = new JButton("Toggle Learning/Processing Mode");

		this.add(clearButton);
		this.addComponentBoundsSetter(thisPp -> {
			clearButton.setBounds(
					(int) ((float) thisPp.getWidth() - (0.2f * (float) thisPp.getWidth())
							+ 0.5),
					(int) ((float) thisPp.getHeight() / 2f + 0.5f) - 26,
					(int) (0.2f * (float) thisPp.getWidth() + 0.5f),
					26);

		});
		this.add(switchModeButton);
		this.addComponentBoundsSetter(thisPp -> {
			switchModeButton.setBounds(
					0,
					(int) ((float) thisPp.getHeight() / 2f + 0.5f) - 26,
					(int) (0.2f * (float) thisPp.getWidth() + 0.5f),
					26);

		});
		switchModeButton.setVisible(curveMode);

		clearButton.addActionListener(e -> clear());
		switchModeButton.addActionListener(e -> controler.toggleCurveApplicationMode());
	}

	protected abstract void clear();

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

			BufferedImage scaled = GPanel.getScaledInstance(fond, imW, imH);

			g.drawImage(scaled, (this.getWidth() - imW) / 2, (this.getHeight() - imH) / 2, imW, imH, this);
		}
	}

	public boolean isCurveMode() {
		return curveMode;
	}

	public void setCurveMode(boolean curveMode) {
		if (curveMode != this.curveMode) {
			this.curveMode = curveMode;

			switchModeButton.setVisible(curveMode);
		}
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		if (occupied != this.occupied) {
			this.occupied = occupied;

			switchModeButton.setEnabled(!occupied);
		}
	}

}
