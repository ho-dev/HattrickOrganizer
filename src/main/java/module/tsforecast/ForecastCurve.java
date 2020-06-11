package module.tsforecast;

/*
 * ForecastCurve.java
 *
 * Created on 19.March 2006, 11:04
 *
 *Version 0.5
 *history :
 *19.03.06  Version 0.1
 *26.08.06  Version 0.11 rebuilt
 *04.09.06  Version 0.2  UpdateFactor becomes TrainerLS
 *09.02.07  Version 0.3  replaced readFutureMatches with generated matches schedule instead of database driven
 *21.02.07  Version 0.4  added tooltip to points
 *01.10.09  Version 0.5  improve forcast formula
 */

/**
 *
 * @author  michael.roux
 */

import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;
import core.model.match.MatchType;
import core.model.misc.Basics;
import core.model.series.Liga;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Properties;

// Referenced classes of package hoplugins.tsforecast:
//            Curve

abstract class ForecastCurve extends Curve {

	public static final boolean FUTURE = true;
	public static final boolean PAST = false;

	protected double m_dGeneralSpirit = 4.5D;
	protected double m_dTrainerLeadership = 0.6D;
	protected int m_iNoWeeksForecast = 4;

	public ForecastCurve(boolean future) throws SQLException {
		super();
		if (future)
			readFutureMatches();
		else
			readPastMatches();
		Collections.sort(m_clPoints);
	}

	public void setSpirit(int pos, double spirit) throws Exception {
		if (pos >= 0 && pos < m_clPoints.size()) {
			(m_clPoints.get(pos)).m_dSpirit = spirit;
			forecast(pos);
		}
	}

	public void setAttitude(int pos, int a) throws Exception {
		if (pos >= 0 && pos < m_clPoints.size()) {
			(m_clPoints.get(pos)).m_iAttitude = a;
			forecast(pos);
		}
	}

	public void setAttitudes(Properties properties) {
		for (int i = 0; i < m_clPoints.size(); i++) {
			if ((m_clPoints.get(i)).m_mtMatchType != UNKNOWN_MATCH) {
				String str = "Match_"
						+ DateFormat.getDateInstance(DateFormat.SHORT).format(
								(m_clPoints.get(i)).m_dDate);
				String value = properties.getProperty(str);
				if (value != null)
					(m_clPoints.get(i)).m_iAttitude = (new Integer(value))
							.intValue();
			}
		}
	}

	public double getGeneralSpirit() {
		return m_dGeneralSpirit;
	}

	public void setGeneralSpirit(double d) throws Exception {
		m_dGeneralSpirit = d;
		forecast(0);
	}

	public void setStartPoint(Curve.Point point) {
		super.addPoint(0, point);
		// delete all points that are before startpoint
		for (int i = 1; i < m_clPoints.size(); i++) {
			if ((m_clPoints.get(i)).m_dDate.before(point.m_dDate)) {
				m_clPoints.remove(i--);
			} else {
				break;
			}
		}
	}

	@Override
	public void addPoint(int i, Curve.Point point) {
		super.addPoint(i, point);
	}

	public void forecast(int pos) throws Exception {
		if (m_clPoints.size() > pos) {
			Curve.Point point1 = m_clPoints.get(pos);
			Curve.Point point2 = null;

			for (int i = pos + 1; i < m_clPoints.size(); i++) {
				point2 = m_clPoints.get(i);
				if (i == 1) { // Spirit stays the same from point0 to point1 as
								// point0 is the start point and no update point
					point2.m_dSpirit = point1.m_dSpirit;
				} else {
					if (point2.m_iPointType == RESET_PT)
						point2.m_dSpirit = TEAM_SPIRIT_RESET;
					else if (point1.m_iAttitude == IMatchDetails.EINSTELLUNG_PIC)
						point2.m_dSpirit = point1.m_dSpirit + point1.m_dSpirit
								/ 3D;
					else if (point1.m_iAttitude == IMatchDetails.EINSTELLUNG_MOTS)
						point2.m_dSpirit = point1.m_dSpirit / 2D;
					else {
						point2.m_dSpirit = forecastUpdate(point1, point2);
					}
					if (point2.m_dSpirit > 10D)
						point2.m_dSpirit = 10D;
					else if (point2.m_dSpirit < 0.0D)
						point2.m_dSpirit = 0.0D;
				}
				point1 = point2;
			}
		}
	}

	// -- protected
	// ----------------------------------------------------------------------

	protected abstract double forecastUpdate(Curve.Point point1,
			Curve.Point point2) throws Exception;

	// -- private
	// ------------------------------------------------------------------------

	private void readFutureMatches() throws SQLException {
		Basics ibasics = HOVerwaltung.instance().getModel().getBasics();
		Liga iliga = HOVerwaltung.instance().getModel().getLeague();

		// MASTERS_MATCH 7 ???

		// find last match (League, Cup or Qualification)
		if (ibasics != null && iliga != null) {
			ResultSet resultset = m_clJDBC
					.executeQuery("select MATCHDATE, MATCHTYP from MATCHESKURZINFO "
							+ "where (HEIMID="
							+ ibasics.getTeamId()
							+ " or GASTID="
							+ ibasics.getTeamId()
							+ ") "
							+ "  and MATCHDATE = (select MAX(MATCHDATE) "
							+ "    from MATCHESKURZINFO "
							+ "    where (HEIMID="
							+ ibasics.getTeamId()
							+ " or GASTID="
							+ ibasics.getTeamId()
							+ ") "
							+ "      and (MATCHTYP="
							+ MatchType.LEAGUE.getId()
							+ "        or MATCHTYP="
							+ MatchType.QUALIFICATION.getId()
							+ "        or MATCHTYP="
							+ MatchType.CUP.getId()
							+ ")" + "      and STATUS=1)");
			/*
			 * select MATCHDATE, MATCHTYP from MATCHESKURZINFO where
			 * (HEIMID=132932 OR GASTID=132932) and MATCHDATE = select
			 * MAX(MATCHDATE) from MATCHESKURZINFO where (HEIMID=132932 OR
			 * GASTID=132932) and (MATCHTYP = 1 OR MATCHTYP= 2 OR MATCHTYP = 3)
			 * and STATUS = 1
			 */

			if (resultset != null && resultset.first()) {
				short start = 0;
				Curve.Point point = null;

				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime(resultset.getTimestamp("MATCHDATE"));
				MatchType lastMatchType = MatchType.getById(resultset
						.getInt("MATCHTYP"));
				point = new Curve.Point(resultset.getTimestamp("MATCHDATE"),
						IMatchDetails.EINSTELLUNG_NORMAL,
						ibasics.getSpieltag(), lastMatchType);
				m_clPoints.add(point);
				addUpdatePoints(point);
				if (lastMatchType == MatchType.LEAGUE) {
					start = -1;
				}

				// 14 Ligaspiele
				// Relegation am 15. Spieltag
				// Pokalrunde 1 ist Die vor 1. Ligaspiel
				// 16 Pokalrunden
				// P0 L1 P1 L2 P2 L3 P3 L4 P4 L5 P5 L6 P6 L7 P7 L8 P8 L9 P9 L0
				// P0 L1 P1 L2 P2 L3 P3 L4 P4 Rel P5 Reset P0
				// Masters ?

				for (short s = start; s < m_iNoWeeksForecast; s++) { // 16 weeks
																		// ahead

					if (lastMatchType == MatchType.CUP) { // add League Match
						// SUNDAY = 0
						calendar.add(Calendar.DATE, Calendar.SATURDAY
								- Calendar.TUESDAY - 1);
						switch (ibasics.getSpieltag() + s) {
						case 16:
							point = new Curve.Point(calendar.getTime(),
									TEAM_SPIRIT_RESET, RESET_PT);
							break;
						case 15:
							point = new Curve.Point(calendar.getTime(),
									IMatchDetails.EINSTELLUNG_NORMAL,
									ibasics.getSpieltag() + s,
									MatchType.QUALIFICATION);
							point.m_strTooltip = new String(HOVerwaltung
									.instance().getLanguageString(
											"ls.match.matchtype.qualification"));
							break;
						default:
							point = new Curve.Point(calendar.getTime(),
									IMatchDetails.EINSTELLUNG_NORMAL,
									ibasics.getSpieltag() + s, MatchType.LEAGUE);
							point.m_strTooltip = new String(
									(ibasics.getSpieltag() + s)
											+ ". "
											+ HOVerwaltung.instance()
													.getLanguageString(
															"ls.match.matchtype.league"));
							break;
						}
						m_clPoints.add(point);
						addUpdatePoints(point);
					}
					// add Cup Match
					calendar.add(Calendar.DATE, Calendar.TUESDAY + 1);
					point = new Curve.Point(calendar.getTime(),
							IMatchDetails.EINSTELLUNG_NORMAL,
							ibasics.getSpieltag() + s, MatchType.CUP);
					point.m_strTooltip = new String((ibasics.getSpieltag() + s)
							+ ". "
							+ HOVerwaltung.instance().getLanguageString(
									"ls.match.matchtype.cup"));
					m_clPoints.add(point);
					addUpdatePoints(point);
					lastMatchType = MatchType.CUP;

				}
			}
			// Daten mit Datenbankdaten anreichern
		}
	}

	private void readPastMatches() throws SQLException {
		Basics ibasics = HOVerwaltung.instance().getModel().getBasics();
		Liga iliga = HOVerwaltung.instance().getModel().getLeague();

		GregorianCalendar gregoriancalendar = new GregorianCalendar();
		gregoriancalendar.setTime(ibasics.getDatum());
		gregoriancalendar.add(Calendar.WEEK_OF_YEAR, -WEEKS_BACK);
		Timestamp start = new Timestamp(gregoriancalendar.getTimeInMillis());

		if (ibasics != null && iliga != null) {
			// PAARUNG contains all Leaguematches, but no other
			// MATCHESKURZINFO contains all other matches but no matchday
			ResultSet resultset = m_clJDBC
					.executeQuery("select * from (select MATCHESKURZINFO.MATCHDATE as SORTDATE, -1 as SPIELTAG, MATCHESKURZINFO.MATCHTYP, "
							+ "MATCHDETAILS.GASTEINSTELLUNG, MATCHDETAILS.HEIMEINSTELLUNG, MATCHDETAILS.HEIMID "
							+ "from MATCHESKURZINFO, MATCHDETAILS "
							+ "where (MATCHDETAILS.HEIMID="
							+ ibasics.getTeamId()
							+ " OR MATCHDETAILS.GASTID="
							+ ibasics.getTeamId()
							+ ") "
							+ "and MATCHESKURZINFO.MATCHTYP="
							+ MatchType.CUP.getId()
							+ "and MATCHESKURZINFO.MATCHID=MATCHDETAILS.MATCHID "
							+ "and MATCHESKURZINFO.MATCHDATE < '"
							+ ibasics.getDatum()
							+ "' and MATCHESKURZINFO.MATCHDATE > '"
							+ start
							+ "' "
							+ "union "
							+ "select PAARUNG.DATUM as SORTDATE, PAARUNG.SPIELTAG, "
							+ MatchType.LEAGUE.getId()
							+ " as MATCHTYP, "
							+ "MATCHDETAILS.GASTEINSTELLUNG, MATCHDETAILS.HEIMEINSTELLUNG, MATCHDETAILS.HEIMID "
							+ "from PAARUNG, MATCHDETAILS "
							+ "where (MATCHDETAILS.HEIMID="
							+ ibasics.getTeamId()
							+ " OR MATCHDETAILS.GASTID="
							+ ibasics.getTeamId()
							+ ") "
							+ "and PAARUNG.MATCHID=MATCHDETAILS.MATCHID "
							+ "and PAARUNG.DATUM < '"
							+ ibasics.getDatum()
							+ "' "
							+ "and PAARUNG.DATUM > '"
							+ start
							+ "') "
							+ "order by SORTDATE");
			/*
			 * select MATCHESKURZINFO.MATCHDATE as SORTDATE, -1 as SPIELTAG,
			 * MATCHESKURZINFO.MATCHTYP, MATCHDETAILS.GASTEINSTELLUNG,
			 * MATCHDETAILS.HEIMEINSTELLUNG, MATCHDETAILS.HEIMID from
			 * MATCHESKURZINFO, MATCHDETAILS where (MATCHDETAILS.HEIMID=132932
			 * OR MATCHDETAILS.GASTID=132932) and MATCHESKURZINFO.MATCHTYP=3 and
			 * MATCHESKURZINFO.MATCHID=MATCHDETAILS.MATCHID and SORTDATE <
			 * '2006-09-01' and SORTDATE > '2006-01-01' union select
			 * PAARUNG.DATUM as SORTDATE, PAARUNG.SPIELTAG, 1 as MATCHTYP,
			 * MATCHDETAILS.GASTEINSTELLUNG, MATCHDETAILS.HEIMEINSTELLUNG,
			 * MATCHDETAILS.HEIMID from PAARUNG, MATCHDETAILS where
			 * (MATCHDETAILS.HEIMID=132932 OR MATCHDETAILS.GASTID=132932) and
			 * PAARUNG.MATCHID=MATCHDETAILS.MATCHID and SORTDATE < '2006-09-01'
			 * and SORTDATE > '2006-01-01' order by SORTDATE
			 */
			int iMatchDay = 0;
			java.util.Date maxDate = null;

			Curve.Point point;
			if (resultset != null) {
				for (boolean flag = resultset != null && resultset.first(); flag; flag = resultset
						.next()) {
					if (resultset.getInt("SPIELTAG") > 0) {
						iMatchDay = resultset.getInt("SPIELTAG");
					}
					if (ibasics.getTeamId() == resultset.getInt("HEIMID")) {
						point = new Curve.Point(
								resultset.getTimestamp("SORTDATE"),
								resultset.getInt("HEIMEINSTELLUNG"), iMatchDay,
								MatchType.getById(resultset.getInt("MATCHTYP")));
					} else {
						point = new Curve.Point(
								resultset.getTimestamp("SORTDATE"),
								resultset.getInt("GASTEINSTELLUNG"), iMatchDay,
								MatchType.getById(resultset.getInt("MATCHTYP")));
					}
					m_clPoints.add(point);
					maxDate = point.m_dDate;
				}
			}
			// Add update points
			// this function reads only league, cup and qualification matches,
			// therefor has to add all Updatepoints
			int iEnd = m_clPoints.size();
			for (int i = 0; i < iEnd; i++) {
				point = m_clPoints.get(i);
				if (point.m_mtMatchType == MatchType.LEAGUE) {
					addUpdatePoints(point, true);
					if (point.m_iMatchDay == 14) {
						addEndOfSeasonPoints(point, maxDate);
					}
				}
			}
		}
	}

	private void addUpdatePoints(Curve.Point point) {
		addUpdatePoints(point, false);
	}

	private void addUpdatePoints(Curve.Point point, boolean bAllPoints) {
		GregorianCalendar gregoriancalendar = new GregorianCalendar();
		gregoriancalendar.setTime(point.m_dDate);

		// League game ?
		if (bAllPoints || point.m_mtMatchType == MatchType.LEAGUE
				|| point.m_mtMatchType == MatchType.QUALIFICATION
				|| point.m_iPointType == RESET_PT) {
			gregoriancalendar.add(Calendar.HOUR_OF_DAY, 21); // Sunday 15:00
			m_clPoints.add(new Curve.Point(gregoriancalendar.getTime(),
					TEAM_SPIRIT_UNKNOWN));
			gregoriancalendar.add(Calendar.HOUR_OF_DAY, 21); // Monday 12:00
			m_clPoints.add(new Curve.Point(gregoriancalendar.getTime(),
					TEAM_SPIRIT_UNKNOWN));
			gregoriancalendar.add(Calendar.HOUR_OF_DAY, 21); // Tuesday 09:00
			m_clPoints.add(new Curve.Point(gregoriancalendar.getTime(),
					TEAM_SPIRIT_UNKNOWN));
		}
		if (bAllPoints || point.m_mtMatchType == MatchType.CUP) {
			gregoriancalendar.add(Calendar.HOUR_OF_DAY, 21); // Wednesday 06:00
			m_clPoints.add(new Curve.Point(gregoriancalendar.getTime(),
					TEAM_SPIRIT_UNKNOWN));
			gregoriancalendar.add(Calendar.HOUR_OF_DAY, 21); // Thursday 03:00
			m_clPoints.add(new Curve.Point(gregoriancalendar.getTime(),
					TEAM_SPIRIT_UNKNOWN));
			gregoriancalendar.add(Calendar.HOUR_OF_DAY, 21); // Friday 00:00
			m_clPoints.add(new Curve.Point(gregoriancalendar.getTime(),
					TEAM_SPIRIT_UNKNOWN));
			gregoriancalendar.add(Calendar.HOUR_OF_DAY, 21); // Friday 21:00
			m_clPoints.add(new Curve.Point(gregoriancalendar.getTime(),
					TEAM_SPIRIT_UNKNOWN));
		}
	}

	private void addEndOfSeasonPoints(Curve.Point point, java.util.Date maxDate) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(point.m_dDate);

		// L14 p14 rel p15 RE p0 L1 (Kleinbuchstaben ist optional)
		cal.add(Calendar.DATE, 7); // Matchday 15

		if (maxDate != null && maxDate.after(cal.getTime())) {
			Curve.Point p = new Curve.Point(cal.getTime(),
					IMatchDetails.EINSTELLUNG_UNBEKANNT, 15,
					MatchType.QUALIFICATION);
			// don't add point only update points, real point come from database
			// if at all
			addUpdatePoints(p, true);

			cal.add(Calendar.DATE, 7); // Matchday 16

			if (maxDate.after(cal.getTime())) {
				p = new Curve.Point(cal.getTime(), TEAM_SPIRIT_RESET, RESET_PT);
				m_clPoints.add(p);
				addUpdatePoints(p, true);
			}
		}
	}

}