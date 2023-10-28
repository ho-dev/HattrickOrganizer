package core.gui.theme;

import core.db.AbstractTable;
import core.util.HODateTime;

public class TeamLogoInfo extends AbstractTable.Storable {

    public TeamLogoInfo(){}

    private int teamId;
    private String url;
    private String filename;
    private HODateTime lastAccess;

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public HODateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(HODateTime lastAccess) {
        this.lastAccess = lastAccess;
    }
}
