package utilities;

import java.io.File;

import gClasses.DataAssociator;

public abstract class PreferencesHelper {

	public static void savePreferences(int maxIter, int multiThreading) {
		DataAssociator da;
		try {
			da = new DataAssociator(new File(Starter.def_dir + "/preferences"));

			da.setValue("maxIter", maxIter);
			da.setValue("multiThreading", multiThreading);

			da.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getSavedLastFolderLoaded() {
		String defaultValue = null;
		try {
			DataAssociator da = new DataAssociator(new File(Starter.def_dir + "/preferences"));
			return da.getValueString("lastFolderLoaded");
		} catch (Exception e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	public static int getSavedMultiThreading() {
		int defaultValue = 1;
		try {
			DataAssociator da = new DataAssociator(new File(Starter.def_dir + "/preferences"));
			if (da.exists("multiThreading")) {
				return da.getValueInt("multiThreading");
			} else {
				return defaultValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	public static int getSavedIter() {
		int defaultValue = 1;
		try {
			DataAssociator da = new DataAssociator(new File(Starter.def_dir + "/preferences"));
			if (da.exists("maxIter")) {
				return da.getValueInt("maxIter");
			} else {
				return defaultValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

}
