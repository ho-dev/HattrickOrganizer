package core.gui.model;

import core.constants.player.PlayerAbility;
import core.constants.player.PlayerSkill;
//import core.epv.EPVData;
import core.db.DBManager;
import core.gui.comp.entry.*;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.match.Matchdetails;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.util.HTCalendarFactory;
import core.util.Helper;
import core.util.StringUtils;
import module.playerOverview.SpielerStatusLabelEntry;

import java.awt.Color;
import java.sql.Timestamp;

import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;


/**
 * Create the userColumns
 *
 * @author Thorsten Dietz
 */
final public class UserColumnFactory {

    //~ Static fields/initializers -----------------------------------------------------------------
    /**
     * id from the column NAME
     **/
    public static final int NAME = 1;

    /**
     * id from the column BEST_POSITION
     **/
    public static final int BEST_POSITION = 40;

    /**
     * id from the column LINUP
     **/
    public static final int LINUP = 50;

    /**
     * id from the column GROUP
     **/
    public static final int GROUP = 60;

    /**
     * id from the column ID
     **/
    public static final int ID = 440;

    /**
     * id from the column DATUM
     **/
    public static final int DATUM = 450;

    /**
     * id from the column RATING
     **/
    public static final int RATING = 435;

    /**
     * id from the column DATUM
     **/
    public static final int AUTO_LINEUP = 510;


    /**
     * id from the column BEST_POSITION
     **/
    public static final int LAST_MATCH = 461;

    /**
     * @return PlayerCBItem[]
     */
    public static PlayerCBItem[] createPlayerCBItemArray() {
        final PlayerCBItem[] playerCBItemArray = new PlayerCBItem[5];
        playerCBItemArray[0] = new PlayerCBItem(590, "ls.team.teamspirit") {
            @Override
            public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem) {
                return new ColorLabelEntry(spielerCBItem.getStimmung(),
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            }
        };

        playerCBItemArray[1] = new PlayerCBItem(600, "ls.team.confidence") {
            @Override
            public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem) {
                return new ColorLabelEntry(spielerCBItem.getSelbstvertrauen(),
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            }
        };

        playerCBItemArray[2] = new PlayerCBItem(601, "Position") {
            @Override
            public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem) {
                ColorLabelEntry colorLabelEntry = new ColorLabelEntry(ImageUtilities
                        .getJerseyIcon(MatchRoleID
                                        .getHTPosidForHOPosition4Image((byte) spielerCBItem
                                                .getPosition()),
                                (byte) 0, 0),
                        -MatchRoleID.getSortId((byte) spielerCBItem
                                .getPosition(), false),
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                colorLabelEntry.setText(MatchRoleID.getNameForPosition((byte) spielerCBItem
                        .getPosition())
                        + " ("
                        + spielerCBItem.getSpieler().calcPosValue((byte) spielerCBItem
                                .getPosition(),
                        true) + ")");
                return colorLabelEntry;
            }
        };

        playerCBItemArray[3] = new PlayerCBItem(RATING, "Rating") {
            @Override
            public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem) {
                return new RatingTableEntry(spielerCBItem.getRating(), false);
            }
        };

        playerCBItemArray[4] = new PlayerCBItem(602, "ls.player.age") {
            @Override
            public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem) {
                Player player = spielerCBItem.getSpieler();
                Timestamp matchDate = spielerCBItem.getMatchdetails().getSpielDatum();
                System.out.println(spielerCBItem.getMatchdate());

                if (matchDate != null) {
                    String ageString = player.getAdjustedAgeFromDate(matchDate);
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

        return playerCBItemArray;
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
                int einstellung = (matchdetails.getHeimId() == teamid) ? matchdetails.getHomeEinstellung() : matchdetails.getGuestEinstellung();
                return new ColorLabelEntry(Matchdetails.getNameForEinstellung(einstellung), ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            }
        };
        matchDetailsColumnsArray[2] = new MatchDetailsColumn(570, "ls.team.tactic") {
            @Override
            public IHOTableEntry getTableEntry(Matchdetails matchdetails) {
                final int teamid = HOVerwaltung.instance().getModel()
                        .getBasics().getTeamId();
                int tactic = (matchdetails.getHeimId() == teamid) ? matchdetails.getHomeTacticType() : matchdetails.getGuestTacticType();
                return new ColorLabelEntry(Matchdetails.getNameForTaktik(tactic), ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            }
        };
        matchDetailsColumnsArray[3] = new MatchDetailsColumn(580, "ls.team.tacticalskill") {
            @Override
            public IHOTableEntry getTableEntry(Matchdetails matchdetails) {
                final int teamid = HOVerwaltung.instance().getModel()
                        .getBasics().getTeamId();
                int tacticSkill = (matchdetails.getHeimId() == teamid) ? matchdetails.getHomeTacticSkill() : matchdetails.getGuestTacticSkill();
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
        final PlayerColumn[] playerGoalsArray = new PlayerColumn[4];
        playerGoalsArray[0] = new PlayerColumn(380, "TG", "ToreGesamt", 20) {
            @Override
            public int getValue(Player player) {
                return player.getToreGesamt();
            }
        };

        playerGoalsArray[1] = new PlayerColumn(390, "TF", "ToreFreund", 20) {
            @Override
            public int getValue(Player player) {
                return player.getToreFreund();
            }
        };

        playerGoalsArray[2] = new PlayerColumn(400, "TL", "ToreLiga", 20) {
            @Override
            public int getValue(Player player) {
                return player.getToreLiga();
            }
        };

        playerGoalsArray[3] = new PlayerColumn(410, "TP", "TorePokal", 20) {
            @Override
            public int getValue(Player player) {
                return player.getTorePokal();
            }
        };
        return playerGoalsArray;
    }

    /**
     * @return PlayerSkillColumn []
     */
    public static PlayerSkillColumn[] createPlayerSkillArray() {
        final PlayerSkillColumn[] playerSkillArray = new PlayerSkillColumn[12];
        playerSkillArray[0] = new PlayerSkillColumn(80, "ls.player.short_leadership", "ls.player.leadership", PlayerSkill.LEADERSHIP);
        playerSkillArray[1] = new PlayerSkillColumn(90, "ls.player.short_experience", "ls.player.experience", PlayerSkill.EXPERIENCE);
        playerSkillArray[2] = new PlayerSkillColumn(100, "ls.player.short_form", "ls.player.form", PlayerSkill.FORM);
        playerSkillArray[3] = new PlayerSkillColumn(110, "ls.player.skill_short.stamina", "ls.player.skill.stamina", PlayerSkill.STAMINA);
        playerSkillArray[4] = new PlayerSkillColumn(115, "ls.player.short_loyalty", "ls.player.loyalty", PlayerSkill.LOYALTY);
        playerSkillArray[5] = new PlayerSkillColumn(120, "ls.player.skill_short.keeper", "ls.player.skill.keeper", PlayerSkill.KEEPER);
        playerSkillArray[6] = new PlayerSkillColumn(130, "ls.player.skill_short.defending", "ls.player.skill.defending", PlayerSkill.DEFENDING);
        playerSkillArray[7] = new PlayerSkillColumn(140, "ls.player.skill_short.playmaking", "ls.player.skill.playmaking", PlayerSkill.PLAYMAKING);
        playerSkillArray[8] = new PlayerSkillColumn(150, "ls.player.skill_short.passing", "ls.player.skill.passing", PlayerSkill.PASSING);
        playerSkillArray[9] = new PlayerSkillColumn(160, "ls.player.skill_short.winger", "ls.player.skill.winger", PlayerSkill.WINGER);
        playerSkillArray[10] = new PlayerSkillColumn(170, "ls.player.skill_short.scoring", "ls.player.skill.scoring", PlayerSkill.SCORING);
        playerSkillArray[11] = new PlayerSkillColumn(180, "ls.player.skill_short.setpieces", "ls.player.skill.setpieces", PlayerSkill.SET_PIECES);

        return playerSkillArray;
    }

    /**
     * @return PlayerColumn []
     */
    public static PlayerColumn[] createPlayerBasicArray() {
        final PlayerColumn[] playerBasicArray = new PlayerColumn[2];
        playerBasicArray[0] = new PlayerColumn(NAME, "ls.player.name", 0) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                return new SpielerLabelEntry(player,
                        HOVerwaltung.instance().getModel()
                                .getLineupWithoutRatingRecalc()
                                .getPositionBySpielerId(player.getSpielerID()),
                        0f, false, false);
            }

            @Override
            public boolean isEditable() {
                return true; //false
            }
        };
        playerBasicArray[0].setDisplay(false);

        playerBasicArray[1] = new PlayerColumn(ID, "ls.player.id", 0) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                return new ColorLabelEntry(player.getSpielerID(),
                        player.getSpielerID() + "",
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            }

            @Override
            public boolean isEditable() {
                return true; //false
            }

            @Override
            public void setSize(TableColumn column) {
                // Column ID is not shown!
                column.setMinWidth(0);
                column.setPreferredWidth(0);
            }
        };
        playerBasicArray[1].setDisplay(false);
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
                final Color background = MatchesColumnModel.getColor4Matchtyp(match.getMatchTyp());
                return new ColorLabelEntry(match.getMatchDateAsTimestamp().getTime(),
                        java.text.DateFormat.getDateTimeInstance().format(match
                                .getMatchDateAsTimestamp()),
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.LEFT);
            }

            @Override
            public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem) {
                return new ColorLabelEntry(spielerCBItem.getMatchdate(),
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
            }
        };

        matchesArray[1] = new MatchKurzInfoColumn(460, "Spielart", 20) {
            @Override
            public IHOTableEntry getTableEntry(MatchKurzInfo match) {
                final Color background = MatchesColumnModel.getColor4Matchtyp(match.getMatchTyp());
                return new ColorLabelEntry(ThemeManager.getIcon(HOIconName.MATCHICONS[match.getMatchTyp().getIconArrayIndex()]),
                        match.getMatchTyp().getId(), ColorLabelEntry.FG_STANDARD,
                        background, SwingConstants.CENTER);
            }

            @Override
            public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem) {
                final Color background = MatchesColumnModel.getColor4Matchtyp(spielerCBItem.getMatchTyp());
                return new ColorLabelEntry(ThemeManager.getIcon(HOIconName.MATCHICONS[spielerCBItem.getMatchTyp().getIconArrayIndex()]),
                        spielerCBItem.getMatchTyp().getId(),
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.CENTER);
            }
        };

        matchesArray[2] = new MatchKurzInfoColumn(470, "Heim", 60) {
            @Override
            public IHOTableEntry getTableEntry(MatchKurzInfo match) {
                final Color background = MatchesColumnModel.getColor4Matchtyp(match.getMatchTyp());
                ColorLabelEntry entry = new ColorLabelEntry(match.getHeimName(), ColorLabelEntry.FG_STANDARD,
                        background, SwingConstants.LEFT);
                entry.setFGColor((match.getHeimID() == HOVerwaltung.instance().getModel().getBasics()
                        .getTeamId()) ? ThemeManager.getColor(HOColorName.TEAM_FG) : ThemeManager.getColor(HOColorName.LABEL_FG));

                if (match.getMatchStatus() != MatchKurzInfo.FINISHED)
                    entry.setIcon(ImageUtilities.NOIMAGEICON);
                else if (match.getHeimTore() > match.getGastTore())
                    entry.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR, Color.WHITE));
                else if (match.getHeimTore() < match.getGastTore())
                    entry.setIcon(ImageUtilities.NOIMAGEICON);
                else
                    entry.setIcon(ThemeManager.getTransparentIcon("star_gray", Color.WHITE));
                return entry;
            }

            @Override
            public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem) {
                final Color background = MatchesColumnModel.getColor4Matchtyp(spielerCBItem.getMatchTyp());
                ColorLabelEntry entry = new ColorLabelEntry(spielerCBItem.getHeimteam() + "",
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.LEFT);
                entry.setFGColor((spielerCBItem.getHeimID() == HOVerwaltung.instance().getModel().getBasics()
                        .getTeamId()) ? ThemeManager.getColor(HOColorName.TEAM_FG) : ThemeManager.getColor(HOColorName.LABEL_FG));
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
                final Color background = MatchesColumnModel.getColor4Matchtyp(match.getMatchTyp());
                ColorLabelEntry entry = new ColorLabelEntry(match.getGastName(), ColorLabelEntry.FG_STANDARD,
                        background, SwingConstants.LEFT);
                entry.setFGColor((match.getGastID() == HOVerwaltung.instance().getModel().getBasics()
                        .getTeamId()) ? ThemeManager.getColor(HOColorName.TEAM_FG) : ThemeManager.getColor(HOColorName.LABEL_FG));

                if (match.getMatchStatus() != MatchKurzInfo.FINISHED)
                    entry.setIcon(ImageUtilities.NOIMAGEICON);
                else if (match.getHeimTore() > match.getGastTore())
                    entry.setIcon(ImageUtilities.NOIMAGEICON);
                else if (match.getHeimTore() < match.getGastTore())
                    entry.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR, Color.WHITE));
                else
                    entry.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR_GRAY, Color.WHITE));

                return entry;
            }

            @Override
            public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem) {
                final Color background = MatchesColumnModel.getColor4Matchtyp(spielerCBItem.getMatchTyp());
                ColorLabelEntry entry = new ColorLabelEntry(spielerCBItem.getGastteam() + "",
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.LEFT);
                entry.setFGColor((spielerCBItem.getGastID() == HOVerwaltung.instance().getModel().getBasics()
                        .getTeamId()) ? ThemeManager.getColor(HOColorName.TEAM_FG) : ThemeManager.getColor(HOColorName.LABEL_FG));
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
                final Color background = MatchesColumnModel.getColor4Matchtyp(match.getMatchTyp());
                return new ColorLabelEntry(StringUtils.getResultString(match.getHeimTore(),
                        match.getGastTore()),
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.CENTER);
            }

            @Override
            public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem) {
                final Color background = MatchesColumnModel.getColor4Matchtyp(spielerCBItem.getMatchTyp());
                return new ColorLabelEntry(StringUtils.getResultString(spielerCBItem.getMatchdetails().getHomeGoals(),
                        spielerCBItem.getMatchdetails().getGuestGoals()),
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.CENTER);
            }

        };

        matchesArray[5] = new MatchKurzInfoColumn(494, "ls.match.hatstats.me", 80) {
            @Override
            public IHOTableEntry getTableEntry(MatchKurzInfo match, Matchdetails matchDetails) {
                final Color background = MatchesColumnModel.getColor4Matchtyp(match.getMatchTyp());
                return new ColorLabelEntry(String.valueOf(match.isHomeMatch() ? matchDetails.getHomeHatStats() : matchDetails.getAwayHatStats()),
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.CENTER);
            }

            @Override
            public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem) {
                final Color background = MatchesColumnModel.getColor4Matchtyp(spielerCBItem.getMatchTyp());
                return new ColorLabelEntry("not implemented 123456789",
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.CENTER);
            }

        };

        matchesArray[6] = new MatchKurzInfoColumn(498, "ls.match.hatstats.opp", 80) {
            @Override
            public IHOTableEntry getTableEntry(MatchKurzInfo match, Matchdetails matchDetails) {
                final Color background = MatchesColumnModel.getColor4Matchtyp(match.getMatchTyp());
                return new ColorLabelEntry(String.valueOf(match.isHomeMatch() ? matchDetails.getAwayHatStats() : matchDetails.getHomeHatStats()),
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.CENTER);
            }

            @Override
            public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem) {
                final Color background = MatchesColumnModel.getColor4Matchtyp(spielerCBItem.getMatchTyp());
                return new ColorLabelEntry("not implemented 123456789",
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.CENTER);
            }

        };


        matchesArray[7] = new MatchKurzInfoColumn(500, "ls.match.id", 55) {

            @Override
            public IHOTableEntry getTableEntry(MatchKurzInfo match) {
                final Color background = MatchesColumnModel.getColor4Matchtyp(match.getMatchTyp());
                return new ColorLabelEntry(match.getMatchID(), match.getMatchID() + "",
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.RIGHT);
            }

            @Override
            public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem) {
                final Color background = MatchesColumnModel.getColor4Matchtyp(spielerCBItem.getMatchTyp());
                return new ColorLabelEntry(spielerCBItem.getMatchID()+"",
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.CENTER);
            }
        };

        return matchesArray;
    }


    /*	*//**
     *
     * @return MatchKurzInfoColumn[]
     *//*
	public static MatchKurzInfoColumn[] createMatchesArray(){
		final MatchKurzInfoColumn[] matchesArray = new MatchKurzInfoColumn[6];
		matchesArray[0] = new MatchKurzInfoColumn(450,"Datum",70){
			@Override
			public IHOTableEntry getTableEntry(MatchKurzInfo match){
				final Color background = MatchesColumnModel.getColor4Matchtyp(match.getMatchTyp());
				return new ColorLabelEntry(match.getMatchDateAsTimestamp().getTime(),
                        java.text.DateFormat.getDateTimeInstance().format(match
                                .getMatchDateAsTimestamp()),
                                ColorLabelEntry.FG_STANDARD, background,
                                SwingConstants.LEFT);
			}

			@Override
			public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem){
				return new ColorLabelEntry(spielerCBItem.getMatchdate(),
                    ColorLabelEntry.FG_STANDARD,
                    ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
			}
		};

		matchesArray[1] = new MatchKurzInfoColumn(460,"Spielart",20){
			@Override
			public IHOTableEntry getTableEntry(MatchKurzInfo match){
				final Color background = MatchesColumnModel.getColor4Matchtyp(match.getMatchTyp());
				return new ColorLabelEntry(ThemeManager.getIcon(HOIconName.MATCHTYPES[match.getMatchTyp().getIconArrayIndex()]),
                        match.getMatchTyp().getId(), ColorLabelEntry.FG_STANDARD,
                        background, SwingConstants.CENTER);
			}

			@Override
			public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem){
				final Color background = MatchesColumnModel.getColor4Matchtyp(spielerCBItem.getMatchTyp());
				return new ColorLabelEntry(ThemeManager.getIcon(HOIconName.MATCHTYPES[spielerCBItem.getMatchTyp().getIconArrayIndex()]),
                        spielerCBItem.getMatchTyp().getId(),
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.CENTER);
			}
		};

		matchesArray[2] = new MatchKurzInfoColumn(470,"Heim",60){
			@Override
			public IHOTableEntry getTableEntry(MatchKurzInfo match){
				final Color background = MatchesColumnModel.getColor4Matchtyp(match.getMatchTyp());
				ColorLabelEntry entry = new ColorLabelEntry(match.getHeimName(), ColorLabelEntry.FG_STANDARD,
                        background, SwingConstants.LEFT);
				entry.setFGColor((match.getHeimID() == HOVerwaltung.instance().getModel().getBasics()
                        .getTeamId())?ThemeManager.getColor(HOColorName.TEAM_FG):ThemeManager.getColor(HOColorName.LABEL_FG));

				if (match.getMatchStatus() != MatchKurzInfo.FINISHED)
					entry.setIcon(ImageUtilities.NOIMAGEICON);
				else if (match.getHeimTore() > match.getGastTore())
					entry.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR, Color.WHITE));
				else if (match.getHeimTore() < match.getGastTore())
					entry.setIcon(ImageUtilities.NOIMAGEICON);
				else
					entry.setIcon(ThemeManager.getTransparentIcon("star_gray", Color.WHITE));
				return entry;
			}

			@Override
			public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem){
				final Color background = MatchesColumnModel.getColor4Matchtyp(spielerCBItem.getMatchTyp());
				ColorLabelEntry entry = new ColorLabelEntry(spielerCBItem.getHeimteam() + "",
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.LEFT);
				entry.setFGColor((spielerCBItem.getHeimID() == HOVerwaltung.instance().getModel().getBasics()
                        .getTeamId())?ThemeManager.getColor(HOColorName.TEAM_FG):ThemeManager.getColor(HOColorName.LABEL_FG));
				return entry;
			}

			@Override
			public void setSize(TableColumn column){
				column.setMinWidth(60);
				column.setPreferredWidth((preferredWidth==0)?160:preferredWidth);
			}
		};

		matchesArray[3] = new MatchKurzInfoColumn(480,"Gast",60){
			@Override
			public IHOTableEntry getTableEntry(MatchKurzInfo match){
				final Color background = MatchesColumnModel.getColor4Matchtyp(match.getMatchTyp());
				ColorLabelEntry entry = new ColorLabelEntry(match.getGastName(), ColorLabelEntry.FG_STANDARD,
                        background, SwingConstants.LEFT);
				entry.setFGColor((match.getGastID() == HOVerwaltung.instance().getModel().getBasics()
                        .getTeamId())?ThemeManager.getColor(HOColorName.TEAM_FG):ThemeManager.getColor(HOColorName.LABEL_FG));

				if (match.getMatchStatus() != MatchKurzInfo.FINISHED)
					entry.setIcon(ImageUtilities.NOIMAGEICON);
				else if (match.getHeimTore() > match.getGastTore())
					entry.setIcon(ImageUtilities.NOIMAGEICON);
				else if (match.getHeimTore() < match.getGastTore())
					entry.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR, Color.WHITE));
				else
					entry.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR_GRAY, Color.WHITE));

				return entry;
			}

			@Override
			public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem){
				final Color background = MatchesColumnModel.getColor4Matchtyp(spielerCBItem.getMatchTyp());
				ColorLabelEntry entry = new ColorLabelEntry(spielerCBItem.getGastteam() + "",
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.LEFT);
				entry.setFGColor((spielerCBItem.getGastID() == HOVerwaltung.instance().getModel().getBasics()
                        .getTeamId())?ThemeManager.getColor(HOColorName.TEAM_FG):ThemeManager.getColor(HOColorName.LABEL_FG));
				return entry;
			}

			@Override
			public void setSize(TableColumn column){
				column.setMinWidth(60);
				column.setPreferredWidth((preferredWidth==0)?160:preferredWidth);
			}
		};

		matchesArray[4] = new MatchKurzInfoColumn(490,"ls.match.result",45){
			@Override
			public IHOTableEntry getTableEntry(MatchKurzInfo match){
				final Color background = MatchesColumnModel.getColor4Matchtyp(match.getMatchTyp());
				return new ColorLabelEntry(StringUtils.getResultString(match.getHeimTore(),
                        match.getGastTore()),
                        	ColorLabelEntry.FG_STANDARD, background,
                        	SwingConstants.CENTER);
			}

			@Override
			public IHOTableEntry getTableEntry(SpielerMatchCBItem spielerCBItem){
				final Color background = MatchesColumnModel.getColor4Matchtyp(spielerCBItem.getMatchTyp());
				return new ColorLabelEntry(StringUtils.getResultString(spielerCBItem.getMatchdetails().getHomeGoals(),
						spielerCBItem.getMatchdetails().getGuestGoals()),
                        	ColorLabelEntry.FG_STANDARD, background,
                        	SwingConstants.CENTER);
			}

		};

		matchesArray[5] = new MatchKurzInfoColumn(500,"ls.match.id",55){

			@Override
			public IHOTableEntry getTableEntry(MatchKurzInfo match){
				final Color background = MatchesColumnModel.getColor4Matchtyp(match.getMatchTyp());
				return new ColorLabelEntry(match.getMatchID(), match.getMatchID() + "",
                        ColorLabelEntry.FG_STANDARD, background,
                        SwingConstants.RIGHT);
			}
		};

		return matchesArray;
	}
*/

    /**
     * creates an array of various player columns
     *
     * @return PlayerColumn[]
     */
    public static PlayerColumn[] createPlayerAdditionalArray() {
        final PlayerColumn[] playerAdditionalArray = new PlayerColumn[12];

        playerAdditionalArray[0] = new PlayerColumn(10, " ", " ", 0) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                int sort = player.getTrikotnummer();
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
                        SwingConstants.LEFT);
            }

            @Override
            public boolean isEditable() {
                return false;
            }
        };

        playerAdditionalArray[1] = new PlayerColumn(20, " ", "ls.player.nationality", 25) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                return new ColorLabelEntry(ImageUtilities.getCountryFlagIcon(player.getNationalitaet()),
                        player.getNationalitaet(),
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
            }
        };

        playerAdditionalArray[2] = new PlayerColumn(30, "ls.player.age", 40) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                String ageString = player.getAlterWithAgeDaysAsString();
                int birthdays = 0;
                boolean playerExists;

                if (playerCompare == null) {
                    // Birthdays since last HRF
                    birthdays = (int) (Math.floor(player.getAlterWithAgeDays()) - player.getAlter());
                    playerExists = false;
                } else {
                    // Birthdays since compare
                    birthdays = (int) (Math.floor(player.getAlterWithAgeDays()) - Math.floor(playerCompare.getAlterWithAgeDays()));
                    if (playerCompare.isOld())
                        // Player was not in our team at compare date
                        playerExists = false;
                    else
                        // Player was in our team at compare date
                        playerExists = true;
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

                String posValue = MatchRoleID.getNameForPosition(idealPosition)
                        + " ("
                        + player.getIdealPosStaerke(true, true, 1)
                        + "%)";
                byte[] alternativePosition = player.getAlternativePositions();
                for (byte altPos : alternativePosition) {
                    if (altPos == idealPosition) {
                        posValue += " *";
                        break;
                    }
                }

                ColorLabelEntry tmp = new ColorLabelEntry(
                        -MatchRoleID.getSortId(idealPosition, false)
                                + (player.getIdealPosStaerke(true) / 100.0f),
                        posValue,
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                tmp.setIcon(ThemeManager.getIcon((player.getUserPosFlag() < 0) ? HOIconName.TOOTHEDWHEEL : HOIconName.HAND));
                return tmp;
            }

            @Override
            public boolean isEditable() {
                return false;
            }

        };

        // Position
        playerAdditionalArray[4] = new PlayerColumn(LINUP, "Aufgestellt", 28) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                final HOModel model = HOVerwaltung.instance().getModel();
                final MatchRoleID positionBySpielerId = model.getLineupWithoutRatingRecalc()
                        .getPositionBySpielerId(player.getSpielerID());
                if (model.getLineupWithoutRatingRecalc().isPlayerInLineup(player.getSpielerID())
                        && (positionBySpielerId != null)) {
                    final ColorLabelEntry colorLabelEntry = new ColorLabelEntry(
                            ImageUtilities.getJerseyIcon(
                                    positionBySpielerId,
                                    player.getTrikotnummer()
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
                        player.getTrikotnummer()),
                        -player.getTrikotnummer() - 1000,
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
                SpielerStatusLabelEntry entry = new SpielerStatusLabelEntry();
                entry.setPlayer(player);
                return entry;
            }
        };


        playerAdditionalArray[7] = new PlayerColumn(420, "ls.player.wage", 100) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                final String bonus = "";
                final int gehalt = (int) (player.getGehalt() / core.model.UserParameter.instance().faktorGeld);
                final String gehalttext = Helper.getNumberFormat(true, 0).format(gehalt);
                if (playerCompare == null) {
                    return new DoppelLabelEntry(new ColorLabelEntry(gehalt,
                            gehalttext + bonus,
                            ColorLabelEntry.FG_STANDARD,
                            ColorLabelEntry.BG_STANDARD,
                            SwingConstants.RIGHT),
                            new ColorLabelEntry("",
                                    ColorLabelEntry.FG_STANDARD,
                                    ColorLabelEntry.BG_STANDARD,
                                    SwingConstants.RIGHT));
                }

                final int gehalt2 = (int) (playerCompare.getGehalt() / core.model.UserParameter
                        .instance().faktorGeld);
                return new DoppelLabelEntry(new ColorLabelEntry(gehalt,
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
                final String text = Helper.getNumberFormat(false, 0).format(player.getTSI());
                if (playerCompare == null) {
                    return new DoppelLabelEntry(new ColorLabelEntry(player
                            .getTSI(),
                            text,
                            ColorLabelEntry.FG_STANDARD,
                            ColorLabelEntry.BG_STANDARD,
                            SwingConstants.RIGHT),
                            new ColorLabelEntry("",
                                    ColorLabelEntry.FG_STANDARD,
                                    ColorLabelEntry.BG_STANDARD,
                                    SwingConstants.RIGHT));
                }


                return new DoppelLabelEntry(new ColorLabelEntry(player
                        .getTSI(),
                        text,
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD,
                        SwingConstants.RIGHT),
                        new ColorLabelEntry(player.getTSI()
                                - playerCompare.getTSI(), ColorLabelEntry.BG_STANDARD,
                                false, false, 0));
            }

            @Override
            public void setSize(TableColumn column) {
                column.setMinWidth(Helper.calcCellWidth(90));

            }
        };

        playerAdditionalArray[9] = new PlayerColumn(LAST_MATCH, "LastMatchRating", 50) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                if (player.getLastMatchRating() > 0) {
                    //
                    MatchKurzInfo info = DBManager.instance().getMatchesKurzInfoByMatchID(player.getLastMatchId());
                    if(info==null){
                        return new LastMatchLabelEntry((float) player.getLastMatchRating());
                    }else
                        return new LastMatchLabelEntry((float) player.getLastMatchRating(), player.getLastMatchDate(), info.getMatchTyp());
                }
                return new LastMatchLabelEntry();
            }

        };

        playerAdditionalArray[10] = new PlayerColumn(436, "Marktwert", 140) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
//					EPVData data = HOVerwaltung.instance().getModel().getEPV().getEPVData(player);
//					double price = HOVerwaltung.instance().getModel().getEPV().getPrice(data);
//					final String text = Helper.getNumberFormat(true, 0).format(price);

                if (playerCompare == null) {

                    return new DoppelLabelEntry(new ColorLabelEntry(0,
                            "",
                            ColorLabelEntry.FG_STANDARD,
                            ColorLabelEntry.BG_STANDARD,
                            SwingConstants.RIGHT),
                            new ColorLabelEntry("",
                                    ColorLabelEntry.FG_STANDARD,
                                    ColorLabelEntry.BG_STANDARD,
                                    SwingConstants.RIGHT));

                }
//					EPVData comparedata = HOVerwaltung.instance().getModel().getEPV().getEPVData(playerCompare);
                int htweek = HTCalendarFactory.getHTWeek(playerCompare.getHrfDate());
//					double compareepv = HOVerwaltung.instance().getModel().getEPV().getPrice(comparedata, htweek);
                return new DoppelLabelEntry(new ColorLabelEntry(0,
                        "",
                        ColorLabelEntry.FG_STANDARD,
                        ColorLabelEntry.BG_STANDARD,
                        SwingConstants.RIGHT),
                        new ColorLabelEntry((float) (0 - 0),
                                ColorLabelEntry.BG_STANDARD,
                                true, false, 0)
                );
            }
        };

        playerAdditionalArray[11] = new PlayerColumn(437, "ls.player.short_motherclub", "ls.player.motherclub", 25) {
            @Override
            public IHOTableEntry getTableEntry(Player player, Player playerCompare) {
                HomegrownEntry home = new HomegrownEntry();
                home.setPlayer(player);
                setPreferredWidth(35);
                return home;
            }
        };

        return playerAdditionalArray;
    }
}
