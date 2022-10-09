package core.db;

import core.model.enums.MatchType;
import core.model.match.MatchTeamRating;
import java.sql.Types;
import java.util.List;

public class MatchTeamRatingTable extends AbstractTable {
    public final static String TABLENAME = "MATCHTEAMRATING";

    protected MatchTeamRatingTable(JDBCAdapter adapter) {
        super(TABLENAME, adapter);
        idColumns = 3;
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
                ColumnDescriptor.Builder.newInstance().setColumnName("MatchID").setGetter((o) -> ((MatchTeamRating) o).getMatchId()).setSetter((o, v) -> ((MatchTeamRating) o).setMatchId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("MatchTyp").setGetter((o) -> ((MatchTeamRating) o).getMatchTyp().getId()).setSetter((o, v) -> ((MatchTeamRating) o).setMatchTyp(MatchType.getById((int) v))).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("TeamID").setGetter((o) -> ((MatchTeamRating) o).getTeamId()).setSetter((o, v) -> ((MatchTeamRating) o).setTeamId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("FanclubSize").setGetter((o) -> ((MatchTeamRating) o).getFanclubSize()).setSetter((o, v) -> ((MatchTeamRating) o).setFanclubSize((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("PowerRating").setGetter((o) -> ((MatchTeamRating) o).getPowerRating()).setSetter((o, v) -> ((MatchTeamRating) o).setPowerRating((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("GlobalRanking").setGetter((o) -> ((MatchTeamRating) o).getGlobalRanking()).setSetter((o, v) -> ((MatchTeamRating) o).setGlobalRanking((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("RegionRanking").setGetter((o) -> ((MatchTeamRating) o).getRegionRanking()).setSetter((o, v) -> ((MatchTeamRating) o).setRegionRanking((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("LeagueRanking").setGetter((o) -> ((MatchTeamRating) o).getLeagueRanking()).setSetter((o, v) -> ((MatchTeamRating) o).setLeagueRanking((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("NumberOfVictories").setGetter((o) -> ((MatchTeamRating) o).getNumberOfVictories()).setSetter((o, v) -> ((MatchTeamRating) o).setNumberOfVictories((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("NumberOfUndefeated").setGetter((o) -> ((MatchTeamRating) o).getNumberOfUndefeated()).setSetter((o, v) -> ((MatchTeamRating) o).setNumberOfUndefeated((Integer) v)).setType(Types.INTEGER).isNullable(true).build()
        };
    }

    @Override
    protected String[] getConstraintStatements() {
        return new String[]{" PRIMARY KEY (MATCHID, MATCHTYP, TEAMID)"};
    }

    @Override
    protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder() {
        return new PreparedSelectStatementBuilder(this,"WHERE MatchID = ? AND MatchTyp = ?");
    }

    List<MatchTeamRating> load(int matchID, int matchType) {
        return load(MatchTeamRating.class, matchID, matchType);
    }

    void store(MatchTeamRating teamRating) {
        if (teamRating != null) {
            teamRating.setIsStored(isStored(teamRating.getMatchId(), teamRating.getMatchTyp().getId(), teamRating.getTeamId()));
            store(teamRating);
        }
    }
}
