package module.teamAnalyzer.manager;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.enums.MatchType;
import core.model.series.Paarung;
import core.util.HODateTime;
import module.series.Spielplan;
import module.teamAnalyzer.vo.Team;
import java.util.*;

public class TeamManager {

	private static LinkedHashMap<Integer, Team> teams;
	private static boolean updated = false;

	public static Team getFirstTeam() {
		Map.Entry<Integer, Team> entry = getTeamsMap().entrySet().iterator().next();
		return entry.getValue();
	}

	public static List<Team> getLeagueMatches(Boolean includeOwn) {
		Spielplan league = getDivisionMatches();
		List<Team> teams = new ArrayList<>();
		int ownTeamID = HOVerwaltung.instance().getModel().getBasics().getTeamId();

		if (league != null) {
			List<?> matches = league.getMatches();

			for (Object match : matches) {
				Paarung element = (Paarung) match;

				if (element.getSpieltag() < HOVerwaltung.instance().getModel().getBasics().getSpieltag())
					continue;

				if (element.getHeimId() == ownTeamID) {
					Team t = new Team();

					t.setName(element.getGastName());
					t.setTeamId(element.getGastId());
					t.setTime(element.getDatum());
					t.setMatchType(MatchType.LEAGUE);
					t.setHomeMatch(true);

					teams.add(t);
				}

				if (element.getGastId() == ownTeamID) {
					Team t = new Team();

					t.setName(element.getHeimName());
					t.setTeamId(element.getHeimId());
					t.setTime(element.getDatum());
					t.setMatchType(MatchType.LEAGUE);
					t.setHomeMatch(false);

					teams.add(t);
				}
			}
		}

		if(includeOwn) {
			// add own team before returning list
			Team t = new Team();

			t.setName(HOVerwaltung.instance().getModel().getBasics().getTeamName());
			t.setTeamId(ownTeamID);
			t.setTime(HODateTime.fromHT("2200-01-01 00:00:00")); // to ensure own team appear last
			t.setMatchType(MatchType.NONE);

			teams.add(t);
		}

		return teams;
	}

	public static Team getTeam(int teamId) {
		return getTeamsMap().get(teamId);
	}

	public static boolean isTeamInList(int teamId) {
		return getTeamsMap().get(teamId) != null;
	}

	public static Collection<Team> getTeams(Boolean includeOwn) {
		return getTeamsMap(includeOwn).values();
	}

	public static Collection<Team> getTeams() {
		return getTeams(true);
	}

	public static boolean isUpdated() {
		updated = !updated;

		return !updated;
	}

	public static void addFavouriteTeam(Team team) {
		if (!isTeamInList(team.getTeamId())) {
			System.out.println(team.getMatchType());
			team.setMatchType(MatchType.NONE);
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

	private static LinkedHashMap<Integer, Team> getTeamsMap() {
	 return getTeamsMap(true);
	}


	private static LinkedHashMap<Integer, Team> getTeamsMap(Boolean includeOwn) {
		if (teams == null) {
			teams = new LinkedHashMap<>();

			List<Team> vLMatch = getUpComingMatches(getLeagueMatches(includeOwn));
			Collections.sort(vLMatch);

			var refTS = HOVerwaltung.instance().getModel().getBasics().getDatum();
			for (var team : vLMatch) {
				if (team.getTime().compareTo(refTS) >= 0) {
					teams.putIfAbsent(team.getTeamId(), team);
				}
			}
		}
		return teams;
	}

	private static Spielplan getDivisionMatches() {
		var xtra = HOVerwaltung.instance().getModel().getXtraDaten();
		if ( xtra != null) {
			return DBManager.instance().getSpielplan(xtra.getLeagueLevelUnitID(), HOVerwaltung.instance().getModel().getBasics().getSeason());
		}
		return null;	// nothing downloaded yet
	}

	/**
	 * Returns upcoming matches.
	 */
	private static List<Team> getUpComingMatches(List<Team> vTeams) {
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		var dbMatches = DBManager.instance().getMatchesKurzInfoUpComing(teamId);

		for (var match : dbMatches) {
			Team team = new Team();

			if (match.getHomeTeamID() == teamId) {
				team.setName(match.getGuestTeamName());
				team.setTeamId(match.getGuestTeamID());
			} else {
				team.setName(match.getHomeTeamName());
				team.setTeamId(match.getHomeTeamID());
			}
			team.setTime(match.getMatchSchedule());
			team.setMatchType(match.getMatchTypeExtended());
			team.setHomeMatch(match.isHomeMatch());

			vTeams.add(team);
		}
		return vTeams;
	}
}
