package module.nthrf;

import core.constants.TeamConfidence;
import core.constants.TeamSpirit;
import core.constants.TrainingType;
import core.constants.player.PlayerAggressiveness;
import core.constants.player.PlayerAgreeability;
import core.constants.player.PlayerHonesty;
import core.constants.player.PlayerSpeciality;
import core.file.xml.XMLManager;
import core.file.xml.XMLMatchArchivParser;
import core.file.xml.XMLWorldDetailsParser;
import core.model.match.MatchKurzInfo;
import core.net.MyConnector;
import core.util.HOLogger;
import core.util.HelperWrapper;
import core.HO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class NthrfConvertXml2Hrf {
	private StringBuffer m_sHRFBuffer;
	private long teamId = 0;
	private HelperWrapper helper;

	/**
	 * Create the HRF file.
	 */
	final String createHrf(long teamId, MyConnector dh) throws Exception {
		m_sHRFBuffer = new StringBuffer("");
		helper = HelperWrapper.instance();
		this.teamId = teamId;
		try {
			debug("About to load data for teamId " + teamId);
			// leagueId / countryId
			HashMap<Integer, Integer> countryMapping = getCountryMapping(dh);
			debug("Got " + (countryMapping != null ? countryMapping.keySet().size() : "null") + " country mappings.");

			// nt team detail
			String xml = dh.getHattrickXMLFile("/chppxml.axd?file=nationalteamdetails&teamid=" + teamId);
			NtTeamDetailsParser details = new NtTeamDetailsParser(xml);
			debug("Got team details");

			// world details
			xml = dh.getHattrickXMLFile("/chppxml.axd?file=worlddetails");
			Map<String, String> world = XMLWorldDetailsParser.parseWorldDetailsFromString(xml, String.valueOf(details.getLeagueId()));
			debug("Got world details");

			// nt players + player details
			xml = dh.getHattrickXMLFile("/chppxml.axd?file=nationalplayers&teamid=" + teamId);
			NtPlayersParser players = new NtPlayersParser(xml, dh, countryMapping);
			NtPlayer trainer = NthrfUtil.getTrainer(players);
			debug("Got " + ((players != null && players.getPlayerIds() != null) ? players.getPlayerIds().size() : "null") + " players and trainer");

			// nt matches
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTimeInMillis(System.currentTimeMillis());
			xml = dh.getHattrickXMLFile("/chppxml.axd?file=matches&teamID=" + teamId + "&LastMatchDate=" + new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
			List<MatchKurzInfo> matches = XMLMatchArchivParser.parseMatchesFromString(xml);

			// lineup
			NtLineupParser lineup = null;
			if (matches.size() > 0) {
				xml = dh.getHattrickXMLFile("/chppxml.axd?file=matchlineup&version=2.0&matchID=" + matches.get(matches.size() - 1).getMatchID() + "&teamID=" + teamId);
				lineup = new NtLineupParser(xml);
				debug("Got lineup");
			}

			createBasics(details, players); // ok, TODO
			debug("created basics");
			createLeague();					// ok
			debug("created league");
			createClub(details);			// ok
			debug("created club");
			createTeam(details);			// ok
			debug("created team details");
			if (lineup != null)
				createLineUp(trainer, lineup);	// ok, TODO
			debug("created lineup");
			createEconomy(); 				// ok, TODO
			debug("created economy");
			createArena(details);			// ok
			debug("created arena");
			createPlayers(players);			// ok
			debug("created players");
			createWorld(world, details, trainer);	// ok, TODO
			debug("created world");
			if (lineup != null)
				createLastLineUp(trainer, lineup);		//ok, TODO
			debug("created last lineup");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		return m_sHRFBuffer.toString();
	}

	/**
	 * Erstellt die Arena Daten
	 */
	final void createArena(NtTeamDetailsParser details) throws Exception {
		m_sHRFBuffer.append("[arena]" + "\n");
		m_sHRFBuffer.append("arenaname=" + details.getTeamName() + " Arena\n");
		m_sHRFBuffer.append("arenaid=0\n"); 		// ArenaID
		m_sHRFBuffer.append("antalStaplats=1000\n");// Terraces
		m_sHRFBuffer.append("antalSitt=1000\n");	// Basic
		m_sHRFBuffer.append("antalTak=1000\n");		// Roof
		m_sHRFBuffer.append("antalVIP=1000\n");		// VIP
		m_sHRFBuffer.append("seatTotal=4000\n");	// Total
		m_sHRFBuffer.append("expandingStaplats=0\n");
		m_sHRFBuffer.append("expandingSitt=0\n");
		m_sHRFBuffer.append("expandingTak=0\n");
		m_sHRFBuffer.append("expandingVIP=0\n");
		m_sHRFBuffer.append("expandingSseatTotal=0\n");
		m_sHRFBuffer.append("isExpanding=0\n");
		m_sHRFBuffer.append("ExpansionDate=0\n");
	}

	/**
	 * basic data
	 */
	final void createBasics(NtTeamDetailsParser details, NtPlayersParser players) throws Exception {
		m_sHRFBuffer.append("[basics]\n");
		m_sHRFBuffer.append("application=HO\n");
		m_sHRFBuffer.append("appversion=" + HO.VERSION + "\n");
		m_sHRFBuffer.append("date=" + details.getFetchedDate() + "\n");
		m_sHRFBuffer.append("season=" + "38" + "\n"); 		//TODO: Season
		m_sHRFBuffer.append("matchround=" + "7" + "\n"); 	//TODO: MatchRound
		m_sHRFBuffer.append("teamID=" + details.getTeamId() + "\n");
		m_sHRFBuffer.append("teamName=" + details.getTeamName() + "\n");
		m_sHRFBuffer.append("activationDate=0\n");
		m_sHRFBuffer.append("owner=" + details.getCoachName() + "\n");
		m_sHRFBuffer.append("ownerEmail=0\n");
		m_sHRFBuffer.append("ownerICQ=0\n");
		m_sHRFBuffer.append("ownerHomepage=" + details.getHomePageUrl() + "\n");
		m_sHRFBuffer.append("countryID=" + (players.getAllPlayers().get(0)).getCountryId() + "\n");
		m_sHRFBuffer.append("leagueID=" + details.getLeagueId() + "\n");
		m_sHRFBuffer.append("regionID=0\n");
	}

	/**
	 * Club Data
	 */
	final void createClub(NtTeamDetailsParser details) throws Exception {
		m_sHRFBuffer.append("[club]\n");
		m_sHRFBuffer.append("hjTranare=0\n");		// AssistantTrainers
		m_sHRFBuffer.append("psykolog=0\n");		// Psychologists
		m_sHRFBuffer.append("presstalesman=0\n");	// PressSpokesmen
		m_sHRFBuffer.append("massor=0\n");			// Physiotherapists
		m_sHRFBuffer.append("lakare=0\n");			// Doctors
		m_sHRFBuffer.append("juniorverksamhet=0\n");// YouthLevel
		m_sHRFBuffer.append("undefeated=0\n");		// NumberOfUndefeated
		m_sHRFBuffer.append("victories=0\n");		// NumberOfVictories
		m_sHRFBuffer.append("fanclub=" + details.getFanclubSize() + "\n"); // FanClubSize
	}

	/**
	 * Erstellt die Econemy Daten
	 */
	final void createEconomy() throws Exception {
		m_sHRFBuffer.append("[economy]" + "\n");

		if (false) {
			m_sHRFBuffer.append("supporters=1\n");// TODO
			m_sHRFBuffer.append("sponsors=1\n");
		} else {
			m_sHRFBuffer.append("playingMatch=true");
		}

		m_sHRFBuffer.append("cash=0\n");
		m_sHRFBuffer.append("IncomeSponsorer=0\n");
		m_sHRFBuffer.append("incomePublik=0\n");
		m_sHRFBuffer.append("incomeFinansiella=0\n");
		m_sHRFBuffer.append("incomeTillfalliga=0\n");
		m_sHRFBuffer.append("incomeSumma=0\n");
		m_sHRFBuffer.append("costsSpelare=0\n");
		m_sHRFBuffer.append("costsPersonal=0\n");
		m_sHRFBuffer.append("costsArena=0\n");
		m_sHRFBuffer.append("costsJuniorverksamhet=0\n");
		m_sHRFBuffer.append("costsRantor=0\n");
		m_sHRFBuffer.append("costsTillfalliga=0\n");
		m_sHRFBuffer.append("costsSumma=0\n");
		m_sHRFBuffer.append("total=0\n");
		m_sHRFBuffer.append("lastIncomeSponsorer=0\n");
		m_sHRFBuffer.append("lastIncomePublik=0\n");
		m_sHRFBuffer.append("lastIncomeFinansiella=0\n");
		m_sHRFBuffer.append("lastIncomeTillfalliga=0\n");
		m_sHRFBuffer.append("lastIncomeSumma=0\n");
		m_sHRFBuffer.append("lastCostsSpelare=0\n");
		m_sHRFBuffer.append("lastCostsPersonal=0\n");
		m_sHRFBuffer.append("lastCostsArena=0\n");
		m_sHRFBuffer.append("lastCostsJuniorverksamhet=0\n");
		m_sHRFBuffer.append("lastCostsRantor=0\n");
		m_sHRFBuffer.append("lastCostsTillfalliga=0\n");
		m_sHRFBuffer.append("lastCostsSumma=0\n");
		m_sHRFBuffer.append("lastTotal=0\n");
	}

	final void createLastLineUp(NtPlayer trainer, NtLineupParser lineup) {
		m_sHRFBuffer.append("[lastlineup]" + "\n");

		try {
			// IMatchRoleID.INNENVERTEIDIGER
			NtPlayerPosition p1 = NthrfUtil.getPlayerPositionByRole(lineup, 1);
			NtPlayerPosition p2 = NthrfUtil.getPlayerPositionByRole(lineup, 2);
			NtPlayerPosition p3 = NthrfUtil.getPlayerPositionByRole(lineup, 3);
			NtPlayerPosition p4 = NthrfUtil.getPlayerPositionByRole(lineup, 4);
			NtPlayerPosition p5 = NthrfUtil.getPlayerPositionByRole(lineup, 5);
			NtPlayerPosition p6 = NthrfUtil.getPlayerPositionByRole(lineup, 6);
			NtPlayerPosition p7 = NthrfUtil.getPlayerPositionByRole(lineup, 7);
			NtPlayerPosition p8 = NthrfUtil.getPlayerPositionByRole(lineup, 8);
			NtPlayerPosition p9 = NthrfUtil.getPlayerPositionByRole(lineup, 9);
			NtPlayerPosition p10 = NthrfUtil.getPlayerPositionByRole(lineup, 10);
			NtPlayerPosition p11 = NthrfUtil.getPlayerPositionByRole(lineup, 11);
			NtPlayerPosition p12 = NthrfUtil.getPlayerPositionByRole(lineup, 12);
			NtPlayerPosition p13 = NthrfUtil.getPlayerPositionByRole(lineup, 13);
			NtPlayerPosition p14 = NthrfUtil.getPlayerPositionByRole(lineup, 14);
			NtPlayerPosition p15 = NthrfUtil.getPlayerPositionByRole(lineup, 15);
			NtPlayerPosition p16 = NthrfUtil.getPlayerPositionByRole(lineup, 16);
			NtPlayerPosition p17 = NthrfUtil.getPlayerPositionByRole(lineup, 17);
			NtPlayerPosition p18 = NthrfUtil.getPlayerPositionByRole(lineup, 18);

			m_sHRFBuffer.append("trainer=" + trainer.getPlayerId() + "\n");
			m_sHRFBuffer.append("installning=0\n");		// TODO: Attitude (pic/mots/norm)
			m_sHRFBuffer.append("tactictype=0\n");		// TODO: tactic (AoW/AiM/CA/...)
			m_sHRFBuffer.append("keeper=" + (p1 != null ? ""+p1.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("rightBack=" + (p2 != null ? ""+p2.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("insideBack1=" + (p3 != null ? ""+p3.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("insideBack2=" + (p4 != null ? ""+p4.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("leftBack=" + (p5 != null ? ""+p5.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("rightWinger=" + (p6 != null ? ""+p6.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("insideMid1=" + (p7 != null ? ""+p7.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("insideMid2=" + (p8 != null ? ""+p8.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("leftWinger=" + (p9 != null ? ""+p9.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("forward1=" + (p10 != null ? ""+p10.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("forward2=" + (p11 != null ? ""+p11.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("substBack=" + (p13 != null ? ""+p13.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("substInsideMid=" + (p14 != null ? ""+p14.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("substWinger=" + (p15 != null ? ""+p15.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("substKeeper=" + (p12 != null ? ""+p12.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("substForward=" + (p16 != null ? ""+p16.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("captain=" + (p18 != null ? ""+p18.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("kicker1=" + (p17 != null ? ""+p17.getPlayerId() : "") + "\n");

			m_sHRFBuffer.append("behRightBack=" + (p2 != null ? ""+p2.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behInsideBack1=" + (p3 != null ? ""+p3.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behInsideBack2=" + (p4 != null ? ""+p4.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behLeftBack=" + (p5 != null ? ""+p5.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behRightWinger=" + (p6 != null ? ""+p6.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behInsideMid1=" + (p7 != null ? ""+p7.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behInsideMid2=" + (p8 != null ? ""+p8.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behLeftWinger=" + (p9 != null ? ""+p9.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behForward1=" + (p10 != null ? ""+p10.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behForward2=" + (p11 != null ? ""+p11.getBehaviour() : "0") + "\n");
		} catch (Exception e) {
		}
	}

	/**
	 * Erstellt die Liga Daten
	 */
	final void createLeague() throws Exception {
		m_sHRFBuffer.append("[league]\n");
		m_sHRFBuffer.append("serie=I.1\n");
		m_sHRFBuffer.append("spelade=0\n");		// Matches
		m_sHRFBuffer.append("gjorda=0\n");		// GoalsFor
		m_sHRFBuffer.append("inslappta=0\n");	// GoalsAgainst
		m_sHRFBuffer.append("poang=0\n");		// Points
		m_sHRFBuffer.append("placering=1\n");	// Position
	}

	/**
	 * Erstellt die LineUp Daten
	 */
	final void createLineUp(NtPlayer trainer, NtLineupParser lineup) throws Exception {
		m_sHRFBuffer.append("[lineup]" + "\n");

		try {
			// IMatchRoleID.INNENVERTEIDIGER
			NtPlayerPosition p1 = NthrfUtil.getPlayerPositionByRole(lineup, 1);
			NtPlayerPosition p2 = NthrfUtil.getPlayerPositionByRole(lineup, 2);
			NtPlayerPosition p3 = NthrfUtil.getPlayerPositionByRole(lineup, 3);
			NtPlayerPosition p4 = NthrfUtil.getPlayerPositionByRole(lineup, 4);
			NtPlayerPosition p5 = NthrfUtil.getPlayerPositionByRole(lineup, 5);
			NtPlayerPosition p6 = NthrfUtil.getPlayerPositionByRole(lineup, 6);
			NtPlayerPosition p7 = NthrfUtil.getPlayerPositionByRole(lineup, 7);
			NtPlayerPosition p8 = NthrfUtil.getPlayerPositionByRole(lineup, 8);
			NtPlayerPosition p9 = NthrfUtil.getPlayerPositionByRole(lineup, 9);
			NtPlayerPosition p10 = NthrfUtil.getPlayerPositionByRole(lineup, 10);
			NtPlayerPosition p11 = NthrfUtil.getPlayerPositionByRole(lineup, 11);
			NtPlayerPosition p12 = NthrfUtil.getPlayerPositionByRole(lineup, 12);
			NtPlayerPosition p13 = NthrfUtil.getPlayerPositionByRole(lineup, 13);
			NtPlayerPosition p14 = NthrfUtil.getPlayerPositionByRole(lineup, 14);
			NtPlayerPosition p15 = NthrfUtil.getPlayerPositionByRole(lineup, 15);
			NtPlayerPosition p16 = NthrfUtil.getPlayerPositionByRole(lineup, 16);
			NtPlayerPosition p17 = NthrfUtil.getPlayerPositionByRole(lineup, 17);
			NtPlayerPosition p18 = NthrfUtil.getPlayerPositionByRole(lineup, 18);

			m_sHRFBuffer.append("trainer=" + trainer.getPlayerId() + "\n");
			m_sHRFBuffer.append("installning=0\n");		// TODO: Attitude (pic/mots/norm)
			m_sHRFBuffer.append("tactictype=0\n");		// TODO: tactic (AoW/AiM/CA/...)
			m_sHRFBuffer.append("keeper=" + (p1 != null ? ""+p1.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("rightBack=" + (p2 != null ? ""+p2.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("insideBack1=" + (p3 != null ? ""+p3.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("insideBack2=" + (p4 != null ? ""+p4.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("leftBack=" + (p5 != null ? ""+p5.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("rightWinger=" + (p6 != null ? ""+p6.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("insideMid1=" + (p7 != null ? ""+p7.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("insideMid2=" + (p8 != null ? ""+p8.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("leftWinger=" + (p9 != null ? ""+p9.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("forward1=" + (p10 != null ? ""+p10.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("forward2=" + (p11 != null ? ""+p11.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("substBack=" + (p13 != null ? ""+p13.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("substInsideMid=" + (p14 != null ? ""+p14.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("substWinger=" + (p15 != null ? ""+p15.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("substKeeper=" + (p12 != null ? ""+p12.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("substForward=" + (p16 != null ? ""+p16.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("captain=" + (p18 != null ? ""+p18.getPlayerId() : "") + "\n");
			m_sHRFBuffer.append("kicker1=" + (p17 != null ? ""+p17.getPlayerId() : "") + "\n");

			m_sHRFBuffer.append("behRightBack=" + (p2 != null ? ""+p2.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behInsideBack1=" + (p3 != null ? ""+p3.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behInsideBack2=" + (p4 != null ? ""+p4.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behLeftBack=" + (p5 != null ? ""+p5.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behRightWinger=" + (p6 != null ? ""+p6.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behInsideMid1=" + (p7 != null ? ""+p7.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behInsideMid2=" + (p8 != null ? ""+p8.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behLeftWinger=" + (p9 != null ? ""+p9.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behForward1=" + (p10 != null ? ""+p10.getBehaviour() : "0") + "\n");
			m_sHRFBuffer.append("behForward2=" + (p11 != null ? ""+p11.getBehaviour() : "0") + "\n");
		} catch (Exception e) {
		}
	}

	/**
	 * Erstellt die Player Daten
	 */
	final void createPlayers(NtPlayersParser players) throws Exception {
		List<NtPlayer> pls = players.getAllPlayers();

		for (Iterator<NtPlayer> i = pls.iterator(); i.hasNext(); ) {
			NtPlayer pl = i.next();

			m_sHRFBuffer.append("[player" + pl.getPlayerId() + "]" + "\n");
			m_sHRFBuffer.append("name=" + pl.getName() + "\n");
			m_sHRFBuffer.append("firstname=" + pl.getFirstName() + "\n");
			m_sHRFBuffer.append("nickname=" + pl.getNickName() + "\n");
			m_sHRFBuffer.append("lastname=" + pl.getLastName() + "\n");
			m_sHRFBuffer.append("ald=" + pl.getAgeYears() + "\n");
			m_sHRFBuffer.append("agedays=" + pl.getAgeDays() + "\n");
			m_sHRFBuffer.append("ska=" + pl.getInjury() + "\n");
			m_sHRFBuffer.append("for=" + pl.getForm() + "\n");
			m_sHRFBuffer.append("uth=" + pl.getStaminaSkill() + "\n");
			m_sHRFBuffer.append("spe=" + pl.getPlaymakerSkill() + "\n");
			m_sHRFBuffer.append("mal=" + pl.getScorerSkill() + "\n");
			m_sHRFBuffer.append("fra=" + pl.getPassingSkill() + "\n");
			m_sHRFBuffer.append("ytt=" + pl.getWingerSkill() + "\n");
			m_sHRFBuffer.append("fas=" + pl.getSetPiecesSkill() + "\n");
			m_sHRFBuffer.append("bac=" + pl.getDefenderSkill() + "\n");
			m_sHRFBuffer.append("mlv=" + pl.getKeeperSkill() + "\n");
			m_sHRFBuffer.append("rut=" + pl.getXp() + "\n");
			m_sHRFBuffer.append("led=" + pl.getLeaderShip() + "\n");
			m_sHRFBuffer.append("sal=" + pl.getSalary() + "\n");
			m_sHRFBuffer.append("mkt=" + pl.getTsi() + "\n");
			m_sHRFBuffer.append("gev=" + pl.getCareerGoals() + "\n");
			m_sHRFBuffer.append("gtl=" + pl.getLeagueGoals() + "\n");
			m_sHRFBuffer.append("gtc=" + "0" + "\n"); // CupGoals
			m_sHRFBuffer.append("gtt=" + "0" + "\n"); // FriendliesGoals
			m_sHRFBuffer.append("hat=" + pl.getCareerHattricks() + "\n");
			m_sHRFBuffer.append("CountryID=" + pl.getCountryId() + "\n");
			m_sHRFBuffer.append("warnings=" + pl.getCards() + "\n");
			m_sHRFBuffer.append("speciality=" + pl.getSpeciality() + "\n");
			m_sHRFBuffer.append("specialityLabel="+ PlayerSpeciality.toString(pl.getSpeciality())+ "\n");
			m_sHRFBuffer.append("gentleness=" + pl.getAgreeability() + "\n");
			m_sHRFBuffer.append("gentlenessLabel="+ PlayerAgreeability.toString(pl.getAgreeability())+ "\n");
			m_sHRFBuffer.append("honesty=" + pl.getHonesty() + "\n");
			m_sHRFBuffer.append("honestyLabel=" + PlayerHonesty.toString(pl.getHonesty())+ "\n");
			m_sHRFBuffer.append("Aggressiveness=" + pl.getAggressiveness() + "\n");
			m_sHRFBuffer.append("AggressivenessLabel="+ PlayerAggressiveness.toString(pl.getAggressiveness())+ "\n");

			if (pl.isTrainer()) {
				m_sHRFBuffer.append("TrainerType=" + pl.getTrainerType() + "\n");
				m_sHRFBuffer.append("TrainerSkill=" + pl.getTrainerSkill() + "\n");
			} else {
				m_sHRFBuffer.append("TrainerType=" + "\n");
				m_sHRFBuffer.append("TrainerSkill=" + "\n");
			}

			// TODO: rating
			//if ((m_clTeam != null)
			//		&& (m_clTeam.getPlayerByID(Integer.parseInt(ht.get("PlayerID").toString())) != null)
			//		&& (m_clTeam.getPlayerByID(Integer.parseInt(ht.get("PlayerID").toString())).getRating() >= 0)) {
			//	m_sHRFBuffer.append("rating=" + (int) (m_clTeam.getPlayerByID(Integer.parseInt(ht.get("PlayerID").toString())) .getRating() * 2) + "\n");
			//} else {
				m_sHRFBuffer.append("rating=0" + "\n");
			//}

			//Bonus
			if (pl.getShirtNumber() > 0) {
				m_sHRFBuffer.append("PlayerNumber=" + pl.getShirtNumber() + "\n");
			}

			m_sHRFBuffer.append("TransferListed=" + pl.getTranferlisted() + "\n");
			m_sHRFBuffer.append("NationalTeamID=" + teamId + "\n");
			m_sHRFBuffer.append("Caps=" + pl.getCaps() + "\n");
			m_sHRFBuffer.append("CapsU20=" + pl.getCapsU20() + "\n");
		}
	}

	/**
	 * Erstellt die Team Daten
	 */
	final void createTeam(NtTeamDetailsParser details) throws Exception {
		m_sHRFBuffer.append("[team]" + "\n");
		m_sHRFBuffer.append("trLevel=100\n");			// TrainingLevel
		m_sHRFBuffer.append("staminaTrainingPart=5\n"); //StaminaTrainingPart
		m_sHRFBuffer.append("trTypeValue=8\n");			// TrainingType
		m_sHRFBuffer.append("trType=" +  TrainingType.toString(8) + "\n");

		// TODO: imports de.hattrickorganizer.model.Team. (get though helper from Team)

		if (details.getMorale()>-1 && details.getSelfConfidence()>-1) {
			m_sHRFBuffer.append("stamningValue=" + details.getMorale() + "\n");
			try {
				m_sHRFBuffer.append("stamning=" + TeamSpirit.toString(details.getMorale()) + "\n");
			} catch (Exception e) {
				System.out.println("Cant get text for morale " + details.getMorale() + "\n" + e);
				m_sHRFBuffer.append("stamning=\n");
			}
			m_sHRFBuffer.append("sjalvfortroendeValue=" + details.getSelfConfidence() + "\n");
			try {
				m_sHRFBuffer.append("sjalvfortroende="+  TeamConfidence.toString(details.getSelfConfidence()) + "\n");
			} catch (Exception e) {
				System.out.println("Cant get text for self confidence " + details.getMorale() + "\n" + e);
				m_sHRFBuffer.append("sjalvfortroende=\n");
			}
		} else {
			m_sHRFBuffer.append("playingMatch=true");
		}

		try {
			m_sHRFBuffer.append("exper433=" + details.getXp433() + "\n");
			m_sHRFBuffer.append("exper451=" + details.getXp451() + "\n");
			m_sHRFBuffer.append("exper352=" + details.getXp352() + "\n");
			m_sHRFBuffer.append("exper532=" + details.getXp532() + "\n");
			m_sHRFBuffer.append("exper343=" + details.getXp343() + "\n");
			m_sHRFBuffer.append("exper541=" + details.getXp541() + "\n");
		} catch (Exception e) {
			System.out.println("Cant get text for self confidence " + e);
			m_sHRFBuffer.append("exper433=7\n");
			m_sHRFBuffer.append("exper451=7\n");
			m_sHRFBuffer.append("exper352=7\n");
			m_sHRFBuffer.append("exper532=7\n");
			m_sHRFBuffer.append("exper343=7\n");
			m_sHRFBuffer.append("exper541=7\n");
		}
	}

	/**
	 * Creates the world details for the current user / country.
	 */
	final void createWorld(Map<String, String> world, NtTeamDetailsParser details, NtPlayer trainer) throws Exception {
		m_sHRFBuffer.append("[xtra]\n");

		m_sHRFBuffer.append("TrainingDate=" + world.get("TrainingDate") + "\n");
		m_sHRFBuffer.append("EconomyDate=" + world.get("EconomyDate") + "\n");
		m_sHRFBuffer.append("SeriesMatchDate=" + world.get("SeriesMatchDate") + "\n");
//		m_sHRFBuffer.append("CurrencyRate=" + world.get("CurrencyRate").toString().replace(',', '.') + "\n");
		m_sHRFBuffer.append("CurrencyRate=10\n");
		
		m_sHRFBuffer.append("LogoURL=" + details.getHomePageUrl() + "\n");
		m_sHRFBuffer.append("HasPromoted=False\n"); // HasPromoted

		m_sHRFBuffer.append("TrainerID=" + (trainer!=null ? ""+trainer.getPlayerId() : "") + "\n");
		m_sHRFBuffer.append("TrainerName=" + (trainer!=null ? trainer.getName() : "") + "\n");
		m_sHRFBuffer.append("ArrivalDate=2009-01-01 03:33:33\n"); // TODO: trainer ArrivalDate
		m_sHRFBuffer.append("LeagueLevelUnitID=1\n"); // TODO: LeagueLevelUnitID
	}

	/**
	 * Parse all leagues and (nativeLeagueId) and their countryId.
	 */
	HashMap<Integer, Integer> getCountryMapping(MyConnector dh) {
		HashMap<Integer, Integer> ret = new HashMap<Integer, Integer>(100);
		try {
			String str = getWorldDetailString(dh);
			if (str == null || str.equals("")) {
				return ret;
			}
			Document doc = XMLManager.parseString(str);

			Element ele = null;
			Element root = null;
			NodeList list = null;
			if (doc == null) {
				return ret;
			}
			root = doc.getDocumentElement();
			root = (Element) root.getElementsByTagName("LeagueList").item(0);
			list = root.getElementsByTagName("League");
			for (int i = 0; (list != null) && (i < list.getLength()); i++) {
				root = (Element) list.item(i);
				ele = (Element) root.getElementsByTagName("LeagueID").item(0);
				String leagueID = XMLManager.getFirstChildNodeValue(ele);
				root = (Element) root.getElementsByTagName("Country").item(0);
				ele = (Element) root.getElementsByTagName("CountryID").item(0);
				String countryID = XMLManager.getFirstChildNodeValue(ele);
				ret.put(new Integer(Integer.parseInt(leagueID)), new Integer(Integer.parseInt(countryID)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
    }

    private String getWorldDetailString(MyConnector dh) throws Exception {
        return dh.getHattrickXMLFile("/chppxml.axd?file=worlddetails");
    }

	/**
	 * Save the hrf buffer to a file.
	 */
    final void writeHRF(String filename) {
    	File file = new File(filename);
    	writeHRF(file);
    }

	/**
	 * Save the hrf buffer into a file.
	 */
	final void writeHRF(File file) {
		debug("Create NT HRF file: " + (file != null ? file.getAbsolutePath() : "null"));
		if (file == null) return;

		BufferedWriter out = null;
		final String text = m_sHRFBuffer.toString();
		//utf-8
		OutputStreamWriter outWrit = null;

		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			outWrit = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			out = new BufferedWriter(outWrit);
			if (text != null) {
				out.write(text);
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void debug(String txt) {
		HOLogger.instance().debug(this.getClass(), txt);
	}
}
