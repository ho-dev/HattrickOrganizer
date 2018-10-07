package module.matchesanalyzer.data;

import core.model.match.MatchLineupPlayer;
import core.model.match.MatchLineupTeam;
import core.model.match.Matchdetails;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class MatchesAnalyzerLineup {

	private final int[] counters;
	private final Set<MatchesAnalyzerPlayer> players;

	public MatchesAnalyzerLineup(MatchLineupTeam lineup, Matchdetails details) {
		counters = new int[MatchesAnalyzerPlayer.Position.values().length];
		players = new HashSet<MatchesAnalyzerPlayer>();

		for(MatchLineupPlayer p : lineup.getAufstellung()) {
			// position
			int pos = p.getStartPosition() - 100;
			if(pos < 0 || pos > MatchesAnalyzerPlayer.Role.LFW.ordinal()) continue;
			MatchesAnalyzerPlayer.Role role = MatchesAnalyzerPlayer.Role.values()[pos];
			MatchesAnalyzerPlayer.Behavior behavior = MatchesAnalyzerPlayer.Behavior.values()[p.getStartBehavior()];
			counters[role.getPosition().ordinal()]++;
			
			MatchesAnalyzerPlayer player = new MatchesAnalyzerPlayer(p.getId(), p.getSpielerName(), role, behavior, p.getRating());
			players.add(player);
		}
	}

	public int getDefenders() {
		return counters[MatchesAnalyzerPlayer.Position.DEFENDER.ordinal()] + counters[MatchesAnalyzerPlayer.Position.WINGBACK.ordinal()];
	}

	public int getMidfields() {
		return counters[MatchesAnalyzerPlayer.Position.MIDFIELD.ordinal()] + counters[MatchesAnalyzerPlayer.Position.WINGER.ordinal()];
	}

	public int getForwards() {
		return counters[MatchesAnalyzerPlayer.Position.FORWARD.ordinal()];
	}

	public Iterator<MatchesAnalyzerPlayer> iterator() {
		return players.iterator();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof MatchesAnalyzerMatch)) return false;

		Iterator<MatchesAnalyzerPlayer> it1 = players.iterator();
		Iterator<MatchesAnalyzerPlayer> it2 = ((MatchesAnalyzerLineup)obj).iterator();
		while(it1.hasNext() && it2.hasNext()) {
			if(!it1.next().equals(it2.next())) return false;
		}
		return !(it1.hasNext() || it2.hasNext());
	}

	@Override
	public String toString() {
		// do not display text into the cell
		return null;
	}
}
