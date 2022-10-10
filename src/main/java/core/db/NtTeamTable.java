package core.db;

import core.util.HODateTime;
import module.nthrf.NtTeamDetails;
import java.sql.*;
import java.util.List;

final class NtTeamTable extends AbstractTable {
	public final static String TABLENAME = "NTTEAM";

	NtTeamTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter((o) -> ((NtTeamDetails) o).getHrfId()).setSetter((o, v) -> ((NtTeamDetails) o).setHrfId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TEAM_ID").setGetter((o) -> ((NtTeamDetails) o).getTeamId()).setSetter((o, v) -> ((NtTeamDetails) o).setTeamId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MORALE").setGetter((o) -> ((NtTeamDetails) o).getMorale()).setSetter((o, v) -> ((NtTeamDetails) o).setMorale((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SELFCONFIDENCE").setGetter((o) -> ((NtTeamDetails) o).getSelfConfidence()).setSetter((o, v) -> ((NtTeamDetails) o).setSelfConfidence((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("xp253").setGetter((o) -> ((NtTeamDetails) o).getXp253()).setSetter((o, v) -> ((NtTeamDetails) o).setXp253((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("xp343").setGetter((o) -> ((NtTeamDetails) o).getXp343()).setSetter((o, v) -> ((NtTeamDetails) o).setXp343((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("xp352").setGetter((o) -> ((NtTeamDetails) o).getXp352()).setSetter((o, v) -> ((NtTeamDetails) o).setXp352((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("xp433").setGetter((o) -> ((NtTeamDetails) o).getXp433()).setSetter((o, v) -> ((NtTeamDetails) o).setXp433((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("xp442").setGetter((o) -> ((NtTeamDetails) o).getXp442()).setSetter((o, v) -> ((NtTeamDetails) o).setXp442((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("xp451").setGetter((o) -> ((NtTeamDetails) o).getXp451()).setSetter((o, v) -> ((NtTeamDetails) o).setXp451((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("xp523").setGetter((o) -> ((NtTeamDetails) o).getXp523()).setSetter((o, v) -> ((NtTeamDetails) o).setXp523((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("xp532").setGetter((o) -> ((NtTeamDetails) o).getXp532()).setSetter((o, v) -> ((NtTeamDetails) o).setXp532((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("xp541").setGetter((o) -> ((NtTeamDetails) o).getXp541()).setSetter((o, v) -> ((NtTeamDetails) o).setXp541((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("xp550").setGetter((o) -> ((NtTeamDetails) o).getXp550()).setSetter((o, v) -> ((NtTeamDetails) o).setXp550((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("NAME").setGetter((o) -> ((NtTeamDetails) o).getTeamName()).setSetter((o, v) -> ((NtTeamDetails) o).setTeamName((String) v)).setType(Types.VARCHAR).setLength(127).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SHORTNAME").setGetter((o) -> ((NtTeamDetails) o).getTeamNameShort()).setSetter((o, v) -> ((NtTeamDetails) o).setTeamNameShort((String) v)).setType(Types.VARCHAR).setLength(127).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("COACHID").setGetter((o) -> ((NtTeamDetails) o).getCoachId()).setSetter((o, v) -> ((NtTeamDetails) o).setCoachId((Integer)v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("COACHNAME").setGetter((o) -> ((NtTeamDetails) o).getCoachName()).setSetter((o, v) -> ((NtTeamDetails) o).setCoachName((String) v)).setType(Types.VARCHAR).setLength(127).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LEAGUEID").setGetter((o) -> ((NtTeamDetails) o).getLeagueId()).setSetter((o, v) -> ((NtTeamDetails) o).setLeagueId((Integer)v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LEAGUENAME").setGetter((o) -> ((NtTeamDetails) o).getLeagueName()).setSetter((o, v) -> ((NtTeamDetails) o).setLeagueName((String) v)).setType(Types.VARCHAR).setLength(127).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SUPPORTERPOPULARITY").setGetter((o) -> ((NtTeamDetails) o).getSupportersPopularity()).setSetter((o, v) -> ((NtTeamDetails) o).setSupportersPopularity((Integer)v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("RATING").setGetter((o) -> ((NtTeamDetails) o).getRatingScore()).setSetter((o, v) -> ((NtTeamDetails) o).setRatingScore((Integer)v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("FANCLUBSIZE").setGetter((o) -> ((NtTeamDetails) o).getFanclubSize()).setSetter((o, v) -> ((NtTeamDetails) o).setFanclubSize((Integer)v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("RANK").setGetter((o) -> ((NtTeamDetails) o).getRank()).setSetter((o, v) -> ((NtTeamDetails) o).setRank((Integer)v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("FETCHEDDATE").setGetter((o) -> ((NtTeamDetails) o).getFetchedDate().toDbTimestamp()).setSetter((o, v) -> ((NtTeamDetails) o).setFetchedDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(true).build()
		};
	}

	@Override
	protected String[] getConstraintStatements() {
		return new String[]{
				"  PRIMARY KEY (HRF_ID, TEAM_ID)"
		};
	}

	void storeNTTeam(NtTeamDetails ntTeamDetails) {
		if (ntTeamDetails != null) {
			store(ntTeamDetails);
		}
	}

	private final PreparedSelectStatementBuilder selectBeforeStatementBuilder = new PreparedSelectStatementBuilder(this, "WHERE TEAM_ID=? AND MORALE IS NOT NULL AND FETCHEDDATE<? ORDER BY HRF_ID DESC LIMIT 1");
	private final PreparedSelectStatementBuilder selectTeamStatementBuilder = new PreparedSelectStatementBuilder(this, "WHERE TEAM_ID=? AND MORALE IS NOT NULL AND FETCHEDDATE<? ORDER BY HRF_ID DESC LIMIT 1");
	public NtTeamDetails loadNTTeam(int teamId, Timestamp matchDate) {
		if ( matchDate!=null){
			return loadOne(NtTeamDetails.class, executePreparedSelect(selectBeforeStatementBuilder.getStatement(), teamId, matchDate));
		}
		else {
			return loadOne(NtTeamDetails.class, executePreparedSelect(selectTeamStatementBuilder.getStatement(), teamId));
		}
	}

	public List<NtTeamDetails> loadNTTeams(int hrfId) {
		return load(NtTeamDetails.class, hrfId);
	}
}