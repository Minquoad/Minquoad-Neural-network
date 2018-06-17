package utilities;

import java.awt.Color;

public abstract class Configuration {

	// global
	public static final String VERSION = "2.1.1";
	public static final String DATA_DIRECTORY_NAME = "Minquoad's Perceptron";
	public static final String PREFERENCES_FILE_NAME = "preferences";
	
	//dev
	public static final boolean PRINT_CAUGHT_EXCEPTION_STACK_TRACES = true;

	//interface
	public static final Color HIGHLIGHTING_COLOR = new Color(19, 71, 84);
	public static final Color BACKGROUND_COLOR = new Color(23, 23, 20);
	public static final Color CONTENT_BACKGROUND_COLOR = new Color(39, 40, 34);
	public static final Color FOREGROUND_COLOR = new Color(204, 204, 204);
	public static final Color BORDER_COLOR = new Color(122, 138, 153);

	//algo
	public static final int INSUFFICIENT_PROGRESSIONS_NEEDED_TO_STOP = 8;
	public static final double CONTROL_SAMPLES_SIZE = 0.3;

	
	public static int getSubversion(int index) {
		return Integer.parseInt(VERSION.split(".")[index]);
	}
	
}
