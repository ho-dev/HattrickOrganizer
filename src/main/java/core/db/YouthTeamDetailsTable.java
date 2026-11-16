package core.db;

import core.util.HODateTime;
import module.youth.YouthPlace;
import module.youth.YouthTeamDetails;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

/** The youth team per HRF (youthteamdetails 1.5); its scouts are in YOUTHSCOUT. */
final class YouthTeamDetailsTable extends AbstractTable {

    static final String TABLENAME = "YOUTHTEAMDETAILS";
    private static final int NAME_LENGTH = 127;

    YouthTeamDetailsTable(ConnectionManager adapter) {
        super(TABLENAME, adapter);
        idColumns = 1;
    }

    @Override
    protected void initColumns() {
        final var cols = new ArrayList<ColumnDescriptor>();
        cols.add(ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter(o -> row(o).getHrfId())
                .setSetter((o, v) -> row(o).setHrfId((int) v)).setType(Types.INTEGER).isNullable(false).build());
        cols.add(ColumnDescriptor.Builder.newInstance().setColumnName("YouthTeamID").setGetter(o -> row(o).getYouthTeamId())
                .setSetter((o, v) -> row(o).setYouthTeamId((int) v)).setType(Types.INTEGER).isNullable(false).build());
        cols.add(text("YouthTeamName", o -> row(o).getYouthTeamName(), (o, v) -> row(o).setYouthTeamName((String) v)));
        cols.add(text("ShortTeamName", o -> row(o).getShortTeamName(), (o, v) -> row(o).setShortTeamName((String) v)));
        cols.add(date("CreatedDate", o -> row(o).getCreatedDate(), (o, v) -> row(o).setCreatedDate((HODateTime) v)));
        cols.add(integer("UserID", o -> row(o).getUserId(), (o, v) -> row(o).setUserId((Integer) v)));
        cols.add(integer("GenderID", o -> row(o).getGenderId(), (o, v) -> row(o).setGenderId((Integer) v)));
        cols.add(integer("LeagueSystemID", o -> row(o).getLeagueSystemId(), (o, v) -> row(o).setLeagueSystemId((Integer) v)));
        cols.add(ColumnDescriptor.Builder.newInstance().setColumnName("IsPlayingMatch").setGetter(o -> row(o).getPlayingMatch())
                .setSetter((o, v) -> row(o).setPlayingMatch((Boolean) v)).setType(Types.BOOLEAN).isNullable(true).build());
        cols.addAll(placeColumns());
        cols.add(integer("YouthArenaID", o -> row(o).getYouthArenaId(), (o, v) -> row(o).setYouthArenaId((Integer) v)));
        cols.add(text("YouthArenaName", o -> row(o).getYouthArenaName(), (o, v) -> row(o).setYouthArenaName((String) v)));
        cols.add(integer("YouthLeagueID", o -> row(o).getYouthLeagueId(), (o, v) -> row(o).setYouthLeagueId((Integer) v)));
        cols.add(text("YouthLeagueName", o -> row(o).getYouthLeagueName(), (o, v) -> row(o).setYouthLeagueName((String) v)));
        cols.add(integer("YouthLeagueStatus", o -> row(o).getYouthLeagueStatus(), (o, v) -> row(o).setYouthLeagueStatus((Integer) v)));
        cols.add(integer("MotherTeamID", o -> row(o).getMotherTeamId(), (o, v) -> row(o).setMotherTeamId((Integer) v)));
        cols.add(text("MotherTeamName", o -> row(o).getMotherTeamName(), (o, v) -> row(o).setMotherTeamName((String) v)));
        cols.add(integer("TrainerYouthPlayerID", o -> row(o).getTrainerYouthPlayerId(), (o, v) -> row(o).setTrainerYouthPlayerId((Integer) v)));
        cols.add(date("NextTrainingMatchDate", o -> row(o).getNextTrainingMatchDate(), (o, v) -> row(o).setNextTrainingMatchDate((HODateTime) v)));
        this.columns = cols.toArray(new ColumnDescriptor[0]);
    }

    private static YouthTeamDetails row(Object o) {
        return (YouthTeamDetails) o;
    }

    /** CountryID, CountryName, RegionID, RegionName of a team or a scout. */
    static List<ColumnDescriptor> placeColumns() {
        return List.of(
                integer("CountryID", o -> place(o).getCountryId(), (o, v) -> place(o).setCountryId((Integer) v)),
                text("CountryName", o -> place(o).getCountryName(), (o, v) -> place(o).setCountryName((String) v)),
                integer("RegionID", o -> place(o).getRegionId(), (o, v) -> place(o).setRegionId((Integer) v)),
                text("RegionName", o -> place(o).getRegionName(), (o, v) -> place(o).setRegionName((String) v)));
    }

    private static YouthPlace place(Object o) {
        return (YouthPlace) o;
    }

    static ColumnDescriptor integer(String name, UnaryOperator<Object> getter, BiConsumer<Object, Object> setter) {
        return ColumnDescriptor.Builder.newInstance().setColumnName(name).setGetter(getter)
                .setSetter(setter).setType(Types.INTEGER).isNullable(true).build();
    }

    static ColumnDescriptor text(String name, UnaryOperator<Object> getter, BiConsumer<Object, Object> setter) {
        return ColumnDescriptor.Builder.newInstance().setColumnName(name)
                .setGetter(o -> truncateString((String) getter.apply(o), NAME_LENGTH))
                .setSetter(setter).setType(Types.VARCHAR).setLength(NAME_LENGTH).isNullable(true).build();
    }

    static ColumnDescriptor date(String name, UnaryOperator<Object> getter, BiConsumer<Object, Object> setter) {
        return ColumnDescriptor.Builder.newInstance().setColumnName(name)
                .setGetter(o -> HODateTime.toDbTimestamp((HODateTime) getter.apply(o)))
                .setSetter(setter).setType(Types.TIMESTAMP).isNullable(true).build();
    }

    @Override
    protected String[] getConstraintStatements() {
        return new String[]{"  PRIMARY KEY (HRF_ID)"};
    }

    void store(YouthTeamDetails details) {
        executePreparedDelete(details.getHrfId());
        details.setIsStored(false);
        super.store(details);
    }

    YouthTeamDetails load(int hrfId) {
        return loadOne(YouthTeamDetails.class, hrfId);
    }
}
