package interfaces;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JMenuItem;

import gClasses.gInterfaces.GFrame;
import utilities.Controler;
import utilities.Preferences;

public class Frame extends GFrame implements WindowListener {

	private Controler controler;

	private JMenuItem saveCsvMI;
	private boolean learning = false;

	public Frame(Controler controler) {
		super("Minquoad's Neurons", 930, 670, "resources/pictures/icon.png",
				Preferences.preferencesFile);

		this.controler = controler;

		this.addMenu("File");
		this.addMenu("?");

		JMenuItem loadPerMI = new JMenuItem("Load a Perceptron");
		JMenuItem savePerMI = new JMenuItem("Save this Perceptron");
		JMenuItem loadCsvMI = new JMenuItem("Load a Table (CSV File)");
		saveCsvMI = new JMenuItem("Export this Table (as CSV File)");
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

		this.addWindowListener(this);
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		controler.savePreferences();
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

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

}
