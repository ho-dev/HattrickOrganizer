package module.teamAnalyzer.manager;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchType;
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

	public static Vector<Team> getLeagueMatches() {
		Spielplan league = getDivisionMatches();
		Vector<Team> lteams = new Vector<>();
		int ownTeamID = HOVerwaltung.instance().getModel().getBasics().getTeamId();

		if (league != null) {
			List<?> matches = league.getEintraege();

			for (Iterator<?> iter = matches.iterator(); iter.hasNext();) {
				Paarung element = (Paarung) iter.next();

				if (element.getSpieltag() < HOVerwaltung.instance().getModel().getBasics().getSpieltag())
					continue;

				if (element.getHeimId() == ownTeamID) {
					Team t = new Team();

					t.setName(element.getGastName());
					t.setTeamId(element.getGastId());
					t.setTime(element.getDatum());
					t.setMatchType(MatchType.LEAGUE.getIconArrayIndex());

					lteams.add(t);
				}

				if (element.getGastId() == ownTeamID) {
					Team t = new Team();

					t.setName(element.getHeimName());
					t.setTeamId(element.getHeimId());
					t.setTime(element.getDatum());
					t.setMatchType(MatchType.LEAGUE.getIconArrayIndex());

					lteams.add(t);
				}
			}
		}
		// add own team before returning list
		Team t = new Team();

		t.setName(HOVerwaltung.instance().getModel().getBasics().getTeamName());
		t.setTeamId(ownTeamID);
		t.setTime(Timestamp.valueOf(LocalDateTime.of(LocalDate.parse("1900-01-01"), LocalTime.MIDNIGHT))); // to ensure own team appear first
		t.setMatchType(-1);

		lteams.add(t);

		return lteams;
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
			System.out.println(team.getMatchType());
			team.setMatchType(-1);
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
		if (teams == null) {
			teams = new LinkedHashMap<>();

			Vector<Team> vLMatch = getUpComingMatchs(getLeagueMatches());
			Collections.sort(vLMatch);

			Iterator it = vLMatch.iterator();

			Timestamp refTS = HOVerwaltung.instance().getModel().getBasics().getDatum();
			int ownTeamID = HOVerwaltung.instance().getModel().getBasics().getTeamId();

			while(it.hasNext()){
				Team team = (Team)it.next();

				if ((team.getTime().compareTo(refTS) >= 0) || (team.getTeamId() == ownTeamID)) {
					if (teams.get(team.getTeamId()) == null) {
						teams.put(team.getTeamId(), team);
					}
				}
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

	/*
	 * Return upcoming match, except league matchs
	 */
	private static Vector<Team> getUpComingMatchs(Vector<Team> vTeams) {
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		MatchKurzInfo[] dbMatches = DBManager.instance().getMatchesKurzInfoUpComing(teamId);

		List<MatchKurzInfo> l = new ArrayList<MatchKurzInfo>();

		l.addAll(Arrays.asList(dbMatches));

		Object[] matches = l.toArray();

		for (int i = 0; i < matches.length; i++) {
			MatchKurzInfo match = (MatchKurzInfo) matches[i];
			Team team = new Team();

			if (match.getHeimID() == teamId) {
				team.setName(match.getGastName());
				team.setTeamId(match.getGastID());
			} else {
				team.setName(match.getHeimName());
				team.setTeamId(match.getHeimID());
			}
			team.setTime(match.getMatchDateAsTimestamp());
			team.setMatchType(match.getMatchTyp().getIconArrayIndex());

			vTeams.add(team);
		}
		return vTeams;
	}
}
