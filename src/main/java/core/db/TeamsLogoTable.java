package core.db;

import core.gui.theme.TeamLogoInfo;
import core.util.HODateTime;
import core.util.HOLogger;
import okhttp3.HttpUrl;

import java.sql.Types;

import static core.util.HODateTime.toDbTimestamp;

public class TeamsLogoTable extends AbstractTable {
    /**
     * tablename
     **/
    final static String TABLENAME = "CLUBS_LOGO";

    TeamsLogoTable(ConnectionManager adapter) {
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
     * @return the team logo file name
     */
    public TeamLogoInfo loadTeamLogoInfo(int teamID) {
        return loadOne(TeamLogoInfo.class, teamID);
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
