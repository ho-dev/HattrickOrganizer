package module.teamAnalyzer.manager;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchType;
import core.model.series.LigaTabellenEintrag;
import core.model.series.Paarung;
import module.matches.SpielePanel;
import module.series.Spielplan;
import module.teamAnalyzer.vo.Team;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TeamManager {

	private static Map<Integer, Team> teams;
	private static boolean updated = false;

	public static Team getNextCupOpponent() {
		Team team = new Team();
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		MatchKurzInfo[] cupMatches = DBManager.instance().getMatchesKurzInfo(teamId,
				SpielePanel.NUR_EIGENE_POKALSPIELE, false);
		MatchKurzInfo[] friendlyMatches = DBManager.instance().getMatchesKurzInfo(teamId,
				SpielePanel.NUR_EIGENE_FREUNDSCHAFTSSPIELE, false);
		List<MatchKurzInfo> l = new ArrayList<MatchKurzInfo>();

		l.addAll(Arrays.asList(friendlyMatches));
		l.addAll(Arrays.asList(cupMatches));

		Object[] matches = l.toArray();

		for (int i = 0; i < matches.length; i++) {
			MatchKurzInfo match = (MatchKurzInfo) matches[i];

			if (match.getMatchStatus() != MatchKurzInfo.FINISHED) {
				if (match.getHeimID() == teamId) {
					team.setName(match.getGastName());
					team.setTeamId(match.getGastID());
				} else {
					team.setName(match.getHeimName());
					team.setTeamId(match.getHeimID());
				}
				team.setTime(match.getMatchDateAsTimestamp());

				return team;
			}
		}

		return team;
	}

	public static Team getNextTournamentOpponent() {
		Team team = new Team();
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		MatchKurzInfo[] tournamentMatches = DBManager.instance().getMatchesKurzInfo(teamId,
				SpielePanel.NUR_EIGENE_TOURNAMENTSPIELE, true);
		List<MatchKurzInfo> l = new ArrayList<MatchKurzInfo>();

		l.addAll(Arrays.asList(tournamentMatches));

		Object[] matches = l.toArray();

		for (int i = 0; i < matches.length; i++) {
			MatchKurzInfo match = (MatchKurzInfo) matches[i];

			if (match.getMatchStatus() != MatchKurzInfo.FINISHED) {
				if (match.getHeimID() == teamId) {
					team.setName(match.getGastName());
					team.setTeamId(match.getGastID());
				} else {
					team.setName(match.getHeimName());
					team.setTeamId(match.getHeimID());
				}
				team.setTime(match.getMatchDateAsTimestamp());
				team.setTournament(true);
				return team;
			}
		}

		return team;
	}

	public static Team getNextLeagueOpponent() {
		Spielplan league = getDivisionMatches();

		if (league != null) {
			List<?> matches = league.getPaarungenBySpieltag(HOVerwaltung.instance().getModel()
					.getBasics().getSpieltag());

			for (Iterator<?> iter = matches.iterator(); iter.hasNext();) {
				Paarung element = (Paarung) iter.next();

				if (element.getHeimId() == HOVerwaltung.instance().getModel().getBasics()
						.getTeamId()) {
					Team t = new Team();

					t.setName(element.getGastName());
					t.setTeamId(element.getGastId());
					t.setTime(element.getDatum());

					return t;
				}

				if (element.getGastId() == HOVerwaltung.instance().getModel().getBasics()
						.getTeamId()) {
					Team t = new Team();

					t.setName(element.getHeimName());
					t.setTeamId(element.getHeimId());
					t.setTime(element.getDatum());

					return t;
				}
			}
		}

		return getNextQualificationOpponent();
	}

	public static Team getTeam(int teamId) {
		return getTeamsMap().get(teamId);
	}

	public static boolean isTeamInList(int teamId) {
		if (getTeamsMap().get(teamId) != null) {
			return true;
		}
		return false;
	}

	public static Collection<Team> getTeams() {
		return getTeamsMap().values();
	}

	public static boolean isUpdated() {
		updated = !updated;

		return !updated;
	}

	public static void addFavouriteTeam(Team team) {
		if (!isTeamInList(team.getTeamId())) {
			getTeamsMap().put(team.getTeamId(), team);
		}

		forceUpdate();
	}

	public static void clean() {
		teams = null;
		updated = true;
	}

	public static void forceUpdate() {
		updated = true;
	}

	private static Map<Integer, Team> getTeamsMap() {
		if (teams == null) {
			teams = new HashMap<Integer, Team>();

			List<Team> l = loadDivisionTeams();

			for (Iterator<Team> iter = l.iterator(); iter.hasNext();) {
				Team element = iter.next();

				teams.put(element.getTeamId(), element);
			}

			Team qualTeam = getNextQualificationOpponent();

			if (qualTeam.getTeamId() != 0) {
				teams.put(qualTeam.getTeamId(), qualTeam);
			}

			Team cupTeam = getNextCupOpponent();

			if (cupTeam.getTeamId() != 0) {
				teams.put(cupTeam.getTeamId(), cupTeam);
			}

			List<Team> teamlist = loadTournamentteams();
			for (Team tourneyteam : teamlist) {
				teams.put(tourneyteam.getTeamId(), tourneyteam);
			}
		}
		return teams;
	}

	private static Spielplan getDivisionMatches() {
		Spielplan league = DBManager.instance().getSpielplan(
				HOVerwaltung.instance().getModel().getXtraDaten().getLeagueLevelUnitID(),
				HOVerwaltung.instance().getModel().getBasics().getSeason());

		return league;
	}

	private static Team getNextQualificationOpponent() {
		Team team = new Team();
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		MatchKurzInfo[] qualificationMatches = DBManager.instance().getMatchesKurzInfo(teamId,
				SpielePanel.NUR_EIGENE_PFLICHTSPIELE, false);
		List<MatchKurzInfo> l = new ArrayList<MatchKurzInfo>();

		l.addAll(Arrays.asList(qualificationMatches));

		Object[] matches = l.toArray();

		for (int i = 0; i < matches.length; i++) {
			MatchKurzInfo match = (MatchKurzInfo) matches[i];

			if ((match.getMatchStatus() != MatchKurzInfo.FINISHED)
					&& (match.getMatchTyp() == MatchType.QUALIFICATION)) {
				if (match.getHeimID() == teamId) {
					team.setName(match.getGastName());
					team.setTeamId(match.getGastID());
				} else {
					team.setName(match.getHeimName());
					team.setTeamId(match.getHeimID());
				}
				team.setTime(match.getMatchDateAsTimestamp());

				return team;
			}
		}

		return team;
	}

	private static List<Team> loadDivisionTeams() {
		List<Team> loadedTeams = new ArrayList<Team>();
		Spielplan league = getDivisionMatches();

		if (league != null) {
			List<?> eintraege = league.getTabelle().getEintraege();

			for (Iterator<?> iter = eintraege.iterator(); iter.hasNext();) {
				LigaTabellenEintrag element = (LigaTabellenEintrag) iter.next();
				Team t = new Team();

				t.setName(element.getTeamName());
				t.setTeamId(element.getTeamId());
				loadedTeams.add(t);
			}
		}

		return loadedTeams;
	}

	private static List<Team> loadTournamentteams() {
		List<Team> loadedTeams = new ArrayList<Team>();
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		MatchKurzInfo[] infoarray = DBManager.instance().getMatchesKurzInfo(teamId,
				MatchKurzInfo.UPCOMING);

		for (int i = 0; i < infoarray.length; i++) {
			if ((infoarray[i].getMatchTyp() == MatchType.TOURNAMENTGROUP)
					|| (infoarray[i].getMatchTyp() == MatchType.TOURNAMENTPLAYOFF)) {
				MatchKurzInfo info = infoarray[i];
				Team t = new Team();
				String teamName;
				if (info.getGastID() == teamId) {
					t.setName(info.getHeimName());
					t.setTeamId(info.getHeimID());
					t.setTournament(true);
				} else if (info.getHeimID() == teamId) {
					t.setName(info.getGastName());
					t.setTeamId(info.getGastID());
					t.setTournament(true);
				} else {
					// Huh?
					continue;
				}
				loadedTeams.add(t);
			}
		}
		return loadedTeams;
	}
}
