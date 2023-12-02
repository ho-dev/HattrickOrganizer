package core.db;

import core.model.enums.MatchType;
import module.lineup.substitution.model.GoalDiffCriteria;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.RedCardCriteria;
import module.lineup.substitution.model.Substitution;
import java.sql.Types;
import java.util.List;

public class MatchSubstitutionTable extends AbstractTable {
	/**
	 * tablename
	 **/
	public final static String TABLENAME = "MATCHSUBSTITUTION";

	// Dummy value for ids not used (hrf, team, match)
	private final static int DUMMY = -101;

	protected MatchSubstitutionTable(ConnectionManager adapter) {
		super(TABLENAME, adapter);
		idColumns = 3;
	}

	@Override
	protected void initColumns() {

		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchID").setGetter((o) -> ((Substitution) o).getMatchId()).setSetter((o, v) -> ((Substitution) o).setMatchId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchTyp").setGetter((o) -> ((Substitution) o).getMatchType().getId()).setSetter((o, v) -> ((Substitution) o).setMatchType(MatchType.getById((int) v))).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TeamID").setGetter((o) -> ((Substitution) o).getTeamId()).setSetter((o, v) -> ((Substitution) o).setTeamId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("PlayerOrderID").setGetter((o) -> ((Substitution) o).getPlayerOrderId()).setSetter((o, v) -> ((Substitution) o).setPlayerOrderId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("PlayerIn").setGetter((o) -> ((Substitution) o).getObjectPlayerID()).setSetter((o, v) -> ((Substitution) o).setObjectPlayerID((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("PlayerOut").setGetter((o) -> ((Substitution) o).getSubjectPlayerID()).setSetter((o, v) -> ((Substitution) o).setSubjectPlayerID((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("OrderType").setGetter((o) -> ((Substitution) o).getOrderType().getId()).setSetter((o, v) -> ((Substitution) o).setOrderType(MatchOrderType.fromInt((int) v))).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchMinuteCriteria").setGetter((o) -> ((Substitution) o).getMatchMinuteCriteria()).setSetter((o, v) -> ((Substitution) o).setMatchMinuteCriteria((byte)(int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Pos").setGetter((o) -> ((Substitution) o).getRoleId()).setSetter((o, v) -> ((Substitution) o).setRoleId((byte)(int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Behaviour").setGetter((o) -> ((Substitution) o).getBehaviour()).setSetter((o, v) -> ((Substitution) o).setBehaviour((byte)(int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Card").setGetter((o) -> ((Substitution) o).getRedCardCriteria().getId()).setSetter((o, v) -> ((Substitution) o).setRedCardCriteria(RedCardCriteria.getById((byte)(int) v))).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Standing").setGetter((o) -> ((Substitution) o).getStanding().getId()).setSetter((o, v) -> ((Substitution) o).setStanding(GoalDiffCriteria.getById((byte)(int) v))).setType(Types.INTEGER).isNullable(false).build()
		};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[]{
				"CREATE INDEX IMATCHSUBSTITUTION_1 ON " + getTableName() + "(PlayerOrderID)",
				"CREATE INDEX IMATCHSUBSTITUTION_0 ON " + getTableName() + "(MatchID,MatchTyp,TeamID)"
		};
	}

	/**
	 * Returns an array with substitution belonging to the match-team.
	 *
	 * @param teamId  The teamId for the team in question
	 * @param matchId The matchId for the match in question
	 */
	List<Substitution> getMatchSubstitutionsByMatchTeam(int iMatchType, int teamId, int matchId) {
		return load(Substitution.class, matchId, iMatchType, teamId);
	}

	/**
	 * Stores the substitutions in the database. The ID for each substitution
	 * must be unique for the match. All previous substitutions for the
	 * team/match combination will be deleted.
	 */
	void storeMatchSubstitutionsByMatchTeam(MatchType matchType, int matchId, int teamId, List<Substitution> subs) {
		if ((matchId == DUMMY) || (teamId == DUMMY)) {
			// Rather not...
			return;
		}
		executePreparedDelete(matchId, matchType.getId(), teamId);
		for (Substitution sub : subs) {
			if (sub == null) {
				continue;
			}
			sub.setMatchId(matchId);
			sub.setMatchType(matchType);
			sub.setTeamId(teamId);
			sub.setIsStored(false);
			store(sub);
		}
	}
}
