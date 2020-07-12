package core.db;


import core.training.FuturePlayerTraining;
import core.training.HattrickDate;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class FuturePlayerTrainingTable extends AbstractTable {

    public final static String TABLENAME = "FUTUREPLAYERTRAINING";

    /**
     * constructor
     */
    public FuturePlayerTrainingTable(JDBCAdapter adapter) {
        super(TABLENAME, adapter);
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[] {
                new ColumnDescriptor("playerId", Types.INTEGER, false),
                new ColumnDescriptor("fromWeek", Types.INTEGER, false),
                new ColumnDescriptor("fromSeason", Types.INTEGER, false),
                new ColumnDescriptor("toWeek", Types.INTEGER, true),
                new ColumnDescriptor("toSeason", Types.INTEGER, true),
                new ColumnDescriptor("prio", Types.INTEGER, false)
        };
    }

    List<FuturePlayerTraining> getFuturePlayerTrainingPlan(int playerId) {
        String query = "select * from " +
                TABLENAME +
                " where playerId=" + playerId;
        ResultSet rs = adapter.executeQuery(query);
        try {
            if (rs != null) {
                var ret = new ArrayList<FuturePlayerTraining>();
                rs.beforeFirst();
                while (rs.next()) {
                    ret.add(createFuturePlayerTraining(rs));
                }
                return ret;
            }
        } catch (Exception e) {
            HOLogger.instance().log(getClass(), "DatenbankZugriff.getTraining " + e);
        }
        return null;
    }

    Map<Integer, FuturePlayerTraining> getFuturePlayerTraining(int season, int week){

        String query = "select * from " +
                TABLENAME +
                " where (fromSeason < " + season +
                " or " +
                "fromSeason=" + season + " and fromWeek <= " + week + ")" +
                " and ( toSeason is null or toSeason > " + season +
                " or toSeason = " + season + " and toWeek >= " + week + ")";

        ResultSet rs = adapter.executeQuery(query);

        try {
            if (rs != null) {

                var ret = new Hashtable<Integer, FuturePlayerTraining>();
                rs.beforeFirst();

                while (rs.next()) {
                    var train = createFuturePlayerTraining(rs);
                    ret.put(train.getPlayerId(), train);
                }

                return ret;
            }
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),"DatenbankZugriff.getTraining " + e);
        }

        return null;
    }

    private FuturePlayerTraining createFuturePlayerTraining(ResultSet rs) throws SQLException {
        var playerid = rs.getInt("playerId");
        var fromSeason = rs.getInt("fromSeason");
        var fromWeek = rs.getInt("fromWeek");
        var from = new HattrickDate(fromSeason, fromWeek);
        HattrickDate to = null;
        var toSeason = DbUtil.getNullableInt(rs, "toSeason");
        if ( toSeason != null) {
            var toWeek = DbUtil.getNullableInt(rs, "toWeek");
            to = new HattrickDate(toSeason, toWeek);
        }
        var prio = FuturePlayerTraining.Priority.valueOf(rs.getInt("prio"));
        return  new FuturePlayerTraining(playerid, prio, from, to);
    }

}
