package core.db;

import core.db.user.UserManager;
import core.model.enums.DBDataSource;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import javax.swing.JOptionPane;
import core.HO;
import module.youth.YouthPlayer;

final class DBUpdater {
	ConnectionManager connectionManager;
	DBManager dbManager;

	void updateDB(int DBVersion) {
		// Just add new version cases in the switch..case part
		// and leave the old ones active, so also users which
		// have skipped a version get their database updated.

		dbManager = DBManager.instance();
		this.connectionManager = dbManager.getConnectionManager();

		int version = ((UserConfigurationTable) dbManager.getTable(UserConfigurationTable.TABLENAME)).getDBVersion();

		if (version != DBVersion) {
			try {
				switch (version) {
					default:
						// Unsupported database version
						// We upgrade database from version 300 (HO 3.0)
						HOLogger.instance().log(getClass(), "DB version " + version + " is too old");
						try {
							JOptionPane.showMessageDialog(null,
									"DB is too old.\nPlease update first to HO! 3.0", "Error",
									JOptionPane.ERROR_MESSAGE);
						} catch (Exception e) {
							HOLogger.instance().log(getClass(), e);
						}
						System.exit(0);
						break;

					// hint: fall through (no breaks) is intended
					case 301:
						updateDBv300();  // Bug#509 requires another update run of v300
						updateDBv301(DBVersion);
					case 302:
					case 399:
						updateDBv400(DBVersion);
					case 400:
					case 499:
						updateDBv500(DBVersion);
					case 500:
						updateDBv600(DBVersion);
					case 600:
						updateDBv601(DBVersion);
					case 601:
					case 602:
						updateDBv700(DBVersion);
					case 700:
						updateDBv701(DBVersion);
					case 701:
						updateDBv800(DBVersion);
					case 800:
				}

			} catch (Exception e) {
				HOLogger.instance().error(getClass(), e);
			}
		} else {
			HOLogger.instance().log(getClass(), "No DB update necessary.");
		}
	}

	private void updateDBv800(int dbVersion) throws SQLException {
        assert dbManager.getConnectionManager() != null;
        dbManager.getConnectionManager().executeUpdate("DROP TABLE IF EXISTS SPIELERSKILLUP");

		if (!tableExists(FuturePlayerSkillTrainingTable.TABLENAME)) {
			dbManager.getTable(FuturePlayerSkillTrainingTable.TABLENAME).createTable();
		}

		var playerTable = dbManager.getTable(SpielerTable.TABLENAME);
		playerTable.tryAddColumn("LineupDisabled", "BOOLEAN");
		playerTable.tryAddColumn("ContractDate", "VARCHAR(100)");
		playerTable.tryChangeColumn("OwnerNotes", "VARCHAR(512)");
		playerTable.tryAddIndex("ISPIELER_3", "LASTNAME,FIRSTNAME,DATUM");
		// playerTable.tryDropIndex("ISPIELER_3");

		var transferTable = dbManager.getTable(TransferTable.TABLENAME);
		transferTable.tryAddColumn("motherclubfee", "INTEGER");
		transferTable.tryAddColumn("previousclubcommission", "INTEGER");
		updateDBVersion(dbVersion, 800);
	}

	private String getMigrateEscapesStatement(String table, String column, String where){
        return "Update "+ table + " SET " + column + "=REPLACE("+column+", ?, ?) " + where;
	}
	private void migrateEscapes(String table, String ... columns){
		migrateSelectedEscapes(table, "", columns);
	}
	private void migrateSelectedEscapes(String table, String where, String ... columns){
		for (var column:columns){
			var now = HODateTime.now();
			HOLogger.instance().info(getClass(), "Migrating escapes in column " + column + " of table " + table + " at " + now.toLocaleDateTime()  );
			connectionManager.executePreparedUpdate(getMigrateEscapesStatement(table, column, where), "§", "\\");
			var rows = connectionManager.executePreparedUpdate(getMigrateEscapesStatement(table, column, where),    "#", "'");
			var finished = HODateTime.now();
			HOLogger.instance().info(getClass(), "Migrating escapes in column " + column + " of table " + table +  " " + rows + " rows finished  at " + finished.toLocaleDateTime() + " duration: " + Duration.between(now.instant, finished.instant).toSeconds() + "sec");
		}
	}

	// fix https://github.com/akasolace/HO/issues/1817
	private void updateDBv701(int dbVersion) throws SQLException {
		var teamTable = dbManager.getTable(TeamTable.TABLENAME);
		teamTable.tryDeleteColumn("sTrainingsArt");
		teamTable.tryDeleteColumn("sStimmung");
		teamTable.tryDeleteColumn("sSelbstvertrauen");
		var matchlineupplayerTable = dbManager.getTable(MatchLineupPlayerTable.TABLENAME);
		matchlineupplayerTable.tryDeleteColumn("PositionCode");
		matchlineupplayerTable.tryDeleteColumn("FIELDPOS");
		var mmatchSubstitutionTable = dbManager.getTable(MatchSubstitutionTable.TABLENAME);
		mmatchSubstitutionTable.tryDeleteColumn("HRFID");
		mmatchSubstitutionTable.tryDeleteColumn("LineupName");
		var stadiumTable = dbManager.getTable(StadionTable.TABLENAME);
		stadiumTable.tryDeleteColumn("GesamtGr");
		updateDBVersion(dbVersion, 701);
	}

	private void updateDBv700(int dbVersion) throws SQLException {
		boolean isFixed = false;
		var users = UserManager.instance().getAllUser();
		for ( var user : users){
			if (user.getNumberOfBackups() == 0){	// repair backupLevel
				user.setNumberOfBackups(3);
				isFixed=true;
			}
		}
		if ( isFixed){
			UserManager.instance().save();
		}
		var playerTable = dbManager.getTable(SpielerTable.TABLENAME);
		if (playerTable.tryAddColumn("LastMatch_PlayedMinutes", "INTEGER")) {
			playerTable.tryAddColumn("LastMatch_PositionCode", "INTEGER");
			playerTable.tryAddColumn("LastMatch_RatingEndOfGame", "INTEGER");
		}

		if (playerTable.tryAddColumn("MotherclubId", "INTEGER")) {
			playerTable.tryAddColumn("MotherclubName", "VARCHAR(255)");
			playerTable.tryAddColumn("MatchesCurrentTeam", "INTEGER");
		}

		if (playerTable.tryDeleteColumn("BONUS")) {
			playerTable.tryDeleteColumn("OffsetTorwart");
			playerTable.tryDeleteColumn("OffsetVerteidigung");
			playerTable.tryDeleteColumn("OffsetSpielaufbau");
			playerTable.tryDeleteColumn("OffsetFluegel");
			playerTable.tryDeleteColumn("OffsetTorschuss");
			playerTable.tryDeleteColumn("OffsetPasspiel");
			playerTable.tryDeleteColumn("OffsetStandards");

			var hrfTable = dbManager.getTable(HRFTable.TABLENAME);
			hrfTable.tryDeleteColumn("NAME");


			connectionManager.executeUpdate("DROP TABLE IF EXISTS MATCHLINEUPPENALTYTAKER");

			migrateEscapes("MATCHHIGHLIGHTS", "EventText", "SpielerName", "GehilfeName");
			migrateEscapes("BASICS", "Manager", "TeamName", "YouthTeamName");
			migrateEscapes("MATCHDETAILS", "ArenaName", "GastName", "HeimName", "Matchreport");
			migrateEscapes("MATCHLINEUPPLAYER", "VName", "NickName", "Name");
			migrateEscapes("MATCHLINEUPTEAM", "TeamName");
			migrateEscapes("MATCHESKURZINFO", "GastName", "HeimName");
			migrateEscapes("NTTEAM", "SHORTNAME", "COACHNAME", "LEAGUENAME", "NAME");
			migrateEscapes("PAARUNG", "GastName", "HeimName");
			migrateEscapes("SCOUT", "Name", "Info");
			migrateEscapes("SPIELERNOTIZ", "Notiz");
			migrateEscapes("SPIELER", "ArrivalDate", "MotherclubName", "NickName", "LastName", "FirstName");
			migrateEscapes("STADION", "StadionName");
			migrateEscapes("TA_PLAYER", "NAME");
			migrateEscapes("TRANSFER", "playername", "buyername", "sellername");
			migrateEscapes("HT_WORLDDETAILS", "COUNTRYNAME");
			migrateEscapes("XTRADATA", "LogoURL");
			migrateEscapes("YOUTHPLAYER", "ScoutName", "OwnerNotes", "Statement", "NickName", "LastName", "FirstName");
			migrateEscapes("YOUTHSCOUTCOMMENT", "Text");
			migrateSelectedEscapes("USERCONFIGURATION", "where CONFIG_KEY='hrfImport_HRFPath'", "CONFIG_VALUE");
		}

		var youthplayerTable = dbManager.getTable(YouthPlayerTable.TABLENAME);
		if (!youthplayerTable.primaryKeyExists()) {
			youthplayerTable.addPrimaryKey("HRF_ID,ID");
			youthplayerTable.tryChangeColumn("rating", "DOUBLE");

			var teamTable = dbManager.getTable(TeamTable.TABLENAME);
			teamTable.tryDeleteColumn("sTrainingsArt");
			teamTable.tryDeleteColumn("sStimmung");
			teamTable.tryDeleteColumn("sSelbstvertrauen");

			var matchlineupplayerTable = dbManager.getTable(MatchLineupPlayerTable.TABLENAME);
			matchlineupplayerTable.tryDeleteColumn("PositionCode");
			matchlineupplayerTable.tryDeleteColumn("FIELDPOS");
			var mmatchSubstitutionTable = dbManager.getTable(MatchSubstitutionTable.TABLENAME);
			mmatchSubstitutionTable.tryDeleteColumn("HRFID");
			mmatchSubstitutionTable.tryDeleteColumn("LineupName");
			var stadiumTable = dbManager.getTable(StadionTable.TABLENAME);
			stadiumTable.tryDeleteColumn("GesamtGr");
		}

		var matchSubstitutionTable = dbManager.getTable(MatchSubstitutionTable.TABLENAME);
		matchSubstitutionTable.tryAddIndex("IMATCHSUBSTITUTION_0", "MatchID,MatchTyp,TeamID");
		matchSubstitutionTable.tryDropIndex("IMATCHSUBSTITUTION_3");

		if (!tableExists(SquadInfoTable.TABLENAME)) {
			dbManager.getTable(SquadInfoTable.TABLENAME).createTable();
		}

		updateDBVersion(dbVersion, 700);
	}


	private void updateDBv601(int dbVersion) throws SQLException {
		var playerTable = dbManager.getTable(SpielerTable.TABLENAME);
		if ( playerTable.tryAddColumn("Statement", "VARCHAR(255)")){
			playerTable.tryAddColumn("OwnerNotes", "VARCHAR(255)");
			playerTable.tryAddColumn("PlayerCategory", "INTEGER");
		}
		updateDBVersion(dbVersion, 601);
	}

	private void updateDBv600(int dbVersion) throws SQLException {
		// reduce data base file's disk space
		connectionManager.executeUpdate("CHECKPOINT DEFRAG");
		connectionManager.executeUpdate("SET FILES SPACE TRUE");
		connectionManager.executeUpdate("SET TABLE MATCHHIGHLIGHTS NEW SPACE");
		connectionManager.executeUpdate("SET TABLE MATCHLINEUPPLAYER NEW SPACE");

		connectionManager.executeUpdate("DROP TABLE AUFSTELLUNG IF EXISTS");
		connectionManager.executeUpdate("DROP TABLE MATCHORDER IF EXISTS");
		connectionManager.executeUpdate("DROP TABLE POSITIONEN IF EXISTS");

		var matchLineupTeamTable = dbManager.getTable(MatchLineupTeamTable.TABLENAME);
		if ( !matchLineupTeamTable.primaryKeyExists() ) {
			matchLineupTeamTable.tryAddColumn("ATTITUDE", "INTEGER");
			matchLineupTeamTable.tryAddColumn("TACTIC", "INTEGER");
			matchLineupTeamTable.tryDropIndex("MATCHLINEUPTEAM_IDX");
			matchLineupTeamTable.addPrimaryKey("MATCHID,TEAMID,MATCHTYP");
		}

		if (!tableExists(NtTeamTable.TABLENAME)) {
			dbManager.getTable(NtTeamTable.TABLENAME).createTable();
		}

		// drop indexes where corresponding primary key exists
		dbManager.getTable(BasicsTable.TABLENAME).tryDropIndex("IBASICS_1");
		dbManager.getTable(EconomyTable.TABLENAME).tryDropIndex("ECONOMY_2");
		dbManager.getTable(LigaTable.TABLENAME).tryDropIndex("ILIGA_1");
		dbManager.getTable(SpielerNotizenTable.TABLENAME).tryDropIndex("ISPIELERNOTIZ_1");
		dbManager.getTable(TeamTable.TABLENAME).tryDropIndex("ITEAM_1");
		dbManager.getTable(VereinTable.TABLENAME).tryDropIndex("IVEREIN_1");
		dbManager.getTable(XtraDataTable.TABLENAME).tryDropIndex("IXTRADATA_1");

		if (!columnExistsInTable("IncomeSponsorsBonus", EconomyTable.TABLENAME)) {
			try {
				connectionManager.executeUpdate("ALTER TABLE ECONOMY ADD COLUMN LastIncomeSponsorsBonus INTEGER DEFAULT 0");
				connectionManager.executeUpdate("ALTER TABLE ECONOMY ADD COLUMN IncomeSponsorsBonus INTEGER DEFAULT 0");
				HOLogger.instance().info(getClass(), "Sponsor Bonus columns have been added to Economy table");
			}
			catch (Exception e) {
				HOLogger.instance().error(getClass(), "Error when trying to add Sponsor Bonus columns have been added to Economy table: " + e);
			}
		}

		if (!columnExistsInTable("GoalsCurrentTeam", SpielerTable.TABLENAME)) {
			try {
				connectionManager.executeUpdate("ALTER TABLE SPIELER ADD COLUMN GoalsCurrentTeam INTEGER DEFAULT 0");
				connectionManager.executeUpdate("ALTER TABLE SPIELER ADD COLUMN ArrivalDate VARCHAR (100)");
				HOLogger.instance().info(getClass(), "SPIELER table structure has been updated");
			}
			catch (Exception e) {
				HOLogger.instance().error(getClass(), "Error when trying to add Sponsor Bonus columns have been added to Economy table: " + e);
			}
		}
		updateDBVersion(dbVersion, 600);
	}

	private void updateDBv500(int dbVersion) throws SQLException {
		// Upgrade legacy FINANZEN table to new ECONOMY Table (since HO 5.0)
		if (!tableExists(EconomyTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Datum RENAME TO FetchedDate");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Supporter RENAME TO SupportersPopularity");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Sponsoren RENAME TO SponsorsPopularity");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Finanzen RENAME TO Cash");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinSponsoren RENAME TO IncomeSponsors");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinZuschauer RENAME TO IncomeSpectators");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinZinsen RENAME TO IncomeFinancial");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinSonstiges RENAME TO IncomeTemporary");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinGesamt RENAME TO IncomeSum");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostSpieler RENAME TO CostsPlayers");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostTrainer RENAME TO CostsStaff");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostStadion RENAME TO CostsArena");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostJugend RENAME TO CostsYouth");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostZinsen RENAME TO CostsFinancial");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostSonstiges RENAME TO CostsTemporary");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostGesamt RENAME TO CostsSum");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN GewinnVerlust RENAME TO ExpectedWeeksTotal");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinSponsoren RENAME TO LastIncomeSponsors");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinZuschauer RENAME TO LastIncomeSpectators");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinZinsen RENAME TO LastIncomeFinancial");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinSonstiges RENAME TO LastIncomeTemporary");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinGesamt RENAME TO LastIncomeSum");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostSpieler RENAME TO LastCostsPlayers");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostTrainer RENAME TO LastCostsStaff");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostStadion RENAME TO LastCostsArena");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostJugend RENAME TO LastCostsYouth");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostZinsen RENAME TO LastCostsFinancial");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostSonstiges RENAME TO LastCostsTemporary");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostGesamt RENAME TO LastCostsSum");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteGewinnVerlust RENAME TO LastWeeksTotal");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN ExpectedCash INTEGER DEFAULT 0");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN IncomeSoldPlayers INTEGER DEFAULT 0");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN IncomeSoldPlayersCommission INTEGER DEFAULT 0");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN CostsBoughtPlayers INTEGER DEFAULT 0");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN CostsArenaBuilding INTEGER DEFAULT 0");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastIncomeSoldPlayers INTEGER DEFAULT 0");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastIncomeSoldPlayersCommission INTEGER DEFAULT 0");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastCostsBoughtPlayers INTEGER DEFAULT 0");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastCostsArenaBuilding INTEGER DEFAULT 0");
			connectionManager.executeUpdate("ALTER TABLE FINANZEN RENAME TO ECONOMY");
		}

		AbstractTable matchDetailsTable = dbManager.getTable(MatchDetailsTable.TABLENAME);
		matchDetailsTable.tryAddColumn("HomeFormation", "VARCHAR (5)");
		matchDetailsTable.tryAddColumn("AwayFormation", "VARCHAR (5)");

		AbstractTable basicsTable = dbManager.getTable(BasicsTable.TABLENAME);
		basicsTable.tryAddColumn("YouthTeamName", "VARCHAR (127)");
		basicsTable.tryAddColumn("YouthTeamID", "INTEGER");

		AbstractTable matchLineupPlayerTable = dbManager.getTable(MatchLineupPlayerTable.TABLENAME);
		matchLineupPlayerTable.tryAddColumn("StartSetPieces", "BOOLEAN");

		AbstractTable matchHighlightsTable = dbManager.getTable(MatchHighlightsTable.TABLENAME);
		matchHighlightsTable.tryAddColumn("MatchDate", "TIMESTAMP");

		if (!tableExists(YouthTrainingTable.TABLENAME)) {
			dbManager.getTable(YouthTrainingTable.TABLENAME).createTable();
			dbManager.getTable(YouthPlayerTable.TABLENAME).createTable();
			dbManager.getTable(YouthScoutCommentTable.TABLENAME).createTable();
		}

		AbstractTable youthplayerTable = dbManager.getTable(YouthPlayerTable.TABLENAME);
		for (var skill : YouthPlayer.skillIds) {
			youthplayerTable.tryAddColumn(skill + "Top3", "BOOLEAN");
		}

		if (!tableExists(TeamsLogoTable.TABLENAME)) {
			dbManager.getTable(TeamsLogoTable.TABLENAME).createTable();
		}

		// Upgrade TRAINING table =================================================================================
		if (columnExistsInTable("COACH_LEVEL", TrainingsTable.TABLENAME)) {
			HOLogger.instance().debug(getClass(), "Upgrade of training table was already performed ... process skipped !");
		} else {
			// Step 1. Add new columns in TRAINING table ===========================
			var trainingTable = dbManager.getTable(TrainingsTable.TABLENAME);
			trainingTable.tryAddColumn("COACH_LEVEL", "INTEGER");
			trainingTable.tryAddColumn("TRAINING_ASSISTANTS_LEVEL", "INTEGER");
			trainingTable.tryAddColumn("SOURCE", "INTEGER");
			trainingTable.tryAddColumn("TRAINING_DATE", "TIMESTAMP");

			// Step 2. Migrate existing entries ==================================================================
			var trainings = new ArrayList<int[]>();
			final String statement = "SELECT * FROM " + TrainingsTable.TABLENAME;
			ResultSet rs = connectionManager.executeQuery(statement);
			try {
				if (rs != null) {
					while (rs.next()) {
						var training = new int[]{
								rs.getInt("week"),
								rs.getInt("year")
						};
						trainings.add(training);
					}
				}

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.from(ZoneOffset.UTC));
				var trainingAfterDateSql = "select TRAININGDATE,COTRAINER,TRAINER FROM XTRADATA " +
						"INNER JOIN VEREIN ON VEREIN.HRF_ID=XTRADATA.HRF_ID " +
						"INNER JOIN SPIELER ON SPIELER.HRF_ID=XTRADATA.HRF_ID AND TRAINER>0 " +
						"WHERE TRAININGDATE> ? LIMIT 1";
				var updateStatement = "update " + TrainingsTable.TABLENAME + " SET TRAINING_DATE=?, TRAINING_ASSISTANTS_LEVEL=?, COACH_LEVEL=?, SOURCE=? WHERE YEAR=? AND WEEK=?";
				for (var training : trainings) {
					// Convert year, week to Date
					int dayOfWeek = 1;  // 1-7, locale-dependent such as Sunday-Monday in US.
					WeekFields weekFields = WeekFields.of(Locale.GERMANY);
					LocalDate ld = LocalDate.now()
							.withYear(training[1])
							.with(weekFields.weekOfYear(), training[0])
							.with(weekFields.dayOfWeek(), dayOfWeek);

					String dateString = formatter.format(ld);

					// find hrf of that training week
					// COTrainer from VEREIN,HRF_ID
					// TRAINER from SPIELER,HRF_ID && TRAINER>0
					// TrainingDate from XTRA,HRF_ID

					try (ResultSet rset = connectionManager.executePreparedQuery(trainingAfterDateSql, dateString)) {
						if (rset != null) {
							rset.next();
							var trainingDate = rset.getTimestamp("TRAININGDATE");
							var coTrainer = rset.getInt("COTRAINER");
							var trainer = rset.getInt("TRAINER");

							// update new columns
							connectionManager.executePreparedUpdate(updateStatement, trainingDate, coTrainer, trainer, DBDataSource.MANUAL.getValue(), training[1], training[0]);
						}
					}
				}
			} catch (Exception e) {
				HOLogger.instance().error(getClass(), "Error when trying to migrate existing entries of TRAININGS table: " + e);
			}

			// Step 3. Finalize upgrade of Training table structure ===============================
			trainingTable.tryChangeColumn("COACH_LEVEL", "NOT NULL");
			trainingTable.tryChangeColumn("TRAINING_ASSISTANTS_LEVEL", "NOT NULL");
			trainingTable.tryChangeColumn("TRAINING_DATE", "NOT NULL");
			trainingTable.tryChangeColumn("SOURCE", "NOT NULL");
			trainingTable.tryRenameColumn("TYP", "TRAINING_TYPE");
			trainingTable.tryRenameColumn("INTENSITY", "TRAINING_INTENSITY");
			trainingTable.tryRenameColumn("STAMINATRAININGPART", "STAMINA_SHARE");
			trainingTable.tryDeleteColumn("YEAR");
			trainingTable.tryDeleteColumn("WEEK");
		}

		// Upgrade FutureTraining table ======================================================================================
		if (columnExistsInTable("COACH_LEVEL", FutureTrainingTable.TABLENAME)) {
			HOLogger.instance().debug(getClass(), "Upgrade of FutureTraining table was already performed ... process skipped !");
		}
		else {
			// Step 1. Add new columns in FUTURETRAININGS table ===========================================================
			var futureTrainingTable = dbManager.getTable(FutureTrainingTable.TABLENAME);
			futureTrainingTable.tryAddColumn("COACH_LEVEL", "INTEGER");
			futureTrainingTable.tryAddColumn("TRAINING_ASSISTANTS_LEVEL", "INTEGER");
			futureTrainingTable.tryAddColumn("TRAINING_DATE", "TIMESTAMP");
			futureTrainingTable.tryAddColumn("SOURCE", "INTEGER");


			// Step 2: update columns with non-null values to ensure NOT NULL clauses can be called
			// we store week and season information for future treatment
			String sql = "UPDATE " + FutureTrainingTable.TABLENAME +
					" SET TRAINING_DATE=timestamp('1900-01-01'), TRAINING_ASSISTANTS_LEVEL=WEEK, COACH_LEVEL=SEASON, SOURCE=" +
					DBDataSource.MANUAL.getValue() + " WHERE TRUE";
			connectionManager.executeUpdate(sql);

			// Step 3. Finalize upgrade of FUTURETRAININGS table structure ===============================
			futureTrainingTable.tryChangeColumn("COACH_LEVEL", "NOT NULL");
			futureTrainingTable.tryChangeColumn("TRAINING_ASSISTANTS_LEVEL", "NOT NULL");
			futureTrainingTable.tryChangeColumn("TRAINING_DATE", "NOT NULL");
			futureTrainingTable.tryChangeColumn("SOURCE", "NOT NULL");
			futureTrainingTable.tryRenameColumn("TYPE", "TRAINING_TYPE");
			futureTrainingTable.tryRenameColumn("INTENSITY", "TRAINING_INTENSITY");
			futureTrainingTable.tryRenameColumn("STAMINATRAININGPART", "STAMINA_SHARE");
			futureTrainingTable.tryDeleteColumn("SEASON");
			futureTrainingTable.tryDeleteColumn("WEEK");
		}

		// SourceSystem does NOT exist in HO4, but HeimName does
		if (columnExistsInTable("HeimName", MatchLineupTable.TABLENAME)) {

			HOLogger.instance().debug(getClass(), "Upgrading DB structure SourceSystem/MatchType .... ");
			var matchLineupTable = dbManager.getTable(MatchLineupTable.TABLENAME);
			var matchesKurzInfoTable = dbManager.getTable(MatchesKurzInfoTable.TABLENAME);
			var matchIFATable = dbManager.getTable(IfaMatchTable.TABLENAME);

			matchesKurzInfoTable.tryDropPrimaryKey();
			matchLineupTable.tryDropPrimaryKey();
			matchDetailsTable.tryDropPrimaryKey();
			matchIFATable.tryDropPrimaryKey();

			// Update primary key from matchID => (matchID, MATCHTYP) because doublons might otherwise exists
			connectionManager.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD PRIMARY KEY (MATCHID, MATCHTYP)");
			connectionManager.executeUpdate("ALTER TABLE MATCHLINEUP ADD PRIMARY KEY (MATCHID, MATCHTYP)");

			matchLineupTable.tryDeleteColumn("SourceSystem");
			matchLineupTable.tryDeleteColumn("HeimName");
			matchLineupTable.tryDeleteColumn("HeimID");
			matchLineupTable.tryDeleteColumn("GastName");
			matchLineupTable.tryDeleteColumn("GastID");
			matchLineupTable.tryDeleteColumn("FetchDate");
			matchLineupTable.tryDeleteColumn("MatchDate");
			matchLineupTable.tryDeleteColumn("ArenaID");
			matchLineupTable.tryDeleteColumn("ArenaName");

			dbManager.getTable(MatchHighlightsTable.TABLENAME).tryDeleteColumn("SourceSystem");
			dbManager.getTable(MatchLineupTeamTable.TABLENAME).tryDeleteColumn("SourceSystem");
			dbManager.getTable(MatchSubstitutionTable.TABLENAME).tryDeleteColumn("SourceSystem");
			dbManager.getTable(MatchDetailsTable.TABLENAME).tryDeleteColumn("SourceSystem");
			dbManager.getTable(MatchLineupPlayerTable.TABLENAME).tryDeleteColumn("SourceSystem");

			dbManager.getTable(IfaMatchTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");
			dbManager.getTable(MatchDetailsTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");
			dbManager.getTable(MatchHighlightsTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");
			dbManager.getTable(MatchLineupTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");
			dbManager.getTable(MatchLineupPlayerTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");
			dbManager.getTable(MatchLineupTeamTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");
			dbManager.getTable(MatchSubstitutionTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");
			dbManager.getTable(YouthTrainingTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");

			// Correct history of MATCHESKURZINFO  ==============================================
			String sql = """
					UPDATE MATCHESKURZINFO
					SET MATCHTYP =
					    CASE MATCHTYP
					        WHEN 1001 THEN 3
					        WHEN 1002 THEN 3
					        WHEN 1003 THEN 3
					        WHEN 1004 THEN 3
					        WHEN 1101 THEN 50
					        ELSE -1
					    END
					WHERE
					    MATCHTYP IN (1001, 1002, 1003, 1004, 1101)""";

			connectionManager.executeUpdate(sql);

			// Set MatchType in all table but YouthTable from entry in MATCHESKURZINFO =============================
			// use match lineup table to fix match types, since the lineup table holds the youth matches too
			// the types in lineup table seems to be the correct one - at least in my database (ws) - no fake types of sapphire cup and co.
			//List<String> lTables = List.of("IFA_MATCHES", "MATCHDETAILS", "MATCHLINEUP", "MATCHHIGHLIGHTS", "MATCHLINEUPPLAYER", "MATCHLINEUPTEAM",
			//		"MATCHORDER", "MATCHSUBSTITUTION");

			copyMatchTypes("MATCHESKURZINFO", "IFA_MATCHES");
			if (tableExists("MATCHORDER") && dbVersion<600 ) copyMatchTypes("MATCHESKURZINFO", "MATCHORDER");		// no lineup available yet for match orders
			copyMatchTypes("MATCHLINEUP", "MATCHDETAILS");
			copyMatchTypes("MATCHLINEUP", "MATCHHIGHLIGHTS");
			copyMatchTypes("MATCHLINEUP", "MATCHLINEUPPLAYER");
			copyMatchTypes("MATCHLINEUP", "MATCHLINEUPTEAM");
			copyMatchTypes("MATCHLINEUP", "MATCHSUBSTITUTION");
			copyMatchTypes("MATCHLINEUP", "YOUTHTRAINING");

			// Update primary key from matchID => (matchID, MATCHTYP) because doublons might otherwise exists
			connectionManager.executeUpdate("ALTER TABLE IFA_MATCHES ADD PRIMARY KEY (MATCHID, MATCHTYP)");
			connectionManager.executeUpdate("ALTER TABLE MATCHDETAILS ADD PRIMARY KEY (MATCHID, MATCHTYP)");

			HOLogger.instance().debug(getClass(), "Upgrade of DB structure SourceSystem/MatchType is complete ! ");
		}

		if (!columnExistsInTable("LAST_MATCH_TYPE", "SPIELER")) {
			connectionManager.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LAST_MATCH_TYPE INTEGER ");
		}

		// Delete corrupt entries (wrong week numbers) from TA_PLAYER table
		var hrfTable = (HRFTable)dbManager.getTable(HRFTable.TABLENAME);
		var hrf = hrfTable.getLatestHrf();
		if ( hrf.isOK() ) {
			connectionManager.executeUpdate("DELETE FROM " + TAPlayerTable.TABLENAME
					+ " WHERE WEEK> (SELECT SAISON*16+SPIELTAG-1 FROM "
					+ basicsTable.getTableName() + " WHERE HRF_ID=" + hrf.getHrfId() + ")" );
		}

		dbManager.getTable(MatchesKurzInfoTable.TABLENAME).tryAddColumn("isObsolete", "BOOLEAN");

		if (!tableExists(MatchTeamRatingTable.TABLENAME)) {
			dbManager.getTable(MatchTeamRatingTable.TABLENAME).createTable();
		}

		dbManager.getTable(XtraDataTable.TABLENAME).tryAddColumn("CountryId", "INTEGER");

		updateDBVersion(dbVersion, 500);
	}

	private void copyMatchTypes(String fromTable, String toTable) {
		String sql = "UPDATE " + toTable + " t1 SET MATCHTYP = (SELECT MK.MATCHTYP FROM " + fromTable +" MK WHERE t1.MATCHID = MK.MATCHID)";
		connectionManager.executeUpdate(sql);
		sql = "UPDATE " + toTable + " SET MATCHTYP = 0 WHERE MATCHTYP IS NULL";
		connectionManager.executeUpdate(sql);
	}

	private void updateDBv400(int dbVersion) throws SQLException {
		// Delete existing values to provide sane defaults.
		connectionManager.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'spielerUebersichtsPanel_horizontalRightSplitPane'");
		connectionManager.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'aufstellungsPanel_verticalSplitPane'");
		connectionManager.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'aufstellungsPanel_horizontalRightSplitPane'");
		connectionManager.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'aufstellungsPanel_horizontalLeftSplitPane'");

		if (!columnExistsInTable("SeasonOffset", BasicsTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE BASICS ADD COLUMN SeasonOffset INTEGER");
		}

		if (!columnExistsInTable("Duration", MatchesKurzInfoTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN Duration INTEGER ");
		}
		if (!columnExistsInTable("MatchPart", MatchHighlightsTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN MatchPart INTEGER ");
			connectionManager.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN EventVariation INTEGER ");
		}
		if (!columnExistsInTable("HomeGoal0", MatchDetailsTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal0 INTEGER ");
			connectionManager.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal1 INTEGER ");
			connectionManager.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal2 INTEGER ");
			connectionManager.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal3 INTEGER ");
			connectionManager.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal4 INTEGER ");
			connectionManager.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal0 INTEGER ");
			connectionManager.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal1 INTEGER ");
			connectionManager.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal2 INTEGER ");
			connectionManager.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal3 INTEGER ");
			connectionManager.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal4 INTEGER ");
		}

		if (!columnExistsInTable("NAME", TAPlayerTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN NAME VARCHAR (100) ");
		}

		// use defaults player formula from defaults.xml by resetting the value in the database
		try {
			AbstractTable faktorenTab = dbManager.getTable(FaktorenTable.TABLENAME);
			if (faktorenTab != null) {
				faktorenTab.tryDropTable();
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

		updateDBVersion(dbVersion, 400);
	}

	private void updateDBv301(int dbVersion) throws SQLException {

		connectionManager.executeUpdate("ALTER TABLE MATCHESKURZINFO ALTER COLUMN isDerby SET DATA TYPE BOOLEAN");
		connectionManager.executeUpdate("ALTER TABLE MATCHESKURZINFO ALTER COLUMN isNeutral SET DATA TYPE BOOLEAN");

		if (!columnExistsInTable("EVENT_INDEX", MatchHighlightsTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN EVENT_INDEX INTEGER");
		}

		if (!columnExistsInTable("INJURY_TYPE", MatchHighlightsTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN INJURY_TYPE TINYINT");
		}

		if (columnExistsInTable("TYP", MatchHighlightsTable.TABLENAME)) {
			connectionManager.executeUpdate("UPDATE MATCHHIGHLIGHTS SET MATCH_EVENT_ID = (TYP * 100) + SUBTYP WHERE MATCH_EVENT_ID IS NULL");
			connectionManager.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS DROP TYP");
		}

		if (!columnExistsInTable("LastMatchDate", SpielerTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LastMatchDate VARCHAR (100)");
		}
		if (!columnExistsInTable("LastMatchRating", SpielerTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LastMatchRating INTEGER");
		}
		if (!columnExistsInTable("LastMatchId", SpielerTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LastMatchId INTEGER");
		}

		Arrays.asList("HEIMTORE", "GASTTORE", "SUBTYP").forEach(s -> {
			try {
				if (columnExistsInTable(s, MatchHighlightsTable.TABLENAME)) {
					connectionManager.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS DROP " + s);
				}
			} catch (SQLException e) {
				HOLogger.instance().log(getClass(), e);
			}
		});

		connectionManager.executeUpdate("CREATE INDEX IF NOT EXISTS matchdetails_heimid_idx ON MATCHDETAILS (HEIMID)");
		connectionManager.executeUpdate("CREATE INDEX IF NOT EXISTS matchdetails_gastid_idx ON MATCHDETAILS (GASTID)");
		connectionManager.executeUpdate("CREATE INDEX IF NOT EXISTS matchkurzinfo_heimid_idx ON MATCHESKURZINFO (HEIMID)");
		connectionManager.executeUpdate("CREATE INDEX IF NOT EXISTS matchkurzinfo_gastid_idx ON MATCHESKURZINFO (GASTID)");
		connectionManager.executeUpdate("CREATE INDEX IF NOT EXISTS matchhighlights_teamid_idx ON MATCHHIGHLIGHTS (TEAMID)");
		connectionManager.executeUpdate("CREATE INDEX IF NOT EXISTS matchhighlights_eventid_idx ON MATCHHIGHLIGHTS (MATCH_EVENT_ID)");

		Arrays.asList("GlobalRanking", "LeagueRanking", "RegionRanking", "PowerRating").forEach(s -> {
			try {
				if (!columnExistsInTable(s, VereinTable.TABLENAME)) {
					connectionManager.executeUpdate(String.format("ALTER TABLE VEREIN ADD COLUMN %s INTEGER", s));
				}
			} catch (SQLException e) {
				HOLogger.instance().log(getClass(), e);
			}
		});

		Arrays.asList("TWTrainer", "Physiologen").forEach(s -> {
			try {
				if (columnExistsInTable(s, VereinTable.TABLENAME)) {
					connectionManager.executeUpdate("ALTER TABLE VEREIN DROP " + s);
				}
			} catch (SQLException e) {
				HOLogger.instance().log(getClass(), e);
			}
		});

		updateDBVersion(dbVersion, 301);

	}

	private void updateDBv300() throws SQLException {
		// HO 3.0

		// delete old divider locations
		connectionManager.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='teamAnalyzer_LowerLefSplitPane'");
		connectionManager.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='teamAnalyzer_UpperLeftSplitPane'");
		connectionManager.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='teamAnalyzer_MainSplitPane'");

		//store ArenaId into MATCHESKURZINFO table
		if (!columnExistsInTable("ArenaId", MatchesKurzInfoTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN ArenaId INTEGER");
		}

		//store RegionId into MATCHESKURZINFO table
		if (!columnExistsInTable("RegionId", MatchesKurzInfoTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN RegionId INTEGER");
		}

		//store Weather into MATCHESKURZINFO table
		if (!columnExistsInTable("Weather", MatchesKurzInfoTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN Weather INTEGER");
		}

		//store WeatherForecast into MATCHESKURZINFO table
		if (!columnExistsInTable("WeatherForecast", MatchesKurzInfoTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN WeatherForecast INTEGER");
		}

		//store isDerby into MATCHESKURZINFO table
		if (!columnExistsInTable("isDerby", MatchesKurzInfoTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN isDerby BOOLEAN");
		}

		//store isNeutral into MATCHESKURZINFO table
		if (!columnExistsInTable("isNeutral", MatchesKurzInfoTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN isNeutral BOOLEAN");
		}

		//store Salary into TA_PLAYER table
		if (!columnExistsInTable("SALARY", TAPlayerTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN SALARY INTEGER");
		}

		//store Stamina  into TA_PLAYER table
		if (!columnExistsInTable("STAMINA", TAPlayerTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN STAMINA INTEGER");
		}

		//store MotherClubBonus  into TA_PLAYER table
		if (!columnExistsInTable("MOTHERCLUBBONUS", TAPlayerTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN MOTHERCLUBBONUS BOOLEAN");
		}

		//store Loyalty  into TA_PLAYER table
		if (!columnExistsInTable("LOYALTY", TAPlayerTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN LOYALTY INTEGER");
		}

		//store RATINGINDIRECTSETPIECESATT  into MATCHDETAILS table
		if (!columnExistsInTable("RATINGINDIRECTSETPIECESATT", MatchDetailsTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN RATINGINDIRECTSETPIECESATT INTEGER");
		}

		//store RATINGINDIRECTSETPIECESDEF  into MATCHDETAILS table
		if (!columnExistsInTable("RATINGINDIRECTSETPIECESDEF", MatchDetailsTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN RATINGINDIRECTSETPIECESDEF INTEGER");
		}

		//store FirstName, Nickname  into Playertable
		if (!columnExistsInTable("FirstName", SpielerTable.TABLENAME)) {
			connectionManager.executeUpdate("ALTER TABLE SPIELER ADD COLUMN FirstName VARCHAR (100)");
			connectionManager.executeUpdate("ALTER TABLE SPIELER ADD COLUMN NickName VARCHAR (100)");
			connectionManager.executeUpdate("ALTER TABLE SPIELER ALTER COLUMN Name RENAME TO LastName");
		}

		// Delete league plans which are not of our own team
		try {
			// find own league plans
			int teamId = getTeamId();
			// select saison,ligaid from paarung where heimid=520472 group by saison,ligaid
			HashMap<Integer, Integer> ownLeaguePlans = new HashMap<>();
			ResultSet rs = connectionManager.executeQuery("select saison,ligaid from paarung where heimid=" + teamId + " group by saison,ligaid");
			if (rs != null) {
				while (rs.next()) {
					int saison = rs.getInt(1);
					int league = rs.getInt(2);
					ownLeaguePlans.put(saison, league);
				}
				rs.close();
			}
			// delete entries in SPIELPLAN and PAARUNG which are not from own team
			rs = connectionManager.executeQuery("select saison,ligaid from spielplan");
			if (rs != null) {
				while (rs.next()) {
					int saison = rs.getInt(1);
					int league = rs.getInt(2);
					if (!ownLeaguePlans.containsKey(saison) || ownLeaguePlans.get(saison) != league) {
						// league is not our own one
						connectionManager.executeUpdate("DELETE FROM spielplan WHERE ligaid=" + league + " and saison=" + saison);
						connectionManager.executeUpdate("DELETE FROM paarung WHERE ligaid=" + league + " and saison=" + saison);
					}
				}
				rs.close();
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
		HOLogger.instance().info(DBUpdater.class, "updateDBv300() successfully completed");
	}

	private void updateDBVersion(int DBVersion, int version) {
		if (version < DBVersion) {
			if (!HO.isDevelopment()) {
				HOLogger.instance().info(DBUpdater.class, "Update to " + version + " done. Updating DBVersion");
				dbManager.saveUserParameter("DBVersion", version);
			}
			else {
				HOLogger.instance().debug(DBUpdater.class, "Update to " + version + " done but this is a development version so DBVersion will remain unchanged");
			}
		}
		else if (version == DBVersion) {
			if (!HO.isDevelopment()) {
				HOLogger.instance().info(DBUpdater.class, "Update complete, setting DBVersion to " + version);
				dbManager.saveUserParameter("DBVersion", version);
			} else {
				HOLogger.instance().debug(DBUpdater.class, "Update to " + version + " complete but this is a development version so DBVersion will remain unchanged");
			}
		}
		else {
			HOLogger.instance().error(DBUpdater.class, "Error trying to set DB version to unidentified value:  " + version
							+ " (isDevelopment=" + HO.isDevelopment() + ")");
		}
	}

	private int getTeamId() {
		try {
			ResultSet rs = connectionManager.executeQuery("select teamid from basics limit 1");
			if (rs != null) {
				rs.next();
				int ret = rs.getInt(1);
				rs.close();
				return ret;
			}
		} catch (SQLException e) {
			HOLogger.instance().log(getClass(), e);
		}
		return 0;
	}

	private boolean columnExistsInTable(String columnName, String tableName) throws SQLException {
		String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME = '" +
				tableName.toUpperCase() + "' AND COLUMN_NAME = '" + columnName.toUpperCase() + "'";
		ResultSet rs = this.connectionManager.executeQuery(sql);
		if (rs != null) return rs.next();
		return false;
	}

	private boolean tableExists(String tableName) throws SQLException {
		String sql = "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME = '" + tableName.toUpperCase() + "'";
		ResultSet rs = this.connectionManager.executeQuery(sql);
		if (rs != null) return rs.next();
		return false;
	}

	private void resetUserColumns() {
		HOLogger.instance().debug(getClass(), "Resetting player overview rows.");
		String sql = "DELETE FROM USERCOLUMNS WHERE COLUMN_ID BETWEEN 2000 AND 3000";
		connectionManager.executeUpdate(sql);

		HOLogger.instance().debug(getClass(), "Resetting lineup overview rows.");
		sql = "DELETE FROM USERCOLUMNS WHERE COLUMN_ID BETWEEN 3000 AND 4000";
		connectionManager.executeUpdate(sql);
	}
}
