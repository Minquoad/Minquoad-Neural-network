package utilities;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public abstract class CsvFormatHelper {

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
