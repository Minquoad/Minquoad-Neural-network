package interfaces;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.math.BigDecimal;
import java.math.MathContext;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;

import utilities.Controler;
import utilities.Controler.Mode;

public class DataPan extends JPanel {

	private Controler.Mode showedMod = null;
	private double[][] showedData = null;
	private double[][] showedResults = null;
	private int showedInputCount = 0;
	private int showedOutputCount = 0;

	// grapicals
	private JTable table;
	private JScrollPane scrollPane;

	public DataPan() {
		this.setOpaque(false);
		this.setLayout(new GridLayout());

		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				revalidate();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
	}

	public void setNoneMode(double[][] data) {
		if (data != null) {

			if (showedMod == Mode.WILL_LEARN && showedData == data) {
				this.setDataTableHeaderToNone();
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

				table = new JTable(tableData, columnNames);

				setTheme(table);

				setScrollPane(new JScrollPane(table));
			}

			showedMod = Mode.NONE;
			showedData = data;
		}
	}

	public void setLearningMode(double[][] data, int inputCount) {
		if (data != null) {

			if (showedMod == Mode.NONE && showedData == data) {
				this.updatDataTableHeader(inputCount);
			} else if (showedMod != Mode.WILL_LEARN || showedData != data || showedInputCount != inputCount) {

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

				table = new JTable(tableData, columnNames);

				setTheme(table);

				setScrollPane(new JScrollPane(table));
			}

			showedMod = Mode.WILL_LEARN;
			showedData = data;
			showedInputCount = inputCount;
		}

	}

	public void setProcessingMode(double[][] data, int outputCount) {
		if (data != null) {
			if (showedMod != Mode.WILL_PROCEED || showedData != data || showedOutputCount != outputCount) {

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

				table = new JTable(tableData, columnNames);

				setTheme(table);

				setScrollPane(new JScrollPane(table));
			}

			showedMod = Mode.WILL_PROCEED;
			showedData = data;
			showedOutputCount = outputCount;
		}
	}

	public void setProcessedMode(double[][] data, double[][] results) {
		if (data != null) {

			if (showedMod != Mode.HAS_PROCEED || showedData != data || showedResults != results) {

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

				table = new JTable(tableData, columnNames);

				setTheme(table);

				setScrollPane(new JScrollPane(table));
			}

			showedMod = Mode.HAS_PROCEED;
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
			table.getColumnModel().getColumn(i).setHeaderValue(i);
		}
	}

	private void setScrollPane(JScrollPane scrollPane) {
		if (this.scrollPane != null)
			this.remove(this.scrollPane);
		this.scrollPane = scrollPane;
		scrollPane.getViewport().setBackground(new Color(23, 23, 20));
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		this.add(scrollPane);
		revalidate();
	}

	private void setTheme(JTable table) {

		table.setDefaultEditor(Object.class, null);
		table.getTableHeader().setReorderingAllowed(false);

		table.setBackground(new Color(28, 106, 126));
		table.setForeground(new Color(0, 0, 0));
		table.getTableHeader().setBackground(new Color(11, 11, 11));
		table.getTableHeader().setForeground(new Color(28, 106, 126));

		table.setFont(new Font("Dialog", Font.BOLD, 12));
		table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));

		UIManager.getDefaults().put("TableHeader.cellBorder", BorderFactory.createLineBorder(new Color(122, 138, 153)));

	}

	private double formatDouble(double dou) {
		if (Double.isFinite(dou)) {
			return new BigDecimal(dou).round(new MathContext(5)).doubleValue();
		} else {
			return dou;
		}
	}

}
