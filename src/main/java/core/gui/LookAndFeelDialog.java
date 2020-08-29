package core.gui;

import core.gui.comp.icon.ColorIcon;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.table.*;

public class LookAndFeelDialog extends JDialog {

	private static final long serialVersionUID = -5492754898221009950L;

	public LookAndFeelDialog() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initComponents();
		pack();
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		JTable table = new JTable();
		table.setAutoCreateRowSorter(true);
		table.setModel(new MyTableModel(getLookAndFeelOverview()));
		table.setDefaultRenderer(Object.class, new ColorIconTableCellRenderer(table.getDefaultRenderer(Object.class)));

		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	/**
	 * Gets a map containing an <code>Object</code> representation of the Look
	 * and Feel keys and their values.
	 * 
	 * @return A map with laf keys and values.
	 */
	private static LinkedHashMap<String, Object> getLookAndFeelOverview() {
		Collection<String> keys = getLookAndFeelKeys();
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		for (String key : keys) {
			Object val = defaults.get(key);
			map.put(key, (val != null) ? val : "null");
		}
		return map;
	}

	/**
	 * Gets a sorted collection of all Look and Feel keys.
	 * 
	 * @return A sorted collection of all Look and Feel keys.
	 */
	private static Collection<String> getLookAndFeelKeys() { ;
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		return defaults.keySet().stream().map(Object::toString).sorted().collect(Collectors.toList());
	}

	private static class MyTableModel extends AbstractTableModel {

		private String[] columns = { "Key", "Value" };

		private static final long serialVersionUID = 6272326208121321089L;
		private Map<String, Object> data;
		private List<String> keys;

		public MyTableModel(Map<String, Object> data) {
			this.data = data;
			this.keys = new ArrayList<>(data.keySet());
		}

		@Override
		public int getRowCount() {
			return this.keys.size();
		}

		@Override
		public int getColumnCount() {
			return columns.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			String key = this.keys.get(rowIndex);
			if (columnIndex == 0) {
				return key;
			}
			return this.data.get(key);
		}

		@Override
		public String getColumnName(int column) {
			return this.columns[column];
		}
	}


	/**
	 * Decorator renderer to display colours using {@link ColorIcon}.
	 */
	static class ColorIconTableCellRenderer implements TableCellRenderer {
		TableCellRenderer tableCellRenderer;

		public ColorIconTableCellRenderer(TableCellRenderer renderer) {
			this.tableCellRenderer = renderer;
		}


		public Component getTableCellRendererComponent(JTable table, Object value,
													   boolean isSelected, boolean hasFocus, int row, int column) {

			Component comp = tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (value instanceof Color) {
				Color colorUI = (Color)value;
				JLabel compLabel = (JLabel)comp;
				compLabel.setIcon(new ColorIcon(colorUI));

				return compLabel;
			}

			return comp;
		}
	}
}
