package core.db;

import core.gui.theme.TeamLogoInfo;
import core.util.HODateTime;
import core.util.HOLogger;
import okhttp3.HttpUrl;
import tool.updater.UpdateHelper;
import java.io.File;
import java.nio.file.Path;
import java.sql.Types;

import static core.util.HODateTime.toDbTimestamp;

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
                ColumnDescriptor.Builder.newInstance().setColumnName("TEAM_ID").setGetter((p) -> ((TeamLogoInfo) p).getTeamId()).setSetter((p, v) -> ((TeamLogoInfo) p).setTeamId((int) v)).setType(Types.INTEGER).isPrimaryKey(true).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("URL").setGetter((p) -> ((TeamLogoInfo) p).getUrl()).setSetter((p, v) -> ((TeamLogoInfo) p).setUrl((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("FILENAME").setGetter((p) -> ((TeamLogoInfo) p).getFilename()).setSetter((p, v) -> ((TeamLogoInfo) p).setFilename((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("LAST_ACCESS").setGetter((p) -> toDbTimestamp(((TeamLogoInfo) p).getLastAccess())).setSetter((p, v) -> ((TeamLogoInfo) p).setLastAccess((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(true).build()
        };
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

        var info = loadOne(TeamLogoInfo.class, teamID);
        if ( info == null ){
            return null;
        }
        var url = info.getUrl();
        if ( url == null || url.isEmpty() || url.equals("null") ){
            HOLogger.instance().debug(this.getClass(), "team with no logo team ID=" + teamID);
            return null;
        }

        // 2. Check if the logo has already been downloaded

        var filename = teamLogoFolderPath.resolve(info.getFilename()).toString();
        File logo = new File(filename);
        if (logo.exists()) {
            return filename;
        } else {
            // we try to download the logo from HT servers
            boolean bSuccess = UpdateHelper.download(url, logo);
            if (!bSuccess) {
                HOLogger.instance().error(this.getClass(), "error when trying to download logo of team ID: " + teamID + "\n" + url);
                return null;
            }
        }
        //we update LAST_ACCESS value
        info.setLastAccess(HODateTime.now());
        store(info);
        return filename;
    }

    public void storeTeamLogoInfo(TeamLogoInfo info) {
        if ( info == null ) return;
        String logoURL = null, fileName = null;

        if (info.getUrl() == null) {
            // case of bot team ?
            HOLogger.instance().debug(this.getClass(), "storeTeamLogoInfo: logo URI was null for team " + info.getTeamId());
        } else {
            var logoURI = info.getUrl();
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
            info.setFilename(fileName);
        }

        info.setUrl(logoURL);
        info.setIsStored(isStored(info.getTeamId()));
        store(info);
    }
}
