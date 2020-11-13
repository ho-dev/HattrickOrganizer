package core.gui.theme.dark;

import com.github.weisj.darklaf.DarkLaf;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.util.HOLogger;

import javax.swing.*;
import java.awt.*;

public class DarculaDarkTheme extends DarkTheme {

    public final static String THEME_NAME = "Darcula";

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
            LafManager.setTheme(new DarculaTheme());
            UIManager.setLookAndFeel(DarkLaf.class.getCanonicalName());
            UIDefaults defaults = UIManager.getLookAndFeelDefaults();

            final Color neutralGrey = new Color(80, 80, 80);

            // Use defaults from LAF
            ThemeManager.instance().put(HOColorName.TABLEENTRY_FG, defaults.getColor("Label.foreground"));
            ThemeManager.instance().put(HOColorName.LABEL_FG, defaults.getColor("Label.foreground"));
            ThemeManager.instance().put(HOColorName.PANEL_BG, defaults.getColor("background"));
            ThemeManager.instance().put(HOColorName.TABLEENTRY_BG, neutralGrey);

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
            ThemeManager.instance().put(HOColorName.TEAM_FG, new Color(100, 131, 226));

            // Lineup
            ThemeManager.instance().put(HOColorName.LINEUP_POS_MIN_BG, neutralGrey);
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
            ThemeManager.instance().put(HOColorName.STAT_PANEL_FG_HELPING_LINES, defaults.getColor("Label.foreground").darker());

            // TS Forecast
            ThemeManager.instance().put(HOColorName.TSFORECAST_ALT_COLOR, new Color(160, 160, 210));

            // HRF Explorer
            ThemeManager.instance().put(HOColorName.HRF_GREEN_BG, new Color(56, 76, 53));
            ThemeManager.instance(). put(HOColorName.HRF_LIGHTBLUE_BG, new Color(55, 71, 83));
            ThemeManager.instance().put(HOColorName.HRF_DARKBLUE_BG, new Color(25, 25, 68));
            ThemeManager.instance().put(HOColorName.HRF_RED_BG, new Color(68, 40, 40));

            // Smileys
            ThemeManager.instance().put(HOColorName.SMILEYS_COLOR, defaults.getColor("Label.foreground"));

            // Player Specialties
            ThemeManager.instance().put(HOColorName.PLAYER_SPECIALTY_COLOR, defaults.getColor("Label.foreground"));

            // palettes
            ThemeManager.instance().put(HOColorName.PALETTE5[0], Color.decode("#35618f"));
            ThemeManager.instance().put(HOColorName.PALETTE5[1], Color.decode("#85dc4d"));
            ThemeManager.instance().put(HOColorName.PALETTE5[2], Color.decode("#9e21a9"));
            ThemeManager.instance().put(HOColorName.PALETTE5[3], Color.decode("#e3faac"));
            ThemeManager.instance().put(HOColorName.PALETTE5[4], Color.decode("#7220f6"));

            ThemeManager.instance().put(HOColorName.PALETTE10[0], Color.decode("#8de4d3"));
            ThemeManager.instance().put(HOColorName.PALETTE10[1], Color.decode("#a6003e"));
            ThemeManager.instance().put(HOColorName.PALETTE10[2], Color.decode("#a1d832"));
            ThemeManager.instance().put(HOColorName.PALETTE10[3], Color.decode("#b825af"));
            ThemeManager.instance().put(HOColorName.PALETTE10[4], Color.decode("#37d51a"));
            ThemeManager.instance().put(HOColorName.PALETTE10[5], Color.decode("#fd4e8b"));
            ThemeManager.instance().put(HOColorName.PALETTE10[6], Color.decode("#1be19f"));
            ThemeManager.instance().put(HOColorName.PALETTE10[7], Color.decode("#375282"));
            ThemeManager.instance().put(HOColorName.PALETTE10[8], Color.decode("#e9b4f5"));
            ThemeManager.instance().put(HOColorName.PALETTE10[9], Color.decode("#338821"));

            ThemeManager.instance().put(HOColorName.PALETTE15[0], Color.decode("#b8e27d"));
            ThemeManager.instance().put(HOColorName.PALETTE15[1], Color.decode("#82269b"));
            ThemeManager.instance().put(HOColorName.PALETTE15[2], Color.decode("#7cee4d"));
            ThemeManager.instance().put(HOColorName.PALETTE15[3], Color.decode("#df72ef"));
            ThemeManager.instance().put(HOColorName.PALETTE15[4], Color.decode("#ccad34"));
            ThemeManager.instance().put(HOColorName.PALETTE15[5], Color.decode("#f23387"));
            ThemeManager.instance().put(HOColorName.PALETTE15[6], Color.decode("#56ebd3"));
            ThemeManager.instance().put(HOColorName.PALETTE15[7], Color.decode("#18519b"));
            ThemeManager.instance().put(HOColorName.PALETTE15[8], Color.decode("#a1def0"));
            ThemeManager.instance().put(HOColorName.PALETTE15[9], Color.decode("#bb190a"));
            ThemeManager.instance().put(HOColorName.PALETTE15[10], Color.decode("#199bce"));
            ThemeManager.instance().put(HOColorName.PALETTE15[11], Color.decode("#f2f27a"));
            ThemeManager.instance().put(HOColorName.PALETTE15[12], Color.decode("#ffd9e3"));
            ThemeManager.instance().put(HOColorName.PALETTE15[13], Color.decode("#495552"));
            ThemeManager.instance().put(HOColorName.PALETTE15[14], Color.decode("#fc8f3b"));

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
