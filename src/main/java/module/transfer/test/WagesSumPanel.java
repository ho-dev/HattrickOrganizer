package module.transfer.test;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.HODateTime;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;

public class WagesSumPanel extends JPanel {

	private static final long serialVersionUID = -5258776512974633343L;
	private Player player;
	private JTable table;

	WagesSumPanel() {
		initComponents();
	}

	void setPlayer(Player player) {
		this.player = player;
		refreshData();
	}
	
	private void initComponents() {
		setLayout(new BorderLayout());
		this.table = new JTable();
		this.table.setAutoCreateRowSorter(true);
		this.table.setModel(new MyTableModel(new ArrayList<>()));
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
		this.table.getRowSorter().setSortKeys(sortKeys);
		add(new JScrollPane(this.table));		
	}

	private void refreshData() {
		if (this.player != null) {
			Transfer t = Transfer.getTransfer(player.getPlayerID());
			HODateTime buyingDate;
			if (player.isHomeGrown()) {
				buyingDate = DBManager.instance().getSpielerFirstHRF(player.getPlayerID()).getHrfDate();
			} else {
				buyingDate = t.purchaseDate;
			}
			
			var sellingDate = (t.sellingDate != null) ? t.sellingDate : HODateTime.now();
			var updates = Calc.getUpdates(HOVerwaltung.instance().getModel().getXtraDaten().getEconomyDate(), buyingDate, sellingDate);
			
			List<Wage> wagesByAge = Wage.getWagesByAge(player.getPlayerID());

			Map<Integer, Wage> ageWageMap = new HashMap<>();
			for (Wage wage : wagesByAge) {
				ageWageMap.put(wage.getAge(), wage);
			}

			var birthDay17 = Calc.get17thBirthday(player.getPlayerID());
			Map<Integer, Entry> ageWageSumMap = new HashMap<>();
			for (var date : updates) {
				int ageAt = Calc.getAgeAt(birthDay17, date);
				Integer key = ageAt;
				Entry value = ageWageSumMap.get(key);
				if (value == null) {
					value = new Entry();
					value.age = ageAt;
					value.wage = ageWageMap.get(ageAt).getWage();
					ageWageSumMap.put(key, value);
				}
				value.total += ageWageMap.get(ageAt).getWage();
				value.count++;
			}

			List<Entry> data = new ArrayList<>(ageWageSumMap.values());
			this.table.setModel(new MyTableModel(data));
		} else {
			this.table.setModel(new MyTableModel(new ArrayList<>()));
		}
	}

	private class Entry {
		int wage;
		int total;
		int age;
		int count;
	}

	private class MyTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -178729026403624599L;
		private String[] columnNames = { "Age", "Wage", "Wages payed", "Total" };
		private List<Entry> data;

		MyTableModel(List<Entry> data) {
			this.data = data;
		}

		@Override
		public int getRowCount() {
			return this.data.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Entry wages = this.data.get(rowIndex);
			return switch (columnIndex) {
				case 0 -> wages.age;
				case 1 -> wages.wage;
				case 2 -> wages.count;
				case 3 -> wages.total;
				default -> null;
			};
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
	}

}
