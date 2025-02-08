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

/*
 *
 * @author  michael.roux
 */

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.cup.CupLevel;
import core.model.enums.MatchType;
import core.model.match.IMatchDetails;
import core.model.match.MatchKurzInfo;
import core.model.misc.Basics;
import core.model.series.Liga;
import core.util.HODateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static core.model.StaffType.SPORTPSYCHOLOGIST;
import static java.lang.Math.*;

abstract class ForecastCurve extends Curve {

	boolean isNtTeam;
	double psychologyEffect;
	protected int m_iNoWeeksForecast = 4;

	public ForecastCurve(DBManager dbManager, boolean future) {
		super(dbManager);
		var basics = HOVerwaltung.instance().getModel().getBasics();
		if (basics != null) {
			isNtTeam = basics.isNationalTeam();
            HOVerwaltung.instance().getModel().getStaff().stream()
					.filter(i -> i.getStaffType() == SPORTPSYCHOLOGIST)
					.findAny()
					.ifPresent(teamPsychologist -> psychologyEffect = teamPsychologist.getLevel() / 10.0);
            if (future)
				readFutureMatches();
			else
				readPastMatches();
			Collections.sort(m_clPoints);
		}
	}

	public double getTargetSpirit(){
		var ret = 4.5 + psychologyEffect;
		if (isNtTeam){
			ret += 0.5;
		}
		return ret;
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

//	public void setGeneralSpirit(double d) throws Exception {
//		m_dGeneralSpirit = d;
//		forecast(0);
//	}

	public void setStartPoint(Curve.Point point) {
		super.addPoint(0, point);
		// delete all points that are before startpoint
		for (int i = 1; i < m_clPoints.size(); i++) {
			if ((m_clPoints.get(i)).m_dDate.isBefore(point.m_dDate)) {
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

	// TODO: Add impact of training intensity settings
	public void forecast(int pos) throws Exception {
		if (m_clPoints.size() > pos) {
			Curve.Point point1 = m_clPoints.get(pos);
			Curve.Point point2;

			for (int i = pos + 1; i < m_clPoints.size(); i++) {
				point2 = m_clPoints.get(i);
				if (i == 1) { // Spirit stays the same from point0 to point1 as
								// point0 is the start point and no update point
					point2.m_dSpirit = point1.m_dSpirit;
				} else {
					if (point2.m_iPointType == RESET_PT)
						point2.m_dSpirit = getTargetSpirit();
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

	/**
	 *  Calculate the team spirit after changing the training intensity
	 * 	// HT-Tools Hattrick Manager Assistant
	 * 	//Copyright 2014-2015 Ventouris Anastasios
	 * 	//Licensed under GPL v3
	 * 	//		TSafter = (TSbefore * Math.pow(1.2, Math.log(TIafter / TIbefore, 0.7))).toFixed(2);
	 * 	//
	 * 	//		if (TSafter > 10) {
	 * 	//			TSafter = 10;
	 * 	//		}
	 * @param spiritBefore				team spirit before changing the training intensity [0..10]
	 * @param trainingIntensityBefore	training intensity [0..100]%
	 * @param trainingIntensityAfter	training intensity [0..100]%
	 * @return double Team spirit value after intensity change
	 */
	protected  double calculateTeamSpiritBoost(double spiritBefore, double trainingIntensityBefore, double trainingIntensityAfter) {
		return min(10, spiritBefore * pow(1.2, log(trainingIntensityAfter/trainingIntensityBefore)/log(0.7)));
	}

	protected abstract double forecastUpdate(Curve.Point point1,
			Curve.Point point2) throws Exception;

	// TODO: Add training events (Intensity)
	private void readFutureMatches() {
		Basics ibasics = HOVerwaltung.instance().getModel().getBasics();
		if (ibasics == null) return;

		// MASTERS_MATCH 7 ???

		var teamId = ibasics.getTeamId();
		var matches = DBManager.instance().getMatchesKurzInfo(teamId, MatchKurzInfo.UPCOMING);
		if (matches.isEmpty()) return;
		var cupMatch = matches.stream().filter(i -> i.getMatchType() == MatchType.CUP && i.getCupLevel() == CupLevel.NATIONALorDIVISIONAL).findAny();
		if (cupMatch.isEmpty()) {
			// no upcoming cup match found (might be not yet loaded)
			var latestCupMatch = dbManager.getMatchesKurzInfo(
					"where (HEIMID=? or GASTID=?) and MATCHTYP=? and CUPLEVEL=? ORDER BY MATCHDATE desc LIMIT 1 ",
					teamId, teamId, MatchType.CUP.getId(), CupLevel.NATIONALorDIVISIONAL.getId());
			if (!latestCupMatch.isEmpty()) {
				var match = latestCupMatch.get(0);
				// check if it was won
				if (teamId == match.getHomeTeamID() && match.getHomeTeamGoals() > match.getGuestTeamGoals() ||
						teamId == match.getGuestTeamID() && match.getGuestTeamGoals() > match.getHomeTeamGoals()) {
					cupMatch = java.util.Optional.of(match);
				}
			}
		}

		var toDate = ibasics.getDatum().plus(7 * m_iNoWeeksForecast, ChronoUnit.DAYS);
		var trainingIntensityChangeDate = HOVerwaltung.instance().getModel().getXtraDaten().getLatestDailyUpdateDateBeforeTraining();
		if (trainingIntensityChangeDate == null){
			trainingIntensityChangeDate = HOVerwaltung.instance().getModel().getXtraDaten().getNextTrainingDate();
		}
		var training = HOVerwaltung.instance().getModel().getTraining();
		var intensity = (double)training.getTrainingIntensity();
		var spirit = HOVerwaltung.instance().getModel().getTeam().getTeamSpirit();
		var updates = HOVerwaltung.instance().getModel().getXtraDaten().getDailyUpdates();
		var nextDailyUpdates = updates.stream().filter(Objects::nonNull).sorted().collect(Collectors.toList());

		if (cupMatch.isPresent()) {
			// create future cup match dates
			for (int i = 0; i < m_iNoWeeksForecast; i++) {
				var potentialCupMatch = new MatchKurzInfo();
				potentialCupMatch.setMatchType(MatchType.CUP);
				potentialCupMatch.setMatchStatus(-1);
				potentialCupMatch.setMatchSchedule(cupMatch.get().getMatchSchedule().plus(7, ChronoUnit.DAYS));
				matches.add(potentialCupMatch);
				cupMatch = java.util.Optional.of(potentialCupMatch);
			}
		}

		var matchesSorted = matches.stream().sorted(Comparator.comparing(MatchKurzInfo::getMatchSchedule)).toList();
		for (var match : matchesSorted) {
			var matchDate = match.getMatchSchedule();
			if (matchDate.isAfter(toDate)) break;

			// Insert daily update points which are before training intensity changes
			// or before match date if current date is after training intensity change
			insertDailyUpdates(nextDailyUpdates, trainingIntensityChangeDate.isBefore(matchDate)? trainingIntensityChangeDate: matchDate);
			if (trainingIntensityChangeDate.isBefore(matchDate)) {
				m_clPoints.add(new Point(trainingIntensityChangeDate, spirit, TRAINING_PT, intensity));
				trainingIntensityChangeDate=trainingIntensityChangeDate.plus(7, ChronoUnit.DAYS);
			}
			// Insert daily update points which are between training intensity change and match date
			insertDailyUpdates(nextDailyUpdates, matchDate);

			var htweek = matchDate.toHTWeek();
			Curve.Point point;
			if (match.getMatchType() == MatchType.LEAGUE) {
				switch (htweek.week) {
					case 16 -> point = new Point(matchDate, getTargetSpirit(), RESET_PT);
					case 15 -> {
						point = new Point(matchDate,
								IMatchDetails.EINSTELLUNG_NORMAL,
								htweek.week,
								MatchType.QUALIFICATION,
								intensity);
						point.m_strTooltip = TranslationFacility.tr("ls.match.matchtype.qualification");
					}
					default -> {
						point = new Point(matchDate,
								IMatchDetails.EINSTELLUNG_NORMAL,
								htweek.week, MatchType.LEAGUE, intensity);
						point.m_strTooltip = (htweek.week)
								+ ". "
								+ TranslationFacility.tr("ls.match.matchtype.league");
					}
				}
				m_clPoints.add(point);
			} else if (match.getMatchType() == MatchType.CUP && match.getCupLevel() == CupLevel.NATIONALorDIVISIONAL) {
				var matchTypTooltip = new StringBuilder("ls.match.matchtype.cup");
				if (match.getMatchStatus() == -1) matchTypTooltip.append(".potential");
				// add Cup Match
				point = new Curve.Point(matchDate,
						IMatchDetails.EINSTELLUNG_NORMAL,
						htweek.week, MatchType.CUP, intensity);
				point.m_strTooltip = htweek.week
						+ ". "
						+ TranslationFacility.tr(matchTypTooltip.toString());
				m_clPoints.add(point);
			}
		}
	}

	private void insertDailyUpdates(List<HODateTime> nextDailyUpdates, HODateTime until) {
		var removeUpdates = new ArrayList<HODateTime>();
		for (var update: nextDailyUpdates){
			if ( update.isBefore(until)) {
				m_clPoints.add(new Curve.Point(update, TEAM_SPIRIT_UNKNOWN, UPDATE_PT));
				removeUpdates.add(update);
			}
		}
		for (var remove: removeUpdates) {
			nextDailyUpdates.remove(remove);
			nextDailyUpdates.add(remove.plus(7, ChronoUnit.DAYS));
		}
	}

	private void readPastMatches() {
		Basics ibasics = HOVerwaltung.instance().getModel().getBasics();
		Liga iliga = HOVerwaltung.instance().getModel().getLeague();
		if (iliga != null) {
			var start = ibasics.getDatum().minus(WEEKS_BACK*7, ChronoUnit.DAYS).toDbTimestamp();
			var types = new ArrayList<Integer>();
			types.add(MatchType.CUP.getId());
			types.add(MatchType.LEAGUE.getId());
			var matches = dbManager.getMatchesKurzInfo(ibasics.getTeamId(), MatchKurzInfo.FINISHED, start, types );

			int iMatchDay;
			HODateTime maxDate = null;

			Curve.Point point;
			for ( var match : matches){
				var details = match.getMatchdetails();
				if ( details != null){
					var sortDate = details.getMatchDate();
					iMatchDay = sortDate.toLocaleHTWeek().week;
					var attitude = ibasics.getTeamId()==details.getHomeTeamId()?details.getHomeEinstellung():details.getGuestEinstellung();
					point = new Point(sortDate, attitude, iMatchDay, details.getMatchType(), -1);

					m_clPoints.add(point);
					maxDate = point.m_dDate;
				}
			}
			// Add update points
			// this function reads only league, cup and qualification matches,
			// therefor has to add all Updatepoints
			var points = new ArrayList<>(m_clPoints);
			for ( var p : points){
				point = p;
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
		var pointDate=point.m_dDate;

		// League game ?
		if (bAllPoints || point.m_mtMatchType == MatchType.LEAGUE
				|| point.m_mtMatchType == MatchType.QUALIFICATION
				|| point.m_iPointType == RESET_PT) {
			pointDate = pointDate.plus(21, ChronoUnit.HOURS); // Sunday 15:00
			m_clPoints.add(new Curve.Point(pointDate, TEAM_SPIRIT_UNKNOWN, UPDATE_PT));
			pointDate = pointDate.plus( 21, ChronoUnit.HOURS); // Monday 12:00
			m_clPoints.add(new Curve.Point(pointDate, TEAM_SPIRIT_UNKNOWN, UPDATE_PT));
			pointDate = pointDate.plus( 21, ChronoUnit.HOURS); // Tuesday 09:00
			m_clPoints.add(new Curve.Point(pointDate, TEAM_SPIRIT_UNKNOWN, UPDATE_PT));
		}
		if (bAllPoints || point.m_mtMatchType == MatchType.CUP) {
			pointDate = pointDate.plus(21, ChronoUnit.HOURS); // Wednesday 06:00
			m_clPoints.add(new Curve.Point(pointDate, TEAM_SPIRIT_UNKNOWN, UPDATE_PT));
			pointDate = pointDate.plus(21, ChronoUnit.HOURS); // Thursday 03:00
			m_clPoints.add(new Curve.Point(pointDate, TEAM_SPIRIT_UNKNOWN, UPDATE_PT));
			pointDate = pointDate.plus(21, ChronoUnit.HOURS); // Friday 00:00
			m_clPoints.add(new Curve.Point(pointDate, TEAM_SPIRIT_UNKNOWN, UPDATE_PT));
			pointDate = pointDate.plus(21, ChronoUnit.HOURS); // Friday 21:00
			m_clPoints.add(new Curve.Point(pointDate, TEAM_SPIRIT_UNKNOWN, UPDATE_PT));
		}
	}

	private void addEndOfSeasonPoints(Curve.Point point, HODateTime maxDate) {
		var pointDate = point.m_dDate.plus(7, ChronoUnit.DAYS); 	// L14 p14 rel p15 RE p0 L1 (Kleinbuchstaben ist optional)

		if (maxDate != null && maxDate.isAfter(pointDate)) {
			Curve.Point p = new Curve.Point(pointDate,
					IMatchDetails.EINSTELLUNG_UNBEKANNT, 15,
					MatchType.QUALIFICATION, -1);
			// don't add point only update points, real point come from database
			// if at all
			addUpdatePoints(p, true);

			pointDate = pointDate.plus( 7, ChronoUnit.DAYS); // Matchday 16

			if (maxDate.isAfter(pointDate)) {
				p = new Curve.Point(pointDate, getTargetSpirit(), RESET_PT);
				m_clPoints.add(p);
				addUpdatePoints(p, true);
			}
		}
	}
}