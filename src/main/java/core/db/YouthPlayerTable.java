package core.db;

import core.model.player.Specialty;
import core.util.HODateTime;
import module.youth.YouthSkillInfo;
import module.youth.YouthPlayer;
import core.util.HOLogger;
import module.training.Skills;

import java.sql.*;
import java.util.*;

import static core.util.HODateTime.toDbTimestamp;

public class YouthPlayerTable  extends AbstractTable {

    /**
     * tablename
     **/
    final static String TABLENAME = "YOUTHPLAYER";

    YouthPlayerTable(JDBCAdapter adapter) {
        super(TABLENAME, adapter);
    }

    @Override
    protected void initColumns() {
        var tmp = new ArrayList<>(List.of(
                new ColumnDescriptor("HRF_ID", Types.INTEGER, false),
                new ColumnDescriptor("ID", Types.INTEGER, false),
                new ColumnDescriptor("FirstName", Types.VARCHAR, true, 100),
                new ColumnDescriptor("NickName", Types.VARCHAR, true, 100),
                new ColumnDescriptor("LastName", Types.VARCHAR, true, 100),
                new ColumnDescriptor("Age", Types.INTEGER, false),
                new ColumnDescriptor("AgeDays", Types.INTEGER, false),
                new ColumnDescriptor("ArrivalDate", Types.TIMESTAMP, true),
                new ColumnDescriptor("PromotionDate", Types.TIMESTAMP, true),
                new ColumnDescriptor("CanBePromotedIn", Types.INTEGER, false),
                new ColumnDescriptor("PlayerNumber", Types.VARCHAR, true, 10),
                new ColumnDescriptor("Statement", Types.VARCHAR, true, 255),
                new ColumnDescriptor("OwnerNotes", Types.VARCHAR, true, 255),
                new ColumnDescriptor("PlayerCategoryID", Types.INTEGER, false),
                new ColumnDescriptor("Cards", Types.INTEGER, false),
                new ColumnDescriptor("InjuryLevel", Types.INTEGER, true),
                new ColumnDescriptor("Specialty", Types.INTEGER, true),
                new ColumnDescriptor("CareerGoals", Types.INTEGER, true),
                new ColumnDescriptor("CareerHattricks", Types.INTEGER, true),
                new ColumnDescriptor("LeagueGoals", Types.INTEGER, true),
                new ColumnDescriptor("FriendlyGoals", Types.INTEGER, true),
                new ColumnDescriptor("ScoutId", Types.INTEGER, true),
                new ColumnDescriptor("ScoutingRegionID", Types.INTEGER, true),
                new ColumnDescriptor("ScoutName", Types.VARCHAR, true, 255),
                new ColumnDescriptor("YouthMatchID", Types.INTEGER, true),
                new ColumnDescriptor("positionCode", Types.INTEGER, true),
                new ColumnDescriptor("playedMinutes", Types.INTEGER, true),
                new ColumnDescriptor("rating", Types.INTEGER, true),
                new ColumnDescriptor("YouthMatchDate", Types.TIMESTAMP, true)
        ));

        for ( var skillId : YouthPlayer.skillIds) {
            tmp.addAll(createColumnDescriptors(skillId));
        }

        columns = tmp.toArray(new ColumnDescriptor[0]);
    }

    private Collection<ColumnDescriptor> createColumnDescriptors(Skills.HTSkillID skillId) {
        var prefix = skillId.toString();
        return new ArrayList<>(List.of(
                new ColumnDescriptor(prefix, Types.INTEGER, true),
                new ColumnDescriptor(prefix + "Max", Types.INTEGER, true),
                new ColumnDescriptor(prefix + "Start", Types.INTEGER, true),
                new ColumnDescriptor(prefix + "IsMaxReached", Types.BOOLEAN, false),
                new ColumnDescriptor(prefix + "Value", Types.DOUBLE, false),
                new ColumnDescriptor(prefix + "StartValue", Types.DOUBLE, false),
                new ColumnDescriptor(prefix + "Top3", Types.BOOLEAN, true)));
    }

    /**
     * save youth players
     */
    void storeYouthPlayers(int hrfId, List<YouthPlayer> players) {
        if (players != null) {
            // Delete old values
            executePreparedDelete(hrfId);

            for ( YouthPlayer p: players){
                storeYouthPlayer(hrfId, p);
            }
        }
    }

    private final PreparedDeleteStatementBuilder deleteYouthPlayerStatementBuilder = new PreparedDeleteStatementBuilder(this, "WHERE HRF_ID=? AND ID=?");
    void storeYouthPlayer(int hrfId, YouthPlayer player) {
        adapter.executePreparedUpdate(deleteYouthPlayerStatementBuilder.getStatement(), hrfId, player.getId());

        var values = new ArrayList<>();
        values.add(hrfId);
        values.add(player.getId());
        values.add(player.getFirstName());
        values.add(player.getNickName());
        values.add(player.getLastName());
        values.add(player.getAgeYears());
        values.add(player.getAgeDays());
        values.add(toDbTimestamp(player.getArrivalDate()));
        values.add(toDbTimestamp(player.getPromotionDate()));
        values.add(player.getCanBePromotedIn());
        values.add(player.getPlayerNumber());
        values.add(player.getStatement());
        values.add(player.getOwnerNotes());
        values.add(player.getPlayerCategoryID());
        values.add(player.getCards());
        values.add(player.getInjuryLevel());
        values.add(player.getSpecialty().getValue());
        values.add(player.getCareerGoals());
        values.add(player.getCareerHattricks());
        values.add(player.getLeagueGoals());
        values.add(player.getFriendlyGoals());
        values.add(player.getScoutId());
        values.add(player.getScoutingRegionID());
        values.add(player.getScoutName());
        values.add(player.getYouthMatchID());
        values.add(player.getPositionCode());
        values.add(player.getPlayedMinutes());
        values.add(player.getRating());
        values.add(toDbTimestamp(player.getYouthMatchDate()));

        for ( var skillId: YouthPlayer.skillIds){
            AppendSkillInfo(values, player, skillId);
        }

        try {
            executePreparedInsert(values.toArray());
        } catch (Exception e) {
            HOLogger.instance().log(getClass(), "saveYouthPlayer: " + e);
        }
        var scoutComments = player.getScoutComments();
        if (scoutComments.size() > 0) {
            var youthScoutCommentTable = (YouthScoutCommentTable) DBManager.instance().getTable(YouthScoutCommentTable.TABLENAME);
            if (youthScoutCommentTable.countScoutComments(player.getId()) == 0) {
                int i = 0;
                for (var c : scoutComments) {
                    youthScoutCommentTable.storeYouthScoutComment(i++, player.getId(), c);
                }
            }
        }
    }

    private void AppendSkillInfo(ArrayList<Object> values, YouthPlayer player, Skills.HTSkillID skillID) {
        var skillInfo = player.getSkillInfo(skillID);
        values.add(skillInfo.getCurrentLevel());
        values.add(skillInfo.getMax());
        values.add(skillInfo.getStartLevel());
        values.add(skillInfo.isMaxReached());
        values.add(skillInfo.getCurrentValue());
        values.add(skillInfo.getStartValue());
        values.add(skillInfo.isTop3());
    }

    /**
     * load youth player of HRF file id
     */
    List<YouthPlayer> loadYouthPlayers(int hrfID) {
        final ArrayList<YouthPlayer> ret = new ArrayList<>();
        if ( hrfID > -1) {
            var rs = executePreparedSelect(hrfID);
            try {
                if (rs != null) {
                    while (rs.next()) {
                        var player = createObject(rs);
                        ret.add(player);
                    }
                }
            } catch (Exception e) {
                HOLogger.instance().log(getClass(), "DatenbankZugriff.loadYouthPlayers: " + e);
            }
        }
        return ret;
    }

    private final PreparedSelectStatementBuilder loadYouthPlayerOfMatchDateStatementBuilder = new PreparedSelectStatementBuilder(this, " WHERE ID=? AND YOUTHMATCHDATE=?");
    public YouthPlayer loadYouthPlayerOfMatchDate(int id, Timestamp date) {
        var rs = adapter.executePreparedQuery(loadYouthPlayerOfMatchDateStatementBuilder.getStatement(), id, date);
        try {
            if (rs != null) {
                if (rs.next()) {
                    return createObject(rs);
                }
            }
        } catch (Exception e) {
            HOLogger.instance().log(getClass(), "DatenbankZugriff.loadYouthPlayer: " + e);
        }
        return null;
    }

    private YouthPlayer createObject(ResultSet rs) {
        YouthPlayer ret = new YouthPlayer();
        try {
            ret.setHrfid(rs.getInt("HRF_ID"));
            ret.setId(rs.getInt("ID"));
            ret.setAgeDays(rs.getInt("AgeDays"));
            ret.setAgeYears(rs.getInt("Age"));
            ret.setArrivalDate(HODateTime.fromDbTimestamp(rs.getTimestamp("ArrivalDate")));
            ret.setCanBePromotedIn(rs.getInt("CanBePromotedIn"));
            ret.setCards(rs.getInt("Cards"));
            ret.setCareerGoals(rs.getInt("CareerGoals"));
            ret.setCareerHattricks(rs.getInt("CareerHattricks"));
            ret.setFirstName(rs.getString("FirstName"));
            ret.setNickName(rs.getString("NickName"));
            ret.setLastName(rs.getString("LastName"));
            ret.setFriendlyGoals(rs.getInt("FriendlyGoals"));
            ret.setInjuryLevel(rs.getInt("InjuryLevel"));
            ret.setLeagueGoals(rs.getInt("LeagueGoals"));
            ret.setOwnerNotes(rs.getString("OwnerNotes"));
            ret.setPlayedMinutes(rs.getInt("PlayedMinutes"));
            ret.setPlayerCategoryID(rs.getInt("PlayerCategoryID"));
            ret.setPlayerNumber(rs.getString("PlayerNumber"));
            ret.setPositionCode(rs.getInt("PositionCode"));
            ret.setRating(rs.getDouble("Rating"));
            ret.setScoutId(rs.getInt("ScoutId"));
            ret.setScoutingRegionID(rs.getInt("ScoutingRegionID"));
            ret.setScoutName(rs.getString("ScoutName"));
            ret.setSpecialty(Specialty.valueOf(DBManager.getInteger(rs,"Specialty")));
            ret.setStatement(rs.getString("Statement"));
            ret.setYouthMatchDate(HODateTime.fromDbTimestamp(rs.getTimestamp("YouthMatchDate")));
            ret.setYouthMatchID(rs.getInt("YouthMatchID"));
            for ( var skillId: YouthPlayer.skillIds){
                setSkillInfo(ret, rs, skillId);
            }

        } catch (Exception e) {
            HOLogger.instance().error(getClass(),e);
        }
        return ret;
    }

    private void setSkillInfo(YouthPlayer youthPlayer, ResultSet rs, Skills.HTSkillID skillID) throws SQLException {
        var skillinfo = new YouthSkillInfo(skillID);
        var columnPrefix = skillID.toString();
        skillinfo.setCurrentLevel(DBManager.getInteger(rs, columnPrefix));
        skillinfo.setStartLevel(DBManager.getInteger(rs, columnPrefix + "Start"));
        skillinfo.setMax(DBManager.getInteger(rs, columnPrefix + "Max"));
        skillinfo.setMaxReached(rs.getBoolean(columnPrefix + "IsMaxReached"));
        skillinfo.setCurrentValue(rs.getDouble(columnPrefix + "Value"));
        skillinfo.setStartValue(rs.getDouble(columnPrefix + "StartValue"));
        skillinfo.setIsTop3(DBManager.getBoolean(rs, columnPrefix + "Top3"));
        youthPlayer.setSkillInfo(skillinfo);
    }

    private final DBManager.PreparedStatementBuilder loadMinScoutingDateStatementBuilder = new DBManager.PreparedStatementBuilder(this.adapter,"select min(ArrivalDate) from " + getTableName() + " where PromotionDate is NULL" );
    public Timestamp loadMinScoutingDate() {
        try {
            var rs = adapter.executePreparedQuery(loadMinScoutingDateStatementBuilder.getStatement());
            if (rs != null) {
                if (rs.next()) {
                    return rs.getTimestamp(1);
                }
            }
        }
        catch (Exception e){
            HOLogger.instance().log(getClass(),e);
        }
        return null;
    }

}
