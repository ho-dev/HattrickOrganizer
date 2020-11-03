// %929884203:de.hattrickorganizer.net%
/*
 * ConvertXml2Hrf.java
 *
 * Created on 12. Januar 2004, 09:44
 */
package core.file.xml;

import core.constants.TeamConfidence;
import core.constants.TeamSpirit;
import core.constants.TrainingType;
import core.constants.player.PlayerAggressiveness;
import core.constants.player.PlayerAgreeability;
import core.constants.player.PlayerHonesty;
import core.constants.player.PlayerSpeciality;
import core.gui.CursorToolkit;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchLineup;
import core.model.match.MatchLineupTeam;
import core.model.match.MatchType;
import core.model.match.Matchdetails;
import core.model.player.IMatchRoleID;
import core.module.config.ModuleConfig;
import core.net.MyConnector;
import core.util.HOLogger;
import core.util.IOUtils;
import module.lineup.substitution.model.Substitution;
import core.HO;
import org.w3c.dom.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import static core.file.xml.XMLManager.xmlAttribute2Hash;
import static core.file.xml.XMLManager.xmlValue2Hash;

/**
 * Convert the necessary xml data into a HRF file.
 * 
 * @author thomas.werth
 */
public class ConvertXml2Hrf {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private ConvertXml2Hrf() {
	}

	/**
	 * Create the HRF data and return it in one string.
	 * 
	 * @throws IOException
	 */
	public static String createHrf()
			throws IOException {
		// init
		StringBuilder buffer = new StringBuilder();

		// Hashtable's füllen
		final MyConnector mc = MyConnector.instance();
		HOMainFrame.instance().setWaitInformation(5);

		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		String teamDetails = mc.getTeamdetails(-1);
		
		if (teamDetails == null) {
			return null;
		}
		
		List<TeamInfo> teamInfoList = null;
		if (teamId <= 0) {
			// We have no team selected
			teamInfoList = XMLTeamDetailsParser.getTeamInfoFromString(teamDetails);
			if (teamInfoList.size() == 1) {
				teamId = teamInfoList.get(0).getTeamId();
			} else if (teamInfoList.size() >= 2){
				CursorToolkit.stopWaitCursor(HOMainFrame.instance().getRootPane());
				TeamSelectionDialog selection = new TeamSelectionDialog(HOMainFrame.instance(), teamInfoList);
				selection.setVisible(true);
				
				if (selection.getCancel() == true) {
					return null;
				}
				
				teamId = selection.getSelectedTeam().getTeamId();
			} else {
				return null;
			}
		}
		
		Map<String, String> teamdetailsDataMap = XMLTeamDetailsParser
				.parseTeamdetailsFromString(teamDetails, teamId);
		HOMainFrame.instance().setWaitInformation(10);
		Map<String, String> clubDataMap = XMLClubParser.parseClubFromString(mc.getVerein(teamId));
		HOMainFrame.instance().setWaitInformation(15);
		Map<String, String> ligaDataMap = XMLLeagueDetailsParser.parseLeagueDetailsFromString(mc.getLeagueDetails(teamdetailsDataMap.get("LeagueLevelUnitID")),
						teamdetailsDataMap.get("TeamID").toString());
		HOMainFrame.instance().setWaitInformation(20);
		Map<String, String> worldDataMap = XMLWorldDetailsParser.parseWorldDetailsFromString(
				mc.getWorldDetails(Integer.parseInt(teamdetailsDataMap
						.get("LeagueID").toString())),
				teamdetailsDataMap.get("LeagueID").toString());

		// Currency fix
		
		if (ModuleConfig.instance().containsKey("CurrencyRate")) {
			worldDataMap.put("CurrencyRate", ModuleConfig.instance().getString("CurrencyRate"));
		} else {
			// We need to get hold of the currency info for the primary team, no matter which team we download.
			TeamInfo primary = null;
			
			if (teamInfoList == null) {
				teamInfoList = XMLTeamDetailsParser.getTeamInfoFromString(teamDetails);
			}
			for (TeamInfo info : teamInfoList) {
				if (info.isPrimaryTeam()) {
					primary = info;
					break;
				}
			}
			
			if (primary != null) {
				
				primary = XMLWorldDetailsParser.updateTeamInfoWithCurrency(primary, mc.getWorldDetails(primary.getLeagueId()));
				
				ModuleConfig.instance().setString("CurrencyRate", primary.getCurrencyRate().trim());
				
				worldDataMap.put("CurrencyRate", ModuleConfig.instance().getString("CurrencyRate"));
			} else {
				HOLogger.instance().error(ConvertXml2Hrf.class, "ConvertXML2Hrf: No primary team found!");
			}
		}


		HOMainFrame.instance().setWaitInformation(25);
		MatchLineup matchLineup = XMLMatchLineupParser.parseMatchLineupFromString(mc.getMatchLineup(-1, teamId,
						MatchType.LEAGUE).toString());
		HOMainFrame.instance().setWaitInformation(30);
		List<MyHashtable> playersData = new xmlPlayersParser().parsePlayersFromString(mc.getPlayers(teamId));
		int youthteamId = HOVerwaltung.instance().getModel().getBasics().getYouthTeamId();
		List<MyHashtable> youthplayers=null;
		if ( youthteamId > 0 ){
			youthplayers = new xmlPlayersParser().parseYouthPlayersFromString(mc.getYouthPlayers(youthteamId));
		}
		HOMainFrame.instance().setWaitInformation(35);
		Map<String, String> economyDataMap = XMLEconomyParser
				.parseEconomyFromString(mc.getEconomy(teamId));
		HOMainFrame.instance().setWaitInformation(40);
		Map<String, String> trainingDataMap = XMLTrainingParser
				.parseTrainingFromString(mc.getTraining(teamId));
		HOMainFrame.instance().setWaitInformation(45);
		List<MyHashtable> staffData = XMLStaffParser
				.parseStaffFromString(mc.getStaff(teamId));
		HOMainFrame.instance().setWaitInformation(50);

		int arenaId = 0;
		try {
			arenaId = Integer.parseInt(teamdetailsDataMap.get("ArenaID"));
		} catch (Exception ignored) {
			
		}
		Map<String, String> arenaDataMap = XMLArenaParser
				.parseArenaFromString(mc.getArena(arenaId));

		// MatchOrder
		HOMainFrame.instance().setWaitInformation(55);
		List<MatchKurzInfo> matches = XMLMatchesParser
				.parseMatchesFromString(mc.getMatches(Integer
						.parseInt(teamdetailsDataMap.get("TeamID").toString()),
						false, true));
		HOMainFrame.instance().setWaitInformation(57);

		// Automatisch alle MatchLineups runterladen
		Map<String, String> nextLineupDataMap = null;
		for (MatchKurzInfo match : matches) {
			if ((match.getMatchStatus() == MatchKurzInfo.UPCOMING)){
				HOMainFrame.instance().setWaitInformation(58);
				// Match is always from the normal system, and league will do
				// the trick as the type.
				nextLineupDataMap = XMLMatchOrderParser
						.parseMatchOrderFromString(mc.getMatchOrder(
								match.getMatchID(), match.getMatchTyp(), teamId));
				break;
			}
		}

		HOMainFrame.instance().setWaitInformation(59);

		MatchLineupTeam matchLineupTeam = null;
		int lastAttitude = 0;
		int lastTactic = 0;
		// Team ermitteln, für Ratings der Player wichtig
		if (matchLineup != null) {
			Matchdetails md = XMLMatchdetailsParser
					.parseMachtdetailsFromString(
							mc.getMatchdetails(matchLineup.getMatchID(),
									matchLineup.getMatchTyp()), null);

			if (matchLineup.getHeimId() == Integer.parseInt(teamdetailsDataMap
					.get("TeamID").toString())) {
				matchLineupTeam = (MatchLineupTeam) matchLineup.getHeim();
				if (md != null) {
					lastAttitude = md.getHomeEinstellung();
					lastTactic = md.getHomeTacticType();
				}
			} else {
				matchLineupTeam = (MatchLineupTeam) matchLineup.getGast();
				if (md != null) {
					lastAttitude = md.getGuestEinstellung();
					lastTactic = md.getGuestTacticType();
				}
			}
		}

		// Abschnitte erstellen
		HOMainFrame.instance().setWaitInformation(60);

		// basics
		createBasics(teamdetailsDataMap, worldDataMap, buffer);
		HOMainFrame.instance().setWaitInformation(65);

		// Liga
		createLeague(ligaDataMap, buffer);
		HOMainFrame.instance().setWaitInformation(70);

		// Club
		createClub(clubDataMap, economyDataMap, teamdetailsDataMap, buffer);
		HOMainFrame.instance().setWaitInformation(75);

		// team
		createTeam(trainingDataMap, buffer);
		HOMainFrame.instance().setWaitInformation(80);

		// lineup
		buffer.append(createLineUp(
				String.valueOf(teamdetailsDataMap.get("TrainerID")),
				nextLineupDataMap));
		HOMainFrame.instance().setWaitInformation(85);

		// economy
		createEconemy(economyDataMap, buffer);
		HOMainFrame.instance().setWaitInformation(90);

		// Arena
		createArena(arenaDataMap, buffer);
		HOMainFrame.instance().setWaitInformation(93);

		// players
		createPlayers(matchLineupTeam, playersData, buffer);
		HOMainFrame.instance().setWaitInformation(96);

		// youth players
		if ( youthplayers != null){
			createYouthPlayers(youthplayers, buffer);
			HOMainFrame.instance().setWaitInformation(97);
		}

		// xtra Data
		createWorld(clubDataMap, teamdetailsDataMap, trainingDataMap,
				worldDataMap, buffer);
		HOMainFrame.instance().setWaitInformation(99);

		// lineup from the last match
		createLastLineUp(teamdetailsDataMap, matchLineupTeam, lastAttitude,
				lastTactic, buffer);
		
		// staff
		createStaff(staffData, buffer);

		HOMainFrame.instance().setWaitInformation(100);

		return buffer.toString();
	}

	/**
	 * Create the arena data.
	 */
	private static void createArena(Map<String, String> arenaDataMap, StringBuilder buffer) {
		buffer.append("[arena]").append('\n');
		buffer.append("arenaname=").append(arenaDataMap.get("ArenaName"))
				.append('\n');
		buffer.append("arenaid=").append(arenaDataMap.get("ArenaID"))
				.append('\n');
		buffer.append("antalStaplats=").append(arenaDataMap.get("Terraces"))
				.append('\n');
		buffer.append("antalSitt=").append(arenaDataMap.get("Basic"))
				.append('\n');
		buffer.append("antalTak=").append(arenaDataMap.get("Roof"))
				.append('\n');
		buffer.append("antalVIP=").append(arenaDataMap.get("VIP")).append('\n');
		buffer.append("seatTotal=").append(arenaDataMap.get("Total"))
				.append('\n');
		buffer.append("expandingStaplats=")
				.append(arenaDataMap.get("ExTerraces")).append('\n');
		buffer.append("expandingSitt=").append(arenaDataMap.get("ExBasic"))
				.append('\n');
		buffer.append("expandingTak=").append(arenaDataMap.get("ExRoof"))
				.append('\n');
		buffer.append("expandingVIP=").append(arenaDataMap.get("ExVIP"))
				.append('\n');
		buffer.append("expandingSseatTotal=")
				.append(arenaDataMap.get("ExTotal")).append('\n');
		buffer.append("isExpanding=").append(arenaDataMap.get("isExpanding"))
				.append('\n');

		// Achtung bei keiner Erweiterung = 0!
		buffer.append("ExpansionDate=")
				.append(arenaDataMap.get("ExpansionDate")).append('\n');
	}

	// //////////////////////////////////////////////////////////////////////////////
	// Helper
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Create the basic data.
	 */
	private static void createBasics(Map<String, String> teamdetailsDataMap,
			Map<String, String> worldDataMap, StringBuilder buffer) {
		buffer.append("[basics]\n");
		buffer.append("application=HO\n");
		buffer.append("appversion=").append(HO.VERSION).append('\n');
		buffer.append("date=").append(teamdetailsDataMap.get("FetchedDate")).append('\n');
		buffer.append("season=").append(worldDataMap.get("Season")).append('\n');
		buffer.append("seasonOffset=").append(worldDataMap.get("SeasonOffset")).append('\n');
		buffer.append("matchround=").append(worldDataMap.get("MatchRound")).append('\n');
		buffer.append("teamID=").append(teamdetailsDataMap.get("TeamID")).append('\n');
		buffer.append("teamName=").append(teamdetailsDataMap.get("TeamName")).append('\n');
		buffer.append("youthTeamID=").append(teamdetailsDataMap.get("YouthTeamID")).append('\n');
		buffer.append("youthTeamName=").append(teamdetailsDataMap.get("YouthTeamName")).append('\n');
		buffer.append("activationDate=").append(teamdetailsDataMap.get("ActivationDate")).append('\n');
		buffer.append("owner=").append(teamdetailsDataMap.get("Loginname")).append('\n');
		buffer.append("ownerHomepage=").append(teamdetailsDataMap.get("HomePage")).append('\n');
		buffer.append("countryID=").append(worldDataMap.get("CountryID")).append('\n');
		buffer.append("leagueID=").append(teamdetailsDataMap.get("LeagueID")).append('\n');
		buffer.append("regionID=").append(teamdetailsDataMap.get("RegionID")).append('\n');
		buffer.append("hasSupporter=").append(teamdetailsDataMap.get("HasSupporter")).append('\n');
	}

	/**
	 * Create the club data.
	 */
	private static void createClub(Map<String, String> clubDataMap,
			Map<String, String> economyDataMap, Map<String, String> teamdetailsDataMap,
			StringBuilder buffer) {
		buffer.append("[club]\n");
		buffer.append("hjTranare=").append(clubDataMap.get("AssistantTrainers")).append('\n');
		buffer.append("psykolog=").append(clubDataMap.get("Psychologists")).append('\n');
		buffer.append("presstalesman=").append(clubDataMap.get("PressSpokesmen")).append('\n');
		buffer.append("lakare=").append(clubDataMap.get("Doctors")).append('\n');
		buffer.append("financialDirectorLevels=").append(clubDataMap.get("FinancialDirectorLevels")).append('\n');
		buffer.append("formCoachLevels=").append(clubDataMap.get("FormCoachLevels")).append('\n');
		buffer.append("tacticalAssistantLevels=").append(clubDataMap.get("TacticalAssistantLevels")).append('\n');
		buffer.append("juniorverksamhet=").append(clubDataMap.get("YouthLevel")).append('\n');
		buffer.append("undefeated=").append(teamdetailsDataMap.get("NumberOfUndefeated")).append('\n');
		buffer.append("victories=").append(teamdetailsDataMap.get("NumberOfVictories")).append('\n');
		buffer.append("fanclub=").append(economyDataMap.get("FanClubSize")).append('\n');
		buffer.append("GlobalRanking=").append(teamdetailsDataMap.get("GlobalRanking")).append('\n');
		buffer.append("LeagueRanking=").append(teamdetailsDataMap.get("LeagueRanking")).append('\n');
		buffer.append("RegionRanking=").append(teamdetailsDataMap.get("RegionRanking")).append('\n');
		buffer.append("PowerRating=").append(teamdetailsDataMap.get("PowerRating")).append('\n');
	}

	/**
	 * Create the economy data.
	 */
	private static void createEconemy(Map<String, String> economyDataMap,
			StringBuilder buffer) {
		// wahrscheinlich in Training.asp fehlt noch
		buffer.append("[economy]").append('\n');

		if (economyDataMap.get("SponsorsPopularity") != null) {
			buffer.append("supporters=")
					.append(economyDataMap.get("SupportersPopularity"))
					.append('\n');
			buffer.append("sponsors=")
					.append(economyDataMap.get("SponsorsPopularity"))
					.append('\n');
			// es wird grad gespielt flag setzen
		} else {
			buffer.append("playingMatch=true");
		}

		buffer.append("cash=").append(economyDataMap.get("Cash")).append('\n');
		buffer.append("IncomeSponsorer=")
				.append(economyDataMap.get("IncomeSponsors")).append('\n');
		buffer.append("incomePublik=")
				.append(economyDataMap.get("IncomeSpectators")).append('\n');
		buffer.append("incomeFinansiella=")
				.append(economyDataMap.get("IncomeFinancial")).append('\n');
		buffer.append("incomeTillfalliga=")
				.append(economyDataMap.get("IncomeTemporary")).append('\n');
		buffer.append("incomeSumma=").append(economyDataMap.get("IncomeSum"))
				.append('\n');
		buffer.append("costsSpelare=")
				.append(economyDataMap.get("CostsPlayers")).append('\n');
		buffer.append("costsPersonal=")
				.append(economyDataMap.get("CostsStaff")).append('\n');
		buffer.append("costsArena=").append(economyDataMap.get("CostsArena"))
				.append('\n');
		buffer.append("costsJuniorverksamhet=")
				.append(economyDataMap.get("CostsYouth")).append('\n');
		buffer.append("costsRantor=")
				.append(economyDataMap.get("CostsFinancial")).append('\n');
		buffer.append("costsTillfalliga=")
				.append(economyDataMap.get("CostsTemporary")).append('\n');
		buffer.append("costsSumma=").append(economyDataMap.get("CostsSum"))
				.append('\n');
		buffer.append("total=")
				.append(economyDataMap.get("ExpectedWeeksTotal")).append('\n');
		buffer.append("lastIncomeSponsorer=")
				.append(economyDataMap.get("LastIncomeSponsors")).append('\n');
		buffer.append("lastIncomePublik=")
				.append(economyDataMap.get("LastIncomeSpectators"))
				.append('\n');
		buffer.append("lastIncomeFinansiella=")
				.append(economyDataMap.get("LastIncomeFinancial")).append('\n');
		buffer.append("lastIncomeTillfalliga=")
				.append(economyDataMap.get("LastIncomeTemporary")).append('\n');
		buffer.append("lastIncomeSumma=")
				.append(economyDataMap.get("LastIncomeSum")).append('\n');
		buffer.append("lastCostsSpelare=")
				.append(economyDataMap.get("LastCostsPlayers")).append('\n');
		buffer.append("lastCostsPersonal=")
				.append(economyDataMap.get("LastCostsStaff")).append('\n');
		buffer.append("lastCostsArena=")
				.append(economyDataMap.get("LastCostsArena")).append('\n');
		buffer.append("lastCostsJuniorverksamhet=")
				.append(economyDataMap.get("LastCostsYouth")).append('\n');
		buffer.append("lastCostsRantor=")
				.append(economyDataMap.get("LastCostsFinancial")).append('\n');
		buffer.append("lastCostsTillfalliga=")
				.append(economyDataMap.get("LastCostsTemporary")).append('\n');
		buffer.append("lastCostsSumma=")
				.append(economyDataMap.get("LastCostsSum")).append('\n');
		buffer.append("lastTotal=")
				.append(economyDataMap.get("LastWeeksTotal")).append('\n');
	}

	/**
	 * Create last lineup section.
	 */
	private static void createLastLineUp(Map<String, String> teamdetailsDataMap,
			MatchLineupTeam matchLineupTeam, int lastAttitude, int lastTactic,
			StringBuilder buffer) {
		buffer.append("[lastlineup]").append('\n');
		buffer.append("trainer=").append(teamdetailsDataMap.get("TrainerID"))
				.append('\n');

		try {
			buffer.append("installning=").append(lastAttitude).append('\n');
			buffer.append("tactictype=").append(lastTactic).append('\n');
			// The field is coachmodifier in matchOrders and StyleOfPlay in MatchLineup
			// but we both named it styleOfPlay
			buffer.append("styleOfPlay=").append(matchLineupTeam.getStyleOfPlay()).append('\n');
			buffer.append("keeper=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.keeper).getSpielerId())
					.append('\n');
			buffer.append("rightBack=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.rightBack).getSpielerId())
					.append('\n');
			buffer.append("insideBack1=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.rightCentralDefender)
							.getSpielerId()).append('\n');
			buffer.append("insideBack2=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.leftCentralDefender)
							.getSpielerId()).append('\n');
			buffer.append("insideBack3=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.middleCentralDefender)
							.getSpielerId()).append('\n');
			buffer.append("leftBack=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.leftBack).getSpielerId())
					.append('\n');
			buffer.append("rightWinger=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.rightWinger).getSpielerId())
					.append('\n');
			buffer.append("insideMid1=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.rightInnerMidfield).getSpielerId())
					.append('\n');
			buffer.append("insideMid2=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.leftInnerMidfield).getSpielerId())
					.append('\n');
			buffer.append("insideMid3=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.centralInnerMidfield)
							.getSpielerId()).append('\n');
			buffer.append("leftWinger=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.leftWinger).getSpielerId())
					.append('\n');
			buffer.append("forward1=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.rightForward).getSpielerId())
					.append('\n');
			buffer.append("forward2=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.leftForward).getSpielerId())
					.append('\n');
			buffer.append("forward3=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.centralForward).getSpielerId())
					.append('\n');
			buffer.append("substBack=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.substCD1).getSpielerId())
					.append('\n');
			buffer.append("substInsideMid=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.substIM1).getSpielerId())
					.append('\n');
			buffer.append("substWinger=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.substWI1).getSpielerId())
					.append('\n');
			buffer.append("substKeeper=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.substGK1).getSpielerId())
					.append('\n');
			buffer.append("substForward=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.substFW1).getSpielerId())
					.append('\n');
			buffer.append("captain=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.captain).getSpielerId())
					.append('\n');
			buffer.append("kicker1=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.setPieces).getSpielerId())
					.append('\n');

			buffer.append("behrightBack=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.rightBack).getTaktik())
					.append('\n');
			buffer.append("behinsideBack1=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.rightCentralDefender).getTaktik())
					.append('\n');
			buffer.append("behinsideBack2=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.leftCentralDefender).getTaktik())
					.append('\n');
			buffer.append("behinsideBack3=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.middleCentralDefender).getTaktik())
					.append('\n');
			buffer.append("behleftBack=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.leftBack).getTaktik())
					.append('\n');
			buffer.append("behrightWinger=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.rightWinger).getTaktik())
					.append('\n');
			buffer.append("behinsideMid1=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.rightInnerMidfield).getTaktik())
					.append('\n');
			buffer.append("behinsideMid2=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.leftInnerMidfield).getTaktik())
					.append('\n');
			buffer.append("behinsideMid3=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.centralInnerMidfield).getTaktik())
					.append('\n');
			buffer.append("behleftWinger=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.leftWinger).getTaktik())
					.append('\n');
			buffer.append("behforward1=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.rightForward).getTaktik())
					.append('\n');
			buffer.append("behforward2=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.leftForward).getTaktik())
					.append('\n');
			buffer.append("behforward3=")
					.append(matchLineupTeam.getPlayerByPosition(
							IMatchRoleID.centralForward).getTaktik())
					.append('\n');

			int i = 0;
			for (Substitution sub : matchLineupTeam.getSubstitutions()) {
				if (sub != null) {
					buffer.append("subst").append(i).append("playerOrderID=")
							.append(sub.getPlayerOrderId()).append('\n');
					buffer.append("subst").append(i).append("playerIn=")
							.append(sub.getObjectPlayerID()).append('\n');
					buffer.append("subst").append(i).append("playerOut=")
							.append(sub.getSubjectPlayerID()).append('\n');
					buffer.append("subst").append(i).append("orderType=")
							.append(sub.getOrderType().getId()).append('\n');
					buffer.append("subst").append(i)
							.append("matchMinuteCriteria=")
							.append(sub.getMatchMinuteCriteria()).append('\n');
					buffer.append("subst").append(i).append("pos=")
							.append(sub.getRoleId()).append('\n');
					buffer.append("subst").append(i).append("behaviour=")
							.append(sub.getBehaviour()).append('\n');
					buffer.append("subst").append(i).append("card=")
							.append(sub.getRedCardCriteria().getId())
							.append('\n');
					buffer.append("subst").append(i).append("standing=")
							.append(sub.getStanding().getId()).append('\n');
					i++;
				}
			}
		} catch (Exception e) {
			HOLogger.instance().debug(ConvertXml2Hrf.class,
					"Error(last lineup): " + e);
		}
	}

	/**
	 * Create the league data.
	 */
	private static void createLeague(Map<String, String> ligaDataMap, StringBuilder buffer) {
		buffer.append("[league]\n");
		buffer.append("serie=").append(ligaDataMap.get("LeagueLevelUnitName"))
				.append('\n');
		buffer.append("spelade=").append(ligaDataMap.get("Matches"))
				.append('\n');
		buffer.append("gjorda=").append(ligaDataMap.get("GoalsFor"))
				.append('\n');
		buffer.append("inslappta=").append(ligaDataMap.get("GoalsAgainst"))
				.append('\n');
		buffer.append("poang=").append(ligaDataMap.get("Points")).append('\n');
		buffer.append("placering=").append(ligaDataMap.get("Position"))
				.append('\n');
	}

	private static String getPlayerForNextLineup(String position, Map<?, ?> next) {
		if (next != null) {
			final Object ret = next.get(position);
			if (ret != null) {
				return ret.toString();
			}
		}
		return "0";
	}

	private static String getPlayerOrderForNextLineup(String position,
			Map<?, ?> map) {
		if (map != null) {
			String ret = (String) map.get(position);

			if (ret != null) {
				ret = ret.trim();
				if (!"null".equals(ret) && !"".equals(ret)) {
					return ret.trim();
				}
			}
		}
		return "0";
	}

	/**
	 * Creates the lineup data.
	 *
	 * @param trainerId
	 *            The playerId of the trainer of the club.
	 * @param nextLineup
	 *            The lineup info hashmap from the parser.
	 * @return
	 * @throws Exception
	 */
	public static String createLineUp(String trainerId, Map<String, String> nextLineup) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[lineup]").append('\n');

		if (nextLineup != null) {
			
			try {
				buffer.append("trainer=").append(trainerId).append('\n');
				buffer.append("installning=").append(nextLineup.get("Attitude")).append('\n');
				buffer.append("styleOfPlay=").append(nextLineup.get("StyleOfPlay")).append('\n');
				buffer.append("tactictype=").append(nextLineup.get("TacticType")).append('\n');
				buffer.append("keeper=").append(getPlayerForNextLineup("KeeperID", nextLineup))	.append('\n');
				buffer.append("rightBack=").append(getPlayerForNextLineup("RightBackID", nextLineup)).append('\n');
				buffer.append("rightCentralDefender=").append(getPlayerForNextLineup("RightCentralDefenderID",nextLineup)).append('\n');
				buffer.append("leftCentralDefender=").append(getPlayerForNextLineup("LeftCentralDefenderID",nextLineup)).append('\n');
				buffer.append("middleCentralDefender=").append(getPlayerForNextLineup("MiddleCentralDefenderID",nextLineup)).append('\n');
				buffer.append("leftBack=").append(getPlayerForNextLineup("LeftBackID", nextLineup))	.append('\n');
				buffer.append("rightwinger=").append(getPlayerForNextLineup("RightWingerID", nextLineup)).append('\n');
				buffer.append("rightInnerMidfield=").append(getPlayerForNextLineup("RightInnerMidfieldID",nextLineup)).append('\n');
				buffer.append("leftInnerMidfield=").append(getPlayerForNextLineup("LeftInnerMidfieldID",nextLineup)).append('\n');
				buffer.append("middleInnerMidfield=").append(getPlayerForNextLineup("CentralInnerMidfieldID",nextLineup)).append('\n');
				buffer.append("leftwinger=").append(getPlayerForNextLineup("LeftWingerID", nextLineup)).append('\n');
				buffer.append("rightForward=").append(getPlayerForNextLineup("RightForwardID", nextLineup)).append('\n');
				buffer.append("leftForward=").append(getPlayerForNextLineup("LeftForwardID", nextLineup)).append('\n');
				buffer.append("centralForward=").append(getPlayerForNextLineup("CentralForwardID",nextLineup)).append('\n');
				buffer.append("substgk1=").append(getPlayerForNextLineup("substGK1ID", nextLineup)).append('\n');
				buffer.append("substgk2=").append(getPlayerForNextLineup("substGK2ID", nextLineup)).append('\n');
				buffer.append("substcd1=").append(getPlayerForNextLineup("substCD1ID", nextLineup)).append('\n');
				buffer.append("substcd2=").append(getPlayerForNextLineup("substCD2ID", nextLineup)).append('\n');
				buffer.append("substwb1=").append(getPlayerForNextLineup("substWB1ID", nextLineup)).append('\n');
				buffer.append("substwb2=").append(getPlayerForNextLineup("substWB2ID", nextLineup)).append('\n');
				buffer.append("substim1=").append(getPlayerForNextLineup("substIM1ID",nextLineup)).append('\n');
				buffer.append("substim2=").append(getPlayerForNextLineup("substIM2ID",nextLineup)).append('\n');
				buffer.append("substwi1=").append(getPlayerForNextLineup("substWI1ID", nextLineup)).append('\n');
				buffer.append("substwi2=").append(getPlayerForNextLineup("substWI2ID", nextLineup)).append('\n');
				buffer.append("substfw1=").append(getPlayerForNextLineup("substFW1ID", nextLineup)).append('\n');
				buffer.append("substfw2=").append(getPlayerForNextLineup("substFW2ID", nextLineup)).append('\n');
				buffer.append("substxt1=").append(getPlayerForNextLineup("substXT1ID", nextLineup)).append('\n');
				buffer.append("substxt2=").append(getPlayerForNextLineup("substXT2ID", nextLineup)).append('\n');
				buffer.append("captain=").append(getPlayerForNextLineup("CaptainID", nextLineup)).append('\n');
				buffer.append("kicker1=").append(getPlayerForNextLineup("KickerID", nextLineup)).append('\n');
	
				buffer.append("order_rightback=").append(getPlayerOrderForNextLineup("RightBackOrder",	nextLineup)).append('\n');
				buffer.append("order_rightCentralDefender=").append(getPlayerOrderForNextLineup("RightCentralDefenderOrder", nextLineup)).append('\n');
				buffer.append("order_leftCentralDefender=").append(getPlayerOrderForNextLineup("LeftCentralDefenderOrder", nextLineup)).append('\n');
				buffer.append("order_middleCentralDefender=").append(getPlayerOrderForNextLineup("MiddleCentralDefenderOrder", nextLineup)).append('\n');
				buffer.append("order_leftBack=").append(getPlayerOrderForNextLineup("LeftBackOrder",nextLineup)).append('\n');
				buffer.append("order_rightWinger=").append(getPlayerOrderForNextLineup("RightWingerOrder",	nextLineup)).append('\n');
				buffer.append("order_rightInnerMidfield=").append(getPlayerOrderForNextLineup("RightInnerMidfieldOrder", nextLineup)).append('\n');
				buffer.append("order_leftInnerMidfield=").append(getPlayerOrderForNextLineup("LeftInnerMidfieldOrder", nextLineup)).append('\n');
				buffer.append("order_centralInnerMidfield=").append(getPlayerOrderForNextLineup("CentralInnerMidfieldOrder", nextLineup)).append('\n');
				buffer.append("order_leftWinger=").append(getPlayerOrderForNextLineup("LeftWingerOrder",nextLineup)).append('\n');
				buffer.append("order_rightForward=").append(getPlayerOrderForNextLineup("RightForwardOrder",nextLineup)).append('\n');
				buffer.append("order_leftForward=").append(getPlayerOrderForNextLineup("LeftForwardOrder",nextLineup)).append('\n');
				buffer.append("order_centralForward=").append(getPlayerOrderForNextLineup("CentralForwardOrder",	nextLineup)).append('\n');

				int iSub=-1;
				String playerOrderIdString;
				while ((playerOrderIdString = getMatchOrderInfo( nextLineup, ++iSub, "playerOrderID")) != null ) {
					buffer.append(playerOrderIdString)
							.append(getMatchOrderInfo(nextLineup, iSub, "playerIn"))
							.append(getMatchOrderInfo(nextLineup, iSub, "playerOut"))
							.append(getMatchOrderInfo(nextLineup, iSub, "orderType"))
							.append(getMatchOrderInfo(nextLineup, iSub, "matchMinuteCriteria"))
							.append(getMatchOrderInfo(nextLineup, iSub, "pos"))
							.append(getMatchOrderInfo(nextLineup, iSub, "behaviour"))
							.append(getMatchOrderInfo(nextLineup, iSub, "card"))
							.append(getMatchOrderInfo(nextLineup, iSub, "standing"));
				}

				for (int i = 0; i < 11; i++) {
					String key = "PenaltyTaker" + i;
					buffer.append("penalty").append(i).append("=")
							.append(getPlayerForNextLineup(key, nextLineup))
							.append('\n');
				}
	
			} catch (Exception e) {
				HOLogger.instance().debug(ConvertXml2Hrf.class,
						"Error(lineup): " + e);
			}
		}
		return buffer.toString();
	}

	private static String getMatchOrderInfo(Map<String, String> nextLineup, int i, String key) {
		String _key = "subst"+i+key;
		var value = nextLineup.get(_key);
		if ( value != null)	return _key + "=" + value + '\n';
		return null;
	}

	/**
	 * Create the player data.
	 */
	private static void createPlayers(MatchLineupTeam matchLineupTeam,
									  List<MyHashtable> playersData, StringBuilder buffer) {
		Map<?, ?> ht = null;

		for (int i = 0; (playersData != null) && (i < playersData.size()); i++) {
			ht = (Map<?, ?>) playersData.get(i);

			buffer.append("[player").append(ht.get("PlayerID").toString())
					.append(']').append('\n');

			if (ht.get("NickName").toString().length() > 0) {
				buffer.append("name=");
				buffer.append(ht.get("FirstName").toString()).append(" '");
				buffer.append(ht.get("NickName").toString()).append("' ");
				buffer.append(ht.get("LastName").toString()).append('\n');
			} else {
				buffer.append("name=");
				buffer.append(ht.get("FirstName").toString()).append(' ');
				buffer.append(ht.get("LastName").toString()).append('\n');
			}
			buffer.append("firstname=").append(ht.get("FirstName").toString())
					.append('\n');
			buffer.append("nickname=").append(ht.get("NickName").toString())
					.append('\n');
			buffer.append("lastname=").append(ht.get("LastName").toString())
					.append('\n');
			buffer.append("ald=").append(ht.get("Age").toString()).append('\n');
			buffer.append("agedays=").append(ht.get("AgeDays").toString())
					.append('\n');
			buffer.append("ska=").append(ht.get("InjuryLevel").toString())
					.append('\n');
			buffer.append("for=").append(ht.get("PlayerForm").toString())
					.append('\n');
			buffer.append("uth=").append(ht.get("StaminaSkill").toString())
					.append('\n');
			buffer.append("spe=").append(ht.get("PlaymakerSkill").toString())
					.append('\n');
			buffer.append("mal=").append(ht.get("ScorerSkill").toString())
					.append('\n');
			buffer.append("fra=").append(ht.get("PassingSkill").toString())
					.append('\n');
			buffer.append("ytt=").append(ht.get("WingerSkill").toString())
					.append('\n');
			buffer.append("fas=").append(ht.get("SetPiecesSkill").toString())
					.append('\n');
			buffer.append("bac=").append(ht.get("DefenderSkill").toString())
					.append('\n');
			buffer.append("mlv=").append(ht.get("KeeperSkill").toString())
					.append('\n');
			buffer.append("rut=").append(ht.get("Experience").toString())
					.append('\n');
			buffer.append("loy=").append(ht.get("Loyalty").toString())
					.append('\n');
			buffer.append("homegr=")
					.append(ht.get("MotherClubBonus").toString()).append('\n');
			buffer.append("led=").append(ht.get("Leadership").toString())
					.append('\n');
			buffer.append("sal=").append(ht.get("Salary").toString())
					.append('\n');
			buffer.append("mkt=").append(ht.get("MarketValue").toString())
					.append('\n');
			buffer.append("gev=").append(ht.get("CareerGoals").toString())
					.append('\n');
			buffer.append("gtl=").append(ht.get("LeagueGoals").toString())
					.append('\n');
			buffer.append("gtc=").append(ht.get("CupGoals").toString())
					.append('\n');
			buffer.append("gtt=").append(ht.get("FriendliesGoals").toString())
					.append('\n');
			buffer.append("hat=").append(ht.get("CareerHattricks").toString())
					.append('\n');
			buffer.append("CountryID=").append(ht.get("CountryID").toString())
					.append('\n');
			buffer.append("warnings=").append(ht.get("Cards").toString())
					.append('\n');
			buffer.append("speciality=").append(ht.get("Specialty").toString())
					.append('\n');
			buffer.append("specialityLabel=")
					.append(PlayerSpeciality.toString(Integer.parseInt(ht.get(
							"Specialty").toString()))).append('\n');
			buffer.append("gentleness=")
					.append(ht.get("Agreeability").toString()).append('\n');
			buffer.append("gentlenessLabel=")
					.append(PlayerAgreeability.toString(Integer.parseInt(ht
							.get("Agreeability").toString()))).append('\n');
			buffer.append("honesty=").append(
					ht.get("Honesty").toString() + "\n");
			buffer.append("honestyLabel=")
					.append(PlayerHonesty.toString(Integer.parseInt(ht.get(
							"Honesty").toString()))).append('\n');
			buffer.append("Aggressiveness=")
					.append(ht.get("Aggressiveness").toString()).append('\n');
			buffer.append("AggressivenessLabel=")
					.append(PlayerAggressiveness.toString(Integer.parseInt(ht
							.get("Aggressiveness").toString()))).append('\n');

			if (ht.get("TrainerSkill") != null) {
				buffer.append("TrainerType=")
						.append(ht.get("TrainerType").toString()).append('\n');
				buffer.append("TrainerSkill=")
						.append(ht.get("TrainerSkill").toString()).append('\n');
			} else {
				buffer.append("TrainerType=").append('\n');
				buffer.append("TrainerSkill=").append('\n');
			}
			if (ht.get("LastMatch_Date") != null) {
				buffer.append("LastMatch_Date=")
						.append(ht.get("LastMatch_Date")).append('\n');
				buffer.append("LastMatch_Rating=")
						.append(ht.get("LastMatch_Rating").toString()).append('\n');
				buffer.append("LastMatch_id=")
						.append(ht.get("LastMatch_id").toString()).append('\n');
			} else {
				buffer.append("LastMatch_Date=").append('\n');
				buffer.append("LastMatch_Rating=").append('\n');
				buffer.append("LastMatch_id=0").append('\n');
			}

			if ((matchLineupTeam != null)
					&& (matchLineupTeam.getPlayerByID(Integer.parseInt(ht.get(
					"PlayerID").toString())) != null)
					&& (matchLineupTeam.getPlayerByID(
					Integer.parseInt(ht.get("PlayerID").toString()))
					.getRating() >= 0)) {
				buffer.append("rating=")
						.append((int) (matchLineupTeam
								.getPlayerByID(
										Integer.parseInt(ht.get("PlayerID")
												.toString())).getRating() * 2))
						.append('\n');
			} else {
				buffer.append("rating=0").append('\n');
			}

			// Bonus
			if ((ht.get("PlayerNumber") != null)
					|| (!ht.get("PlayerNumber").equals(""))) {
				buffer.append("PlayerNumber=").append(ht.get("PlayerNumber"))
						.append('\n');
			}

			buffer.append("TransferListed=").append(ht.get("TransferListed"))
					.append('\n');
			buffer.append("NationalTeamID=").append(ht.get("NationalTeamID"))
					.append('\n');
			buffer.append("Caps=").append(ht.get("Caps")).append('\n');
			buffer.append("CapsU20=").append(ht.get("CapsU20")).append('\n');
		}
	}

	/**
	 * Create youth player data.
	 */
	private static void createYouthPlayers(List<MyHashtable> playersData, StringBuilder buffer) {

		for (var player: playersData ) {

			buffer.append("[youthplayer").append(player.get("YouthPlayerID")).append(']').append('\n');
			buffer.append("Name=").append(createPlayername(player)).append('\n');
			createHRFLine(buffer, player, "Age");
			createHRFLine(buffer, player, "AgeDays");
			createHRFLine(buffer, player, "ArrivalDate");
			createHRFLine(buffer, player, "CanBePromotedIn");
			createHRFLine(buffer, player, "PlayerNumber");
			createHRFLine(buffer, player, "Statement");
			createHRFLine(buffer, player, "OwnerNotes");
			createHRFLine(buffer, player, "PlayerCategoryID");

			createHRFLine(buffer, player, "Cards");
			createHRFLine(buffer, player, "InjuryLevel");
			createHRFLine(buffer, player, "Specialty");
			createHRFLine(buffer, player, "CareerGoals");
			createHRFLine(buffer, player, "CareerHattricks");
			createHRFLine(buffer, player, "LeagueGoals");
			createHRFLine(buffer, player, "FriendlyGoals");
			createHRFLine(buffer, player, "PlayerCategoryID");
			createHRFSkillLines(buffer, player, "KeeperSkill");
			createHRFSkillLines(buffer, player, "DefenderSkill");
			createHRFSkillLines(buffer, player, "PlaymakerSkill");
			createHRFSkillLines(buffer, player, "WingerSkill");
			createHRFSkillLines(buffer, player, "PassingSkill");
			createHRFSkillLines(buffer, player, "ScorerSkill");
			createHRFSkillLines(buffer, player, "SetPiecesSkill");

			createHRFLine(buffer, player, "ScoutId");
			createHRFLine(buffer, player, "ScoutName");
			createHRFLine(buffer, player, "ScoutingRegionID");

			for ( int i=0; createScoutComment(buffer, player, i); i++);

			createHRFLine(buffer, player, "YouthMatchID");
			createHRFLine(buffer, player, "YouthMatchDate");
			createHRFLine(buffer, player, "PositionCode");
			createHRFLine(buffer, player, "PlayedMinutes");
			createHRFLine(buffer, player, "Rating");
		}
	}

	private static boolean createScoutComment(StringBuilder buffer, MyHashtable player, int i) {
		var prefix = "ScoutComment"+i;

		var text = player.get(prefix+"Text");
		if ( text != null){
			createHRFLine(buffer, player, prefix+"Text");
			createHRFLine(buffer, player, prefix+"Type");
			createHRFLine(buffer, player, prefix+"Variation");
			createHRFLine(buffer, player, prefix+"SkillType");
			createHRFLine(buffer, player, prefix+"SkillLevel");
			return true;
		}
		return false;
	}

	private static void createHRFSkillLines(StringBuilder buffer, MyHashtable player, String skill) {
		createHRFLine(buffer, player, skill);
		createHRFLine(buffer, player, skill+"IsAvailable");
		createHRFLine(buffer, player, skill+"IsMaxReached");
		createHRFLine(buffer, player, skill+"MayUnlock");
		createHRFLine(buffer, player, skill+"Max");
		createHRFLine(buffer, player, skill+"MaxIsAvailable");
	}

	private static void createHRFLine(StringBuilder buffer, MyHashtable player, String key) {
		buffer.append(key).append("=").append(player.get(key)).append('\n');
	}

	private static String createPlayername( MyHashtable player) {
		String name = player.get("FirstName");
		StringBuilder builder = new StringBuilder(name);
		String nickname = player.get("NickName");
		if (nickname.length() > 0) {
			if (name.length() > 0) builder.append(" ");
			builder.append("'" + nickname + "'");
		}
		String lastname = player.get("LastName");
		if (lastname.length() > 0) {
			if (name.length() > 0 || nickname.length()>0) builder.append(" ");
			builder.append(lastname);
		}
		return builder.toString();
	}

	/**
	 * Create team related data (training, confidence, formation experience,
	 * etc.).
	 */
	private static void createTeam(Map<String, String> trainingDataMap,
			StringBuilder buffer) {
		buffer.append("[team]" + "\n");
		buffer.append("trLevel=").append(trainingDataMap.get("TrainingLevel"))
				.append('\n');
		buffer.append("staminaTrainingPart=")
				.append(trainingDataMap.get("StaminaTrainingPart"))
				.append('\n');
		buffer.append("trTypeValue=")
				.append(trainingDataMap.get("TrainingType")).append('\n');
		buffer.append("trType=")
				.append(TrainingType.toString(Integer.parseInt(trainingDataMap
						.get("TrainingType").toString()))).append('\n');

		if ((trainingDataMap.get("Morale") != null)
				&& (trainingDataMap.get("SelfConfidence") != null)) {
			buffer.append("stamningValue=")
					.append(trainingDataMap.get("Morale")).append('\n');
			buffer.append("stamning=")
					.append(TeamSpirit.toString(Integer
							.parseInt(trainingDataMap.get("Morale").toString())))
					.append('\n');
			buffer.append("sjalvfortroendeValue=")
					.append(trainingDataMap.get("SelfConfidence")).append('\n');
			buffer.append("sjalvfortroende=")
					.append(TeamConfidence.toString(Integer
							.parseInt(trainingDataMap.get("SelfConfidence")
									.toString()))).append('\n');
		} else {
			buffer.append("playingMatch=true");
		}

		buffer.append("exper433=").append(trainingDataMap.get("Experience433"))
				.append('\n');
		buffer.append("exper451=").append(trainingDataMap.get("Experience451"))
				.append('\n');
		buffer.append("exper352=").append(trainingDataMap.get("Experience352"))
				.append('\n');
		buffer.append("exper532=").append(trainingDataMap.get("Experience532"))
				.append('\n');
		buffer.append("exper343=").append(trainingDataMap.get("Experience343"))
				.append('\n');
		buffer.append("exper541=").append(trainingDataMap.get("Experience541"))
				.append('\n');
		if (trainingDataMap.get("Experience442") != null) {
			buffer.append("exper442=")
					.append(trainingDataMap.get("Experience442")).append('\n');
		}
		if (trainingDataMap.get("Experience523") != null) {
			buffer.append("exper523=")
					.append(trainingDataMap.get("Experience523")).append('\n');
		}
		if (trainingDataMap.get("Experience550") != null) {
			buffer.append("exper550=")
					.append(trainingDataMap.get("Experience550")).append('\n');
		}
		if (trainingDataMap.get("Experience253") != null) {
			buffer.append("exper253=")
					.append(trainingDataMap.get("Experience253")).append('\n');
		}
	}

	/**
	 * Create the world data.
	 */
	private static void createWorld(Map<String, String> clubDataMap,
			Map<String, String> teamdetailsDataMap, Map<String, String> trainingDataMap,
			Map<String, String> worldDataMap, StringBuilder buffer) {
		buffer.append("[xtra]\n");
		buffer.append("TrainingDate=").append(worldDataMap.get("TrainingDate"))
				.append('\n');
		buffer.append("EconomyDate=").append(worldDataMap.get("EconomyDate"))
				.append('\n');
		buffer.append("SeriesMatchDate=")
				.append(worldDataMap.get("SeriesMatchDate")).append('\n');
		buffer.append("CurrencyRate=")
				.append(worldDataMap.get("CurrencyRate").toString()
						.replace(',', '.')).append('\n');
		buffer.append("LogoURL=").append(teamdetailsDataMap.get("LogoURL"))
				.append('\n');
		buffer.append("HasPromoted=").append(clubDataMap.get("HasPromoted"))
				.append('\n');

		buffer.append("TrainerID=").append(trainingDataMap.get("TrainerID"))
				.append('\n');
		buffer.append("TrainerName=")
				.append(trainingDataMap.get("TrainerName")).append('\n');
		buffer.append("ArrivalDate=")
				.append(trainingDataMap.get("ArrivalDate")).append('\n');
		buffer.append("LeagueLevelUnitID=")
				.append(teamdetailsDataMap.get("LeagueLevelUnitID"))
				.append('\n');
	}

	private static void createStaff(List<MyHashtable> staffList, StringBuilder buffer) {
		
		buffer.append("[staff]").append('\n');
		
		for (int i = 0; (staffList != null) && (i < staffList.size()); i++) {
			MyHashtable hash = staffList.get(i);
			
			buffer.append("staff").append(i).append("Name=").append(hash.get("Name")).append('\n');
			buffer.append("staff").append(i).append("StaffId=").append(hash.get("StaffId")).append('\n');
			buffer.append("staff").append(i).append("StaffType=").append(hash.get("StaffType")).append('\n');
			buffer.append("staff").append(i).append("StaffLevel=").append(hash.get("StaffLevel")).append('\n');
			buffer.append("staff").append(i).append("Cost=").append(hash.get("Cost")).append('\n');
		}
	}

    /**
	 * Save the HRF file.
	 */
	private void writeHRF(String dateiname, StringBuilder buffer) {
		BufferedWriter out = null;

		try {
			File f = new File(dateiname);

			if (f.exists()) {
				f.delete();
			}

			f.createNewFile();

			// write utf 8
			OutputStreamWriter outWrit = new OutputStreamWriter(
					new FileOutputStream(f), "UTF-8");
			out = new BufferedWriter(outWrit);

			// write ansi
			out.write(buffer.toString());
		} catch (Exception except) {
			HOLogger.instance().log(getClass(), except);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
