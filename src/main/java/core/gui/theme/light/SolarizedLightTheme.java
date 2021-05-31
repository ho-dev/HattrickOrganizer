package core.gui.theme.light;

import com.github.weisj.darklaf.DarkLaf;
import com.github.weisj.darklaf.LafManager;
import core.gui.theme.BaseTheme;
import core.gui.theme.HOBooleanName;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.UserParameter;

import javax.swing.*;
import java.awt.*;

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

            setFont(UserParameter.instance().fontSize);
            ThemeManager.instance().put(HOBooleanName.IMAGEPANEL_BG_PAINTED, false);

            // DEFAULT COLOR
            ThemeManager.instance().put(HOColorName.RED, defaults.getColor("palette.red"));
            ThemeManager.instance().put(HOColorName.BLUE, defaults.getColor("palette.blue"));

            // Use defaults from LAF
            ThemeManager.instance().put(HOColorName.TABLEENTRY_FG, defaults.getColor("Label.foreground"));
            ThemeManager.instance().put(HOColorName.LABEL_FG, defaults.getColor("Label.foreground"));
            ThemeManager.instance().put(HOColorName.PANEL_BG, defaults.getColor("background"));
            ThemeManager.instance().put(HOColorName.TABLEENTRY_BG, Color.WHITE);
            ThemeManager.instance().put(HOColorName.BACKGROUND_CONTAINER, Color.WHITE);
//            ThemeManager.instance().put(HOColorName.PLAYER_SKILL_SPECIAL_BG, new Color(56, 76, 53));
//            ThemeManager.instance().put(HOColorName.PLAYER_SKILL_BG, new Color(95, 86, 38));
            ThemeManager.instance().put(HOColorName.TABLE_SELECTION_FG, defaults.getColor("Label.foreground"));
            ThemeManager.instance().put(HOColorName.TABLE_SELECTION_BG, new Color(200, 200, 200));

            // Smileys
            ThemeManager.instance().put(HOColorName.SMILEYS_COLOR, defaults.getColor("Label.foreground"));

            // Player Specialties
            ThemeManager.instance().put(HOColorName.PLAYER_SPECIALTY_COLOR, defaults.getColor("Label.foreground"));

            // Statistics
            ThemeManager.instance().put(HOColorName.STAT_PANEL_BG, defaults.getColor("background").brighter());

            //training bar
            ThemeManager.instance().put(HOColorName.FULL_TRAINING_DONE, defaults.getColor("palette.forest"));
            ThemeManager.instance().put(HOColorName.PARTIAL_TRAINING_DONE, defaults.getColor("palette.lime"));
            ThemeManager.instance().put(HOColorName.FULL_STAMINA_DONE, defaults.getColor("palette.blue"));

            // borders training position in lineup
            ThemeManager.instance().put(HOColorName.PLAYER_POSITION_PANEL_BORDER, ThemeManager.getColor(HOColorName.TABLEENTRY_BG));

            // League Details
            ThemeManager.instance().put(HOColorName.SHOW_MATCH, defaults.getColor("palette.forest"));
            ThemeManager.instance().put(HOColorName.DOWNLOAD_MATCH, defaults.getColor("palette.red"));
            ThemeManager.instance().put(HOColorName.LEAGUEHISTORY_GRID_FG, defaults.getColor("background").darker());
            ThemeManager.instance().put(HOColorName.LEAGUEHISTORY_CROSS_FG, defaults.getColor("background").darker());
            ThemeManager.instance().put(HOColorName.HOME_TEAM_FG, new Color(179,60,180));
            ThemeManager.instance().put(HOColorName.SELECTED_TEAM_FG, new Color(36,175,235));
            ThemeManager.instance().put(HOColorName.LEAGUE_PANEL_BG, defaults.getColor("background").brighter());

            // Lineup

            ThemeManager.instance().put(HOColorName.LINEUP_RATING_BORDER, Color.BLACK);
            ThemeManager.instance().put(HOColorName.RATING_BORDER_BELOW_LIMIT, new Color(255, 0, 0));
            ThemeManager.instance().put(HOColorName.RATING_BORDER_ABOVE_LIMIT, new Color(0, 0, 225));
            ThemeManager.instance().put(HOColorName.START_ASSISTANT, defaults.getColor("palette.forest"));
            ThemeManager.instance().put(HOColorName.CLEAR_LINEUP, defaults.getColor("palette.red"));
            ThemeManager.instance().put(HOColorName.LINEUP_COLOR, defaults.getColor("palette.brown"));
            ThemeManager.instance().put(HOColorName.LINEUP_HIGHLIGHT_FG, defaults.getColor("palette.brown"));

            ThemeManager.instance().put(HOColorName.LINK_LABEL_FG, new Color(61,64,66));

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
