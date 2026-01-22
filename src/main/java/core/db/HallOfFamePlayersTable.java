package core.db;

import core.util.HODateTime;
import module.halloffame.HallOfFamePlayer;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class HallOfFamePlayersTable extends AbstractTable {

    /**
     * Table name
     **/
    static final String TABLENAME = "HALLOFFAME";

    HallOfFamePlayersTable(ConnectionManager adapter) {
        super(TABLENAME, adapter);
        idColumns = 2;
    }

    @Override
    protected void initColumns() {
        this.columns = new ArrayList<>(List.of(
                ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter((p) -> ((HallOfFamePlayer) p).getHrfId()).setSetter((p, v) -> ((HallOfFamePlayer) p).setHrfId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("ID").setGetter((p) -> ((HallOfFamePlayer) p).getPlayerId()).setSetter((p, v) -> ((HallOfFamePlayer) p).setPlayerId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("CountryId").setGetter((p) -> ((HallOfFamePlayer) p).getCountryId()).setSetter((p, v) -> ((HallOfFamePlayer) p).setCountryId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("ExpertTypeId").setGetter((p) -> ((HallOfFamePlayer) p).getExpertTypeId()).setSetter((p, v) -> ((HallOfFamePlayer) p).setExpertTypeId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("FirstName").setGetter((p) -> ((HallOfFamePlayer) p).getFirstName()).setSetter((p, v) -> ((HallOfFamePlayer) p).setFirstName((String) v)).setType(Types.VARCHAR).setLength(100).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("NickName").setGetter((p) -> ((HallOfFamePlayer) p).getNickName()).setSetter((p, v) -> ((HallOfFamePlayer) p).setNickName((String) v)).setType(Types.VARCHAR).setLength(100).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("LastName").setGetter((p) -> ((HallOfFamePlayer) p).getLastName()).setSetter((p, v) -> ((HallOfFamePlayer) p).setLastName((String) v)).setType(Types.VARCHAR).setLength(100).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("HOFAge").setGetter((p) -> ((HallOfFamePlayer) p).getHofAge()).setSetter((p, v) -> ((HallOfFamePlayer) p).setHofAge((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("Age").setGetter((p) -> ((HallOfFamePlayer) p).getAge()).setSetter((p, v) -> ((HallOfFamePlayer) p).setAge((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("AgeDays").setGetter((p) -> ((HallOfFamePlayer) p).getAgeDays()).setSetter((p, v) -> ((HallOfFamePlayer) p).setAgeDays((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("HOFDate").setGetter((p) -> HODateTime.toDbTimestamp(((HallOfFamePlayer) p).getHofDate())).setSetter((p, v) -> ((HallOfFamePlayer) p).setHofDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("ArrivalDate").setGetter((p) -> HODateTime.toDbTimestamp(((HallOfFamePlayer) p).getArrivalDate())).setSetter((p, v) -> ((HallOfFamePlayer) p).setArrivalDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("NextBirthday").setGetter((p) -> HODateTime.toDbTimestamp(((HallOfFamePlayer) p).getNextBirthday())).setSetter((p, v) -> ((HallOfFamePlayer) p).setNextBirthday((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(true).build()
        )).toArray(new ColumnDescriptor[0]);
    }

    @Override
    public String createDeleteStatement() {
        return createDeleteStatement("WHERE HRF_ID=?");
    }

    public void DeleteHallOfFamePlayersTable(int hrfId) {
        executePreparedDelete(hrfId);
    }

    public void storeHallOfFamePlayer(int hrfId, HallOfFamePlayer player) {
        player.setHrfId(hrfId);
        store(player);
    }

    @Override
    public String createSelectStatement() {
        return createSelectStatement("WHERE HRF_ID=?");
    }

    public List<HallOfFamePlayer> loadHallOfFame(int hrfId) {
        return load(HallOfFamePlayer.class, hrfId);
    }
}
