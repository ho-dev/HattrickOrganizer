package tool.dbcleanup;

import core.db.DBManager;
import core.file.hrf.HRF;
import core.gui.RefreshManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.enums.MatchType;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JFrame;


/**
 * HO Database Cleanup Tool
 * Removes old HRFs and old matches from the DB to speedup HO
 * 
 * @author flattermann <HO@flattermann.net>
 */
public class DBCleanupTool {
	public static int REMOVE_NONE = -1;
	public static int REMOVE_ALL = 0;

	public DBCleanupTool() {
		
	}
	
	public void showDialog(JFrame owner) {
		new DBCleanupDialog(owner, this);
	}

	/**
	 * Remove old HRFs
	 * 
	 * @param keepWeeks remove HRFs older than x weeks (-1=keep All, 0=remove All)
	 * @param autoRemove if true, automatically remove all HRFs except the first per training week  
	 */
	public void cleanupHRFs (int keepWeeks, boolean autoRemove) {
		Timestamp removeDate = null;
		if (keepWeeks >= 0) {
			var hrfDateFrom = HODateTime.now().minus(keepWeeks*7, ChronoUnit.DAYS);
			removeDate = hrfDateFrom.toDbTimestamp();
		}
		if (removeDate != null || autoRemove) {
			cleanupHRFs(removeDate, autoRemove);
		}
	}
	
	/**
	 * Remove old HRFs
	 * 
	 * @param removeDate remove HRFs older than x (null=keep all)
	 * @param autoRemove if true, automatically remove all HRFs except the first per training week  
	 */
	public void cleanupHRFs (Timestamp removeDate, boolean autoRemove) {
		HOLogger.instance().debug(getClass(),
				"Removing old HRFs: removeDate=" + removeDate + ", autoRemove=" + autoRemove);
		HRF[] allHrfs = DBManager.instance().loadAllHRFs( true);
		var latestHrf = DBManager.instance().getLatestHRF();
		int lastSeason = -1;
		int lastWeek = -1;
		int counter = 0;
		for (HRF curHrf : allHrfs) {
			int curId = curHrf.getHrfId();
			var curDate = curHrf.getDatum();
			var htWeek = curDate.toTrainingWeek();
			int curHtSeasonTraining = htWeek.season;
			int curHtWeekTraining = htWeek.week;
			boolean remove = false;
			if (removeDate != null && removeDate.after(curDate.toDbTimestamp())) {
				remove = true;
			} else if (autoRemove) {
				if (lastSeason == curHtSeasonTraining && lastWeek == curHtWeekTraining) {
					remove = true;
				} else {
					lastSeason = curHtSeasonTraining;
					lastWeek = curHtWeekTraining;
				}
			}
			// Do not remove the latest HRF
			if (remove && curId != latestHrf.getHrfId()) {
				HOLogger.instance().debug(getClass(),
						"Removing Hrf: " + curId + " @ " + curDate + " (" + curHtSeasonTraining + "/" + curHtWeekTraining + ")");
				DBManager.instance().deleteHRF(curId);
				counter++;
			}
		}
		HOLogger.instance().debug(getClass(), "Removed " + counter + "/" + allHrfs.length + " HRFs from DB!");
		if (counter > 0) {
			reInitHO();
		}
	}
	
	/**
	 * Remove old matches from DB (by weeks)
	 * 
	 * @param keepWeeksOwnMatches	keep x weeks of own matches (-1=keep All, 0=remove All)
	 * @param keepWeeksOwnFriendlies keep x weeks of own friendlies (-1=keep All, 0=remove All)
	 * @param keepWeeksOtherMatches keep x weeks of other matches (-1=keep All, 0=remove All)
	 * @param keepWeeksOtherFriendlies keep x weeks of other friendlies (-1=keep All, 0=remove All)
	 */
	void cleanupMatches (int keepWeeksOwnMatches, int keepWeeksOwnFriendlies, int keepWeeksOtherMatches, int keepWeeksOtherFriendlies) {
		Timestamp removeDateOwnMatches = null;
		Timestamp removeDateOwnFriendlies = null;
		Timestamp removeDateOtherMatches = null;
		Timestamp removeDateOtherFriendlies = null;
		if (keepWeeksOwnMatches >= 0) {
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.WEEK_OF_YEAR, -keepWeeksOwnMatches);
			removeDateOwnMatches = new Timestamp(cal.getTimeInMillis());
		}
		if (keepWeeksOwnFriendlies >= 0) {
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.WEEK_OF_YEAR, -keepWeeksOwnFriendlies);
			removeDateOwnFriendlies = new Timestamp(cal.getTimeInMillis());
		}
		if (keepWeeksOtherMatches >= 0) {
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.WEEK_OF_YEAR, -keepWeeksOtherMatches);
			removeDateOtherMatches = new Timestamp(cal.getTimeInMillis());
		}
		if (keepWeeksOtherFriendlies >= 0) {
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.WEEK_OF_YEAR, -keepWeeksOtherFriendlies);
			removeDateOtherFriendlies = new Timestamp(cal.getTimeInMillis());
		}
		if (removeDateOwnMatches != null || removeDateOwnFriendlies != null
				|| removeDateOtherMatches != null || removeDateOtherFriendlies != null) {
			cleanupMatches (removeDateOwnMatches, removeDateOwnFriendlies, removeDateOtherMatches, removeDateOtherFriendlies);
		}
	}
	
	/**
	 * Remove old matches from DB (by date)
	 * 
	 * @param removeDateOwnMatches	remove own matches older than x (null=keep All)
	 * @param removeDateOwnFriendlies	remove own friendlies older than x (null=keep All)
	 * @param removeDateOtherMatches remove other matches older than x (null=keep All)
	 * @param removeDateOtherFriendlies remove other friendlies older than x (null=keep All)
	 */
	private void cleanupMatches (Timestamp removeDateOwnMatches, Timestamp removeDateOwnFriendlies, 
			Timestamp removeDateOtherMatches, Timestamp removeDateOtherFriendlies) {
		HOLogger.instance().debug(getClass(),  
				"Removing old matches: "
				+ "removeDateOwnMatches="+removeDateOwnMatches 
				+ ", removeDateOwnFriendlies="+removeDateOwnFriendlies 
				+ ", removeDateOtherMatches="+removeDateOtherMatches
				+ ", removeDateOtherFriendlies="+removeDateOtherFriendlies);
		var kurzInfos = DBManager.instance().getMatchesKurzInfo(-1);
		int counter = 0;
		int myTeamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		for (MatchKurzInfo curKurzInfo : kurzInfos) {
			Timestamp curMatchDate = curKurzInfo.getMatchSchedule().toDbTimestamp();
			int curMatchId = curKurzInfo.getMatchID();
			MatchType curMatchType = curKurzInfo.getMatchType();
			boolean isMyMatch = (curKurzInfo.getHomeTeamID() == myTeamId || curKurzInfo.getGuestTeamID() == myTeamId);
			boolean removeMatch = false;
			if (isMyMatch) {
				if (curMatchType.isFriendly()) {
					if (removeDateOwnFriendlies != null && removeDateOwnFriendlies.after(curMatchDate)) {
						// Remove friendly of my team
						removeMatch = true;
					}
				} else if (removeDateOwnMatches != null && removeDateOwnMatches.after(curMatchDate)) {
					// Remove non-friendly of my team
					removeMatch = true;					
				}
			} else {
				if (curMatchType.isFriendly()) {
					if (removeDateOtherFriendlies != null && removeDateOtherFriendlies.after(curMatchDate)) {
						// Remove friendly of other team
						removeMatch = true;
					}
				} else if (removeDateOtherMatches != null && removeDateOtherMatches.after(curMatchDate)) {
					// Remove non-friendly of other team
					removeMatch = true;					
				}
				
			}
			if (removeMatch) {
				// Remove match
				HOLogger.instance().debug(getClass(), 
						"Removing match "+curMatchId+" @ "+curMatchDate+ " (myMatch="+isMyMatch+", type="+curMatchType+")");
	            DBManager.instance().deleteMatch(curKurzInfo);
				counter++;
			} 
		}
		HOLogger.instance().debug(getClass(), "Removed " + counter + "/" + kurzInfos.size() + " matches from DB!");
		if (counter > 0) {
			reInitHO();
		}
	}
	
	private void reInitHO() {
        RefreshManager.instance().doReInit();
	}
}
