package core.db;

import core.module.config.ModuleConfig;
import core.util.HOLogger;
import module.playeranalysis.PlayerAnalysisModule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.JOptionPane;

import core.HO;

final class DBUpdater {
	JDBCAdapter m_clJDBCAdapter;
	DBManager dbZugriff;

	void setDbZugriff(DBManager dbZugriff) {
		this.dbZugriff = dbZugriff;
	}

	void updateDB(int DBVersion) {
		// I introduce some new db versioning system.
		// Just add new version cases in the switch..case part
		// and leave the old ones active, so also users which
		// have skipped a version get their database updated.
		// jailbird.
		int version = 0;
		this.m_clJDBCAdapter = dbZugriff.getAdapter();

		version = ((UserConfigurationTable) dbZugriff.getTable(UserConfigurationTable.TABLENAME))
				.getDBVersion();

		// We may now update depending on the version identifier!
		if (version != DBVersion) {
			try {
				HOLogger.instance().log(getClass(), "Updating DB to version " + DBVersion + "...");

				switch (version) { // hint: fall though (no breaks) is intended
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
                    updateDBv24(DBVersion, version);
				}
				

				HOLogger.instance().log(getClass(), "done.");
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), e);
			}
		} else {
			HOLogger.instance().log(getClass(), "No DB update necessary.");
		}
	}

	/**
	 * Update DB structure to v6
	 */
	private void updateDBv6() throws Exception {

		m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN AGEDAYS INTEGER");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE SCOUT ADD COLUMN AGEDAYS INTEGER");

		// Always set field DBVersion to the new value as last action.
		// Do not use DBVersion but the value, as update packs might
		// do version checking again before applying!
		dbZugriff.saveUserParameter("DBVersion", 6);
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
		AbstractTable faktorenTab = dbZugriff.getTable(FaktorenTable.TABLENAME);
		if (faktorenTab != null) {
			faktorenTab.dropTable();
			faktorenTab.createTable();
		}

		// Always set field DBVersion to the new value as last action.
		// Do not use DBVersion but the value, as update packs might
		// do version checking again before applying!
		dbZugriff.saveUserParameter("DBVersion", 7);
	}

	/**
	 * Update DB structure to v8
	 * 
	 * @throws Exception
	 */
	private void updateDBv8() throws Exception {
		m_clJDBCAdapter.executeUpdate("ALTER TABLE Spieler ADD COLUMN TrainingBlock BOOLEAN");
		m_clJDBCAdapter
				.executeUpdate("UPDATE Spieler SET TrainingBlock=false WHERE TrainingBlock IS null");
		// Always set field DBVersion to the new value as last action.
		// Do not use DBVersion but the value, as update packs might
		// do version checking again before applying!
		dbZugriff.saveUserParameter("DBVersion", 8);
	}

	/**
	 * Update DB structure to v9
	 */
	private void updateDBv9() throws Exception {
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
		dbZugriff.saveUserParameter("DBVersion", 9);
	}

	/**
	 * Update database to version 10.
	 */
	private void updateDBv10() throws Exception {
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
		dbZugriff.saveUserParameter("DBVersion", 10);
	}

	/**
	 * Update database to version 11.
	 */
	private void updateDBv11() throws Exception {
		// Problems in 1.431 release has shifted contents here to v12.
		dbZugriff.saveUserParameter("DBVersion", 11);
		return;
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
			dbZugriff.getTable(MatchSubstitutionTable.TABLENAME).createTable();
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
			dbZugriff.saveUserParameter("DBVersion", 12);
		}
	}

	/**
	 * Updates the database to the 1.432 release. This method might be executed
	 * multiple times (since there are beta releases) so make sure that every
	 * statement added here CAN be exceuted multiple times.
	 * 
	 * @param DBVersion
	 * @param version
	 * @throws SQLException
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
			dbZugriff.getTable(ModuleConfigTable.TABLENAME).createTable();
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
			dbZugriff.getTable(TransferTable.TABLENAME).createTable();
		}
		if (!tableExists(TransferTypeTable.TABLENAME)) {
			dbZugriff.getTable(TransferTypeTable.TABLENAME).createTable();
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
			dbZugriff.getTable(TAFavoriteTable.TABLENAME).createTable();
		}
		if (!tableExists(TAPlayerTable.TABLENAME)) {
			dbZugriff.getTable(TAPlayerTable.TABLENAME).createTable();
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
			ModuleConfigTable mConfigTable = (ModuleConfigTable) dbZugriff
					.getTable(ModuleConfigTable.TABLENAME);
			ResultSet rs = m_clJDBCAdapter.executeQuery("Select * from TEAMANALYZER_SETTINGS");
			if (rs != null) {
				try {
					HashMap<String, Object> tmp = new HashMap<String, Object>();
					while (rs.next()) {
						tmp.put("TA_" + rs.getString("NAME"),
								Boolean.valueOf(rs.getBoolean("VALUE")));
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
			dbZugriff.getTable(IfaMatchTable.TABLENAME).createTable();
		}

		if (!tableExists(WorldDetailsTable.TABLENAME)) {
			dbZugriff.getTable(WorldDetailsTable.TABLENAME).createTable();
		}

		// Spieler table
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
			dbZugriff.getTable(PenaltyTakersTable.TABLENAME).createTable();
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
				 	dbZugriff.saveUserParameter("DBVersion", DBVersion);
			 }
			 else
			 {
				 	HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
				 	dbZugriff.saveUserParameter("DBVersion", DBVersion - 1);
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
	private void updateDBv19(int DBVersion, int version) throws SQLException {
		
		// 1.433 stuff.
		
		if (version < DBVersion) {
			if(!HO.isDevelopment()) {
				HOLogger.instance().info(DBUpdater.class, "Update done, setting db version number from " + version + " to " + DBVersion);
				dbZugriff.saveUserParameter("DBVersion", DBVersion);
			} else {
			 	HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
				dbZugriff.saveUserParameter("DBVersion", DBVersion - 1);
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
			dbZugriff.getTable(StaffTable.TABLENAME).createTable();
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
				dbZugriff.saveUserParameter("DBVersion", DBVersion);
			} else {
			 	HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
				dbZugriff.saveUserParameter("DBVersion", DBVersion - 1);
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
				dbZugriff.saveUserParameter("DBVersion", DBVersion);
			} else {
			 	HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
				dbZugriff.saveUserParameter("DBVersion", DBVersion - 1);
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
            dbZugriff.getTable(WorldDetailsTable.TABLENAME).createTable();
        }

        if (version < DBVersion) {
            if(!HO.isDevelopment()) {
                HOLogger.instance().info(DBUpdater.class, "Update done, setting db version number from " + version + " to " + DBVersion);
                dbZugriff.saveUserParameter("DBVersion", DBVersion);
            } else {
                HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
                dbZugriff.saveUserParameter("DBVersion", DBVersion - 1);
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

        // remove currency name information from XTRADATA table
        if (columnExistsInTable("CurrencyName", XtraDataTable.TABLENAME)) {
			dropColumn("CurrencyName", "XTRADATA");
		}

        if (version < DBVersion) {
            if(!HO.isDevelopment()) {
                HOLogger.instance().info(DBUpdater.class, "Update done, setting db version number from " + version + " to " + DBVersion);
                dbZugriff.saveUserParameter("DBVersion", DBVersion);
            } else {
                HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
                dbZugriff.saveUserParameter("DBVersion", DBVersion - 1);
            }
        } else {
            HOLogger.instance().info(DBUpdater.class,
                    "Update done, db version number will NOT be increased from " + version
                            + " to " + DBVersion + " (isDevelopment=" + HO.isDevelopment() + ")");
        }
    }
	
	private void updateDBv24(int DBVersion, int version) throws SQLException {
		// 1.438
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

		if (version < DBVersion) {
			if(!HO.isDevelopment()) {
				HOLogger.instance().info(DBUpdater.class, "Update done, setting db version number from " + version + " to " + DBVersion);
				dbZugriff.saveUserParameter("DBVersion", DBVersion);
			} else {
				HOLogger.instance().info(DBUpdater.class, "Development update done, setting db version number from " + version + " to " + (DBVersion - 1));
				dbZugriff.saveUserParameter("DBVersion", DBVersion - 1);
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
			m_clJDBCAdapter = dbZugriff.getAdapter();

		double lastConfigUpdate = ((UserConfigurationTable) dbZugriff
				.getTable(UserConfigurationTable.TABLENAME)).getLastConfUpdate();
		/**
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
		dbZugriff.saveUserParameter("LastConfUpdate", 1.4101);
	}

	private void updateConfigTo1420(boolean alreadyApplied) {
		resetTrainingParameters();
		resetPredictionOffsets();
		dbZugriff.saveUserParameter("anzahlNachkommastellen", 2);

		// always set the LastConfUpdate as last step
		dbZugriff.saveUserParameter("LastConfUpdate", 1.420);
	}

	private void updateConfigTo1424(boolean alreadyApplied) {
		resetTrainingParameters(); // Reset training parameters (just to be
									// sure)

		dbZugriff.saveUserParameter("updateCheck", "true");
		dbZugriff.saveUserParameter("newsCheck", "true");

		// always set the LastConfUpdate as last step
		dbZugriff.saveUserParameter("LastConfUpdate", 1.424);
	}

	private void updateConfigTo1425(boolean alreadyApplied) {
		// Argentina.properties is outdated and got replaced by
		// Spanish_sudamericano.properties
		m_clJDBCAdapter
				.executeUpdate("UPDATE USERCONFIGURATION SET CONFIG_VALUE='Spanish_sudamericano' where CONFIG_KEY='sprachDatei' and CONFIG_VALUE='Argentina'");

		// Apply only once
		if (!alreadyApplied) {
			dbZugriff.saveUserParameter("updateCheck", "true");
			dbZugriff.saveUserParameter("newsCheck", "true");

			// Drop the feedback tables to force new feedback upload
			m_clJDBCAdapter.executeUpdate("DROP TABLE IF EXISTS FEEDBACK_SETTINGS");
			m_clJDBCAdapter.executeUpdate("DROP TABLE IF EXISTS FEEDBACK_UPLOAD");
		}

		// always set the LastConfUpdate as last step
		dbZugriff.saveUserParameter("LastConfUpdate", 1.425);
	}

	private void updateConfigTo1429(boolean alreadyApplied) {

		if (!alreadyApplied) {
			resetTrainingParameters();
		}
		// always set the LastConfUpdate as last step
		dbZugriff.saveUserParameter("LastConfUpdate", 1.429);
	}

	private void updateConfigTo1431(boolean alreadyApplied) {

		if (!alreadyApplied) {
			try {
				HOLogger.instance().debug(getClass(), "Reseting player overview rows.");
				String sql = "DELETE FROM USERCOLUMNS WHERE COLUMN_ID BETWEEN 2000 AND 3000";
				m_clJDBCAdapter.executeQuery(sql);

				HOLogger.instance().debug(getClass(), "Reseting lineup overview rows.");
				sql = "DELETE FROM USERCOLUMNS WHERE COLUMN_ID BETWEEN 3000 AND 4000";
				m_clJDBCAdapter.executeQuery(sql);
			} catch (Exception e) {
				HOLogger.instance().debug(getClass(),
						"Error updating to config 1431: " + e.getMessage());
			}
		}
		// always set the LastConfUpdate as last step
		dbZugriff.saveUserParameter("LastConfUpdate", 1.431);
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
		dbZugriff.saveUserParameter("LastConfUpdate", 1.434);
	}

	private void updateConfigTo1436(boolean alreadyApplied) {

		try {
			if (!alreadyApplied) {
				dbZugriff.saveUserParameter("ReleaseChannel", "Stable");
			}
			dbZugriff.removeUserParameter("newsCheck");
			dbZugriff.removeUserParameter("userCheck");
			dbZugriff.removeUserParameter("logoutOnExit");
		} catch (Exception e) {
			HOLogger.instance().debug(getClass(),
					"Error updating to config 1436: " + e.getMessage());
		}

		// always set the LastConfUpdate as last step
		dbZugriff.saveUserParameter("LastConfUpdate", 1.436);
	}

	private void resetTrainingParameters() {
		// Reset Training Speed Parameters for New Training
		// 1.429 training speed in db is now an offset
		HOLogger.instance()
				.info(this.getClass(), "Resetting training parameters to default values");
		dbZugriff.saveUserParameter("DAUER_TORWART", 0.0);
		dbZugriff.saveUserParameter("DAUER_VERTEIDIGUNG", 0.0);
		dbZugriff.saveUserParameter("DAUER_SPIELAUFBAU", 0.0);
		dbZugriff.saveUserParameter("DAUER_PASSPIEL", 0.0);
		dbZugriff.saveUserParameter("DAUER_FLUEGELSPIEL", 0.0);
		dbZugriff.saveUserParameter("DAUER_CHANCENVERWERTUNG", 0.0);
		dbZugriff.saveUserParameter("DAUER_STANDARDS", 0.0);

		dbZugriff.saveUserParameter("AlterFaktor", 0.0);
		dbZugriff.saveUserParameter("TrainerFaktor", 0.0);
		dbZugriff.saveUserParameter("CoTrainerFaktor", 0.0);
		dbZugriff.saveUserParameter("IntensitaetFaktor", 0.0);
	}

	private void resetPredictionOffsets() {
		// Reset Rating offsets for Rating Prediction
		// because of changes in the prediction files
		HOLogger.instance().info(this.getClass(), "Resetting rating prediction offsets");
		dbZugriff.saveUserParameter("leftDefenceOffset", 0.0);
		dbZugriff.saveUserParameter("middleDefenceOffset", 0.0);
		dbZugriff.saveUserParameter("rightDefenceOffset", 0.0);
		dbZugriff.saveUserParameter("midfieldOffset", 0.0);
		dbZugriff.saveUserParameter("leftAttackOffset", 0.0);
		dbZugriff.saveUserParameter("middleAttackOffset", 0.0);
		dbZugriff.saveUserParameter("rightAttackOffset", 0.0);
	}

	private boolean hasPrimaryKey(String tableName) throws SQLException {
		String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.SYSTEM_PRIMARYKEYS WHERE TABLE_NAME = '"
				+ tableName.toUpperCase() + "'";
		ResultSet rs = this.m_clJDBCAdapter.executeQuery(sql);
		return rs.next();
	}

	private boolean columnExistsInTable(String columnName, String tableName) throws SQLException {
		String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME = '"
				+ tableName.toUpperCase()
				+ "' AND COLUMN_NAME = '"
				+ columnName.toUpperCase()
				+ "'";
		ResultSet rs = this.m_clJDBCAdapter.executeQuery(sql);
		return rs.next();
	}

	private boolean tableExists(String tableName) throws SQLException {
		String sql = "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME = '"
				+ tableName.toUpperCase() + "'";
		ResultSet rs = this.m_clJDBCAdapter.executeQuery(sql);
		return rs.next();
	}

	private boolean indexExists(String indexName, String tableName) throws SQLException {
		String sql = "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_INDEXINFO WHERE INDEX_NAME = '"
				+ indexName.toUpperCase() + "' AND TABLE_NAME = '" + tableName.toUpperCase() + "'";
		ResultSet rs = this.m_clJDBCAdapter.executeQuery(sql);
		return rs.next();
	}

	private void dropColumn(String column, String table) throws SQLException {
		if (columnExistsInTable(column, table)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE " + table + " DROP " + column);
		}
	}
}
