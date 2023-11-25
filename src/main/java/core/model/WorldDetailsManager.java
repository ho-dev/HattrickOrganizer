package core.model;

import core.db.DBManager;

import java.util.HashMap;
import java.util.List;

public class WorldDetailsManager {

	private static WorldDetailsManager WMANAGER = null;
	private List<WorldDetailLeague> leagues;
	private HashMap<Integer, WorldDetailLeague> countryMap = new HashMap<Integer, WorldDetailLeague>();
	private HashMap<Integer, WorldDetailLeague> leagueMap = new HashMap<Integer, WorldDetailLeague>();
	private int totalUsers;

	public static WorldDetailsManager instance() {
		if (WMANAGER == null) {
			WMANAGER = new WorldDetailsManager();
		}
		return WMANAGER;
	}

	private WorldDetailsManager() {
		initialize();
	}

	private void initialize() {
		leagues = DBManager.instance().getAllWorldDetailLeagues();
		leagueMap.clear();
		countryMap.clear();
		totalUsers = 0;
		for (var league : leagues) {
			totalUsers += league.getActiveUsers();
			countryMap.put(league.getCountryId(), league);
			leagueMap.put(league.getLeagueId(), league);
		}
	}

	public void refresh() {
		initialize();
	}

	public String getNameByCountryId(int countryId) {
		return countryMap.get(countryId).getCountryName();
	}

	public WorldDetailLeague getWorldDetailLeagueByLeagueId(Integer leagueId) {
		return leagueMap.get(leagueId);
	}

	public WorldDetailLeague getWorldDetailLeagueByCountryId(Integer countryId) {
		if (countryMap.size() == 0)
		{
			initialize();
		}

		return countryMap.get(countryId);
	}

	public final List<WorldDetailLeague> getLeagues() {
		return leagues;
	}

	public final int getTotalUsers() {
		return totalUsers;
	}

}
