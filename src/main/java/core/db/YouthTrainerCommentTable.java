package core.db;

import core.model.player.CommentType;
import module.youth.YouthTrainerComment;
import core.util.HOLogger;
import module.training.Skills;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class YouthTrainerCommentTable extends AbstractTable {
    /**
     * tablename
     **/
    final static String TABLENAME = "YOUTHTRAINERCOMMENT";

    YouthTrainerCommentTable(JDBCAdapter adapter) {
        super(TABLENAME, adapter);
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
                new ColumnDescriptor("YOUTHPLAYER_ID", Types.INTEGER, false),
                new ColumnDescriptor("MATCH_ID", Types.INTEGER, false),
                new ColumnDescriptor("INDEX", Types.INTEGER, false),
                new ColumnDescriptor("Text", Types.VARCHAR, true, 255),
                new ColumnDescriptor("Type", Types.INTEGER, true),
                new ColumnDescriptor("Variation", Types.INTEGER, true),
                new ColumnDescriptor("SkillType", Types.INTEGER, true),
                new ColumnDescriptor("SkillLevel", Types.INTEGER, true)
        };
    }

    public List<YouthTrainerComment> loadYouthTrainerComments(int id) {
        final ArrayList<YouthTrainerComment> ret = new ArrayList<>();
        var rs =executePreparedSelect(id);
        try {
            if (rs != null) {
                rs.beforeFirst();
                while (rs.next()) {
                    var comment = createObject(rs);
                    ret.add(comment);
                }
            }
        } catch (Exception e) {
            HOLogger.instance().log(getClass(), "DatenbankZugriff.loadYouthTrainerComments: " + e);
        }
        return ret;
    }

    private YouthTrainerComment createObject(ResultSet rs) {
        var ret = new YouthTrainerComment();
        try {
            ret.setYouthPlayerId(rs.getInt("YOUTHPLAYER_ID"));
            ret.setMatchId(rs.getInt("MATCH_ID"));
            ret.setIndex(rs.getInt("INDEX"));
            ret.setText(rs.getString("TEXT"));
            ret.setType(CommentType.valueOf(DBManager.getInteger(rs, "TYPE")));
            ret.setVariation(DBManager.getInteger(rs, "VARIATION"));
            ret.setSkillType(Skills.ScoutCommentSkillTypeID.valueOf(DBManager.getInteger(rs, "SKILLTYPE")));
            ret.setSkillLevel(DBManager.getInteger(rs, "SKILLLEVEL"));
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),e);
        }
        return ret;
    }

}
