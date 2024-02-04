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
            addColor(HOColorName.RED, defaults.getColor("palette.red"));
            addColor(HOColorName.BLUE, defaults.getColor("palette.blue"));
            addColor(HOColorName.GREEN, defaults.getColor("palette.green"));
            addColor(HOColorName.YELLOW, defaults.getColor("palette.yellow"));
            addColor(HOColorName.ORANGE, defaults.getColor("palette.orange"));

            // Use defaults from LAF
            addColor(HOColorName.TABLEENTRY_FG, defaults.getColor("Label.foreground"));
            addColor(HOColorName.LABEL_FG, defaults.getColor("Label.foreground"));
            addColor(HOColorName.PANEL_BG, defaults.getColor("background"));
            addColor(HOColorName.TABLEENTRY_BG, Color.WHITE);
            addColor(HOColorName.BACKGROUND_CONTAINER, Color.WHITE);
            addColor(HOColorName.TABLE_SELECTION_FG, defaults.getColor("Label.foreground"));
            addColor(HOColorName.TABLE_SELECTION_BG, new Color(200, 200, 200));

            // Smileys
            addColor(HOColorName.SMILEYS_COLOR, defaults.getColor("Label.foreground"));

            // Player Specialties
            addColor(HOColorName.PLAYER_SPECIALTY_COLOR, defaults.getColor("Label.foreground"));

            // Statistics
            addColor(HOColorName.STAT_PANEL_BG, defaults.getColor("background").brighter());

            //training bar
            addColor(HOColorName.FULL_TRAINING_DONE, defaults.getColor("palette.forest"));
            addColor(HOColorName.PARTIAL_TRAINING_DONE, defaults.getColor("palette.lime"));
            addColor(HOColorName.FULL_STAMINA_DONE, defaults.getColor("palette.blue"));

            // borders training position in lineup
            addColor(HOColorName.PLAYER_POSITION_PANEL_BORDER, ThemeManager.getColor(HOColorName.TABLEENTRY_BG));

            // League Details
            addColor(HOColorName.SHOW_MATCH, defaults.getColor("palette.forest"));
            addColor(HOColorName.DOWNLOAD_MATCH, defaults.getColor("palette.red"));
//            addColor(HOColorName.LEAGUEHISTORY_GRID_FG, defaults.getColor("background").darker());
            addColor(HOColorName.LEAGUEHISTORY_CROSS_FG, defaults.getColor("background").darker());
            addColor(HOColorName.HOME_TEAM_FG, new Color(179,60,180));
            addColor(HOColorName.SELECTED_TEAM_FG, new Color(36,175,235));
            addColor(HOColorName.LEAGUE_PANEL_BG, defaults.getColor("background").brighter());

            // Lineup
//            addColor(HOColorName.LINEUP_RATING_BORDER, Color.BLACK);
            addColor(HOColorName.RATING_BORDER_BELOW_LIMIT, new Color(255, 0, 0));
            addColor(HOColorName.RATING_BORDER_ABOVE_LIMIT, new Color(0, 0, 225));
            addColor(HOColorName.START_ASSISTANT, defaults.getColor("palette.forest"));
            addColor(HOColorName.CLEAR_LINEUP, defaults.getColor("palette.red"));
            addColor(HOColorName.LINEUP_COLOR, defaults.getColor("palette.brown"));
            addColor(HOColorName.LINEUP_HIGHLIGHT_FG, defaults.getColor("palette.brown"));

            addColor(HOColorName.LINK_LABEL_FG, new Color(61,64,66));

            // Transfer module
            addColor(HOColorName.TRANSFER_IN_COLOR, defaults.getColor("palette.green"));
            addColor(HOColorName.TRANSFER_OUT_COLOR, defaults.getColor("palette.red"));

            //players details
            addColor(HOColorName.PLAYER_DETAILS_BAR_BORDER_COLOR, defaults.getColor("Label.foreground"));
            addColor(HOColorName.PLAYER_DETAILS_BAR_FILL_GREEN, new Color(0, 255, 0));
            addColor(HOColorName.PLAYER_DETAILS_STARS_FILL, defaults.getColor("palette.yellow"));


            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
