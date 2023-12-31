package core.gui.theme;

import core.db.AbstractTable;

import java.awt.*;
import java.util.*;
import java.util.List;

import static core.gui.theme.HOColorName.*;

public class HOColor extends AbstractTable.Storable {


    static EnumMap<HOColorName, Map<String, HOColor>> colors = new EnumMap<>(HOColorName.class);

    static {
        addColor(RED, Color.RED);
        addColor(BLACK, Color.BLACK);
        addColor(WHITE, Color.WHITE);
        addColor(GRAY, Color.GRAY);
        addColor(GREEN, Color.GREEN);
        addColor(YELLOW, Color.YELLOW);
        addColor(BLUE, Color.BLUE);
        addColor(ORANGE, Color.ORANGE);
        addColor(DARK_GRAY, Color.DARK_GRAY);
        addColor(LIGHT_GRAY, Color.LIGHT_GRAY);
        addColor(LIGHTGREEN, new Color(220, 255, 220));
        addColor(LIGHTYELLOW, new Color(255, 255, 200));
        addColor(HO_GRAY1, new Color(230, 230, 230));
        addColor(FOREST_GREEN, new Color(34,139,34));

        addColor(URL_PANEL_BG, new Color(9, 9, 167));

        addColor(PANEL_BG, new Color(214,217,223));
        addColor(PANEL_BORDER, DARK_GRAY);
        addColor(PLAYER_POSITION_PANEL_BORDER, LIGHT_GRAY);
        addColor(BUTTON_BG, WHITE);
        addColor(BUTTON_ASSIST_CANCEL_BG,  new Color(226, 31, 31));
        addColor(BUTTON_ASSIST_OK_BG,  new Color(34, 225, 36));
        addColor(LABEL_ERROR_FG, Color.RED);
        addColor(LABEL_SUCCESS_FG, GREEN);
        addColor(LABEL_ONGREEN_FG, WHITE);
        addColor(LABEL_FG, BLACK);
        addColor(LIST_FG, BLACK);
        addColor(LIST_CURRENT_FG, new Color(0, 0, 150));
        addColor(TABLE_SELECTION_BG, new Color(235, 235, 235));
        addColor(TABLE_SELECTION_FG, LABEL_FG);
        addColor(LIST_SELECTION_BG, new Color(220, 220, 255));

        addColor(MATCHHIGHLIGHT_FAILED_FG, GRAY);

        // player
        addColor(PLAYER_SKILL_SPECIAL_BG, LIGHTGREEN);
        addColor(PLAYER_SKILL_BG, LIGHTYELLOW);
        addColor(TABLEENTRY_BG, WHITE);
        addColor(BACKGROUND_CONTAINER, Color.WHITE);
        addColor(TABLEENTRY_FG, BLACK);
        addColor(PLAYER_POS_BG, new Color(220, 220, 255));
        addColor(PLAYER_SUBPOS_BG, new Color(235, 235, 255));
        addColor(PLAYER_OLD_FG, GRAY);
        addColor(TABLEENTRY_IMPROVEMENT_FG, new Color(34, 139, 34));
        addColor(TABLEENTRY_DECLINE_FG, new Color(235, 0, 0));
        addColor(SKILLENTRY2_BG, GRAY);

        // league Table
        addColor(HOME_TEAM_FG, new Color(179,60,180));
        addColor(HOColorName.SELECTED_TEAM_FG, new Color(36,90,235));
        addColor(LEAGUE_TITLE_BG, HO_GRAY1);
        addColor(LEAGUE_PROMOTED_BG, LIGHTGREEN);
        addColor(LEAGUE_RELEGATION_BG, LIGHTYELLOW);
        addColor(LEAGUE_DEMOTED_BG, new Color(255, 220, 220));
        addColor(LEAGUE_BG, WHITE);
        addColor(LEAGUE_FG, BLACK);

        // league history panel
        addColor(LEAGUEHISTORY_LINE1_FG, Color.GREEN);
        addColor(LEAGUEHISTORY_LINE2_FG, Color.CYAN);
        addColor(LEAGUEHISTORY_LINE3_FG, Color.GRAY);
        addColor(LEAGUEHISTORY_LINE4_FG, BLACK);
        addColor(LEAGUEHISTORY_LINE5_FG, Color.ORANGE);
        addColor(LEAGUEHISTORY_LINE6_FG, Color.PINK);
        addColor(LEAGUEHISTORY_LINE7_FG, Color.RED);
        addColor(LEAGUEHISTORY_LINE8_FG, Color.MAGENTA);
        addColor(LEAGUEHISTORY_CROSS_FG, LIGHT_GRAY);
        addColor(LEAGUEHISTORY_GRID_FG, LIGHT_GRAY);

        // lineup
        addColor(SEL_OVERLAY_SELECTION_BG, new Color(10, 255, 10, 40));
        addColor(SEL_OVERLAY_BG, new Color(255, 10, 10, 40));
        addColor(LINEUP_POS_MIN_BORDER, LIGHT_GRAY);
        addColor(SUBST_CHANGED_VALUE_BG, LIGHTGREEN);
        addColor(SWAP_COLOR, FOREST_GREEN);
        addColor(RESET_COLOR, Color.RED);
        addColor(SWAP_COLOR_PRESSED, Color.RED);
        addColor(LINEUP_RATING_BORDER, Color.BLACK);
        addColor(LINEUP_PARTIAL_TRAINING, new Color(34, 255, 255));
        addColor(LINEUP_FULL_TRAINING, new Color(0, 0, 255));
        addColor(LINEUP_PLAYER_SELECTED, Color.LIGHT_GRAY);
        addColor(LINEUP_PLAYER_SUB, new Color(220, 220, 220));
        addColor(LINEUP_BG_BUTTONS, new Color(144, 238, 144));
        addColor(WARNING_ICON_CB_COLOR, Color.RED);
        addColor(HOColorName.RATING_BORDER_BELOW_LIMIT, new Color(255, 0, 0));
        addColor(HOColorName.RATING_BORDER_ABOVE_LIMIT, new Color(0, 0, 225));
        addColor(HOColorName.START_ASSISTANT, new Color(34, 139, 34));
        addColor(HOColorName.CLEAR_LINEUP, new Color(255, 0, 0));
        addColor(HOColorName.LINEUP_COLOR, new Color(0, 0, 139));
        addColor(HOColorName.LINEUP_HIGHLIGHT_FG, new Color(0, 0, 139));
        addColor(HOColorName.ORDERS_LINEUP, new Color(190, 190, 190));
        addColor(HOColorName.ORDERS_TICK, new Color(45, 75, 45));
        addColor(HOColorName.ORDERS_PEN, new Color(139, 0, 0));

        //matches module
        addColor(HOColorName.HOME_ACTION, new Color(110, 205, 234));
        addColor(HOColorName.GUEST_ACTION, new Color(209, 94, 94));
        addColor(HOColorName.NEUTRAL_ACTION, new Color(166, 166, 166));
        addColor(HOColorName.BORDER_RATING_BAR, Color.BLACK);


        // shirts
        addColor(SHIRT_KEEPER, BLACK);
        addColor(SHIRT_CENTRALDEFENCE, new Color(0, 0, 220));
        addColor(SHIRT_WINGBACK, new Color(0, 220, 0));
        addColor(SHIRT_MIDFIELD, new Color(220, 220, 0));
        addColor(SHIRT_WING, new Color(220, 140, 0));
        addColor(SHIRT_FORWARD, new Color(220, 0, 0));
        addColor(SHIRT_SUBKEEPER, new Color(200, 200, 200));
        addColor(SHIRT_SUBDEFENCE, new Color(200, 200, 255));
        addColor(SHIRT_SUBMIDFIELD, new Color(255, 255, 180));
        addColor(SHIRT_SUBWING, new Color(255, 225, 180));
        addColor(SHIRT_SUBFORWARD, new Color(255, 200, 200));
        addColor(SHIRT, HO_GRAY1);

        // Smileys
        addColor(SMILEYS_COLOR, BLACK);

        // Players specialty Colors
        addColor(PLAYER_SPECIALTY_COLOR, BLACK);
        addColor(PLAYER_SPECIALTY_NEGATIVE_COLOR, RED);


        addColor(PLAYER_STAR_COLOR, new Color(255,215,0));

        addColor(STAT_CASH, BLACK);
        addColor(STAT_WINLOST, Color.GRAY);
        addColor(STAT_INCOMESUM, Color.GREEN);
        addColor(STAT_COSTSUM, Color.RED);
        addColor(STAT_INCOMESPECTATORS, new Color(0, 180, 0));
        addColor(STAT_INCOMESPONSORS, new Color(0, 120, 60));
        addColor(STAT_INCOMEFINANCIAL, new Color(0, 60, 120));
        addColor(STAT_INCOMETEMPORARY, new Color(0, 0, 180));
        addColor(STAT_COSTARENA, new Color(180, 0, 0));
        addColor(STAT_COSTSPLAYERS, new Color(180, 36, 0));
        addColor(STAT_COSTFINANCIAL, new Color(180, 72, 0));
        addColor(STAT_COSTTEMPORARY, new Color(180, 108, 0));
        addColor(STAT_COSTSTAFF, new Color(180, 144, 0));
        addColor(STAT_COSTSYOUTH, new Color(180, 180, 0));
        addColor(STAT_FANS, Color.CYAN);
        addColor(STAT_MARKETVALUE, Color.BLUE);
        addColor(STAT_WAGE, new Color(150, 20, 20));
        addColor(STAT_RATING2, BLACK);
        addColor(STAT_TOTAL, Color.GRAY);
        addColor(STAT_MOOD, Color.PINK);
        addColor(STAT_CONFIDENCE, Color.CYAN);
        addColor(STAT_HATSTATS, Color.YELLOW);
        addColor(STAT_LODDAR, new Color(150, 20, 20));
        addColor(STAT_PANEL_BG, Color.WHITE);
        addColor(STAT_PANEL_FG, Color.DARK_GRAY);

        // matchtypes
        addColor(MATCHTYPE_BG, WHITE);
        addColor(MATCHTYPE_LEAGUE_BG, LIGHTYELLOW);
        addColor(MATCHTYPE_QUALIFIKATION_BG, new Color(255, 200, 200));
        addColor(MATCHTYPE_CUP_BG, new Color(200, 255, 200));
        addColor(MATCHTYPE_FRIENDLY_BG, Color.WHITE);
        addColor(MATCHTYPE_INT_BG, LIGHT_GRAY);
        addColor(MATCHTYPE_MASTERS_BG, new Color(255, 215, 120));
        addColor(MATCHTYPE_INTFRIENDLY_BG, WHITE);
        addColor(MATCHTYPE_NATIONAL_BG, new Color(240, 220, 255));
        addColor(MATCHTYPE_TOURNAMENT_GROUP_BG, new Color(218, 237, 247));
        addColor(MATCHTYPE_TOURNAMENT_FINALS_BG, new Color(218, 237, 247));
        addColor(MATCHTYPE_DIVISIONBATTLE_BG, new Color(200, 210, 247));

        // 1.431
        addColor(MATCHDETAILS_PROGRESSBAR_GREEN, new Color(0, 124, 0));
        addColor(MATCHDETAILS_PROGRESSBAR_RED, new Color(124, 0, 0));

        // Transfer module
        addColor(TRANSFER_IN_COLOR, GREEN);
        addColor(TRANSFER_OUT_COLOR, RED);

        // Team Analyzer
        addColor(TA_TEAM_LEAGUE_NEXT, Color.RED);
        addColor(TA_TEAM_CUP_NEXT, Color.GREEN);
        addColor(TA_TEAM_TOURNAMENT_NEXT, new Color(0, 51, 255)); // Darkish blue
        addColor(TA_TEAM_TOURNAMENT, new Color(0, 179, 255)); // Lightish blue

        // Colours for form streak in league details
        addColor(FORM_STREAK_WIN, new Color(73, 146, 45));
        addColor(FORM_STREAK_DRAW, new Color(111, 111, 111));
        addColor(FORM_STREAK_DEFEAT, new Color(224, 51, 51));
        addColor(FORM_STREAK_UNKNOWN, new Color(170, 170, 170));

        // Colours for alternating rows in table
        addColor(TABLE_LEAGUE_EVEN, Color.WHITE);
        addColor(TABLE_LEAGUE_ODD, new Color(240, 240, 240));

        // Training
        addColor(TRAINING_BIRTHDAY_BG, new Color(255, 240, 175));
        addColor(TRAINING_FULL_BG,LIGHTGREEN);
        addColor(TRAINING_PARTIAL_BG,LIGHTYELLOW);
        addColor(TRAINING_OSMOSIS_BG, Color.LIGHT_GRAY);
        addColor(HOColorName.TRAINING_ICON_COLOR_1, new Color(0, 0, 0));
        addColor(HOColorName.TRAINING_ICON_COLOR_2, new Color(255, 255, 255));

        // Training bars
        addColor(FULL_TRAINING_DONE, new Color(37, 110, 9));
        addColor(PARTIAL_TRAINING_DONE, new Color(73, 208, 21));
        addColor(FULL_STAMINA_DONE, new Color(69, 127, 217));
        addColor(FULL_TRAINING_PLANNED, new Color(56, 56, 56));
        addColor(PARTIAL_TRAINING_PLANNED, new Color(184, 184, 184, 184));
        addColor(STAMINA_PLANNED, new Color(104, 242, 255));

        // TS Forecast
        addColor(TSFORECAST_ALT_COLOR, Color.BLUE);

        // HRF Explorer
        addColor(HOColorName.HRF_GREEN_BG, new Color(220,255,220));
        addColor(HOColorName.HRF_LIGHTBLUE_BG, new Color(235,235,255));
        addColor(HOColorName.HRF_DARKBLUE_BG, new Color(220,220,255));
        addColor(HOColorName.HRF_RED_BG, new Color(255,200,200));

        // Player State colours
        addColor(HOColorName.FG_STANDARD, Color.BLACK);
        addColor(HOColorName.FG_TRANSFERLISTED, new Color(0, 180, 0));
        addColor(HOColorName.FG_BRUISED, new Color(100, 0, 0));
        addColor(HOColorName.FG_INJURED, new Color(200, 0, 0));
        addColor(HOColorName.FG_TWO_YELLOW_CARDS, new Color(100, 100, 0));
        addColor(HOColorName.FG_RED_CARD, new Color(200, 20, 20));
        addColor(HOColorName.INJURY, new Color(255, 0, 0));
        addColor(HOColorName.PLASTER, new Color(247, 195, 176));

        //players details
        addColor(HOColorName.PLAYER_DETAILS_BAR_BORDER_COLOR, Color.BLACK);
        addColor(HOColorName.PLAYER_DETAILS_BAR_FILL_GREEN, Color.GREEN);
        addColor(HOColorName.PLAYER_DETAILS_STARS_FILL, Color.YELLOW);

        // Promotion
        addColor(HOColorName.FG_PROMOTION_INFO, new Color(238, 39, 39, 255));

        // palettes
        addColor(PALETTE13_0,  new Color(255, 204, 0));
        addColor(PALETTE13_1,  new Color(23, 111, 36));
        addColor(PALETTE13_2,  new Color(255, 59, 48));
        addColor(PALETTE13_3,  new Color(49, 220, 209));
        addColor(PALETTE13_4,  new Color(162, 132, 94));
        addColor(PALETTE13_5,  new Color(147, 250, 45));
        addColor(PALETTE13_6,  new Color(0, 122, 255));
        addColor(PALETTE13_7,  new Color(255, 149, 0));
        addColor(PALETTE13_8,  new Color(88, 86, 214));
        addColor(PALETTE13_9,  new Color(142, 142, 147));
        addColor(PALETTE13_10,  new Color(175, 82, 222));
        addColor(PALETTE13_11,  new Color(40, 205, 65));
        addColor(PALETTE13_12,  new Color(90, 200, 250));

        // League Details
        addColor(HOColorName.SHOW_MATCH, new Color(23, 111, 36));
        addColor(HOColorName.DOWNLOAD_MATCH, new Color(238, 39, 39, 255));

        addColor(HOColorName.LINK_LABEL_FG, new Color(6, 69, 173));

    }

    public static Color getColor(HOColorName name, String theme){
        return getColor(name, theme, new ArrayList<>());
    }
    private static Color getColor(HOColorName name, String theme, List<String> colorNames) {
        if (!colorNames.contains(name.name())) { // break endless recursion
            var colorMap = colors.get(name);
            if (colorMap != null) {
                var hoColor = colorMap.get(theme);
                if (hoColor == null) {
                    hoColor = colorMap.get("default");
                }
                if (hoColor != null) {
                    var ref = hoColor.colorReference;
                    if (ref != null) {
                        colorNames.add(hoColor.getName());
                        return getColor(ref, theme, colorNames);
                    } else {
                        return hoColor.getColor();
                    }
                }
            }
        }
        return null;
    }

    private static void addColor(HOColorName name, Color color) {
        addColor(new HOColor(name, color));
    }
    private static void addColor(HOColorName name, HOColorName colorReference) {
        addColor(new HOColor(name, colorReference));
    }

    public static void addColor(HOColor color){
        var colorMap = colors.computeIfAbsent(color.name, k -> new HashMap<>());
        colorMap.put(color.theme, color);
    }

    /**
     * Unique color name
     */
    private HOColorName name;

    /**
     * Theme name
     */
    private String theme;

    /**
     * Color refers to another color.
     */
    private HOColorName colorReference;

    /**
     * The color value
     * If null, this color refers to another color
     */
    private Color color = new Color(0);

    /**
     * Default value (used to reset user defined colors)
     */
    private HOColor defaultValue;

    HOColor(){
        super();
    }

    public HOColor(HOColorName name, HOColorName o) {
        this(name, "default",  o);
    }

    private HOColor(HOColorName name, Color o) {
        this(name, "default",  o);
    }
    public HOColor(HOColorName name, String theme, Color o) {
        this.name = name;
        this.theme = theme;
        this.color = o;
    }
    public HOColor(HOColorName name, String theme, HOColorName o) {
        this.name = name;
        this.theme = theme;
        this.colorReference = o;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getName() {
        return name.name();
    }

    public void setName(String name) {
        this.name = HOColorName.valueOf(name);
    }

    public String getColorReference() {
        return colorReference.name();
    }

    public void setColorReference(String colorReference) {
        this.colorReference = HOColorName.valueOf(colorReference);
    }

    public Integer getValue() {
        if (color != null) {
            return color.getRGB();
        }
        return null;
    }

    public void setValue(Integer v) {
        if (v != null) {
            this.color = new Color(v, true);
        } else {
            this.color = null;
        }
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String v) {
        this.theme = v;
    }

    public void setDefaultValue(Object o) {
        if ( o instanceof String){
            this.defaultValue = new HOColor(this.name, HOColorName.valueOf((String)o));
        }
        else if (o instanceof Color){
            this.defaultValue = new HOColor(this.name, (Color)o);
        }
    }
}
