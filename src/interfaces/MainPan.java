package interfaces;

import java.awt.Component;
import java.awt.Graphics;

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
		this.setComponentIfChanged(dataPan, this.dataPan, 0f, 0.6f, 0.5f, 0.4f);
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
		g.setColor(Preferences.BORDER);
		g.drawLine(i, 0, i, this.getHeight());
	}

}
