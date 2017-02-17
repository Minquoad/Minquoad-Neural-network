package interfaces;

import gClasses.gInterfaces.GDialog;

public class ErrorInFilePopup extends GDialog {

	public ErrorInFilePopup() {
		super("About", "<br/>An error occured while reading the file.", 300, 100, true);

		this.setVisible(true);
	}

}
