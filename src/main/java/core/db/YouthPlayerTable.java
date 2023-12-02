package core.db;

import core.constants.player.PlayerSkill;
import core.model.player.Specialty;
import core.util.HODateTime;
import module.youth.YouthPlayer;
import core.util.HOLogger;

import java.sql.*;
import java.util.*;

import static module.youth.YouthSkillInfo.getSkillName;

public class YouthPlayerTable  extends AbstractTable {

    /**
     * tablename
     **/
    final static String TABLENAME = "YOUTHPLAYER";

    YouthPlayerTable(ConnectionManager adapter) {
        super(TABLENAME, adapter);
        idColumns = 2;
    }

    @Override
    protected void initColumns() {
        var tmp = new ArrayList<>(List.of(
                ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter((p) -> ((YouthPlayer) p).getHrfid()).setSetter((p, v) -> ((YouthPlayer) p).setHrfid((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("ID").setGetter((p) -> ((YouthPlayer) p).getId()).setSetter((p, v) -> ((YouthPlayer) p).setId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("FirstName").setGetter((p) -> ((YouthPlayer) p).getFirstName()).setSetter((p, v) -> ((YouthPlayer) p).setFirstName((String) v)).setType(Types.VARCHAR).setLength(100).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("NickName").setGetter((p) -> ((YouthPlayer) p).getNickName()).setSetter((p, v) -> ((YouthPlayer) p).setNickName((String) v)).setType(Types.VARCHAR).setLength(100).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("LastName").setGetter((p) -> ((YouthPlayer) p).getLastName()).setSetter((p, v) -> ((YouthPlayer) p).setLastName((String) v)).setType(Types.VARCHAR).setLength(100).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("Age").setGetter((p) -> ((YouthPlayer) p).getAgeYears()).setSetter((p, v) -> ((YouthPlayer) p).setAgeYears((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("AgeDays").setGetter((p) -> ((YouthPlayer) p).getAgeDays()).setSetter((p, v) -> ((YouthPlayer) p).setAgeDays((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("ArrivalDate").setGetter((p) -> HODateTime.toDbTimestamp(((YouthPlayer) p).getArrivalDate())).setSetter((p, v) -> ((YouthPlayer) p).setArrivalDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("PromotionDate").setGetter((p) -> HODateTime.toDbTimestamp(((YouthPlayer) p).getPromotionDate())).setSetter((p, v) -> ((YouthPlayer) p).setPromotionDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("CanBePromotedIn").setGetter((p) -> ((YouthPlayer) p).getCanBePromotedIn()).setSetter((p, v) -> ((YouthPlayer) p).setCanBePromotedIn((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("PlayerNumber").setGetter((p) -> ((YouthPlayer) p).getPlayerNumber()).setSetter((p, v) -> ((YouthPlayer) p).setPlayerNumber((String) v)).setType(Types.VARCHAR).setLength(10).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("Statement").setGetter((p) -> ((YouthPlayer) p).getStatement()).setSetter((p, v) -> ((YouthPlayer) p).setStatement((String) v)).setType(Types.VARCHAR).setLength(255).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("OwnerNotes").setGetter((p) -> ((YouthPlayer) p).getOwnerNotes()).setSetter((p, v) -> ((YouthPlayer) p).setOwnerNotes((String) v)).setType(Types.VARCHAR).setLength(255).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("PlayerCategoryID").setGetter((p) -> ((YouthPlayer) p).getPlayerCategoryID()).setSetter((p, v) -> ((YouthPlayer) p).setPlayerCategoryID((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("Cards").setGetter((p) -> ((YouthPlayer) p).getCards()).setSetter((p, v) -> ((YouthPlayer) p).setCards((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("InjuryLevel").setGetter((p) -> ((YouthPlayer) p).getInjuryLevel()).setSetter((p, v) -> ((YouthPlayer) p).setInjuryLevel((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("Specialty").setGetter((p) -> Specialty.getValue(((YouthPlayer) p).getSpecialty())).setSetter((p, v) -> ((YouthPlayer) p).setSpecialty(Specialty.getSpecialty((Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("CareerGoals").setGetter((p) -> ((YouthPlayer) p).getCareerGoals()).setSetter((p, v) -> ((YouthPlayer) p).setCareerGoals((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("CareerHattricks").setGetter((p) -> ((YouthPlayer) p).getCareerHattricks()).setSetter((p, v) -> ((YouthPlayer) p).setCareerHattricks((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("LeagueGoals").setGetter((p) -> ((YouthPlayer) p).getLeagueGoals()).setSetter((p, v) -> ((YouthPlayer) p).setLeagueGoals((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("FriendlyGoals").setGetter((p) -> ((YouthPlayer) p).getFriendlyGoals()).setSetter((p, v) -> ((YouthPlayer) p).setFriendlyGoals((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("ScoutId").setGetter((p) -> ((YouthPlayer) p).getScoutId()).setSetter((p, v) -> ((YouthPlayer) p).setScoutId((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("ScoutingRegionID").setGetter((p) -> ((YouthPlayer) p).getScoutingRegionID()).setSetter((p, v) -> ((YouthPlayer) p).setScoutingRegionID((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("ScoutName").setGetter((p) -> ((YouthPlayer) p).getScoutName()).setSetter((p, v) -> ((YouthPlayer) p).setScoutName((String) v)).setType(Types.VARCHAR).setLength(255).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("YouthMatchID").setGetter((p) -> ((YouthPlayer) p).getYouthMatchID()).setSetter((p, v) -> ((YouthPlayer) p).setYouthMatchID((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("positionCode").setGetter((p) -> ((YouthPlayer) p).getPositionCode()).setSetter((p, v) -> ((YouthPlayer) p).setPositionCode((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("playedMinutes").setGetter((p) -> ((YouthPlayer) p).getPlayedMinutes()).setSetter((p, v) -> ((YouthPlayer) p).setPlayedMinutes((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("rating").setGetter((p) -> ((YouthPlayer) p).getRating()).setSetter((p, v) -> ((YouthPlayer) p).setRating((Double) v)).setType(Types.DOUBLE).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("YouthMatchDate").setGetter((p) -> HODateTime.toDbTimestamp(((YouthPlayer) p).getYouthMatchDate())).setSetter((p, v) -> ((YouthPlayer) p).setYouthMatchDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(true).build()
        ));

        for ( var skillId : YouthPlayer.skillIds) {
            tmp.addAll(createColumnDescriptors(skillId));
        }

        columns = tmp.toArray(new ColumnDescriptor[0]);
    }

    private Collection<ColumnDescriptor> createColumnDescriptors(PlayerSkill skillId) {
        var prefix = getSkillName(skillId);
        return new ArrayList<>(List.of(
                ColumnDescriptor.Builder.newInstance().setColumnName(prefix).setGetter((p) -> ((YouthPlayer) p).getSkillInfo(skillId).getCurrentLevel()).setSetter((p, v) -> ((YouthPlayer) p).setCurrentLevel(skillId, (Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName(prefix + "Max").setGetter((p) -> ((YouthPlayer) p).getSkillInfo(skillId).getMax()).setSetter((p, v) -> ((YouthPlayer) p).setMax(skillId, (Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName(prefix + "Start").setGetter((p) -> ((YouthPlayer) p).getSkillInfo(skillId).getStartLevel()).setSetter((p, v) -> ((YouthPlayer) p).setStartLevel(skillId, (Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName(prefix + "IsMaxReached").setGetter((p) -> ((YouthPlayer) p).getSkillInfo(skillId).isMaxReached()).setSetter((p, v) -> ((YouthPlayer) p).setIsMaxReached(skillId, (boolean) v)).setType(Types.BOOLEAN).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName(prefix + "Value").setGetter((p) -> ((YouthPlayer) p).getSkillInfo(skillId).getCurrentValue()).setSetter((p, v) -> ((YouthPlayer) p).setCurrentValue(skillId, (double) v)).setType(Types.DOUBLE).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName(prefix + "StartValue").setGetter((p) -> ((YouthPlayer) p).getSkillInfo(skillId).getStartValue()).setSetter((p, v) -> ((YouthPlayer) p).setStartValue(skillId, (double) v)).setType(Types.DOUBLE).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName(prefix + "Top3").setGetter((p) -> ((YouthPlayer) p).getSkillInfo(skillId).isTop3()).setSetter((p, v) -> ((YouthPlayer) p).setIsTop3(skillId, (Boolean) v)).setType(Types.BOOLEAN).isNullable(true).build()
        ));
    }

    @Override
    protected String[] getConstraintStatements() {
        return new String[]{" PRIMARY KEY (HRF_ID, ID)"};
    }

    @Override
    public String createDeleteStatement() {
        return createDeleteStatement("WHERE HRF_ID=?");
    }
    /**
     * delete youth players
     */
    public void deleteYouthPlayers(int hrfId) {
        executePreparedDelete(hrfId);
    }

    /**
     * store youth player
     * @param hrfId int
     * @param player YouthPlayer
     */
    void storeYouthPlayer(int hrfId, YouthPlayer player) {
        player.setHrfid(hrfId);
        store(player);
    }

    @Override
    public String createSelectStatement() {
        return createSelectStatement("WHERE HRF_ID=?");
    }

    /**
     * load youth player of HRF file id
     */
    List<YouthPlayer> loadYouthPlayers(int hrfID) {
        return load(YouthPlayer.class, hrfID);
    }

    public YouthPlayer loadYouthPlayerOfMatchDate(int id, Timestamp date) {
        return loadOne(YouthPlayer.class, connectionManager.executePreparedQuery(createSelectStatement(" WHERE ID=? AND YOUTHMATCHDATE=?"), id, date));
    }

    public Timestamp loadMinScoutingDate() {
        try (var rs = connectionManager.executePreparedQuery("select min(ArrivalDate) from " + getTableName() + " where PromotionDate is NULL" )) {
            if (rs != null) {
                if (rs.next()) {
                    return rs.getTimestamp(1);
                }
            }
        } catch (Exception e){
            HOLogger.instance().log(getClass(),e);
        }
        return null;
    }

}
