package interfaces;

import javax.swing.JTextPane;

import utilities.Propreties;

public class GLabel extends JTextPane {

	public GLabel() {
		this.setEditable(false);
		this.setOpaque(false);
		this.setForeground(Propreties.FOREGROUND);
	}
	
}
