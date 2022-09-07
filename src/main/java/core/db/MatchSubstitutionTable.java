package core.db;

import core.util.HOLogger;
import module.lineup.substitution.model.GoalDiffCriteria;
import module.lineup.substitution.model.RedCardCriteria;
import module.lineup.substitution.model.Substitution;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MatchSubstitutionTable extends AbstractTable {
	/**
	 * tablename
	 **/
	public final static String TABLENAME = "MATCHSUBSTITUTION";

	// Dummy value for ids not used (hrf, team, match)
	private final static int DUMMY = -101;

	protected MatchSubstitutionTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {

		columns = new ColumnDescriptor[]{
				new ColumnDescriptor("MatchID", Types.INTEGER, false),
				new ColumnDescriptor("MatchTyp", Types.INTEGER, false),
				new ColumnDescriptor("TeamID", Types.INTEGER, false),
				new ColumnDescriptor("HrfID", Types.INTEGER, false),
				new ColumnDescriptor("PlayerOrderID", Types.INTEGER, false),
				new ColumnDescriptor("PlayerIn", Types.INTEGER, false),
				new ColumnDescriptor("PlayerOut", Types.INTEGER, false),
				new ColumnDescriptor("OrderType", Types.INTEGER, false),
				new ColumnDescriptor("MatchMinuteCriteria", Types.INTEGER, false),
				new ColumnDescriptor("Pos", Types.INTEGER, false),
				new ColumnDescriptor("Behaviour", Types.INTEGER, false),
				new ColumnDescriptor("Card", Types.INTEGER, false),
				new ColumnDescriptor("Standing", Types.INTEGER, false),
				new ColumnDescriptor("LineupName", Types.VARCHAR, false, 256)
		};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[]{
				"CREATE INDEX IMATCHSUBSTITUTION_1 ON " + getTableName() + "(PlayerOrderID)",
				"CREATE INDEX IMATCHSUBSTITUTION_2 ON " + getTableName() + "(MatchID,TeamID)",
				"CREATE INDEX IMATCHSUBSTITUTION_3 ON " + getTableName() + "(HrfID)"
		};
	}

	@Override
	protected PreparedDeleteStatementBuilder createPreparedDeleteStatementBuilder() {
		return new PreparedDeleteStatementBuilder(this,"WHERE MATCHTYP=? AND MATCHID=?");
	}

	@Override
	protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder() {
		return new PreparedSelectStatementBuilder(this," WHERE MatchTyp = ? AND MatchID = ? AND TeamID = ?");
	}

	/**
	 * Returns an array with substitution belonging to the match-team.
	 *
	 * @param teamId  The teamId for the team in question
	 * @param matchId The matchId for the match in question
	 */
	java.util.List<Substitution> getMatchSubstitutionsByMatchTeam(int iMatchType, int teamId, int matchId) {
		List<Substitution> subst = new ArrayList<>();
		try {
			var rs = executePreparedSelect(iMatchType, matchId, teamId);
			while (rs.next()) {
				byte orderId = (byte) rs.getInt("OrderType");
				int playerIn = rs.getInt("PlayerIn");
				int playerOut = rs.getInt("PlayerOut");
				var sub = new Substitution(rs.getInt("PlayerOrderID"), playerIn, playerOut,
						orderId, (byte) rs.getInt("MatchMinuteCriteria"),
						(byte) rs.getInt("Pos"), (byte) rs.getInt("Behaviour"),
						RedCardCriteria.getById((byte) rs.getInt("Card")),
						GoalDiffCriteria.getById((byte) rs.getInt("Standing")));
				subst.add(sub);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.getMatchSubstitutions Error" + e);
		}

		return subst;
	}

	/**
	 * Stores the substitutions in the database. The ID for each substitution
	 * must be unique for the match. All previous substitutions for the
	 * team/match combination will be deleted.
	 */
	void storeMatchSubstitutionsByMatchTeam(int iMatchType, int matchId, int teamId,
											java.util.List<Substitution> subs) {
		if ((matchId == DUMMY) || (teamId == DUMMY)) {
			// Rather not...
			return;
		}
		// D is string dummy
		storeSub(iMatchType, matchId, teamId, subs);
	}

	private final PreparedDeleteStatementBuilder deleteSubStatementBuilder = new PreparedDeleteStatementBuilder(this, "WHERE MatchTyp=? AND MatchID=? AND TeamID=? AND HRFID=? AND LineupName=?");

	private void storeSub(int iMatchType, int matchId, int teamId, List<Substitution> subs) {
		this.adapter.executePreparedUpdate(deleteSubStatementBuilder.getStatement(),
				iMatchType,
				matchId,
				teamId,
				MatchSubstitutionTable.DUMMY,
				"D"
		);

		for (Substitution sub : subs) {

			if (sub == null) {
				continue;
			}

			try {
				executePreparedInsert(
						matchId,
						iMatchType,
						teamId,
						MatchSubstitutionTable.DUMMY,
						sub.getPlayerOrderId(),
						sub.getObjectPlayerID(),
						sub.getSubjectPlayerID(),
						sub.getOrderType().getId(),
						sub.getMatchMinuteCriteria(),
						sub.getRoleId(),
						sub.getBehaviour(),
						sub.getRedCardCriteria().getId(),
						sub.getStanding().getId(),
						"D"
				);
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "DB.storeMatchSubstitution Error" + e);
				HOLogger.instance().log(getClass(), e);
			}
		}
	}
}
