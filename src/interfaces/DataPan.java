package interfaces;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.MathContext;

import javax.swing.JTable;

import gClasses.gInterfaces.GTablePanel;
import utilities.Configuration;

public class DataPan extends GTablePanel {

	public enum Mode {
		NONE, LEARNING, PROCESSING_WITH_RESULTS, PROCESSING_WITHOUT_RESULTS, PROCESSING_CURVE, PROCESSED_CURVE;
	}

	private Mode showedMod = null;
	private double[][] showedData = null;
	private double[][] showedResults = null;
	private int showedInputCount = 0;
	private int showedOutputCount = 0;

	public DataPan() {
		this.setBackground(Configuration.CONTENT_BACKGROUND_COLOR);
		this.setForeground(Configuration.FOREGROUND_COLOR);
		this.setHeaderBackground(new Color(8, 8, 8));
		this.setHeaderForeground(Configuration.HIGHLIGHTING_COLOR);
		this.setHeaderCellBorderColor(Configuration.HIGHLIGHTING_COLOR);
		this.generateGridColor();
	}

	public void setNoneMode(double[][] data) {
		if (data != null) {

			if ((showedMod == Mode.LEARNING || showedMod == Mode.PROCESSING_CURVE) && showedData == data) {
				this.setDataTableHeaderToNone();
				if (data[0].length == 1) {
					this.table.getTableHeader().setUI(null);
				}
			} else if (showedMod != Mode.NONE || showedData != data) {

				int columnCount = data[0].length;

				String[] columnNames = new String[columnCount];

				for (int i = 0; i < columnCount; i++) {
					columnNames[i] = Integer.toString(i + 1);
				}
				Object[][] tableData = new Object[data.length][columnCount];

				for (int i = 0; i < data.length; i++) {
					double[] line = data[i];
					if (line.length == columnCount) {
						for (int j = 0; j < line.length; j++) {
							tableData[i][j] = formatDouble(line[j]);
						}
					}
				}

				this.table = new JTable(tableData, columnNames);
				if (columnCount == 1) {
					this.table.getTableHeader().setUI(null);
				}

				this.setTable(table);
			}

			showedMod = Mode.NONE;
			showedData = data;
		}
	}

	public void setLearningMode(double[][] data, int inputCount) {
		if (data != null) {

			if (showedMod == Mode.NONE && showedData == data) {
				this.updatDataTableHeader(inputCount);
			} else if (showedMod != Mode.LEARNING || showedData != data || showedInputCount != inputCount) {

				int columnCount = data[0].length;

				String[] columnNames = new String[columnCount];

				for (int i = 0; i < inputCount; i++)
					columnNames[i] = "input";
				for (int i = inputCount; i < columnCount; i++)
					columnNames[i] = "output";

				Object[][] tableData = new Object[data.length][columnCount];

				for (int i = 0; i < data.length; i++) {
					double[] line = data[i];
					if (line.length == columnCount) {
						for (int j = 0; j < line.length; j++) {
							tableData[i][j] = formatDouble(line[j]);
						}
					}
				}

				this.table = new JTable(tableData, columnNames);

				this.setTable(table);
			}

			showedMod = Mode.LEARNING;
			showedData = data;
			showedInputCount = inputCount;
		}

	}

	public void setProcessingMode(double[][] data, int outputCount) {
		if (data != null) {

			if (showedMod != Mode.PROCESSING_WITHOUT_RESULTS || showedData != data
					|| showedOutputCount != outputCount) {

				int columnCount = data[0].length + outputCount;

				String[] columnNames = new String[columnCount];

				for (int i = 0; i < data[0].length; i++) {
					columnNames[i] = "input";
				}
				for (int i = data[0].length; i < columnCount; i++) {
					columnNames[i] = "output";
				}

				Object[][] tableData = new Object[data.length][columnCount];

				for (int i = 0; i < data.length; i++) {
					double[] line = data[i];
					for (int j = 0; j < line.length; j++) {
						tableData[i][j] = formatDouble(line[j]);
					}
					for (int j = line.length; j < line.length + outputCount; j++) {
						tableData[i][j] = "?";
					}
				}

				this.table = new JTable(tableData, columnNames);

				this.setTable(table);
			}

			showedMod = Mode.PROCESSING_WITHOUT_RESULTS;
			showedData = data;
			showedOutputCount = outputCount;
		}
	}

	public void setCurveProcessingMode(double[][] data) {
		if (data != null) {

			if (showedMod != Mode.PROCESSING_CURVE || showedData != data) {

				String[] columnNames = new String[1];
				columnNames[0] = "source data";

				Object[][] tableData = new Object[data.length][1];

				for (int i = 0; i < data.length; i++) {
					tableData[i][0] = formatDouble(data[i][0]);
				}

				this.table = new JTable(tableData, columnNames);

				this.setTable(table);
			}

			showedMod = Mode.PROCESSING_CURVE;
			showedData = data;
		}
	}

	public void setCurveProcessedMode(double[][] data, double[][] results) {
		if (data != null) {

			if (showedMod != Mode.PROCESSED_CURVE || showedData != data || showedResults != results) {

				String[] columnNames = new String[1];
				columnNames[0] = "source and generated data";

				Object[][] tableData = new Object[data.length + results.length][1];

				for (int i = 0; i < data.length; i++) {
					tableData[i][0] = formatDouble(data[i][0]);
				}
				for (int i = 0; i < results.length; i++) {
					tableData[i + data.length][0] = formatDouble(results[i][0]);
				}

				this.table = new JTable(tableData, columnNames);

				this.setTable(table);
			}

			showedMod = Mode.PROCESSED_CURVE;
			showedData = data;
			showedResults = results;
		}
	}

	public void setProcessedMode(double[][] data, double[][] results) {
		if (data != null) {

			if (showedMod != Mode.PROCESSING_WITH_RESULTS || showedData != data || showedResults != results) {

				int columnCount = data[0].length + results[0].length;

				String[] columnNames = new String[columnCount];

				for (int i = 0; i < data[0].length; i++) {
					columnNames[i] = "input";
				}
				for (int i = data[0].length; i < columnCount; i++) {
					columnNames[i] = "output";
				}
				Object[][] tableData = new Object[data.length][columnCount];

				for (int i = 0; i < data.length; i++) {
					double[] line = data[i];
					for (int j = 0; j < line.length; j++) {
						tableData[i][j] = line[j];
					}
					for (int j = line.length; j < line.length + results[0].length; j++) {
						tableData[i][j] = formatDouble(results[i][j - line.length]);
					}
				}

				this.table = new JTable(tableData, columnNames);

				this.setTable(table);
			}

			showedMod = Mode.PROCESSING_WITH_RESULTS;
			showedData = data;
			showedResults = results;
		}
	}

	private void updatDataTableHeader(int inputCount) {
		int i = 0;
		for (; i < inputCount; i++) {
			table.getColumnModel().getColumn(i).setHeaderValue("input");
		}
		for (; i < table.getColumnModel().getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setHeaderValue("output");
		}
	}

	private void setDataTableHeaderToNone() {
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setHeaderValue(i + 1);
		}
	}

	private static double formatDouble(double dou) {
		if (Double.isFinite(dou)) {
			return new BigDecimal(dou).round(new MathContext(5)).doubleValue();
		} else {
			return dou;
		}
	}

}
