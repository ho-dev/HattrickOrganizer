package core.db;

import core.util.HODateTime;
import module.youth.YouthScout;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static core.db.YouthTeamDetailsTable.date;
import static core.db.YouthTeamDetailsTable.integer;
import static core.db.YouthTeamDetailsTable.text;

/** The youth team's scouts per HRF (youthteamdetails ScoutList). */
final class YouthScoutTable extends AbstractTable {

    static final String TABLENAME = "YOUTHSCOUT";

    YouthScoutTable(ConnectionManager adapter) {
        super(TABLENAME, adapter);
        // loaded and replaced per HRF
        idColumns = 1;
    }

    @Override
    protected void initColumns() {
        final var cols = new ArrayList<ColumnDescriptor>();
        cols.add(ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter(o -> row(o).getHrfId())
                .setSetter((o, v) -> row(o).setHrfId((int) v)).setType(Types.INTEGER).isNullable(false).build());
        cols.add(ColumnDescriptor.Builder.newInstance().setColumnName("YouthScoutID").setGetter(o -> row(o).getYouthScoutId())
                .setSetter((o, v) -> row(o).setYouthScoutId((int) v)).setType(Types.INTEGER).isNullable(false).build());
        cols.add(text("ScoutName", o -> row(o).getScoutName(), (o, v) -> row(o).setScoutName((String) v)));
        cols.add(integer("Age", o -> row(o).getAge(), (o, v) -> row(o).setAge((Integer) v)));
        cols.addAll(YouthTeamDetailsTable.placeColumns());
        cols.add(integer("InCountryID", o -> row(o).getInCountryId(), (o, v) -> row(o).setInCountryId((Integer) v)));
        cols.add(text("InCountryName", o -> row(o).getInCountryName(), (o, v) -> row(o).setInCountryName((String) v)));
        cols.add(integer("InRegionID", o -> row(o).getInRegionId(), (o, v) -> row(o).setInRegionId((Integer) v)));
        cols.add(text("InRegionName", o -> row(o).getInRegionName(), (o, v) -> row(o).setInRegionName((String) v)));
        cols.add(date("HiredDate", o -> row(o).getHiredDate(), (o, v) -> row(o).setHiredDate((HODateTime) v)));
        cols.add(date("LastCalled", o -> row(o).getLastCalled(), (o, v) -> row(o).setLastCalled((HODateTime) v)));
        cols.add(integer("PlayerTypeSearch", o -> row(o).getPlayerTypeSearch(), (o, v) -> row(o).setPlayerTypeSearch((Integer) v)));
        cols.add(integer("HofPlayerId", o -> row(o).getHofPlayerId(), (o, v) -> row(o).setHofPlayerId((Integer) v)));
        cols.add(date("TravelStartDate", o -> row(o).getTravelStartDate(), (o, v) -> row(o).setTravelStartDate((HODateTime) v)));
        cols.add(integer("TravelLength", o -> row(o).getTravelLength(), (o, v) -> row(o).setTravelLength((Integer) v)));
        cols.add(integer("TravelType", o -> row(o).getTravelType(), (o, v) -> row(o).setTravelType((Integer) v)));
        this.columns = cols.toArray(new ColumnDescriptor[0]);
    }

    private static YouthScout row(Object o) {
        return (YouthScout) o;
    }

    @Override
    protected String[] getConstraintStatements() {
        return new String[]{"  PRIMARY KEY (HRF_ID, YouthScoutID)"};
    }

    void store(int hrfId, List<YouthScout> scouts) {
        executePreparedDelete(hrfId);
        for (var scout : scouts) {
            scout.setHrfId(hrfId);
            scout.setIsStored(false);
            super.store(scout);
        }
    }

    List<YouthScout> load(int hrfId) {
        return super.load(YouthScout.class, hrfId);
    }
}
