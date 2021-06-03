package core.db;

import core.model.match.MatchEvent;
import core.model.match.Matchdetails;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

final class MatchHighlightsTable extends AbstractTable {
	final static String TABLENAME = "MATCHHIGHLIGHTS";

	protected MatchHighlightsTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[] {
				new ColumnDescriptor("MatchID", Types.INTEGER, false),
				new ColumnDescriptor("MatchTyp", Types.INTEGER, false),
				new ColumnDescriptor("MatchDate", Types.TIMESTAMP, true),
				new ColumnDescriptor("Minute", Types.INTEGER, false),
				new ColumnDescriptor("SpielerId", Types.INTEGER, false),
				new ColumnDescriptor("SpielerName", Types.VARCHAR, false, 256),
				new ColumnDescriptor("TeamId", Types.INTEGER, false),
				new ColumnDescriptor("SpielerHeim", Types.BOOLEAN, false),
				new ColumnDescriptor("GehilfeID", Types.INTEGER, false),
				new ColumnDescriptor("GehilfeName", Types.VARCHAR, false, 256),
				new ColumnDescriptor("GehilfeHeim", Types.BOOLEAN, false),
				new ColumnDescriptor("EventText", Types.VARCHAR, false, 5000),
				new ColumnDescriptor("MATCH_EVENT_ID", Types.INTEGER, false),
				new ColumnDescriptor("EVENT_INDEX", Types.INTEGER, false),
				new ColumnDescriptor("INJURY_TYPE", Types.TINYINT, false),
				new ColumnDescriptor("MatchPart", Types.INTEGER, true),
				new ColumnDescriptor("EventVariation", Types.INTEGER, true)
		};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
				"CREATE INDEX iMATCHHIGHLIGHTS_1 ON " + getTableName() + " (MatchID)",
				"CREATE INDEX matchhighlights_teamid_idx ON " + getTableName() + " (TeamId)",
				"CREATE INDEX matchhighlights_eventid_idx ON " + getTableName() + " (MATCH_EVENT_ID)",
		};
	}

	void storeMatchHighlights(Matchdetails details) {
		if (details != null) {

			final String[] where = { "MatchTyp", "MatchID" };
			final String[] werte = { "" + details.getMatchType().getId(), "" + details.getMatchID() };

			// Remove existing entry
			delete(where, werte);

			try {
				final ArrayList<MatchEvent> vHighlights = details.getHighlights();
				for (final MatchEvent highlight : vHighlights) {
					StringBuilder sql = new StringBuilder(100);

					sql.append("INSERT INTO ").append(getTableName());
					sql.append(" ( MatchId, MatchDate, MatchTyp, Minute, EVENT_INDEX, SpielerId, SpielerName, TeamId, MATCH_EVENT_ID, SpielerHeim, GehilfeID, GehilfeName, GehilfeHeim, INJURY_TYPE, MatchPart, EventVariation, EventText) VALUES (");
					sql.append(details.getMatchID()).append(",'");
					sql.append(details.getSpielDatum()).append("', ");
					sql.append(details.getMatchType().getId()).append(", ");
					sql.append(highlight.getMinute()).append(", ");
					sql.append(highlight.getM_iMatchEventIndex()).append(", ");
					sql.append(highlight.getPlayerId()).append(", '");
					sql.append(DBManager.insertEscapeSequences(highlight.getPlayerName())).append("', ");
					sql.append(highlight.getTeamID()).append(", ");
					sql.append(highlight.getiMatchEventID()).append(", ");
					sql.append(highlight.getSpielerHeim()).append(", ");
					sql.append(highlight.getAssistingPlayerId()).append(", '");
					sql.append(DBManager.insertEscapeSequences(highlight.getAssistingPlayerName())).append("', ");
					sql.append(highlight.getGehilfeHeim()).append(", ");
					sql.append(highlight.getM_eInjuryType().getValue()).append(", ");
					sql.append(highlight.getMatchPartId().getValue()).append(", ");
					sql.append(highlight.getEventVariation()).append(", '");
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
	ArrayList<MatchEvent> getMatchHighlights(int iMatchType, int matchId) {
		try {
			final ArrayList<MatchEvent> vMatchHighlights = new ArrayList<>();

			String sql = "SELECT * FROM " + getTableName() +
					" WHERE MatchTyp=" + iMatchType +
					" AND MatchId=" + matchId +
					" ORDER BY EVENT_INDEX, Minute";
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
		highlight.setPlayerId(rs.getInt("SpielerId"));
		highlight.setPlayerName(DBManager.deleteEscapeSequences(rs.getString("SpielerName")));
		highlight.setTeamID(rs.getInt("TeamId"));
		highlight.setMatchEventID(rs.getInt("MATCH_EVENT_ID"));
		highlight.setSpielerHeim(rs.getBoolean("SpielerHeim"));
		highlight.setAssistingPlayerId(rs.getInt("GehilfeID"));
		highlight.setAssistingPlayerName(DBManager.deleteEscapeSequences(rs.getString("GehilfeName")));
		highlight.setGehilfeHeim(rs.getBoolean("GehilfeHeim"));
		highlight.setEventText(DBManager.deleteEscapeSequences(rs.getString("EventText")));
		highlight.setM_eInjuryType(rs.getInt("INJURY_TYPE"));
		highlight.setMatchPartId(MatchEvent.MatchPartId.fromMatchPartId(DBManager.getInteger(rs,"MatchPart")));
		highlight.setEventVariation(DBManager.getInteger(rs, "EventVariation"));
		return highlight;
	}

	public void deleteMatchHighlightsBefore(int iMatchType, Timestamp before) {
		var sql = "DELETE FROM " +
				getTableName() +
				" WHERE MatchTyp=" +
				iMatchType +
				" AND MatchDate IS NOT NULL AND MatchDate<'" +
				before.toString() + "'";
		try {
			adapter.executeUpdate(sql);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.deleteMatchLineupsBefore Error" + e);
		}
	}
}
