package core.db;

import core.constants.TrainingType;
import core.util.HOLogger;
import module.youth.YouthPlayer;
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
        ret.setTraining1(YouthTrainingType.valueOf(DBManager.getInteger(rs, "Training1")));
        ret.setTraining2(YouthTrainingType.valueOf(DBManager.getInteger(rs, "Training2")));
        return ret;
    }

}
