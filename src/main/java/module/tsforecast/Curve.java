package module.tsforecast;

/*
 * Curve.java
 *
 * Created on 18.March 2006, 11:04
 *
 *Version 0.5
 *history :
 *18.03.06  Version 0.1
 *19.03.06  Version 0.2  subclassing
 *02.04.06  Version 0.3  added Matchday in Point
 *24.08.06  Version 0.31 rebuilt
 *19.02.07  Version 0.4  added Trainer Start Point 
 *21.02.07  Version 0.5  added tooltip to point
 */

/*
 *
 * @author  michael.roux
 */

import core.db.ConnectionManager;
import core.db.DBManager;
import core.model.match.IMatchDetails;
import core.model.enums.MatchType;
import core.util.HODateTime;

import java.awt.Color;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;

class Curve {

	static final int WEEKS_BACK = 26;

	static final int RESET = -1;
	static final MatchType UNKNOWN_MATCH = MatchType.NONE;

	static final double TEAM_SPIRIT_UNKNOWN = -1D;
	static final double TEAM_SPIRIT_RESET = 4.5D;

	static final int STANDARD_PT = 0;
	static final int RESET_PT = 1;
	static final int NEW_TRAINER_PT = 10;
	static final int TRAINER_DOWN_PT = 11;
	static final int START_TRAINER_PT = 12;

	protected DBManager dbManager;

	protected ArrayList<Point> m_clPoints = new ArrayList<>();

	private Iterator<Point> m_clIterator = null;
	private Point m_currentPoint = null;
	private Color m_Color = null;

	class Point implements Comparable<Point> {

		double m_dSpirit;
		int m_iAttitude;
		HODateTime m_dDate;
		int m_iMatchDay;
		MatchType m_mtMatchType;
		int m_iPointType;
		String m_strTooltip = null;

		Point(Point point) {
			m_dDate = point.m_dDate;
			m_dSpirit = point.m_dSpirit;
			m_iAttitude = point.m_iAttitude;
			m_iMatchDay = point.m_iMatchDay;
			m_mtMatchType = point.m_mtMatchType;
			m_iPointType = point.m_iPointType;
			m_strTooltip = point.m_strTooltip;
		}

		Point(HODateTime date, double dSpirit, int iAttitude, int iMatchDay,
				MatchType iMatchType, int iPointType) {
			m_dDate = date;
			m_dSpirit = dSpirit;
			m_iAttitude = iAttitude;
			m_iMatchDay = iMatchDay;
			m_mtMatchType = iMatchType;
			m_iPointType = iPointType;
		}

		Point(HODateTime date, int iAttitude, int iMatchDay, MatchType matchType) {
			this(date, TEAM_SPIRIT_UNKNOWN, iAttitude, iMatchDay, matchType,
					STANDARD_PT);
		}

		Point(HODateTime date, double dSpirit) {
			this(date, dSpirit, IMatchDetails.EINSTELLUNG_UNBEKANNT, 0,
					UNKNOWN_MATCH, STANDARD_PT);
		}

		Point(HODateTime date, double dSpirit, int iPointType) { // Spirit or Coach
															// leadership
			this(date, dSpirit, IMatchDetails.EINSTELLUNG_UNBEKANNT, 0,
					UNKNOWN_MATCH, iPointType);
		}

		@Override
		public int compareTo(Point obj) {
			return m_dDate.compareTo(obj.m_dDate);
		}
	}

	Curve(DBManager dbManager) {
		this.dbManager = dbManager;
	}

	boolean first() {
		m_clIterator = m_clPoints.iterator();
		return !m_clPoints.isEmpty();
	}

	boolean last() {
		for (m_clIterator = m_clPoints.iterator(); m_clIterator.hasNext();)
			m_currentPoint = m_clIterator.next();
		return !m_clPoints.isEmpty();
	}

	boolean next() {
		if (m_clIterator.hasNext()) {
			m_currentPoint = m_clIterator.next();
			return true;
		}
		return false;

	}

	HODateTime getDate() {
		return m_currentPoint.m_dDate;
	}

	double getSpirit() {
		return m_currentPoint.m_dSpirit;
	}

	int getAttitude() {
		return m_currentPoint.m_iAttitude;
	}

	MatchType getMatchType() {
		return m_currentPoint.m_mtMatchType;
	}

	int getMatchDay() {
		return m_currentPoint.m_iMatchDay;
	}

	String getTooltip() {
		return m_currentPoint.m_strTooltip;
	}

	Color getColor() {
		return m_Color;
	}

	int getPointType() {
		return m_currentPoint.m_iPointType;
	}

	void setColor(Color color) {
		m_Color = color;
	}

	Point getFirstPoint() {
		return m_clPoints.get(0);
	}

	Point getLastPoint() {
		return m_clPoints.get(m_clPoints.size() - 1);
	}

	void addPoint(int i, Point point) {
		m_clPoints.add(i, new Point(point));
	}

	protected static int getDiffDays(Point point1, Point point2) {
		return (int)Duration.between(point1.m_dDate.instant, point2.m_dDate.instant).toDays();
	}

}