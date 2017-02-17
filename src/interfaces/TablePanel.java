package interfaces;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class TablePanel extends JPanel {

	private Color headerForeground = new Color(28, 106, 126);
	private Color headerBackground = new Color(11, 11, 11);
	private Color foreground = new Color(0, 0, 0);
	private Color background = new Color(28, 106, 126);

	protected JTable table;
	protected JScrollPane scrollPane;

	public TablePanel() {
		this.setOpaque(false);
		this.setLayout(new GridLayout());

		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}

			@Override
			public void componentResized(ComponentEvent e) {
				revalidate();
			}

			@Override
			public void componentMoved(ComponentEvent e) {}

			@Override
			public void componentHidden(ComponentEvent e) {}
		});
	}

	public void setTable(JTable table) {

		this.table = table;

		setTheme(table);

		if (this.scrollPane != null) {
			this.remove(this.scrollPane);
		}
		this.scrollPane = new JScrollPane(table);
		this.scrollPane.getViewport().setBackground(new Color(23, 23, 20));
		this.scrollPane.setBorder(BorderFactory.createEmptyBorder());
		this.add(this.scrollPane);
		this.revalidate();
	}

	private void setTheme(JTable table) {

		table.setOpaque(false);

		DefaultTableCellRenderer stringRenderer = (DefaultTableCellRenderer) table.getDefaultRenderer(String.class);
		stringRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		table.setDefaultEditor(Object.class, null);
		table.getTableHeader().setReorderingAllowed(false);

		table.setBackground(background);
		table.setForeground(foreground);
		table.getTableHeader().setBackground(headerBackground);
		table.getTableHeader().setForeground(headerForeground);

		table.setFont(new Font("Dialog", Font.BOLD, 12));
		table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));

		UIManager.getDefaults().put("TableHeader.cellBorder",
				BorderFactory.createLineBorder(new Color(background.getRed() + foreground.getRed(),
						background.getGreen() + foreground.getGreen(), background.getBlue() + foreground.getBlue())));
	}

	public Color getHeaderForeground() {
		return headerForeground;
	}

	public void setHeaderForeground(Color headerForeground) {
		this.headerForeground = headerForeground;
	}

	public Color getHeaderBackground() {
		return headerBackground;
	}

	public void setHeaderBackground(Color headerBackground) {
		this.headerBackground = headerBackground;
	}

	public Color getForeground() {
		return foreground;
	}

	public void setForeground(Color foreground) {
		this.foreground = foreground;
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}

}
