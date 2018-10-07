package module.transfer.test;


public class WeekDayTime {

	/**
	 * Day like defined in the Calendar class (e.g. Calendar.SATURDAY)
	 */
	private int day;
	private int hour;
	private int minute;
	private int second;

	public WeekDayTime() {
	}

	public WeekDayTime(int day) {
		setDay(day);
	}

	public WeekDayTime(int day, int hour) {
		this(day);
		setHour(hour);
	}

	public WeekDayTime(int day, int hour, int minute) {
		this(day, hour);
		setMinute(minute);
	}
	
	public WeekDayTime(int day, int hour, int minute, int second) {
		this(day, hour, minute);
		setSecond(second);
	}
	
	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		if (hour < 0 || hour > 23) {
			throw new IllegalArgumentException("Invalid value for hour: " + hour
					+ " (has to be a value between 0 and 23)");
		}
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		if (minute < 0 || minute > 59) {
			throw new IllegalArgumentException("Invalid value for minute: " + minute
					+ " (has to be a value between 0 and 59)");
		}
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		if (second < 0 || second > 59) {
			throw new IllegalArgumentException("Invalid value for second: " + second
					+ " (has to be a value between 0 and 59)");
		}
		this.second = second;
	}
}