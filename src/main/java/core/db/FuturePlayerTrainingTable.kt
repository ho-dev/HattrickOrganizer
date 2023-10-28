package core.db;


import core.training.FuturePlayerTraining;
import java.sql.Types;
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
                ColumnDescriptor.Builder.newInstance().setColumnName("playerId").setGetter((o) -> ((FuturePlayerTraining) o).getPlayerId()).setSetter((o, v) -> ((FuturePlayerTraining) o).setPlayerId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("fromWeek").setGetter((o) -> ((FuturePlayerTraining) o).getFromWeek()).setSetter((o, v) -> ((FuturePlayerTraining) o).setFromWeek((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("fromSeason").setGetter((o) -> ((FuturePlayerTraining) o).getFromSeason()).setSetter((o, v) -> ((FuturePlayerTraining) o).setFromSeason((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("toWeek").setGetter((o) -> ((FuturePlayerTraining) o).getToWeek()).setSetter((o, v) -> ((FuturePlayerTraining) o).setToWeek((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("toSeason").setGetter((o) -> ((FuturePlayerTraining) o).getToSeason()).setSetter((o, v) -> ((FuturePlayerTraining) o).setToSeason((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("prio").setGetter((o) -> ((FuturePlayerTraining) o).getPriority().getValue()).setSetter((o, v) -> ((FuturePlayerTraining) o).setPriority(FuturePlayerTraining.Priority.valueOf((int) v))).setType(Types.INTEGER).isNullable(false).build()
        };
    }

    List<FuturePlayerTraining> getFuturePlayerTrainingPlan(int playerId) {
        return load(FuturePlayerTraining.class, playerId);
    }

    public void storeFuturePlayerTrainings(List<FuturePlayerTraining> futurePlayerTrainings) {
        for (var t : futurePlayerTrainings) {
            store(t);
        }
    }
}