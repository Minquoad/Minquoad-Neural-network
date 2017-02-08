package interfaces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import entities.neuralNetwork.Layer;
import entities.neuralNetwork.Perceptron;
import entities.neuralNetwork.neurons.BlankNeuron;
import entities.neuralNetwork.neurons.ExpNeuron;
import entities.neuralNetwork.neurons.LnNeuron;
import entities.neuralNetwork.neurons.Neuron;
import entities.neuralNetwork.neurons.PeriodicNeuron;
import entities.neuralNetwork.neurons.SigNeuron;
import gClasses.GRessourcesCollector;
import gClasses.gInterfaces.GPanel;
import utilities.Starter;

public class PerceptronDisplayer extends GPanel {

	// meta
	private boolean valide;
	private int inputCount;

	// grapicals
	private static Color BACKGROUND = new Color(39, 40, 34);
	private static Color LINES_COLOR = new Color(204, 204, 204);
	private int[] nerveWeb;
	private ArrayList<Point> blankNeuronePosition;
	private ArrayList<NeuronDisplayer> neuronDisplayers = new ArrayList<NeuronDisplayer>();

	public PerceptronDisplayer() {
		this.setBackground(BACKGROUND);
	}

	public void setPerceptron(Perceptron per) {

		while (!neuronDisplayers.isEmpty()) {
			this.remove(neuronDisplayers.get(0));
			neuronDisplayers.remove(0);
		}

		valide = per.isValid();
		inputCount = per.getInputCount();

		blankNeuronePosition = new ArrayList<Point>();

		int layerCount = per.getLayerCount();

		nerveWeb = new int[layerCount];

		int maxNeuronsCountInALayer = 0;

		for (int i = 0; i < layerCount; i++) {

			Layer currentLayer = per.getLayer(i);

			int neuronsCountInCurrentLayer = currentLayer.getNeuroneCount();

			nerveWeb[i] = neuronsCountInCurrentLayer;

			maxNeuronsCountInALayer = Math.max(maxNeuronsCountInALayer, nerveWeb[i]);

			for (int j = 0; j < neuronsCountInCurrentLayer; j++) {
				if (currentLayer.getNeurone(j).getClass() == BlankNeuron.class) {
					blankNeuronePosition.add(new Point(i, j));
				}
			}

		}

		for (int i = 0; i < layerCount; i++) {

			Layer currentLayer = per.getLayer(i);

			int neuronsCountInCurrentLayer = currentLayer.getNeuroneCount();

			for (int j = 0; j < neuronsCountInCurrentLayer; j++) {

				Neuron currentNeuron = currentLayer.getNeurone(j);

				BufferedImage fond = null;

				if (i == 0 && j < per.getInputCount())
					fond = GRessourcesCollector.getBufferedImage("resources/pictures/neurons/neu.png");
				else if (currentNeuron.getClass() == BlankNeuron.class)
					fond = GRessourcesCollector.getBufferedImage("resources/pictures/neurons/blank.png");
				else if (currentNeuron.getClass() == ExpNeuron.class)
					fond = GRessourcesCollector.getBufferedImage("resources/pictures/neurons/exp.png");
				else if (currentNeuron.getClass() == LnNeuron.class)
					fond = GRessourcesCollector.getBufferedImage("resources/pictures/neurons/ln.png");
				else if (currentNeuron.getClass() == PeriodicNeuron.class)
					fond = GRessourcesCollector.getBufferedImage("resources/pictures/neurons/sin.png");
				else if (currentNeuron.getClass() == SigNeuron.class)
					fond = GRessourcesCollector.getBufferedImage("resources/pictures/neurons/sig.png");
				else if (currentNeuron.getClass() == Neuron.class)
					fond = GRessourcesCollector.getBufferedImage("resources/pictures/neurons/lin.png");

				NeuronDisplayer neuDisp = new NeuronDisplayer(fond);

				float w = (float) 1000 / (float) nerveWeb.length;
				float h = (float) 1000 / (float) nerveWeb[i];

				float fh = (float) 1000 / (float) maxNeuronsCountInALayer;

				Point position = new Point((int) ((w * i + w * (i + 1)) / 2), (int) ((h * j + h * (j + 1)) / 2));

				this.add(neuDisp, position.x - (int) (w / 2), position.y - (int) (fh / 2), (int) w, (int) fh);

				neuronDisplayers.add(neuDisp);

			}

		}

	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		g.setColor(new Color(11, 11, 10));
		g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
		g.setColor(new Color(60, 61, 56));
		g.drawRect(1, 1, this.getWidth() - 3, this.getHeight() - 3);

		if (nerveWeb != null) {

			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(2));
			g2.setColor(LINES_COLOR);

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if (valide) {

				int blueContourThickness = this.getWidth() / 25;
				g2.setPaint(new GradientPaint(0, 0, new Color(19, 71, 84), blueContourThickness, 0, BACKGROUND, true));
				Polygon polygon = new Polygon();
				polygon.addPoint(0, 0);
				polygon.addPoint(blueContourThickness, blueContourThickness);
				polygon.addPoint(blueContourThickness, this.getHeight() - blueContourThickness);
				polygon.addPoint(0, this.getHeight());
				g2.fillPolygon(polygon);
				g2.setPaint(new GradientPaint(this.getWidth(), 0, new Color(19, 71, 84),
						this.getWidth() - blueContourThickness, 0, BACKGROUND, true));
				polygon = new Polygon();
				polygon.addPoint(this.getWidth(), 0);
				polygon.addPoint(this.getWidth(), this.getHeight());
				polygon.addPoint(this.getWidth() - blueContourThickness, this.getHeight() - blueContourThickness);
				polygon.addPoint(this.getWidth() - blueContourThickness, blueContourThickness);
				g2.fillPolygon(polygon);
				g2.setPaint(new GradientPaint(0, 0, new Color(19, 71, 84), 0, blueContourThickness, BACKGROUND, true));
				polygon = new Polygon();
				polygon.addPoint(0, 0);
				polygon.addPoint(this.getWidth(), 0);
				polygon.addPoint(this.getWidth() - blueContourThickness, blueContourThickness);
				polygon.addPoint(blueContourThickness, blueContourThickness);
				g2.fillPolygon(polygon);
				g2.setPaint(new GradientPaint(0, this.getHeight(), new Color(19, 71, 84), 0,
						this.getHeight() + blueContourThickness, BACKGROUND, true));
				polygon = new Polygon();
				polygon.addPoint(this.getWidth() - blueContourThickness, this.getHeight() - blueContourThickness);
				polygon.addPoint(this.getWidth(), this.getHeight());
				polygon.addPoint(0, this.getHeight());
				polygon.addPoint(blueContourThickness, this.getHeight() - blueContourThickness);
				g2.fillPolygon(polygon);

			}

			g2.setPaint(new GradientPaint(0, 0,
					new Color(LINES_COLOR.getRed(), LINES_COLOR.getGreen(), LINES_COLOR.getBlue(), 0),
					this.getWidth() / (nerveWeb.length * 2), 0, LINES_COLOR, true));

			for (int j = 0; j < nerveWeb[0] && j < this.inputCount; j++) {

				float w1 = (float) this.getWidth() / (float) nerveWeb.length;
				float h1 = (float) this.getHeight() / (float) nerveWeb[0];

				Point firstPoint = new Point((int) ((w1 * 0 + w1 * (0 + 1)) / 2), (int) ((h1 * j + h1 * (j + 1)) / 2));

				Point secondPoint = new Point(2, firstPoint.y);

				g2.drawLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
			}

			for (int j = 0; j < nerveWeb[nerveWeb.length - 1]; j++) {

				float w1 = (float) this.getWidth() / (float) nerveWeb.length;
				float h1 = (float) this.getHeight() / (float) nerveWeb[(nerveWeb.length - 1)];

				Point firstPoint = new Point(
						(int) ((w1 * (nerveWeb.length - 1) + w1 * ((nerveWeb.length - 1) + 1)) / 2),
						(int) ((h1 * j + h1 * (j + 1)) / 2));

				Point secondPoint = new Point(this.getWidth() - 4, firstPoint.y);

				g2.drawLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
			}

			g2.setPaint(LINES_COLOR);

			for (int i = 0; i < nerveWeb.length - 1; i++) {

				for (int j = 0; j < nerveWeb[i]; j++) {

					float w1 = (float) this.getWidth() / (float) nerveWeb.length;
					float h1 = (float) this.getHeight() / (float) nerveWeb[i];

					Point firstPoint = new Point((int) ((w1 * i + w1 * (i + 1)) / 2),
							(int) ((h1 * j + h1 * (j + 1)) / 2));

					for (int k = 0; k < nerveWeb[i + 1]; k++) {

						float w2 = (float) this.getWidth() / (float) nerveWeb.length;
						float h2 = (float) this.getHeight() / (float) nerveWeb[i + 1];

						Point secondPoint = new Point((int) ((w2 * (i + 1) + w2 * (i + 2)) / 2),
								(int) ((h2 * k + h2 * (k + 1)) / 2));

						boolean isBlank = false;
						for (int l = 0; l < blankNeuronePosition.size() && !isBlank; l++) {
							isBlank |= blankNeuronePosition.get(l).getX() == i + 1
									&& blankNeuronePosition.get(l).getY() == k;
						}
						if (!isBlank) {
							g2.drawLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
						}
					}

				}
			}
		}
	}

	private class NeuronDisplayer extends JPanel {

		private BufferedImage fond;

		public NeuronDisplayer(BufferedImage fond) {

			this.setOpaque(false);

			this.fond = fond;

		}

		public void paintComponent(Graphics g) {

			super.paintComponent(g);

			float rate = Math.min((float) this.getWidth() / (float) fond.getWidth(),
					(float) this.getHeight() / (float) fond.getHeight());

			rate /= 2;

			int imW = (int) ((float) fond.getWidth() * rate);
			int imH = (int) ((float) fond.getHeight() * rate);

			if (rate == 1)
				g.drawImage(fond, (this.getWidth() - imW) / 2, (this.getHeight() - imH) / 2, imW, imH, this);
			else if (rate < 1) {
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
