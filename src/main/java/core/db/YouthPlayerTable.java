package core.db;

import core.constants.player.PlayerSkill;
import core.model.player.Player;
import core.model.player.YouthPlayer;
import core.util.HOLogger;
import module.training.Skills;

import java.sql.ResultSet;
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
        columns = new ColumnDescriptor[]{

                new ColumnDescriptor("HRF_ID", Types.INTEGER, false),
                new ColumnDescriptor("ID", Types.INTEGER, false),
                new ColumnDescriptor("FirstName", Types.VARCHAR, true, 100),
                new ColumnDescriptor("NickName", Types.VARCHAR, true, 100),
                new ColumnDescriptor("LastName", Types.VARCHAR, true, 100),
                new ColumnDescriptor("Age", Types.INTEGER, false),
                new ColumnDescriptor("AgeDays", Types.INTEGER, false),
                new ColumnDescriptor("ArrivalDate", Types.TIMESTAMP, true),
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
                new ColumnDescriptor("YouthMatchDate", Types.TIMESTAMP, true),

                new ColumnDescriptor("Keeper", Types.INTEGER, true),
                new ColumnDescriptor("KeeperMax", Types.INTEGER, true),
                new ColumnDescriptor("KeeperIsAvailable", Types.BOOLEAN, false),
                new ColumnDescriptor("KeeperIsMaxAvailable", Types.BOOLEAN, false),
                new ColumnDescriptor("KeeperMayUnlock", Types.BOOLEAN, true),
                new ColumnDescriptor("KeeperIsMaxReached", Types.BOOLEAN, false),

                new ColumnDescriptor("Defender", Types.INTEGER, true),
                new ColumnDescriptor("DefenderMax", Types.INTEGER, true),
                new ColumnDescriptor("DefenderIsAvailable", Types.BOOLEAN, false),
                new ColumnDescriptor("DefenderIsMaxAvailable", Types.BOOLEAN, false),
                new ColumnDescriptor("DefenderMayUnlock", Types.BOOLEAN, true),
                new ColumnDescriptor("DefenderIsMaxReached", Types.BOOLEAN, false),

                new ColumnDescriptor("Playmaker", Types.INTEGER, true),
                new ColumnDescriptor("PlaymakerMax", Types.INTEGER, true),
                new ColumnDescriptor("PlaymakerIsAvailable", Types.BOOLEAN, false),
                new ColumnDescriptor("PlaymakerIsMaxAvailable", Types.BOOLEAN, false),
                new ColumnDescriptor("PlaymakerMayUnlock", Types.BOOLEAN, true),
                new ColumnDescriptor("PlaymakerIsMaxReached", Types.BOOLEAN, false),

                new ColumnDescriptor("Winger", Types.INTEGER, true),
                new ColumnDescriptor("WingerMax", Types.INTEGER, true),
                new ColumnDescriptor("WingerIsAvailable", Types.BOOLEAN, false),
                new ColumnDescriptor("WingerIsMaxAvailable", Types.BOOLEAN, false),
                new ColumnDescriptor("WingerMayUnlock", Types.BOOLEAN, true),
                new ColumnDescriptor("WingerIsMaxReached", Types.BOOLEAN, false),

                new ColumnDescriptor("Passing", Types.INTEGER, true),
                new ColumnDescriptor("PassingMax", Types.INTEGER, true),
                new ColumnDescriptor("PassingIsAvailable", Types.BOOLEAN, false),
                new ColumnDescriptor("PassingIsMaxAvailable", Types.BOOLEAN, false),
                new ColumnDescriptor("PassingMayUnlock", Types.BOOLEAN, true),
                new ColumnDescriptor("PassingIsMaxReached", Types.BOOLEAN, false),

                new ColumnDescriptor("SetPieces", Types.INTEGER, true),
                new ColumnDescriptor("SetPiecesMax", Types.INTEGER, true),
                new ColumnDescriptor("SetPiecesIsAvailable", Types.BOOLEAN, false),
                new ColumnDescriptor("SetPiecesIsMaxAvailable", Types.BOOLEAN, false),
                new ColumnDescriptor("SetPiecesMayUnlock", Types.BOOLEAN, true),
                new ColumnDescriptor("SetPiecesIsMaxReached", Types.BOOLEAN, false)
        };
    }

    /**
     * save youth players
     */
    void saveYouthPlayers(int hrfId, List<YouthPlayer> players, Timestamp date) {

        final String[] awhereS = { "HRF_ID" };
        final String[] awhereV = { "" + hrfId };

        if (players != null) {
            // Delete old values
            delete(awhereS, awhereV);

            for ( YouthPlayer p: players){
                saveYouthPlayer(hrfId, p, date);
            }
        }
    }

    private void saveYouthPlayer(int hrfId, YouthPlayer player, Timestamp date) {

        final String[] awhereS = { "HRF_ID", "ID" };
        final String[] awhereV = { "" + hrfId, "" + player.getId()};
        if (player != null) {
            // Delete old values
            delete(awhereS, awhereV);

            //insert vorbereiten
            String statement =
                    " (HRF_ID,ID,FirstName,NickName,LastName,Age,AgeDays,ArrivalDate,CanBePromotedIn,PlayerNumber," +
                    "Statement,OwnerNotes,PlayerCategoryID,Cards,InjuryLevel,Specialty,CareerGoals,CareerHattricks," +
                    "LeagueGoals,FriendlyGoals,ScoutId,ScoutingRegionID,ScoutName,YouthMatchID,PositionCode," +
                    "PlayedMinutes,Rating,YouthMatchDate," +
                    "Keeper,KeeperMax,KeeperIsAvailable,KeeperIsMaxAvailable,KeeperMayUnlock,KeeperIsMaxReached," +
                    "Defender,DefenderMax,DefenderIsAvailable,DefenderIsMaxAvailable,DefenderMayUnlock,DefenderIsMaxReached," +
                    "Playmaker,PlaymakerMax,PlaymakerIsAvailable,PlaymakerIsMaxAvailable,PlaymakerMayUnlock,PlaymakerIsMaxReached," +
                    "Winger,WingerMax,WingerIsAvailable,WingerIsMaxAvailable,WingerMayUnlock,WingerIsMaxReached," +
                    "Passing,PassingMax,PassingIsAvailable,PassingIsMaxAvailable,PassingMayUnlock,PassingIsMaxReached," +
                    "SetPieces,SetPiecesMax,SetPiecesIsAvailable,SetPiecesIsMaxAvailable,SetPiecesMayUnlock,SetPiecesIsMaxReached" +
                    ") VALUES(";

            var sql = new StringBuilder("INSERT INTO ");
            sql.append(getTableName())
                    .append(statement)
                    .append(hrfId).append(",")
                    .append(player.getId()).append(",'")
                    .append(DBManager.insertEscapeSequences(player.getFirstName())).append("','")
                    .append(DBManager.insertEscapeSequences(player.getNickName())).append("','")
                    .append(DBManager.insertEscapeSequences(player.getLastName())).append("',")
                    .append(player.getAgeYears()).append(",")
                    .append(player.getAgeDays()).append(",")
                    .append(DBManager.nullOrDateString(player.getArrivalDate())).append(",")
                    .append(player.getCanBePromotedIn()).append(",'")
                    .append(player.getPlayerNumber()).append("','")
                    .append(player.getStatement()).append("','")
                    .append(player.getOwnerNotes()).append("',")
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
                    .append(player.getScoutName()).append("',")
                    .append(player.getYouthMatchID()).append(",")
                    .append(player.getPositionCode()).append(",")
                    .append(player.getPlayedMinutes()).append(",")
                    .append(player.getRating()).append(",")
                    .append(DBManager.nullOrDateString(player.getYouthMatchDate())).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.GOALKEEPER).level).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.GOALKEEPER).max).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.GOALKEEPER).isAvailable).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.GOALKEEPER).isMaxAvailable).append(",")
                    .append(String.valueOf(player.getSkillInfo(Skills.HTSkillID.GOALKEEPER).mayUnlock)).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.GOALKEEPER).isMaxReached).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.DEFENDING).level).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.DEFENDING).max).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.DEFENDING).isAvailable).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.DEFENDING).isMaxAvailable).append(",")
                    .append(String.valueOf(player.getSkillInfo(Skills.HTSkillID.DEFENDING).mayUnlock)).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.DEFENDING).isMaxReached).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.PLAYMAKING).level).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.PLAYMAKING).max).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.PLAYMAKING).isAvailable).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.PLAYMAKING).isMaxAvailable).append(",")
                    .append(String.valueOf(player.getSkillInfo(Skills.HTSkillID.PLAYMAKING).mayUnlock)).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.PLAYMAKING).isMaxReached).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.WINGER).level).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.WINGER).max).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.WINGER).isAvailable).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.WINGER).isMaxAvailable).append(",")
                    .append(String.valueOf(player.getSkillInfo(Skills.HTSkillID.WINGER).mayUnlock)).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.WINGER).isMaxReached).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.PASSING).level).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.PASSING).max).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.PASSING).isAvailable).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.PASSING).isMaxAvailable).append(",")
                    .append(String.valueOf(player.getSkillInfo(Skills.HTSkillID.PASSING).mayUnlock)).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.PASSING).isMaxReached).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.SET_PIECES).level).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.SET_PIECES).max).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.SET_PIECES).isAvailable).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.SET_PIECES).isMaxAvailable).append(",")
                    .append(String.valueOf(player.getSkillInfo(Skills.HTSkillID.SET_PIECES).mayUnlock)).append(",")
                    .append(player.getSkillInfo(Skills.HTSkillID.SET_PIECES).isMaxReached)
                    .append(")");
            try {
                adapter.executeUpdate(sql.toString());
            }
            catch ( Exception e){
                HOLogger.instance().log(getClass(),"saveYouthPlayer: " + sql.toString() + " : " + e);
            }
            var scoutComments = player.getScoutComments();
            if ( scoutComments.size()>0){
                var youthScoutCommentTable = (YouthScoutCommentTable)DBManager.instance().getTable(YouthScoutCommentTable.TABLENAME);
                if ( youthScoutCommentTable.countScoutComments(player.getId())==0){
                    int i=0;
                    for ( var c : scoutComments){
                        youthScoutCommentTable.saveYouthScoutComment(i++,player.getId(),c);
                    }
                }
            }
        }
    }

    /**
     * load youth player of HRF file
     */
    List<YouthPlayer> getYouthPlayer(int hrfID) {
        ResultSet rs = null;
        YouthPlayer player = null;
        String sql = null;
        final ArrayList<YouthPlayer> ret = new ArrayList<>();
        if ( hrfID > -1) {

            sql = "SELECT * from " + getTableName() + " WHERE HRF_ID = " + hrfID;
            rs = adapter.executeQuery(sql);

            try {
                if (rs != null) {
                    rs.beforeFirst();

                    while (rs.next()) {
                        player = createObject(rs);

                        //HOLogger.instance().log(getClass(), player.getSpielerID () );
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
        YouthPlayer player = new YouthPlayer();

        return player;
    }

}
