package module.teamAnalyzer.manager;

import core.db.DBManager;
import core.model.HOVerwaltung;
import module.teamAnalyzer.vo.PlayerInfo;
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
/*		Calendar date = Calendar.getInstance();
		date.add(Calendar.HOUR, UserParameter.instance().TimeZoneDifference);
		return HTCalendarFactory.getHTSeason(date.getTime());*/
	}

	public static int getCurrentHTWeek() {
		return HOVerwaltung.instance().getModel().getBasics().getSpieltag();
/*		Calendar date = Calendar.getInstance();
		date.add(Calendar.HOUR, UserParameter.instance().TimeZoneDifference);
		return HTCalendarFactory.getHTWeek(date.getTime());*/
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
