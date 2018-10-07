package module.transfer.test;

import core.db.DBManager;
import core.model.player.Spieler;

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
	private Spieler spieler;
	private JTable table;

	WagesSumPanel() {
		initComponents();
	}

	void setPlayer(Spieler player) {
		this.spieler = player;
		refreshData();
	}
	
	private void initComponents() {
		setLayout(new BorderLayout());
		this.table = new JTable();
		this.table.setAutoCreateRowSorter(true);
		this.table.setModel(new MyTableModel(new ArrayList<Entry>()));
		List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
		this.table.getRowSorter().setSortKeys(sortKeys);
		add(new JScrollPane(this.table));		
	}

	private void refreshData() {
		if (this.spieler != null) {
			Transfer t = Transfer.getTransfer(spieler.getSpielerID());
			Date buyingDate;
			if (spieler.isHomeGrown()) {
				buyingDate = new Date(DBManager.instance()
						.getSpielerFirstHRF(spieler.getSpielerID()).getHrfDate().getTime());
			} else {
				buyingDate = t.purchaseDate;
			}
			
			Date sellingDate = (t.sellingDate != null) ? t.sellingDate : new Date(); 
			List<Date> updates = Calc.getUpdates(Calc.getEconomyDate(), buyingDate, sellingDate);
			
			List<Wage> wagesByAge = Wage.getWagesByAge(spieler.getSpielerID());

			Map<Integer, Wage> ageWageMap = new HashMap<Integer, Wage>();
			for (Wage wage : wagesByAge) {
				ageWageMap.put(Integer.valueOf(wage.getAge()), wage);
			}

			Date birthDay17 = Calc.get17thBirthday(spieler.getSpielerID());
			Map<Integer, Entry> ageWageSumMap = new HashMap<Integer, Entry>();
			for (Date date : updates) {
				int ageAt = Calc.getAgeAt(birthDay17, date);
				Integer key = Integer.valueOf(ageAt);
				Entry value = ageWageSumMap.get(key);
				if (value == null) {
					value = new Entry();
					value.age = ageAt;
					value.wage = ageWageMap.get(Integer.valueOf(ageAt)).getWage();
					ageWageSumMap.put(key, value);
				}
				value.total += ageWageMap.get(Integer.valueOf(ageAt)).getWage();
				value.count++;
			}

			List<Entry> data = new ArrayList<Entry>(ageWageSumMap.values());
			this.table.setModel(new MyTableModel(data));
		} else {
			this.table.setModel(new MyTableModel(new ArrayList<Entry>()));
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
			switch (columnIndex) {
			case 0:
				return wages.age;
			case 1:
				return wages.wage;
			case 2:
				return wages.count;
			case 3:
				return wages.total;
			}
			return null;
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
	}

}
