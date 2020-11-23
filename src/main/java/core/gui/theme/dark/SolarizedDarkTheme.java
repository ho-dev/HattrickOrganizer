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

            // Smileys
            ThemeManager.instance().put(HOColorName.SMILEYS_COLOR, defaults.getColor("Label.foreground"));

            // Player Specialties
            ThemeManager.instance().put(HOColorName.PLAYER_SPECIALTY_COLOR, defaults.getColor("Label.foreground"));

            // palettes
            ThemeManager.instance().put(HOColorName.PALETTE5[0], Color.decode("#69ef7b"));
            ThemeManager.instance().put(HOColorName.PALETTE5[1], Color.decode("#a52e78"));
            ThemeManager.instance().put(HOColorName.PALETTE5[2], Color.decode("#8ae1f9"));
            ThemeManager.instance().put(HOColorName.PALETTE5[3], Color.decode("#056e12"));
            ThemeManager.instance().put(HOColorName.PALETTE5[4], Color.decode("#eb70d5"));

            ThemeManager.instance().put(HOColorName.PALETTE10[0], Color.decode("#b4ddd4"));
            ThemeManager.instance().put(HOColorName.PALETTE10[1], Color.decode("#3441c5"));
            ThemeManager.instance().put(HOColorName.PALETTE10[2], Color.decode("#9bc732"));
            ThemeManager.instance().put(HOColorName.PALETTE10[3], Color.decode("#a90aa1"));
            ThemeManager.instance().put(HOColorName.PALETTE10[4], Color.decode("#2cf52b"));
            ThemeManager.instance().put(HOColorName.PALETTE10[5], Color.decode("#a1173d"));
            ThemeManager.instance().put(HOColorName.PALETTE10[6], Color.decode("#11e38c"));
            ThemeManager.instance().put(HOColorName.PALETTE10[7], Color.decode("#fc5468"));
            ThemeManager.instance().put(HOColorName.PALETTE10[8], Color.decode("#097b35"));
            ThemeManager.instance().put(HOColorName.PALETTE10[9], Color.decode("#f996f1"));

            ThemeManager.instance().put(HOColorName.PALETTE15[0], Color.decode("#b4ddd4"));
            ThemeManager.instance().put(HOColorName.PALETTE15[1], Color.decode("#80412c"));
            ThemeManager.instance().put(HOColorName.PALETTE15[2], Color.decode("#4fd256"));
            ThemeManager.instance().put(HOColorName.PALETTE15[3], Color.decode("#ba0951"));
            ThemeManager.instance().put(HOColorName.PALETTE15[4], Color.decode("#9cc662"));
            ThemeManager.instance().put(HOColorName.PALETTE15[5], Color.decode("#cd49dc"));
            ThemeManager.instance().put(HOColorName.PALETTE15[6], Color.decode("#285d28"));
            ThemeManager.instance().put(HOColorName.PALETTE15[7], Color.decode("#f2b0f6"));
            ThemeManager.instance().put(HOColorName.PALETTE15[8], Color.decode("#5b468b"));
            ThemeManager.instance().put(HOColorName.PALETTE15[9], Color.decode("#36edd3"));
            ThemeManager.instance().put(HOColorName.PALETTE15[10], Color.decode("#f24219"));
            ThemeManager.instance().put(HOColorName.PALETTE15[11], Color.decode("#32a190"));
            ThemeManager.instance().put(HOColorName.PALETTE15[12], Color.decode("#fc8f3b"));
            ThemeManager.instance().put(HOColorName.PALETTE15[13], Color.decode("#000000"));
            ThemeManager.instance().put(HOColorName.PALETTE15[14], Color.decode("#ffce54"));

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
