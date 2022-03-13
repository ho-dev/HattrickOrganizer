package core.db;

import core.model.enums.MatchType;
import core.model.match.MatchEvent;
import core.model.match.Matchdetails;
import core.model.match.SourceSystem;
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
				new ColumnDescriptor("EVENT_INDEX", Types.INTEGER, false),
				new ColumnDescriptor("TeamId", Types.INTEGER, false),
				new ColumnDescriptor("MATCH_EVENT_ID", Types.INTEGER, false),
				new ColumnDescriptor("MatchDate", Types.TIMESTAMP, true),
				new ColumnDescriptor("Minute", Types.INTEGER, false),
				new ColumnDescriptor("SpielerId", Types.INTEGER, false),
				new ColumnDescriptor("SpielerName", Types.VARCHAR, false, 256),
				new ColumnDescriptor("SpielerHeim", Types.BOOLEAN, false),
				new ColumnDescriptor("GehilfeID", Types.INTEGER, false),
				new ColumnDescriptor("GehilfeName", Types.VARCHAR, false, 256),
				new ColumnDescriptor("GehilfeHeim", Types.BOOLEAN, false),
				new ColumnDescriptor("EventText", Types.VARCHAR, false, 5000),
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
				"SET TABLE " + getTableName() + " NEW SPACE"
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

					String sql = "INSERT INTO " + getTableName() +
							" ( MatchId, MatchDate, MatchTyp, Minute, EVENT_INDEX, SpielerId, SpielerName, TeamId, MATCH_EVENT_ID, SpielerHeim, GehilfeID, GehilfeName, GehilfeHeim, INJURY_TYPE, MatchPart, EventVariation, EventText) VALUES (" +
							details.getMatchID() + ",'" +
							details.getMatchDate().toDbTimestamp() + "', " +
							details.getMatchType().getId() + ", " +
							highlight.getMinute() + ", " +
							highlight.getM_iMatchEventIndex() + ", " +
							highlight.getPlayerId() + ", '" +
							DBManager.insertEscapeSequences(highlight.getPlayerName()) + "', " +
							highlight.getTeamID() + ", " +
							highlight.getiMatchEventID() + ", " +
							highlight.getSpielerHeim() + ", " +
							highlight.getAssistingPlayerId() + ", '" +
							DBManager.insertEscapeSequences(highlight.getAssistingPlayerName()) + "', " +
							highlight.getGehilfeHeim() + ", " +
							highlight.getM_eInjuryType().getValue() + ", " +
							highlight.getMatchPartId().getValue() + ", " +
							highlight.getEventVariation() + ", '" +
							DBManager.insertEscapeSequences(highlight.getEventText()) + "') ";
					adapter.executeUpdate(sql);
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

	public void deleteYouthMatchHighlightsBefore(Timestamp before) {
		var sql = "DELETE FROM " +
				getTableName() +
				" WHERE MatchTyp IN " + MatchType.getWhereClauseFromSourceSystem(SourceSystem.YOUTH.getValue()) +
				" AND MatchDate IS NOT NULL AND MatchDate<'" +
				before.toString() + "'";
		try {
			adapter.executeUpdate(sql);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.deleteMatchLineupsBefore Error" + e);
		}
	}
}
