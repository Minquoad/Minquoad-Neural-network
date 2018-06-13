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

import entities.neuralNetwork.Layer;
import entities.neuralNetwork.Perceptron;
import entities.neuralNetwork.neurons.BlankNeuron;
import entities.neuralNetwork.neurons.Neuron;
import entities.neuralNetwork.neurons.NeuronType;
import gClasses.GResourcesCollector;
import gClasses.gInterfaces.gPanel.GPanel;
import utilities.Configuration;

public class PerceptronDisplayer extends GPanel {

	private static final BufferedImage INPUT_NEURON_PICTURE = GResourcesCollector
			.getBufferedImage("resources/pictures/neurons/neu.png");

	// meta
	private boolean valide;
	private int inputCount;

	// grapicals
	private int[] nerveWeb;
	private ArrayList<Point> blankNeuronePosition;
	private ArrayList<GPanel> neuronDisplayers = new ArrayList<GPanel>();

	public PerceptronDisplayer() {
		this.setBackground(Configuration.CONTENT_BACKGROUND_COLOR);
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

				fond = NeuronType.getEnumFromInstance(currentNeuron).getPicture();
				if (i == 0 && j < per.getInputCount())
					fond = INPUT_NEURON_PICTURE;

				GPanel neuDisp = new GPanel();
				neuDisp.setBackgroundPicture(fond, GPanel.BackgroundDisplayType.FIT);

				float w = 1f / (float) nerveWeb.length;
				float h = 1f / (float) nerveWeb[i];

				float fh = 1f / (float) maxNeuronsCountInALayer;

				float x = (w * (float) i + w * ((float) i + 1f)) / 2f;
				float y = (h * (float) j + h * ((float) j + 1f)) / 2f;

				this.add(neuDisp, x - (w / 4), y - (fh / 4), w / 2, fh / 2);

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
			g2.setColor(Configuration.FOREGROUND_COLOR);

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if (valide) {

				int blueContourThickness = this.getWidth() / 25;
				g2.setPaint(new GradientPaint(0, 0, Configuration.HIGHLIGHTING_COLOR, blueContourThickness, 0,
						Configuration.CONTENT_BACKGROUND_COLOR, true));
				Polygon polygon = new Polygon();
				polygon.addPoint(0, 0);
				polygon.addPoint(blueContourThickness, blueContourThickness);
				polygon.addPoint(blueContourThickness, this.getHeight() - blueContourThickness);
				polygon.addPoint(0, this.getHeight());
				g2.fillPolygon(polygon);
				g2.drawPolygon(polygon);
				g2.setPaint(new GradientPaint(this.getWidth(), 0, Configuration.HIGHLIGHTING_COLOR,
						this.getWidth() - blueContourThickness, 0, Configuration.CONTENT_BACKGROUND_COLOR, true));
				polygon = new Polygon();
				polygon.addPoint(this.getWidth(), 0);
				polygon.addPoint(this.getWidth(), this.getHeight());
				polygon.addPoint(this.getWidth() - blueContourThickness, this.getHeight() - blueContourThickness);
				polygon.addPoint(this.getWidth() - blueContourThickness, blueContourThickness);
				g2.fillPolygon(polygon);
				g2.drawPolygon(polygon);
				g2.setPaint(new GradientPaint(0, 0, Configuration.HIGHLIGHTING_COLOR, 0, blueContourThickness,
						Configuration.CONTENT_BACKGROUND_COLOR, true));
				polygon = new Polygon();
				polygon.addPoint(0, 0);
				polygon.addPoint(this.getWidth(), 0);
				polygon.addPoint(this.getWidth() - blueContourThickness, blueContourThickness);
				polygon.addPoint(blueContourThickness, blueContourThickness);
				g2.fillPolygon(polygon);
				g2.drawPolygon(polygon);
				g2.setPaint(new GradientPaint(0, this.getHeight(), Configuration.HIGHLIGHTING_COLOR, 0,
						this.getHeight() + blueContourThickness, Configuration.CONTENT_BACKGROUND_COLOR, true));
				polygon = new Polygon();
				polygon.addPoint(this.getWidth() - blueContourThickness, this.getHeight() - blueContourThickness);
				polygon.addPoint(this.getWidth(), this.getHeight());
				polygon.addPoint(0, this.getHeight());
				polygon.addPoint(blueContourThickness, this.getHeight() - blueContourThickness);
				g2.fillPolygon(polygon);
				g2.drawPolygon(polygon);
			}

			if (nerveWeb.length != 0) {
				g2.setPaint(new GradientPaint(0, 0,
						new Color(Configuration.FOREGROUND_COLOR.getRed(),
								Configuration.FOREGROUND_COLOR.getGreen(),
								Configuration.FOREGROUND_COLOR.getBlue(), 0),
						this.getWidth() / (nerveWeb.length * 2), 0, Configuration.FOREGROUND_COLOR, true));

				for (int j = 0; j < nerveWeb[0] && j < this.inputCount; j++) {

					float w1 = (float) this.getWidth() / (float) nerveWeb.length;
					float h1 = (float) this.getHeight() / (float) nerveWeb[0];

					Point firstPoint = new Point((int) ((w1 * 0 + w1 * (0 + 1)) / 2),
							(int) ((h1 * j + h1 * (j + 1)) / 2));

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
			}

			g2.setPaint(Configuration.FOREGROUND_COLOR);

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

}
