package core.db;

import core.model.match.MatchEvent;
import core.model.match.Matchdetails;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

final class MatchHighlightsTable extends AbstractTable {
	final static String TABLENAME = "MATCHHIGHLIGHTS";

	protected MatchHighlightsTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[14];
		columns[0] = new ColumnDescriptor("MatchID", Types.INTEGER, false);
		columns[1] = new ColumnDescriptor("Minute", Types.INTEGER, false);
		columns[2] = new ColumnDescriptor("SpielerId", Types.INTEGER, false);
		columns[3] = new ColumnDescriptor("SpielerName", Types.VARCHAR, false, 256);
		columns[4] = new ColumnDescriptor("TeamId", Types.INTEGER, false);
		columns[5] = new ColumnDescriptor("SpielerHeim", Types.BOOLEAN, false);
		columns[6] = new ColumnDescriptor("GehilfeID", Types.INTEGER, false);
		columns[7] = new ColumnDescriptor("GehilfeName", Types.VARCHAR, false, 256);
		columns[8] = new ColumnDescriptor("GehilfeHeim", Types.BOOLEAN, false);
		columns[9] = new ColumnDescriptor("EventText", Types.VARCHAR, false, 5000);
		columns[10] = new ColumnDescriptor("MATCH_EVENT_ID", Types.INTEGER, false);
		columns[11] = new ColumnDescriptor("EVENT_INDEX", Types.INTEGER, false);
		columns[12] = new ColumnDescriptor("INJURY_TYPE", Types.TINYINT, false);
		columns[13] = new ColumnDescriptor("MatchPart", Types.INTEGER, true);
	}

	@Override
	protected String[] getCreateIndizeStatements() {
		return new String[] {
				"CREATE INDEX iMATCHHIGHLIGHTS_1 ON " + getTableName() + "(" + columns[0].getColumnName() + ")",
				"CREATE INDEX matchhighlights_teamid_idx ON " + getTableName() + " (" + columns[4].getColumnName() + ")",
				"CREATE INDEX matchhighlights_eventid_idx ON " + getTableName() + " (" + columns[10].getColumnName() + ")",
		};
	}

	void storeMatchHighlights(Matchdetails details) {
		if (details != null) {

			final String[] where = { "MatchID" };
			final String[] werte = { "" + details.getMatchID() };

			// Remove existing entry
			delete(where, werte);

			try {
				final ArrayList<MatchEvent> vHighlights = details.getHighlights();
				HOLogger.instance().debug(getClass(), "count of highlights: " + vHighlights.size());
				for (int i = 0; i < vHighlights.size(); i++) {
					final MatchEvent highlight = vHighlights.get(i);
					StringBuilder sql = new StringBuilder(100);

					sql.append("INSERT INTO ").append(getTableName());
					sql.append(" ( MatchId, Minute, EVENT_INDEX, SpielerId, SpielerName, TeamId, MATCH_EVENT_ID, SpielerHeim, GehilfeID, GehilfeName, GehilfeHeim, INJURY_TYPE, MatchPart, EventText) VALUES (");
					sql.append(details.getMatchID()).append(", ");
					sql.append(highlight.getMinute()).append(", ");
					sql.append(highlight.getM_iMatchEventIndex()).append(", ");
					sql.append(highlight.getSpielerID()).append(", '");
					sql.append(DBManager.insertEscapeSequences(highlight.getSpielerName())).append("', ");
					sql.append(highlight.getTeamID()).append(", ");
					sql.append(highlight.getiMatchEventID()).append(", ");
					sql.append(highlight.getSpielerHeim()).append(", ");
					sql.append(highlight.getGehilfeID()).append(", '");
					sql.append(DBManager.insertEscapeSequences(highlight.getGehilfeName())).append("', ");
					sql.append(highlight.getGehilfeHeim()).append(", ");
					sql.append(highlight.getM_eInjuryType().getValue()).append(", '");
					sql.append(highlight.getMatchPartId().getValue()).append(", '");
					sql.append(DBManager.insertEscapeSequences(highlight.getEventText())).append("') ");
					adapter.executeUpdate(sql.toString());
				}
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "DB.storeMatchHighlights Error" + e);
				HOLogger.instance().log(getClass(), e);
			}
		}
	}

	/**
	 * @param matchId the match id
	 * @return the match highlights
	 */
	ArrayList<MatchEvent> getMatchHighlights(int matchId) {
		try {
			final ArrayList<MatchEvent> vMatchHighlights = new ArrayList<>();

			String sql = "SELECT * FROM " + getTableName() + " WHERE MatchId=" + matchId + " ORDER BY EVENT_INDEX, Minute";
			ResultSet rs = adapter.executeQuery(sql);

			rs.beforeFirst();

			while (rs.next()) {
				vMatchHighlights.add(createObject(rs));
			}
			return vMatchHighlights;

		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
		return new ArrayList<>();
	}

	private MatchEvent createObject(ResultSet rs) throws SQLException {
		final MatchEvent highlight = new MatchEvent();

		highlight.setMatchId(rs.getInt("MatchId"));
		highlight.setMinute(rs.getInt("Minute"));
		highlight.setSpielerID(rs.getInt("SpielerId"));
		highlight.setSpielerName(DBManager.deleteEscapeSequences(rs.getString("SpielerName")));
		highlight.setTeamID(rs.getInt("TeamId"));
		highlight.setMatchEventID(rs.getInt("MATCH_EVENT_ID"));
		highlight.setSpielerHeim(rs.getBoolean("SpielerHeim"));
		highlight.setGehilfeID(rs.getInt("GehilfeID"));
		highlight.setGehilfeName(DBManager.deleteEscapeSequences(rs.getString("GehilfeName")));
		highlight.setGehilfeHeim(rs.getBoolean("GehilfeHeim"));
		highlight.setEventText(DBManager.deleteEscapeSequences(rs.getString("EventText")));
		highlight.setM_eInjuryType(rs.getInt("INJURY_TYPE"));
		highlight.setMatchPartId(MatchEvent.MatchPartId.fromMatchPartId(rs.getInt("MatchPart")));
		if ( rs.wasNull()) highlight.setMatchPartId(null);

		return highlight;
	}

	ArrayList<MatchEvent> getMatchHighlightsByTypIdAndPlayerId(int type, int playerId) {
		ArrayList<MatchEvent> matchHighlights = new ArrayList<>();

		String sql = "SELECT * FROM " + getTableName() + " WHERE TYP=" + type + " AND "
				+ "SpielerId=" + playerId + " ORDER BY Minute, HeimTore, GastTore";
		try {
			ResultSet rs = adapter.executeQuery(sql);
			if (rs != null) {
				rs.beforeFirst();
				while (rs.next()) {
					matchHighlights.add(createObject(rs));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return matchHighlights;
	}

}
