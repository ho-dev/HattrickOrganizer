package core.db;

import core.util.HOLogger;
import module.lineup.substitution.model.GoalDiffCriteria;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.RedCardCriteria;
import module.lineup.substitution.model.Substitution;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MatchSubstitutionTable extends AbstractTable {
	/** tablename **/
	public final static String TABLENAME = "MATCHSUBSTITUTION";

	// Dummy value for ids not used (hrf, team, match)
	private final static int DUMMY = -101;

	protected MatchSubstitutionTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {

		columns = new ColumnDescriptor[13];
		columns[0] = new ColumnDescriptor("MatchID", Types.INTEGER, false);
		columns[1] = new ColumnDescriptor("TeamID", Types.INTEGER, false);
		columns[2] = new ColumnDescriptor("HrfID", Types.INTEGER, false);
		columns[3] = new ColumnDescriptor("PlayerOrderID", Types.INTEGER, false);
		columns[4] = new ColumnDescriptor("PlayerIn", Types.INTEGER, false);
		columns[5] = new ColumnDescriptor("PlayerOut", Types.INTEGER, false);
		columns[6] = new ColumnDescriptor("OrderType", Types.INTEGER, false);
		columns[7] = new ColumnDescriptor("MatchMinuteCriteria", Types.INTEGER, false);
		columns[8] = new ColumnDescriptor("Pos", Types.INTEGER, false);
		columns[9] = new ColumnDescriptor("Behaviour", Types.INTEGER, false);
		columns[10] = new ColumnDescriptor("Card", Types.INTEGER, false);
		columns[11] = new ColumnDescriptor("Standing", Types.INTEGER, false);
		columns[12] = new ColumnDescriptor("LineupName", Types.VARCHAR, false, 256);
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
				"CREATE INDEX IMATCHSUBSTITUTION_1 ON " + getTableName() + "("
						+ columns[3].getColumnName() + ")",
				"CREATE INDEX IMATCHSUBSTITUTION_2 ON " + getTableName() + "("
						+ columns[0].getColumnName() + "," + columns[1].getColumnName() + ")",
				"CREATE INDEX IMATCHSUBSTITUTION_3 ON " + getTableName() + "("
						+ columns[2].getColumnName() + ")" };
	}

	/**
	 * Returns an array with substitution belonging to the match-team.
	 * 
	 * @param teamId
	 *            The teamId for the team in question
	 * @param matchId
	 *            The matchId for the match in question
	 * 
	 */
	java.util.List<Substitution> getMatchSubstitutionsByMatchTeam(int teamId, int matchId) {
		return getSubBySql("SELECT * FROM " + getTableName() + " WHERE MatchID = " + matchId
				+ " AND TeamID = " + teamId);

	}

	/**
	 * Returns an array with substitution belonging to given hrfId
	 * 
	 * @param hrfId
	 *            The teamId for the team in question
	 * 
	 */
	java.util.List<Substitution> getMatchSubstitutionsByHrf(int hrfId, String lineupName) {
		return getSubBySql("SELECT * FROM " + getTableName() + " WHERE HrfID = " + hrfId
				+ " AND LineupName = '" + lineupName + "'");
	}

	/**
	 * Stores the substitutions in the database. The ID for each substitution
	 * must be unique for the match. All previous substitutions for the
	 * team/match combination will be deleted.
	 */
	void storeMatchSubstitutionsByMatchTeam(int matchId, int teamId,
			java.util.List<Substitution> subs) {
		if ((matchId == DUMMY) || (teamId == DUMMY)) {
			// Rather not...
			return;
		}
		// D is string dummy
		storeSub(matchId, teamId, DUMMY, subs, "D");
	}

	/**
	 * Stores the substitutions in the database. The ID for each substitution
	 * must be unique for the match. All previous substitutions for the hrf will
	 * be deleted.
	 */
	void storeMatchSubstitutionsByHrf(int hrfId, java.util.List<Substitution> subs,
			String lineupName) {
		if (hrfId == DUMMY) {
			// Rather not...
			return;
		}
		storeSub(DUMMY, DUMMY, hrfId, subs, lineupName);
	}

	private void storeSub(int matchId, int teamId, int hrfId, java.util.List<Substitution> subs,
			String lineupName) {
		String sql = null;

		final String[] where = { "MatchID", "TeamID", "HrfID", "LineupName" };
		final String[] values = { "" + matchId, "" + teamId, "" + hrfId, "'" + lineupName + "'" };

		// Get rid of any old subs for the inputs.
		delete(where, values);

		for (Substitution sub : subs) {

			if (sub == null) {
				continue;
			}

			try {
				sql = "INSERT INTO "
						+ getTableName()
						+ " (  MatchID, TeamID, HrfID, PlayerOrderID, PlayerIn, PlayerOut, OrderType,";
				sql += " MatchMinuteCriteria, Pos, Behaviour, Card, Standing, LineupName ) VALUES(";
				sql += matchId + "," + teamId + "," + hrfId + "," + sub.getPlayerOrderId() + ","
						+ sub.getObjectPlayerID() + "," + sub.getSubjectPlayerID() + ","
						+ sub.getOrderType().getId() + "," + sub.getMatchMinuteCriteria() + ","
						+ sub.getRoleId() + "," + sub.getBehaviour() + "," + sub.getRedCardCriteria().getId() + ","
						+ sub.getStanding().getId() + "," + "'" + lineupName + "')";

				adapter.executeUpdate(sql);
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "DB.storeMatchSubstitution Error" + e);
				HOLogger.instance().log(getClass(), e);
			}
		}
	}

	private java.util.List<Substitution> getSubBySql(String sql) {
		Substitution sub = null;
		ResultSet rs = null;
		List<Substitution> subst = new ArrayList<Substitution>();

		try {
			rs = adapter.executeQuery(sql);
			rs.beforeFirst();
			while (rs.next()) {
				byte orderId = (byte) rs.getInt("OrderType");
				int playerIn = rs.getInt("PlayerIn");
				int playerOut = rs.getInt("PlayerOut");
				sub = new Substitution(rs.getInt("PlayerOrderID"), playerIn, playerOut,
						orderId, (byte) rs.getInt("MatchMinuteCriteria"),
						(byte) rs.getInt("Pos"), (byte) rs.getInt("Behaviour"),
						RedCardCriteria.getById((byte) rs.getInt("Card")),
						GoalDiffCriteria.getById((byte) rs.getInt("Standing")));
				subst.add(sub);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.getMatchSubstitutions Error" + e);
		}

		return subst;
	}
	
	protected void deleteAllMatchSubstitutionsByMatchId(int matchId) {
		if (matchId <= 0) {
			return;
		}
		final String[] where = { "MatchID", "HrfID" };
		final String[] values = { String.valueOf(matchId), String.valueOf(DUMMY)};
		delete(where, values);
	}
	
	protected void deleteAllMatchSubstitutionsByHrfId (int hrfId) {
		if (hrfId <= 0) {
			return;
		}
		final String[] where = { "MatchID", "HrfID" };
		final String[] values = { String.valueOf(DUMMY), String.valueOf(hrfId)};
		delete(where, values);
		
	}

}
