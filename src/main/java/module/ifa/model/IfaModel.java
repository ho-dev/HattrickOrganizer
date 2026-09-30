package module.ifa.model;

import core.db.DBManager;
import core.model.WorldDetailLeague;
import core.model.WorldDetailsManager;
import module.ifa.IfaMatch;
import module.ifa.PluginIfaUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IfaModel {

    private final List<IfaMatch> visited = new ArrayList<>();
    private final List<IfaMatch> hosted = new ArrayList<>();
    private List<IfaStatistic> hostedStatistic;
    private List<IfaStatistic> visitedStatistic;
    private Summary visitedSummary;
    private Summary hostedSummary;
    private double maxCoolness;
    private int totalCountries;
    private final List<ModelChangeListener> listeners = new ArrayList<>();

    public IfaModel() {
        init();
    }

    private void init() {
        this.visited.clear();
        this.hosted.clear();
        this.visited.addAll(DBManager.instance().getIFAMatches(false));
        this.hosted.addAll(DBManager.instance().getIFAMatches(true));

        this.maxCoolness = 0.0;
        this.totalCountries = 0;
        WorldDetailsManager.instance().getLeagues().stream()
            .filter(WorldDetailLeague::isNationalLeague)
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

    public int getVisitedCountriesCount() {
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
            } else {
                if (away) {
                    stat.increaseLost();
                } else {
                    stat.increaseWon();
                }
            }
        }

        var matchTimestamp = match.getPlayedDate();
        var statMatchDate = stat.getLastMatchDate();
        if (statMatchDate == null || statMatchDate.isBefore(matchTimestamp)) {
            stat.setLastMatchDate(matchTimestamp);
        }
    }

    private void createVisitedStatistic() {
        Map<Integer, IfaStatistic> map = new HashMap<>();
        for (IfaMatch match : this.visited) {
            addStatistic(map, match, match.getHomeCountryIdWithReload(true), true);
        }
        this.visitedStatistic = new ArrayList<>(map.values());
    }

    private void createHostedStatistic() {
        Map<Integer, IfaStatistic> map = new HashMap<>();
        for (IfaMatch match : this.hosted) {
            addStatistic(map, match, match.getAwayCountryIdWithReload(true), false);
        }
        this.hostedStatistic = new ArrayList<>(map.values());
    }

    private void addStatistic(Map<Integer, IfaStatistic> map, IfaMatch match, Integer countryId, boolean isVisited) {
        if (countryId == null || countryId <= 0) {
            return;
        }
        IfaStatistic stat = map.get(countryId);
        if (stat == null) {
            stat = new IfaStatistic();
            stat.setCountry(new Country(countryId));
            map.put(countryId, stat);
        }
        updateStats(stat, match, isVisited);
    }

    /**
     * Alternative country for ifa statistics
     * Replaced by <a href="https://github.com/ho-dev/HattrickOrganizer/issues/2249">...</a> in September 2026
     * Can be reset if HO users complain and prefer to use league stats instead of countries again
     *
     * @param leagueId int League id
     * @return Integer of league's country, null if not found
     */
    @Deprecated
    private Integer getLeagueCountryId(int leagueId) {
        var league = WorldDetailsManager.instance().getWorldDetailLeagueByLeagueId(leagueId);
        if (league != null) {
            return league.getCountryId();
        }
        return null;
    }

    public int getTotalCountries() {
        return this.totalCountries;
    }
}
