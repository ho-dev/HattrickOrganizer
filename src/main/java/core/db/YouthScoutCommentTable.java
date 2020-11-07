package core.db;

import core.constants.player.PlayerSkill;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.player.YouthPlayer;
import core.model.player.YouthPlayer.ScoutComment;
import core.util.HOLogger;
import core.util.Helper;
import tool.hrfExplorer.HrfExplorer;

import javax.swing.plaf.synth.SynthCheckBoxMenuItemUI;
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
            if (rs != null && rs.next()) {
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

    public void saveYouthScoutComment(int i, int youthPlayerId,  ScoutComment c) {
        //insert vorbereiten
        String sql = "INSERT INTO " + getTableName() +
                " (YOUTHPLAYER_ID,INDEX,Text,Type,Variation,SkillType,SkillLevel) VALUES(" +
                youthPlayerId + "," +
                i + ",'" +
                DBManager.insertEscapeSequences(c.getText()) + "'," +
                c.getType() + "," +
                c.getVariation() + "," +
                c.getSkillType() + "," +
                c.getSkillLevel() +
                ")";
        adapter.executeUpdate(sql);
    }

    public List<ScoutComment> getYouthScoutComments(int youthplayer_id) {
        final ArrayList<ScoutComment> ret = new ArrayList<>();
        if ( youthplayer_id > -1) {
            var sql = "SELECT * from " + getTableName() + " WHERE YOUTHPLAYER_ID = " + youthplayer_id;
            var rs = adapter.executeQuery(sql);
            try {
                if (rs != null) {
                    rs.beforeFirst();
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
            ret.setText(DBManager.deleteEscapeSequences(rs.getString("Text")));
            ret.setSkillLevel(rs.getInt("SkillLevel"));
            ret.setSkillType(rs.getInt("SkillType"));
            ret.setVariation(rs.getInt("Variation"));
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),e);
        }
        return ret;
    }
}
