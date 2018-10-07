package module.transfer.test;

import java.util.Date;

public class PlayerWage {

	private int age;
	private int wage;
	private HTWeek week;
	private Date financialUpdateDate;

	public int getWage() {
		return wage;
	}

	public void setWage(int wage) {
		this.wage = wage;
	}

	public HTWeek getWeek() {
		return week;
	}

	public void setWeek(HTWeek week) {
		this.week = week;
	}

	public Date getFinancialUpdateDate() {
		return financialUpdateDate;
	}

	public void setFinancialUpdateDate(Date financialUpdateDate) {
		this.financialUpdateDate = financialUpdateDate;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
