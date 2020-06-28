package core.gui.theme.dark;

import com.github.weisj.darklaf.DarkLaf;
import com.github.weisj.darklaf.LafManager;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.util.HOLogger;

import javax.swing.*;
import java.awt.*;

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
            UIDefaults defaults = UIManager.getLookAndFeelDefaults();

            final Color blueishColour = new Color(25, 85, 100);

            // Use defaults from LAF
            ThemeManager.instance().put(HOColorName.TABLEENTRY_FG, defaults.getColor("Label.foreground"));
            ThemeManager.instance().put(HOColorName.LABEL_FG, defaults.getColor("Label.foreground"));
            ThemeManager.instance().put(HOColorName.PANEL_BG, defaults.getColor("background"));
            ThemeManager.instance().put(HOColorName.TABLEENTRY_BG, blueishColour);

            ThemeManager.instance().put(HOColorName.TABLE_SELECTION_FG, Color.WHITE);
            ThemeManager.instance().put(HOColorName.TABLE_SELECTION_BG, new Color(65, 65, 65));

            ThemeManager.instance().put(HOColorName.PLAYER_SKILL_SPECIAL_BG, new Color(56, 76, 53));
            ThemeManager.instance().put(HOColorName.PLAYER_SKILL_BG, new Color(95, 86, 38));
            ThemeManager.instance().put(HOColorName.PLAYER_POS_BG, new Color(55, 71, 83));
            ThemeManager.instance().put(HOColorName.PLAYER_SUBPOS_BG, new Color(60, 60, 60));

            // League Details
            // defaults defined by darklaf
            ThemeManager.instance().put(HOColorName.LEAGUE_TITLE_BG, defaults.getColor("TableHeader.background"));
            ThemeManager.instance().put(HOColorName.TABLE_LEAGUE_EVEN, defaults.getColor("Table.background"));
            ThemeManager.instance().put(HOColorName.TABLE_LEAGUE_ODD, defaults.getColor("Table.backgroundAlternative"));
            ThemeManager.instance().put(HOColorName.LEAGUE_FG, defaults.getColor("Table.foreground"));
            ThemeManager.instance().put(HOColorName.LEAGUE_BG, defaults.getColor("Table.background"));
            ThemeManager.instance().put(HOColorName.TEAM_FG, Color.WHITE);

            // Lineup
            ThemeManager.instance().put(HOColorName.LINEUP_POS_MIN_BG, blueishColour);
            ThemeManager.instance().put(HOColorName.LINEUP_RATING_BORDER, Color.GRAY);

            ThemeManager.instance().put(HOColorName.LINEUP_PLAYER_SELECTED, new Color(60, 63, 65));
            ThemeManager.instance().put(HOColorName.LINEUP_PLAYER_SUB, new Color(48, 54, 56));

            // Matches
            ThemeManager.instance().put(HOColorName.MATCHTYPE_LEAGUE_BG, new Color(95, 86, 38));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_BG, new Color(60, 60, 60));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_FRIENDLY_BG, new Color(60, 63, 65));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_INTFRIENDLY_BG, new Color(60, 63, 65));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_INT_BG, new Color(50, 67, 67));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_CUP_BG, new Color(56, 76, 53));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_QUALIFIKATION_BG, new Color(83, 45, 45));

            ThemeManager.instance().put(HOColorName.MATCHTYPE_MASTERS_BG, new Color(80, 70, 43));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_NATIONAL_BG, new Color(57, 54, 62));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_TOURNAMENT_GROUP_BG, new Color(48, 54, 56));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_TOURNAMENT_FINALS_BG, new Color(61, 67, 68));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_DIVISIONBATTLE_BG, new Color(66, 68, 80));

            //Training
            ThemeManager.instance().put(HOColorName.TRAINING_BIRTHDAY_BG, new Color(66, 66, 24));

            // Statistics
            ThemeManager.instance().put(HOColorName.STAT_PANEL_BG, defaults.getColor("background").brighter());
            ThemeManager.instance().put(HOColorName.STAT_PANEL_FG, defaults.getColor("Label.foreground"));

            // TS Forecast
            ThemeManager.instance().put(HOColorName.TSFORECAST_ALT_COLOR, new Color(160, 160, 210));

            // HRF Explorer
            ThemeManager.instance().put(HOColorName.HRF_GREEN_BG, new Color(56, 76, 53));
            ThemeManager.instance(). put(HOColorName.HRF_LIGHTBLUE_BG, new Color(55, 71, 83));
            ThemeManager.instance().put(HOColorName.HRF_DARKBLUE_BG, new Color(25, 25, 68));
            ThemeManager.instance().put(HOColorName.HRF_RED_BG, new Color(68, 40, 40));

            return super.enableTheme();
        } catch (Exception e) {
            HOLogger.instance().warning(getClass(),
                    String.format("Error loading %s: %s",
                            THEME_NAME,
                            e.getMessage()
                    ));
            return false;
        }
    }
}
