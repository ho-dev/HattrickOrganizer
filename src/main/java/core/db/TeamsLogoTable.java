package core.db;

import core.gui.HOMainFrame;
import core.net.MyConnector;
import core.util.HOLogger;
import module.youth.YouthTraining;
import module.youth.YouthTrainingType;
import tool.updater.UpdateController;
import tool.updater.UpdateHelper;

import java.io.File;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
                new ColumnDescriptor("LOGO_URL", Types.VARCHAR, true, 256),
                new ColumnDescriptor("LAST_ACCESS", Types.TIMESTAMP, true)
        };
    }

    /**
     * Gets team logo file name BUT it will triggers download of the logo from internet if it is not yet available.
     * It will also update LAST_ACCESS field
     *
     * @param teamID the team id
     * @param teamLogoFolderPath the team logo root folder path
     * @return the team logo file name
     */
    public String getTeamLogoFileName(Path teamLogoFolderPath, int teamID){

        String logoFullURL, logoFileName;

        // 1. Get logoFileName from the database
        StringBuilder sql = new StringBuilder("SELECT LOGO_URL from " + getTableName()) ;
        sql.append(" WHERE TEAM_ID=").append(teamID);
        var rs= adapter.executeQuery(sql.toString());
        if (rs == null) {
            HOLogger.instance().error(this.getClass(), "error with table " + getTableName());
            return null;
        }

        try {
            if (rs.next() == false) {
                HOLogger.instance().error(this.getClass(), "logo information not available in database for team ID=" + teamID);
                return null;
            }
            else {
                logoFullURL = "http blblblbl" + rs.getString("LOGO_URL"); // ToDo that part
                logoFileName = teamLogoFolderPath.resolve(String.valueOf(teamID)).toString();
            }
        }
        catch (SQLException throwables) {
            HOLogger.instance().error(this.getClass(), "error with table " + getTableName());
            return null;
        }

        // 2. Check if the logo has already been downloaded

            File logo = new File(logoFileName);
            if (logo.exists()) {
                return logoFileName;
            }
            else
            {
               // we try to download the logo from HT servers
                boolean bSuccess = UpdateHelper.download(logoFullURL, logo);
                if (!bSuccess) {
                    HOLogger.instance().error(this.getClass(), "error when trying to download logo of team ID: " + teamID + "\n" + logoFullURL );
                    return null;
                }
            }

            //we update LAST_ACCESS value
            updateLastAccessTime(teamID);

            return logoFileName;
        }



    public void storeTeamLogoInfo(int teamID, String logoURL, Timestamp lastAccess){
        StringBuilder sql = new StringBuilder("MERGE INTO " + getTableName() + " AS t USING (VALUES(") ;
        sql.append(teamID).append(", '").append(logoURL).append("', ").append(lastAccess).append(")) AS vals(a,b,c) ");
        sql.append("ON t.TEAM_ID=vals.a \n");
        sql.append("WHEN MATCHED THEN UPDATE SET t.LOGO_URL=vals.b, t.LAST_ACCESS=vals.c \n");
        sql.append("WHEN NOT MATCHED THEN INSERT VALUES vals.a, vals.b,vals.c");

        adapter.executeUpdate(sql.toString());
        HOLogger.instance().debug(this.getClass(), "storeTeamLogoInfo: " +  teamID + " " +  logoURL + " " +  lastAccess);
    }



    public void updateLastAccessTime(int teamID){
        String sql = "UPDATE CLUBS_LOGO SET LAST_ACCESS = " + new Timestamp(System.currentTimeMillis()) + " WHERE TEAM_ID = " + teamID;
        adapter.executeUpdate(sql);
        HOLogger.instance().debug(this.getClass(), "Update access time info of teamID : " +  teamID);
    }

}
