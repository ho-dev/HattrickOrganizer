package core.db;

import core.model.Tournament.TournamentDetails;
import core.util.HODateTime;
import java.sql.Types;

public final class TournamentDetailsTable extends AbstractTable {

	public final static String TABLENAME = "TOURNAMENTDETAILS";

	TournamentDetailsTable(JDBCAdapter adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("TournamentId").setGetter((p) -> ((TournamentDetails) p).getTournamentId()).setSetter((p, v) -> ((TournamentDetails) p).setTournamentId((int) v)).setType(Types.INTEGER).isNullable(false).isPrimaryKey(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Name").setGetter((p) -> ((TournamentDetails) p).getName()).setSetter((p, v) -> ((TournamentDetails) p).setName((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TournamentType").setGetter((p) -> ((TournamentDetails) p).getTournamentType()).setSetter((p, v) -> ((TournamentDetails) p).setTournamentType((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Season").setGetter((p) -> ((TournamentDetails) p).getSeason()).setSetter((p, v) -> ((TournamentDetails) p).setSeason((short) (int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LogoUrl").setGetter((p) -> ((TournamentDetails) p).getLogoUrl()).setSetter((p, v) -> ((TournamentDetails) p).setLogoUrl((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TrophyType").setGetter((p) -> ((TournamentDetails) p).getTrophyType()).setSetter((p, v) -> ((TournamentDetails) p).setTrophyType((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("NumberOfTeams").setGetter((p) -> ((TournamentDetails) p).getNumberOfTeams()).setSetter((p, v) -> ((TournamentDetails) p).setNumberOfTeams((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("NumberOfGroups").setGetter((p) -> ((TournamentDetails) p).getNumberOfGroups()).setSetter((p, v) -> ((TournamentDetails) p).setNumberOfGroups((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatchRound").setGetter((p) -> ((TournamentDetails) p).getLastMatchRound()).setSetter((p, v) -> ((TournamentDetails) p).setLastMatchRound((short) (int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("FirstMatchRoundDate").setGetter((p) -> ((TournamentDetails) p).getFirstMatchRoundDate().toDbTimestamp()).setSetter((p, v) -> ((TournamentDetails) p).setFirstMatchRoundDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("NextMatchRoundDate").setGetter((p) -> ((TournamentDetails) p).getNextMatchRoundDate().toDbTimestamp()).setSetter((p, v) -> ((TournamentDetails) p).setNextMatchRoundDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IsMatchesOngoing").setGetter((p) -> ((TournamentDetails) p).getMatchesOngoing()).setSetter((p, v) -> ((TournamentDetails) p).setMatchesOngoing((boolean) v)).setType(Types.BOOLEAN).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Creator_UserID").setGetter((p) -> ((TournamentDetails) p).getCreator_UserId()).setSetter((p, v) -> ((TournamentDetails) p).setCreator_UserId((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Creator_Loginname").setGetter((p) -> ((TournamentDetails) p).getCreator_Loginname()).setSetter((p, v) -> ((TournamentDetails) p).setCreator_Loginname((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(true).build()
		};
	}

	public TournamentDetails getTournamentDetails(int tournamentId)
	{
		return loadOne(TournamentDetails.class, tournamentId);
	}

	/**
	 * Store Tournament Details into DB
	 */
	void storeTournamentDetails(TournamentDetails details) {
		store(details);
	}
}
