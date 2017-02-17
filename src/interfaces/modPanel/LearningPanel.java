package interfaces.modPanel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JTextPane;

import interfaces.MainPan;
import utilities.Controler;
import utilities.Preferences;

public class LearningPanel extends ModPanel {

	private final int MAX_MAX_ITER = 10_000;
	private final double SLIDER_PRECISION = 20;
	private final long SLIDER_BUTTON_SPEED = 32;

	private int maxIter;
	private int multiThreading;

	private LearningInfoPanel learningInfoPanel;
	private boolean learning = false;

	private MainButton runButton;
	private MainButton stopButton;
	private JSlider maxIterSlider;
	private JSlider multiThreadingSlider;
	private JButton unlearnButton;
	private JButton clearButton;
	private JButton increaseMaxIter;
	private JButton decreaseMaxIter;

	public LearningPanel(Controler controler) {

		int cores = Runtime.getRuntime().availableProcessors();

		maxIter = Math.min(Preferences.getMaxIter(), MAX_MAX_ITER);
		multiThreading = Math.min(Preferences.getMultiThreading(), cores);

		JTextPane maxIterLabel = MainPan.creadStandartJTextPane();
		maxIterLabel.setText("Max iterations : " + maxIter);

		double sliderPower = Math.pow(10d, 1d / SLIDER_PRECISION);

		double sliderValue = -1;
		if (maxIter != 0) {
			sliderValue = Math.log(maxIter) / Math.log(sliderPower);
		}
		double sliderMaxValue = Math.log(MAX_MAX_ITER) / Math.log(sliderPower);

		maxIterSlider = new JSlider(-1, (int) (0.5d + sliderMaxValue), (int) (0.5d + sliderValue));
		maxIterSlider.setOpaque(false);
		ActionListener maxIterSliderListener = (e) -> {
			int value = maxIterSlider.getValue();
			if (value == -1) {
				maxIter = 0;
			} else {
				maxIter = (int) (0.5d + Math.pow(sliderPower, (double) value));
			}

			maxIterLabel.setText("Max iterations : " + maxIter);

			increaseMaxIter.setEnabled(maxIter != MAX_MAX_ITER);
			decreaseMaxIter.setEnabled(maxIter != 0);
		};

		maxIterSlider.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				maxIterSliderListener.actionPerformed(null);
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseClicked(MouseEvent arg0) {

			}
		});

		maxIterSlider.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {}

			@Override
			public void mouseDragged(MouseEvent e) {
				maxIterSliderListener.actionPerformed(null);
			}
		});

		increaseMaxIter = new JButton("+");
		decreaseMaxIter = new JButton("-");

		increaseMaxIter.addActionListener((e) -> {
			maxIter++;
			double newSliderValue = Math.log(maxIter) / Math.log(sliderPower);
			maxIterSlider.setValue((int) (0.5d + newSliderValue));
			increaseMaxIter.setEnabled(maxIter != MAX_MAX_ITER);
			decreaseMaxIter.setEnabled(maxIter != 0);

			maxIterLabel.setText("Max iterations : " + maxIter);

		});
		increaseMaxIter.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				new Thread(() -> {
					try {
						Thread.sleep(750);
						while (increaseMaxIter.getModel().isPressed() && maxIter != MAX_MAX_ITER) {
							maxIter++;
							double newSliderValue = Math.log(maxIter) / Math.log(sliderPower);
							maxIterSlider.setValue((int) (0.5d + newSliderValue));
							increaseMaxIter.setEnabled(maxIter != MAX_MAX_ITER);
							decreaseMaxIter.setEnabled(maxIter != 0);

							maxIterLabel.setText("Max iterations : " + maxIter);

							Thread.sleep(1000 / SLIDER_BUTTON_SPEED);
						}
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}).start();
			}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		if (maxIter == MAX_MAX_ITER) {
			increaseMaxIter.setEnabled(false);
		}

		decreaseMaxIter.addActionListener((e) -> {
			maxIter--;
			double newSliderValue = -1;
			if (maxIter != 0) {
				newSliderValue = Math.log(maxIter) / Math.log(sliderPower);
			}
			maxIterSlider.setValue((int) (0.5d + newSliderValue));
			increaseMaxIter.setEnabled(maxIter != MAX_MAX_ITER);
			decreaseMaxIter.setEnabled(maxIter != 0);

			maxIterLabel.setText("Max iterations : " + maxIter);
		});
		decreaseMaxIter.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				new Thread(() -> {
					try {
						Thread.sleep(750);
						while (decreaseMaxIter.getModel().isPressed() && maxIter != 0) {
							maxIter--;
							double newSliderValue = -1;
							if (maxIter != 0) {
								newSliderValue = Math.log(maxIter) / Math.log(sliderPower);
							}
							maxIterSlider.setValue((int) (0.5d + newSliderValue));
							increaseMaxIter.setEnabled(maxIter != MAX_MAX_ITER);
							decreaseMaxIter.setEnabled(maxIter != 0);

							maxIterLabel.setText("Max iterations : " + maxIter);

							Thread.sleep(1000 / SLIDER_BUTTON_SPEED);
						}
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}).start();
			}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		if (maxIter == 0) {
			decreaseMaxIter.setEnabled(false);
		}

		JTextPane multiThreadingLabel = MainPan.creadStandartJTextPane();
		multiThreadingLabel.setText("Thread used : " + multiThreading);
		multiThreadingSlider = new JSlider(1, cores, multiThreading);
		multiThreadingSlider.setOpaque(false);
		multiThreadingSlider.addChangeListener((e) -> {
			multiThreading = multiThreadingSlider.getValue();
			multiThreadingLabel.setText("Thread used : " + multiThreading);
		});

		JTextPane minProgressionLabel = MainPan.creadStandartJTextPane();
		minProgressionLabel.setText("Minimum progression per iterations");
		
		
		learningInfoPanel = new LearningInfoPanel();
		
		clearButton = new JButton("Clear");
		runButton = new MainButton("resources/pictures/computing.jpg");
		stopButton = new MainButton("resources/pictures/stopLearning.jpg");
		unlearnButton = new JButton("UNLEARN");

		maxIterLabel.setPreferredSize(new Dimension(160, 26));
		this.add(maxIterLabel, 0, 0);
		maxIterSlider.setPreferredSize(new Dimension(160, 26));
		this.addToRight(maxIterSlider, maxIterLabel, 8);
		this.addToRight(decreaseMaxIter, maxIterSlider, 8);
		this.addToRight(increaseMaxIter, decreaseMaxIter, 3);
		multiThreadingLabel.setPreferredSize(new Dimension(160, 26));
		this.addToBottom(multiThreadingLabel, maxIterLabel, 8);
		multiThreadingSlider.setPreferredSize(new Dimension(160, 26));
		this.addToRight(multiThreadingSlider, multiThreadingLabel, 8);
		this.addToBottom(minProgressionLabel, multiThreadingLabel, 8);

		this.add(runButton, 0, 500, 200, 500);
		this.add(stopButton, 0, 500, 200, 500);
		stopButton.setVisible(false);
		this.add(learningInfoPanel, 200, 500, 800, 500);
		this.add(clearButton);
		this.add(unlearnButton);

		runButton.addActionListener((e) -> controler.startLearning());
		stopButton.addActionListener((e) -> controler.endLearning());
		clearButton.addActionListener((e) -> learningInfoPanel.clear());
		unlearnButton.addActionListener((e) -> controler.unlearn());

	}

	public int getMaxIter() {
		return maxIter;
	}

	public int getMultiThreading() {
		return multiThreading;
	}

	public void appendInfoLine(int iter, double mse, double lastEvolution, long duration) {
		learningInfoPanel.appendInfoLine(iter, mse, lastEvolution, duration);
	}

	public void setLearning(boolean learning) {
		if (learning != this.learning) {
			this.learning = learning;

			runButton.setVisible(!learning);
			stopButton.setVisible(learning);

			maxIterSlider.setEnabled(!learning);

			increaseMaxIter.setEnabled(maxIter != MAX_MAX_ITER && !learning);
			decreaseMaxIter.setEnabled(maxIter != 0 && !learning);

			multiThreadingSlider.setEnabled(!learning);
			unlearnButton.setEnabled(!learning);
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		unlearnButton.setBounds(0, this.getHeight() / 2 - 26, 200 * this.getWidth() / 1000, 26);
		clearButton.setBounds(this.getWidth() - (200 * this.getWidth() / 1000), this.getHeight() / 2 - 26,
				200 * this.getWidth() / 1000, 26);
	}

	public void startNewLearning() {
		learningInfoPanel.startNewLearning();
	}

}
