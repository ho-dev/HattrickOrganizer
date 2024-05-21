package core.model;

import core.db.DBManager;
import core.file.xml.XMLManager;
import core.file.xml.XMLWorldDetailsParser;
import core.net.MyConnector;
import core.util.HOLogger;

import java.util.*;

public class WorldDetailsManager {

	private static WorldDetailsManager WMANAGER = null;
	private List<WorldDetailLeague> leagues;
	private final HashMap<Integer, WorldDetailLeague> countryMap = new HashMap<>();
	private final HashMap<Integer, WorldDetailLeague> leagueMap = new HashMap<>();
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

	/**
	 * Get world detail information of country id.
	 * If cache is empty, it will be initialized by the details stored in the database.
	 * If this information does not contain the requested country id, the world details will be downloaded
	 * from hattrick. The downloaded object will be added to the cache and the database.
	 * @param countryId Country Id
	 * @return WorldDetailLeague
	 */
	public WorldDetailLeague getWorldDetailLeagueByCountryId(Integer countryId) {
		if (countryMap.isEmpty()) {
			initialize();
		}
		var ret = countryMap.get(countryId);
		if (ret == null) {
			ret = downloadWorldDetailLeague(countryId);
			DBManager.instance().storeWorldDetailLeague(ret);
			initialize();
		}
		return ret;
	}

	/**
	 * Download missing world detail information
	 * @param countryId Country Id
	 * @return WorldDetailLeague
	 */
	private WorldDetailLeague downloadWorldDetailLeague(Integer countryId) {
		WorldDetailLeague ret = null;
		try {
			var worldDetails = MyConnector.instance().getWorldDetailsByCountryId(countryId);
			var leagues = XMLWorldDetailsParser.parseDetails(XMLManager.parseString(worldDetails));
			if (!leagues.isEmpty()) {
				ret = leagues.get(0);
			}
		}
		catch (Exception e) {
			HOLogger.instance().warning(getClass(), "Error downloading world details from " + countryId);
		}
		return ret;
	}

	public final List<WorldDetailLeague> getLeagues() {
		return leagues;
	}

	public final int getTotalUsers() {
		return totalUsers;
	}

}
