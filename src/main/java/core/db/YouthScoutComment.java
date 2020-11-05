package core.db;

import java.sql.Types;

public class YouthScoutComment extends AbstractTable {

    /** tablename **/
    final static String TABLENAME = "YOUTHSCOUTCOMMENT";

    YouthScoutComment(JDBCAdapter adapter) {
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
}
