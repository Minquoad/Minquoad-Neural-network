package interfaces;

import javax.swing.JTextPane;

import utilities.Preferences;

public class GLabel extends JTextPane {

	public GLabel() {
		this.setEditable(false);
		this.setOpaque(false);
		this.setForeground(Preferences.FOREGROUND);
	}
	
}
