package module.teamAnalyzer.manager;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.enums.MatchType;
import core.model.series.Paarung;
import module.series.Spielplan;
import module.teamAnalyzer.vo.Team;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class TeamManager {

	private static LinkedHashMap<Integer, Team> teams;
	private static boolean updated = false;

	public static Team getFirstTeam() {
		Map.Entry<Integer, Team> entry = getTeamsMap().entrySet().iterator().next();
		return entry.getValue();
	}

	public static Vector<Team> getLeagueMatches(Boolean includeOwn) {
		Spielplan league = getDivisionMatches();
		Vector<Team> lteams = new Vector<>();
		int ownTeamID = HOVerwaltung.instance().getModel().getBasics().getTeamId();

		if (league != null) {
			List<?> matches = league.getEintraege();

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

					lteams.add(t);
				}

				if (element.getGastId() == ownTeamID) {
					Team t = new Team();

					t.setName(element.getHeimName());
					t.setTeamId(element.getHeimId());
					t.setTime(element.getDatum());
					t.setMatchType(MatchType.LEAGUE);

					lteams.add(t);
				}
			}
		}

		if(includeOwn) {
			// add own team before returning list
			Team t = new Team();

			t.setName(HOVerwaltung.instance().getModel().getBasics().getTeamName());
			t.setTeamId(ownTeamID);
			t.setTime(Timestamp.valueOf(LocalDateTime.of(LocalDate.parse("2200-01-01"), LocalTime.MIDNIGHT))); // to ensure own team appear last
			t.setMatchType(MatchType.NONE);

			lteams.add(t);
		}

		return lteams;
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

			Vector<Team> vLMatch = getUpComingMatchs(getLeagueMatches(includeOwn));
			Collections.sort(vLMatch);


			Timestamp refTS = HOVerwaltung.instance().getModel().getBasics().getDatum();

			for ( var team : vLMatch){

				if (team.getTime().compareTo(refTS) >= 0) {
					teams.putIfAbsent(team.getTeamId(), team);
				}
			}
		}
		return teams;
	}

	private static Spielplan getDivisionMatches() {
		return DBManager.instance().getSpielplan(
				HOVerwaltung.instance().getModel().getXtraDaten().getLeagueLevelUnitID(),
				HOVerwaltung.instance().getModel().getBasics().getSeason());
	}

	/*
	 * Return upcoming match, except league matchs
	 */
	private static Vector<Team> getUpComingMatchs(Vector<Team> vTeams) {
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		MatchKurzInfo[] dbMatches = DBManager.instance().getMatchesKurzInfoUpComing(teamId);

		List<MatchKurzInfo> l = new ArrayList<>(Arrays.asList(dbMatches));

		Object[] matches = l.toArray();

		for (Object o : matches) {
			MatchKurzInfo match = (MatchKurzInfo) o;
			Team team = new Team();

			if (match.getHomeTeamID() == teamId) {
				team.setName(match.getGuestTeamName());
				team.setTeamId(match.getGuestTeamID());
			} else {
				team.setName(match.getHomeTeamName());
				team.setTeamId(match.getHomeTeamID());
			}
			team.setTime(match.getMatchDateAsTimestamp());
			team.setMatchType(match.getMatchTypeExtended());

			vTeams.add(team);
		}
		return vTeams;
	}
}
