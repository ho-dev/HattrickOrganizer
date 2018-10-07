package module.transfer.test;

import core.db.DBManager;
import core.util.StringUtils;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class WagesPanel extends JPanel {

	private static final long serialVersionUID = -7417813938334965612L;

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
		List<Data> list = new ArrayList<Data>();

		Date buyingDate = Calc.getBuyingDate(playerId);
		if (buyingDate == null) {
			buyingDate = new Date(DBManager.instance()
					.getSpielerFirstHRF(playerId).getHrfDate().getTime());
		}
		List<Date> updates = Calc.getUpdates(Calc.getEconomyDate(), buyingDate,
				new Date());
		List<Wage> wagesByAge = Wage.getWagesByAge(playerId);

		Map<Integer, Wage> ageWageMap = new HashMap<Integer, Wage>();
		for (Wage wage : wagesByAge) {
			ageWageMap.put(Integer.valueOf(wage.getAge()), wage);
		}

		Date birthDay17 = Calc.get17thBirthday(playerId);

		for (int i = 0; i < updates.size(); i++) {
			Date date = updates.get(i);
			Data data = new Data();
			int ageAt = Calc.getAgeAt(birthDay17, date);
			data.age = ageAt;
			data.date = date;
			data.htWeek = HTWeek.getHTWeekByDate(date);
			data.wage = ageWageMap.get(Integer.valueOf(ageAt)).getWage();

			list.add(data);
		}

		return list;
	}

	private List<RowData> getRows(int playerId) {
		List<RowData> list = new ArrayList<RowData>();

		List<Data> dataList = getData(playerId);
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		Map<Integer, Sum> sumMap = new HashMap<Integer, Sum>();
		int sum = 0;

		for (int i = 0; i < dataList.size(); i++) {
			Data data = dataList.get(i);
			RowData row = new RowData();
			row.age = String.valueOf(data.age);
			row.date = df.format(data.date);

			row.season = String.valueOf(data.htWeek.getSeason());
			row.week = String.valueOf(data.htWeek.getWeek());
			row.wage = String.valueOf(data.wage);

			sum += data.wage;

			if (i < dataList.size() - 1) {
				if (dataList.get(i + 1).htWeek.getSeason() > data.htWeek
						.getSeason()) {
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
		HTWeek htWeek;
		Date date;
		int wage;
	}
}
