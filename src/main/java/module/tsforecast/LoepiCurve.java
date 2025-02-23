package module.tsforecast;

/*
 * LoepiCurve.java
 *
 * Created on 22.March 2006, 11:04
 *
 *Version 0.21
 *history :
 *22.03.06  Version 0.1 Creation
 *31.03.06  Version 0.2 Improve forecast function and constructor
 *26.08.06  Version 0.21 rebuilt
 *04.09.06  Version 0.3 Change formula to a trainerLS-based formula
 */

import core.db.DBManager;

import java.sql.SQLException;

public class LoepiCurve extends ForecastCurve {

	private final TrainerCurve m_TrainerCurve;

	public LoepiCurve(DBManager dbManager, TrainerCurve t, boolean future) throws SQLException {
		super(dbManager, future);
		m_TrainerCurve = t;
	}

	// -- protected
	// ------------------------------------------------------------------------

	// TS über 4,5:
	// TS(neu)=TS(alt)*(1-((TS(alt)-4.5)/(FQ/3))/100)
	// alte Formel: TS(neu)=TS(alt)*(1-((TS(alt)-4,5)*Faktor)/100).
	// TS unter 4,5:
	// TS(neu)=TS(alt)*(1+((TS(alt)-4.5)*(-1)*(FQ/2))/100)
	// alte Formel: TS(neu)=TS(alt)*(1+((TS(alt)-4,5)*(-1)/(Faktor/1,5))/100).

	@Override
	protected double forecastUpdate(Curve.Point point1, Curve.Point point2) {

		double dRet = point1.m_dSpirit;
		var targetSpirit = getTargetSpirit();
		if (dRet >= targetSpirit) {
			dRet *= 1.0D - (((dRet - targetSpirit) / (m_TrainerCurve
					.getLeadership(point1.m_dDate) / 3D)) / 100D);
		} else {
			dRet *= 1.0D + (((dRet - targetSpirit) * -1D)
					* (m_TrainerCurve.getLeadership(point1.m_dDate) / 2D) / 100D);
		}
		if (point1.trainingIntensity != point2.trainingIntensity) {
			if (point2.trainingIntensity == -1) {
				point2.trainingIntensity = point1.trainingIntensity;
			} else {
				dRet = calculateTeamSpiritBoost(dRet, point1.trainingIntensity, point2.trainingIntensity);
			}
		}
		return dRet;
	}

}