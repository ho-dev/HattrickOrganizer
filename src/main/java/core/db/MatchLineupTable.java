package core.db;

import core.model.match.MatchLineup;
import core.model.enums.MatchType;
import core.model.match.SourceSystem;
import core.util.HOLogger;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

public final class MatchLineupTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "MATCHLINEUP";
	
	MatchLineupTable(JDBCAdapter adapter){
		super(TABLENAME,adapter);
		idColumns = 2;
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchID").setGetter((o) -> ((MatchLineup) o).getMatchID()).setSetter((o, v) -> ((MatchLineup) o).setMatchID((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchTyp").setGetter((o) -> ((MatchLineup) o).getMatchType().getId()).setSetter((o, v) -> ((MatchLineup) o).setMatchTyp(MatchType.getById((int) v))).setType(Types.INTEGER).isNullable(false).build()
		};
	}

	@Override
	protected String[] getConstraintStatements() {
		return new String[] {" PRIMARY KEY (MATCHID, MATCHTYP)"};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
			"CREATE INDEX IMATCHLINEUP_1 ON " + getTableName() + "(MatchID)"};
	}

	MatchLineup loadMatchLineup(int iMatchType, int matchID) {
		return loadOne(MatchLineup.class, matchID, iMatchType);
	}

	void storeMatchLineup(MatchLineup lineup) {
		if ( lineup != null) {
			lineup.setIsStored(isStored(lineup.getMatchID(), lineup.getMatchType().getId()));
			if (!lineup.isStored()) {    // do not update, because there is nothing to update (only ids in class)
				store(lineup);
			}
		}
	}

	private final PreparedSelectStatementBuilder loadYouthMatchLineupsStatementBuilder = new PreparedSelectStatementBuilder(this, " WHERE MATCHTYP IN (" + getMatchTypeInValues() + ")");
	public List<MatchLineup> loadYouthMatchLineups() {
		return load(MatchLineup.class,  adapter.executePreparedQuery(loadYouthMatchLineupsStatementBuilder.getStatement()));
	}

	private final PreparedDeleteStatementBuilder deleteYouthMatchLineupsBeforeStatementBuilder = new PreparedDeleteStatementBuilder(this, getDeleteYouthMatchLineupsBeforeStatementSQL());

	private String getDeleteYouthMatchLineupsBeforeStatementSQL() {
		var matchTypes = getMatchTypeInValues();
		return " WHERE MATCHTYP IN (" +
				matchTypes +
				") AND MATCHID IN (SELECT MATCHID FROM  MATCHDETAILS WHERE SpielDatum<? AND MATCHTYP IN (" +
				matchTypes + "))";
	}

	public void deleteYouthMatchLineupsBefore(Timestamp before) {
		try {
			this.adapter.executePreparedUpdate(deleteYouthMatchLineupsBeforeStatementBuilder.getStatement(), before);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.deleteMatchLineupsBefore Error" + e);
		}
	}

	private String getMatchTypeInValues() {
		return MatchType.fromSourceSystem(SourceSystem.YOUTH).stream().map(i->String.valueOf(i.getId())).collect(Collectors.joining(","));
	}
}
