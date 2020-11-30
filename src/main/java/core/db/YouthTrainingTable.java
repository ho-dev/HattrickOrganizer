package core.db;

import java.sql.Types;

public class YouthTrainingTable extends AbstractTable{
    /** tablename **/
    final static String TABLENAME = "YOUTHTRAINING";

    YouthTrainingTable(JDBCAdapter adapter) {
        super(TABLENAME, adapter);
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
                new ColumnDescriptor("MATCH_ID", Types.INTEGER, false),
                new ColumnDescriptor("TRAINING1", Types.INTEGER, true),
                new ColumnDescriptor("TRAINING2", Types.INTEGER, true)
        };
    }
}
