package module.matches;

import core.db.DBManager;
import core.model.match.MatchEvent;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchLineupTeam;
import core.model.match.Matchdetails;
import core.net.OnlineWorker;
import core.util.HOLogger;

import java.util.ArrayList;
import java.util.List;

public class MatchesModel {

	private MatchKurzInfo match;
	private Matchdetails details;
	private MatchLineupTeam home;
	private MatchLineupTeam away;
	private List<MatchModelChangeListener> listeners = new ArrayList<MatchModelChangeListener>();

	public void setMatch(MatchKurzInfo match) {
		if (this.match != match) {
			this.match = match;
			this.details = null;
			this.home = null;
			this.away = null;
			fireMatchChanged();
		}
	}

	public MatchKurzInfo getMatch() {
		return this.match;
	}

	public Matchdetails getDetails() {
		if (this.details == null && this.match != null) {
			this.details = this.match.getMatchdetails();
		}
		return this.details;
	}
	
	public MatchLineupTeam getHomeTeamInfo() {
		if (home == null && match != null) {
			home = DBManager.instance().getMatchLineupTeam(details.getSourceSystem().getId(), match.getMatchID(), match.getHeimID());

			if ( home == null){
				// Lineup team was not stored (Verlegenheitstruppe)
				var ok = OnlineWorker.downloadMatchData(match, true);
				home = DBManager.instance().getMatchLineupTeam(details.getSourceSystem().getId(), match.getMatchID(), match.getHeimID());
			}
		}
		return home;
	}
	
	public MatchLineupTeam getAwayTeamInfo() {
		if (away == null && match != null) {
			away = DBManager.instance().getMatchLineupTeam(details.getSourceSystem().getId(), match.getMatchID(), match.getGastID());
		}
		return away;
	}

	public void addMatchModelChangeListener(MatchModelChangeListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	public void removeMatchModelChangeListener(MatchModelChangeListener listener) {
		this.listeners.remove(listener);
	}

	private void fireMatchChanged() {
		for (int i = this.listeners.size() - 1; i >= 0; i--) {
			this.listeners.get(i).matchChanged();
		}
	}
}
