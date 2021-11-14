package module.transfer.test;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class HTWeek {

	private int season;
	private int week;

	public int getSeason() {
		return season;
	}

	public void setSeason(int season) {
		this.season = season;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	private static Calendar origin;
	private static Calendar getOrigin()
	{
		if ( origin == null ) {
			origin = new GregorianCalendar();
			origin.set(1997, Calendar.SEPTEMBER, 22, 0, 0, 0);
			origin.set(Calendar.MILLISECOND, 0);
		}
		return origin;
	}

	public Date toDate()
	{
		Calendar orig = getOrigin();
		orig.add(Calendar.DATE, (this.season-1)*112 + (this.week-1)*7);
		return orig.getTime();
	}

	public HTWeek(Date date) {
		Calendar origin = getOrigin();
		long msDiff = date.getTime() - origin.getTimeInMillis();
		long dayDiff = msDiff / 1000 / 60 / 60 / 24;
		season = (int) Math.floor(dayDiff / (16 * 7)) + 1;
		week = (int) Math.floor((dayDiff % (16 * 7)) / 7) + 1;
	}

	public HTWeek(Instant time){
		this(Date.from(time));
	}

	public long weekSinceOrigin(){
		return ((long)(season-1)*16+week-1);
	}

}
