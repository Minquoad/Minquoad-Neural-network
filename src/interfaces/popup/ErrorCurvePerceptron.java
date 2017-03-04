package interfaces.popup;

import gClasses.gInterfaces.GDialog;

public class ErrorCurvePerceptron extends GDialog {

	public ErrorCurvePerceptron() {
		super("About", "<br/>Neural network for curves must have only one output neuron", 300, 100, true);

		this.setVisible(true);
	}

}
