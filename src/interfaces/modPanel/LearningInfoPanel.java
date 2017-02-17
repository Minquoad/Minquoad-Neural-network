package interfaces.modPanel;

import java.awt.Color;
import java.awt.Point;
import java.math.BigDecimal;
import java.math.MathContext;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import interfaces.TablePanel;
import utilities.Preferences;

public class LearningInfoPanel extends TablePanel {

	private DefaultTableModel model;
	private int lastDescibedIteration = -1;

	public LearningInfoPanel() {
		this.setBackground(Preferences.BLUE);
		this.setForeground(Preferences.FOREGROUND);
		this.setHeaderBackground(new Color(11, 11, 11));
		this.setHeaderForeground(Preferences.BLUE);
		this.setHeaderCellBorderColor(Preferences.BLUE);
		this.generateGridColor();
		
		this.clear();
	}

	public void clear() {

		String[] colHeadings = { "Iteration", "MSE", "LIE (%)", "time (ms)" };
		model = new DefaultTableModel(0, colHeadings.length);
		model.setColumnIdentifiers(colHeadings);
		table = new JTable(model);

		this.setTable(table);
	}

	public void appendInfoLine(int iter, double mse, double lastEvolution, long duration) {

		if (iter != lastDescibedIteration) {
			lastDescibedIteration = iter;

			String mseString;
			if (Double.isFinite(mse)) {
				mseString = Double.toString(new BigDecimal(mse).round(new MathContext(5)).doubleValue());
			} else {
				mseString = Double.toString(mse);
			}

			String mseProgression;
			if (iter == 0) {
				mseProgression = "/";
			} else {
				mseProgression = Double
						.toString(new BigDecimal(lastEvolution * 100d).round(new MathContext(5)).doubleValue());
			}

			model.addRow(new Object[] { iter, mseString, mseProgression, duration });

			scrollPane.getViewport().setViewPosition(new Point(0, table.getHeight() - 1));
		}
	}

	public void startNewLearning() {
		if (table.getRowCount() != 0) {
			model.addRow(new Object[] { "", "", "", "" });
		}
	}

}
