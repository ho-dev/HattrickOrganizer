package core.db;


import core.training.FuturePlayerTraining;
import core.util.HODateTime;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

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
        columns = new ColumnDescriptor[]{
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
                getTableName() +
                " where playerId=" +
                playerId;
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

    private FuturePlayerTraining createFuturePlayerTraining(ResultSet rs) throws SQLException {
        var playerid = rs.getInt("playerId");
        var fromSeason = rs.getInt("fromSeason");
        var fromWeek = rs.getInt("fromWeek");
        var from = HODateTime.fromHTWeek(new HODateTime.HTWeek(fromSeason, fromWeek));
        HODateTime to = null;
        var toSeason = DBManager.getInteger(rs, "toSeason");
        var toWeek = DBManager.getInteger(rs, "toWeek");
        if (toSeason != null && toWeek != null) {
            to = HODateTime.fromHTWeek(new HODateTime.HTWeek(toSeason, toWeek));
        }
        var prio = FuturePlayerTraining.Priority.valueOf(DBManager.getInteger(rs,"prio"));
        return new FuturePlayerTraining(playerid, prio, from, to);
    }

    public void storeFuturePlayerTrainings(int spielerID, List<FuturePlayerTraining> futurePlayerTrainings) {
        final String[] where = {"playerId"};
        final String[] werte = {String.valueOf(spielerID)};
        try {
            delete(where, werte);

            for (var t : futurePlayerTrainings) {
                var to = t.getTo();
                var from = t.getFrom();
                String sql = "INSERT INTO " +
                        getTableName() +
                        " (  playerId, prio, fromSeason, fromWeek, toSeason, toWeek ) VALUES(" +
                        t.getPlayerId() + ", " +
                        t.getPriority().getValue() + ", " +
                        from.toHTWeek().season + ", " +
                        from.toHTWeek().week + ", " +
                        (to != null ? to.toHTWeek().season : null) + ", " +
                        (to != null ? to.toHTWeek().week : null) +
                        ")";
                adapter.executeUpdate(sql);
            }
        } catch (Exception e) {
            HOLogger.instance().log(getClass(), e);
        }
    }
}