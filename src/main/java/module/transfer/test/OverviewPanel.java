package module.transfer.test;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.player.Player;
import module.transfer.PlayerTransfer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class OverviewPanel extends JPanel {

	private static final long serialVersionUID = -5446688280760617921L;
	private JTable table;

	public OverviewPanel() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		List<TableRow> data = new ArrayList<TableRow>();

		List<PlayerTransfer> sold = DBManager.instance().getTransfers(0, false, true);
		List<PlayerTransfer> bought = DBManager.instance().getTransfers(0, true, false);

		for (PlayerTransfer sale : sold) {
			TableRow row = new TableRow(getBuy(sale, bought), sale);
			if (addSomeData(row)) {
				data.add(row);
			}
		}

		this.table = new JTable(new MyTableModel(data));
		table.setPreferredScrollableViewportSize(new Dimension(1000, 400));
		table.getColumnModel().getColumn(0).setCellRenderer(new DateRenderer());
		table.getColumnModel().getColumn(2).setCellRenderer(new NumberRenderer());
		table.getColumnModel().getColumn(3).setCellRenderer(new DateRenderer());
		table.getColumnModel().getColumn(4).setCellRenderer(new NumberRenderer());
		table.getColumnModel().getColumn(5).setCellRenderer(new NumberRenderer());
		table.getColumnModel().getColumn(6).setCellRenderer(new NumberRenderer());
		table.getColumnModel().getColumn(7).setCellRenderer(new NumberRenderer());
		table.getColumnModel().getColumn(8).setCellRenderer(new NumberRenderer());
		table.getColumnModel().getColumn(9).setCellRenderer(new NumberRenderer());
		table.setAutoCreateRowSorter(true);
		
		List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
		table.getRowSorter().setSortKeys(sortKeys);

		add(new JScrollPane(table), BorderLayout.CENTER);

		JButton button = new JButton("test");
//		add(button, BorderLayout.SOUTH);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				selectedRow = table.convertRowIndexToModel(selectedRow);
				int playerId = ((MyTableModel) table.getModel()).getPlayerId(selectedRow);

				WagesPanel p = new WagesPanel(playerId);
				JDialog dlg = new JDialog();
				dlg.getContentPane().add(new JScrollPane(p));
				dlg.pack();
				dlg.setVisible(true);
			}
		});
	}

	private boolean addSomeData(TableRow row) {
		Player player = getPlayer(row.getSale().getPlayerId(), row.getSale().getPlayerName());

		if (player == null) {
			System.out.println("####- player " + row.getSale().getPlayerId() + " ("
					+ row.getSale().getPlayerName() + ") not found");
			return false;
		}
		if (player.isHomeGrown()) {
			System.out.println("####- player " + row.getSale().getPlayerId() + " ("
					+ row.getSale().getPlayerName() + ") isHomeGrown");
			return false;
		}

		Date buyingDate = row.getBuyingDate();
		Date birthDay17 = Calc.get17thBirthday(player.getSpielerID());
		int wagesSum = 0;

		// if (player.isHomeGrown()) {
		// buyingDate = new Date(DBManager.instance()
		// .getSpielerFirstHRF(player.getSpielerID()).getHrfDate()
		// .getTime());
		// }

		List<Date> updates = Calc.getUpdates(Calc.getEconomyDate(), buyingDate,
				row.getSellingDate());
		row.setWeeksInTeam(updates.size());

		Map<Integer, Wage> ageWageMap = new HashMap<Integer, Wage>();
		List<Wage> wagesByAge = Wage.getWagesByAge(player.getSpielerID());
		if (!wagesByAge.isEmpty()) {
			// order by age
			Collections.sort(wagesByAge, new Comparator<Wage>() {

				@Override
				public int compare(Wage o1, Wage o2) {
					return Integer.valueOf(o1.getAge()).compareTo(Integer.valueOf(o2.getAge()));
				}
			});
			for (Wage wage : wagesByAge) {
				ageWageMap.put(Integer.valueOf(wage.getAge()), wage);
			}

			// the first wage is payed immediately when the player is bought
			wagesSum += wagesByAge.get(0).getWage();
		}

		boolean allWagesFound = true;
		for (Date date : updates) {
			int ageAt = Calc.getAgeAt(birthDay17, date);
			Wage wage = ageWageMap.get(Integer.valueOf(ageAt));
			if (wage != null) {
				wagesSum += wage.getWage();
				// System.out.println("####- " + player.getName() + " wage is "
				// + wage.getWage() + " for age " + ageAt);
			} else {
				System.out
						.println("####- " + player.getName() + " wage not found for age " + ageAt);
				allWagesFound = false;
			}

		}

		if (allWagesFound) {
			row.setSumOfLoan(wagesSum);
		} else {
			return false;
		}

		int daysInTeam = Calc.getDaysBetween(row.getSellingDate(), buyingDate);
		double fee = row.getSellingPrice() * (TransferFee.getFee(daysInTeam) / 100);
		double feePreviousClub = 0;
		double feeMotherClub = 0;
		if (!player.isHomeGrown()) {
//			feePreviousClub = row.getSellingPrice() * (TransferFee.feePreviousClub(2) / 100);
			feeMotherClub = row.getSellingPrice() * 0.02d;
		}
		int cost = (int) (fee + feePreviousClub + feeMotherClub);
		row.setSellingCosts(cost);
		int gewinn = row.getSellingPrice() - row.getBuyPrice() - wagesSum - cost;
		row.setNetProfit(gewinn);

		return true;
	}

	private PlayerTransfer getBuy(PlayerTransfer sale, List<PlayerTransfer> buys) {
		for (PlayerTransfer buy : buys) {
			if (buy.getPlayerId() == sale.getPlayerId()) {
				if (sale.getSellerid() == buy.getBuyerid()) {
					return buy;
				}
			}
		}
		return null;
	}

	private Player getPlayer(int playerId, String playerName) {
		List<Player> all = HOVerwaltung.instance().getModel().getAllOldSpieler();
		for (Player player : all) {
			if (playerId != 0) {
				if (player.getSpielerID() == playerId) {
					return player;
				}
			} else {
				if (player.getName().equals(playerName)) {
					return player;
				}
			}
		}
		return null;
	}

	private class Sum {
		int val;
	}

	private class MyTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1236642855349013418L;
		private String[] columns = { "Buying date", "Name", "Buy price", "Selling date",
				"Selling price", "Weeks in team", "Selling costs", "Sum of loan", "Net profit",
				"Profit per week" };
		private List<TableRow> data;

		MyTableModel(List<TableRow> data) {
			this.data = data;
		}

		MyTableModel() {
			this.data = new ArrayList<TableRow>();
		}

		@Override
		public int getRowCount() {
			return this.data.size();
		}

		@Override
		public int getColumnCount() {
			return columns.length;
		}

		public int getPlayerId(int row) {
			return this.data.get(row).getSale().getPlayerId();
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Date.class;
			case 1:
				return String.class;
			case 2:
				return Integer.class;
			case 3:
				return Date.class;
			case 4:
				return Integer.class;
			case 5:
				return Integer.class;
			case 6:
				return Integer.class;
			case 7:
				return Integer.class;
			case 8:
				return Integer.class;
			case 9:
				return Integer.class;
			default:
				return super.getColumnClass(columnIndex);
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			TableRow row = this.data.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return row.getBuyingDate();
			case 1:
				return row.getPlayerName();
			case 2:
				return row.getBuyPrice();
			case 3:
				return row.getSellingDate();
			case 4:
				return row.getSellingPrice();
			case 5:
				return row.getWeeksInTeam();
			case 6:
				return row.getSellingCosts();
			case 7:
				return row.getSumOfLoan();
			case 8:
				return row.getNetProfit();
			case 9:
				return row.getProfitPerWeek();
			default:
				return null;
			}
		}

		@Override
		public String getColumnName(int column) {
			return columns[column];
		}

	}

	private class TableRow {

		private PlayerTransfer buy;
		private PlayerTransfer sale;
		private int sumOfLoan;
		private int weeksInTeam;
		private int netProfit;
		private int sellingCosts;

		TableRow(PlayerTransfer buy, PlayerTransfer sale) {
			this.buy = buy;
			this.sale = sale;
		}

		public Date getBuyingDate() {
			return (this.buy != null) ? this.buy.getDate() : null;
		}

		public String getPlayerName() {
			return this.sale.getPlayerName();
		}

		public int getBuyPrice() {
			return (this.buy != null) ? this.buy.getPrice() : 0;
		}

		public Date getSellingDate() {
			return this.sale.getDate();
		}

		public int getSellingPrice() {
			return this.sale.getPrice();
		}

		public int getWeeksInTeam() {
			return this.weeksInTeam;
		}

		public void setWeeksInTeam(int value) {
			this.weeksInTeam = value;
		}

		public int getSumOfLoan() {
			return this.sumOfLoan;
		}

		public void setSumOfLoan(int value) {
			this.sumOfLoan = value;
		}

		public int getNetProfit() {
			return this.netProfit;
		}

		public void setNetProfit(int value) {
			this.netProfit = value;
		}

		public int getProfitPerWeek() {
			if (this.weeksInTeam == 0) {
				return this.netProfit;
			}
			return this.netProfit / this.weeksInTeam;
		}

		public int getSellingCosts() {
			return this.sellingCosts;
		}

		public void setSellingCosts(int value) {
			this.sellingCosts = value;
		}

		public PlayerTransfer getBuy() {
			return buy;
		}

		public void setBuy(PlayerTransfer buy) {
			this.buy = buy;
		}

		public PlayerTransfer getSale() {
			return sale;
		}

		public void setSale(PlayerTransfer sale) {
			this.sale = sale;
		}
	}

	private class DateRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -1900876860494850567L;
		private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			String val = "";
			if (value != null) {
				val = dateFormat.format((Date) value);
			}
			return super.getTableCellRendererComponent(table, val, isSelected, hasFocus, row,
					column);
		}
	}

	private class NumberRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 6523652645812912812L;
		private NumberFormat numberFormat = NumberFormat.getIntegerInstance();

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {

			String val = "";
			if (value != null) {
				val = numberFormat.format((Number) value);
			}

			JLabel comp = (JLabel) super.getTableCellRendererComponent(table, val, isSelected,
					hasFocus, row, column);

			comp.setHorizontalAlignment(SwingConstants.RIGHT);
			return comp;
		}
	}
}
