package module.teamAnalyzer.manager;

import core.db.DBManager;
import core.model.UserParameter;
import core.util.HTCalendarFactory;
import module.teamAnalyzer.vo.PlayerInfo;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class PlayerDataManager {

	public static final int UNKNOWN = -1;
	public static final int AVAILABLE = 0;
	public static final int INJURED = 1;
	public static final int SUSPENDED = 2;
	public static final int SOLD = 3;

	public static PlayerInfo getLatestPlayerInfo(int playerId) {
		return DBManager.instance().getTALatestPlayerInfo(playerId);
	}

	public static PlayerInfo getPlayerInfo(int id, int week, int season) {
		return DBManager.instance().getTAPlayerInfo(id, week, season);
	}

	public static void update(List<PlayerInfo> players) {
		for (Iterator<PlayerInfo> iter = players.iterator(); iter.hasNext();) {
			PlayerInfo parsedPlayer = iter.next();
			setPlayer(parsedPlayer);
		}
	}

	public static int getCurrentWeekNumber() {
		return getCurrentHTWeek() + (getCurrentHTSeason() * 16);
	}

	private static int getCurrentHTSeason() {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.HOUR, UserParameter.instance().TimeZoneDifference);
		return HTCalendarFactory.getHTSeason(date.getTime());
	}

	private static int getCurrentHTWeek() {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.HOUR, UserParameter.instance().TimeZoneDifference);
		return HTCalendarFactory.getHTWeek(date.getTime());
	}

	private static void setPlayer(PlayerInfo info) {
		if (info.getPlayerId() == 0) {
			return;
		}

		PlayerInfo actual = DBManager.instance().getTAPlayerInfo(info.getPlayerId(),
				getCurrentHTWeek(), getCurrentHTSeason());
		
		if (actual.getPlayerId() == 0) {
			DBManager.instance().addTAPlayerInfo(info);
		} else {
			DBManager.instance().updateTAPlayerInfo(info);
		}
	}
}
