package interfaces.modPanel;

import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import utilities.Controler;
import utilities.Preferences;

public class ProcessingPanel extends ModPanel {

	private JTextArea processingInfoText;
	private MainButton runButton;
	private boolean occupied = false;

	public ProcessingPanel(Controler controler) {

		processingInfoText = new JTextArea();
		processingInfoText.setEditable(false);
		processingInfoText.setTabSize(4);
		processingInfoText.setMargin(new Insets(0, 3, 0, 3));
		processingInfoText.setFont(new Font("monospaced", Font.BOLD, 12));
		processingInfoText.setBackground(Preferences.CONTENT_BACKGROUND);
		processingInfoText.setForeground(Preferences.FOREGROUND);
		JScrollPane processingInfoTextScroll = new JScrollPane(processingInfoText);
		processingInfoTextScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		processingInfoTextScroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Preferences.BORDER));

		runButton = new MainButton("resources/pictures/proceed.jpg");

		JButton clearButton = new JButton("Clear");

		// placement
		this.add(runButton, 0, 0.5f, 0.2f, 0.5f);
		this.add(processingInfoTextScroll, 0f, 0f, 1f, 0.5f);
		this.add(clearButton);
		this.addComponentBoundsSetter(thisPp -> {
			clearButton.setBounds(
					(int) ((float) thisPp.getWidth() - (0.2f * (float) thisPp.getWidth())
							+ 0.5),
					(int) ((float) thisPp.getHeight() / 2f + 0.5f),
					(int) (0.2f * (float) thisPp.getWidth() + 0.5f),
					26);
		});

		// actions
		runButton.addActionListener(e -> controler.startProcessing());
		clearButton.addActionListener(e -> processingInfoText.setText(""));
	}

	public void appendInfo(String str) {
		if (processingInfoText.getText().length() == 0) {
			while (str.indexOf("\n") == 0) {
				str = str.replaceFirst("\n", "");
			}
		}
		processingInfoText.append(str);
		processingInfoText.setCaretPosition(processingInfoText.getDocument().getLength());
	}

	public void setOccupied(boolean occupied) {
		if (occupied != this.occupied ) {
			this.occupied = occupied;

			runButton.setEnabled(!occupied);
		}
	}

}
