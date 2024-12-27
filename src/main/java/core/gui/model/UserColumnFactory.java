package core.gui.model;

import core.constants.player.PlayerAbility;
import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.gui.comp.entry.*;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.enums.MatchType;
import core.model.match.MatchKurzInfo;
import core.model.match.Matchdetails;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.model.player.PlayerCategory;
import core.util.HODateTime;
import core.util.Helper;
import core.util.StringUtils;
import module.playerOverview.PlayerStatusLabelEntry;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;

import static core.model.player.IMatchRoleID.aPositionBehaviours;
import static core.model.player.MatchRoleID.isFieldMatchRoleId;

/**
 * User column factory creates {@link core.gui.comp.table.UserColumn} instances used in the
 * different {@link core.gui.comp.table.HOTableModel} table models.  Each column has a name,
 * an ID, and may have a preferred width.
 */
final public class UserColumnFactory {

    public static final int NAME = 1;
    public static final int BEST_POSITION = 40;
    public static final int SCHUM_RANK_BENCHMARK = 898;
    public static final int LINEUP = 50;
    public static final int GROUP = 60;
    public static final int ID = 440;
    public static final int DATUM = 450;
    public static final int RATING = 435;
    public static final int DURATION = 890;
    public static final int AUTO_LINEUP = 510;
    public static final int LAST_MATCH_RATING = 461;


    public static PlayerColumn2[] createPlayerCBItemArray() {
        final PlayerColumn2[] playerColumn2Array = new PlayerColumn2[5];
        playerColumn2Array[0] = new PlayerColumn2(590, "ls.team.teamspirit") {
            @Override
            public IHOTableEntry getTableEntry(PlayerMatchCBItem spielerCBItem) {
                return new ColorLabelEntry(spielerCBItem.getStimmung(),
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            }
        };

        playerColumn2Array[1] = new PlayerColumn2(600, "ls.team.confidence") {
            @Override
            public IHOTableEntry getTableEntry(PlayerMatchCBItem spielerCBItem) {
                return new ColorLabelEntry(spielerCBItem.getSelbstvertrauen(),
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            }
        };

        playerColumn2Array[2] = new PlayerColumn2(601, "Position") {
            @Override
            public IHOTableEntry getTableEntry(PlayerMatchCBItem spielerCBItem) {
                ColorLabelEntry colorLabelEntry = new ColorLabelEntry(ImageUtilities
                        .getJerseyIcon(
                                MatchRoleID.getHTPosidForHOPosition4Image((byte) spielerCBItem.getPosition()),
                                (byte) 0,
                                0
                        ),
                        -MatchRoleID.getSortId((byte) spielerCBItem.getPosition(), false),
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                var position = spielerCBItem.getPosition();
                if (aPositionBehaviours.contains(position)) {
                    var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
                    var r = ratingPredictionModel.getPlayerMatchAverageRating(spielerCBItem.getSpieler(), (byte) position);
                    colorLabelEntry.setText(MatchRoleID.getNameForPosition((byte) position)
                            + String.format("(%.2f)", r));
                }
                return colorLabelEntry;
            }
        };

        playerColumn2Array[3] = new PlayerColumn2(RATING, "Rating") {
            @Override
            public IHOTableEntry getTableEntry(PlayerMatchCBItem spielerCBItem) {
                return new RatingTableEntry(spielerCBItem.getRating(), false);
            }
        };

        playerColumn2Array[4] = new PlayerColumn2(602, "ls.player.age") {
            @Override
            public IHOTableEntry getTableEntry(PlayerMatchCBItem spielerCBItem) {
                Player player = spielerCBItem.getSpieler();
                var matchDate = spielerCBItem.getMatchdetails().getMatchDate();

                if (matchDate != null) {
                    String ageString = player.getAgeWithDaysAsString(matchDate);
                    return new ColorLabelEntry(player.getDoubleAgeFromDate(matchDate),
                            ageString,
                            ColorLabelEntry.FG_STANDARD,
                            ColorLabelEntry.BG_STANDARD,
                            SwingConstants.LEFT);
                } else {
                    return new ColorLabelEntry("",
                            ColorLabelEntry.FG_STANDARD,
                            ColorLabelEntry.BG_STANDARD,
                            SwingConstants.LEFT);
                }
            }
        };

        return playerColumn2Array;
    }

    /**
     * @return MatchDetailsColumn[]
     */
    public static MatchDetailsColumn[] createMatchDetailsColumnsArray() {
        final MatchDetailsColumn[] matchDetailsColumnsArray = new MatchDetailsColumn[4];
        matchDetailsColumnsArray[0] = new MatchDetailsColumn(550, "ls.match.weather", 30) {
            @Override
            public IHOTableEntry getTableEntry(Matchdetails matchdetails) {
                return new ColorLabelEntry(ThemeManager.getIcon(HOIconName.WEATHER[matchdetails.getWetterId()]),
                        matchdetails.getWetterId(),
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            }
        };// Wetter
        matchDetailsColumnsArray[1] = new MatchDetailsColumn(560, "ls.team.teamattitude") {
            @Override
            public IHOTableEntry getTableEntry(Matchdetails matchdetails) {
                final int teamid = HOVerwaltung.instance().getModel()
                        .getBasics().getTeamId();
                int einstellung = (matchdetails.getHomeTeamId() == teamid) ? matchdetails.getHomeEinstellung() : matchdetails.getGuestEinstellung();
                return new ColorLabelEntry(Matchdetails.getNameForEinstellung(einstellung), ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            }
        };
        matchDetailsColumnsArray[2] = new MatchDetailsColumn(570, "ls.team.tactic") {
            @Override
            public IHOTableEntry getTableEntry(Matchdetails matchdetails) {
                final int teamid = HOVerwaltung.instance().getModel()
                        .getBasics().getTeamId();
                int tactic = (matchdetails.getHomeTeamId() == teamid) ? matchdetails.getHomeTacticType() : matchdetails.getGuestTacticType();
                return new ColorLabelEntry(Matchdetails.getNameForTaktik(tactic), ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            }
        };
        matchDetailsColumnsArray[3] = new MatchDetailsColumn(580, "ls.team.tacticalskill") {
            @Override
            public IHOTableEntry getTableEntry(Matchdetails matchdetails) {
                final int teamid = HOVerwaltung.instance().getModel()
                        .getBasics().getTeamId();
                int tacticSkill = (matchdetails.getHomeTeamId() == teamid) ? matchdetails.getHomeTacticSkill() : matchdetails.getGuestTacticSkill();
                return new ColorLabelEntry(PlayerAbility.getNameForSkill(tacticSkill), ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            }
        };

        return matchDetailsColumnsArray;
    }

    /**
     * @return PlayerColumn[]
     */
    public static PlayerColumn[] createGoalsColumnsArray() {
        final PlayerColumn[] playerGoalsArray = new PlayerColumn[5];
        playerGoalsArray[0] = new PlayerColumn(380, "TG", "ls.player.career_goals", 20) {
            @Override
            public int getValue(Player player) {
                return player.getTotalGoals();
            }
        };

        playerGoalsArray[1] = new PlayerColumn(420, "TG", "ls.player.team_goals", 20) {
            @Override
            public int getValue(Player player) {
                return player.getCurrentTeamGoals();
            }
        };

        playerGoalsArray[2] = new PlayerColumn(390, "HT", "ls.player.hattricks", 20) {
            @Override
            public int getValue(Player player) {
                return player.getHatTricks();
            }
        };

        playerGoalsArray[3] = new PlayerColumn(400, "TL", "ls.player.season_series_goals", 20) {
            @Override
            public int getValue(Player player) {
                return player.getLeagueGoals();
            }
        };

        playerGoalsArray[4] = new PlayerColumn(410, "TP", "ls.player.season_cup_goals", 20) {
            @Override
            public int getValue(Player player) {
                return player.getCupGameGoals();
            }
        };
        return playerGoalsArray;
    }

    /**
     * @return PlayerSkillColumn []
     */
    public static PlayerSkillColumn[] createPlayerSkillArray() {
        final PlayerSkillColumn[] playerSkillArray = new PlayerSkillColumn[12];
        playerSkillArray[0] = new PlayerSkillColumn(100, "ls.player.short_form", "ls.player.form", PlayerSkill.FORM);
        playerSkillArray[1] = new PlayerSkillColumn(110, "ls.player.skill_short.stamina", "ls.player.skill.stamina", PlayerSkill.STAMINA);
        playerSkillArray[2] = new PlayerSkillColumn(115, "ls.player.short_loyalty", "ls.player.loyalty", PlayerSkill.LOYALTY);
        playerSkillArray[3] = new PlayerSkillColumn(120, "ls.player.skill_short.keeper", "ls.player.skill.keeper", PlayerSkill.KEEPER);
        playerSkillArray[4] = new PlayerSkillColumn(130, "ls.player.skill_short.defending", "ls.player.skill.defending", PlayerSkill.DEFENDING);
        playerSkillArray[5] = new PlayerSkillColumn(140, "ls.player.skill_short.playmaking", "ls.player.skill.playmaking", PlayerSkill.PLAYMAKING);
        playerSkillArray[6] = new PlayerSkillColumn(150, "ls.player.skill_short.passing", "ls.player.skill.passing", PlayerSkill.PASSING);
        playerSkillArray[7] = new PlayerSkillColumn(160, "ls.player.skill_short.winger", "ls.player.skill.winger", PlayerSkill.WINGER);
        playerSkillArray[8] = new PlayerSkillColumn(170, "ls.player.skill_short.scoring", "ls.player.skill.scoring", PlayerSkill.SCORING);
        playerSkillArray[9] = new PlayerSkillColumn(180, "ls.player.skill_short.setpieces", "ls.player.skill.setpieces", PlayerSkill.SETPIECES);
        playerSkillArray[10] = new PlayerSkillColumn(80, "ls.player.short_leadership", "ls.player.leadership", PlayerSkill.LEADERSHIP);
        playerSkillArray[11] = new PlayerSkillColumn(90, "ls.player.short_experience", "ls.player.experience", PlayerSkill.EXPERIENCE);

        return playerSkillArray;
    }

    /**
     * @return PlayerColumn []
     */
    public static PlayerColumn[] createPlayerBasicArray() {
        final PlayerColumn[] playerBasicArray = new PlayerColumn[2];
        playerBasicArray[0] = new PlayerColumn(NAME, "ls.player.name", 160) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                var team = HOVerwaltung.instance().getModel().getCurrentLineup();
                var pos = team.getPositionById(player.getPlayerId());
                return new PlayerLabelEntry(player, pos, 0f, false, false);
            }

            @Override
            public boolean canBeDisabled() {
                return false;
            }
        };

        playerBasicArray[1] = new PlayerColumn(ID, "ls.player.id", 0) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                return new ColorLabelEntry(player.getPlayerId(),
                        String.valueOf(player.getPlayerId()),
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            }

            @Override
            public boolean canBeDisabled() {
                return false;
            }

            @Override
            public void setSize(TableColumn column) {
                // Column ID is not shown!
                // isDisplay needs to be true in order to get players' ids in hidden column in the table model
                column.setMinWidth(0);
                column.setPreferredWidth(0);
            }
        };
        return playerBasicArray;
    }

    /**
     * @return PlayerPositionColumn[]
     */
    public static PlayerPositionColumn[] createPlayerPositionArray() {
        final PlayerPositionColumn[] playerPositionArray = new PlayerPositionColumn[19];
        playerPositionArray[0] = new PlayerPositionColumn(190, "ls.player.position_short.keeper", "ls.player.position.keeper", IMatchRoleID.KEEPER);
        playerPositionArray[1] = new PlayerPositionColumn(200, "ls.player.position_short.centraldefender", "ls.player.position.centraldefender", IMatchRoleID.CENTRAL_DEFENDER);
        playerPositionArray[2] = new PlayerPositionColumn(210, "ls.player.position_short.centraldefendertowardswing", "ls.player.position.centraldefendertowardswing", IMatchRoleID.CENTRAL_DEFENDER_TOWING);
        playerPositionArray[3] = new PlayerPositionColumn(220, "ls.player.position_short.centraldefenderoffensive", "ls.player.position.centraldefenderoffensive", IMatchRoleID.CENTRAL_DEFENDER_OFF);
        playerPositionArray[4] = new PlayerPositionColumn(230, "ls.player.position_short.wingback", "ls.player.position.wingback", IMatchRoleID.BACK);
        playerPositionArray[5] = new PlayerPositionColumn(240, "ls.player.position_short.wingbacktowardsmiddle", "ls.player.position.wingbacktowardsmiddle", IMatchRoleID.BACK_TOMID);
        playerPositionArray[6] = new PlayerPositionColumn(250, "ls.player.position_short.wingbackoffensive", "ls.player.position.wingbackoffensive", IMatchRoleID.BACK_OFF);
        playerPositionArray[7] = new PlayerPositionColumn(260, "ls.player.position_short.wingbackdefensive", "ls.player.position.wingbackdefensive", IMatchRoleID.BACK_DEF);
        playerPositionArray[8] = new PlayerPositionColumn(270, "ls.player.position_short.innermidfielder", "ls.player.position.innermidfielder", IMatchRoleID.MIDFIELDER);
        playerPositionArray[9] = new PlayerPositionColumn(280, "ls.player.position_short.innermidfieldertowardswing", "ls.player.position.innermidfieldertowardswing", IMatchRoleID.MIDFIELDER_TOWING);
        playerPositionArray[10] = new PlayerPositionColumn(290, "ls.player.position_short.innermidfielderoffensive", "ls.player.position.innermidfielderoffensive", IMatchRoleID.MIDFIELDER_OFF);
        playerPositionArray[11] = new PlayerPositionColumn(300, "ls.player.position_short.innermidfielderdefensive", "ls.player.position.innermidfielderdefensive", IMatchRoleID.MIDFIELDER_DEF);
        playerPositionArray[12] = new PlayerPositionColumn(310, "ls.player.position_short.winger", "ls.player.position.winger", IMatchRoleID.WINGER);
        playerPositionArray[13] = new PlayerPositionColumn(320, "ls.player.position_short.wingertowardsmiddle", "ls.player.position.wingertowardsmiddle", IMatchRoleID.WINGER_TOMID);
        playerPositionArray[14] = new PlayerPositionColumn(330, "ls.player.position_short.wingeroffensive", "ls.player.position.wingeroffensive", IMatchRoleID.WINGER_OFF);
        playerPositionArray[15] = new PlayerPositionColumn(340, "ls.player.position_short.wingerdefensive", "ls.player.position.wingerdefensive", IMatchRoleID.WINGER_DEF);
        playerPositionArray[16] = new PlayerPositionColumn(350, "ls.player.position_short.forward", "ls.player.position.forward", IMatchRoleID.FORWARD);
        playerPositionArray[17] = new PlayerPositionColumn(360, "ls.player.position_short.forwardtowardswing", "ls.player.position.forwardtowardswing", IMatchRoleID.FORWARD_TOWING);
        playerPositionArray[18] = new PlayerPositionColumn(370, "ls.player.position_short.forwarddefensive", "ls.player.position.forwarddefensive", IMatchRoleID.FORWARD_DEF);
        return playerPositionArray;
    }


    /**
     * @return matches table for Matches Module
     */
    public static MatchKurzInfoColumn[] createMatchesArray() {
        final MatchKurzInfoColumn[] matchesArray = new MatchKurzInfoColumn[8];
        matchesArray[0] = new MatchKurzInfoColumn(450, "Datum", 70) {
            @Override
            public IHOTableEntry getTableEntry(MatchKurzInfo match) {
                return new ColorLabelEntry(match.getMatchSchedule().instant.getEpochSecond(),
                        match.getMatchSchedule().toLocaleDateTime(),
                        ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD,
                        SwingConstants.LEFT);
            }

            @Override
            public IHOTableEntry getTableEntry(PlayerMatchCBItem spielerCBItem) {
                return new ColorLabelEntry(spielerCBItem.getMatchdate().instant.getEpochSecond(),
                        HODateTime.toLocaleDateTime(spielerCBItem.getMatchdate()),
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
            }
        };

        matchesArray[1] = new MatchKurzInfoColumn(460, "Spielart", 20) {
            @Override
            public IHOTableEntry getTableEntry(MatchKurzInfo match) {
                return new ColorLabelEntry(ThemeManager.getIcon(HOIconName.MATCHICONS[match.getMatchTypeExtended().getIconArrayIndex()]),
                        match.getMatchType().getId(), ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
            }

            @Override
            public IHOTableEntry getTableEntry(PlayerMatchCBItem spielerCBItem) {
                return new ColorLabelEntry(ThemeManager.getIcon(HOIconName.MATCHICONS[spielerCBItem.getMatchType().getIconArrayIndex()]),
                        spielerCBItem.getMatchType().getMatchTypeId(),
                        ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD,
                        SwingConstants.CENTER);
            }
        };

        matchesArray[2] = new MatchKurzInfoColumn(470, "Heim", 60) {
            @Override
            public IHOTableEntry getTableEntry(MatchKurzInfo match) {
                ColorLabelEntry entry = new ColorLabelEntry(match.getHomeTeamName(), ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                entry.setFGColor((match.getHomeTeamID() == HOVerwaltung.instance().getModel().getBasics()
                        .getTeamId()) ? ThemeManager.getColor(HOColorName.HOME_TEAM_FG) : ThemeManager.getColor(HOColorName.LABEL_FG));

                if ((match.getMatchStatus() == MatchKurzInfo.FINISHED) && (match.getHomeTeamGoals() > match.getGuestTeamGoals())) {
                    entry.setFont(entry.getFont().deriveFont(Font.BOLD));
                } else {
                    entry.setFont(entry.getFont().deriveFont(Font.PLAIN));
                }
                return entry;
            }

            @Override
            public IHOTableEntry getTableEntry(PlayerMatchCBItem spielerCBItem) {
                ColorLabelEntry entry = new ColorLabelEntry(spielerCBItem.getHomeTeamName(),
                        ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD,
                        SwingConstants.LEFT);
                entry.setFGColor((spielerCBItem.getHeimID() == HOVerwaltung.instance().getModel().getBasics()
                        .getTeamId()) ? ThemeManager.getColor(HOColorName.HOME_TEAM_FG) : ThemeManager.getColor(HOColorName.LABEL_FG));
                return entry;
            }

            @Override
            public void setSize(TableColumn column) {
                column.setMinWidth(60);
                column.setPreferredWidth((preferredWidth == 0) ? 160 : preferredWidth);
            }
        };

        matchesArray[3] = new MatchKurzInfoColumn(480, "Gast", 60) {
            @Override
            public IHOTableEntry getTableEntry(MatchKurzInfo match) {
                ColorLabelEntry entry = new ColorLabelEntry(match.getGuestTeamName(), ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                entry.setFGColor((match.getGuestTeamID() == HOVerwaltung.instance().getModel().getBasics()
                        .getTeamId()) ? ThemeManager.getColor(HOColorName.HOME_TEAM_FG) : ThemeManager.getColor(HOColorName.LABEL_FG));

                if ((match.getMatchStatus() == MatchKurzInfo.FINISHED) && (match.getHomeTeamGoals() < match.getGuestTeamGoals())) {
                    entry.setFont(entry.getFont().deriveFont(Font.BOLD));
                } else {
                    entry.setFont(entry.getFont().deriveFont(Font.PLAIN));
                }

                return entry;
            }

            @Override
            public IHOTableEntry getTableEntry(PlayerMatchCBItem spielerCBItem) {
                ColorLabelEntry entry = new ColorLabelEntry(spielerCBItem.getGuestTeamName(),
                        ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD,
                        SwingConstants.LEFT);
                entry.setFGColor((spielerCBItem.getGastID() == HOVerwaltung.instance().getModel().getBasics()
                        .getTeamId()) ? ThemeManager.getColor(HOColorName.HOME_TEAM_FG) : ThemeManager.getColor(HOColorName.LABEL_FG));
                return entry;
            }

            @Override
            public void setSize(TableColumn column) {
                column.setMinWidth(60);
                column.setPreferredWidth((preferredWidth == 0) ? 160 : preferredWidth);
            }
        };

        matchesArray[4] = new MatchKurzInfoColumn(490, "ls.match.result", 45) {
            @Override
            public IHOTableEntry getTableEntry(MatchKurzInfo match) {
                return new ColorLabelEntry(match.getResultLong(),
                        ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD,
                        SwingConstants.LEFT);
            }

            @Override
            public IHOTableEntry getTableEntry(PlayerMatchCBItem spielerCBItem) {
                return new ColorLabelEntry(spielerCBItem.getMatchdetails().getResultEx(),
                        ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD,
                        SwingConstants.CENTER);
            }

        };

        matchesArray[5] = new MatchKurzInfoColumn(494, "ls.match.hatstats.me", 80) {
            @Override
            public IHOTableEntry getTableEntry(MatchKurzInfo match, Matchdetails matchDetails) {
                int hatstats = 0;
                if (matchDetails!=null){
                    hatstats = match.isHomeMatch() ? matchDetails.getHomeHatStats() : matchDetails.getAwayHatStats();
                }
                return new ColorLabelEntry(hatstats, String.valueOf(hatstats),
                        ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD,
                        SwingConstants.CENTER);
            }

            @Override
            public IHOTableEntry getTableEntry(PlayerMatchCBItem spielerCBItem) {
                return new ColorLabelEntry("not implemented 123456789",
                        ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD,
                        SwingConstants.CENTER);
            }

        };

        matchesArray[6] = new MatchKurzInfoColumn(498, "ls.match.hatstats.opp", 80) {
            @Override
            public IHOTableEntry getTableEntry(MatchKurzInfo match, Matchdetails matchDetails) {
                int hatstats = 0;
                if (matchDetails != null) {
                    hatstats = match.isHomeMatch() ? matchDetails.getAwayHatStats() : matchDetails.getHomeHatStats();
                }
                return new ColorLabelEntry(hatstats, String.valueOf(hatstats),
                        ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD,
                        SwingConstants.CENTER);
            }

            @Override
            public IHOTableEntry getTableEntry(PlayerMatchCBItem spielerCBItem) {
                return new ColorLabelEntry("not implemented 123456789",
                        ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD,
                        SwingConstants.CENTER);
            }

        };

        matchesArray[7] = new MatchKurzInfoColumn(500, "ls.match.id", 55) {

            @Override
            public IHOTableEntry getTableEntry(MatchKurzInfo match) {
                return new ColorLabelEntry(match.getMatchID(), String.valueOf(match.getMatchID()),
                        ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD,
                        SwingConstants.RIGHT);
            }

            @Override
            public IHOTableEntry getTableEntry(PlayerMatchCBItem spielerCBItem) {
                return new ColorLabelEntry(String.valueOf(spielerCBItem.getMatchID()),
                        ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD,
                        SwingConstants.CENTER);
            }
        };

        return matchesArray;
    }

    /**
     * creates an array of various player columns
     *
     * @return PlayerColumn[]
     */
    public static PlayerColumn[] createPlayerAdditionalArray() {
        final PlayerColumn[] playerAdditionalArray = new PlayerColumn[30];

        playerAdditionalArray[0] = new PlayerColumn(10, "ls.player.shirtnumber.short", "ls.player.shirtnumber", 25) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                int sort = player.getShirtNumber();
                if (sort <= 0) {
                    // Temporary players don't have a shirt number
                    sort = 10000;
                }
                String shirtNumberText = String.valueOf(sort);
                // If the player does not have a shirt number then display an empty string
                if (sort >= 100) {
                    shirtNumberText = "";
                }

                return new ColorLabelEntry(sort, shirtNumberText, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD,
                        SwingConstants.CENTER);
            }
        };

        playerAdditionalArray[1] = new PlayerColumn(20, " ", "ls.player.nationality", 25) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                return new ColorLabelEntry(ImageUtilities.getCountryFlagIcon(player.getNationalityId()),
                        player.getNationalityId(),
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
            }
        };

        playerAdditionalArray[2] = new PlayerColumn(30, "ls.player.age", 55) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                String ageString = player.getAgeWithDaysAsString();
                int birthdays;
                boolean playerExists;

                if (playerCompare == null) {
                    // Birthdays since last HRF
                    birthdays = (int) (Math.floor(player.getAlterWithAgeDays()) - player.getAge());
                    playerExists = false;
                } else {
                    // Birthdays since compare
                    birthdays = (int) (Math.floor(player.getAlterWithAgeDays()) - Math.floor(playerCompare.getAlterWithAgeDays()));
                    // Player was not in our team at compare date
                    // Player was in our team at compare date
                    playerExists = !playerCompare.isGoner();
                }
                return new ColorLabelEntry(
                        birthdays,
                        ageString,
                        player.getAlterWithAgeDays(),
                        playerExists,
                        ColorLabelEntry.BG_STANDARD,
                        true);
            }
        };

        playerAdditionalArray[3] = new PlayerColumn(40, "BestePosition", 100) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {

                byte idealPosition = player.getIdealPosition();
                String posValue = String.format("%s (%.2f)",
                        MatchRoleID.getNameForPosition(idealPosition),
                        player.getPositionRating(idealPosition));
                if ( player.isAnAlternativeBestPosition(idealPosition) ) {
                    posValue += " *";
                }

                ColorLabelEntry tmp = new ColorLabelEntry(
                        -MatchRoleID.getSortId(idealPosition, false)
                                + (player.getPositionRating(idealPosition) / 100.0f),
                        posValue,
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                tmp.setIcon(ThemeManager.getIcon((player.getUserPosFlag() < 0) ? HOIconName.TOOTHEDWHEEL : HOIconName.HAND));
                return tmp;
            }

            @Override
            public boolean canBeDisabled() {
                return false;
            }

        };

        // Position
        playerAdditionalArray[4] = new PlayerColumn(LINEUP, " ", "Aufgestellt", 40) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                final HOModel model = HOVerwaltung.instance().getModel();
                var team = model.getCurrentLineup();
                final MatchRoleID positionBySpielerId =  team.getPositionByPlayerId(player.getPlayerId());
                if (team.isPlayerInLineup(player.getPlayerId()) && positionBySpielerId != null) {
                    final ColorLabelEntry colorLabelEntry = new ColorLabelEntry(
                            ImageUtilities.getJerseyIcon(
                                    positionBySpielerId,
                                    player.getShirtNumber()
                            ),
                            -positionBySpielerId
                                    .getSortId(),
                            ColorLabelEntry.FG_STANDARD,
                            ColorLabelEntry.BG_STANDARD,
                            SwingConstants.CENTER
                    );
                    colorLabelEntry.setToolTipText(MatchRoleID.getNameForPosition(positionBySpielerId.getPosition()));
                    return colorLabelEntry;
                }

                return new ColorLabelEntry(ImageUtilities.getJerseyIcon(null,
                        player.getShirtNumber()),
                        -player.getShirtNumber() - 1000,
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD,
                        SwingConstants.CENTER);
            }
        };

        playerAdditionalArray[5] = new PlayerColumn(GROUP, "Gruppe", 50) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                SmilieEntry smilieEntry = new SmilieEntry();
                smilieEntry.setPlayer(player);
                return smilieEntry;
            }
        };

        playerAdditionalArray[6] = new PlayerColumn(70, "Status", 50) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                PlayerStatusLabelEntry entry = new PlayerStatusLabelEntry();
                entry.setPlayer(player);
                return entry;
            }
        };

        playerAdditionalArray[7] = new PlayerColumn(421, "ls.player.wage", 100) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                final String bonus = "";
                final int gehalt = (int) (player.getWage() / core.model.UserParameter.instance().FXrate);
                final String gehalttext = Helper.getNumberFormat(true, 0).format(gehalt);
                if (playerCompare == null) {
                    return new DoubleLabelEntries(new ColorLabelEntry(gehalt,
                            gehalttext + bonus,
                            ColorLabelEntry.FG_STANDARD,
                            ColorLabelEntry.BG_STANDARD,
                            SwingConstants.RIGHT),
                            new ColorLabelEntry("",
                                    ColorLabelEntry.FG_STANDARD,
                                    ColorLabelEntry.BG_STANDARD,
                                    SwingConstants.RIGHT));
                }

                final int gehalt2 = (int) (playerCompare.getWage() / core.model.UserParameter
                        .instance().FXrate);
                return new DoubleLabelEntries(new ColorLabelEntry(gehalt,
                        gehalttext + bonus,
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD,
                        SwingConstants.RIGHT),
                        new ColorLabelEntry(gehalt - gehalt2,
                                ColorLabelEntry.BG_STANDARD,
                                true, false, 0));
            }
        };
        playerAdditionalArray[8] = new PlayerColumn(430, "ls.player.tsi", 0) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                final String text = Helper.getNumberFormat(false, 0).format(player.getTsi());
                if (playerCompare == null) {
                    return new DoubleLabelEntries(new ColorLabelEntry(player
                            .getTsi(),
                            text,
                            ColorLabelEntry.FG_STANDARD,
                            ColorLabelEntry.BG_STANDARD,
                            SwingConstants.RIGHT),
                            new ColorLabelEntry("",
                                    ColorLabelEntry.FG_STANDARD,
                                    ColorLabelEntry.BG_STANDARD,
                                    SwingConstants.RIGHT));
                }


                return new DoubleLabelEntries(new ColorLabelEntry(player
                        .getTsi(),
                        text,
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD,
                        SwingConstants.RIGHT),
                        new ColorLabelEntry(player.getTsi()
                                - playerCompare.getTsi(), ColorLabelEntry.BG_STANDARD,
                                false, false, 0));
            }

            @Override
            public void setSize(TableColumn column) {
                column.setMinWidth(Helper.calcCellWidth(90));
                column.setPreferredWidth(preferredWidth);
            }
        };

        // Last match rating column.
        playerAdditionalArray[9] = new PlayerColumn(RATING, "Rating", 40) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                var lastMatchRating = player.getLastMatchRating();
                if (lastMatchRating != null && lastMatchRating > 0) {
                    return new RatingTableEntry(lastMatchRating, true);
                }
                return new RatingTableEntry();
            }
        };

        // Last Match date column.
        playerAdditionalArray[10] = new PlayerColumn(LAST_MATCH_RATING, "LastMatchRating", 80) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                var matchId = player.getLastMatchId();
                if (matchId != null && matchId > 0) {
                    MatchKurzInfo info = DBManager.instance().getMatchesKurzInfoByMatchID(matchId, null);
                    if (info != null) {
                        return new MatchDateTableEntry(info.getMatchSchedule(), info.getMatchTypeExtended());
                    } else {
                        var dateAsString = player.getLastMatchDate();
                        if (!StringUtils.isEmpty(dateAsString)) {
                            var date = HODateTime.fromHT(dateAsString);
                            var matchType = player.getLastMatchType();
                            return new MatchDateTableEntry(date, matchType);
                        }
                    }
                }
                return new MatchDateTableEntry(null, MatchType.NONE);
            }
        };

        playerAdditionalArray[11] = new PlayerColumn(436, "Marktwert", 140) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                if (playerCompare == null) {

                    return new DoubleLabelEntries(new ColorLabelEntry(0,
                            "",
                            ColorLabelEntry.FG_STANDARD,
                            ColorLabelEntry.BG_STANDARD,
                            SwingConstants.RIGHT),
                            new ColorLabelEntry("",
                                    ColorLabelEntry.FG_STANDARD,
                                    ColorLabelEntry.BG_STANDARD,
                                    SwingConstants.RIGHT));

                }

                return new DoubleLabelEntries(new ColorLabelEntry(0,
                        "",
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD,
                        SwingConstants.RIGHT),
                        new ColorLabelEntry((float) (0),
                                ColorLabelEntry.BG_STANDARD,
                                true, false, 0)
                );
            }
        };

        playerAdditionalArray[12] = new PlayerColumn(437, "ls.player.short_motherclub", "ls.player.motherclub", 25) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                HomegrownEntry home = new HomegrownEntry();
                home.setPlayer(player);
                setPreferredWidth(35);
                return home;
            }
        };

        playerAdditionalArray[13] = new PlayerColumn(438, "ls.player.category", "ls.player.category", 25) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                var category = player.getPlayerCategory();
                String text;
                double sort;
                if (category != null && category != PlayerCategory.NoCategorySet) {
                    text = category.toString();
                    sort = category.getId();
                } else {
                    text = "";
                    sort = 100;
                }
                return new ColorLabelEntry(sort, text, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            }
        };
        playerAdditionalArray[14] = new PlayerColumn(439, "ls.player.statement", "ls.player.statement", 25) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                return new ColorLabelEntry(player.getPlayerStatement(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            }
        };
        playerAdditionalArray[15] = new PlayerColumn(441, "ls.player.ownernotes", "ls.player.ownernotes", 25) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                return new ColorLabelEntry(player.getOwnerNotes(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            }
        };


        // Last match rating end of game column.
        playerAdditionalArray[16] = new PlayerColumn(891, "ls.player.ratingend", 60) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                var lastMatchRatingEndOfGame = player.getLastMatchRatingEndOfGame();
                if (lastMatchRatingEndOfGame != null && lastMatchRatingEndOfGame > 0) {
//                    MatchKurzInfo info = DBManager.instance().getMatchesKurzInfoByMatchID(player.getLastMatchId(), null);
//                    if (info == null) {
//                        return new RatingTableEntry((float) player.getLastMatchRating(), true);
//                    } else {
                    return new RatingTableEntry(lastMatchRatingEndOfGame, true);
//                    }
                }
                return new RatingTableEntry();
            }
        };
        //last match minutes played
        playerAdditionalArray[17] = new PlayerColumn(DURATION, "ls.player.lastminutes", 50) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                var minutes = player.getLastMatchMinutes();
                String text;
                if (minutes == null) {
                    text = "";
                } else {
                    text = minutes.toString();
                }
                double sort = 100;
                return new ColorLabelEntry(sort, text, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
            }
        };

        //last match position
        playerAdditionalArray[18] = new PlayerColumn(892, "ls.player.lastlineup", 50) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                var position = player.getLastMatchPosition();
                double sort;
                String text;
                if (position == null || !isFieldMatchRoleId(position)) {
                    text = "";
                    sort = 1000;
                } else {
                    text = MatchRoleID.getNameForPositionWithoutTactic(MatchRoleID.getPosition(position, (byte) -1));
                    sort = position;
                }
                return new ColorLabelEntry(sort, text, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            }
        };

        // mother club name
        playerAdditionalArray[19] = new PlayerColumn(893, "ls.player.motherclub.name", 50) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                return new ColorLabelEntry(player.getOrDownloadMotherClubName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            }
        };

        // matches current team
        playerAdditionalArray[20] = new PlayerColumn(894, "ls.player.matchescurrentteam", 50) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                var m = player.getCurrentTeamMatches();
                String t;
                if ( m!= null){
                    t = m.toString();
                }
                else {
                    t = "";
                    m = 0;
                }
                return new ColorLabelEntry(m, t, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            }
        };

        // htms28
        playerAdditionalArray[21] = new PlayerColumn(895, "ls.player.htms28", 50) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                var m = player.getHtms28();
                String t;
                if ( m!= null){
                    t = m.toString();
                }
                else {
                    t = "";
                    m = 0;
                }
                return new ColorLabelEntry(m, t, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            }
        };

        playerAdditionalArray[22] = new PlayerColumn(896, "ls.player.htms", 50) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                var m = player.getHtms();
                String t;
                if ( m!= null){
                    t = m.toString();
                }
                else {
                    t = "";
                    m = 0;
                }
                return new ColorLabelEntry(m, t, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            }
        };
        // Schum-rank rating column.
        playerAdditionalArray[23] = new PlayerColumn(897, "ls.player.schum-rank", 40) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                var schumrank = player.getSchumRank();
                String t = String.format("%.2f", schumrank);
                return new ColorLabelEntry(schumrank, t, ColorLabelEntry.FG_STANDARD, player.isExcellentSchumRank()?Color.green:ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            }
        };
        playerAdditionalArray[24] = new PlayerColumn(SCHUM_RANK_BENCHMARK, "ls.player.schum-rank-benchmark", 40) { // 898
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                var schumrank = player.getSchumRank();
                var benchmark = player.getSchumRankBenchmark();
                var r = schumrank/benchmark*100;
                String t = String.format("%.2f / %.2f%%", benchmark, r);
                return new ColorLabelEntry(r, t, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            }
        };
        playerAdditionalArray[25] = new PlayerColumn(900, "ls.player.cost-to-convert-trainer-weak", "ls.player.cost-to-convert-trainer-weak.tooltip",  40) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                return getTrainerTransferEntry(player, 4);
            }
        };
        playerAdditionalArray[26] = new PlayerColumn(901, "ls.player.cost-to-convert-trainer-inadequate", "ls.player.cost-to-convert-trainer-inadequate.tooltip", 40) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                return getTrainerTransferEntry(player, 5);
            }
        };
        playerAdditionalArray[27] = new PlayerColumn(902, "ls.player.cost-to-convert-trainer-passable", "ls.player.cost-to-convert-trainer-passable.tooltip", 40) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                return getTrainerTransferEntry(player, 6);
            }
        };
        playerAdditionalArray[28] = new PlayerColumn(903, "ls.player.cost-to-convert-trainer-solid", "ls.player.cost-to-convert-trainer-solid.tooltip", 40) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                return getTrainerTransferEntry(player, 7);
            }
        };
        playerAdditionalArray[29] = new PlayerColumn(904, "ls.player.cost-to-convert-trainer-excellent", "ls.player.cost-to-convert-trainer-excellent.tooltip", 40) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                return getTrainerTransferEntry(player, 8);
            }
        };

        return playerAdditionalArray;
    }

    private static IHOTableEntry getTrainerTransferEntry(Player player, int i) {
        var costs = player.calculateCoachConversionCosts(i);
        var string = "";
        var swedishKrona = 0;
        if (costs != null){
            string = costs.toLocaleString();
            swedishKrona = costs.getSwedishKrona();
        }
        return new ColorLabelEntry(swedishKrona, string, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
    }
}
