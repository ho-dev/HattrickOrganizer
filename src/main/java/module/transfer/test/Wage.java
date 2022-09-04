package module.transfer.test;

import core.db.DBManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Wage {

	private int age;
	private int wage;

	public Wage() {
	}

	public Wage(int age, int wage) {
		this.age = age;
		this.wage = wage;
	}

	/**
	 * Gets the age (year).
	 * 
	 * @return
	 */
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getWage() {
		return wage;
	}

	public void setWage(int wage) {
		this.wage = wage;
	}

	static private DBManager.PreparedStatementBuilder playerStatementBuilder = new DBManager.PreparedStatementBuilder(DBManager.instance().getAdapter(),
			"SELECT age, gehalt FROM Player WHERE spielerid=? GROUP BY age, gehalt");
	/**
	 * Gets a list of Wages for player which have to be payed for his different
	 * ages (the wages to be payed at birthdays in the past).
	 * 
	 * @param playerID
	 * @return
	 */
	public static List<Wage> getWagesByAge(int playerID) {
		List<Wage> wages = new ArrayList<Wage>();
		ResultSet rs = DBManager.instance().getAdapter().executePreparedQuery(playerStatementBuilder.getStatement(), playerID);
		try {
			while (rs.next()) {
				wages.add(new Wage(rs.getInt("age"), rs.getInt("gehalt") / 10));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return wages;
	}

}
