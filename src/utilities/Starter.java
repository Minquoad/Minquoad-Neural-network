package utilities;

public class Starter {

	public static final String version = "1.6.0";
	public static final boolean printCaughtExceptionStackTraces = false;

	public static void main(String[] args) {

		Controler controler = new Controler();

		if (Preferences.isFirstRunning()) {
			controler.help();
		}
	}

}
