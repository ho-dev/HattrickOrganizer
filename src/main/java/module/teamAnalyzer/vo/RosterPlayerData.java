package module.teamAnalyzer.vo;

import java.util.ArrayList;
import java.util.List;

public class RosterPlayerData {
    //~ Instance fields ----------------------------------------------------------------------------

    private String name;
    private RosterRoleData[] app = new RosterRoleData[25];
    private int id;

    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new PlayerData object.
     */
    public RosterPlayerData() {
        for (int i = 0; i < 25; i++) {
            app[i] = new RosterRoleData(i);
        }
    }

    //~ Methods ------------------------------------------------------------------------------------
    public void setId(int i) {
        id = i;
    }

    public int getId() {
        return id;
    }

    public int getMainPosition() {
        int pos = -1;
        int val = -1;

        for (int i = 0; i < 25; i++) {
            if (app[i].getApp() > val) {
                pos = i;
                val = app[i].getApp();
            }
        }

        return pos;
    }

    public RosterRoleData getMainRole() {
        return app[getMainPosition()];
    }

    public void setName(String i) {
        name = i;
    }

    public String getName() {
        return name;
    }

    public List<RosterRoleData> getSecondaryRoles() {
        int main = getMainPosition();
        List<RosterRoleData> l = new ArrayList<RosterRoleData>();

        for (int i = 0; i < app.length; i++) {
            RosterRoleData array_element = app[i];

            if ((array_element.getApp() > 0) && (array_element.getPos() != main)) {
                l.add(array_element);
            }
        }

        return l;
    }

    public void addMatch(SpotLineup spot) {
        if (spot.getPosition() > -1) {
            app[spot.getPosition()].addMatch(spot.getRating());
        }
    }
}
