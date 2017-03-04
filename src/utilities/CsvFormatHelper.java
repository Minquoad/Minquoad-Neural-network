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

	public static boolean isCurve(double[][] data) {
		return data[0].length == 1 || data.length == 1;
	}

	public static double[][] toSampleArray(double[][] data, int inputCount) {
		double[] curve = null;
		if (data[0].length == 1) {
			curve = columnToCurve(data);
		}
		if (data.length == 1) {
			curve = lineToCurve(data);
		}
		if (curve != null && inputCount < curve.length) {
			data = toSampleArray(curve, inputCount);
		}
		return data;
	}

	public static double[][] toSampleArray(double[] curve, int inputCount) {
		double[][] samples = new double[curve.length - inputCount][inputCount + 1];

		for (int i = 0; i < samples.length; i++) {
			for (int j = 0; j < inputCount; j++) {
				samples[i][j] = curve[i + j];
			}
			samples[i][inputCount] = curve[i + inputCount];
		}

		return samples;
	}

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

	public static double[][] concatColumnByColumn(double[][] table0, double[][] table1) {
		double[][] newTab = new double[table0.length + table1.length][table0[0].length];

		for (int i = 0; i < table0.length; i++) {
			newTab[i] = table0[i];
		}
		for (int i = 0; i < table1.length; i++) {
			newTab[i + table0.length] = table1[i];
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
		char separator = ',';
		if (str.indexOf(';') != -1) {
			separator = ';';
		}

		int valuesCount = 1;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == separator) {
				valuesCount++;
			}
		}

		double[] values = new double[valuesCount];

		for (int i = 0; i < values.length - 1; i++) {
			values[i] = Double.valueOf(str.substring(0, str.indexOf(separator)));
			str = str.substring(str.indexOf(separator) + 1, str.length());
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

			if (data.length == 1 && data[0].length == 1) {
				return null;
			} else {
				return data;
			}
		}
	}

	public static double[] columnToCurve(double[][] data) {
		double[] curve = new double[data.length];

		for (int i = 0; i < curve.length; i++) {
			curve[i] = data[i][0];
		}

		return curve;
	}

	public static double[][] curveToColumn(double[] curve) {
		double[][] column = new double[curve.length][1];

		for (int i = 0; i < curve.length; i++) {
			column[i][0] = curve[i];
		}

		return column;
	}

	public static double[][] toColumnIfNeeded(double[][] curve) {
		if (curve.length == 1) {
			return curveToColumn(lineToCurve(curve));
		} else {
			return curve;
		}
	}

	public static double[] lineToCurve(double[][] data) {
		double[] curve = new double[data[0].length];

		for (int i = 0; i < curve.length; i++) {
			curve[i] = data[0][i];
		}

		return curve;
	}

}
