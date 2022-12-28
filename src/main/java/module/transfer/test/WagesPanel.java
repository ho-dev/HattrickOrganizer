package module.transfer.test;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.util.HODateTime;
import core.util.StringUtils;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class WagesPanel extends JPanel {

	WagesPanel(int playerId) {
		initComponents(playerId);
	}

	private void initComponents(int playerId) {
		setLayout(new GridBagLayout());

		List<RowData> rows = getRows(playerId);
		GridBagConstraints gbc = new GridBagConstraints();
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

		gbc.gridy = 0;
		gbc.insets = new Insets(2, 2, 2, 2);
		for (RowData row : rows) {
			addRow(row, gbc);
			gbc.gridy++;
		}
	}

	private List<Data> getData(int playerId) {
		var list = new ArrayList<Data>();

		var buyingDate = Calc.getBuyingDate(playerId);
		if (buyingDate == null) {
			buyingDate = DBManager.instance().loadPlayerFirstHRF(playerId).getHrfDate();
		}
		var updates = Calc.getUpdates(HOVerwaltung.instance().getModel().getXtraDaten().getEconomyDate(), buyingDate, HODateTime.now());
		List<Wage> wagesByAge = Wage.getWagesByAge(playerId);

		var ageWageMap = new HashMap<Integer, Wage>();
		for (Wage wage : wagesByAge) {
			ageWageMap.put(wage.getAge(), wage);
		}

		var birthDay17 = Calc.get17thBirthday(playerId);

		for (var date : updates) {
			Data data = new Data();
			int ageAt = Calc.getAgeAt(birthDay17, date);
			data.age = ageAt;
			data.date = date;
			data.wage = ageWageMap.get(ageAt).getWage();
			list.add(data);
		}

		return list;
	}

	private List<RowData> getRows(int playerId) {
		var list = new ArrayList<RowData>();
		List<Data> dataList = getData(playerId);
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		int sum = 0;
		for (int i = 0; i < dataList.size(); i++) {
			Data data = dataList.get(i);
			RowData row = new RowData();
			row.age = String.valueOf(data.age);
			row.date = df.format(data.date);

			var htweek = data.date.toHTWeek();
			row.season = String.valueOf(htweek.season);
			row.week = String.valueOf(htweek.week);
			row.wage = String.valueOf(data.wage);

			sum += data.wage;

			if (i < dataList.size() - 1) {
				if (dataList.get(i + 1).date.toHTWeek().season > data.date.toHTWeek().season) {
					row.wageSum = String.valueOf(sum);
				}
			}
			list.add(row);
		}

		return list;
	}

	private void addRow(RowData data, GridBagConstraints gbc) {
		if (!StringUtils.isEmpty(data.age)) {
			JLabel ageLabel = new JLabel(data.age);
			gbc.gridx = 0;
			add(ageLabel, gbc);
		}

		if (!StringUtils.isEmpty(data.season)) {
			JLabel seasonLabel = new JLabel(data.season);
			gbc.gridx = 1;
			add(seasonLabel, gbc);
		}

		if (!StringUtils.isEmpty(data.week)) {
			JLabel weekLabel = new JLabel(data.week);
			gbc.gridx = 2;
			add(weekLabel, gbc);
		}

		if (!StringUtils.isEmpty(data.date)) {
			JLabel dateLabel = new JLabel(data.date);
			gbc.gridx = 3;
			add(dateLabel, gbc);
		}

		if (!StringUtils.isEmpty(data.wage)) {
			JLabel wageLabel = new JLabel(data.wage);
			gbc.gridx = 4;
			add(wageLabel, gbc);
		}

		if (!StringUtils.isEmpty(data.wageSum)) {
			JLabel sumLabel = new JLabel(data.wageSum);
			sumLabel.setFont(sumLabel.getFont().deriveFont(Font.BOLD));
			gbc.gridx = 5;
			add(sumLabel, gbc);
		}
	}

	private class Sum {
		int sum;
	}

	private class RowData {
		String age;
		String season;
		String week;
		String date;
		String wage;
		String wageSum;
	}

	private class Data {
		int age;
		HODateTime date;
		int wage;
	}
}
