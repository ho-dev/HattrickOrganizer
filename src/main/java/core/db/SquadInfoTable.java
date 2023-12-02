package core.db;

import core.util.HODateTime;
import module.teamAnalyzer.vo.SquadInfo;

import java.sql.Types;
import java.util.List;

public class SquadInfoTable extends AbstractTable {
    final static String TABLENAME = "SQUAD";

    SquadInfoTable(ConnectionManager adapter) {
        super(TABLENAME, adapter);
        idColumns = 2;
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
                ColumnDescriptor.Builder.newInstance().setColumnName("TEAMID").setGetter((p) -> ((SquadInfo) p).getTeamId()).setSetter((p, v) -> ((SquadInfo) p).setTeamId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("LASTMATCH").setGetter((p) -> ((SquadInfo) p).getLastMatchDate().toDbTimestamp()).setSetter((p, v) -> ((SquadInfo) p).setLastMatchDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("FETCHDATE").setGetter((p) -> ((SquadInfo) p).getFetchDate().toDbTimestamp()).setSetter((p, v) -> ((SquadInfo) p).setFetchDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("BRUISED").setGetter((p) -> ((SquadInfo) p).getBruisedCount()).setSetter((p, v) -> ((SquadInfo) p).setBruisedCount((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("INJURED").setGetter((p) -> ((SquadInfo) p).getInjuredCount()).setSetter((p, v) -> ((SquadInfo) p).setInjuredCount((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("INJUREDWEEKS").setGetter((p) -> ((SquadInfo) p).getInjuredWeeksSum()).setSetter((p, v) -> ((SquadInfo) p).setInjuredWeeksSum((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("YELLOWCARDS").setGetter((p) -> ((SquadInfo) p).getSingleYellowCards()).setSetter((p, v) -> ((SquadInfo) p).setSingleYellowCards((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("TWOYELLOWCARDS").setGetter((p) -> ((SquadInfo) p).getTwoYellowCards()).setSetter((p, v) -> ((SquadInfo) p).setTwoYellowCards((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("SUSPENDED").setGetter((p) -> ((SquadInfo) p).getRedCards()).setSetter((p, v) -> ((SquadInfo) p).setRedCards((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("TRANSFERLISTED").setGetter((p) -> ((SquadInfo) p).getTransferListedCount()).setSetter((p, v) -> ((SquadInfo) p).setTransferListedCount((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("TSISUM").setGetter((p) -> ((SquadInfo) p).gettSISum()).setSetter((p, v) -> ((SquadInfo) p).settSISum((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("SALARY").setGetter((p) -> ((SquadInfo) p).getSalarySum()).setSetter((p, v) -> ((SquadInfo) p).setSalarySum((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("PLAYER").setGetter((p) -> ((SquadInfo) p).getPlayerCount()).setSetter((p, v) -> ((SquadInfo) p).setPlayerCount((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("MOTHERCLUB").setGetter((p) -> ((SquadInfo) p).getHomegrownCount()).setSetter((p, v) -> ((SquadInfo) p).setHomegrownCount((int) v)).setType(Types.INTEGER).isNullable(false).build()
        };
    }

    @Override
    protected String[] getCreateIndexStatement() {
        return new String[] { "CREATE INDEX ITA_SQUAD_TEAMID_MATCH ON " + TABLENAME
                + " (TEAMID, LASTMATCH)" };
    }

    public void storeSquadInfo(SquadInfo squadInfo) {
        squadInfo.setIsStored(isStored(squadInfo.getTeamId(), squadInfo.getLastMatchDate().toDbTimestamp()));
        store(squadInfo);
    }

    private final String loadAllSquadInfoSql = createSelectStatement("WHERE TEAMID=?");
    public List<SquadInfo> loadSquadInfo(int teamId){
        return load(SquadInfo.class, connectionManager.executePreparedQuery(loadAllSquadInfoSql, teamId));
    }
}
