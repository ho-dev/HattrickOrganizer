package core.db;

import core.model.player.YouthPlayer;
import core.util.HOLogger;
import module.training.Skills;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

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
        var tmp = new ArrayList<ColumnDescriptor>(List.of(
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

        columns = (ColumnDescriptor[]) tmp.toArray(new ColumnDescriptor[0]);
    }

    private Collection<ColumnDescriptor> createColumnDescriptors(Skills.HTSkillID skillId) {
        var prefix = skillId.toString();
        return new ArrayList<ColumnDescriptor>(List.of(
                new ColumnDescriptor(prefix, Types.INTEGER, true),
                new ColumnDescriptor(prefix + "Max", Types.INTEGER, true),
                new ColumnDescriptor(prefix + "Start", Types.INTEGER, true),
                new ColumnDescriptor(prefix + "IsMaxReached", Types.BOOLEAN, false),
                new ColumnDescriptor(prefix + "Value", Types.DOUBLE, false),
                new ColumnDescriptor(prefix + "StartValue", Types.DOUBLE, false)));
    }

    /**
     * save youth players
     */
    void storeYouthPlayers(int hrfId, List<YouthPlayer> players, Timestamp date) {

        final String[] awhereS = { "HRF_ID" };
        final String[] awhereV = { "" + hrfId };

        if (players != null) {
            // Delete old values
            delete(awhereS, awhereV);

            for ( YouthPlayer p: players){
                storeYouthPlayer(hrfId, p, date);
            }
        }
    }

    private void storeYouthPlayer(int hrfId, YouthPlayer player, Timestamp date) {

        final String[] awhereS = {"HRF_ID", "ID"};
        final String[] awhereV = {"" + hrfId, "" + player.getId()};
        // Delete old values
        delete(awhereS, awhereV);

        //insert vorbereiten
        var sql = new StringBuilder("INSERT INTO ")
                .append(getTableName())
                .append(" (HRF_ID,ID,FirstName,NickName,LastName,Age,AgeDays,ArrivalDate,PromotionDate," +
                        "CanBePromotedIn,PlayerNumber," +
                        "Statement,OwnerNotes,PlayerCategoryID,Cards,InjuryLevel,Specialty,CareerGoals,CareerHattricks," +
                        "LeagueGoals,FriendlyGoals,ScoutId,ScoutingRegionID,ScoutName,YouthMatchID,PositionCode," +
                        "PlayedMinutes,Rating,YouthMatchDate");

        for ( var skillId : YouthPlayer.skillIds){
            sql.append(",").append(getSkillColumnNames(skillId));
        }
        sql.append(") VALUES(")
                .append(hrfId).append(",")
                .append(player.getId()).append(",'")
                .append(DBManager.insertEscapeSequences(player.getFirstName())).append("','")
                .append(DBManager.insertEscapeSequences(player.getNickName())).append("','")
                .append(DBManager.insertEscapeSequences(player.getLastName())).append("',")
                .append(player.getAgeYears()).append(",")
                .append(player.getAgeDays()).append(",")
                .append(DBManager.nullOrValue(player.getArrivalDate())).append(",")
                .append(DBManager.nullOrValue(player.getPromotionDate())).append(",")
                .append(player.getCanBePromotedIn()).append(",'")
                .append(player.getPlayerNumber()).append("','")
                .append(DBManager.insertEscapeSequences(player.getStatement())).append("','")
                .append(DBManager.insertEscapeSequences(player.getOwnerNotes())).append("',")
                .append(player.getPlayerCategoryID()).append(",")
                .append(player.getCards()).append(",")
                .append(player.getInjuryLevel()).append(",")
                .append(player.getSpecialty()).append(",")
                .append(player.getCareerGoals()).append(",")
                .append(player.getCareerHattricks()).append(",")
                .append(player.getLeagueGoals()).append(",")
                .append(player.getFriendlyGoals()).append(",")
                .append(player.getScoutId()).append(",")
                .append(player.getScoutingRegionID()).append(",'")
                .append(DBManager.insertEscapeSequences(player.getScoutName())).append("',")
                .append(player.getYouthMatchID()).append(",")
                .append(player.getPositionCode()).append(",")
                .append(player.getPlayedMinutes()).append(",")
                .append(player.getRating()).append(",")
                .append(DBManager.nullOrValue(player.getYouthMatchDate())).append(",");

        for ( var skillId: YouthPlayer.skillIds){
            AppendSkillInfo(sql, player, skillId);
        }
        sql.append(")");
        try {
            adapter.executeUpdate(sql.toString());
        } catch (Exception e) {
            HOLogger.instance().log(getClass(), "saveYouthPlayer: " + sql.toString() + " : " + e);
        }
        var scoutComments = player.getScoutComments();
        if (scoutComments.size() > 0) {
            var youthScoutCommentTable = (YouthScoutCommentTable) DBManager.instance().getTable(YouthScoutCommentTable.TABLENAME);
            if (youthScoutCommentTable.countScoutComments(player.getId()) == 0) {
                int i = 0;
                for (var c : scoutComments) {
                    youthScoutCommentTable.saveYouthScoutComment(i++, player.getId(), c);
                }
            }
        }
    }

    private String getSkillColumnNames(Skills.HTSkillID prefix) {
        return prefix +","
                + prefix + "Max,"
                + prefix + "Start,"
                + prefix + "IsMaxReached,"
                + prefix + "Value,"
                + prefix + "StartValue";
    }

    private void AppendSkillInfo(StringBuilder sql, YouthPlayer player, Skills.HTSkillID skillID) {
        sql.append(player.getSkillInfo(skillID).getCurrentLevel()).append(",")
                .append(player.getSkillInfo(skillID).getMax()).append(",")
                .append(player.getSkillInfo(skillID).getStartLevel()).append(",")
                .append(player.getSkillInfo(skillID).isMaxReached()).append(",")
                .append(player.getSkillInfo(skillID).getCurrentValue()).append(",")
                .append(player.getSkillInfo(skillID).getStartValue()).append(",");
    }

    /**
     * load youth player of HRF file id
     */
    List<YouthPlayer> loadYouthPlayer(int hrfID) {
        final ArrayList<YouthPlayer> ret = new ArrayList<>();
        if ( hrfID > -1) {
            var sql = "SELECT * from " + getTableName() + " WHERE HRF_ID = " + hrfID;
            var rs = adapter.executeQuery(sql);
            try {
                if (rs != null) {
                    rs.beforeFirst();
                    while (rs.next()) {
                        var player = createObject(rs);
                        ret.add(player);
                    }
                }
            } catch (Exception e) {
                HOLogger.instance().log(getClass(), "DatenbankZugriff.getYouthPlayer: " + e);
            }
        }
        return ret;
    }

    private YouthPlayer createObject(ResultSet rs) {
        YouthPlayer ret = new YouthPlayer();
        try {
            ret.setHrfid(rs.getInt("HRF_ID"));
            ret.setId(rs.getInt("ID"));
            ret.setAgeDays(rs.getInt("AgeDays"));
            ret.setAgeYears(rs.getInt("Age"));
            ret.setArrivalDate(rs.getTimestamp("ArrivalDate"));
            ret.setCanBePromotedIn(rs.getInt("CanBePromotedIn"));
            ret.setCards(rs.getInt("Cards"));
            ret.setCareerGoals(rs.getInt("CareerGoals"));
            ret.setCareerHattricks(rs.getInt("CareerHattricks"));
            ret.setFirstName(DBManager.deleteEscapeSequences(rs.getString("FirstName")));
            ret.setNickName(DBManager.deleteEscapeSequences(rs.getString("NickName")));
            ret.setLastName(DBManager.deleteEscapeSequences(rs.getString("LastName")));
            ret.setFriendlyGoals(rs.getInt("FriendlyGoals"));
            ret.setInjuryLevel(rs.getInt("InjuryLevel"));
            ret.setLeagueGoals(rs.getInt("LeagueGoals"));
            ret.setOwnerNotes(DBManager.deleteEscapeSequences(rs.getString("OwnerNotes")));
            ret.setPlayedMinutes(rs.getInt("PlayedMinutes"));
            ret.setPlayerCategoryID(rs.getInt("PlayerCategoryID"));
            ret.setPlayerNumber(rs.getString("PlayerNumber"));
            ret.setPositionCode(rs.getInt("PositionCode"));
            ret.setRating(rs.getDouble("Rating"));
            ret.setScoutId(rs.getInt("ScoutId"));
            ret.setScoutingRegionID(rs.getInt("ScoutingRegionID"));
            ret.setScoutName(DBManager.deleteEscapeSequences(rs.getString("ScoutName")));
            ret.setSpecialty(rs.getInt("Specialty"));
            ret.setStatement(DBManager.deleteEscapeSequences(rs.getString("Statement")));
            ret.setYouthMatchDate(rs.getTimestamp("YouthMatchDate"));
            ret.setYouthMatchID(rs.getInt("YouthMatchID"));
            for ( var skillId: YouthPlayer.skillIds){
                SetSkillInfo(ret, rs, skillId);
            }

        } catch (Exception e) {
            HOLogger.instance().log(getClass(),e);
        }
        return ret;
    }

    private void SetSkillInfo(YouthPlayer youthPlayer, ResultSet rs, Skills.HTSkillID skillID) throws SQLException {
        var skillinfo = new YouthPlayer.SkillInfo(skillID);
        var columnPrefix = skillID.toString();
        skillinfo.setCurrentLevel(DBManager.getInteger(rs, columnPrefix));
        skillinfo.setStartLevel(DBManager.getInteger(rs,columnPrefix+"Start"));
        skillinfo.setMax(DBManager.getInteger(rs,columnPrefix+"Max"));
        skillinfo.setMaxReached(DBManager.getBoolean(rs, columnPrefix+"IsMaxReached"));
        skillinfo.setCurrentValue(rs.getDouble(columnPrefix+"Value"));
        skillinfo.setStartValue(rs.getDouble(columnPrefix+"StartValue"));
        youthPlayer.setSkillInfo(skillinfo);
    }

    public Timestamp loadMinScoutingDate() {
        var sql = "select min(ArrivalDate) from " + getTableName() + " where PromotionDate is NULL";
        try {
            var rs = adapter.executeQuery(sql);
            if (rs != null) {
                rs.beforeFirst();
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
