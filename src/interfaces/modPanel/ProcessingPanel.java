package interfaces.modPanel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import gClasses.gInterfaces.numberField.IntegerField;
import interfaces.GLabel;
import utilities.Controler;
import utilities.Propreties;

public class ProcessingPanel extends ModPanel {

	private JTextArea processingInfoText;
	private MainButton runButton;

	private IntegerField valueExtendedCount;
	private GLabel valueExtendedCountLabel;

	public ProcessingPanel(Controler controler) {
		super(controler);

		valueExtendedCountLabel = new GLabel();
		valueExtendedCountLabel.setText("Number of values to generate : ");
		valueExtendedCount = new IntegerField();
		valueExtendedCount.setDefaultValue(1);
		valueExtendedCount.setMinValue(1);
		valueExtendedCount.displayDefaultValue();
		valueExtendedCount.setForeground(Propreties.FOREGROUND);
		valueExtendedCount.setBorderColor(Propreties.HIGHLIGHTING);

		processingInfoText = new JTextArea();
		processingInfoText.setEditable(false);
		processingInfoText.setTabSize(4);
		processingInfoText.setMargin(new Insets(0, 3, 0, 3));
		processingInfoText.setFont(new Font("monospaced", Font.BOLD, 12));
		processingInfoText.setBackground(Propreties.CONTENT_BACKGROUND);
		processingInfoText.setForeground(Propreties.FOREGROUND);
		JScrollPane processingInfoTextScroll = new JScrollPane(processingInfoText);
		processingInfoTextScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		processingInfoTextScroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Propreties.BORDER));

		runButton = new MainButton("resources/pictures/proceed.jpg");

		// sizing

		valueExtendedCountLabel.setPreferredSize(new Dimension(valueExtendedCountLabel.getPreferredSize().width, 26));
		valueExtendedCount.setPreferredSize(new Dimension(160, 26));

		// placement

		this.add(valueExtendedCountLabel);
		valueExtendedCountLabel.setBounds(3, 3, valueExtendedCountLabel.getPreferredSize().width,
				valueExtendedCountLabel.getPreferredSize().height);
		this.addAnchoredToRight(valueExtendedCount, valueExtendedCountLabel, 3, -2);

		this.add(runButton, 0, 0.5f, 0.2f, 0.5f);
		this.add(processingInfoTextScroll, 0.2f, 0.5f, 0.8f, 0.5f);

		valueExtendedCountLabel.setVisible(isCurveMode());
		valueExtendedCount.setVisible(isCurveMode());

		// actions
		runButton.addActionListener(e -> controler.startProcessing());
	}

	public int getValueExtendedCount() {
		int value = valueExtendedCount.getValue();
		valueExtendedCount.setValue(value);
		return value;
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

	@Override
	public void setOccupied(boolean occupied) {
		if (occupied != this.isOccupied()) {
			super.setOccupied(occupied);

			runButton.setEnabled(!occupied);
		}
	}

	@Override
	public void setCurveMode(boolean curveMode) {
		super.setCurveMode(curveMode);
		valueExtendedCountLabel.setVisible(isCurveMode());
		valueExtendedCount.setVisible(isCurveMode());
	}

	@Override
	protected void clear() {
		processingInfoText.setText("");
	}

}
