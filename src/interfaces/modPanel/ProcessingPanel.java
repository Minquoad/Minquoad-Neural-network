package interfaces.modPanel;

import utilities.Controler;

public class ProcessingPanel extends ModPanel {

	public ProcessingPanel(Controler controler) {

		MainButton runButton = new MainButton("resources/pictures/proceed.jpg");

		this.add(runButton, 0, 500, 200, 500);

		runButton.addActionListener((e) -> controler.startProcessing());

	}

}
