package core.db;

import core.model.enums.MatchType;
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

    @Override
    protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder(){
        return new PreparedSelectStatementBuilder(this, "");
    }
    public List<YouthTraining> loadYouthTrainings() {
        final ArrayList<YouthTraining> ret = new ArrayList<>();
        var rs = executePreparedSelect();
        try {
            if (rs != null) {
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
        var matchType = rs.getInt("MatchTyp");
        var ret = new YouthTraining(matchId, MatchType.getById(matchType));
        ret.setTraining(YouthTraining.Priority.Primary, YouthTrainingType.valueOf(DBManager.getInteger(rs, "Training1")));
        ret.setTraining(YouthTraining.Priority.Secondary, YouthTrainingType.valueOf(DBManager.getInteger(rs, "Training2")));
        return ret;
    }

    public void storeYouthTraining(YouthTraining youthTraining) {
        var matchId = youthTraining.getMatchId();
        executePreparedDelete(matchId);
        if ( youthTraining.getTraining(YouthTraining.Priority.Primary) != null ||
                youthTraining.getTraining(YouthTraining.Priority.Secondary) != null) {
            executePreparedInsert(
                    matchId,
                    youthTraining.getMatchType().getId(),
                    YouthTrainingType.getValue(youthTraining.getTraining(YouthTraining.Priority.Primary)),
                    YouthTrainingType.getValue(youthTraining.getTraining(YouthTraining.Priority.Secondary))
            );
        }
    }
}
