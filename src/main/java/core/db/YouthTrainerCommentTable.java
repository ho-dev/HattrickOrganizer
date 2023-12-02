package core.db;

import core.model.player.CommentType;
import module.youth.YouthTrainerComment;
import module.training.Skills;
import java.sql.Types;
import java.util.List;

public class YouthTrainerCommentTable extends AbstractTable {
    /**
     * tablename
     **/
    final static String TABLENAME = "YOUTHTRAINERCOMMENT";

    YouthTrainerCommentTable(ConnectionManager adapter) {
        super(TABLENAME, adapter);
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
                ColumnDescriptor.Builder.newInstance().setColumnName("YOUTHPLAYER_ID").setGetter((p) -> ((YouthTrainerComment) p).getYouthPlayerId()).setSetter((p, v) -> ((YouthTrainerComment) p).setYouthPlayerId( (int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("MATCH_ID").setGetter((p) -> ((YouthTrainerComment) p).getYouthMatchId()).setSetter((p, v) -> ((YouthTrainerComment) p).setMatchId( (int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("INDEX").setGetter((p) -> ((YouthTrainerComment) p).getIndex()).setSetter((p, v) -> ((YouthTrainerComment) p).setIndex( (int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("Text").setGetter((p) -> ((YouthTrainerComment) p).getText()).setSetter((p, v) -> ((YouthTrainerComment) p).setText( (String) v)).setType(Types.VARCHAR).setLength(255).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("Type").setGetter((p) -> ((YouthTrainerComment) p).getType().getValue()).setSetter((p, v) -> ((YouthTrainerComment) p).setType(CommentType.valueOf( (Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("Variation").setGetter((p) -> ((YouthTrainerComment) p).getVariation()).setSetter((p, v) -> ((YouthTrainerComment) p).setVariation((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("SkillType").setGetter((p) -> ((YouthTrainerComment) p).getSkillType().getValue()).setSetter((p, v) -> ((YouthTrainerComment) p).setSkillType(Skills.ScoutCommentSkillTypeID.valueOf( (Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("SkillLevel").setGetter((p) -> ((YouthTrainerComment) p).getSkillLevel()).setSetter((p, v) -> ((YouthTrainerComment) p).setSkillLevel((Integer) v)).setType(Types.INTEGER).isNullable(true).build()
        };
    }

    public List<YouthTrainerComment> loadYouthTrainerComments(int id) {
        return load(YouthTrainerComment.class, id);
    }
}
