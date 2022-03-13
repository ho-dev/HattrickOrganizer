// %1127327738353:hoplugins%
package core.file.xml;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.match.*;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.util.HODateTime;
import core.util.HOLogger;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class MatchExporter {
	//~ Static fields/initializers -----------------------------------------------------------------

	//~ Constructors -------------------------------------------------------------------------------

	//~ Methods ------------------------------------------------------------------------------------

	/**
	 * List of useful data for export
	 *
	 * @param startingDate starting data to export from (for all matchTypes)
	 *
	 * @return List of ExportMatchData objects
	 */
	public static List<ExportMatchData> getDataUsefullMatches (HODateTime startingDate) {
		return getDataUsefullMatches(startingDate, startingDate);
	}

	public static List<ExportMatchData> getDataUsefullMatches(HODateTime startingDate, HODateTime startingDateForFriendlies) {
		return getDataUsefullMatches(startingDate, startingDateForFriendlies, true);
	}

	public static List<ExportMatchData> getDataUsefullMatches(HODateTime startingDate, HODateTime startingDateForFriendlies, boolean strict) {
		return getDataUsefullMatches(startingDate, startingDateForFriendlies, strict, false);
	}
	
	/**
	 * List of useful data for export
	 *
	 * @param startingDate starting data to export from (for non friendlies)
	 * @param startingDateForFriendlies starting data to export from (for friendlies)
	 * @param strict is true, export only matches *without* cards, injuries, tactical problems / overconfidence / weather SE...
	 * @param skipPullBack is true, skip matches with pull back event
	 *
	 * @return List of ExportMatchData objects
	 */
	public static List<ExportMatchData> getDataUsefullMatches(HODateTime startingDate, HODateTime startingDateForFriendlies, boolean strict, boolean skipPullBack) {
		HOLogger.instance().log(MatchExporter.class, "Collecting MatchData");		
		List<ExportMatchData> export = new ArrayList<>();
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		MatchKurzInfo[] matches = DBManager.instance().getMatchesKurzInfo(teamId);

		//check all matches
		for (var match: matches) {
			Matchdetails details = DBManager.instance().loadMatchDetails(match.getMatchType().getId(), match.getMatchID());
			boolean isFriendly = match.getMatchType().isFriendly();
			if (isValidMatch(match, details, startingDateForFriendlies, strict, skipPullBack) && isFriendly
					|| isValidMatch(match, details, startingDate, strict, skipPullBack) && !isFriendly ) {

				//Nun lineup durchlaufen und Spielerdaten holen
				Vector<MatchLineupPosition> aufstellung = DBManager.instance().getMatchLineupPlayers(details.getMatchID(), details.getMatchType(), teamId);
				Hashtable<Integer, Player> lineUpISpieler = new Hashtable<>();

				boolean dataOK = true;

				for (int k = 0;(aufstellung != null) && (k < aufstellung.size()); k++) {
					//MatchDaten zum Player holen
					MatchLineupPosition player = aufstellung.get(k);

					//Alte Werte zum Player holen fï¿½r das Matchdate
					Player formerPlayerData;

					//Bankl + verlketzte ï¿½berspringen
					if (player.getRoleId() >= IMatchRoleID.startReserves) {
						continue;
					}

					formerPlayerData = DBManager.instance().getSpielerAtDate(player.getPlayerId(),match.getMatchSchedule().toDbTimestamp());

					//Keine Daten verfï¿½gbar ?
					if (formerPlayerData == null) {
						//Abbruch
						dataOK = false;
						lineUpISpieler.clear();
						break;
					}

					//ISpieler in ht ablegen
					lineUpISpieler.put(player.getPlayerId(), formerPlayerData);
				} //end for aufstellung

				//Matchdaten ablegen da einwandfrei
				if (dataOK) {
					ExportMatchData data = new ExportMatchData();
					data.setDetails(details);
					data.setInfo(match);
					data.setPlayers(lineUpISpieler);
					export.add(data);					
				}
			} //end For usefull Matches        
		}
		return export;
	}

	private static boolean isValidMatch(MatchKurzInfo info, Matchdetails details, HODateTime startingDate, boolean strict, boolean skipPullBack) {
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		if ((info.getMatchStatus() != MatchKurzInfo.FINISHED) ||  details == null || (details.getMatchID() == -1)) {
			HOLogger.instance().debug(MatchExporter.class, "Ignoring match " + info.getMatchID() + ": not finished");
			return false;
		}
		// Check for WO
		if (details.getHomeMidfield() <= 1 &&
				details.getGuestMidfield() <= 1) {
			HOLogger.instance().debug(MatchExporter.class, "Ignoring match " + info.getMatchID() + ": Walk over");
			return false;
		}
		ArrayList<MatchEvent> highlights = details.getHighlights();
		//Aussortieren starten...
		if (info.getMatchSchedule().isBefore(startingDate)) { //Zu alt !!!
			return false;
		} else if (DBManager.instance().getHrfIDSameTraining(info.getMatchSchedule().toDbTimestamp()) == -1) //Kein HRF gefunden
		{
			HOLogger.instance().debug(MatchExporter.class, "Ignoring match " + info.getMatchID() + ": No matching HRF found");
			return false;
		} else if (strict)//Datum i.O. weitere checks fahren
		{
			//Highlights prüfen auf Verletzung, Rote Karte, Verwirrung, Unterschätzung
			// Check Highlights for our team only
			
			if (MatchHelper.instance().hasRedCard(highlights, teamId)) {
				//Karten check
				HOLogger.instance().debug(MatchExporter.class, "Ignoring match " + info.getMatchID() + ": Got a red card");
				return false;
			}
			//injury / tactical problems / overconfidence check
			else if (MatchHelper.instance().hasInjury(highlights, teamId)) {
				HOLogger.instance().debug(MatchExporter.class, "Ignoring match " + info.getMatchID() + ": Injured or bruised player");
				return false;							
			} else if (MatchHelper.instance().hasTacticalProblems(highlights, teamId)) {								
				// Tactical Problems // Verwirrung
				HOLogger.instance().debug(MatchExporter.class, "Ignoring match " + info.getMatchID() + ": Tactical problems");
				return false;							
			} else if (MatchHelper.instance().hasOverConfidence(highlights, teamId)) { 
				// Overconfidence // Unterschaetzen
				HOLogger.instance().debug(MatchExporter.class, "Ignoring match " + info.getMatchID() + ": Overconfidence");
				return false;
			} else if (MatchHelper.instance().hasWeatherSE(highlights, teamId)) {
				// Weather based SpecialEvents check (as this SE alters player ratings)
				HOLogger.instance().debug(MatchExporter.class, "Ignoring match " + info.getMatchID() + ": Weather SE");
				return false;
				// Manual Substitution
			} else if (MatchHelper.instance().hasManualSubstitution(highlights, teamId)) {
				HOLogger.instance().debug(MatchExporter.class, "Ignoring match " + info.getMatchID() + ": Manual Substitution");
				return false;							
			}

		}
		if (skipPullBack && MatchHelper.instance().hasPullBack(highlights, teamId)) {
			HOLogger.instance().debug(MatchExporter.class, "Ignoring match " + info.getMatchID() + ": Pull Back");
			return false;
		}
		//ende for highlight check

		//		HOLogger.instance().debug(MatchExporter.class, "Exporting match " + info.getMatchID());
		return true;

	}



}
