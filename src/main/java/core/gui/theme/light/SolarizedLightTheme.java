package core.gui.theme.light;

import com.github.weisj.darklaf.DarkLaf;
import com.github.weisj.darklaf.LafManager;
import core.gui.theme.HOBooleanName;
import core.gui.theme.Theme;
import core.gui.theme.ThemeManager;
import core.model.UserParameter;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class SolarizedLightTheme implements Theme {
    public final static String THEME_NAME = "Solarized Light";

    /**
     * @inheritDoc
     */
    @Override
    public String getName() {
        return THEME_NAME;
    }

    @Override
    public boolean loadTheme() {
        try {
            LafManager.setTheme(new com.github.weisj.darklaf.theme.SolarizedLightTheme());
            UIManager.setLookAndFeel(DarkLaf.class.getCanonicalName());

            Map<String, Object> properties = new HashMap<>();
            properties.put("fontSize", UserParameter.instance().schriftGroesse);

            ThemeManager.instance().put(HOBooleanName.IMAGEPANEL_BG_PAINTED, false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
