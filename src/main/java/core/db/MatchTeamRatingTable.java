package core.db;

import core.model.match.MatchTeamRating;
import core.util.HOLogger;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MatchTeamRatingTable extends AbstractTable {
    public final static String TABLENAME = "MATCHTEAMRATING";

    protected MatchTeamRatingTable(JDBCAdapter adapter) {
        super(TABLENAME, adapter);
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
                new ColumnDescriptor("MatchID", Types.INTEGER, false),
                new ColumnDescriptor("MatchTyp", Types.INTEGER, false),
                new ColumnDescriptor("TeamID", Types.INTEGER, false),
                new ColumnDescriptor("FanclubSize", Types.INTEGER, false),
                new ColumnDescriptor("PowerRating", Types.INTEGER, true),
                new ColumnDescriptor("GlobalRanking", Types.INTEGER, true),
                new ColumnDescriptor("RegionRanking", Types.INTEGER, true),
                new ColumnDescriptor("LeagueRanking", Types.INTEGER, true),
                new ColumnDescriptor("NumberOfVictories", Types.INTEGER, true),
                new ColumnDescriptor("NumberOfUndefeated", Types.INTEGER, true)
        };
    }

    @Override
    protected String[] getConstraintStatements() {
        return new String[]{" PRIMARY KEY (MATCHID, MATCHTYP, TEAMID)"};
    }

    @Override
    protected PreparedStatement createSelectStatement() {
        return createSelectStatement("WHERE MatchTyp = ? AND MatchID = ?");
    }

    List<MatchTeamRating> load(int matchID, int matchType) {
        var ret = new ArrayList<MatchTeamRating>();
        try {
            var rs = executePreparedSelect(matchType, matchID);
            if (rs != null) {
                rs.beforeFirst();
                while (rs.next()) {
                    ret.add(new MatchTeamRating(rs));
                }
            }
        } catch (Exception e) {
            HOLogger.instance().log(getClass(), "DB.MatchTeamRating Error" + e);
        }
        return ret;
    }

    @Override
    protected PreparedStatement createDeleteStatement() {
        return createDeleteStatement("WHERE MatchTyp=? AND MatchID=? AND TeamID=?");
    }

    void store(MatchTeamRating teamRating) {
        if (teamRating != null) {

            try {
                executePreparedDelete(
                        teamRating.getMatchTyp().getId(),
                        teamRating.getMatchId(),
                        teamRating.getTeamId()
                );
                executePreparedInsert(
                        +teamRating.getMatchId(),
                        +teamRating.getMatchTyp().getId(),
                        +teamRating.getTeamId(),
                        +teamRating.getFanclubSize(),
                        +teamRating.getPowerRating(),
                        +teamRating.getGlobalRanking(),
                        +teamRating.getRegionRanking(),
                        +teamRating.getLeagueRanking(),
                        +teamRating.getNumberOfVictories(),
                        +teamRating.getNumberOfUndefeated()
                );
            } catch (Exception e) {
                HOLogger.instance().log(getClass(), "DB.store MatchTeamRating Error" + e);
                HOLogger.instance().log(getClass(), e);
            }
        }
    }
}
