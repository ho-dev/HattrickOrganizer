package core.gui.theme.light;

import com.github.weisj.darklaf.DarkLaf;
import com.github.weisj.darklaf.LafManager;
import core.gui.theme.BaseTheme;
import core.gui.theme.HOBooleanName;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.UserParameter;

import javax.swing.*;

public class SolarizedLightTheme extends BaseTheme {
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
            UIDefaults defaults = UIManager.getLookAndFeelDefaults();

            setFont(UserParameter.instance().schriftGroesse);
            ThemeManager.instance().put(HOBooleanName.IMAGEPANEL_BG_PAINTED, false);

            // Smileys
            ThemeManager.instance().put(HOColorName.SMILEYS_COLOR, defaults.getColor("Label.foreground"));

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
