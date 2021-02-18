package core.db;

import core.HO;
import core.training.TrainingWeekManager;
import core.util.HOLogger;

import javax.swing.*;
import java.sql.ResultSet;
import java.time.Instant;


final class DBConfigUpdater {
	final static DBManager dbManager = DBManager.instance();
	final static JDBCAdapter m_clJDBCAdapter = dbManager.getAdapter();


	public static void updateDBConfig(double configVersion) {

		double currentConfigVersion = ((UserConfigurationTable) dbManager.getTable(UserConfigurationTable.TABLENAME)).getLastConfUpdate();

		/*
		 * We have to use separate 'if-then' clauses for each conf version  (ascending order) because a user might have skipped some HO releases
		 * DO NOT use 'if-then-else' here, as this would ignores some updates!
		 */

		if (currentConfigVersion < 1.436) {
			HOLogger.instance().log(DBConfigUpdater.class, "DB config version " + configVersion + " is too old");
			try {
				JOptionPane.showMessageDialog(null,
						"DB is too old.\nPlease update first to HO! 3.0", "Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				HOLogger.instance().log(DBConfigUpdater.class, e);
			}
			System.exit(0);
		}

		if ((currentConfigVersion < 5) || (HO.isDevelopment() && (currentConfigVersion == 5))) {
			HOLogger.instance().log(DBConfigUpdater.class, "Updating configuration to version 5.0  ...");
			updateDBConfig5(configVersion, HO.isDevelopment() && (currentConfigVersion == 5));
		}

	}

	private static void updateDBConfig5(double configVersion, boolean alreadyApplied){

		if (!alreadyApplied) {
			// Creating entries into TRAININGS table ===========================================================
			try {
				String sql = "SELECT TRAININGDATE FROM XTRADATA ORDER BY TRAININGDATE ASC LIMIT 1";
				ResultSet rs = m_clJDBCAdapter.executeQuery(sql);
				rs.next();
				Instant firstTrainingDate = rs.getTimestamp("TRAININGDATE").toInstant();
				TrainingWeekManager twm = new TrainingWeekManager(firstTrainingDate, false, false);
				twm.push2TrainingsTable();
			} catch (Exception e) {
				HOLogger.instance().error(DBConfigUpdater.class, "Error when trying to create entries inside TRAINING table");
			}

			// Creating entries into FUTURETRAININGS table ===========================================================
			HOLogger.instance().debug(DBConfigUpdater.class, "@wsbrenk code logic for migration of FuturesTraining table entries should be made here, I am not sure this is required");

			updateDBConfigVersion(configVersion, 5d);
		}
	}


	private static void updateDBConfigVersion(double DBConfigVersion, double version) {
		if (version < DBConfigVersion) {
			if(!HO.isDevelopment()) {
				HOLogger.instance().debug(DBConfigUpdater.class, "Update to config " + version + " done. Updating DBConfigVersion");
				dbManager.saveUserParameter("LastConfUpdate", version);
			}
			else {
				HOLogger.instance().debug(DBConfigUpdater.class, "Update to config " + version + " done but this is a development version so DBConfigVersion will remain unchanged");
			}
		}
		else if (version == DBConfigVersion){
			if(!HO.isDevelopment()) {
				HOLogger.instance().debug(DBConfigUpdater.class, "DB config update complete, setting DBConfigVersion to " + version);
				dbManager.saveUserParameter("LastConfUpdate", version);
			}
			else {
				HOLogger.instance().debug(DBConfigUpdater.class, "Update to config " + version + " complete but this is a development version so DBConfigVersion will remain unchanged");
			}
		}
		else {
			HOLogger.instance().error(DBConfigUpdater.class,
					"Error trying to set DB version to unidentified value:  " + version + " (isDevelopment=" + HO.isDevelopment() + ")");
		}
	}



}
