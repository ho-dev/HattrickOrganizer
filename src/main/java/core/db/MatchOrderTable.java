package core.db;

import core.model.enums.MatchType;
import core.model.player.MatchRoleID;
import core.net.MyConnector;
import core.net.OnlineWorker;
import core.util.HOLogger;
import module.lineup.Lineup;
import module.lineup.LineupPosition;


import java.sql.ResultSet;
import java.sql.Types;

/**
 *
 * Table to save players position for future matches: used by training preview
 * Save only: matchid, playerid and position id
 *
 * @author yaute
 */
public class MatchOrderTable extends AbstractTable {
	/** tablename **/
	public final static String TABLENAME = "MATCHORDER";

	// ~ Constructors
	// -------------------------------------------------------------------------------

	protected MatchOrderTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	// ~ Methods
	// -------------------------------------------------------------------------------

	@Override
	protected void initColumns() {

		columns = new ColumnDescriptor[4];
		columns[0] = new ColumnDescriptor("MatchID", Types.INTEGER, false);
		columns[1] = new ColumnDescriptor("MatchTyp", Types.INTEGER, false);
		columns[2] = new ColumnDescriptor("SpielerID", Types.INTEGER, false);
		columns[3] = new ColumnDescriptor("PositionCode", Types.INTEGER, false);
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
				"CREATE INDEX IMATCHORDER_1 ON " + getTableName() + "("
						+ columns[0].getColumnName() + ")" };
	}

	/**
	 * Get the lineup position of a futur match
	 *
	 * @param matchId:	match id
	 * @param matchTyp:	match type
	 * @return			lineup position of the match
	 */
	public LineupPosition getMatchOrder(int matchId, MatchType matchTyp) {

		LineupPosition lineupPos = new LineupPosition();

		try {
			String sql = "SELECT * FROM " + getTableName() + " WHERE MatchID = " + matchId;
			ResultSet rs = adapter.executeQuery(sql);

			rs.beforeFirst();
			if (rs.next()) {
				while (rs.next()) {
					lineupPos.addPosition(rs.getInt("PositionCode"), rs.getInt("SpielerID"));
				}
			}
			else {
				lineupPos = addMatchOrder(matchId, matchTyp);
			}

		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getMatchOrder Error" + e);
		}
		return lineupPos;
	}

	/**
	 * Get the lineup position of a futur match
	 *
	 * @param matchId:	match id
	 * @param matchTyp:	match type
	 * @return			lineup position of the match
	 */
	public LineupPosition getMatchOrder(int matchId, MatchType matchTyp, boolean verifyInternetAccess) {

		LineupPosition lineupPos = new LineupPosition();

		try {
			String sql = "SELECT * FROM " + getTableName() + " WHERE MatchID = " + matchId;
			ResultSet rs = adapter.executeQuery(sql);

			rs.beforeFirst();
			if (rs.next()) {
				while (rs.next()) {
					lineupPos.addPosition(rs.getInt("PositionCode"), rs.getInt("SpielerID"));
				}
			}
			else {
				if (! (verifyInternetAccess && (! MyConnector.hasInternetAccess()))){
					lineupPos = addMatchOrder(matchId, matchTyp);
				}
			}

		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getMatchOrder Error" + e);
		}
		return lineupPos;
	}


	/**
	 * Clean table
	 */
	public void removeMatchOrder() {
		adapter.executeUpdate("DELETE FROM " +  getTableName());
	}

	/**
	 * Add match order from CHPP request
	 *
	 * @param matchId:	match id
	 * @param matchTyp:	match type
	 * @return			lineup position of the match
	 */
	private LineupPosition addMatchOrder(int matchId, MatchType matchTyp) {
		Lineup linueup = OnlineWorker.getLineupbyMatchId(matchId, matchTyp);

		if (linueup != null) {
			LineupPosition lineupPos = new LineupPosition();
			insertMatchOrder(linueup, matchId, matchTyp.getId(), lineupPos);
			return lineupPos;
		}
		return null;
	}

	/**
	 * Insert match order
	 *
	 * @param lineup:		match lineup
	 * @param matchId:		match id
	 *
	 * output:
	 * @param lineupPos:	match lineup position
	 */
	private void insertMatchOrder(Lineup lineup, int matchId, int iMatchType, LineupPosition lineupPos) {
		for ( var pos : lineup.getAllPositions() ){
			var matchRoleId = (MatchRoleID)pos;
			int m_iId = matchRoleId.getId();
			int m_iSpielerId = matchRoleId.getPlayerId();

			String statement = "INSERT INTO " + getTableName() + " ( MatchID, MatchTyp, SpielerID, PositionCode) VALUES(";
			statement += ("" + matchId + "," + iMatchType + "," + m_iSpielerId + "," + m_iId + ")");

			adapter.executeUpdate(statement);
			if (lineupPos != null)
				lineupPos.addPosition(m_iId, m_iSpielerId);
		}
	}
}
