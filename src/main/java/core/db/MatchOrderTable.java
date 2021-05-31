package core.db;

import core.model.match.MatchType;
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
 * Table to save players position for futur matchs: used by training preview
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
	 * Update match order
	 * Remove and insert rows
	 *
	 * @param linueup:	lineup of match
	 * @param matchId:	match id
	 * @return			true if update, false if no update
	 */
    public void updateMatchOrder(Lineup linueup, int matchId) {
			removeMatchOrder();
			insertMatchOrder(linueup, matchId, null);
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
			insertMatchOrder(linueup, matchId, lineupPos);
			return lineupPos;
		}
		return null;
	}

	/**
	 * Insert match order
	 *
	 * @param linueup:		match lineup
	 * @param matchId:		match id
	 *
	 * output:
	 * @param lineupPos:	match lineup position
	 */
	private void insertMatchOrder(Lineup linueup, int matchId, LineupPosition lineupPos) {
		for (int i = 0;(linueup.getPositionen() != null) && (i < linueup.getPositionen().size()); i++) {

			int m_iId = ((MatchRoleID) linueup.getPositionen().elementAt(i)).getId();
			int m_iSpielerId = ((MatchRoleID) linueup.getPositionen().elementAt(i)).getPlayerId();

			String statement = "INSERT INTO " + getTableName() + " ( MatchID, SpielerID, PositionCode) VALUES(";
			statement += ("" + matchId + "," + m_iSpielerId + "," + m_iId + ")");

			adapter.executeUpdate(statement);
			if (lineupPos != null)
				lineupPos.addPosition(m_iId, m_iSpielerId);
		}
	}
}
