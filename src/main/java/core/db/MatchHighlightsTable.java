package core.db;

import core.model.enums.MatchType;
import core.model.match.MatchEvent;
import core.model.match.Matchdetails;
import core.model.match.SourceSystem;
import core.util.HOLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

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

	@Override
	protected PreparedStatement createDeleteStatement(){
		return createDeleteStatement("WHERE MATCHTYP=? AND MATCHID=?");
	}

	void storeMatchHighlights(Matchdetails details) {
		if (details != null) {
			try {
				// Remove existing entry
				executePreparedDelete(details.getMatchType().getId(), details.getMatchID());
				final ArrayList<MatchEvent> vHighlights = details.downloadHighlightsIfMissing();
				for (final MatchEvent highlight : vHighlights) {
					executePreparedInsert(
							details.getMatchID(),
							details.getMatchType().getId(),
							highlight.getM_iMatchEventIndex(),
							highlight.getTeamID(),
							highlight.getiMatchEventID(),
							details.getMatchDate().toDbTimestamp(),
							highlight.getMinute(),
							highlight.getPlayerId(),
							highlight.getPlayerName(),
							highlight.getSpielerHeim(),
							highlight.getAssistingPlayerId(),
							highlight.getAssistingPlayerName(),
							highlight.getGehilfeHeim(),
							highlight.getEventText(),
							highlight.getM_eInjuryType().getValue(),
							highlight.getMatchPartId().getValue(),
							highlight.getEventVariation()
					);
				}
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "DB.storeMatchHighlights Error" + e);
				HOLogger.instance().log(getClass(), e);
			}
		}
	}

	@Override
	protected PreparedStatement createSelectStatement(){
		return createSelectStatement("WHERE MatchTyp=? AND MatchId=? ORDER BY EVENT_INDEX, Minute");
	}

	/**
	 * @param matchId the match id
	 * @return the match highlights
	 */
	ArrayList<MatchEvent> getMatchHighlights(int iMatchType, int matchId) {
		try {
			final ArrayList<MatchEvent> vMatchHighlights = new ArrayList<>();
			ResultSet rs = executePreparedSelect(iMatchType, matchId);
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
		highlight.setPlayerName(rs.getString("SpielerName"));
		highlight.setTeamID(rs.getInt("TeamId"));
		highlight.setMatchEventID(rs.getInt("MATCH_EVENT_ID"));
		highlight.setSpielerHeim(rs.getBoolean("SpielerHeim"));
		highlight.setAssistingPlayerId(rs.getInt("GehilfeID"));
		highlight.setAssistingPlayerName(rs.getString("GehilfeName"));
		highlight.setGehilfeHeim(rs.getBoolean("GehilfeHeim"));
		highlight.setEventText(rs.getString("EventText"));
		highlight.setM_eInjuryType(rs.getInt("INJURY_TYPE"));
		highlight.setMatchPartId(MatchEvent.MatchPartId.fromMatchPartId(DBManager.getInteger(rs,"MatchPart")));
		highlight.setEventVariation(DBManager.getInteger(rs, "EventVariation"));
		return highlight;
	}

	private PreparedStatement deleteYouthMatchHighlightsBeforeStatement;
	private PreparedStatement getDeleteYouthMatchHighlightsBeforeStatement(){
		if ( deleteYouthMatchHighlightsBeforeStatement==null){
			var lMatchTypes =  MatchType.fromSourceSystem(SourceSystem.valueOf(SourceSystem.YOUTH.getValue()));
			var inValues = lMatchTypes.stream().map(p -> String.valueOf(p.getId())).collect(Collectors.joining(","));
			deleteYouthMatchHighlightsBeforeStatement=adapter.createPreparedStatement(
					"DELETE FROM " +
					getTableName() +
					" WHERE MatchTyp IN (" +
					inValues +
					") AND MatchDate IS NOT NULL AND MatchDate<?");
		}
		return deleteYouthMatchHighlightsBeforeStatement;
	}
	public void deleteYouthMatchHighlightsBefore(Timestamp before) {
		try {
			adapter.executePreparedUpdate(getDeleteYouthMatchHighlightsBeforeStatement(), before);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.deleteMatchLineupsBefore Error" + e);
		}
	}
}
