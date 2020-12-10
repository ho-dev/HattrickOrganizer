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

            // DEFAULT COLOR
            ThemeManager.instance().put(HOColorName.RED, defaults.getColor("palette.red"));
            ThemeManager.instance().put(HOColorName.BLUE, defaults.getColor("palette.blue"));

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

            // Lineup
            ThemeManager.instance().put(HOColorName.LINEUP_POS_MIN_BG, neutralGrey);
            ThemeManager.instance().put(HOColorName.LINEUP_RATING_BORDER, Color.GRAY);

            ThemeManager.instance().put(HOColorName.LINEUP_PLAYER_SELECTED, new Color(60, 63, 65));
            ThemeManager.instance().put(HOColorName.LINEUP_PLAYER_SUB, new Color(48, 54, 56));
            ThemeManager.instance().put(HOColorName.TABLEENTRY_IMPROVEMENT_FG, defaults.getColor("palette.lime"));

            ThemeManager.instance().put(HOColorName.RATING_BORDER_BELOW_LIMIT, new Color(255, 0, 0));
            ThemeManager.instance().put(HOColorName.RATING_BORDER_ABOVE_LIMIT, new Color(65, 105, 225));
            ThemeManager.instance().put(HOColorName.START_ASSISTANT, defaults.getColor("palette.lime"));
            ThemeManager.instance().put(HOColorName.CLEAR_LINEUP, defaults.getColor("palette.red"));
            ThemeManager.instance().put(HOColorName.LINEUP_COLOR, defaults.getColor("palette.gray"));
            ThemeManager.instance().put(HOColorName.LINEUP_HIGHLIGHT_FG, defaults.getColor("palette.blue"));

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
            ThemeManager.instance().put(HOColorName.PALETTE13[0], new Color(0, 255, 0));
            ThemeManager.instance().put(HOColorName.PALETTE13[1], new Color(255, 255, 0));
            ThemeManager.instance().put(HOColorName.PALETTE13[2], new Color(240, 32, 219));
            ThemeManager.instance().put(HOColorName.PALETTE13[3], new Color(255, 255, 255));
            ThemeManager.instance().put(HOColorName.PALETTE13[4], new Color(0, 255, 255));
            ThemeManager.instance().put(HOColorName.PALETTE13[5], new Color(200, 247, 197));
            ThemeManager.instance().put(HOColorName.PALETTE13[6], new Color(249, 140, 122));
            ThemeManager.instance().put(HOColorName.PALETTE13[7], new Color(0, 0, 0));
            ThemeManager.instance().put(HOColorName.PALETTE13[8], new Color(156, 193, 12));
            ThemeManager.instance().put(HOColorName.PALETTE13[9], new Color(255, 0, 0));
            ThemeManager.instance().put(HOColorName.PALETTE13[10], new Color(42, 161, 92));
            ThemeManager.instance().put(HOColorName.PALETTE13[11], new Color(49, 142, 255));
            ThemeManager.instance().put(HOColorName.PALETTE13[12], new Color(169, 169, 169));

            //training bars
            ThemeManager.instance().put(HOColorName.FULL_TRAINING_DONE, defaults.getColor("palette.forest"));
            ThemeManager.instance().put(HOColorName.PARTIAL_TRAINING_DONE, defaults.getColor("palette.lime"));
            ThemeManager.instance().put(HOColorName.FULL_STAMINA_DONE, defaults.getColor("palette.blue"));

            // borders training position in lineup
            ThemeManager.instance().put(HOColorName.PLAYER_POSITION_PANEL_BORDER, ThemeManager.getColor(HOColorName.TABLEENTRY_BG));
            ThemeManager.instance().put(HOColorName.LINEUP_FULL_TRAINING, new Color(240, 32, 219));
            ThemeManager.instance().put(HOColorName.LINEUP_PARTIAL_TRAINING, new Color(249, 140, 122));

            //players
            ThemeManager.instance().put(HOColorName.TABLEENTRY_DECLINE_FG, new Color(231, 144, 60));

            // League Details
            ThemeManager.instance().put(HOColorName.SHOW_MATCH, defaults.getColor("palette.lime"));
            ThemeManager.instance().put(HOColorName.DOWNLOAD_MATCH, defaults.getColor("palette.red"));
            ThemeManager.instance().put(HOColorName.LEAGUEHISTORY_GRID_FG, defaults.getColor("background").brighter());
            ThemeManager.instance().put(HOColorName.LEAGUEHISTORY_CROSS_FG, defaults.getColor("background").brighter());
            ThemeManager.instance().put(HOColorName.HOME_TEAM_FG, new Color(100, 131, 226));
            ThemeManager.instance().put(HOColorName.SELECTED_TEAM_FG, new Color(177, 193, 240));
            ThemeManager.instance().put(HOColorName.LEAGUE_PANEL_BG, defaults.getColor("background").brighter());


            return super.enableTheme();
        } catch (Exception e) {
            HOLogger.instance().warning(getClass(),
                    String.format("Error loading %s: %s", THEME_NAME, e));
            return false;
        }
    }
}
