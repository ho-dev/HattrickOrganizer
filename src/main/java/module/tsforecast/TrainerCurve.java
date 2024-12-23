package module.tsforecast;

import core.db.ConnectionManager;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.time.temporal.ChronoUnit;

public class TrainerCurve extends Curve {

	TrainerCurve(DBManager dbManager) {
		super(dbManager);
		readTrainer();
	}

	double getLeadership(HODateTime d) {
		double dRet = -1;
		if (d != null) {
			for (Point p : m_clPoints) {
				if (p.m_dDate.isBefore(d))
					dRet = p.m_dSpirit;
			}
		} else {
			throw new NullPointerException("Given date is null!");
		}
		if (dRet < 0) {
			HOLogger.instance().debug(
					this.getClass(),
					"Trainer for "
							+ d.toLocaleDateTime() + " has no leadership!");
			dRet = 1; // quick hack to fix start problems when there are only
						// few old datasets
		}
		return dRet;
	}

	private static final String readTrainerBeforeStartSql = "select SPIELERID, FUEHRUNG, DATUM from SPIELER where TRAINERTYP <> -1 and DATUM <= ? order by DATUM desc";
	private static final String readTrainerSql = "select SPIELERID, FUEHRUNG, DATUM from SPIELER where TRAINERTYP <> -1 and DATUM > ? and DATUM <= ? order by DATUM";
	private void readTrainer() {
		var start = HOVerwaltung.instance().getModel().getBasics().getDatum().minus(WEEKS_BACK*7, ChronoUnit.DAYS);

		int iLeadership;
		int iLastLeadership = -1;
		int iID;
		int iLastID = -1;

		ConnectionManager connectionManager = dbManager.getConnectionManager();

		// get last skill just before start date
		try (ResultSet resultset = connectionManager.executePreparedQuery(readTrainerBeforeStartSql, start.toDbTimestamp())) {
			boolean gotInitial = false;
			assert resultset != null;
			if (resultset.next()) {
				iLeadership = resultset.getInt("FUEHRUNG");
				m_clPoints.add(new Point(HODateTime.fromDbTimestamp(resultset.getTimestamp("DATUM")),
						iLeadership, START_TRAINER_PT));
				gotInitial = true;
			}

			try (ResultSet rs = connectionManager.executePreparedQuery(readTrainerSql, start.toDbTimestamp(), HOVerwaltung.instance().getModel().getBasics().getDatum().toDbTimestamp())) {
				while (true) {
					assert rs != null;
					if (!rs.next()) break;
					iLeadership = rs.getInt("FUEHRUNG");
					iID = rs.getInt("SPIELERID");

					if (!gotInitial) { // initial trainer unknown (database too
						// young)
						m_clPoints.add(new Point(HODateTime.fromDbTimestamp(rs.getTimestamp("DATUM")),
								iLeadership, START_TRAINER_PT));
						gotInitial = true;
					}
					if (iID != iLastID) { // New Trainer
						m_clPoints.add(new Point(HODateTime.fromDbTimestamp(rs.getTimestamp("DATUM")),
								iLeadership, NEW_TRAINER_PT));
					} else if (iLastLeadership != -1
							&& iLeadership != iLastLeadership) { // Trainer
						// Skilldown
						m_clPoints.add(new Point(HODateTime.fromDbTimestamp(rs.getTimestamp("DATUM")),
								iLeadership, TRAINER_DOWN_PT));
					}
					iLastLeadership = iLeadership;
					iLastID = iID;
				}
			}
		} catch (Exception e) {
			HOLogger.instance().error(this.getClass(), e);
		}
	}

}