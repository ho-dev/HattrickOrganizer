package core.db;

import core.gui.HOMainFrame;
import core.net.MyConnector;
import core.util.HOLogger;
import module.youth.YouthTraining;
import module.youth.YouthTrainingType;
import okhttp3.HttpUrl;
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
                new ColumnDescriptor("URL", Types.VARCHAR, true, 256),
                new ColumnDescriptor("FILENAME", Types.VARCHAR, true, 256),
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

        String logoURL, logoFileName;

        // 1. Get logoFileName from the database
        var rs= adapter.executeQuery("SELECT * from " + getTableName() + " WHERE TEAM_ID=" + teamID);
        if (rs == null) {
            HOLogger.instance().error(this.getClass(), "error with table " + getTableName());
            return null;
        }

        try {
            if (!rs.next()) {
                HOLogger.instance().error(this.getClass(), "logo information not available in database for team ID=" + teamID);
                return null;
            }
            else {
                logoURL = rs.getString("URL");
                if(logoURL.equals("null")){
                    HOLogger.instance().error(this.getClass(), "logo information not available in database for team ID=" + teamID);
                    return null;
                }

                logoFileName = teamLogoFolderPath.resolve(rs.getString("FILENAME")).toString();
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
                boolean bSuccess = UpdateHelper.download(logoURL, logo);
                if (!bSuccess) {
                    HOLogger.instance().error(this.getClass(), "error when trying to download logo of team ID: " + teamID + "\n" + logoURL );
                    return null;
                }
            }

            //we update LAST_ACCESS value
            updateLastAccessTime(teamID);

            return logoFileName;
        }



    public void storeTeamLogoInfo(int teamID, String logoURI, Timestamp lastAccess){
        String logoURL=null, fileName=null;

        if(logoURI == null) {
            // case of bot team ?
            HOLogger.instance().debug(this.getClass(), "storeTeamLogoInfo: logo URI was null for team " + teamID);
        }
        else{
            if (logoURI.contains(".")) {
                logoURL = "https:" + logoURI;
                HttpUrl url = HttpUrl.parse(logoURL);
                if ( url != null ) {
                    fileName = url.pathSegments().get(url.pathSize() - 1);
                }
            }

            if ( fileName == null){
                HOLogger.instance().error(this.getClass(), "storeTeamLogoInfo: logo URI not recognized " + logoURI);
                return;
            }
        }

        String sql = "MERGE INTO " + getTableName() + " AS t USING (VALUES(" + teamID + ", '" + logoURL + "', '" + fileName + "', " + lastAccess + ")) AS vals(a, b, c, d) " +
                "ON t.TEAM_ID=vals.a \n" +
                "WHEN MATCHED THEN UPDATE SET t.URL=vals.b, t.FILENAME=vals.c, t.LAST_ACCESS=vals.d \n" +
                "WHEN NOT MATCHED THEN INSERT VALUES vals.a, vals.b, vals.c, vals.d";

        adapter.executeUpdate(sql);
//        HOLogger.instance().debug(this.getClass(), "storeTeamLogoInfo: " +  teamID + " " +  logoURL + " " +  lastAccess);
    }



    public void updateLastAccessTime(int teamID){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String sql = "UPDATE CLUBS_LOGO SET LAST_ACCESS = '" + now + "' WHERE TEAM_ID = " + teamID;
        adapter.executeUpdate(sql);
//        HOLogger.instance().debug(this.getClass(), "Update access time info of teamID : " +  teamID);
    }

}
