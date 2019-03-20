package module.transfer.test;

import core.db.DBManager;
import core.model.HOVerwaltung;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calc {

	public static void main(String[] args) {
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:s");
		try {
			// 26.05. 02.06. 09.06. 16.03. 23.06. 30.06 07.07. 14.07. 21.07.
			// 28.07. 04.08. 11.08. 18.08.

			// Date buyingDate = df.parse("25.05.2012 17:00:12");
			// Date sellingDate = df.parse("09.06.2012 10:30:45");
			// WeekDayTime time = new WeekDayTime(Calendar.SATURDAY, 0, 0, 1);
			//
			// Date lastEconomyDateInPeriod =
			// lastUpdateBefore(Calendar.SATURDAY, time, sellingDate);
			// System.out.println("buyingDate: " + buyingDate);
			// System.out.println("sellingDate: " + sellingDate);
			// System.out.println("lastEconomyDateInPeriod: " +
			// lastEconomyDateInPeriod);
			// System.out.println("updates: " + getUpdates(buyingDate,
			// lastEconomyDateInPeriod));
			//
			// // 18 Jahre und 28 Tage, nächster Geburtstag: 23.10.2012
			//
			// int age = 18 * 112 + 28;
			// getWages(age, buyingDate, sellingDate, time);

			WeekDayTime time = new WeekDayTime(Calendar.SATURDAY, 0, 0, 1);
			Date fromDate = df.parse("14.12.2012 17:00:12");
			Date toDate = df.parse("31.12.2012 10:30:45");

			List<Date> list = getUpdates(time, fromDate, toDate);
			for (Date date : list) {
				System.out.println(date);
			}
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
	}

	public static Date getBuyingDate(int playerId) {
		List<Date> dates = getBuyingDates(playerId);
		if (!dates.isEmpty()) {
			return dates.get(0);
		}
		return null;
	}

	public static List<Date> getBuyingDates(int playerId) {
		List<Date> list = new ArrayList<Date>();

		String query = "SELECT * FROM transfer WHERE playerid=" + playerId + " AND buyerid="
				+ HOVerwaltung.instance().getModel().getBasics().getTeamId();
		ResultSet rs = DBManager.instance().getAdapter().executeQuery(query);
		try {
			while (rs.next()) {
				list.add(new Date(rs.getTimestamp("date").getTime()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	// EconomyDate Germany 2012-12-15 00:00:01
	public static WeekDayTime getEconomyDate() {
		return new WeekDayTime(Calendar.SATURDAY, 0, 0, 1);
	}

	/**
	 * Gets a range of birthdays for a player.
	 * 
	 * @param playerId
	 *            the id of the player
	 * @param from
	 *            the birthday to start with (including, minimum 17).
	 * @param to
	 *            the birthday to end with (including)
	 * @return a list of birthdays starting at 'from', ending at 'to'
	 */
	public static List<Birthday> getBirthdays(int playerId, int from, int to) {
		List<Birthday> list = new ArrayList<Birthday>();

		Date birthday17 = get17thBirthday(playerId);

		if (from < 17) {
			from = 17;
		}

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(birthday17);

		int offset = from - 17 - 1;
		cal.add(GregorianCalendar.DAY_OF_MONTH, offset * 112);

		for (int i = from; i <= to; i++) {
			cal.add(GregorianCalendar.DAY_OF_MONTH, 112);
			list.add(new Birthday(i, cal.getTime()));
		}

		return list;
	}
	
	public static Date get17thBirthday(int playerId) {
		String query = "SELECT LIMIT 0 1 AGE, AGEDAYS, DATUM FROM player WHERE spielerid="
				+ playerId;
		ResultSet rs = DBManager.instance().getAdapter().executeQuery(query);
		try {
			if (rs.next()) {
				Date rsDate = new Date(rs.getTimestamp("DATUM").getTime());
				int yearsDiffTo17 = rs.getInt("AGE") - 17;
				int daysSince17 = yearsDiffTo17 * 112 + rs.getInt("AGEDAYS");

				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(rsDate);
				cal.add(GregorianCalendar.DAY_OF_MONTH, -daysSince17);
				return cal.getTime();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static int getWagesSum(int playerId, Date dateFrom, Date dateTo) {
		List<Date> updates = Calc.getUpdates(Calc.getEconomyDate(), dateFrom, dateTo);
		List<Wage> wagesByAge = Wage.getWagesByAge(playerId);

		Map<Integer, Wage> ageWageMap = new HashMap<Integer, Wage>();
		for (Wage wage : wagesByAge) {
			ageWageMap.put(Integer.valueOf(wage.getAge()), wage);
		}

		Date birthDay17 = Calc.get17thBirthday(playerId);
		int sum = 0;
		for (Date date : updates) {
			int ageAt = Calc.getAgeAt(birthDay17, date);
			Integer key = Integer.valueOf(ageAt);
			sum += ageWageMap.get(Integer.valueOf(ageAt)).getWage();
		}
		return sum;
	}
	
	/**
	 * 
	 * @param age
	 *            total age in days (note that a HT-year has 112 days)
	 * @param buyingDate
	 * @param sellingDate
	 * @param economyUpdate
	 */
	private static Map<Integer, List<Date>> getWages(int age, Date buyingDate, Date sellingDate,
			WeekDayTime economyUpdate) {
		Map<Integer, List<Date>> wages = new HashMap<Integer, List<Date>>();
		int totalDaysInPeriod = getDaysBetween(sellingDate, buyingDate);
		System.out.println("totalDaysInPeriod: " + totalDaysInPeriod);

		int years = age / 112;
		int days = age % 112;
		System.out.println(years + ", " + days);

		int daysToNextBirthday = 112 - days;
		System.out.println("daysToNextBirthday " + daysToNextBirthday);

		Date firstDate = nextUpdateAfter(economyUpdate, buyingDate);
		if (totalDaysInPeriod < daysToNextBirthday) {
			System.out.println("nextUpdateAfter " + buyingDate + " - "
					+ nextUpdateAfter(economyUpdate, buyingDate));
		}

		return wages;
	}

	private static int getUpdates(Date buyingDate, Date lastEconomyDateInPeriod) {
		long millis = lastEconomyDateInPeriod.getTime() - buyingDate.getTime();
		long updates = millis / 1000 / 60 / 60 / 24 / 7;
		updates += 1; // add one for lastEconomyDateInPeriod itself
		return (int) updates;
	}

	private static Date nextUpdateAfter(WeekDayTime time, Date date) {
		// Bsp.: Sa. 01:00
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);

		// set the weekday to the day when the updates happens
		cal.set(Calendar.DAY_OF_WEEK, time.getDay());
		cal.set(Calendar.HOUR_OF_DAY, time.getHour());
		cal.set(Calendar.MINUTE, time.getMinute());
		cal.set(Calendar.SECOND, time.getSecond());
		cal.set(Calendar.MILLISECOND, 0);

		// if the day where the update happens is before the given date,
		// take the update next week
		if (date.getTime() > cal.getTimeInMillis()) {
			cal.add(Calendar.DAY_OF_WEEK, 7);
		}

		return cal.getTime();
	}

	private static Date lastUpdateBefore(int dayOfWeek, WeekDayTime time, Date date) {
		// Bsp.: Sa. 01:00
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);

		// set the weekday to the day when the updates happens
		cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		cal.set(Calendar.HOUR_OF_DAY, time.getHour());
		cal.set(Calendar.MINUTE, time.getMinute());
		cal.set(Calendar.SECOND, time.getSecond());
		cal.set(Calendar.MILLISECOND, 0);

		// if the day where the update happens is past the given date,
		// take the update before (one week back)
		if (date.getTime() < cal.getTimeInMillis()) {
			cal.add(Calendar.DAY_OF_WEEK, -7);
		}

		return cal.getTime();
	}

	/**
	 * Gets the difference between <code>date1</code> and <code>date2</code> in
	 * days (date1 - date2).
	 * <p/>
	 * Note that the the time is not taken into account, so this method will
	 * return <code>3</code> for <code>date1</code> <i>May 21 00:00:00 2009</i>
	 * and <code>date2</code> <i>May 18 23:59:59 2009</i> although there are 48
	 * hours and 1 second between the two dates.
	 * <p/>
	 * If <code>date1</code> is before </code>date2</code> a negative number of
	 * days will be returned.
	 * 
	 * @param date1
	 *            the first date.
	 * @param date2
	 *            the second date.
	 * @return the difference between date1 and date2 in days (date1 - date2).
	 * @throws NullPointerException
	 *             if one (or both) of the given dates is <code>null</code>.
	 */
	public static int getDaysBetween(Date date1, Date date2) {
		// comparing the timestamp is fast and avoids unnecessary object
		// creation
		// if both dates are the same
		if (date1.getTime() == date2.getTime()) {
			return 0;
		}

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date1);
		setMinTime(cal);
		BigDecimal date1Millis = new BigDecimal(cal.getTimeInMillis());

		cal.setTime(date2);
		setMinTime(cal);
		BigDecimal date2Millis = new BigDecimal(cal.getTimeInMillis());

		return date1Millis.subtract(date2Millis)
				.divide(new BigDecimal(86400000), BigDecimal.ROUND_HALF_EVEN).intValue();
	}

	/**
	 * Sets the calendar's time values to their minimum. This will be the very
	 * first millisecond of a day (00:00:00.000).
	 * 
	 * @param cal
	 *            The calendar to zero the time.
	 */
	private static void setMinTime(Calendar cal) {
		cal.set(GregorianCalendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
		cal.set(GregorianCalendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
		cal.set(GregorianCalendar.SECOND, cal.getMinimum(Calendar.SECOND));
		cal.set(GregorianCalendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
	}

	public static int getAgeAt(Date date, int playerId) {
		String query = "SELECT LIMIT 0 1 AGE, AGEDAYS, DATUM FROM player WHERE spielerid="
				+ playerId;
		ResultSet rs = DBManager.instance().getAdapter().executeQuery(query);
		try {
			if (rs.next()) {
				int age = rs.getInt("AGE") * 112 + rs.getInt("AGEDAYS");
				Date rsDate = new Date(rs.getTimestamp("DATUM").getTime());
				return Calc.getDaysBetween(date, rsDate) + age;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public static int getAgeAt(Date birthDay17, Date date) {
		if (date.before(birthDay17)) {
			throw new IllegalArgumentException();
		}
		int days = getDaysBetween(date, birthDay17);

		return 17 + days / 112;
	}

	public static List<Date> getUpdates(WeekDayTime updateTime, Date from, Date to) {
		List<Date> list = new ArrayList<Date>();

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(from);
		cal.set(Calendar.DAY_OF_WEEK, updateTime.getDay());
		cal.set(Calendar.HOUR_OF_DAY, updateTime.getHour());
		cal.set(Calendar.MINUTE, updateTime.getMinute());
		cal.set(Calendar.SECOND, updateTime.getSecond());
		cal.set(Calendar.MILLISECOND, 0);

		if (cal.getTime().before(from)) {
			cal.add(Calendar.DAY_OF_YEAR, 7);
		}

		Date date = null;
		while ((date = cal.getTime()).before(to)) {
			list.add(date);
			cal.add(Calendar.DAY_OF_YEAR, 7);
		}

		return list;
	}
}
