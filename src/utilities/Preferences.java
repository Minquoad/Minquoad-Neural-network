package utilities;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.filechooser.FileSystemView;

import gClasses.DataAssociator;
import gClasses.gInterfaces.GChoixFichier;
import gClasses.gInterfaces.GChoixFichier.FileActionListener;
import threads.LearningMode;

public abstract class Preferences {

	public static final Color HIGHLIGHTING = new Color(19, 71, 84);
	public static final Color BACKGROUND = new Color(23, 23, 20);
	public static final Color CONTENT_BACKGROUND = new Color(39, 40, 34);
	public static final Color FOREGROUND = new Color(204, 204, 204);
	public static final Color BORDER = new Color(122, 138, 153);

	public static final File preferencesFile = new File(
			FileSystemView.getFileSystemView().getDefaultDirectory().toString()
					+ "/Minquoad's Perceptron/preferences");

	public static final int INSUFFICIENT_PROGRESSIONS_NEEDED_TO_STOP = 8;
	
	private static String lastFolderLoaded = FileSystemView.getFileSystemView().getDefaultDirectory().toString();
	private static int multiThreading = 1;
	private static int maxIter = 1;
	private static boolean interationsUnlimited = true;
	private static double minimumProgressionPerIteration = 0.01;
	private static LearningMode learningMode = LearningMode.SIMPLE;
	private static boolean firstRunning = false;

	static {
		File file = new File(
				FileSystemView.getFileSystemView().getDefaultDirectory().toString() + "/Minquoad's Perceptron");
		if (!file.exists()) {
			firstRunning = true;
			file.mkdir();
		}

		try {
			DataAssociator da = new DataAssociator(preferencesFile);

			if (!da.exists("version") || !da.getValueString("version").equals(Starter.version)) {
				firstRunning = true;
			}

			if (da.exists("lastFolderLoaded")) {
				lastFolderLoaded = da.getValueString("lastFolderLoaded");
			}
			if (da.exists("multiThreading")) {
				multiThreading = da.getValueInt("multiThreading");
			}
			if (da.exists("maxIter")) {
				maxIter = da.getValueInt("maxIter");
			}
			if (da.exists("interationsUnlimited")) {
				interationsUnlimited = Boolean.parseBoolean(da.getValueString("interationsUnlimited"));
			}
			if (da.exists("learningMode")) {
				learningMode = LearningMode.getLearningModeByName(da.getValueString("learningMode"));
			}
			if (da.exists("minimumProgressionPerIteration")) {
				minimumProgressionPerIteration = Double
						.parseDouble(da.getValueString("minimumProgressionPerIteration"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void selectFileAndPerforme(Component parent, GChoixFichier.Mode mode,
			FileActionListener fileActionListener) {
		FileActionListener fileActionListenerThatSaveLastUsedPath = (file) -> {

			fileActionListener.actionPerformed(file);

			Preferences.setLastFolderLoaded(file.getPath());
		};

		GChoixFichier.selectFileAndPerforme(parent, Preferences.getLastFolderLoaded(), mode,
				fileActionListenerThatSaveLastUsedPath);
	}

	public static void save() {
		try {
			DataAssociator da = new DataAssociator(preferencesFile);

			da.setValue("maxIter", maxIter);
			da.setValue("interationsUnlimited", Boolean.toString(interationsUnlimited));
			da.setValue("multiThreading", multiThreading);
			da.setValue("lastFolderLoaded", lastFolderLoaded);
			da.setValue("minimumProgressionPerIteration", Double.toString(minimumProgressionPerIteration));
			da.setValue("learningMode", learningMode.toString());
			da.setValue("version", Starter.version);

			da.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isFirstRunning() {
		return firstRunning;
	}

	public static String getLastFolderLoaded() {
		return lastFolderLoaded;
	}

	public static void setLastFolderLoaded(String lastFolderLoaded) {
		Preferences.lastFolderLoaded = lastFolderLoaded;
	}

	public static int getMultiThreading() {
		return multiThreading;
	}

	public static void setMultiThreading(int multiThreading) {
		Preferences.multiThreading = multiThreading;
	}

	public static int getMaxIter() {
		return maxIter;
	}

	public static void setMaxIter(int maxIter) {
		Preferences.maxIter = maxIter;
	}

	public static boolean isInterationsUnlimited() {
		return interationsUnlimited;
	}

	public static void setInterationsUnlimited(boolean interationsUnlimited) {
		Preferences.interationsUnlimited = interationsUnlimited;
	}

	public static double getMinimumProgressionPerIteration() {
		return minimumProgressionPerIteration;
	}

	public static void setMinimumProgressionPerIteration(double minimumProgressionPerIteration) {
		Preferences.minimumProgressionPerIteration = minimumProgressionPerIteration;
	}

	public static LearningMode getLearningMode() {
		return learningMode;
	}

	public static void setLearningMode(LearningMode learningMode) {
		Preferences.learningMode = learningMode;
	}

}
