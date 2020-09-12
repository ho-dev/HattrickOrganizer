package core.gui.theme;

import com.github.weisj.darklaf.icons.IconLoader;
import core.icon.OverlayIcon;
import core.icon.TextIcon;
import org.javatuples.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupTeamFactory {

    private static GroupTeamFactory instance;

    private static final int DEFAULT_SIZE = 18;
    private static final int SMALL_SIZE = 12;

    private static final Map<String, Pair<String, String>> groupIconProperties = new HashMap<>();
    private static final Map<String, Icon> GROUP_ICON = new HashMap<>();
    private static final String INACTIVE_SUFFIX = "_greyed";
    private static final String ACTIVE_SUFFIX = "_active";

    private static final Icon GREYED_ICON = ImageUtilities.getSvgIcon(HOIconName.GREYED_OUT, DEFAULT_SIZE, DEFAULT_SIZE);

    public static final String[] TEAMSMILIES = { "", "A-Team","B-Team", "C-Team", "D-Team", "E-Team", "F-Team" };
    public static final String NO_TEAM = "No-Team";

    static {
        // Pair stores group icon properties, fill colour & stroke colour in that order.
        groupIconProperties.put("A-Team", new Pair("#f65252", "#bd1b1b"));
        groupIconProperties.put("B-Team", new Pair("#d382ff", "#8449a4"));
        groupIconProperties.put("C-Team", new Pair("#38a0fe", "#1b5284"));
        groupIconProperties.put("D-Team", new Pair("#f4ad2f", "#845c13"));
        groupIconProperties.put("E-Team", new Pair("#45e6a4", "#2a9267"));
        groupIconProperties.put("F-Team", new Pair("#f6ef6b", "#b7b241"));
    }

    private GroupTeamFactory() {}

    public static GroupTeamFactory instance() {
        if (instance == null) {
            instance = new GroupTeamFactory();
        }

        return instance;
    }

    public Icon getGreyedGroupIcon(String groupName) {
        return getGreyedGroupIcon(groupName, DEFAULT_SIZE);
    }

    public Icon getGreyedGroupIcon(String groupName, int size) {
        return getDefaultGroupIcon(groupName, false, size);
    }

    public Icon getActiveGroupIcon(String groupName) {
        return getActiveGroupIcon(groupName, DEFAULT_SIZE);
    }

    public Icon getActiveGroupIcon(String groupName, int size) {
        return getDefaultGroupIcon(groupName, true, size);
    }

    public Icon getDefaultGroupIcon(String groupName, boolean active, int size) {
        String suffix = active? ACTIVE_SUFFIX: INACTIVE_SUFFIX;
        Icon icon = GROUP_ICON.get(groupName + suffix + "_" + size);

        if (icon == null) {
            int baseline = Math.round(size * 13f / 16f);
            int fontSize = Math.round(size * 12f / 16f);

            String groupLetter = "";

            Pattern p = Pattern.compile("([A-F])-Team(.png)?");
            Matcher matcher = p.matcher(groupName);
            if (matcher.find()) {
                groupLetter = matcher.group(1);
            }

            final OverlayIcon overlayIcon;
            if (active) {
                overlayIcon = new OverlayIcon(GroupTeamFactory.getGroupIcon(groupName, size, size, "1.0"),
                        new TextIcon(groupLetter, Color.WHITE, new Font("Sans", Font.BOLD, fontSize), size, size, baseline));
            } else {
                overlayIcon = new OverlayIcon(new OverlayIcon(GroupTeamFactory.getGroupIcon(groupName, size, size, "1.0"), GREYED_ICON),
                        new TextIcon(groupLetter, Color.WHITE, new Font("SansSerif", Font.BOLD, fontSize), size, size, baseline));
            }
            GROUP_ICON.put(groupName + suffix + "_" + size, overlayIcon);
            return overlayIcon;
        } else {
            return icon;
        }
    }

    private Icon getGroupIcon(String key, int width, int height) {
        return getGroupIcon(key, width, height, null);
    }

    private static Icon getGroupIcon(String key, int width, int height, String opacity) {

        String compositeKey = "group_" + key + "_" + width + "_" + height + "_" + opacity;
        Icon fullIcon = ThemeManager.getIcon(compositeKey);

        if (fullIcon == null) {

            Pair<String, String> props = groupIconProperties.getOrDefault(key, new Pair("#FFFFFF", "#FFFFFF"));
            String imagePath = (String)ThemeManager.getIconPath(HOIconName.GROUP_TEAM);

            Map<Object, Object> colorMap = new HashMap<>();
            colorMap.put("fillColor", ImageUtilities.getColorFromHex(props.getValue0()));
            colorMap.put("strokeColor", ImageUtilities.getColorFromHex(props.getValue1()));
            if (opacity != null) {
                colorMap.put("opacityVal", (int)(Float.parseFloat(opacity)*100));
            } else {
                colorMap.put("opacityVal", 100);
            }

            fullIcon = IconLoader.get().loadSVGIcon(imagePath, width, height, true, colorMap);
        }

        return fullIcon;
    }
}
