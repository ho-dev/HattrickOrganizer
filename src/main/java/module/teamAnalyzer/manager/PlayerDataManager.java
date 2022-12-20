package module.teamAnalyzer.manager;

import core.db.DBManager;
import core.model.HOVerwaltung;
import module.teamAnalyzer.vo.PlayerInfo;
import module.teamAnalyzer.vo.SquadInfo;

import java.util.List;

public class PlayerDataManager {

	public static final int UNKNOWN = -1;
	public static final int AVAILABLE = 0;
	public static final int INJURED = 1;
	public static final int SUSPENDED = 2;
	public static final int TRANSFER_LISTED = 3;
	public static final int YELLOW = 4;
	public static final int DOUBLE_YELLOW = 5;
	public static final int BRUISED = 6;

	public static PlayerInfo getLatestPlayerInfo(int playerId) {
		return DBManager.instance().getTALatestPlayerInfo(playerId);
	}

	public static PlayerInfo getPlayerInfo(int id) {
		return DBManager.instance().getTAPlayerInfo(id, getCurrentHTWeek(), getCurrentHTSeason());
	}
	public static PlayerInfo getPlayerInfo(int id, int week, int season) {
		return DBManager.instance().getTAPlayerInfo(id, week, season);
	}

	public static void update(List<PlayerInfo> players) {
		for (PlayerInfo parsedPlayer : players) {
			setPlayer(parsedPlayer);
		}
	}

	public static int getCurrentHTSeason() {
		return HOVerwaltung.instance().getModel().getBasics().getSeason();
	}

	public static int getCurrentHTWeek() {
		return HOVerwaltung.instance().getModel().getBasics().getSpieltag();
	}

	public static int calcCurrentWeekNumber(){
		return calcWeekNumber(PlayerDataManager.getCurrentHTSeason(), PlayerDataManager.getCurrentHTWeek());
	}

	/**
	 * Calculate a number from season and week numbers
	 * @param season season number [1..]
	 * @param week week number [1..16]
	 * @return number
	 */
	public static int calcWeekNumber(int season, int week) {
		return season*16 + week - 1;
	}

	private static void setPlayer(PlayerInfo info) {
		if (info.getPlayerId() == 0) {
			return;
		}
		DBManager.instance().storeTAPlayerInfo(info);
	}
	public static void update(SquadInfo squadInfo) {
		DBManager.instance().storeSquadInfo(squadInfo);
	}

}
