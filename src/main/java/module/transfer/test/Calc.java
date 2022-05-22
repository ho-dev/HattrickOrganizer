package module.transfer.test;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.util.HODateTime;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calc {

	public static HODateTime getBuyingDate(int playerId) {
		var dates = getBuyingDates(playerId);
		if (!dates.isEmpty()) {
			return dates.get(0);
		}
		return null;
	}

	public static List<HODateTime> getBuyingDates(int playerId) {
		var list = new ArrayList<HODateTime>();

		String query = "SELECT * FROM transfer WHERE playerid=" + playerId + " AND buyerid="
				+ HOVerwaltung.instance().getModel().getBasics().getTeamId();
		ResultSet rs = DBManager.instance().getAdapter().executeQuery(query);
		try {
			while (rs.next()) {
				list.add(HODateTime.fromDbTimestamp(rs.getTimestamp("date")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	public static HODateTime get17thBirthday(int playerId) {
		String query = "SELECT LIMIT 0 1 AGE, AGEDAYS, DATUM FROM player WHERE spielerid=" + playerId;
		ResultSet rs = DBManager.instance().getAdapter().executeQuery(query);
		try {
			if (rs.next()) {
				var rsDate = HODateTime.fromDbTimestamp(rs.getTimestamp("DATUM"));
				int yearsDiffTo17 = rs.getInt("AGE") - 17;
				int daysSince17 = yearsDiffTo17 * 112 + rs.getInt("AGEDAYS");
				return rsDate.minus(daysSince17, ChronoUnit.DAYS);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getWagesSum(int playerId, HODateTime dateFrom, HODateTime dateTo) {
		var updates = Calc.getUpdates(HOVerwaltung.instance().getModel().getXtraDaten().getEconomyDate(), dateFrom, dateTo);
		List<Wage> wagesByAge = Wage.getWagesByAge(playerId);

		Map<Integer, Wage> ageWageMap = new HashMap<>();
		for (Wage wage : wagesByAge) {
			ageWageMap.put(wage.getAge(), wage);
		}

		var birthDay17 = Calc.get17thBirthday(playerId);
		int sum = 0;
		for (var date : updates) {
			int ageAt = Calc.getAgeAt(birthDay17, date);
			Integer key = ageAt;
			sum += ageWageMap.get(ageAt).getWage();
		}
		return sum;
	}

	public static int getAgeAt(HODateTime date, int playerId) {
		String query = "SELECT LIMIT 0 1 AGE, AGEDAYS, DATUM FROM player WHERE spielerid="
				+ playerId;
		ResultSet rs = DBManager.instance().getAdapter().executeQuery(query);
		try {
			assert rs != null;
			if (rs.next()) {
				int age = rs.getInt("AGE") * 112 + rs.getInt("AGEDAYS");
				var rsDate =HODateTime.fromDbTimestamp(rs.getTimestamp("DATUM"));
				return (int)Duration.between(rsDate.instant, date.instant).toDays() + age;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public static int getAgeAt(HODateTime birthDay17, HODateTime date) {
		if (date.isBefore(birthDay17)) {
			throw new IllegalArgumentException();
		}
		int days = (int)Duration.between(birthDay17.instant, date.instant).toDays();

		return 17 + days / 112;
	}

	public static List<HODateTime> getUpdates(HODateTime updateTime, HODateTime from, HODateTime to) {
		var list = new ArrayList<HODateTime>();
		while (!updateTime.isBefore(from)) updateTime=updateTime.minus(7, ChronoUnit.DAYS);
		while (!updateTime.isAfter(to)){
			list.add(updateTime);
			updateTime=updateTime.plus(7, ChronoUnit.DAYS);
		}
		return list;
	}
}
