package core.db;

import core.util.HOLogger;
import okhttp3.HttpUrl;
import tool.updater.UpdateHelper;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class TeamsLogoTable extends AbstractTable {
    /**
     * tablename
     **/
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

    @Override
    protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder(){
        return new PreparedSelectStatementBuilder(this, " WHERE TEAM_ID=?");
    }
    @Override
    protected PreparedDeleteStatementBuilder createPreparedDeleteStatementBuilder(){
        return new PreparedDeleteStatementBuilder(this, " WHERE TEAM_ID=?");
    }

    /**
     * Gets team logo file name BUT it will triggers download of the logo from internet if it is not yet available.
     * It will also update LAST_ACCESS field
     *
     * @param teamID             the team id
     * @param teamLogoFolderPath the team logo root folder path
     * @return the team logo file name
     */
    public String getTeamLogoFileName(Path teamLogoFolderPath, int teamID) {

        String logoURL, logoFileName;

        try {
            // 1. Get logoFileName from the database
            var rs = executePreparedSelect(teamID);
            if (rs == null) {
                HOLogger.instance().error(this.getClass(), "error with table " + getTableName());
                return null;
            }
            if (!rs.next()) {
                HOLogger.instance().info(this.getClass(), "logo information not available in database for team ID=" + teamID);
                return null;
            } else {
                logoURL = rs.getString("URL");
                if (logoURL.equals("null")) {
                    HOLogger.instance().debug(this.getClass(), "team with no logo team ID=" + teamID);
                    return null;
                }

                logoFileName = teamLogoFolderPath.resolve(rs.getString("FILENAME")).toString();
            }
        } catch (SQLException throwables) {
            HOLogger.instance().error(this.getClass(), "error with table " + getTableName());
            return null;
        }

        // 2. Check if the logo has already been downloaded

        File logo = new File(logoFileName);
        if (logo.exists()) {
            return logoFileName;
        } else {
            // we try to download the logo from HT servers
            boolean bSuccess = UpdateHelper.download(logoURL, logo);
            if (!bSuccess) {
                HOLogger.instance().error(this.getClass(), "error when trying to download logo of team ID: " + teamID + "\n" + logoURL);
                return null;
            }
        }

        //we update LAST_ACCESS value
        updateLastAccessTime(teamID);

        return logoFileName;
    }

    public void storeTeamLogoInfo(int teamID, String logoURI, Timestamp lastAccess) {
        String logoURL = null, fileName = null;

        if (logoURI == null) {
            // case of bot team ?
            HOLogger.instance().debug(this.getClass(), "storeTeamLogoInfo: logo URI was null for team " + teamID);
        } else {
            if (logoURI.contains(".")) {
                if (!logoURI.startsWith("http")) {
                    logoURL = "http:" + logoURI;
                } else {
                    logoURL = logoURI;
                }
                HttpUrl url = HttpUrl.parse(logoURL);
                if (url != null) {
                    fileName = url.pathSegments().get(url.pathSize() - 1);
                }
            }

            if (fileName == null) {
                HOLogger.instance().error(this.getClass(), "storeTeamLogoInfo: logo URI not recognized " + logoURI);
                return;
            }
        }

        executePreparedDelete(teamID);
        executePreparedInsert(
                teamID,
                logoURL,
                fileName,
                lastAccess
        );

    }

    @Override
    protected PreparedUpdateStatementBuilder createPreparedUpdateStatementBuilder(){
        return new PreparedUpdateStatementBuilder(this, "SET LAST_ACCESS = ? WHERE TEAM_ID = ?");
    }

    public void updateLastAccessTime(int teamID) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        executePreparedUpdate(now, teamID);
    }

}
