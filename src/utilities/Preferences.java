package utilities;

import java.awt.Component;
import java.io.File;

import javax.swing.filechooser.FileSystemView;

import gClasses.DataAssociator;
import gClasses.gInterfaces.GFileChooser;
import gClasses.gInterfaces.GFileChooser.FileActionListener;
import threads.LearningMode;

public abstract class Preferences {

	private static final String DEFAULT_DIRECTORY_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().toString();
	
	public static final File preferencesFile = new File(DEFAULT_DIRECTORY_PATH + File.pathSeparator
			+ Configuration.DATA_DIRECTORY_NAME + File.pathSeparator + Configuration.PREFERENCES_FILE_NAME);

	private static String lastFolderLoaded = DEFAULT_DIRECTORY_PATH;
	private static int multiThreading = 1;
	private static int maxIter = 1;
	private static boolean interationsUnlimited = true;
	private static double minimumProgressionPerIteration = 0.01;
	private static LearningMode learningMode = LearningMode.SIMPLE;
	private static boolean firstRunning = false;

	private static final String LAST_LOADED_VERSION_KEY = "lastLoadedVersion";
	private static final String LAST_FOLDER_LOADED_KEY = "lastFolderLoaded";
	private static final String MULTI_THREADING_KEY = "multiThreading";
	private static final String MAX_ITER_KEY = "maxIter";
	private static final String INTERATIONS_UNLIMITED_KEY = "interationsUnlimited";
	private static final String LEARNING_MODE_KEY = "learningMode";
	private static final String MINIMUM_PROGRESSION_PER_ITERATION_KEY = "minimumProgressionPerIteration";

	static {
		File file = new File(DEFAULT_DIRECTORY_PATH + File.pathSeparator + Configuration.DATA_DIRECTORY_NAME);
		if (!file.exists()) {
			firstRunning = true;
			file.mkdir();
		}

		try {
			DataAssociator da = new DataAssociator(preferencesFile);

			if (!da.exists(LAST_LOADED_VERSION_KEY) || !da.getValueString(LAST_LOADED_VERSION_KEY).equals(Configuration.VERSION)) {
				firstRunning = true;
			}

			if (da.exists(LAST_FOLDER_LOADED_KEY)) {
				lastFolderLoaded = da.getValueString(LAST_FOLDER_LOADED_KEY);
			}
			if (da.exists(MULTI_THREADING_KEY)) {
				multiThreading = da.getValueInt(MULTI_THREADING_KEY);
			}
			if (da.exists(MAX_ITER_KEY)) {
				maxIter = da.getValueInt(MAX_ITER_KEY);
			}
			if (da.exists(INTERATIONS_UNLIMITED_KEY)) {
				interationsUnlimited = Boolean.parseBoolean(da.getValueString(INTERATIONS_UNLIMITED_KEY));
			}
			if (da.exists(LEARNING_MODE_KEY)) {
				learningMode = LearningMode.getLearningModeByName(da.getValueString(LEARNING_MODE_KEY));
			}
			if (da.exists(MINIMUM_PROGRESSION_PER_ITERATION_KEY)) {
				minimumProgressionPerIteration = Double
						.parseDouble(da.getValueString(MINIMUM_PROGRESSION_PER_ITERATION_KEY));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void selectFileAndPerforme(Component parent, GFileChooser.Mode mode,
			FileActionListener fileActionListener) {
		FileActionListener fileActionListenerThatSaveLastUsedPath = file -> {
			fileActionListener.actionPerformed(file);
			Preferences.setLastFolderLoaded(file.getPath());
		};

		GFileChooser.selectFileAndPerforme(parent, Preferences.getLastFolderLoaded(), mode,
				fileActionListenerThatSaveLastUsedPath);
	}

	public static void save() {
		try {
			DataAssociator da = new DataAssociator(preferencesFile);

			da.setValue(MAX_ITER_KEY, maxIter);
			da.setValue(INTERATIONS_UNLIMITED_KEY, Boolean.toString(interationsUnlimited));
			da.setValue(MULTI_THREADING_KEY, multiThreading);
			da.setValue(LAST_FOLDER_LOADED_KEY, lastFolderLoaded);
			da.setValue(MINIMUM_PROGRESSION_PER_ITERATION_KEY, Double.toString(minimumProgressionPerIteration));
			da.setValue(LEARNING_MODE_KEY, learningMode.toString());
			da.setValue(LAST_LOADED_VERSION_KEY, Configuration.VERSION);

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
