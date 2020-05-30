package core.gui.theme.dark;

import com.github.weisj.darklaf.DarkLaf;
import com.github.weisj.darklaf.LafManager;

import javax.swing.*;

public class SolarizedDarkTheme extends DarkTheme {

    public final static String THEME_NAME = "Solarized";

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
            LafManager.setTheme(new com.github.weisj.darklaf.theme.SolarizedDarkTheme());
            UIManager.setLookAndFeel(DarkLaf.class.getCanonicalName());

            return super.enableTheme();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
