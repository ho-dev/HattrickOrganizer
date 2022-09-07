package core.db;

import core.model.player.CommentType;
import module.youth.YouthPlayer.ScoutComment;
import core.util.HOLogger;
import module.training.Skills;
import tool.hrfExplorer.HrfExplorer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
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
                new ColumnDescriptor("YOUTHPLAYER_ID", Types.INTEGER, false),
                new ColumnDescriptor("INDEX", Types.INTEGER, false),
                new ColumnDescriptor("Text", Types.VARCHAR, true, 255),
                new ColumnDescriptor("Type", Types.INTEGER, true),
                new ColumnDescriptor("Variation", Types.INTEGER, true),
                new ColumnDescriptor("SkillType", Types.INTEGER, true),
                new ColumnDescriptor("SkillLevel", Types.INTEGER, true)
        };
    }

    private final DBManager.PreparedStatementBuilder countScoutCommentsStatementBuilder = new DBManager.PreparedStatementBuilder(this.adapter, "SELECT count(*) FROM "+getTableName()+" WHERE YOUTHPLAYER_ID=?");
    public int countScoutComments(int youthplayerid) {
        var rs = adapter.executePreparedQuery(countScoutCommentsStatementBuilder.getStatement(), youthplayerid);
        try {
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException sexc) {
            HrfExplorer.appendText("" + sexc);
        }
        return 0;
    }

    public void storeYouthScoutComment(int i, int youthPlayerId, ScoutComment c) {
        executePreparedInsert(
                youthPlayerId,
                i,
                c.getText(),
                ValueOf(c.getType()),
                c.getVariation(),
                ValueOf(c.getSkillType()),
                c.getSkillLevel()
        );
    }

    private Integer ValueOf(CommentType type) {
        if (type != null) return type.getValue();
        return null;
    }

    private Integer ValueOf(Skills.ScoutCommentSkillTypeID skillType) {
        if (skillType != null) return skillType.getValue();
        return null;
    }

    public List<ScoutComment> loadYouthScoutComments(int youthplayer_id) {
        final ArrayList<ScoutComment> ret = new ArrayList<>();
        if ( youthplayer_id > -1) {
            var rs = executePreparedSelect(youthplayer_id);
            try {
                if (rs != null) {
                    while (rs.next()) {
                        var comment = createObject(rs);
                        ret.add(comment);
                    }
                }
            } catch (Exception e) {
                HOLogger.instance().log(getClass(), "DatenbankZugriff.getYouthScoutComments: " + e);
            }
        }
        return ret;
    }

    private ScoutComment createObject(ResultSet rs) {
        ScoutComment ret = new ScoutComment();
        try {
            ret.setYouthPlayerId(rs.getInt("YouthPlayer_Id"));
            ret.setIndex(rs.getInt("Index"));
            ret.setText(rs.getString("Text"));
            ret.setSkillLevel(rs.getInt("SkillLevel"));
            ret.setSkillType(Skills.ScoutCommentSkillTypeID.valueOf(rs.getInt("SkillType")));
            ret.setVariation(rs.getInt("Variation"));
            ret.setType(CommentType.valueOf(rs.getInt("Type")));
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),e);
        }
        return ret;
    }
}
