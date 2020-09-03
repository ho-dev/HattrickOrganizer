package core.gui.theme;

import com.github.weisj.darklaf.icons.IconLoader;
import org.javatuples.Pair;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GroupTeam {

    private static final Map<String, Pair<String, String>> groupIconProperties = new HashMap<>();

    static {
        // Pair stores group icon properties, fill colour & stroke colour in that order.
        groupIconProperties.put("A-Team", new Pair("#f65252", "#bd1b1b"));
        groupIconProperties.put("B-Team", new Pair("#d382ff", "#8449a4"));
        groupIconProperties.put("C-Team", new Pair("#38a0fe", "#1b5284"));
        groupIconProperties.put("D-Team", new Pair("#f4ad2f", "#845c13"));
        groupIconProperties.put("E-Team", new Pair("#45e6a4", "#2a9267"));
        groupIconProperties.put("F-Team", new Pair("#f6ef6b", "#b7b241"));
    }

    public static final String[] TEAMSMILIES = { "", "A-Team","B-Team", "C-Team", "D-Team", "E-Team", "F-Team" };
    public static final String NO_TEAM = "No-Team";

    public static Icon getGroupIcon(String key) {
        return getGroupIcon(key, 12, 12);
    }

    public static Icon getGroupIcon(String key, int width, int height) {
        return getGroupIcon(key, width, height, null);
    }

    public static Icon getGroupIcon(String key, int width, int height, String opacity) {

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
            }
            else{
                colorMap.put("opacityVal", 1);
            }

            fullIcon = IconLoader.get().loadSVGIcon(imagePath, width, height, true, colorMap);
        }

        return fullIcon;
    }
}
