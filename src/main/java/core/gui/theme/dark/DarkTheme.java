package core.gui.theme.dark;


import core.gui.theme.HOBooleanName;
import core.gui.theme.Theme;
import core.gui.theme.ThemeManager;
import core.model.UserParameter;

import java.util.HashMap;
import java.util.Map;

public abstract class DarkTheme implements Theme {

    public boolean enableTheme() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("fontSize", UserParameter.instance().schriftGroesse);

        ThemeManager.instance().put(HOBooleanName.IMAGEPANEL_BG_PAINTED, false);
        return true;
    }

}
