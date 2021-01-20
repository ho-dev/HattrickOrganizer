package module.series;

import core.model.enums.RatingsStatistics;

import java.util.Map;

public class Model {

	private Spielplan currentSeries;
	private String currentTeam;
	private Map<Integer, Map<RatingsStatistics,Integer>> leagueStatistics;
	private boolean isStatTypeMax;

	public Spielplan getCurrentSeries() {
		return currentSeries;
	}

	public void setCurrentSeries(Spielplan currentSeries) {
		this.currentSeries = currentSeries;
	}

	public String getCurrentTeam() {
		return currentTeam;
	}

	public void setCurrentTeam(String currentTeam) {
		this.currentTeam = currentTeam;
	}

	public Map<Integer, Map<RatingsStatistics, Integer>> getLeagueStatistics() {
		return leagueStatistics;
	}

	public void setLeagueStatistics(Map<Integer, Map<RatingsStatistics, Integer>> leagueStatistics) {
		this.leagueStatistics = leagueStatistics;
	}

	public boolean isStatTypeMax() {
		return isStatTypeMax;
	}

	public void setStatTypeMax(boolean statTypeMax) {
		isStatTypeMax = statTypeMax;
	}
}
