package module.statistics;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;

import java.awt.*;

import static core.gui.theme.HOColorName.*;

// reference to HOColorName.PALETTE15
public final class Colors {

    public static final HOColorName COLOR_PLAYER_FORM = PALETTE13_0;
    public static final HOColorName COLOR_PLAYER_STAMINA = PALETTE13_1;
    public static final HOColorName COLOR_PLAYER_LOYALTY = PALETTE13_2;
    public static final HOColorName COLOR_PLAYER_GK = PALETTE13_3;
    public static final HOColorName COLOR_PLAYER_DE = PALETTE13_4;
    public static final HOColorName COLOR_PLAYER_PM = PALETTE13_5;
    public static final HOColorName COLOR_PLAYER_PS = PALETTE13_6;
    public static final HOColorName COLOR_PLAYER_WI = PALETTE13_7;
    public static final HOColorName COLOR_PLAYER_SC = PALETTE13_8;
    public static final HOColorName COLOR_PLAYER_SP = PALETTE13_9;
    public static final HOColorName COLOR_PLAYER_LEADERSHIP = PALETTE13_10;
    public static final HOColorName COLOR_PLAYER_XP = PALETTE13_11;
    public static final HOColorName COLOR_PLAYER_RATING = PALETTE13_12;
    public static final HOColorName COLOR_PLAYER_TSI = PALETTE13_0;
    public static final HOColorName COLOR_PLAYER_WAGE = PALETTE13_1;

    public static final HOColorName COLOR_TEAM_RATING = PALETTE13_0;
    public static final HOColorName COLOR_TEAM_HATSTATS = PALETTE13_1;
    public static final HOColorName COLOR_TEAM_LODDAR = PALETTE13_2;
    public static final HOColorName COLOR_TEAM_TOTAL_STRENGTH = PALETTE13_3;
    public static final HOColorName COLOR_TEAM_MID = PALETTE13_4;
    public static final HOColorName COLOR_TEAM_RD = PALETTE13_5;
    public static final HOColorName COLOR_TEAM_CD = PALETTE13_6;
    public static final HOColorName COLOR_TEAM_LD = PALETTE13_7;
    public static final HOColorName COLOR_TEAM_RA = PALETTE13_8;
    public static final HOColorName COLOR_TEAM_CA = PALETTE13_9;
    public static final HOColorName COLOR_TEAM_LA = PALETTE13_10;
    public static final HOColorName COLOR_TEAM_TS = PALETTE13_11;
    public static final HOColorName COLOR_TEAM_CONFIDENCE = PALETTE13_12;

    public static final HOColorName COLOR_FINANCE_CASH = PALETTE13_0;
    public static final HOColorName COLOR_FINANCE_INCOME_SPONSORS =PALETTE13_1;
    public static final HOColorName COLOR_FINANCE_COST_PLAYERS = PALETTE13_2;

    public static final HOColorName COLOR_CLUB_ASSISTANT_TRAINERS_LEVEL = PALETTE13_0;
    public static final HOColorName COLOR_CLUB_FINANCIAL_DIRECTORS_LEVEL = PALETTE13_1;
    public static final HOColorName COLOR_CLUB_FORM_COACHS_LEVEL= PALETTE13_2;
    public static final HOColorName COLOR_CLUB_DOCTORS_LEVEL = PALETTE13_3;
    public static final HOColorName COLOR_CLUB_SPOKE_PERSONS_LEVEL = PALETTE13_4;
    public static final HOColorName COLOR_CLUB_SPORT_PSYCHOLOGIST_LEVELS = PALETTE13_5;
    public static final HOColorName COLOR_CLUB_TACTICAL_ASSISTANT_LEVELS = PALETTE13_6;
    public static final HOColorName COLOR_CLUB_FAN_CLUB_SIZE = PALETTE13_7;
    public static final HOColorName COLOR_CLUB_GLOBAL_RANKING = PALETTE13_8;
    public static final HOColorName COLOR_CLUB_LEAGUE_RANKING = PALETTE13_9;
    public static final HOColorName COLOR_CLUB_POWER_RATING = PALETTE13_10;

    public static Color getColor(HOColorName i) {return ThemeManager.getColor(i);}

}
