package module.tsforecast;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.enums.MatchType;
import core.model.misc.Basics;
import core.model.series.Liga;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static core.model.match.IMatchDetails.EINSTELLUNG_NORMAL;
import static core.model.match.IMatchDetails.EINSTELLUNG_UNBEKANNT;

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

	public HistoryCurve(DBManager dbManager) throws SQLException {
		super(dbManager);

		readSpiritHistory();
		readPastMatches();
		// BAUSTELLE XLint
		Collections.sort(m_clPoints);
		fillupSpirit();
	}

	private static final String readSpiritHistorySql  = "select DATUM, ISTIMMUNG, TRAININGSINTENSITAET from HRF, TEAM where HRF.HRF_ID = TEAM.HRF_ID and DATUM <= ? and DATUM > ? order by DATUM";

	private void readSpiritHistory() throws SQLException {
		Basics ibasics = HOVerwaltung.instance().getModel().getBasics();
		var start = ibasics.getDatum().minus(WEEKS_BACK * 7, ChronoUnit.DAYS).toDbTimestamp();
		try (ResultSet rs = dbManager.getConnectionManager().executePreparedQuery(readSpiritHistorySql, ibasics.getDatum().toDbTimestamp(), start)) {
			if (rs != null) {
				while (rs.next()) {
					double dSpirit = rs.getInt("ISTIMMUNG") + 0.5D;
					if (dSpirit > m_dMaxSpirit)
						dSpirit = m_dMaxSpirit;

					double trainingIntensity = rs.getInt("TRAININGSINTENSITAET");
					m_clPoints.add(new Point(HODateTime.fromDbTimestamp(rs.getTimestamp("DATUM")), dSpirit, trainingIntensity));
				}
			}
		}
	}

	private void readPastMatches() {
        Basics ibasics = HOVerwaltung.instance().getModel().getBasics();
        Liga iliga = HOVerwaltung.instance().getModel().getLeague();

        Curve.Point pLastLeagueMatch = null;
        var start = ibasics.getDatum().minus(WEEKS_BACK * 7, ChronoUnit.DAYS);

        // Table PAARUNG is required for SPIELTAG but does only include League
        // matches
        // Table MATCHESKURZINFO includes all matches but not SPIELTAG
        // Table MATCHDETAILS includes EINSTELLUNG
        if (iliga != null) {
            // reverse order - loadOfficial loads descending order
            var matches = dbManager.loadOfficialMatchesBetween(ibasics.getTeamId(), start, ibasics.getDatum()).stream().sorted((i, j) -> j.getMatchSchedule().compareTo(i.getMatchSchedule())).toList();

            for (var match : matches) {
                var sortDate = match.getMatchSchedule();
                if (sortDate == null) continue; // Corrupt database record (MatchDate IS NOT NULL)
                var matchDay = sortDate.toHTWeek().week();
                var isHomeTeam = ibasics.getTeamId() == match.getHomeTeamID();
                var teamAttitude = EINSTELLUNG_NORMAL;
                var details = match.getMatchdetails();
                if (details != null) {
                    teamAttitude = (isHomeTeam ? details.getHomeEinstellung() : details.getGuestEinstellung());
                    if (teamAttitude == EINSTELLUNG_UNBEKANNT) {
                        // Unknown team attitude happens if the match details were not downloaded for this match
                        HOLogger.instance().warning(this.getClass(), "Missing details of match:  %s - %s from %s".formatted(match.getHomeTeamName(), match.getGuestTeamName(), match.getMatchSchedule().toLocalDateTime()));
                        teamAttitude = EINSTELLUNG_NORMAL;
                    }
                }
                Curve.Point pNextLeagueMatch = new Point(
                    sortDate,
                    teamAttitude,
                    matchDay,
                    match.getMatchType()
                );

                m_clPoints.add(pNextLeagueMatch);

                // correction of matchdays at end of season for non league
                // matches
                if (pNextLeagueMatch.m_mtMatchType != MatchType.LEAGUE) {
                    if (pLastLeagueMatch != null) { // first match
                        if (getDiffDays(pLastLeagueMatch, pNextLeagueMatch) <= 7) {
                            // matchday stays the same
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
