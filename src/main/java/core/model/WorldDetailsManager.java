package core.model;

import core.db.DBManager;

import java.util.HashMap;

public class WorldDetailsManager {

	private static WorldDetailsManager WMANAGER = null;
	private WorldDetailLeague[] leagues;
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
		for (int i = 0; i < leagues.length; i++) {
			totalUsers += leagues[i].getActiveUsers();
			countryMap.put(Integer.valueOf(leagues[i].getCountryId()), leagues[i]);
			leagueMap.put(Integer.valueOf(leagues[i].getLeagueId()), leagues[i]);
		}
	}

	public void refresh() {
		initialize();
	}

	public int size() {
		return leagues.length;
	}

	public String getNameByCountryId(int countryId) {
		return countryMap.get(Integer.valueOf(countryId)).getCountryName();
	}

	public WorldDetailLeague getWorldDetailLeagueByLeagueId(Integer leagueId) {
		return leagueMap.get(leagueId);
	}

	public WorldDetailLeague getWorldDetailLeagueByCountryId(Integer countryId) {
		return countryMap.get(countryId);
	}

	public final WorldDetailLeague[] getLeagues() {
		return leagues;
	}

	public final int getTotalUsers() {
		return totalUsers;
	}

}
