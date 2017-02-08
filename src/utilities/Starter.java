package utilities;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JTextPane;
import javax.swing.filechooser.FileSystemView;

import gClasses.DataAssociator;
import gClasses.gInterfaces.GChoixFichier;
import gClasses.gInterfaces.GChoixFichier.FileActionListener;

public class Starter {

	public static final String version = "1.3.2";

	public static final String def_dir = FileSystemView.getFileSystemView().getDefaultDirectory().toString()
			+ "/Minquoad's Perceptron";

	public static void main(String[] args) {

		File file = new File(def_dir);
		if (!file.exists()) {
			file.mkdir();
		}

		DataAssociator da = new DataAssociator(new File(Starter.def_dir + "/preferences"));

		boolean firstRunning = false;

		if (!da.exists("version") || (da.exists("version") && !da.getValueString("version").equals(version))) {
			da.resetData();
			firstRunning = true;
			da.setValue("version", version);
		}

		if (!da.exists("maxIter"))
			da.setValue("maxIter", 1);
		if (!da.exists("multiThreading"))
			da.setValue("multiThreading", 1);
		if (!da.exists("lastFolderLoaded"))
			da.setValue("lastFolderLoaded", System.getProperty("user.dir"));

		da.save();

		Controler controler = new Controler();

		if (firstRunning) {
			controler.help();
		}
	}

	public static int getSavedIter() {
		DataAssociator da = new DataAssociator(new File(Starter.def_dir + "/preferences"));
		return da.getValueInt("maxIter");
	}

	public static int getSavedMultiThreading() {
		DataAssociator da = new DataAssociator(new File(Starter.def_dir + "/preferences"));
		return da.getValueInt("multiThreading");
	}

	public static void selectFileAndPerforme(Component parent, FileActionListener fileActionListener) {
		FileActionListener fileActionListenerThatSaveLastUsedPath = (file) -> {
			
			fileActionListener.actionPerformed(file);

			DataAssociator da = new DataAssociator(new File(Starter.def_dir + "/preferences"));
			da.setValue("lastFolderLoaded", file.getPath());
			da.save();
		};
		
		GChoixFichier.selectFileAndPerforme(parent, Starter.getSavedLastFolderLoaded(), fileActionListenerThatSaveLastUsedPath);
	}

	public static String getSavedLastFolderLoaded() {
		DataAssociator da = new DataAssociator(new File(Starter.def_dir + "/preferences"));
		return da.getValueString("lastFolderLoaded");
	}

	public static JTextPane getCenteredTextZone(String text) {
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setOpaque(false);
		textPane.setContentType("text/html");
		textPane.setText(
				"<body style='text-align: center;font-family: arial;color: rgb(204, 204, 204);'>" + text + "</body>");
		return textPane;
	}

	public static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint,
			boolean higherQuality) {
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage) img;
		int w, h;
		if (higherQuality) {
			w = img.getWidth();
			h = img.getHeight();
		} else {
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}
			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}
			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}

}
