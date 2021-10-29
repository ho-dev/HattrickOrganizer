package module.transfer.test;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.db.DBManager;
import core.model.player.Player;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class WagesOverviewPanel extends JPanel {

	private Player player;
	private JTable table;

	WagesOverviewPanel() {
		initComponents();
	}
	
	void setPlayer(Player player) {
		this.player = player;
		refreshData();
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		this.table = new JTable(new MyTableModel(new ArrayList<>()));
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	private void refreshData() {
		if (this.player != null) {
			Date buyingDate = Calc.getBuyingDate(player.getPlayerID());
			if (buyingDate == null) {
				buyingDate = new Date(DBManager.instance()
						.getSpielerFirstHRF(player.getPlayerID()).getHrfDate().getTime());
			}
			List<Date> updates = Calc.getUpdates(Calc.getEconomyDate(), buyingDate, new Date());
			List<Wage> wagesByAge = Wage.getWagesByAge(player.getPlayerID());

			var ageWageMap = new HashMap<Integer, Wage>();
			for (Wage wage : wagesByAge) {
				ageWageMap.put(wage.getAge(), wage);
			}

			Date birthDay17 = Calc.get17thBirthday(player.getPlayerID());
			var entries = new ArrayList<Entry>();
			for (Date date : updates) {
				int ageAt = Calc.getAgeAt(birthDay17, date);
				Entry entry = new Entry();
				entry.age = ageAt;
				entry.economyUpdate = date;
				entry.htWeek = new HTWeek(date);
				entry.wage = ageWageMap.get(ageAt).getWage();
				entries.add(entry);
			}

			this.table.setModel(new MyTableModel(entries));
		} else {
			this.table.setModel(new MyTableModel(new ArrayList<>()));
		}
	}

	private class MyTableModel extends AbstractTableModel {

		private String[] columnNames = { "Age", "Season", "Week", "Economy update", "Wage payed" };
		private List<Entry> list;

		public MyTableModel(List<Entry> list) {
			this.list = list;
		}

		@Override
		public int getRowCount() {
			return list.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Entry entry = this.list.get(rowIndex);
			return switch (columnIndex) {
				case 0 -> entry.age;
				case 1 -> entry.htWeek.getSeason();
				case 2 -> entry.htWeek.getWeek();
				case 3 -> entry.economyUpdate;
				case 4 -> entry.wage;
				default -> null;
			};
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
	}

	private class Entry {
		HTWeek htWeek;
		int age;
		Date economyUpdate;
		int wage;
	}
}
