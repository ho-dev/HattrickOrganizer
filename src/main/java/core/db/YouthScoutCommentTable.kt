package core.db;

import core.model.player.CommentType;
import module.youth.YouthPlayer.ScoutComment;
import module.training.Skills;
import java.sql.Types;
import java.util.List;

public class YouthScoutCommentTable extends AbstractTable {

    /** tablename **/
    final static String TABLENAME = "YOUTHSCOUTCOMMENT";

    YouthScoutCommentTable(JDBCAdapter adapter) {
        super(TABLENAME, adapter);
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
                ColumnDescriptor.Builder.newInstance().setColumnName("YOUTHPLAYER_ID").setGetter((p) -> ((ScoutComment) p).getYouthPlayerId()).setSetter((p, v) -> ((ScoutComment) p).setYouthPlayerId( (int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("INDEX").setGetter((p) -> ((ScoutComment) p).getIndex()).setSetter((p, v) -> ((ScoutComment) p).setIndex( (int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("Text").setGetter((p) -> ((ScoutComment) p).getText()).setSetter((p, v) -> ((ScoutComment) p).setText( (String) v)).setType(Types.VARCHAR).setLength(255).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("Type").setGetter((p) -> ((ScoutComment) p).getType().getValue()).setSetter((p, v) -> ((ScoutComment) p).setType(CommentType.valueOf((Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("Variation").setGetter((p) -> ((ScoutComment) p).getVariation()).setSetter((p, v) -> ((ScoutComment) p).setVariation((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("SkillType").setGetter((p) -> Skills.ScoutCommentSkillTypeID.value(((ScoutComment) p).getSkillType())).setSetter((p, v) -> ((ScoutComment) p).setSkillType(Skills.ScoutCommentSkillTypeID.valueOf((Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("SkillLevel").setGetter((p) -> ((ScoutComment) p).getSkillLevel()).setSetter((p, v) -> ((ScoutComment) p).setSkillLevel((Integer) v)).setType(Types.INTEGER).isNullable(true).build()
        };
    }

    public void storeYouthScoutComments(int youthplayerId,  List<ScoutComment> comments) {
        executePreparedDelete(youthplayerId);
        for ( var comment : comments){
            comment.setIsStored(false);
            comment.setYouthPlayerId(youthplayerId);
            store(comment);
        }
    }
    @Override
    protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder(){
        return new PreparedSelectStatementBuilder(this, "WHERE YOUTHPLAYER_ID=? order by INDEX");
    }
    public List<ScoutComment> loadYouthScoutComments(int youthplayer_id) {
        return load(ScoutComment.class, youthplayer_id);
    }

}
