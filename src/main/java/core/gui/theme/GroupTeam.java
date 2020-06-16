package core.gui.theme;

import com.kitfox.svg.app.beans.SVGIcon;

public class GroupTeam {

    private static final String [] FILL_COLOR = {"#FFFFFF", "#f65252", "#d382ff", "#38a0fe","#f4ad2f", "#45e6a4",  "#f6ef6b"};
    private static final String [] STROKE_COLOR = {"#FFFFFF", "#bd1b1b", "#8449a4", "#1b5284","#845c13", "#2a9267",  "#b7b241"};

    public static final String[] TEAMSMILIES 	= { "", "A-Team","B-Team", "C-Team", "D-Team", "E-Team", "F-Team" };
    public static final String NO_TEAM 			= "No-Team";

    public static SVGIcon getGroupIcon(String key) {
        return getGroupIcon(key, 24, 24);
    }

    public static SVGIcon getGroupIcon(String key, int width, int height) {
        return getGroupIcon(key, width, height, null);
    }

    public static SVGIcon getGroupIcon(String key, int width, int height, String opacity) {

        for (int i = 0; i < 7; i++) {
            if (key.equals(GroupTeam.TEAMSMILIES[i])) {
                return ImageUtilities.getSvgIcon(HOIconName.GROUP_TEAM,  width,  height, FILL_COLOR[i], STROKE_COLOR[i], opacity);
            }

        }
        return ImageUtilities.getSvgIcon(HOIconName.GROUP_TEAM,  width,  height, FILL_COLOR[0], STROKE_COLOR[0], opacity);
    }
}
