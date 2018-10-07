package module.ifa.model;

import module.ifa.PluginIfaUtils;

import java.util.List;

public class Summary {
	private int countriesTotal;
	private int playedTotal;
	private int wonTotal;
	private int drawTotal;
	private int lostTotal;
	private long lastMatch;
	private double coolnessTotal;

	Summary(List<IfaStatistic> data) {
		init(data);
	}

	public int getCountriesTotal() {
		return countriesTotal;
	}

	public int getPlayedTotal() {
		return playedTotal;
	}

	public int getWonTotal() {
		return wonTotal;
	}

	public int getDrawTotal() {
		return drawTotal;
	}

	public int getLostTotal() {
		return lostTotal;
	}

	public long getLastMatch() {
		return lastMatch;
	}

	public double getCoolnessTotal() {
		return coolnessTotal;
	}

	private void init(List<IfaStatistic> data) {
		for (IfaStatistic stat : data) {
			countriesTotal++;
			playedTotal += stat.getMatchesPlayed();
			wonTotal += stat.getMatchesWon();
			drawTotal += stat.getMatchesDraw();
			lostTotal += stat.getMatchesLost();
			coolnessTotal += PluginIfaUtils.getCoolness(stat.getCountry().getCountryId());
			if (lastMatch < stat.getLastMatchDate()) {
				lastMatch = stat.getLastMatchDate();
			}
		}
	}
}
