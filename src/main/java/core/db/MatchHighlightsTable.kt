package core.db;

import core.model.enums.MatchType;
import core.model.match.MatchEvent;
import core.model.match.Matchdetails;
import core.model.match.SourceSystem;
import core.util.HODateTime;
import core.util.HOLogger;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

final class MatchHighlightsTable extends AbstractTable {
	final static String TABLENAME = "MATCHHIGHLIGHTS";

	MatchHighlightsTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
		idColumns = 2;
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[] {
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchID").setGetter((o) -> ((MatchEvent) o).getMatchId()).setSetter((o, v) -> ((MatchEvent) o).setMatchId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchTyp").setGetter((o) -> ((MatchEvent) o).getMatchType().getId()).setSetter((o, v) -> ((MatchEvent) o).setMatchType(MatchType.getById((int) v))).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("EVENT_INDEX").setGetter((o) -> ((MatchEvent) o).getMatchEventIndex()).setSetter((o, v) -> ((MatchEvent) o).setMatchEventIndex((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TeamId").setGetter((o) -> ((MatchEvent) o).getTeamID()).setSetter((o, v) -> ((MatchEvent) o).setTeamID((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MATCH_EVENT_ID").setGetter((o) -> ((MatchEvent) o).getMatchEventID().getValue()).setSetter((o, v) -> ((MatchEvent) o).setMatchEventID((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchDate").setGetter((o) -> HODateTime.toDbTimestamp(((MatchEvent) o).getMatchDate())).setSetter((o, v) -> ((MatchEvent) o).setMatchDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Minute").setGetter((o) -> ((MatchEvent) o).getMinute()).setSetter((o, v) -> ((MatchEvent) o).setMinute((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SpielerId").setGetter((o) -> ((MatchEvent) o).getPlayerId()).setSetter((o, v) -> ((MatchEvent) o).setPlayerId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SpielerName").setGetter((o) -> ((MatchEvent) o).getPlayerName()).setSetter((o, v) -> ((MatchEvent) o).setPlayerName((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SpielerHeim").setGetter((o) -> ((MatchEvent) o).getSpielerHeim()).setSetter((o, v) -> ((MatchEvent) o).setSpielerHeim((boolean) v)).setType(Types.BOOLEAN).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GehilfeID").setGetter((o) -> ((MatchEvent) o).getAssistingPlayerId()).setSetter((o, v) -> ((MatchEvent) o).setAssistingPlayerId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GehilfeName").setGetter((o) -> ((MatchEvent) o).getAssistingPlayerName()).setSetter((o, v) -> ((MatchEvent) o).setAssistingPlayerName((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GehilfeHeim").setGetter((o) -> ((MatchEvent) o).getGehilfeHeim()).setSetter((o, v) -> ((MatchEvent) o).setGehilfeHeim((boolean) v)).setType(Types.BOOLEAN).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("EventText").setGetter((o) -> ((MatchEvent) o).getEventText()).setSetter((o, v) -> ((MatchEvent) o).setEventText((String) v)).setType(Types.VARCHAR).setLength(5000).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("INJURY_TYPE").setGetter((o) -> Matchdetails.eInjuryType.toInteger(((MatchEvent) o).getM_eInjuryType())).setSetter((o, v) -> ((MatchEvent) o).setM_eInjuryType((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchPart").setGetter((o) -> MatchEvent.MatchPartId.toInteger(((MatchEvent) o).getMatchPartId())).setSetter((o, v) -> ((MatchEvent) o).setMatchPartId(MatchEvent.MatchPartId.fromMatchPartId((Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("EventVariation").setGetter((o) -> ((MatchEvent) o).getEventVariation()).setSetter((o, v) -> ((MatchEvent) o).setEventVariation((Integer) v)).setType(Types.INTEGER).isNullable(true).build()
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
			// Remove existing entries
			executePreparedDelete( details.getMatchID(), details.getMatchType().getId());
			final List<MatchEvent> vHighlights = details.downloadHighlightsIfMissing();
			for (final MatchEvent highlight : vHighlights) {
				highlight.setIsStored(false);
				highlight.setMatchDate(details.getMatchDate());
				highlight.setMatchType(details.getMatchType());
				highlight.setMatchId(details.getMatchID());
				store(highlight);
			}
		}
	}

	@Override
	protected PreparedSelectStatementBuilder  createPreparedSelectStatementBuilder(){
		return new PreparedSelectStatementBuilder(this,"WHERE MatchId=? AND MatchTyp=? ORDER BY EVENT_INDEX, Minute");
	}

	/**
	 * @param matchId the match id
	 * @return the match highlights
	 */
	List<MatchEvent> getMatchHighlights(int iMatchType, int matchId) {
		return load(MatchEvent.class, matchId, iMatchType);
	}

	private final PreparedDeleteStatementBuilder deleteYouthMatchHighlightsBeforeStatementBuilder = new PreparedDeleteStatementBuilder(this,
			getDeleteYouthMatchHighlightsBeforeStatementSQL());

	private String getDeleteYouthMatchHighlightsBeforeStatementSQL() {
		var lMatchTypes = MatchType.fromSourceSystem(SourceSystem.valueOf(SourceSystem.YOUTH.getValue()));
		var inValues = lMatchTypes.stream().map(p -> String.valueOf(p.getId())).collect(Collectors.joining(","));
		return " WHERE MatchTyp IN (" +
				inValues +
				") AND MatchDate IS NOT NULL AND MatchDate<?";
	}

	public void deleteYouthMatchHighlightsBefore(Timestamp before) {
		try {
			adapter.executePreparedUpdate(deleteYouthMatchHighlightsBeforeStatementBuilder.getStatement(), before);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.deleteMatchLineupsBefore Error" + e);
		}
	}
}
