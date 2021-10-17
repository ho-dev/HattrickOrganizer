package module.ifa.model;

import core.db.DBManager;
import core.model.WorldDetailsManager;
import module.ifa.IfaMatch;
import module.ifa.PluginIfaUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IfaModel {

	public static final int APACHE_LEAGUE_ID = 1001;

	private final List<IfaMatch> visited = new ArrayList<IfaMatch>();
	private final List<IfaMatch> hosted = new ArrayList<IfaMatch>();
	private List<IfaStatistic> hostedStatistic;
	private List<IfaStatistic> visitedStatistic;
	private Summary visitedSummary;
	private Summary hostedSummary;
	private double maxCoolness;
	private int totalCountries;
	private final List<ModelChangeListener> listeners = new ArrayList<ModelChangeListener>();

	public IfaModel() {
		init();
	}

	private void init() {
		this.visited.clear();
		this.hosted.clear();
		this.visited.addAll(Arrays.asList(DBManager.instance().getIFAMatches(false)));
		this.hosted.addAll(Arrays.asList(DBManager.instance().getIFAMatches(true)));

		this.maxCoolness = 0.0;
		this.totalCountries = 0;
		WorldDetailsManager.instance().getLeagues().stream()
				.filter(l -> l.getLeagueId() != APACHE_LEAGUE_ID)
				.forEach(l -> {
					this.maxCoolness += PluginIfaUtils.getCoolness(l.getCountryId());
					this.totalCountries++;
				});
		fireModelChanged();
	}

	public double getMaxCoolness() {
		return this.maxCoolness;
	}
	
	public Summary getVisitedSummary() {
		if (this.visitedSummary == null) {
			this.visitedSummary = new Summary(getVisitedStatistic());
		}
		return this.visitedSummary;
	}

	public Summary getHostedSummary() {
		if (this.hostedSummary == null) {
			this.hostedSummary = new Summary(getHostedStatistic());
		}
		return this.hostedSummary;
	}

	public boolean isHosted(int countryId) {
		List<IfaStatistic> hosted = getHostedStatistic();
		for (IfaStatistic stat : hosted) {
			if (countryId == stat.getCountry().getCountryId()) {
				return true;
			}
		}
		return false;
	}

	public boolean isVisited(int countryId) {
		List<IfaStatistic> visited = getVisitedStatistic();
		for (IfaStatistic stat : visited) {
			if (countryId == stat.getCountry().getCountryId()) {
				return true;
			}
		}
		return false;
	}

	public List<IfaStatistic> getVisitedStatistic() {
		if (this.visitedStatistic == null) {
			createVisitedStatistic();
		}
		return this.visitedStatistic;
	}

	public List<IfaStatistic> getHostedStatistic() {
		if (this.hostedStatistic == null) {
			createHostedStatistic();
		}
		return this.hostedStatistic;
	}

	public void addModelChangeListener(ModelChangeListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	public void removeModelChangeListener(ModelChangeListener listener) {
		this.listeners.remove(listener);
	}

	public void reload() {
		this.visitedStatistic = null;
		this.hostedStatistic = null;
		this.visitedSummary = null;
		this.hostedSummary = null;
		init();
	}

	public int getVistedCountriesCount() {
		return getVisitedStatistic().size();
	}

	public int getHostedCountriesCount() {
		return getHostedStatistic().size();
	}

	private void fireModelChanged() {
		for (int i = this.listeners.size() - 1; i >= 0; i--) {
			this.listeners.get(i).modelChanged();
		}
	}

	private void updateStats(IfaStatistic stat, IfaMatch match, boolean away) {
		stat.increasePlayed();

		if (match.getHomeTeamGoals() == match.getAwayTeamGoals()) {
			stat.increaseDraw();
		} else {
			if (match.getHomeTeamGoals() < match.getAwayTeamGoals()) {
				if (away) {
					stat.increaseWon();
				} else {
					stat.increaseLost();
				}
			} else if (match.getHomeTeamGoals() > match.getAwayTeamGoals()) {
				if (away) {
					stat.increaseLost();
				} else {
					stat.increaseWon();
				}
			}
		}

		long matchTimestamp = match.getPlayedDate().getTime();
		if (stat.getLastMatchDate() < matchTimestamp) {
			stat.setLastMatchDate(matchTimestamp);
		}
	}

	private void createVisitedStatistic() {
		Map<Integer, IfaStatistic> map = new HashMap<Integer, IfaStatistic>();
		for (IfaMatch match : this.visited) {
			Integer id = Integer.valueOf(WorldDetailsManager.instance()
					.getWorldDetailLeagueByLeagueId(match.getHomeLeagueId()).getCountryId());
			IfaStatistic stat = map.get(id);
			if (stat == null) {
				stat = new IfaStatistic();
				stat.setCountry(new Country(id));
				map.put(id, stat);
			}

			updateStats(stat, match, true);
		}
		this.visitedStatistic = new ArrayList<IfaStatistic>(map.values());
	}

	private void createHostedStatistic() {
		Map<Integer, IfaStatistic> map = new HashMap<Integer, IfaStatistic>();
		for (IfaMatch match : this.hosted) {
			Integer id = Integer.valueOf(WorldDetailsManager.instance()
					.getWorldDetailLeagueByLeagueId(match.getAwayLeagueId()).getCountryId());
			IfaStatistic stat = map.get(id);
			if (stat == null) {
				stat = new IfaStatistic();
				stat.setCountry(new Country(id));
				map.put(id, stat);
			}

			updateStats(stat, match, false);
		}
		this.hostedStatistic = new ArrayList<IfaStatistic>(map.values());
	}

	public int getTotalCountries() {
		return this.totalCountries;
	}
}
