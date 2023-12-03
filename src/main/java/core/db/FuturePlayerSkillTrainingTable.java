package core.db;


import core.constants.player.PlayerSkill;
import core.training.FuturePlayerSkillTraining;
import core.training.FuturePlayerTraining;

import java.sql.Types;
import java.util.List;

public class FuturePlayerSkillTrainingTable extends AbstractTable {

    public final static String TABLENAME = "FUTUREPLAYERSKILLTRAINING";

    /**
     * constructor
     */
    public FuturePlayerSkillTrainingTable(ConnectionManager adapter) {
        super(TABLENAME, adapter);
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
                ColumnDescriptor.Builder.newInstance()
                        .setColumnName("playerId")
                        .setGetter((o) -> ((FuturePlayerSkillTraining) o).getPlayerId())
                        .setSetter((o, v) -> ((FuturePlayerSkillTraining) o).setPlayerId((int) v))
                        .setType(Types.INTEGER)
                        .isNullable(false)
                        .build(),
                ColumnDescriptor.Builder.newInstance()
                        .setColumnName("skillId")
                        .setGetter((o) -> ((FuturePlayerSkillTraining) o).getSkillId().toInt())
                        .setSetter((o, v) -> ((FuturePlayerSkillTraining) o).setSkillId(PlayerSkill.fromInteger((Integer) v)))
                        .setType(Types.INTEGER)
                        .isNullable(false)
                        .build(),
                ColumnDescriptor.Builder.newInstance()
                        .setColumnName("prio")
                        .setGetter((o) -> FuturePlayerSkillTraining.getPriorityAsInteger((FuturePlayerSkillTraining)o))
                        .setSetter((o, v) -> ((FuturePlayerSkillTraining) o).setPriority(FuturePlayerTraining.Priority.valueOf((int) v)))
                        .setType(Types.INTEGER)
                        .isNullable(true)
                        .build()
        };
    }

    List<FuturePlayerSkillTraining> loadFuturePlayerSkillTraining(int playerId) {
        return load(FuturePlayerSkillTraining.class, playerId);
    }

    public void storeFuturePlayerSkillTraining(int playerId, List<FuturePlayerSkillTraining> futurePlayerTrainings) {
        executePreparedDelete(playerId);
        for (var t : futurePlayerTrainings) {
            store(t);
        }
    }
}