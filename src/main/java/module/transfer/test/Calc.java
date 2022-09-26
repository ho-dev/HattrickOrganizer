package module.transfer.test;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.util.HODateTime;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Calc {

	public static HODateTime getBuyingDate(int playerId) {
		var dates = getBuyingDates(playerId);
		if (!dates.isEmpty()) {
			return dates.get(0);
		}
		return null;
	}

	static private final DBManager.PreparedStatementBuilder transferStatementBuilder = new DBManager.PreparedStatementBuilder(
			"SELECT * FROM transfer WHERE playerid=? AND buyerid=?");
	public static List<HODateTime> getBuyingDates(int playerId) {
		var list = new ArrayList<HODateTime>();
		ResultSet rs = Objects.requireNonNull(DBManager.instance().getAdapter()).executePreparedQuery(transferStatementBuilder.getStatement(), playerId, HOVerwaltung.instance().getModel().getBasics().getTeamId());
		try {
			while (rs.next()) {
				list.add(HODateTime.fromDbTimestamp(rs.getTimestamp("date")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	static private final DBManager.PreparedStatementBuilder playerStatementBuilder = new DBManager.PreparedStatementBuilder(
			"SELECT LIMIT 0 1 AGE, AGEDAYS, DATUM FROM player WHERE spielerid=?");
	public static HODateTime get17thBirthday(int playerId) {
		ResultSet rs = Objects.requireNonNull(DBManager.instance().getAdapter()).executePreparedQuery(playerStatementBuilder.getStatement(), playerId);
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
			sum += ageWageMap.get(ageAt).getWage();
		}
		return sum;
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
