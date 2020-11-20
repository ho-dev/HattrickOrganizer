package core.db;

import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PenaltyTakersTable extends AbstractTable {

	public final static String TABLENAME = "MATCHLINEUPPENALTYTAKER";

	protected PenaltyTakersTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[3];
		columns[0] = new ColumnDescriptor("PlayerID", Types.INTEGER, false);
		columns[1] = new ColumnDescriptor("Pos", Types.INTEGER, false);
		columns[2] = new ColumnDescriptor("LineupName", Types.VARCHAR, false, 256);
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] { "CREATE INDEX PENALTYTAKERS_3 ON " + TABLENAME + "("
				+ columns[2].getColumnName() + ")" };
	}

	void storePenaltyTakers(String lineupName, List<MatchRoleID> penaltyTakers)
			throws SQLException {
		String sql = null;

		String[] where = { "LineupName" };
		String[] values = { "'" + lineupName + "'" };

		delete(where, values);

		if (penaltyTakers != null && !penaltyTakers.isEmpty()) {
			for (int i = 0; i < penaltyTakers.size(); i++) {
				MatchRoleID penaltyTaker = penaltyTakers.get(i);
				if (penaltyTaker != null && penaltyTaker.getId() > 0) {
					sql = "INSERT INTO " + TABLENAME + " ( LineupName, PlayerID, Pos ) VALUES (";
					sql += "'" + lineupName + "'," + penaltyTaker.getSpielerId() + "," + penaltyTaker.getId()  + ")";
					adapter.executeUpdate_(sql);
				}
			}
		}
	}

	List<MatchRoleID> getPenaltyTakers(String lineupName) throws SQLException {
		String sql = "SELECT * FROM " + TABLENAME + " WHERE LineupName='" + lineupName + "' ORDER BY Pos";
		List<MatchRoleID> list = new ArrayList<MatchRoleID>();

		ResultSet rs = adapter.executeQuery(sql);
		int counter = 0;
		while (rs.next()) {
			list.add(new MatchRoleID(rs.getInt("Pos"), rs.getInt("PlayerID"), IMatchRoleID.NORMAL));
			counter++;
		}
		return list;
	}

	private String asString(Object o) {
		if (o != null) {
			return String.valueOf(o);
		}
		return null;
	}
}
