package core.db;

import core.util.HODateTime;
import module.youth.YouthTrainerComment;

import java.sql.Types;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

/**
 * The coach's training comments per youth player (youthplayerdetails 1.4 TrainingReports). Not a
 * snapshot: a report is stored once and kept, a re-download adds only the new ones. The table was
 * declared long before CHPP sent the reports but never registered, so no database had it before
 * DB step 1100.
 */
public class YouthTrainerCommentTable extends AbstractTable {

    static final String TABLENAME = "YOUTHTRAINERCOMMENT";
    static final int TEXT_LENGTH = 1024;

    YouthTrainerCommentTable(ConnectionManager adapter) {
        super(TABLENAME, adapter);
        // player, date, type and skill identify a report
        idColumns = 4;
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
                col("YOUTHPLAYER_ID", Types.INTEGER, p -> row(p).getYouthPlayerId(), (p, v) -> row(p).setYouthPlayerId((int) v)),
                col("REPORT_DATE", Types.TIMESTAMP, p -> HODateTime.toDbTimestamp(row(p).getReportDate()),
                        (p, v) -> row(p).setReportDate((HODateTime) v)),
                col("REPORT_TYPE", Types.INTEGER, p -> row(p).getReportType(), (p, v) -> row(p).setReportType((int) v)),
                col("SKILL_TYPE", Types.INTEGER, p -> row(p).getSkillType(), (p, v) -> row(p).setSkillType((int) v)),
                col("SKILL_LEVEL", Types.INTEGER, p -> row(p).getSkillLevel(), (p, v) -> row(p).setSkillLevel((int) v)),
                ColumnDescriptor.Builder.newInstance().setColumnName("TEXT")
                        .setGetter(p -> truncateString(row(p).getText(), TEXT_LENGTH))
                        .setSetter((p, v) -> row(p).setText((String) v))
                        .setType(Types.VARCHAR).setLength(TEXT_LENGTH).isNullable(true).build()
        };
    }

    private static YouthTrainerComment row(Object o) {
        return (YouthTrainerComment) o;
    }

    private static ColumnDescriptor col(String name, int type,
                                        UnaryOperator<Object> getter, BiConsumer<Object, Object> setter) {
        return ColumnDescriptor.Builder.newInstance().setColumnName(name).setGetter(getter)
                .setSetter(setter).setType(type).isNullable(false).build();
    }

    @Override
    protected String[] getConstraintStatements() {
        // no extra index: the key starts with YOUTHPLAYER_ID, which serves the per-player load
        return new String[]{"  PRIMARY KEY (YOUTHPLAYER_ID, REPORT_DATE, REPORT_TYPE, SKILL_TYPE)"};
    }

    private final String loadByPlayerSql = createSelectStatement("WHERE YOUTHPLAYER_ID=? ORDER BY REPORT_DATE DESC");

    /** The player's reports, newest first. */
    public List<YouthTrainerComment> loadYouthTrainerComments(int youthPlayerId) {
        return load(YouthTrainerComment.class, connectionManager.executePreparedQuery(loadByPlayerSql, youthPlayerId));
    }

    /** Adds the reports not stored yet; a stored one is left as it is. */
    void storeYouthTrainerComments(List<YouthTrainerComment> reports) {
        for (var report : reports) {
            final var key = new Object[]{report.getYouthPlayerId(), HODateTime.toDbTimestamp(report.getReportDate()),
                    report.getReportType(), report.getSkillType()};
            if (!isStored(key)) {
                report.setIsStored(false);
                store(report);
            }
        }
    }
}
