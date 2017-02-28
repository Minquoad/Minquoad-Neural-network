package utilities;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public abstract class CsvFormatHelper {

	public static double[][] concatLineByLine(double[][] table0, double[][] table1) {
		double[][] newTab = new double[table0.length][table0[0].length + table1[0].length];
		for (int i = 0; i < newTab.length; i++) {
			for (int j = 0; j < table0[0].length; j++) {
				newTab[i][j] = table0[i][j];
			}
			for (int j = 0; j < table1.length; j++) {
				newTab[i][j + table0[0].length] = table1[i][j];
			}
		}
		return newTab;
	}

	public static double[][] randomizeSampleOrder(double[][] samples) {
		double[][] randomizedSamples = new double[samples.length][samples[0].length];

		List<double[]> sampleList = new LinkedList<double[]>();
		for (double[] sample : samples) {
			sampleList.add(sample);
		}
		Random rand = new Random();
		for (int i = 0; i < randomizedSamples.length; i++) {
			int j = rand.nextInt(sampleList.size());
			randomizedSamples[i] = sampleList.get(j);
			sampleList.remove(j);
		}

		return randomizedSamples;
	}

	public static void save(File file, double[][] data) {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[i].length; j++) {
					bos.write(Double.toString(data[i][j]).getBytes());
					if (j != data[i].length - 1) {
						bos.write(";".getBytes());
					}
				}
				if (i != data.length - 1) {
					bos.write("\n".getBytes());
				}
			}
			bos.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static double[] getDoubleTable(String str) {

		int valuesCount = 1;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == ';') {
				valuesCount++;
			}
		}

		double[] values = new double[valuesCount];

		for (int i = 0; i < values.length - 1; i++) {
			values[i] = Double.valueOf(str.substring(0, str.indexOf(';')));
			str = str.substring(str.indexOf(';') + 1, str.length());
		}

		values[values.length - 1] = Double.valueOf(str);

		return values;
	}

	public static double[][] getData(File fil) throws Exception {

		ArrayList<String> lineList = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new FileReader(fil));

		String line = br.readLine();
		while (line != null) {
			lineList.add(line);
			line = br.readLine();
		}

		br.close();

		if (lineList.isEmpty()) {
			throw new Exception();
		} else {

			double[][] data = new double[lineList.size()][CsvFormatHelper.getDoubleTable(lineList.get(0)).length];

			for (int i = 0; i < lineList.size(); i++) {

				double[] lineData = CsvFormatHelper.getDoubleTable(lineList.get(i));

				if (lineData.length == data[0].length) {
					data[i] = lineData;
				}
			}

			return data;
		}
	}

}
