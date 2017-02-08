package interfaces;

import java.io.File;

import javax.swing.JMenuItem;

import gClasses.gInterfaces.GFrame;
import utilities.Controler;
import utilities.Starter;

public class Frame extends GFrame {

	private JMenuItem saveCsvMI;
	private boolean learning = false;

	public Frame(Controler controler) {
		super("Minquoad's Neurons", 930, 670, "resources/pictures/icon.png",
				new File(Starter.def_dir + "/Frame Preferences"));

		this.addMenu("File");
		this.addMenu("?");

		JMenuItem loadPerMI = new JMenuItem("Load Perceptron");
		JMenuItem savePerMI = new JMenuItem("Save Perceptron");
		JMenuItem loadCsvMI = new JMenuItem("Load CSV");
		saveCsvMI = new JMenuItem("Save CSV");
		JMenuItem helpMI = new JMenuItem("Help");
		JMenuItem aboutMI = new JMenuItem("About");

		loadPerMI.addActionListener((e) -> controler.loadPer());
		savePerMI.addActionListener((e) -> controler.savePer());
		loadCsvMI.addActionListener((e) -> controler.loadCsv());
		saveCsvMI.addActionListener((e) -> controler.saveCsv());
		helpMI.addActionListener((e) -> controler.help());
		aboutMI.addActionListener((e) -> controler.about());

		saveCsvMI.setEnabled(false);

		this.getMenu("File").add(loadPerMI);
		this.getMenu("File").add(savePerMI);
		this.getMenu("File").addSeparator();
		this.getMenu("File").add(loadCsvMI);
		this.getMenu("File").add(saveCsvMI);
		this.getMenu("?").add(helpMI);
		this.getMenu("?").add(aboutMI);

	}

	public void enableCsvSaving(boolean b) {
		saveCsvMI.setEnabled(b);
	}

	public void setLearning(boolean learning) {
		if (learning != this.learning) {
			this.learning = learning;
			this.getMenu("File").setEnabled(!learning);
		}
	}

}
