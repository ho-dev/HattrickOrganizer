package core.db;

import core.model.enums.MatchType;
import module.youth.YouthTraining;
import module.youth.YouthTrainingType;
import java.sql.Types;
import java.util.List;

public class YouthTrainingTable extends AbstractTable{
    /** tablename **/
    final static String TABLENAME = "YOUTHTRAINING";

    YouthTrainingTable(ConnectionManager adapter) {
        super(TABLENAME, adapter);
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
                ColumnDescriptor.Builder.newInstance().setColumnName("MATCHID").setGetter((p) -> ((YouthTraining) p).getYouthMatchId()).setSetter((p, v) -> ((YouthTraining) p).setYouthMatchId( (int) v)).setType(Types.INTEGER).isNullable(false).isPrimaryKey(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("MatchTyp").setGetter((p) -> ((YouthTraining) p).getMatchType().getId()).setSetter((p, v) -> ((YouthTraining) p).setYouthMatchType(MatchType.getById((int)v))).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("TRAINING1").setGetter((p) -> YouthTrainingType.getValue(((YouthTraining) p).getTraining(YouthTraining.Priority.Primary))).setSetter((p, v) -> ((YouthTraining) p).setTraining(YouthTraining.Priority.Primary, YouthTrainingType.valueOf((Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("TRAINING2").setGetter((p) -> YouthTrainingType.getValue(((YouthTraining) p).getTraining(YouthTraining.Priority.Secondary))).setSetter((p, v) -> ((YouthTraining) p).setTraining(YouthTraining.Priority.Secondary, YouthTrainingType.valueOf((Integer) v))).setType(Types.INTEGER).isNullable(true).build()
        };
    }

    @Override
    protected String createSelectStatement() {
        return createSelectStatement("");
    }

    public List<YouthTraining> loadYouthTrainings() {
        return load(YouthTraining.class);
    }

    public void storeYouthTraining(YouthTraining youthTraining) {
        store(youthTraining);
    }
}
