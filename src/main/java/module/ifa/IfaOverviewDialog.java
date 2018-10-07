package module.ifa;

import core.gui.comp.renderer.DoubleTableCellRenderer;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.WorldDetailLeague;
import core.model.WorldDetailsManager;
import module.ifa.model.Country;
import module.ifa.model.IfaModel;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

public class IfaOverviewDialog extends JDialog {

	private static final long serialVersionUID = 5745450861289812050L;
	private final IfaModel model;

	public IfaOverviewDialog(IfaModel model, Frame parent) {
		super(parent);
		this.model = model;
		initComponents();
		pack();
	}

	private void initComponents() {
		setTitle(HOVerwaltung.instance().getLanguageString("ifa.infoDialog.title"));

		MyTableModel tblModel = new MyTableModel();
		JTable table = new JTable(tblModel);

		TableColumn countryColumn = table.getColumnModel().getColumn(MyTableModel.COL_COUNTRY);
		countryColumn.setCellRenderer(new CountryTableCellRenderer());
		countryColumn.setPreferredWidth(250);

		BooleanTableCellRenderer booleanRenderer = new BooleanTableCellRenderer();
		TableColumn visitedColumn = table.getColumnModel().getColumn(MyTableModel.COL_VISITED);
		visitedColumn.setCellRenderer(booleanRenderer);
		visitedColumn.setPreferredWidth(35);

		TableColumn hostedColumn = table.getColumnModel().getColumn(MyTableModel.COL_HOSTED);
		hostedColumn.setCellRenderer(booleanRenderer);
		hostedColumn.setPreferredWidth(35);

		table.getColumnModel().getColumn(MyTableModel.COL_COOLNESS)
				.setCellRenderer(new DoubleTableCellRenderer(2));

		table.getColumnModel().getColumn(MyTableModel.COL_ACTIVE_USERS)
				.setCellRenderer(new DoubleTableCellRenderer(0));

		TableRowSorter<MyTableModel> sorter = new TableRowSorter<MyTableModel>(tblModel);
		sorter.setComparator(MyTableModel.COL_COUNTRY, new Comparator<Country>() {

			@Override
			public int compare(Country o1, Country o2) {
				return o1.getName().compareTo(o2.getName());
			}

		});
		List<SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new SortKey(MyTableModel.COL_COUNTRY, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		table.setRowSorter(sorter);

		JButton closeButton = new JButton();
		closeButton.setText(HOVerwaltung.instance().getLanguageString("ls.button.close"));
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		getContentPane().add(new JScrollPane(table), gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.gridy = 1;
		gbc.insets = new Insets(8, 8, 8, 8);
		getContentPane().add(closeButton, gbc);
	}

	private class MyTableModel extends AbstractTableModel {

		static final int COL_COUNTRY = 0;
		static final int COL_ACTIVE_USERS = 1;
		static final int COL_COOLNESS = 2;
		static final int COL_VISITED = 3;
		static final int COL_HOSTED = 4;
		private static final long serialVersionUID = 4643461935740184896L;
		private final List<Entry> list;
		private String[] columns = { "Country", "Active users", "Coolness", "V", "H" };

		MyTableModel() {
			WorldDetailLeague[] leagues = WorldDetailsManager.instance().getLeagues();
			this.list = new ArrayList<Entry>(leagues.length);

			for (WorldDetailLeague league : leagues) {
				Entry entry = new Entry();
				entry.country = new Country(league.getCountryId());
				entry.league = league;
				entry.coolness = PluginIfaUtils.getCoolness(entry.country.getCountryId());
				this.list.add(entry);
			}
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
			case COL_COUNTRY:
				return HOVerwaltung.instance().getLanguageString("ifa.statisticsTable.col.country");
			case COL_ACTIVE_USERS:
				return HOVerwaltung.instance().getLanguageString("ifa.infoDialog.col.activeUsers");
			case COL_COOLNESS:
				return HOVerwaltung.instance()
						.getLanguageString("ifa.statisticsTable.col.coolness");
			case COL_VISITED:
				return HOVerwaltung.instance().getLanguageString("ifa.infoDialog.col.visited");
			case COL_HOSTED:
				return HOVerwaltung.instance().getLanguageString("ifa.infoDialog.col.hosted");
			default:
				return super.getColumnName(columnIndex);
			}
		}

		@Override
		public int getRowCount() {
			return this.list.size();
		}

		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case COL_COUNTRY:
				return Country.class;
			case COL_ACTIVE_USERS:
				return Integer.class;
			case COL_COOLNESS:
				return Double.class;
			case COL_VISITED:
			case COL_HOSTED:
				return Boolean.class;
			default:
				return super.getColumnClass(columnIndex);
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Entry entry = this.list.get(rowIndex);
			switch (columnIndex) {
			case COL_COUNTRY:
				return entry.country;
			case COL_ACTIVE_USERS:
				return entry.league.getActiveUsers();
			case COL_COOLNESS:
				return entry.coolness;
			case COL_VISITED:
				return IfaOverviewDialog.this.model.isVisited(entry.country.getCountryId());
			case COL_HOSTED:
				return IfaOverviewDialog.this.model.isHosted(entry.country.getCountryId());
			default:
				return null;
			}
		}
	}

	private class Entry {
		Country country;
		WorldDetailLeague league;
		double coolness;
	}

	private class CountryTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -5212837673330509051L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {

			Country country = (Country) value;
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, country.getName(),
					isSelected, hasFocus, row, column);
			label.setIcon(country.getCountryFlag());
			return label;
		}
	}

	private class BooleanTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -5648974651813645856L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {

			JLabel label = (JLabel) super.getTableCellRendererComponent(table, "", isSelected,
					hasFocus, row, column);
			Boolean played = (Boolean) value;
			if (played) {
				label.setIcon(ThemeManager.getIcon(HOIconName.IFA_VISITED));
			} else {
				label.setIcon(null);
			}
			return label;
		}
	}
}
