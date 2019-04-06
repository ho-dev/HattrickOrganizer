package module.matchesanalyzer.data;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchLineupTeam;
import core.model.match.MatchType;
import core.model.match.Matchdetails;
import core.model.misc.Basics;
import core.net.OnlineWorker;
import module.teamAnalyzer.manager.TeamManager;
import module.teamAnalyzer.vo.Team;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class MatchesAnalyzerTeam {
	
	private static final Basics BASICS = HOVerwaltung.instance().getModel().getBasics();
	private static final int MAX_LISTED_PERIOD = -112;
	
	private final int id;
	private final String name;
	private final boolean tournament;
	private final boolean next;
	private final boolean mine;

	private long lastUpdate = 0;

	public MatchesAnalyzerTeam() {
		id = BASICS.getTeamId();
		name = BASICS.getTeamName();
		tournament = false;
		mine = true;
		next = true;
	}

	public MatchesAnalyzerTeam(Team team) {
		id = team.getTeamId();
		name = team.getName();
		tournament = team.isTournament();
		mine = (id == BASICS.getTeamId());
		next = (id == TeamManager.getNextLeagueOpponent().getTeamId());
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isTournament() {
		return tournament;
	}
	
	public boolean isMine() {
		return mine;
	}
	
	public boolean isNext() {
		return next;
	}
	
	private static Set<MatchesAnalyzerMatch> loadMatches(int id) {
		Set<MatchesAnalyzerMatch> matches = new HashSet<MatchesAnalyzerMatch>();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, MAX_LISTED_PERIOD);
		List<MatchKurzInfo> infos = OnlineWorker.getMatchArchive(id, calendar.getTime(), true);
		if (infos == null)
			return matches;

		for(MatchKurzInfo info : infos) {
			if(info == null || info.getMatchStatus() != MatchKurzInfo.FINISHED) continue;

			int matchId = info.getMatchID();

			Matchdetails details = DBManager.instance().getMatchDetails(matchId);
			if(details != null && details.getFetchDatum() == null) continue;

			MatchLineupTeam lineup = DBManager.instance().getMatchLineupTeam(matchId, id);

			MatchesAnalyzerMatch match = new MatchesAnalyzerMatch(id, info, details, lineup);
			matches.add(match);
		}
		
		return matches;
	}

	private static Set<MatchesAnalyzerTeam> loadTeams() {
		Set<MatchesAnalyzerTeam> opponents = new HashSet<MatchesAnalyzerTeam>();

		Iterator<Team> i = TeamManager.getTeams().iterator();
		while(i.hasNext()) {
			Team team = i.next();
			opponents.add(new MatchesAnalyzerTeam(team));
		}
		
		return opponents;
	}

	public List<MatchesAnalyzerMatch> getMatches(boolean league, boolean cup, boolean friendly, boolean unofficial, boolean nationaTeam) {
		Set<MatchesAnalyzerMatch> matches = loadMatches(id);
		List<MatchesAnalyzerMatch> list = new ArrayList<MatchesAnalyzerMatch>();
		for(MatchesAnalyzerMatch match : matches) {
			MatchesAnalyzerMatchType type = match.getType();
			if((league && type.isLeague()) || (cup && type.isCup()) || (friendly && type.isFriendly()) || (unofficial && type.isUnofficial()) || (nationaTeam && type.isNationaTeam())) {
				list.add(match);
			}
		}
		Collections.sort(list, MatchesAnalyzerMatch.reverse_comparator());
		return list;
	}

	public List<MatchesAnalyzerTeam> getTeams() {
		Set<MatchesAnalyzerTeam> opponents = loadTeams();
		List<MatchesAnalyzerTeam> list = new ArrayList<MatchesAnalyzerTeam>();
		list.addAll(opponents);
		Collections.sort(list, MatchesAnalyzerTeam.comparator());
		return list;
	}

	@Override
	public String toString() {
		return name + " (" + id + ")";
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof MatchesAnalyzerTeam)) return false;
		return(((MatchesAnalyzerTeam)obj).getId() == getId());
	}

	public static Comparator<MatchesAnalyzerTeam> comparator() {
		return new Comparator<MatchesAnalyzerTeam>() {
			@Override
			public int compare(MatchesAnalyzerTeam a, MatchesAnalyzerTeam b) {
				return a.getName().compareTo(b.getName());
			}
		};
	}

	public static Comparator<MatchesAnalyzerTeam> reverse_comparator() {
		return new Comparator<MatchesAnalyzerTeam>() {
			@Override
			public int compare(MatchesAnalyzerTeam a, MatchesAnalyzerTeam b) {
				return b.getName().compareTo(a.getName());
			}
		};
	}
}
