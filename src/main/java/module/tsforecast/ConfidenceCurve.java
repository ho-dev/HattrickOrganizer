package module.tsforecast;

/*
 * ConfidenceCurve.java
 *
 * Created on 26.August 2006, 11:04
 *
 *Version 0.1
 *history :
 *18.03.06  Version 0.1 rebuilt
 */

/*
 *
 * @author  michael.roux
 */
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.util.HODateTime;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;

class ConfidenceCurve extends Curve {

	ConfidenceCurve(DBManager dbManager) throws SQLException {
		super(dbManager);
		readConfidenceHistory();
	}

	private void readConfidenceHistory() throws SQLException {
		var start = HOVerwaltung.instance().getModel().getBasics().getDatum().minus(WEEKS_BACK*7, ChronoUnit.DAYS);
		try (ResultSet resultset = dbManager.getConnectionManager().executePreparedQuery("select DATUM, ISELBSTVERTRAUEN from HRF, TEAM where HRF.HRF_ID = TEAM.HRF_ID order by DATUM")) {
			if (resultset != null) {
				while (resultset.next()) {
					var date = HODateTime.fromDbTimestamp(resultset.getTimestamp("DATUM"));
					if (start.isBefore(date)
							&& !HOVerwaltung.instance().getModel().getBasics().getDatum().isBefore(date)) {
						m_clPoints.add(new Point(date, 1 + resultset.getInt("ISELBSTVERTRAUEN"), -1));
					}
				}
			}
		}
	}
}