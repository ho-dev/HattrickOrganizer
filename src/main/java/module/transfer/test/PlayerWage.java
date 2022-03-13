package module.transfer.test;

import core.util.HODateTime;

import java.util.Date;

public class PlayerWage {

	private int age;
	private int wage;
	private HODateTime financialUpdateDate;

	public int getWage() {
		return wage;
	}
	public void setWage(int wage) {
		this.wage = wage;
	}
	public HODateTime getFinancialUpdateDate() {
		return financialUpdateDate;
	}

	public void setFinancialUpdateDate(HODateTime financialUpdateDate) {
		this.financialUpdateDate = financialUpdateDate;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
