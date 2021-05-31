package core.db;

import core.util.HOLogger;
import module.youth.YouthTraining;
import module.youth.YouthTrainingType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class YouthTrainingTable extends AbstractTable{
    /** tablename **/
    final static String TABLENAME = "YOUTHTRAINING";

    YouthTrainingTable(JDBCAdapter adapter) {
        super(TABLENAME, adapter);
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
                new ColumnDescriptor("MATCHID", Types.INTEGER, false),
                new ColumnDescriptor("MatchTyp", Types.INTEGER, false),
                new ColumnDescriptor("TRAINING1", Types.INTEGER, true),
                new ColumnDescriptor("TRAINING2", Types.INTEGER, true)
        };
    }

    public List<YouthTraining> loadYouthTrainings() {
        final ArrayList<YouthTraining> ret = new ArrayList<>();
        var sql = "SELECT * from " + getTableName() ;
        var rs = adapter.executeQuery(sql);
        try {
            if (rs != null) {
                rs.beforeFirst();
                while (rs.next()) {
                    var training = createObject(rs);
                    ret.add(training);
                }
            }
        } catch (Exception e) {
            HOLogger.instance().log(getClass(), "DatenbankZugriff.loadYouthTrainings: " + e);
        }
        return ret;
    }

    private YouthTraining createObject(ResultSet rs) throws SQLException {
        var matchId = rs.getInt("MatchId");
        var ret = new YouthTraining(matchId);
        ret.setTraining(YouthTraining.Priority.Primary, YouthTrainingType.valueOf(DBManager.getInteger(rs, "Training1")));
        ret.setTraining(YouthTraining.Priority.Secondary, YouthTrainingType.valueOf(DBManager.getInteger(rs, "Training2")));
        return ret;
    }

    public void storeYouthTraining(YouthTraining youthTraining) {
        var matchId = youthTraining.getMatchId();
        delete( new String[]{"MatchId"}, new String[]{""+matchId});
        if ( youthTraining.getTraining(YouthTraining.Priority.Primary) != null ||
                youthTraining.getTraining(YouthTraining.Priority.Secondary) != null) {
            StringBuilder sql = new StringBuilder("INSERT INTO " + getTableName() + " ( MatchId, Training1, Training2 ) VALUES(" + matchId);
            for (var p : YouthTraining.Priority.values()) {
                var tt = youthTraining.getTraining(p);
                if (tt == null) {
                    sql.append(",null");
                } else {
                    sql.append(",").append(tt.getValue());
                }
            }
            sql.append(")");
            adapter.executeUpdate(sql.toString());
        }
    }
}
