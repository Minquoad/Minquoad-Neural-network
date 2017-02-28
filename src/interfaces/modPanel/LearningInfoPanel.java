package interfaces.modPanel;

import java.awt.Color;
import java.awt.Point;
import java.math.BigDecimal;
import java.math.MathContext;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import gClasses.gInterfaces.GTablePanel;
import utilities.Preferences;

public class LearningInfoPanel extends GTablePanel {

	private DefaultTableModel model;

	public LearningInfoPanel() {
		this.setBackground(Preferences.HIGHLIGHTING);
		this.setForeground(Preferences.FOREGROUND);
		this.setHeaderBackground(new Color(8, 8, 8));
		this.setHeaderForeground(Preferences.HIGHLIGHTING);
		this.setHeaderCellBorderColor(Preferences.HIGHLIGHTING);
		this.generateGridColor();

		this.clear();
	}

	public void clear() {

		String[] colHeadings = { "Iteration", "Square Error", "LIP (%)", "time (ms)" };
		model = new DefaultTableModel(0, colHeadings.length);
		model.setColumnIdentifiers(colHeadings);
		table = new JTable(model);

		this.setTable(table);
	}

	public void appendInfo(int iter, double squareError, double lastEvolution, long duration) {

		String squareErrorString;
		if (Double.isFinite(squareError)) {
			squareErrorString = Double.toString(new BigDecimal(squareError).round(new MathContext(5)).doubleValue());
		} else {
			squareErrorString = Double.toString(squareError);
		}

		String squareErrorProgression;
		if (iter == 0) {
			squareErrorProgression = "/";
		} else {
			squareErrorProgression = Double
					.toString(new BigDecimal(lastEvolution * 100d).round(new MathContext(5)).doubleValue());
		}

		model.addRow(new Object[] { iter, squareErrorString, squareErrorProgression, duration });

		scrollPane.getViewport().setViewPosition(new Point(0, table.getHeight() - 1));
	}

	public void startNewLearning() {
		if (table.getRowCount() != 0) {
			model.addRow(new Object[] { "", "", "", "" });
		}
	}

}
