package core.db;

import core.net.MyConnector;
import core.util.HOLogger;
import module.youth.YouthTraining;
import module.youth.YouthTrainingType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class TeamsLogoTable extends AbstractTable{
    /** tablename **/
    final static String TABLENAME = "CLUBS_LOGO";

    TeamsLogoTable(JDBCAdapter adapter) {
        super(TABLENAME, adapter);
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
                new ColumnDescriptor("TEAM_ID", Types.INTEGER, false, true),
                new ColumnDescriptor("LOGO_URI", Types.VARCHAR, true, 256),
                new ColumnDescriptor("LAST_ACCESS", Types.TIMESTAMP, false)
        };
    }

    public String getTeamLogoFileName(int teamID){
        var sql = "SELECT LOGO_URI from " + getTableName() ;
        sql += " WHERE TEAM_ID=";
        sql += teamID;
        var rs= adapter.executeQuery(sql);
        try {
            if (rs.next() == false){
                System.out.println("I need tp download the info from team details and store it in the database");
                String logoURI = MyConnector.instance().fetchLogoURI(teamID);
                String filename = "toto.jpg"; //TODO extract the filename from the URI bl/vl/vl/aaa.png  => aaa.png
                boolean isSuccess = storeTeamLogoInfo(logoURI);
                if (isSuccess){
                    return filename;
                }
                else {
                    HOLogger.instance().error(this.getClass(), "error when trying to download logo of team " + teamID);
                    return null;
                }
            }
            return rs.getString("LOGO_URI");
        }
        catch (SQLException throwables) {
            HOLogger.instance().error(this.getClass(), "error when trying to load logo of team " + teamID);
            return null;
        }
    }


    public boolean storeTeamLogoInfo(String logoURI){
        //TODO: I need to do that part
        // download the logo from the uri and enter the entry in the database
    }

//    public void storeYouthTraining(YouthTraining youthTraining) {
//        var matchId = youthTraining.getMatchId();
//        delete( new String[]{"MatchId"}, new String[]{""+matchId});
//        if ( youthTraining.getTraining(YouthTraining.Priority.Primary) != null ||
//                youthTraining.getTraining(YouthTraining.Priority.Secondary) != null) {
//            StringBuilder sql = new StringBuilder("INSERT INTO " + getTableName() + " ( MatchId, Training1, Training2 ) VALUES(" + matchId);
//            for (var p : YouthTraining.Priority.values()) {
//                var tt = youthTraining.getTraining(p);
//                if (tt == null) {
//                    sql.append(",null");
//                } else {
//                    sql.append(",").append(tt.getValue());
//                }
//            }
//            sql.append(")");
//            adapter.executeUpdate(sql.toString());
//        }
//    }
}
