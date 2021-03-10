package core.gui.theme.dark;


import core.gui.theme.BaseTheme;
import core.gui.theme.HOBooleanName;
import core.gui.theme.ThemeManager;
import core.model.UserParameter;

public abstract class DarkTheme extends BaseTheme {

    public boolean enableTheme() {
        setFont(UserParameter.instance().fontSize);
        ThemeManager.instance().put(HOBooleanName.IMAGEPANEL_BG_PAINTED, false);

        return true;
    }

}
