package core.model.match;

import core.db.DBManager;
import core.model.HOVerwaltung;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;


/**
 * Helper class to retrieve match related information.
 *
 * @author aik
 */
public class MatchHelper {

	public static final int UNKNOWN = -99;
	/* Match on neutral ground (we should try to detect if it's AWAY/HOME, eventually) */
	public static final int NEUTRAL_GROUND = -2;
	/* Not a users match */
	public static final int FOREIGN_MATCH = -1;
	
	private static MatchHelper m_clInstance;

	public static MatchHelper instance() {
		if (m_clInstance == null)
			m_clInstance = new MatchHelper();
		return m_clInstance;
	}

	/**
	 * Get the match location.
	 * (using constants from IMatchHelper)
	 * 
	 * @param match		match short info
	 */
	public short getLocation(MatchKurzInfo match) {
		return getLocation(match.getHeimID(), match.getGastID(), match.getMatchID(), match.getMatchTyp());
	}

	/**
	 * Get the match location.
	 * (using constants from IMatchHelper)
	 * 
	 * @param matchId	match Id
	 */
	public short getLocation(SourceSystem sourceSystem, int matchId) {
		MatchLineup ml = DBManager.instance().getMatchLineup(sourceSystem.getId(), matchId);
		return getLocation(ml.getHeimId(), ml.getGastId(), matchId, ml.getMatchTyp());
	}

	/**
	 * Get the match location.
	 * (using constants from IMatchHelper)
	 * 
	 * @param homeTeamId	home team Id
	 * @param awayTeamId	away team Id
	 * @param matchId		match Id
	 * @param matchType		match Type (league, cup, friendly...) from IMatchLineup
	 */
	public short getLocation(int homeTeamId, int awayTeamId, int matchId, MatchType matchType) {
		/**
		 * Current progress:
		 * =================
		 * League/Cup/Qualification: 	Home, Away, AwayDerby are recognized correctly
		 * 
		 * Friendlies:					Home, Away, AwayDerby are recognized correctly
		 * 									(for downloads with HO >= 1.401,
		 * 									for other downloads only HOME is recognized)
		 */
		int userTeamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		String userStadiumName = HOVerwaltung.instance().getModel().getStadium().getStadienname();
		int userStadiumId = HOVerwaltung.instance().getModel().getStadium().getArenaId();
		// TODO: It would be better to use the user region at match date, because in the meantime the user may have changed his region
		int userRegion = HOVerwaltung.instance().getModel().getBasics().getRegionId();

		int location = UNKNOWN;

		if (userTeamId != homeTeamId && userTeamId != awayTeamId) {
			return FOREIGN_MATCH; // foreign match
		}

		Matchdetails details = DBManager.instance().getMatchDetails(matchId);

   		// For a league/qualification/cup game, the home team always has the home advantage (no neutral grounds) 
   		// (exception for cup finals, see below)
   		if (matchType == MatchType.LEAGUE || matchType == MatchType.CUP || matchType == MatchType.QUALIFICATION) {
   			if (homeTeamId == userTeamId)
   				location = IMatchDetails.LOCATION_HOME;
   		}
   		
   		// For friendlies, also check the stadium name, because we may play on neutral ground
   		if (matchType.isFriendly()) {
   			if (homeTeamId == userTeamId) {
   				// TODO: For now, we check the arena name and the id  because old users don't have the arena id in the DB (new since 1.401) but we can remove the name compare later
   				if (details.getArenaName().equals(userStadiumName) 
   						|| (userStadiumId > 0 && details.getArenaID() == userStadiumId)) {
   					// our teamID and our stadium name/stadium Id -> home match
   					location = IMatchDetails.LOCATION_HOME;
   				} else {
   					// our teamID is home team, but not our stadium
   					// i.e. neutral ground
   					location = NEUTRAL_GROUND;
   				}
   			}
   		}
   		
   		// Don't check home matches, except for the cup (because the cup finals are on neutral ground)
   		if (location != IMatchDetails.LOCATION_HOME || matchType == MatchType.CUP) {
   			if (matchType == MatchType.LEAGUE || matchType == MatchType.QUALIFICATION || matchType == MatchType.CUP) {
   	   			/**
   	   			 * league or cup match -> check highlights
   	   			 */
   		   		ArrayList<MatchEvent> highlights = details.getHighlights();
   				for (int i=0; i<highlights.size(); i++) {
   					MatchEvent curHighlight = highlights.get(i);
   					if (curHighlight.getMatchEventID() == MatchEvent.MatchEventID.REGIONAL_DERBY) {
   						location = IMatchDetails.LOCATION_AWAYDERBY;
   					    break;}
   					else if (curHighlight.getMatchEventID() == MatchEvent.MatchEventID.NEUTRAL_GROUND) {
   						// A cup match on neutral ground (finals!) can't be a derby
   						location = NEUTRAL_GROUND;
   						break;
   					}
   					if (i>5) { // 'derby' and 'neutral ground' must be one of the first events
   						break;
   					}
   				}
   				// No home match, no derby -> away match
   				if (location != IMatchDetails.LOCATION_HOME && location != IMatchDetails.LOCATION_AWAYDERBY)
   					location = IMatchDetails.LOCATION_AWAY;
   			} else {
   				/**
   				 * Friendy match (not in our stadium)
   				 */
   				int stadiumRegion = details.getRegionId();
   				if (stadiumRegion > 0 && userRegion > 0) {
   					// Stadium region & user Region valid
   	   				if (userRegion == stadiumRegion)
   	   					location = IMatchDetails.LOCATION_AWAYDERBY;
   	   				else
   	   					location = IMatchDetails.LOCATION_AWAY;
   				} else {
   					// Stadium region or user region invalid 
   					// (old data, downloaded with HO<1.401)
   					location = UNKNOWN;
   				}
   			}
   		}
   		return (short)location;
	}

	public boolean hasOverConfidence (ArrayList<MatchEvent> highlights, int teamId) {
		Iterator<MatchEvent> iter = highlights.iterator();
		while (iter.hasNext()) {
			MatchEvent me = iter.next();
			if (me.getTeamID() == teamId) {
				if (me.getMatchEventID() == MatchEvent.MatchEventID.UNDERESTIMATION) {return true;}
				}
			}
		return false;
	}

	public boolean hasTacticalProblems (ArrayList<MatchEvent> highlights, int teamId) {
		Iterator<MatchEvent> iter = highlights.iterator();
		while (iter.hasNext()) {
			MatchEvent me = iter.next();
			if (me.getTeamID() == teamId) {
				if (me.getMatchEventID() == MatchEvent.MatchEventID.ORGANIZATION_BREAKS) {return true;}
				}
		}
		return false;
	}

	public boolean hasRedCard (ArrayList<MatchEvent> highlights, int teamId) {
		Iterator<MatchEvent> iter = highlights.iterator();
		while (iter.hasNext()) {
			MatchEvent me = iter.next();
			if ((me.getTeamID() == teamId) && (me.isRedCard())) {return true;}
				}
		return false;
	}

	public boolean hasInjury (ArrayList<MatchEvent> highlights, int teamId) {
		Iterator<MatchEvent> iter = highlights.iterator();
		while (iter.hasNext()) {
			MatchEvent me = iter.next();
			if ( (me.getTeamID() == teamId) && me.isBruisedOrInjured()) {return true;}
			}
		return false;
	}

	public boolean hasWeatherSE (ArrayList<MatchEvent> highlights, int teamId) {
		Iterator<MatchEvent> iter = highlights.iterator();
		while (iter.hasNext()) {
								MatchEvent hlight = iter.next();
								if ( (hlight.getTeamID() == teamId) && (hlight.isSpecialtyWeatherSE()))
									{return true;}
								}
									return false;
		}

	public boolean hasManualSubstitution (ArrayList<MatchEvent> highlights, int teamId) {
		Iterator<MatchEvent> iter = highlights.iterator();
		while (iter.hasNext()) {
			MatchEvent hlight = iter.next();
			if (hlight.getTeamID() == teamId) {
					if (hlight.getMatchEventID() == MatchEvent.MatchEventID.PLAYER_SUBSTITUTION_TEAM_IS_BEHIND     ||   // #350
						hlight.getMatchEventID() == MatchEvent.MatchEventID.PLAYER_SUBSTITUTION_TEAM_IS_AHEAD ||   // #351
						hlight.getMatchEventID() == MatchEvent.MatchEventID.PLAYER_SUBSTITUTION_MINUTE      ||   // #352
						hlight.getMatchEventID() == MatchEvent.MatchEventID.CHANGE_OF_TACTIC_TEAM_IS_BEHIND ||   // #360
						hlight.getMatchEventID() == MatchEvent.MatchEventID.CHANGE_OF_TACTIC_TEAM_IS_AHEAD  ||   // #361
						hlight.getMatchEventID() == MatchEvent.MatchEventID.CHANGE_OF_TACTIC_MINUTE) {           // #362
						return true;
					}
				}
			}
		return false;
	}

	public boolean hasPullBack (ArrayList<MatchEvent> highlights, int teamId) {
		Iterator<MatchEvent> iter = highlights.iterator();
		while (iter.hasNext()) {
			MatchEvent hlight = (MatchEvent) iter.next();
			if (hlight.getTeamID() == teamId) {
				// Pull back event
				if (hlight.getMatchEventCategory() == MatchEvent.MatchEventID.WITHDRAW.ordinal()) {return true;}
			}			
		}
		return false;
	}
}
