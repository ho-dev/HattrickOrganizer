package module.tsforecast;

import core.model.HOVerwaltung;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

class TrainerCurve extends Curve {

	TrainerCurve() {
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
							+ new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
									.format(d) + " has no leadership!");
			dRet = 1; // quick hack to fix start problems when there are only
						// few old datasets
		}
		return dRet;
	}

	double getCurrentLeadership() {
		last();
		return getSpirit();
	}

	private void readTrainer() {
		var start = HOVerwaltung.instance().getModel().getBasics().getDatum().minus(WEEKS_BACK, ChronoUnit.WEEKS);

		int iLeadership;
		int iLastLeadership = -1;
		int iID;
		int iLastID = -1;

		// get last skill just before start date
		ResultSet resultset = m_clJDBC
				.executeQuery("select SPIELERID, FUEHRUNG, DATUM from SPIELER "
						+ "where TRAINERTYP <> -1 and DATUM <= '" + start
						+ "' order by DATUM desc");
		try {
			boolean gotInitial = false;
			assert resultset != null;
			if (resultset.next()) {
				iLeadership = resultset.getInt("FUEHRUNG");
				m_clPoints.add(new Point(HODateTime.fromDbTimestamp(resultset.getTimestamp("DATUM")),
						iLeadership, START_TRAINER_PT));
				gotInitial = true;
			}

			resultset = m_clJDBC
					.executeQuery("select SPIELERID, FUEHRUNG, DATUM from SPIELER "
							+ "where TRAINERTYP <> -1 and DATUM > '"
							+ start
							+ "' and DATUM < '"
							+ HOVerwaltung.instance().getModel().getBasics()
									.getDatum() + "' order by DATUM");
			while (true) {
				assert resultset != null;
				if (!resultset.next()) break;
				iLeadership = resultset.getInt("FUEHRUNG");
				iID = resultset.getInt("SPIELERID");

				if (!gotInitial) { // initial trainer unknown (database too
									// young)
					m_clPoints.add(new Point(HODateTime.fromDbTimestamp(resultset.getTimestamp("DATUM")),
							iLeadership, START_TRAINER_PT));
					gotInitial = true;
				}
				if (iID != iLastID) { // New Trainer
					m_clPoints.add(new Point(HODateTime.fromDbTimestamp(resultset.getTimestamp("DATUM")),
							iLeadership, NEW_TRAINER_PT));
				} else if (iLastLeadership != -1
						&& iLeadership != iLastLeadership) { // Trainer
																// Skilldown
					m_clPoints.add(new Point(HODateTime.fromDbTimestamp(resultset.getTimestamp("DATUM")),
							iLeadership, TRAINER_DOWN_PT));
				}
				iLastLeadership = iLeadership;
				iLastID = iID;
			}
		} catch (Exception e) {
			HOLogger.instance().error(this.getClass(), e);
		}
	}

}