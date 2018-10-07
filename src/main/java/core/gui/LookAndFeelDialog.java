package core.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;

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
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	/**
	 * Gets a map containing a <code>String</code> representation of the Look
	 * and Feel keys and their values.
	 * 
	 * @return A map with laf keys and values.
	 */
	private static LinkedHashMap<String, String> getLookAndFeelOverview() {
		Collection<String> keys = getLookAndFeelKeys();
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (String key : keys) {
			Object val = defaults.get(key);
			String strVal = (val != null) ? val.toString() : "null";
			map.put(key, strVal);
		}
		return map;
	}

	/**
	 * Gets a sorted collection of all Look and Feel keys.
	 * 
	 * @return A sorted collection of all Look and Feel keys.
	 */
	private static Collection<String> getLookAndFeelKeys() {
		ArrayList<String> list = new ArrayList<String>();
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		Iterator<Object> it = defaults.keySet().iterator();

		while (it.hasNext()) {
			list.add(it.next().toString());
		}
		Collections.sort(list);
		return list;
	}

	private class MyTableModel extends AbstractTableModel {

		private String[] columns = { "Key", "Value" };

		private static final long serialVersionUID = 6272326208121321089L;
		private Map<String, String> data;
		private List<String> keys;

		public MyTableModel(Map<String, String> data) {
			this.data = data;
			this.keys = new ArrayList<String>(data.keySet());
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
}
