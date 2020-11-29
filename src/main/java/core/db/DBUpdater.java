package core.db;

import core.module.config.ModuleConfig;
import core.util.HOLogger;
import module.playeranalysis.PlayerAnalysisModule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JOptionPane;

import core.HO;

final class DBUpdater {
	JDBCAdapter m_clJDBCAdapter;
	DBManager dbManager;

	void setDbManager(DBManager dbManager) {
		this.dbManager = dbManager;
	}

	void updateDB(int DBVersion) {
		// Just add new version cases in the switch..case part
		// and leave the old ones active, so also users which
		// have skipped a version get their database updated.
		int version;
		this.m_clJDBCAdapter = dbManager.getAdapter();

		version = ((UserConfigurationTable) dbManager.getTable(UserConfigurationTable.TABLENAME))
				.getDBVersion();

		// We may now update depending on the version identifier!
		if (version != DBVersion) {
			try {
				HOLogger.instance().log(getClass(), "Updating DB to version " + DBVersion + "...");
				switch (version) { // hint: fall through (no breaks) is intended
					// here
					case 0:
					case 1:
					case 2:
					case 3:
						HOLogger.instance().log(getClass(), "DB version " + DBVersion + " is to old");
						try {
							JOptionPane.showMessageDialog(null,
									"DB is too old.\nPlease update first to HO 1.431", "Error",
									JOptionPane.ERROR_MESSAGE);
						} catch (Exception e) {
							HOLogger.instance().log(getClass(), e);
						}
						System.exit(0);
					case 5:
						updateDBv6();
					case 6:
						updateDBv7();
					case 7:
						updateDBv8();
					case 8:
						updateDBv9();
					case 9:
						updateDBv10();
					case 10:
						updateDBv11();
					case 11:
						updateDBv12(DBVersion, version);
					case 4:
						// in Beta 1.432 Rev 1906, the DBVersion was set to '4' by
						// mistake.
						// to fix that, we just execute updateDBTo1432() in this
						// case
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 17:
						updateDBTo1432(DBVersion, version);
					case 18:
						updateDBv19(DBVersion, version);
					case 19:
						updateDBv20(DBVersion, version);
					case 20:
						updateDBv21(DBVersion, version);
					case 21:
						updateDBv22(DBVersion, version);
					case 22:
						updateDBv23(DBVersion, version);
					case 23:
						//MATCHREPORT was made longer here but not in table definition
						//to fix this mistake and not repeat code
						//updateDBv24 is falling through to updateDBv25
					case 24:
						updateDBv25(DBVersion, version);
					case 25:
					case 26: // repair corrupt MATCHHIGHLIGHTSTABLE initialized by HO 2.1
						updateDBv26(DBVersion, version);
					case 27:
					case 299:
					case 300:
					case 301: // Bug#509 requires another update run of v300
						updateDBv300(DBVersion, version);
						updateDBv301(DBVersion, version);
					case 302:
					case 399:
						updateDBv400(DBVersion, version);
					case 400:
						updateDBv500(DBVersion, version);
					case 500:
				}

				HOLogger.instance().log(getClass(), "done.");
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), e);
			}
		} else {
			HOLogger.instance().log(getClass(), "No DB update necessary.");
		}
	}

	private void updateDBv500(int dbVersion, int version) throws SQLException {
		// Upgrade legacy FINANZEN table to new ECONOMY Table (since HO 5.0)
		if (!tableExists(EconomyTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Datum RENAME TO FetchedDate");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Supporter RENAME TO SupportersPopularity");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Sponsoren RENAME TO SponsorsPopularity");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Finanzen RENAME TO Cash");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinSponsoren RENAME TO IncomeSponsors");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinZuschauer RENAME TO IncomeSpectators");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinZinsen RENAME TO IncomeFinancial");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinSonstiges RENAME TO IncomeTemporary");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinGesamt RENAME TO IncomeSum");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostSpieler RENAME TO CostsPlayers");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostTrainer RENAME TO CostsStaff");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostStadion RENAME TO CostsArena");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostJugend RENAME TO CostsYouth");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostZinsen RENAME TO CostsFinancial");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostSonstiges RENAME TO CostsTemporary");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostGesamt RENAME TO CostsSum");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN GewinnVerlust RENAME TO ExpectedWeeksTotal");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinSponsoren RENAME TO LastIncomeSponsors");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinZuschauer RENAME TO LastIncomeSpectators");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinZinsen RENAME TO LastIncomeFinancial");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinSonstiges RENAME TO LastIncomeTemporary");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinGesamt RENAME TO LastIncomeSum");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostSpieler RENAME TO LastCostsPlayers");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostTrainer RENAME TO LastCostsStaff");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostStadion RENAME TO LastCostsArena");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostJugend RENAME TO LastCostsYouth");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostZinsen RENAME TO LastCostsFinancial");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostSonstiges RENAME TO LastCostsTemporary");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostGesamt RENAME TO LastCostsSum");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteGewinnVerlust RENAME TO LastWeeksTotal");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN ExpectedCash INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN IncomeSoldPlayers INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN IncomeSoldPlayersCommission INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN CostsBoughtPlayers INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN CostsArenaBuilding INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastIncomeSoldPlayers INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastIncomeSoldPlayersCommission INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastCostsBoughtPlayers INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastCostsArenaBuilding INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN RENAME TO ECONOMY");
		}

		if (!columnExistsInTable("HomeFormation", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeFormation VARCHAR(5) ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN AwayFormation VARCHAR(5) ");
		}

		updateDBVersion(dbVersion, version);
	}


	private void updateDBv400(int dbVersion, int version) throws SQLException {
		// Delete existing values to provide sane defaults.
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'spielerUebersichtsPanel_horizontalRightSplitPane'");
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'aufstellungsPanel_verticalSplitPane'");
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'aufstellungsPanel_horizontalRightSplitPane'");
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'aufstellungsPanel_horizontalLeftSplitPane'");

		if ( !columnExistsInTable("SeasonOffset", BasicsTable.TABLENAME)){
			m_clJDBCAdapter.executeUpdate("ALTER TABLE BASICS ADD COLUMN SeasonOffset INTEGER");
		}

		if (!columnExistsInTable("Duration", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN Duration INTEGER ");
		}
		if (!columnExistsInTable("MatchPart", MatchHighlightsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN MatchPart INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN EventVariation INTEGER ");
		}
		if (!columnExistsInTable("HomeGoal0", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal0 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal1 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal2 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal3 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal4 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal0 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal1 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal2 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal3 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal4 INTEGER ");
		}

		if (!columnExistsInTable("NAME", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN NAME VARCHAR (100) ");
		}

		// use defaults player formula from defaults.xml by resetting the value in the database
		try {
			AbstractTable faktorenTab = dbManager.getTable(FaktorenTable.TABLENAME);
			if (faktorenTab != null) {
				faktorenTab.dropTable();
				faktorenTab.createTable();
			}
		} catch (SQLException throwables) {
			HOLogger.instance().error(getClass(), "updateDBv400:  Faktoren table could not be reset");
			throwables.printStackTrace();
		}


		resetUserColumns();

		//create FuturePlayerTrainingTable
		if (!tableExists(FuturePlayerTrainingTable.TABLENAME)) {
			dbManager.getTable(FuturePlayerTrainingTable.TABLENAME).createTable();
		}

		updateDBVersion(dbVersion, version);
	}

	private void resetUserColumns() {
		HOLogger.instance().debug(getClass(), "Resetting player overview rows.");
		String sql = "DELETE FROM USERCOLUMNS WHERE COLUMN_ID BETWEEN 2000 AND 3000";
		m_clJDBCAdapter.executeQuery(sql);

		HOLogger.instance().debug(getClass(), "Resetting lineup overview rows.");
		sql = "DELETE FROM USERCOLUMNS WHERE COLUMN_ID BETWEEN 3000 AND 4000";
		m_clJDBCAdapter.executeQuery(sql);
	}


	private void updateDBv301(int dbVersion, int version) throws SQLException {

		m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ALTER COLUMN isDerby SET DATA TYPE BOOLEAN");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ALTER COLUMN isNeutral SET DATA TYPE BOOLEAN");

		if (!columnExistsInTable("EVENT_INDEX", MatchHighlightsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN EVENT_INDEX INTEGER");
		}

		if (!columnExistsInTable("INJURY_TYPE", MatchHighlightsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN INJURY_TYPE TINYINT");
		}

		if (columnExistsInTable("TYP", MatchHighlightsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("UPDATE MATCHHIGHLIGHTS SET MATCH_EVENT_ID = (TYP * 100) + SUBTYP WHERE MATCH_EVENT_ID IS NULL");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS DROP TYP");
		}

		if (!columnExistsInTable("LastMatchDate", SpielerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LastMatchDate VARCHAR (100)");
		}
		if (!columnExistsInTable("LastMatchRating", SpielerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LastMatchRating INTEGER");
		}
		if (!columnExistsInTable("LastMatchId", SpielerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LastMatchId INTEGER");
		}

		Arrays.asList("HEIMTORE", "GASTTORE", "SUBTYP").forEach(s -> {
			try {
				if (columnExistsInTable(s, MatchHighlightsTable.TABLENAME)) {
					m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS DROP " + s);
				}
			} catch (SQLException e) {
				HOLogger.instance().log(getClass(), e);
			}
		});

		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchdetails_heimid_idx ON MATCHDETAILS (HEIMID)");
		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchdetails_gastid_idx ON MATCHDETAILS (GASTID)");
		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchkurzinfo_heimid_idx ON MATCHESKURZINFO (HEIMID)");
		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchkurzinfo_gastid_idx ON MATCHESKURZINFO (GASTID)");
		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchhighlights_teamid_idx ON MATCHHIGHLIGHTS (TEAMID)");
		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchhighlights_eventid_idx ON MATCHHIGHLIGHTS (MATCH_EVENT_ID)");

		Arrays.asList("GlobalRanking", "LeagueRanking", "RegionRanking", "PowerRating").forEach(s -> {
			try {
				if (!columnExistsInTable(s, VereinTable.TABLENAME)) {
					m_clJDBCAdapter.executeUpdate(String.format("ALTER TABLE VEREIN ADD COLUMN %s INTEGER", s));
				}
			} catch (SQLException e) {
				HOLogger.instance().log(getClass(), e);
			}
		});

		Arrays.asList("TWTrainer", "Physiologen").forEach(s -> {
			try {
				if (columnExistsInTable(s, VereinTable.TABLENAME)) {
					m_clJDBCAdapter.executeUpdate("ALTER TABLE VEREIN DROP " + s);
				}
			} catch (SQLException e) {
				HOLogger.instance().log(getClass(), e);
			}
		});

		updateDBVersion(dbVersion, version);

	}

	private void updateDBv300(int DBVersion, int version) throws SQLException {
		// HO 3.0

		// delete old divider locations
		m_clJDBCAdapter
				.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='teamAnalyzer_LowerLefSplitPane'");
		m_clJDBCAdapter
				.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='teamAnalyzer_UpperLeftSplitPane'");
		m_clJDBCAdapter
				.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='teamAnalyzer_MainSplitPane'");

		//store ArenaId into MATCHESKURZINFO table
		if (!columnExistsInTable("ArenaId", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN ArenaId INTEGER");
		}

		//store RegionId into MATCHESKURZINFO table
		if (!columnExistsInTable("RegionId", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN RegionId INTEGER");
		}

		//store Weather into MATCHESKURZINFO table
		if (!columnExistsInTable("Weather", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN Weather INTEGER");
		}

		//store WeatherForecast into MATCHESKURZINFO table
		if (!columnExistsInTable("WeatherForecast", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN WeatherForecast INTEGER");
		}

		//store isDerby into MATCHESKURZINFO table
		if (!columnExistsInTable("isDerby", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN isDerby BOOLEAN");
		}

		//store isNeutral into MATCHESKURZINFO table
		if (!columnExistsInTable("isNeutral", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN isNeutral BOOLEAN");
		}

		//store Salary into TA_PLAYER table
		if (!columnExistsInTable("SALARY", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN SALARY INTEGER");
		}

		//store Stamina  into TA_PLAYER table
		if (!columnExistsInTable("STAMINA", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN STAMINA INTEGER");
		}

		//store MotherClubBonus  into TA_PLAYER table
		if (!columnExistsInTable("MOTHERCLUBBONUS", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN MOTHERCLUBBONUS BOOLEAN");
		}

		//store Loyalty  into TA_PLAYER table
		if (!columnExistsInTable("LOYALTY", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN LOYALTY INTEGER");
		}

		//store RATINGINDIRECTSETPIECESATT  into MATCHDETAILS table
		if (!columnExistsInTable("RATINGINDIRECTSETPIECESATT", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN RATINGINDIRECTSETPIECESATT INTEGER");
		}

		//store RATINGINDIRECTSETPIECESDEF  into MATCHDETAILS table
		if (!columnExistsInTable("RATINGINDIRECTSETPIECESDEF", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN RATINGINDIRECTSETPIECESDEF INTEGER");
		}

		//store FirstName, Nickname  into Playertable
		if (!columnExistsInTable("FirstName", SpielerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN FirstName VARCHAR (100)");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN NickName VARCHAR (100)");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ALTER COLUMN Name RENAME TO LastName");
		}

		// Delete league plans which are not of our own team
		try {
			// find own league plans
			int teamId = getTeamId();
			// select saison,ligaid from paarung where heimid=520472 group by saison,ligaid
			HashMap<Integer, Integer> ownLeaguePlans = new HashMap<>();
			ResultSet rs = m_clJDBCAdapter.executeQuery("select saison,ligaid from paarung where heimid=" + teamId + " group by saison,ligaid");
			if (rs != null) {
				while (rs.next()) {
					int saison = rs.getInt(1);
					int league = rs.getInt(2);
					ownLeaguePlans.put(saison, league);
				}
				rs.close();
			}
			// delete entries in SPIELPLAN and PAARUNG which are not from own team
			rs = m_clJDBCAdapter.executeQuery("select saison,ligaid from spielplan");
			if (rs != null) {
				while (rs.next()) {
					int saison = rs.getInt(1);
					int league = rs.getInt(2);
					if (!ownLeaguePlans.containsKey(saison) || ownLeaguePlans.get(saison) != league) {
						// league is not our own one
						m_clJDBCAdapter.executeUpdate("DELETE FROM spielplan WHERE ligaid=" + league + " and saison=" + saison);
						m_clJDBCAdapter.executeUpdate("DELETE FROM paarung WHERE ligaid=" + league + " and saison=" + saison);
					}
				}
				rs.close();
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}

		updateDBVersion(DBVersion, version);
	}

	private void updateDBVersion(int DBVersion, int version) {
		if (version < DBVersion) {
			if(!HO.isDevelopment()) {
				HOLogger.instance().info(DBUpdater.class, "Update done, setting db version number from " + version + " to " + DBVersion);
				dbManager.saveUserParameter("DBVersion", DBVersion);
			} else {
				HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
				dbManager.saveUserParameter("DBVersion", DBVersion - 1);
			}
		} else {
			HOLogger.instance().info(DBUpdater.class,
					"Update done, db version number will NOT be increased from " + version
							+ " to " + DBVersion + " (isDevelopment=" + HO.isDevelopment() + ")");
		}
	}

	private int getTeamId() {
		try {
			ResultSet rs = m_clJDBCAdapter.executeQuery("select teamid from basics limit 1");
			if (rs != null) {
				rs.first();
				int ret =  rs.getInt(1);
				rs.close();
				return ret;
			}
		} catch (SQLException e) {
			HOLogger.instance().log(getClass(), e);
		}
		return 0;
	}

	/**
	 * Update DB structure to v6
	 */
	private void updateDBv6() {

		m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN AGEDAYS INTEGER");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE SCOUT ADD COLUMN AGEDAYS INTEGER");

		// Always set field DBVersion to the new value as last action.
		// Do not use DBVersion but the value, as update packs might
		// do version checking again before applying!
		dbManager.saveUserParameter("DBVersion", 6);
	}

	/**
	 * Update DB structure to v7
	 */
	private void updateDBv7() throws Exception {

		// Adding arena/region ID
		m_clJDBCAdapter.executeUpdate("ALTER TABLE BASICS ADD COLUMN Region INTEGER");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE STADION ADD COLUMN ArenaID INTEGER");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN RegionID INTEGER");

		// Drop and recreate the table
		// (i.e. use defaults from defaults.xml)
		AbstractTable faktorenTab = dbManager.getTable(FaktorenTable.TABLENAME);
		if (faktorenTab != null) {
			faktorenTab.dropTable();
			faktorenTab.createTable();
		}

		// Always set field DBVersion to the new value as last action.
		// Do not use DBVersion but the value, as update packs might
		// do version checking again before applying!
		dbManager.saveUserParameter("DBVersion", 7);
	}

	/**
	 * Update DB structure to v8
	 */
	private void updateDBv8()  {
		m_clJDBCAdapter.executeUpdate("ALTER TABLE Player ADD COLUMN TrainingBlock BOOLEAN");
		m_clJDBCAdapter
				.executeUpdate("UPDATE Player SET TrainingBlock=false WHERE TrainingBlock IS null");
		// Always set field DBVersion to the new value as last action.
		// Do not use DBVersion but the value, as update packs might
		// do version checking again before applying!
		dbManager.saveUserParameter("DBVersion", 8);
	}

	/**
	 * Update DB structure to v9
	 */
	private void updateDBv9() {
		// Add new columns for spectator distribution
		m_clJDBCAdapter.executeUpdate("ALTER TABLE MatchDetails ADD COLUMN soldTerraces INTEGER");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE MatchDetails ADD COLUMN soldBasic INTEGER");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE MatchDetails ADD COLUMN soldRoof INTEGER");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE MatchDetails ADD COLUMN soldVIP INTEGER");

		m_clJDBCAdapter
				.executeUpdate("UPDATE MatchDetails SET soldTerraces=-1 WHERE soldTerraces IS null");
		m_clJDBCAdapter
				.executeUpdate("UPDATE MatchDetails SET soldBasic=-1 WHERE soldBasic IS null");
		m_clJDBCAdapter.executeUpdate("UPDATE MatchDetails SET soldRoof=-1 WHERE soldRoof IS null");
		m_clJDBCAdapter.executeUpdate("UPDATE MatchDetails SET soldVIP=-1 WHERE soldVIP IS null");

		m_clJDBCAdapter.executeUpdate("ALTER TABLE Scout ADD COLUMN Agreeability INTEGER");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE Scout ADD COLUMN baseWage INTEGER");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE Scout ADD COLUMN Nationality INTEGER");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE Scout ADD COLUMN Leadership INTEGER");

		m_clJDBCAdapter
				.executeUpdate("UPDATE Scout SET  Agreeability=-1 WHERE Agreeability IS null");
		m_clJDBCAdapter.executeUpdate("UPDATE Scout SET  baseWage=-1 WHERE baseWage IS null");
		m_clJDBCAdapter.executeUpdate("UPDATE Scout SET  Nationality=-1 WHERE Nationality IS null");
		m_clJDBCAdapter.executeUpdate("UPDATE Scout SET  Leadership=-1 WHERE Leadership IS null");

		// Always set field DBVersion to the new value as last action.
		// Do not use DBVersion but the value, as update packs might
		// do version checking again before applying!
		dbManager.saveUserParameter("DBVersion", 9);
	}

	/**
	 * Update database to version 10.
	 */
	private void updateDBv10(){
		m_clJDBCAdapter.executeUpdate("ALTER TABLE TEAM ADD COLUMN iErfahrung442 INTEGER");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE TEAM ADD COLUMN iErfahrung523 INTEGER");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE TEAM ADD COLUMN iErfahrung550 INTEGER");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE TEAM ADD COLUMN iErfahrung253 INTEGER");

		m_clJDBCAdapter
				.executeUpdate("UPDATE TEAM SET iErfahrung442=8 WHERE iErfahrung442 IS NULL");
		m_clJDBCAdapter
				.executeUpdate("UPDATE TEAM SET iErfahrung523=1 WHERE iErfahrung523 IS NULL");
		m_clJDBCAdapter
				.executeUpdate("UPDATE TEAM SET iErfahrung550=1 WHERE iErfahrung550 IS NULL");
		m_clJDBCAdapter
				.executeUpdate("UPDATE TEAM SET iErfahrung253=1 WHERE iErfahrung253 IS NULL");

		// Always set field DBVersion to the new value as last action.
		// Do not use DBVersion but the value, as update packs might
		// do version checking again before applying!
		dbManager.saveUserParameter("DBVersion", 10);
	}

	/**
	 * Update database to version 11.
	 */
	private void updateDBv11() {
		// Problems in 1.431 release has shifted contents here to v12.
		dbManager.saveUserParameter("DBVersion", 11);
	}

	/**
	 * Update database to version 12.
	 */
	private void updateDBv12(int DBVersion, int version) throws Exception {
		if (!columnExistsInTable("RatingStarsEndOfMatch", "MATCHLINEUPPLAYER")) {
			m_clJDBCAdapter
					.executeUpdate("ALTER TABLE MATCHLINEUPPLAYER ADD COLUMN RatingStarsEndOfMatch REAL");
			m_clJDBCAdapter
					.executeUpdate("UPDATE MATCHLINEUPPLAYER SET RatingStarsEndOfMatch = -1 WHERE RatingStarsEndOfMatch IS NULL");
		}
		if (!columnExistsInTable("HasSupporter", "BASICS")) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE BASICS ADD COLUMN HasSupporter BOOLEAN");
			m_clJDBCAdapter
					.executeUpdate("UPDATE BASICS SET HasSupporter = 'false' WHERE HasSupporter IS NULL");
		}
		if (!columnExistsInTable("Loyalty", "SPIELER")) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN Loyalty INTEGER");
			m_clJDBCAdapter.executeUpdate("UPDATE SPIELER SET Loyalty = 0 WHERE Loyalty IS NULL");
		}
		if (!columnExistsInTable("HomeGrown", "SPIELER")) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN HomeGrown BOOLEAN");
			m_clJDBCAdapter
					.executeUpdate("UPDATE SPIELER SET HomeGrown = 'false' WHERE HomeGrown IS NULL");
		}
		if (!columnExistsInTable("Loyalty", "SCOUT")) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SCOUT ADD COLUMN Loyalty INTEGER");
			m_clJDBCAdapter.executeUpdate("UPDATE SCOUT SET Loyalty = 0 WHERE Loyalty IS NULL");
		}
		if (!columnExistsInTable("MotherClub", "SCOUT")) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SCOUT ADD COLUMN MotherClub BOOLEAN");
			m_clJDBCAdapter
					.executeUpdate("UPDATE SCOUT SET MotherClub = 'false' WHERE MotherClub IS NULL");
		}
		if (!columnExistsInTable("StartPosition", "MATCHLINEUPPLAYER")) {
			m_clJDBCAdapter
					.executeUpdate("ALTER TABLE MATCHLINEUPPLAYER ADD COLUMN StartPosition INTEGER");
			m_clJDBCAdapter
					.executeUpdate("UPDATE MATCHLINEUPPLAYER SET StartPosition = -1 WHERE StartPosition IS NULL");
		}
		if (!columnExistsInTable("StartBehaviour", "MATCHLINEUPPLAYER")) {
			m_clJDBCAdapter
					.executeUpdate("ALTER TABLE MATCHLINEUPPLAYER ADD COLUMN StartBehaviour INTEGER");
			m_clJDBCAdapter
					.executeUpdate("UPDATE MATCHLINEUPPLAYER SET StartBehaviour = -1 WHERE StartBehaviour IS NULL");
		}
		if (!tableExists(MatchSubstitutionTable.TABLENAME)) {
			dbManager.getTable(MatchSubstitutionTable.TABLENAME).createTable();
		}
		if (!columnExistsInTable("LineupName", "MATCHSUBSTITUTION")) {
			m_clJDBCAdapter
					.executeUpdate("ALTER TABLE MATCHSUBSTITUTION ADD COLUMN LineupName VARCHAR");
			m_clJDBCAdapter
					.executeUpdate("UPDATE MATCHSUBSTITUTION SET LineupName = 'D' WHERE LineupName IS NULL");
		}

		// Follow this pattern in the future. Only set db version if not
		// development, or
		// if the current db is more than one version old. The last update
		// should be made
		// during first run of a non development version.

		if ((version == (DBVersion - 1) && !HO.isDevelopment()) || (version < (DBVersion - 1))) {
			dbManager.saveUserParameter("DBVersion", 12);
		}
	}

	/**
	 * Updates the database to the 1.432 release. This method might be executed
	 * multiple times (since there are beta releases) so make sure that every
	 * statement added here CAN be exceuted multiple times.
	 *
	 * @param DBVersion required db version of the current HO version
	 * @param version version of the loaded database
	 * @throws SQLException exception
	 */
	private void updateDBTo1432(int DBVersion, int version) throws SQLException {
		HOLogger.instance().info(
				DBUpdater.class,
				"Running updateDBTo1432(), current version is " + version
						+ ", new version will be " + DBVersion);
		// Stadion table
		if (tableExists("STADION")) {
			dropColumn("VerkaufteSteh", "STADION");
			dropColumn("VerkaufteSitz", "STADION");
			dropColumn("VerkaufteDach", "STADION");
			dropColumn("VerkaufteLogen", "STADION");
			if (indexExists("ISTADION_1", "STADION")) {
				m_clJDBCAdapter.executeUpdate("DROP INDEX ISTADION_1");
			}
		}

		m_clJDBCAdapter
				.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='einzelnePositionenAnzeigen'");
		m_clJDBCAdapter
				.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='DAUER_ALLGEMEIN'");
		m_clJDBCAdapter
				.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='tempTabArenasizer'");

		// MODULE_CONFIGURATION table
		if (!tableExists(ModuleConfigTable.TABLENAME)) {
			dbManager.getTable(ModuleConfigTable.TABLENAME).createTable();
		}

		// Transfers-plugin
		if (tableExists("TRANSFERS_TRANSFERS")) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TRANSFERS_TRANSFERS RENAME TO "
					+ TransferTable.TABLENAME);
		}
		if (tableExists("TRANSFERS_TYPE")) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TRANSFERS_TYPE RENAME TO "
					+ TransferTypeTable.TABLENAME);
		}
		if (!tableExists(TransferTable.TABLENAME)) {
			dbManager.getTable(TransferTable.TABLENAME).createTable();
		}
		if (!tableExists(TransferTypeTable.TABLENAME)) {
			dbManager.getTable(TransferTypeTable.TABLENAME).createTable();
		}

		// TeamAnalyzer-plugin
		if (tableExists("TEAMANALYZER_FAVORITES")) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TEAMANALYZER_FAVORITES RENAME TO "
					+ TAFavoriteTable.TABLENAME);
		}
		if (tableExists("TEAMANALYZER_PLAYERDATA")) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TEAMANALYZER_PLAYERDATA RENAME TO "
					+ TAPlayerTable.TABLENAME);
		}
		if (!tableExists(TAFavoriteTable.TABLENAME)) {
			dbManager.getTable(TAFavoriteTable.TABLENAME).createTable();
		}
		if (!tableExists(TAPlayerTable.TABLENAME)) {
			dbManager.getTable(TAPlayerTable.TABLENAME).createTable();
		}

		if (hasPrimaryKey(TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE " + TAPlayerTable.TABLENAME
					+ " DROP PRIMARY KEY");
		}
		if (!indexExists("ITA_PLAYER_PLAYERID_WEEK", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("CREATE INDEX ITA_PLAYER_PLAYERID_WEEK ON "
					+ TAPlayerTable.TABLENAME + " (playerid, week)");
		}

		if (tableExists("TEAMANALYZER_SETTINGS")) {
			ModuleConfigTable mConfigTable = (ModuleConfigTable) dbManager
					.getTable(ModuleConfigTable.TABLENAME);
			ResultSet rs = m_clJDBCAdapter.executeQuery("Select * from TEAMANALYZER_SETTINGS");
			if (rs != null) {
				try {
					HashMap<String, Object> tmp = new HashMap<>();
					while (rs.next()) {
						tmp.put("TA_" + rs.getString("NAME"), rs.getBoolean("VALUE"));
					}
					mConfigTable.saveConfig(tmp);
				} catch (SQLException e) {
					HOLogger.instance().warning(this.getClass(), e);
				}
			}
			m_clJDBCAdapter.executeUpdate("DROP TABLE TEAMANALYZER_SETTINGS");
		}

		// IFA module
		if (tableExists("PLUGIN_IFA_TEAM")) {
			m_clJDBCAdapter.executeUpdate("DROP TABLE PLUGIN_IFA_TEAM");
		}
		if (tableExists("PLUGIN_IFA_MATCHES_2")) {
			m_clJDBCAdapter.executeUpdate("DROP TABLE PLUGIN_IFA_MATCHES_2");
		}
		if (tableExists("IFA_MATCH")) {
			m_clJDBCAdapter.executeUpdate("DROP TABLE IFA_MATCH");
		}
		if (!tableExists(IfaMatchTable.TABLENAME)) {
			dbManager.getTable(IfaMatchTable.TABLENAME).createTable();
		}

		if (!tableExists(WorldDetailsTable.TABLENAME)) {
			dbManager.getTable(WorldDetailsTable.TABLENAME).createTable();
		}

		// Player table
		dropColumn("sSpezialitaet", "SPIELER");
		dropColumn("sCharakter", "SPIELER");
		dropColumn("sAnsehen", "SPIELER");
		dropColumn("sAgressivitaet", "SPIELER");

		if (!columnExistsInTable("ActivationDate", "basics")) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE basics ADD COLUMN ActivationDate TIMESTAMP");
		}

		if (tableExists("PENALTYTAKERS")) {
			m_clJDBCAdapter.executeUpdate("DROP TABLE PENALTYTAKERS");
		}
		if (!tableExists(PenaltyTakersTable.TABLENAME)) {
			dbManager.getTable(PenaltyTakersTable.TABLENAME).createTable();
		}

		// Follow this pattern in the future. Only set db version if not
		// development, or if the current db is more than one version old. The
		// last update should be made during first run of a non development
		// version.
		if (version < DBVersion)
		{
			 if(!HO.isDevelopment())
			 {
				 	HOLogger.instance().info(DBUpdater.class, "Update done, setting db version number from " + version + " to " + DBVersion);
				 	dbManager.saveUserParameter("DBVersion", DBVersion);
			 }
			 else
			 {
				 	HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
				 	dbManager.saveUserParameter("DBVersion", DBVersion - 1);
			 }
		} else {
			HOLogger.instance().info(DBUpdater.class,
					"Update done, db version number will NOT be increased from " + version
							+ " to " + DBVersion + " (isDevelopment=" + HO.isDevelopment() + ")");
		}
	}


	// Follow this pattern in the future. Only set db version if not
	// development, or if the current db is more than one version old. The
	// last update should be made during first run of a non development
	// version.
	private void updateDBv19(int DBVersion, int version)  {

		// 1.433 stuff.

		if (version < DBVersion) {
			if(!HO.isDevelopment()) {
				HOLogger.instance().info(DBUpdater.class, "Update done, setting db version number from " + version + " to " + DBVersion);
				dbManager.saveUserParameter("DBVersion", DBVersion);
			} else {
			 	HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
				dbManager.saveUserParameter("DBVersion", DBVersion - 1);
			}
		} else {
			HOLogger.instance().info(DBUpdater.class,
					"Update done, db version number will NOT be increased from " + version
					+ " to " + DBVersion + " (isDevelopment=" + HO.isDevelopment() + ")");
		}
	}

	private void updateDBv20(int DBVersion, int version) throws SQLException {
		// 1.434

		if (!tableExists(StaffTable.TABLENAME)) {
			dbManager.getTable(StaffTable.TABLENAME).createTable();
		}

		if (!columnExistsInTable("TacticAssist", VereinTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE " + VereinTable.TABLENAME + " ADD COLUMN TacticAssist INTEGER");
		}

		if (!columnExistsInTable("FormAssist", VereinTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE " + VereinTable.TABLENAME + " ADD COLUMN FormAssist INTEGER");
		}

		if (!columnExistsInTable("StyleOfPlay", AufstellungTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE " + AufstellungTable.TABLENAME + " ADD COLUMN StyleOfPlay INTEGER");
		}

		if (!columnExistsInTable("StyleOfPlay", MatchLineupTeamTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE " + MatchLineupTeamTable.TABLENAME + " ADD COLUMN StyleOfPlay INTEGER");
		}

		if (version < DBVersion) {
			if(!HO.isDevelopment()) {
				HOLogger.instance().info(DBUpdater.class, "Update done, setting db version number from " + version + " to " + DBVersion);
				dbManager.saveUserParameter("DBVersion", DBVersion);
			} else {
			 	HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
				dbManager.saveUserParameter("DBVersion", DBVersion - 1);
			}
		} else {
			HOLogger.instance().info(DBUpdater.class,
					"Update done, db version number will NOT be increased from " + version
					+ " to " + DBVersion + " (isDevelopment=" + HO.isDevelopment() + ")");
		}
	}

	private void updateDBv21(int DBVersion, int version) throws SQLException {
		// 1.435 BETA
		if (columnExistsInTable("MATCHREPORT", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ALTER COLUMN MATCHREPORT SET DATA TYPE VARCHAR(15000)"); // fix an existing bug - maybe 15 000 is not enough
		}

		if (!columnExistsInTable("HEIMHATSTATS", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HEIMHATSTATS INTEGER");
			m_clJDBCAdapter.executeUpdate("UPDATE MATCHDETAILS SET HEIMHATSTATS = HEIMLEFTATT + HEIMRIGHTATT + HEIMMIDATT + 3 * HEIMMIDFIELD + HEIMLEFTDEF + HEIMRIGHTDEF + HEIMMIDDEF");
		}

		if (!columnExistsInTable("GASTHATSTATS", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GASTHATSTATS INTEGER");
			m_clJDBCAdapter.executeUpdate("UPDATE MATCHDETAILS SET GASTHATSTATS = GASTLEFTATT + GASTRIGHTATT + GASTMIDATT + 3 * GASTMIDFIELD + GASTLEFTDEF + GASTRIGHTDEF + GASTMIDDEF");
		}

		m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ALTER COLUMN EVENTTEXT SET DATA TYPE VARCHAR(5000)"); // fix existing bug
		m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ALTER COLUMN HEIMTORE INTEGER"); // fix existing bug


		if (version < DBVersion) {
			if(!HO.isDevelopment()) {
				HOLogger.instance().info(DBUpdater.class, "Update done, setting db version number from " + version + " to " + DBVersion);
				dbManager.saveUserParameter("DBVersion", DBVersion);
			} else {
			 	HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
				dbManager.saveUserParameter("DBVersion", DBVersion - 1);
			}
		} else {
			HOLogger.instance().info(DBUpdater.class,
					"Update done, db version number will NOT be increased from " + version
					+ " to " + DBVersion + " (isDevelopment=" + HO.isDevelopment() + ")");
		}
	}

    private void updateDBv22(int DBVersion, int version) throws SQLException {
        // 1.435

        // refresh HT_WORLD_DETAILS for IFA module
        if (tableExists("HT_WORLDDETAILS")) {
            m_clJDBCAdapter.executeUpdate("DROP TABLE HT_WORLDDETAILS");
        }

        if (!tableExists(WorldDetailsTable.TABLENAME)) {
            dbManager.getTable(WorldDetailsTable.TABLENAME).createTable();
        }

        if (version < DBVersion) {
            if(!HO.isDevelopment()) {
                HOLogger.instance().info(DBUpdater.class, "Update done, setting db version number from " + version + " to " + DBVersion);
                dbManager.saveUserParameter("DBVersion", DBVersion);
            } else {
                HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
                dbManager.saveUserParameter("DBVersion", DBVersion - 1);
            }
        } else {
            HOLogger.instance().info(DBUpdater.class,
                    "Update done, db version number will NOT be increased from " + version
                            + " to " + DBVersion + " (isDevelopment=" + HO.isDevelopment() + ")");
        }
    }

    private void updateDBv23(int DBVersion, int version) throws SQLException {
        // 1.436 BETA

        // add player fired information to SPIELERNOTIZ table
        if (!columnExistsInTable("isFired", SpielerNotizenTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELERNOTIZ ADD COLUMN isFired BOOLEAN");
			m_clJDBCAdapter.executeUpdate("UPDATE SPIELERNOTIZ SET isFired = 'false'");
		}

        if (version < DBVersion) {
            if(!HO.isDevelopment()) {
                HOLogger.instance().info(DBUpdater.class, "Update done, setting db version number from " + version + " to " + DBVersion);
                dbManager.saveUserParameter("DBVersion", DBVersion);
            } else {
                HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
                dbManager.saveUserParameter("DBVersion", DBVersion - 1);
            }
        } else {
            HOLogger.instance().info(DBUpdater.class,
                    "Update done, db version number will NOT be increased from " + version
                            + " to " + DBVersion + " (isDevelopment=" + HO.isDevelopment() + ")");
        }
    }

	private void updateDBv25(int DBVersion, int version) throws SQLException {
		// 1.436

		// remove currency name information from XTRADATA table
		if (columnExistsInTable("CurrencyName", XtraDataTable.TABLENAME)) {
			dropColumn("CurrencyName", "XTRADATA");
		}

		if (columnExistsInTable("MATCHREPORT", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ALTER COLUMN MATCHREPORT SET DATA TYPE VARCHAR(20000)"); // fix an existing bug - 15 000 was not enough
		}

		if (!columnExistsInTable("HEIMHATSTATS", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HEIMHATSTATS INTEGER");
			m_clJDBCAdapter.executeUpdate("UPDATE MATCHDETAILS SET HEIMHATSTATS = HEIMLEFTATT + HEIMRIGHTATT + HEIMMIDATT + 3 * HEIMMIDFIELD + HEIMLEFTDEF + HEIMRIGHTDEF + HEIMMIDDEF");
		}

		if (!columnExistsInTable("GASTHATSTATS", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GASTHATSTATS INTEGER");
			m_clJDBCAdapter.executeUpdate("UPDATE MATCHDETAILS SET GASTHATSTATS = GASTLEFTATT + GASTRIGHTATT + GASTMIDATT + 3 * GASTMIDFIELD + GASTLEFTDEF + GASTRIGHTDEF + GASTMIDDEF");
		}

		if (!columnExistsInTable("CUPLEVEL", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN CUPLEVEL INTEGER");
			m_clJDBCAdapter.executeUpdate("UPDATE MATCHESKURZINFO SET CUPLEVEL = 0");
		}

		if (!columnExistsInTable("CUPLEVELINDEX", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN CUPLEVELINDEX INTEGER");
			m_clJDBCAdapter.executeUpdate("UPDATE MATCHESKURZINFO SET CUPLEVELINDEX = 0");
		}

		if (!tableExists(MatchOrderTable.TABLENAME)) {
			dbManager.getTable(MatchOrderTable.TABLENAME).createTable();
		}

		// use defaults player formula from defaults.xml by resetting the value in the database
		AbstractTable faktorenTab = dbManager.getTable(FaktorenTable.TABLENAME);
		if (faktorenTab != null) {
			faktorenTab.dropTable();
			faktorenTab.createTable();
		}

		// Removed prediction offset, more details https://github.com/akasolace/HO/issues/221
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='leftAttackOffset'");
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='leftDefenceOffset'");
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='middleAttackOffset'");
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='middleDefenceOffset'");
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='midfieldOffset'");
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='rightAttackOffset'");
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='rightDefenceOffset'");

		if (version < DBVersion) {
			if(!HO.isDevelopment()) {
				HOLogger.instance().info(DBUpdater.class, "Update done, setting db version number from " + version + " to " + DBVersion);
				dbManager.saveUserParameter("DBVersion", DBVersion);
			} else {
				HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
				dbManager.saveUserParameter("DBVersion", DBVersion - 1);
			}
		} else {
			HOLogger.instance().info(DBUpdater.class,
					"Update done, db version number will NOT be increased from " + version
					+ " to " + DBVersion + " (isDevelopment=" + HO.isDevelopment() + ")");
		}
	}

	private void updateDBv26(int DBVersion, int version) throws SQLException {
		// HO 2.1

		if (!columnExistsInTable("SubExperience", SpielerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN SubExperience REAL");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN NationalTeamID INTEGER");
		}

		// store [Matches].MatchContextId into MATCHESKURZINFO table
		if (!columnExistsInTable("MatchContextId", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN MatchContextId INTEGER");
		}

		//store [Tournament Details].TournamentTypeID into MATCHESKURZINFO table
		if (!columnExistsInTable("TournamentTypeID", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN TournamentTypeID INTEGER");
		}

		//create TournamentDetailsTable
		if (!tableExists(TournamentDetailsTable.TABLENAME)) {
			dbManager.getTable(TournamentDetailsTable.TABLENAME).createTable();
		}
		else
		{
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TOURNAMENTDETAILS ALTER COLUMN Creator_Loginname VARCHAR (256)");
		}

		if (!columnExistsInTable("MATCH_EVENT_ID", MatchHighlightsTable.TABLENAME)) {
			try {
				m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN MATCH_EVENT_ID INTEGER");
				m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN EVENT_INDEX INTEGER");
				m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN INJURY_TYPE TINYINT");
				m_clJDBCAdapter.executeUpdate("UPDATE MATCHHIGHLIGHTS SET MATCH_EVENT_ID = (TYP * 100) + SUBTYP WHERE MATCH_EVENT_ID IS NULL");
				m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS DROP HEIMTORE");
				m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS DROP GASTTORE");
				m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS DROP TYP");
				m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS DROP SUBTYP");
			}
			catch (Exception e){
				HOLogger.instance().error(DBUpdater.class,	"DB Corrupted !! Updating to 2.1, MATCH_EVENT_ID column missing in MATCHHIGHLIGHTS and creation not possible !!");
			}
		}


		if (version < DBVersion) {
			if(!HO.isDevelopment()) {
				HOLogger.instance().info(DBUpdater.class, "Update done, setting db version number from " + version + " to " + DBVersion);
				dbManager.saveUserParameter("DBVersion", DBVersion);
			} else {
				HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
				dbManager.saveUserParameter("DBVersion", DBVersion - 1);
			}
		} else {
			HOLogger.instance().info(DBUpdater.class,
					"Update done, db version number will NOT be increased from " + version
							+ " to " + DBVersion + " (isDevelopment=" + HO.isDevelopment() + ")");
		}
	}


	/**
	 * Automatic update of User Configuration parameters
	 *
	 * This method is similar to the updateDB() method above The main difference
	 * is that it is based on the HO release version instead of the DB version
	 *
	 * In development mode, we execute the current update steps again (just like
	 * in updateBD()).
	 *
	 * @author flattermann <flattermannHO@gmail.com>
	 */
	void updateConfig() {
		if (m_clJDBCAdapter == null)
			m_clJDBCAdapter = dbManager.getAdapter();

		double lastConfigUpdate = ((UserConfigurationTable) dbManager
				.getTable(UserConfigurationTable.TABLENAME)).getLastConfUpdate();
		/*
		 * We have to use separate 'if-then' clauses for each conf version
		 * (ascending order) because a user might have skipped some HO releases
		 *
		 * DO NOT use 'if-then-else' here, as this would ignores some updates!
		 */
		if (lastConfigUpdate < 1.4101 || (HO.isDevelopment() && lastConfigUpdate == 1.4101)) {
			HOLogger.instance().log(getClass(), "Updating configuration to version 1.410-1...");
			updateConfigTo1410_1(HO.isDevelopment() && lastConfigUpdate == 1.4101);
		}

		if (lastConfigUpdate < 1.420 || (HO.isDevelopment() && lastConfigUpdate == 1.420)) {
			HOLogger.instance().log(getClass(), "Updating configuration to version 1.420...");
			updateConfigTo1420(HO.isDevelopment() && lastConfigUpdate == 1.420);
		}

		if (lastConfigUpdate < 1.424 || (HO.isDevelopment() && lastConfigUpdate == 1.424)) {
			HOLogger.instance().log(getClass(), "Updating configuration to version 1.424...");
			updateConfigTo1424(HO.isDevelopment() && lastConfigUpdate == 1.424);
		}

		if (lastConfigUpdate < 1.425 || (HO.isDevelopment() && lastConfigUpdate == 1.425)) {
			HOLogger.instance().log(getClass(), "Updating configuration to version 1.425...");
			updateConfigTo1425(HO.isDevelopment() && lastConfigUpdate == 1.425);
		}

		if (lastConfigUpdate < 1.429 || (HO.isDevelopment() && lastConfigUpdate == 1.429)) {
			// Lets not reset poor user's custom training setting each time they
			// start...
			HOLogger.instance().log(getClass(), "Updating configuration to version 1.429...");
			updateConfigTo1429(HO.isDevelopment() && lastConfigUpdate == 1.429);
		}

		if (lastConfigUpdate < 1.431 || (HO.isDevelopment() && lastConfigUpdate == 1.431)) {
			HOLogger.instance().log(getClass(), "Updating configuration to version 1.431...");
			updateConfigTo1431(HO.isDevelopment() && lastConfigUpdate == 1.431);
		}

		if (lastConfigUpdate < 1.434 || (HO.isDevelopment() && lastConfigUpdate == 1.434)) {
			HOLogger.instance().log(getClass(), "Updating configuration to version 1.434...");
			updateConfigTo1434(HO.isDevelopment() && lastConfigUpdate == 1.434);
		}

		if (lastConfigUpdate < 1.436 || (HO.isDevelopment() && lastConfigUpdate == 1.436)) {
			HOLogger.instance().log(getClass(), "Updating configuration to version 1.436...");
			updateConfigTo1436(HO.isDevelopment() && lastConfigUpdate == 1.436);
		}
	}

	private void updateConfigTo1410_1(boolean alreadyApplied) {
		resetTrainingParameters();
		resetPredictionOffsets();

		// Drop the feedback tables to force new feedback upload for beta
		// testers
		m_clJDBCAdapter.executeUpdate("DROP TABLE IF EXISTS FEEDBACK_SETTINGS");
		m_clJDBCAdapter.executeUpdate("DROP TABLE IF EXISTS FEEDBACK_UPLOAD");

		// always set the LastConfUpdate as last step
		dbManager.saveUserParameter("LastConfUpdate", 1.4101);
	}

	private void updateConfigTo1420(boolean alreadyApplied) {
		resetTrainingParameters();
		resetPredictionOffsets();
		dbManager.saveUserParameter("nbDecimals", 2);

		// always set the LastConfUpdate as last step
		dbManager.saveUserParameter("LastConfUpdate", 1.420);
	}

	private void updateConfigTo1424(boolean alreadyApplied) {
		resetTrainingParameters(); // Reset training parameters (just to be
									// sure)

		dbManager.saveUserParameter("updateCheck", "true");
		dbManager.saveUserParameter("newsCheck", "true");

		// always set the LastConfUpdate as last step
		dbManager.saveUserParameter("LastConfUpdate", 1.424);
	}

	private void updateConfigTo1425(boolean alreadyApplied) {
		// Argentina.properties is outdated and got replaced by
		// Spanish_sudamericano.properties
		m_clJDBCAdapter
				.executeUpdate("UPDATE USERCONFIGURATION SET CONFIG_VALUE='Spanish_sudamericano' where CONFIG_KEY='sprachDatei' and CONFIG_VALUE='Argentina'");

		// Apply only once
		if (!alreadyApplied) {
			dbManager.saveUserParameter("updateCheck", "true");
			dbManager.saveUserParameter("newsCheck", "true");

			// Drop the feedback tables to force new feedback upload
			m_clJDBCAdapter.executeUpdate("DROP TABLE IF EXISTS FEEDBACK_SETTINGS");
			m_clJDBCAdapter.executeUpdate("DROP TABLE IF EXISTS FEEDBACK_UPLOAD");
		}

		// always set the LastConfUpdate as last step
		dbManager.saveUserParameter("LastConfUpdate", 1.425);
	}

	private void updateConfigTo1429(boolean alreadyApplied) {

		if (!alreadyApplied) {
			resetTrainingParameters();
		}
		// always set the LastConfUpdate as last step
		dbManager.saveUserParameter("LastConfUpdate", 1.429);
	}

	private void updateConfigTo1431(boolean alreadyApplied) {

		if (!alreadyApplied) {
			try {
				resetUserColumns();
			} catch (Exception e) {
				HOLogger.instance().debug(getClass(),
						"Error updating to config 1431: " + e.getMessage());
			}
		}
		// always set the LastConfUpdate as last step
		dbManager.saveUserParameter("LastConfUpdate", 1.431);
	}

	private void updateConfigTo1434(boolean alreadyApplied) {

		if (!alreadyApplied) {
			try {
				ModuleConfig.instance().setBoolean(
						PlayerAnalysisModule.SHOW_PLAYERCOMPARE, true);

			} catch (Exception e) {
				HOLogger.instance().debug(getClass(),
						"Error updating to config 1434: " + e.getMessage());
			}

		}
		// always set the LastConfUpdate as last step
		dbManager.saveUserParameter("LastConfUpdate", 1.434);
	}

	private void updateConfigTo1436(boolean alreadyApplied) {
		try {
			if (!alreadyApplied) {
				switch(HO.getVersionType()){
					case "DEV" -> dbManager.saveUserParameter("ReleaseChannel", "Dev");
					case "BETA" -> dbManager.saveUserParameter("ReleaseChannel", "Beta");
					default -> dbManager.saveUserParameter("ReleaseChannel", "Stable");
				}
			}
			dbManager.removeUserParameter("newsCheck");
			dbManager.removeUserParameter("userCheck");
			dbManager.removeUserParameter("logoutOnExit");
		} catch (Exception e) {
			HOLogger.instance().debug(getClass(),
					"Error updating to config 1436: " + e.getMessage());
		}

		// always set the LastConfUpdate as last step
		dbManager.saveUserParameter("LastConfUpdate", 1.436);
	}

	private void resetTrainingParameters() {
		// Reset Training Speed Parameters for New Training
		// 1.429 training speed in db is now an offset
		HOLogger.instance()
				.info(this.getClass(), "Resetting training parameters to default values");
		dbManager.saveUserParameter("DAUER_TORWART", 0.0);
		dbManager.saveUserParameter("DAUER_VERTEIDIGUNG", 0.0);
		dbManager.saveUserParameter("DAUER_SPIELAUFBAU", 0.0);
		dbManager.saveUserParameter("DAUER_PASSPIEL", 0.0);
		dbManager.saveUserParameter("DAUER_FLUEGELSPIEL", 0.0);
		dbManager.saveUserParameter("DAUER_CHANCENVERWERTUNG", 0.0);
		dbManager.saveUserParameter("DAUER_STANDARDS", 0.0);

		dbManager.saveUserParameter("AlterFaktor", 0.0);
		dbManager.saveUserParameter("TrainerFaktor", 0.0);
		dbManager.saveUserParameter("CoTrainerFaktor", 0.0);
		dbManager.saveUserParameter("IntensitaetFaktor", 0.0);
	}

	private void resetPredictionOffsets() {
		// Reset Rating offsets for Rating Prediction
		// because of changes in the prediction files
		HOLogger.instance().info(this.getClass(), "Resetting rating prediction offsets");
		dbManager.saveUserParameter("leftDefenceOffset", 0.0);
		dbManager.saveUserParameter("middleDefenceOffset", 0.0);
		dbManager.saveUserParameter("rightDefenceOffset", 0.0);
		dbManager.saveUserParameter("midfieldOffset", 0.0);
		dbManager.saveUserParameter("leftAttackOffset", 0.0);
		dbManager.saveUserParameter("middleAttackOffset", 0.0);
		dbManager.saveUserParameter("rightAttackOffset", 0.0);
	}

	private boolean hasPrimaryKey(String tableName) throws SQLException {
		String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.SYSTEM_PRIMARYKEYS WHERE TABLE_NAME = '"
				+ tableName.toUpperCase() + "'";
		ResultSet rs = this.m_clJDBCAdapter.executeQuery(sql);
		if ( rs != null )return rs.next();
		return false;
	}

	private boolean columnExistsInTable(String columnName, String tableName) throws SQLException {
		String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME = '"
				+ tableName.toUpperCase()
				+ "' AND COLUMN_NAME = '"
				+ columnName.toUpperCase()
				+ "'";
		ResultSet rs = this.m_clJDBCAdapter.executeQuery(sql);
		if ( rs != null )return rs.next();
		return false;
	}

	private boolean tableExists(String tableName) throws SQLException {
		String sql = "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME = '"
				+ tableName.toUpperCase() + "'";
		ResultSet rs = this.m_clJDBCAdapter.executeQuery(sql);
		if ( rs != null )return rs.next();
		return false;	}

	private boolean indexExists(String indexName, String tableName) throws SQLException {
		String sql = "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_INDEXINFO WHERE INDEX_NAME = '"
				+ indexName.toUpperCase() + "' AND TABLE_NAME = '" + tableName.toUpperCase() + "'";
		ResultSet rs = this.m_clJDBCAdapter.executeQuery(sql);
		if ( rs != null )return rs.next();
		return false;
	}

	private void dropColumn(String column, String table) throws SQLException {
		if (columnExistsInTable(column, table)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE " + table + " DROP " + column);
		}
	}
}
