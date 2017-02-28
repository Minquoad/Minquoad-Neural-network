package interfaces.modPanel;

import utilities.Controler;

public class ProcessingPanel extends ModPanel {

	public ProcessingPanel(Controler controler) {

		MainButton runButton = new MainButton("resources/pictures/proceed.jpg");

		this.add(runButton, 0, 0.5f, 0.2f, 0.5f);

		runButton.addActionListener(e -> controler.startProcessing());

	}

}
