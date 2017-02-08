package interfaces;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import gClasses.gInterfaces.GPanel;

public class MainPan extends GPanel {

	private PerceptronEditingPan perceptronEditingPan = null;
	private PerceptronDisplayer perceptronDisplayer = null;
	private DataPan dataPan = null;
	private GPanel modePan = null;

	public MainPan() {
		this.setBackground(new Color(23, 23, 20));
	}

	public void setModePan(GPanel modePan) {
		this.setComponentIfChanged(modePan, this.modePan, 0, 0, 500, 500);
		this.modePan = modePan;
	}

	public void setPerceptronEditingPan(PerceptronEditingPan perceptronEditingPan) {
		this.setComponentIfChanged(perceptronEditingPan, this.perceptronEditingPan, 500, 0, 500, 300);
		this.perceptronEditingPan = perceptronEditingPan;
	}

	public void setDataPan(DataPan dataPan) {
		this.setComponentIfChanged(dataPan, this.dataPan, 0, 500, 500, 500);
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

}
