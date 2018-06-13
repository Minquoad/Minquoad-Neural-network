package interfaces;

import javax.swing.JTextPane;

import utilities.Configuration;

public class GLabel extends JTextPane {

	public GLabel() {
		this.setEditable(false);
		this.setOpaque(false);
		this.setForeground(Configuration.FOREGROUND_COLOR);
	}
	
}
