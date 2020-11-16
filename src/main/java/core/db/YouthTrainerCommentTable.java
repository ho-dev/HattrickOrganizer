package core.db;

import java.sql.Types;

public class YouthTrainerCommentTable extends AbstractTable{
    /** tablename **/
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
}
