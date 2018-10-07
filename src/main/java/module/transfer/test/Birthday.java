package module.transfer.test;

import java.util.Date;

public class Birthday {

	private int age;
	private Date date;

	public Birthday(int age, Date date) {
		this.age = age;
		this.date = new Date(date.getTime());
	}

	public int getAge() {
		return age;
	}

	public Date getDate() {
		return new Date(this.date.getTime());
	}

}
