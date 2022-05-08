package module.tsforecast;

import core.model.HOVerwaltung;
import core.model.enums.MatchType;
import core.model.misc.Basics;
import core.model.series.Liga;
import core.util.HODateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

/*
 * HistoryCurve.java
 *
 * Created on 19.March 2006, 11:04
 *
 *Version 0.2
 *history :
 *19.03.06  Version 0.1
 *28.08.06  Version 0.11 rebuilt
 *18.02.07  Version 0.2  optomized SQL-statements
 *
 *
 */

/**
 * 
 * @author michael.roux
 */

// Referenced classes of package hoplugins.tsforecast:
// Curve, ErrorLog

public class HistoryCurve extends Curve {

	static final double m_dMaxSpirit = 10.2D;

	public HistoryCurve() throws SQLException {
		super();

		readSpiritHistory();
		readPastMatches();
		// BAUSTELLE XLint
		Collections.sort(m_clPoints);
		fillupSpirit();
	}

	// -- private
	// ------------------------------------------------------------------------

	private void readSpiritHistory() throws SQLException {
		Basics ibasics = HOVerwaltung.instance().getModel().getBasics();
		var start = ibasics.getDatum().minus(WEEKS_BACK*7, ChronoUnit.DAYS).toDbTimestamp();
		ResultSet resultset = m_clJDBC
				.executeQuery("select DATUM, ISTIMMUNG from HRF, TEAM "
						+ "where HRF.HRF_ID = TEAM.HRF_ID " + "and DATUM <= '"
						+ ibasics.getDatum().toDbTimestamp() + "' and DATUM > '" + start + "'"
						+ "order by DATUM");
		assert resultset != null;
		for (boolean flag = resultset.first(); flag; flag = resultset.next()) {
			double dSpirit = resultset.getInt("ISTIMMUNG") + 0.5D;
			if (dSpirit > m_dMaxSpirit)
				dSpirit = m_dMaxSpirit;
			m_clPoints.add(new Point(HODateTime.fromDbTimestamp(resultset.getTimestamp("DATUM")), dSpirit));
		}
	}

	private void readPastMatches() throws SQLException {
		Basics ibasics = HOVerwaltung.instance().getModel().getBasics();
		Liga iliga = HOVerwaltung.instance().getModel().getLeague();

		Curve.Point pLastLeagueMatch = null;
		var start = ibasics.getDatum().minus(WEEKS_BACK*7, ChronoUnit.DAYS).toDbTimestamp();

		// Table PAARUNG is required for SPIELTAG but does only include League
		// matches
		// Table MATCHESKURZINFO includes all matches but not SPIELTAG
		// Table MATCHDETAILS includes EINSTELLUNG
		if (iliga != null) {
			ResultSet resultset = m_clJDBC
					.executeQuery("SELECT * FROM (select MATCHESKURZINFO.MATCHDATE as SORTDATE, -1 AS SPIELTAG, MATCHESKURZINFO.MATCHTYP, "
							+ "MATCHDETAILS.GASTEINSTELLUNG, MATCHDETAILS.HEIMEINSTELLUNG, MATCHDETAILS.HEIMID "
							+ "from MATCHESKURZINFO, MATCHDETAILS "
							+ "where (MATCHDETAILS.HEIMID="
							+ ibasics.getTeamId()
							+ " OR MATCHDETAILS.GASTID="
							+ ibasics.getTeamId()
							+ ") "
							+ "and MATCHESKURZINFO.MATCHID=MATCHDETAILS.MATCHID "
							+ "and MATCHESKURZINFO.MATCHDATE < '"
							+ ibasics.getDatum().toDbTimestamp()
							+ "' and MATCHESKURZINFO.MATCHDATE > '"
							+ start
							+ "' "
							+ "and MATCHTYP <> "
							+ MatchType.LEAGUE.getId()
							+ " union "
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
							+ ibasics.getDatum().toDbTimestamp()
							+ "' and PAARUNG.DATUM > '"
							+ start
							+ "') "
							+ "order by SORTDATE");
			/*
			 * select MATCHESKURZINFO.MATCHDATE as SORTDATE, -1 as SPIELTAG,
			 * MATCHESKURZINFO.MATCHTYP, MATCHDETAILS.GASTEINSTELLUNG,
			 * MATCHDETAILS.HEIMEINSTELLUNG, MATCHDETAILS.HEIMID from
			 * MATCHESKURZINFO, MATCHDETAILS where (MATCHDETAILS.HEIMID=132932
			 * OR MATCHDETAILS.GASTID=132932) and
			 * MATCHESKURZINFO.MATCHID=MATCHDETAILS.MATCHID and SORTDATE <
			 * '2006-09-01' and SORTDATE > '2006-01-01' and MATCHTYP <> 1 union
			 * select PAARUNG.DATUM as SORTDATE, PAARUNG.SPIELTAG, 1 as
			 * MATCHTYP, MATCHDETAILS.GASTEINSTELLUNG,
			 * MATCHDETAILS.HEIMEINSTELLUNG, MATCHDETAILS.HEIMID from PAARUNG,
			 * MATCHDETAILS where (MATCHDETAILS.HEIMID=132932 OR
			 * MATCHDETAILS.GASTID=132932) and
			 * PAARUNG.MATCHID=MATCHDETAILS.MATCHID and SORTDATE < '2006-09-01'
			 * and SORTDATE > '2006-01-01' order by SORTDATE
			 */
			int i = 0;
			if (resultset != null) {
				for (boolean flag = resultset.first(); flag; flag = resultset
						.next()) {
					if (resultset.getInt("SPIELTAG") > 0) {
						i = resultset.getInt("SPIELTAG");
					}
					Curve.Point pNextLeagueMatch;
					if (ibasics.getTeamId() == resultset.getInt("HEIMID")) {
						pNextLeagueMatch = new Point(
								HODateTime.fromDbTimestamp(resultset.getTimestamp("SORTDATE")),
								resultset.getInt("HEIMEINSTELLUNG"), i,
								MatchType.getById(resultset.getInt("MATCHTYP")));
					} else {
						pNextLeagueMatch = new Point(
								HODateTime.fromDbTimestamp(resultset.getTimestamp("SORTDATE")),
								resultset.getInt("GASTEINSTELLUNG"), i,
								MatchType.getById(resultset.getInt("MATCHTYP")));
					}
					m_clPoints.add(pNextLeagueMatch);

					// correction of matchdays at end of season for non league
					// matches
					if (pNextLeagueMatch.m_mtMatchType != MatchType.LEAGUE) {
						if (pLastLeagueMatch != null) { // first match
							if (getDiffDays(pLastLeagueMatch, pNextLeagueMatch) <= 7) {// matchday
																						// stays
																						// the
																						// same
								continue;
							}
							pNextLeagueMatch.m_iMatchDay++;
							if (getDiffDays(pLastLeagueMatch, pNextLeagueMatch) > 14) {
								pNextLeagueMatch.m_iMatchDay++;
							}
							continue;
						}
						if (pNextLeagueMatch.m_iMatchDay == 0) {
							pNextLeagueMatch.m_iMatchDay = 16;
						}
					} else {
						pLastLeagueMatch = pNextLeagueMatch;
					}
				}
			}
		}
	}

	private void fillupSpirit() {
		if (!m_clPoints.isEmpty()) {
			double dPreviousSpirit = TEAM_SPIRIT_UNKNOWN;
			for (int i = 0; i < m_clPoints.size(); i++) {
				Curve.Point point = m_clPoints.get(i);
				if (point.m_dSpirit == TEAM_SPIRIT_UNKNOWN) {
					if (dPreviousSpirit == TEAM_SPIRIT_UNKNOWN) {
						point.m_dSpirit = nextSpirit(i);
					} else {
						point.m_dSpirit = dPreviousSpirit;
					}
				} else {
					dPreviousSpirit = point.m_dSpirit;
				}
			}
		}
	}

	private double nextSpirit(int i) {
		for (int j = i; j < m_clPoints.size(); j++) {
			Curve.Point point = m_clPoints.get(j);
			if (point.m_dSpirit != TEAM_SPIRIT_UNKNOWN)
				return point.m_dSpirit;
		}
		return TEAM_SPIRIT_UNKNOWN;
	}
}