package core.db;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.player.YouthPlayer;
import core.util.Helper;
import tool.hrfExplorer.HrfExplorer;

import java.sql.SQLException;
import java.sql.Types;

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
                new ColumnDescriptor("Type", Types.INTEGER, false),
                new ColumnDescriptor("Variation", Types.INTEGER, false),
                new ColumnDescriptor("SkillType", Types.INTEGER, false),
                new ColumnDescriptor("SkillLevel", Types.INTEGER, false)
        };
    }

    public int countScoutComments(int youthplayerid) {
        var sql = "SELECT count(*) FROM "+getTableName()+" WHERE YOUTHPLAYER_ID=" + youthplayerid;
        var rs = adapter.executeQuery(sql);
        try {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException sexc) {
            HrfExplorer.appendText("" + sexc);
        }
        return 0;
    }

    public int deleteScoutComments(int youthplayerid) {
        final String[] where = { "YOUTHPLAYER_ID" };
        final String[] values = { "" + youthplayerid };
        return delete(where, values);
    }

    public void saveYouthScoutComment(int i, int youthPlayerId,  YouthPlayer.ScoutComment c) {
        //insert vorbereiten
        var sql = new StringBuilder("INSERT INTO ");
        sql.append(getTableName())
                .append(" (YOUTHPLAYER_ID,INDEX,Text,Type,Variation,SkillType,SkillLevel) VALUES(")
                .append(youthPlayerId).append(",")
                .append(i).append(",")
                .append(c.text).append(",")
                .append(c.type).append(",")
                .append(c.variation).append(",")
                .append(c.skillType).append(",")
                .append(c.skillLevel)
                .append(")");
        adapter.executeUpdate(sql.toString());
    }
}
