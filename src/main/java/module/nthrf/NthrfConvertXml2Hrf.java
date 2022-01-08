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
import core.util.HTDatetime;
import core.util.HelperWrapper;
import core.HO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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
		m_sHRFBuffer = new StringBuffer();
		helper = HelperWrapper.instance();
		this.teamId = teamId;
		try {
			debug("About to load data for teamId " + teamId);
			// leagueId / countryId
			HashMap<Integer, Integer> countryMapping = getCountryMapping(dh);
			debug("Got " + (countryMapping != null ? countryMapping.keySet().size() : "null") + " country mappings.");

			// nt team detail
			String xml = dh.getHattrickXMLFile("/chppxml.axd?file=nationalteamdetails&version=1.9&teamid=" + teamId);
			NtTeamDetails details = new NtTeamDetails(xml);
			debug("Got team details");

			// world details
			xml = dh.getHattrickXMLFile("/chppxml.axd?file=worlddetails");
			Map<String, String> world = XMLWorldDetailsParser.parseWorldDetailsFromString(xml, String.valueOf(details.getLeagueId()));
			debug("Got world details");

			// nt players + player details
			xml = dh.getHattrickXMLFile("/chppxml.axd?file=nationalplayers&teamid=" + teamId);
			NtPlayersParser players = new NtPlayersParser(xml, dh, countryMapping);
			if (players.getAllPlayers().size() == 0 ){
				// training area closed or all players are released
				return "";
			}
			NtPlayer trainer = NthrfUtil.getTrainer(players);
			debug("Got " + (players.getPlayerIds() != null ? players.getPlayerIds().size() : "null") + " players and trainer");

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

			createBasics(details, world); // ok, TODO
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
	final void createArena(NtTeamDetails details) {
		m_sHRFBuffer.append("[arena]" + "\n");
		m_sHRFBuffer.append("arenaname=").append(details.getTeamName()).append(" Arena\n");
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
	final void createBasics(NtTeamDetails details, Map<String, String> world) {
		m_sHRFBuffer.append("[basics]\n");
		m_sHRFBuffer.append("application=HO\n");
		m_sHRFBuffer.append("appversion=").append(HO.VERSION).append("\n");
		var fetched = new HTDatetime(details.getFetchedDate());
		m_sHRFBuffer.append("date=").append(fetched.getHattrickTimeAsString()).append("\n");
		m_sHRFBuffer.append("season=" + "38" + "\n"); 		//TODO: Season
		m_sHRFBuffer.append("matchround=" + "7" + "\n"); 	//TODO: MatchRound
		m_sHRFBuffer.append("teamID=").append(details.getTeamId()).append("\n");
		m_sHRFBuffer.append("teamName=").append(details.getTeamName()).append("\n");
		m_sHRFBuffer.append("activationDate=0\n");
		m_sHRFBuffer.append("owner=").append(details.getCoachName()).append("\n");
		m_sHRFBuffer.append("ownerEmail=0\n");
		m_sHRFBuffer.append("ownerICQ=0\n");
		m_sHRFBuffer.append("ownerHomepage=").append(details.getHomePageUrl()).append("\n");
		m_sHRFBuffer.append("countryID=").append(world.get("CountryID")).append("\n");
		m_sHRFBuffer.append("leagueID=").append(details.getLeagueId()).append("\n");
		m_sHRFBuffer.append("regionID=0\n");
	}

	/**
	 * Club Data
	 */
	final void createClub(NtTeamDetails details) {
		m_sHRFBuffer.append("[club]\n");
		m_sHRFBuffer.append("hjTranare=0\n");		// AssistantTrainers
		m_sHRFBuffer.append("psykolog=0\n");		// Psychologists
		m_sHRFBuffer.append("presstalesman=0\n");	// PressSpokesmen
		m_sHRFBuffer.append("massor=0\n");			// Physiotherapists
		m_sHRFBuffer.append("lakare=0\n");			// Doctors
		m_sHRFBuffer.append("juniorverksamhet=0\n");// YouthLevel
		m_sHRFBuffer.append("undefeated=0\n");		// NumberOfUndefeated
		m_sHRFBuffer.append("victories=0\n");		// NumberOfVictories
		m_sHRFBuffer.append("fanclub=").append(details.getFanclubSize()).append("\n"); // FanClubSize
	}

	/**
	 * Erstellt die Econemy Daten
	 */
	final void createEconomy() {
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

			m_sHRFBuffer.append("trainer=").append(trainer.getPlayerId()).append("\n");
			m_sHRFBuffer.append("installning=0\n");		// TODO: Attitude (pic/mots/norm)
			m_sHRFBuffer.append("tactictype=0\n");		// TODO: tactic (AoW/AiM/CA/...)
			m_sHRFBuffer.append("keeper=").append(p1 != null ? "" + p1.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("rightBack=").append(p2 != null ? "" + p2.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("insideBack1=").append(p3 != null ? "" + p3.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("insideBack2=").append(p4 != null ? "" + p4.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("leftBack=").append(p5 != null ? "" + p5.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("rightWinger=").append(p6 != null ? "" + p6.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("insideMid1=").append(p7 != null ? "" + p7.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("insideMid2=").append(p8 != null ? "" + p8.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("leftWinger=").append(p9 != null ? "" + p9.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("forward1=").append(p10 != null ? "" + p10.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("forward2=").append(p11 != null ? "" + p11.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("substBack=").append(p13 != null ? "" + p13.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("substInsideMid=").append(p14 != null ? "" + p14.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("substWinger=").append(p15 != null ? "" + p15.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("substKeeper=").append(p12 != null ? "" + p12.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("substForward=").append(p16 != null ? "" + p16.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("captain=").append(p18 != null ? "" + p18.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("kicker1=").append(p17 != null ? "" + p17.getPlayerId() : "").append("\n");

			m_sHRFBuffer.append("behRightBack=").append(p2 != null ? "" + p2.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behInsideBack1=").append(p3 != null ? "" + p3.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behInsideBack2=").append(p4 != null ? "" + p4.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behLeftBack=").append(p5 != null ? "" + p5.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behRightWinger=").append(p6 != null ? "" + p6.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behInsideMid1=").append(p7 != null ? "" + p7.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behInsideMid2=").append(p8 != null ? "" + p8.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behLeftWinger=").append(p9 != null ? "" + p9.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behForward1=").append(p10 != null ? "" + p10.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behForward2=").append(p11 != null ? "" + p11.getBehaviour() : "0").append("\n");
		} catch (Exception ignored) {
		}
	}

	/**
	 * Erstellt die Liga Daten
	 */
	final void createLeague() {
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
	final void createLineUp(NtPlayer trainer, NtLineupParser lineup) {
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

			m_sHRFBuffer.append("trainer=").append(trainer.getPlayerId()).append("\n");
			m_sHRFBuffer.append("installning=0\n");		// TODO: Attitude (pic/mots/norm)
			m_sHRFBuffer.append("tactictype=0\n");		// TODO: tactic (AoW/AiM/CA/...)
			m_sHRFBuffer.append("keeper=").append(p1 != null ? "" + p1.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("rightBack=").append(p2 != null ? "" + p2.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("insideBack1=").append(p3 != null ? "" + p3.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("insideBack2=").append(p4 != null ? "" + p4.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("leftBack=").append(p5 != null ? "" + p5.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("rightWinger=").append(p6 != null ? "" + p6.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("insideMid1=").append(p7 != null ? "" + p7.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("insideMid2=").append(p8 != null ? "" + p8.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("leftWinger=").append(p9 != null ? "" + p9.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("forward1=").append(p10 != null ? "" + p10.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("forward2=").append(p11 != null ? "" + p11.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("substBack=").append(p13 != null ? "" + p13.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("substInsideMid=").append(p14 != null ? "" + p14.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("substWinger=").append(p15 != null ? "" + p15.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("substKeeper=").append(p12 != null ? "" + p12.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("substForward=").append(p16 != null ? "" + p16.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("captain=").append(p18 != null ? "" + p18.getPlayerId() : "").append("\n");
			m_sHRFBuffer.append("kicker1=").append(p17 != null ? "" + p17.getPlayerId() : "").append("\n");

			m_sHRFBuffer.append("behRightBack=").append(p2 != null ? "" + p2.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behInsideBack1=").append(p3 != null ? "" + p3.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behInsideBack2=").append(p4 != null ? "" + p4.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behLeftBack=").append(p5 != null ? "" + p5.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behRightWinger=").append(p6 != null ? "" + p6.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behInsideMid1=").append(p7 != null ? "" + p7.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behInsideMid2=").append(p8 != null ? "" + p8.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behLeftWinger=").append(p9 != null ? "" + p9.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behForward1=").append(p10 != null ? "" + p10.getBehaviour() : "0").append("\n");
			m_sHRFBuffer.append("behForward2=").append(p11 != null ? "" + p11.getBehaviour() : "0").append("\n");
		} catch (Exception ignored) {
		}
	}

	/**
	 * Erstellt die Player Daten
	 */
	final void createPlayers(NtPlayersParser players) {
		List<NtPlayer> pls = players.getAllPlayers();

		for (NtPlayer pl : pls) {
			m_sHRFBuffer.append("[player").append(pl.getPlayerId()).append("]").append("\n");
			m_sHRFBuffer.append("name=").append(pl.getName()).append("\n");
			m_sHRFBuffer.append("firstname=").append(pl.getFirstName()).append("\n");
			m_sHRFBuffer.append("nickname=").append(pl.getNickName()).append("\n");
			m_sHRFBuffer.append("lastname=").append(pl.getLastName()).append("\n");
			m_sHRFBuffer.append("ald=").append(pl.getAgeYears()).append("\n");
			m_sHRFBuffer.append("agedays=").append(pl.getAgeDays()).append("\n");
			m_sHRFBuffer.append("ska=").append(pl.getInjury()).append("\n");
			m_sHRFBuffer.append("for=").append(pl.getForm()).append("\n");
			m_sHRFBuffer.append("uth=").append(pl.getStaminaSkill()).append("\n");
			m_sHRFBuffer.append("spe=").append(pl.getPlaymakerSkill()).append("\n");
			m_sHRFBuffer.append("mal=").append(pl.getScorerSkill()).append("\n");
			m_sHRFBuffer.append("fra=").append(pl.getPassingSkill()).append("\n");
			m_sHRFBuffer.append("ytt=").append(pl.getWingerSkill()).append("\n");
			m_sHRFBuffer.append("fas=").append(pl.getSetPiecesSkill()).append("\n");
			m_sHRFBuffer.append("bac=").append(pl.getDefenderSkill()).append("\n");
			m_sHRFBuffer.append("mlv=").append(pl.getKeeperSkill()).append("\n");
			m_sHRFBuffer.append("rut=").append(pl.getXp()).append("\n");
			m_sHRFBuffer.append("led=").append(pl.getLeaderShip()).append("\n");
			m_sHRFBuffer.append("sal=").append(pl.getSalary()).append("\n");
			m_sHRFBuffer.append("mkt=").append(pl.getTsi()).append("\n");
			m_sHRFBuffer.append("gev=").append(pl.getCareerGoals()).append("\n");
			m_sHRFBuffer.append("gtl=").append(pl.getLeagueGoals()).append("\n");
			m_sHRFBuffer.append("gtc=0\n"); // CupGoals
			m_sHRFBuffer.append("gtt=0\n"); // FriendliesGoals
			m_sHRFBuffer.append("hat=").append(pl.getCareerHattricks()).append("\n");
			m_sHRFBuffer.append("CountryID=").append(pl.getCountryId()).append("\n");
			m_sHRFBuffer.append("warnings=").append(pl.getCards()).append("\n");
			m_sHRFBuffer.append("speciality=").append(pl.getSpeciality()).append("\n");
			m_sHRFBuffer.append("specialityLabel=").append(PlayerSpeciality.toString(pl.getSpeciality())).append("\n");
			m_sHRFBuffer.append("gentleness=").append(pl.getAgreeability()).append("\n");
			m_sHRFBuffer.append("gentlenessLabel=").append(PlayerAgreeability.toString(pl.getAgreeability())).append("\n");
			m_sHRFBuffer.append("honesty=").append(pl.getHonesty()).append("\n");
			m_sHRFBuffer.append("honestyLabel=").append(PlayerHonesty.toString(pl.getHonesty())).append("\n");
			m_sHRFBuffer.append("Aggressiveness=").append(pl.getAggressiveness()).append("\n");
			m_sHRFBuffer.append("AggressivenessLabel=").append(PlayerAggressiveness.toString(pl.getAggressiveness())).append("\n");

			if (pl.isTrainer()) {
				m_sHRFBuffer.append("TrainerType=").append(pl.getTrainerType()).append("\n");
				m_sHRFBuffer.append("TrainerSkill=").append(pl.getTrainerSkill()).append("\n");
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
				m_sHRFBuffer.append("PlayerNumber=").append(pl.getShirtNumber()).append("\n");
			}

			m_sHRFBuffer.append("TransferListed=").append(pl.getTranferlisted()).append("\n");
			m_sHRFBuffer.append("NationalTeamID=").append(teamId).append("\n");
			m_sHRFBuffer.append("Caps=").append(pl.getCaps()).append("\n");
			m_sHRFBuffer.append("CapsU20=").append(pl.getCapsU20()).append("\n");
		}
	}

	/**
	 * Erstellt die Team Daten
	 */
	final void createTeam(NtTeamDetails details) {
		m_sHRFBuffer.append("[team]" + "\n");
		m_sHRFBuffer.append("trLevel=100\n");			// TrainingLevel
		m_sHRFBuffer.append("staminaTrainingPart=5\n"); //StaminaTrainingPart
		m_sHRFBuffer.append("trTypeValue=8\n");			// TrainingType
		m_sHRFBuffer.append("trType=").append(TrainingType.toString(8)).append("\n");

		// TODO: imports de.hattrickorganizer.model.Team. (get though helper from Team)

		if (details.getMorale()>-1 && details.getSelfConfidence()>-1) {
			m_sHRFBuffer.append("stamningValue=").append(details.getMorale()).append("\n");
			try {
				m_sHRFBuffer.append("stamning=").append(TeamSpirit.toString(details.getMorale())).append("\n");
			} catch (Exception e) {
				System.out.println("Cant get text for morale " + details.getMorale() + "\n" + e);
				m_sHRFBuffer.append("stamning=\n");
			}
			m_sHRFBuffer.append("sjalvfortroendeValue=").append(details.getSelfConfidence()).append("\n");
			try {
				m_sHRFBuffer.append("sjalvfortroende=").append(TeamConfidence.toString(details.getSelfConfidence())).append("\n");
			} catch (Exception e) {
				System.out.println("Cant get text for self confidence " + details.getMorale() + "\n" + e);
				m_sHRFBuffer.append("sjalvfortroende=\n");
			}
		} else {
			m_sHRFBuffer.append("playingMatch=true");
		}

		try {
			m_sHRFBuffer.append("exper433=").append(details.getXp433()).append("\n");
			m_sHRFBuffer.append("exper451=").append(details.getXp451()).append("\n");
			m_sHRFBuffer.append("exper352=").append(details.getXp352()).append("\n");
			m_sHRFBuffer.append("exper532=").append(details.getXp532()).append("\n");
			m_sHRFBuffer.append("exper343=").append(details.getXp343()).append("\n");
			m_sHRFBuffer.append("exper541=").append(details.getXp541()).append("\n");
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
	final void createWorld(Map<String, String> world, NtTeamDetails details, NtPlayer trainer) {
		m_sHRFBuffer.append("[xtra]\n");

		m_sHRFBuffer.append("TrainingDate=").append(world.get("TrainingDate")).append("\n");
		m_sHRFBuffer.append("EconomyDate=").append(world.get("EconomyDate")).append("\n");
		m_sHRFBuffer.append("SeriesMatchDate=").append(world.get("SeriesMatchDate")).append("\n");
//		m_sHRFBuffer.append("CurrencyRate=" + world.get("CurrencyRate").toString().replace(',', '.') + "\n");
		m_sHRFBuffer.append("CurrencyRate=10\n");
		
		m_sHRFBuffer.append("LogoURL=").append(details.getHomePageUrl()).append("\n");
		m_sHRFBuffer.append("HasPromoted=False\n"); // HasPromoted

		m_sHRFBuffer.append("TrainerID=").append(trainer != null ? "" + trainer.getPlayerId() : "").append("\n");
		m_sHRFBuffer.append("TrainerName=").append(trainer != null ? trainer.getName() : "").append("\n");
		m_sHRFBuffer.append("ArrivalDate=2009-01-01 03:33:33\n"); // TODO: trainer ArrivalDate
		m_sHRFBuffer.append("LeagueLevelUnitID=1\n"); // TODO: LeagueLevelUnitID
	}

	/**
	 * Parse all leagues and (nativeLeagueId) and their countryId.
	 */
	HashMap<Integer, Integer> getCountryMapping(MyConnector dh) {
		HashMap<Integer, Integer> ret = new HashMap<>(100);
		try {
			String str = getWorldDetailString(dh);
			if (str == null || str.equals("")) {
				return ret;
			}
			Document doc = XMLManager.parseString(str);

			Element ele;
			Element root;
			NodeList list;
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
				if (XMLManager.getAttributeValue(root, "Available").trim().equalsIgnoreCase("true")) {
					ele = (Element) root.getElementsByTagName("CountryID").item(0);
					String countryID = XMLManager.getFirstChildNodeValue(ele);
					ret.put(Integer.parseInt(leagueID), Integer.parseInt(countryID));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

    private String getWorldDetailString(MyConnector dh) {
        return dh.getHattrickXMLFile("/chppxml.axd?file=worlddetails&version=1.8");
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

		BufferedWriter out;
		final String text = m_sHRFBuffer.toString();
		//utf-8
		OutputStreamWriter outWrit;

		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			outWrit = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			out = new BufferedWriter(outWrit);
			out.write(text);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void debug(String txt) {
		HOLogger.instance().debug(this.getClass(), txt);
	}
}
