package module.transfer.scout;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.HomegrownEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnController;
import core.model.UserParameter;
import core.model.player.MatchRoleID;
import core.util.CurrencyUtils;
import core.util.Helper;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static core.model.player.IMatchRoleID.*;

/**
 * @author Volker Fischer
 * @version 0.2a    31.10.2001
 */
public class TransferScoutingTableModel extends HOTableModel {

    private abstract static class TransferScoutTableColumn extends UserColumn {

        public TransferScoutTableColumn(int id, String name) {
            this(id, name, name, 50);
        }
        public TransferScoutTableColumn(int id, String name, String tooltip) {
            this(id, name, tooltip, 50);
        }

        public TransferScoutTableColumn(int id, String name, String tooltip, int minWidth) {
            super(id, name, tooltip);
            this.index = this.getId();
            this.minWidth = minWidth;
            this.setPreferredWidth(minWidth);
            this.setDisplay(true);
        }

        abstract public IHOTableEntry getTableEntry(ScoutEintrag scoutEintrag);
    }

    private Vector<ScoutEintrag> m_vScoutEintraege;

    /**
     * Array of ToolTip Strings shown in the table header (first row of table)
     */
    public TransferScoutingTableModel() {
        super(UserColumnController.ColumnModelId.TRANSFERSCOUT, "TransferScout");
        int id = 0;
        columns = new ArrayList<>(List.of(
                new TransferScoutTableColumn(id++, "ls.player.id") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(String.valueOf(scouting.getPlayerID()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.name") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferScoutTableColumn(id++, "scout_price") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(CurrencyUtils.convertCurrency(scouting.getPrice()), ColorLabelEntry.BG_STANDARD, true, 0);
                    }
                },
                new TransferScoutTableColumn(id++, "Ablaufdatum") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getDeadline().getTime(),
                                java.text.DateFormat.getDateTimeInstance()
                                        .format(scouting.getDeadline()),
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "BestePosition") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        var idealPosition = scouting.getPlayer().getIdealPosition();
                        var idealPositionRating = scouting.getPlayer().getIdealPositionRating();
                        return new ColorLabelEntry(
                                MatchRoleID.getSortId(idealPosition, false) - (idealPositionRating / 100.0f),
                                String.format("%s (%.2f)", MatchRoleID.getNameForPosition(idealPosition), idealPositionRating),
                                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.age") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getAlterWithAgeDays(),
                                scouting.getAlterWithAgeDaysAsString(),
                                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.tsi") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getTSI(), String.valueOf(scouting.getTSI()),
                                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.short_motherclub", "ls.player.motherclub") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        HomegrownEntry home = new HomegrownEntry();
                        home.setPlayer(scouting.getPlayer());
                        return home;
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.leadership") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getLeadership(), String.valueOf(scouting.getLeadership()),
                                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.short_experience", "ls.player.experience") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getExperience(), String.valueOf(scouting.getPlayer().getExperience()),
                                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.short_form", "ls.player.form") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getForm() + "",
                                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.stamina", "ls.player.skill.stamina") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getStamina() + "",
                                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.short_loyalty", "ls.player.loyalty") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getLoyalty() + "",
                                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.keeper", "ls.player.skill.keeper") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getGoalkeeperSkill() + "",
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.defending", "ls.player.skill.defending") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getDefendingSkill() + "",
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.playmaking", "ls.player.skill.playmaking") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getPlaymakingSkill() + "",
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.passing", "ls.player.skill.passing") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getPassingSkill() + "",
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.winger", "ls.player.skill.winger") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getWingerSkill() + "",
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.scoring", "ls.player.skill.scoring") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getScoringSkill() + "",
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.setpieces", "ls.player.skill.setpieces") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getSetPiecesSkill() + "",
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.keeper", "ls.player.position.keeper") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(keeper, NORMAL),
                                ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.centraldefender", "ls.player.position.centraldefender") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftCentralDefender, NORMAL),
                                ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.centraldefendertowardswing", "ls.player.position.centraldefendertowardswing") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftCentralDefender, TOWARDS_WING),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.centraldefenderoffensive", "ls.player.position.centraldefenderoffensive") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftCentralDefender, OFFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.wingback", "ls.player.position.wingback") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftBack, NORMAL),
                                ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.wingbacktowardsmiddle", "ls.player.position.wingbacktowardsmiddle") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftBack, TOWARDS_MIDDLE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.wingbackoffensive", "ls.player.position.wingbackoffensive") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftBack, OFFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.wingbackdefensive", "ls.player.position.wingbackdefensive") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftBack, DEFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.innermidfielder", "ls.player.position.innermidfielder") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftInnerMidfield, NORMAL),
                                ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.innermidfieldertowardswing", "ls.player.position.innermidfieldertowardswing") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftInnerMidfield, TOWARDS_WING),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.innermidfielderoffensive", "ls.player.position.innermidfielderoffensive") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftInnerMidfield, OFFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.innermidfielderdefensive", "ls.player.position.innermidfielderdefensive") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftInnerMidfield, DEFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.winger", "ls.player.position.winger") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftWinger, NORMAL),
                                ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.wingertowardsmiddle", "ls.player.position.wingertowardsmiddle") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftWinger, TOWARDS_MIDDLE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.wingeroffensive", "ls.player.position.wingeroffensive") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftWinger, OFFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.wingerdefensive", "ls.player.position.wingerdefensive") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftWinger, DEFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.forward", "ls.player.position.forward") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftForward, NORMAL),
                                ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.forwarddefensive", "ls.player.position.forwarddefensive") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftForward, DEFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.forwardtowardswing", "ls.player.position.forwardtowardswing") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftForward, TOWARDS_WING),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                false,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                }, new TransferScoutTableColumn(id++, "Notizen") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getInfo(),
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_STANDARD, JLabel.LEFT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.wage") {
                    @Override
                    public IHOTableEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(Helper.formatCurrency(scouting.getbaseWage() / UserParameter.instance().FXrate),
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                }
        )).toArray(new TransferScoutTableColumn[0]);

    }

    /**
     * Returns false.  This is the default implementation for all cells.
     *
     * @param row the row being queried
     * @param col the column being queried
     * @return false
     */
    @Override
    public final boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    protected void initData() {
        UserColumn[] displayedColumns = getDisplayedColumns();
        m_clData = new Object[m_vScoutEintraege.size()][columns.length];
        int playernum = 0;
        for (var value : m_vScoutEintraege) {
            int columnnum = 0;
            for (var col : displayedColumns) {
                m_clData[playernum][columnnum] = ((TransferScoutTableColumn) col).getTableEntry(value);
                columnnum++;
            }
            playernum++;
        }
        fireTableDataChanged();
    }

//    /**
//     * Returns the class of the table entry row=0, column=columnIndex
//     * Returns String.class if there is no such entry
//     *
//     * @param columnIndex the column being queried
//     * @return see above
//     */
//    @Override
//    public final Class<?> getColumnClass(int columnIndex) {
//        final Object obj = getValueAt(0, columnIndex);
//        return Objects.requireNonNullElse(obj, "").getClass();
//    }
//
//    //-----Access methods----------------------------------------
//    public final int getColumnCount() {
//        return m_sColumnNames.length;
//    }

//    /**
//     * Returns the name for the column of columnIndex.
//     * Return null if there is no match.
//     *
//     * @param columnIndex the column being queried
//     * @return a string containing the name of <code>column</code>
//     */
//    @Override
//    public final String getColumnName(int columnIndex) {
//        if (m_sColumnNames.length > columnIndex) {
//            return m_sColumnNames[columnIndex];
//        } else {
//            return null;
//        }
//    }
//-
//    /**
//     * Returns the number of data sets (without header).
//     *
//     * @return m_clData.length
//     */
//    public final int getRowCount() {
//        if (m_clData != null) {
//            return m_clData.length;
//        } else {
//            return 0;
//        }
//    }

    public final ScoutEintrag getScoutEintrag(int playerID) {
        for (ScoutEintrag scoutEintrag : m_vScoutEintraege) {
            if (scoutEintrag.getPlayerID() == playerID) {
                return scoutEintrag.duplicate();
            }
        }
        return null;
    }

    /**
     * Returns the list of ScoutEntries
     */
    public final Vector<ScoutEintrag> getScoutListe() {
        return m_vScoutEintraege;
    }

//    public final Object getValue(int row, String columnName) {
//        if ((m_sColumnNames != null) && (m_clData != null)) {
//            int i = 0;
//            while ((i < m_sColumnNames.length) && !m_sColumnNames[i].equals(columnName)) {
//                i++;
//            }
//            return m_clData[row][i];
//        } else {
//            return null;
//        }
//    }
//
//    @Override
//    public final void setValueAt(Object value, int row, int column) {
//        m_clData[row][column] = value;
//    }
//
//    public final Object getValueAt(int row, int column) {
//        if (m_clData != null) {
//            return m_clData[row][column];
//        }
//        return null;
//    }

    /**
     * Set player again
     */
    public final void setValues(Vector<ScoutEintrag> scouteintraege) {
        m_vScoutEintraege = scouteintraege;
        initData();
    }

    /**
     * Add player to the table
     */
    public final void addScoutEintrag(ScoutEintrag scouteintraege) {
        m_vScoutEintraege.add(scouteintraege.duplicate());
        initData();
    }

    /**
     * Remove one ScoutEntry from table
     *
     * @param scouteintraege the ScoutEntry which will be removed from the table
     */
    public final void removeScoutEintrag(ScoutEintrag scouteintraege) {
        if (m_vScoutEintraege.remove(scouteintraege)) {
            initData();
        }
    }

    /**
     * Remove all players from table
     */
    public final void removeScoutEntries() {
        if (m_vScoutEintraege != null) {
            m_vScoutEintraege.clear();
            initData();
        }
    }

//    //-----initialization-----------------------------------------
//
//    /**
//     * Return a Data[][] from the player vector
//     */
//    private void initData() {
//        m_clData = new Object[m_vScoutEintraege.size()][m_sColumnNames.length];
//        for (int i = 0; i < m_vScoutEintraege.size(); i++) {
//            final ScoutEintrag aktuellerScoutEintrag = m_vScoutEintraege.get(i);
//            final Player aktuellerPlayer = new Player();
//            aktuellerPlayer.setFirstName("");  //TODO: fix this
//            aktuellerPlayer.setNickName(" "); //TODO: fix this
//            aktuellerPlayer.setLastName(aktuellerScoutEintrag.getName());
//            aktuellerPlayer.setSpecialty(aktuellerScoutEintrag.getSpeciality());
//            aktuellerPlayer.setExperience(aktuellerScoutEintrag.getErfahrung());
//            aktuellerPlayer.setLeadership(aktuellerScoutEintrag.getLeadership());
//            aktuellerPlayer.setForm(aktuellerScoutEintrag.getForm());
//            aktuellerPlayer.setStamina(aktuellerScoutEintrag.getKondition());
//            aktuellerPlayer.setDefendingSkill(aktuellerScoutEintrag.getVerteidigung());
//            aktuellerPlayer.setScoringSkill(aktuellerScoutEintrag.getTorschuss());
//            aktuellerPlayer.setGoalkeeperSkill(aktuellerScoutEintrag.getTorwart());
//            aktuellerPlayer.setWingerSkill(aktuellerScoutEintrag.getFluegelspiel());
//            aktuellerPlayer.setPassingSkill(aktuellerScoutEintrag.getPasspiel());
//            aktuellerPlayer.setSetPiecesSkill(aktuellerScoutEintrag.getStandards());
//            aktuellerPlayer.setPlaymakingSkill(aktuellerScoutEintrag.getSpielaufbau());
//            aktuellerPlayer.setLoyalty(aktuellerScoutEintrag.getLoyalty());
//            aktuellerPlayer.setHomeGrown(aktuellerScoutEintrag.isHomegrown());
//
//            var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
//            //ID
//            m_clData[i][0] = new ColorLabelEntry(aktuellerScoutEintrag.getPlayerID() + "",
//                    ColorLabelEntry.FG_STANDARD,
//                    ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
//            //Name
//            m_clData[i][1] = new PlayerLabelEntry(aktuellerPlayer, null, 0f, false, false);
//            //Price
//            m_clData[i][2] = new ColorLabelEntry(Helper.formatCurrency(aktuellerScoutEintrag.getPrice() / UserParameter.instance().FXrate),
//                    ColorLabelEntry.FG_STANDARD,
//                    ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
//            //Ablaufdatum
//            m_clData[i][3] = new ColorLabelEntry(aktuellerScoutEintrag.getDeadline().getTime(),
//                    java.text.DateFormat.getDateTimeInstance()
//                            .format(aktuellerScoutEintrag.getDeadline()),
//                    ColorLabelEntry.FG_STANDARD,
//                    ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
//            //Beste Position
//            m_clData[i][4] = new ColorLabelEntry(
//                    MatchRoleID.getSortId(aktuellerPlayer.getIdealPosition(), false) - (aktuellerPlayer.getIdealPositionRating() / 100.0f),
//                    MatchRoleID.getNameForPosition(aktuellerPlayer.getIdealPosition())
//                            + " ("
//                            + aktuellerPlayer.getIdealPositionRating() + ")",
//                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
//            //Age
//            m_clData[i][5] = new ColorLabelEntry(aktuellerScoutEintrag.getAlterWithAgeDays(),
//                    aktuellerScoutEintrag.getAlterWithAgeDaysAsString(),
//                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
//            //TSI
//            m_clData[i][6] = new ColorLabelEntry(aktuellerScoutEintrag.getTSI() + "",
//                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
//            // Homegrown
//            HomegrownEntry home = new HomegrownEntry();
//            home.setPlayer(aktuellerPlayer);
//            m_clData[i][7] = home;
//            //Leadershio
//            m_clData[i][8] = new ColorLabelEntry(aktuellerPlayer.getLeadership() + "",
//                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.RIGHT);
//            //Erfahrung
//            m_clData[i][9] = new ColorLabelEntry(aktuellerPlayer.getExperience() + "",
//                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.RIGHT);
//            //Form
//            m_clData[i][10] = new ColorLabelEntry(aktuellerPlayer.getForm() + "",
//                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.RIGHT);
//            //Kondition
//            m_clData[i][11] = new ColorLabelEntry(aktuellerPlayer.getStamina() + "",
//                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.RIGHT);
//            // Loyalty
//            m_clData[i][12] = new ColorLabelEntry(aktuellerPlayer.getLoyalty() + "",
//                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES,
//                    SwingConstants.RIGHT);
//
//            //Torwart
//            m_clData[i][13] = new ColorLabelEntry(aktuellerPlayer.getGoalkeeperSkill() + "",
//                    ColorLabelEntry.FG_STANDARD,
//                    ColorLabelEntry.BG_SINGLEPLAYERVALUES,
//                    SwingConstants.RIGHT);
//
//            //Verteidigung
//            m_clData[i][14] = new ColorLabelEntry(aktuellerPlayer.getDefendingSkill() + "",
//                    ColorLabelEntry.FG_STANDARD,
//                    ColorLabelEntry.BG_SINGLEPLAYERVALUES,
//                    SwingConstants.RIGHT);
//
//            //Spielaufbau
//            m_clData[i][15] = new ColorLabelEntry(aktuellerPlayer.getPlaymakingSkill() + "",
//                    ColorLabelEntry.FG_STANDARD,
//                    ColorLabelEntry.BG_SINGLEPLAYERVALUES,
//                    SwingConstants.RIGHT);
//
//            //Passpiel
//            m_clData[i][16] = new ColorLabelEntry(aktuellerPlayer.getPassingSkill() + "",
//                    ColorLabelEntry.FG_STANDARD,
//                    ColorLabelEntry.BG_SINGLEPLAYERVALUES,
//                    SwingConstants.RIGHT);
//
//            //Flügelspiel
//            m_clData[i][17] = new ColorLabelEntry(aktuellerPlayer.getWingerSkill() + "",
//                    ColorLabelEntry.FG_STANDARD,
//                    ColorLabelEntry.BG_SINGLEPLAYERVALUES,
//                    SwingConstants.RIGHT);
//
//            //Torschuss
//            m_clData[i][18] = new ColorLabelEntry(aktuellerPlayer.getScoringSkill() + "",
//                    ColorLabelEntry.FG_STANDARD,
//                    ColorLabelEntry.BG_SINGLEPLAYERVALUES,
//                    SwingConstants.RIGHT);
//
//            //Standards
//            m_clData[i][19] = new ColorLabelEntry(aktuellerPlayer.getSetPiecesSkill() + "",
//                    ColorLabelEntry.FG_STANDARD,
//                    ColorLabelEntry.BG_SINGLEPLAYERVALUES,
//                    SwingConstants.RIGHT);
//
//            //Wert Torwart
//            m_clData[i][20] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, IMatchRoleID.keeper, NORMAL),
//                    ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Innnenverteidiger
//            m_clData[i][21] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, IMatchRoleID.leftCentralDefender, NORMAL),
//                    ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Innnenverteidiger Nach Aussen
//            m_clData[i][22] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, IMatchRoleID.leftCentralDefender, TOWARDS_WING),
//                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
//                    false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Innnenverteidiger Offensiv
//            m_clData[i][23] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, IMatchRoleID.leftCentralDefender, OFFENSIVE),
//                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
//                    false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Aussenverteidiger
//            m_clData[i][24] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftBack, NORMAL),
//                    ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Aussenverteidiger Nach Innen
//            m_clData[i][25] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftBack, TOWARDS_MIDDLE),
//                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
//                    false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Aussenverteidiger Offensiv
//            m_clData[i][26] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftBack, OFFENSIVE),
//                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
//                    false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Aussenverteidiger Defensiv
//            m_clData[i][27] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftBack, DEFENSIVE),
//                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
//                    false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Mittelfeld
//            m_clData[i][28] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftInnerMidfield, NORMAL),
//                    ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Mittelfeld Nach Aussen
//            m_clData[i][29] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftInnerMidfield, TOWARDS_WING),
//                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
//                    false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Mittelfeld Offensiv
//            m_clData[i][30] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftInnerMidfield, OFFENSIVE),
//                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
//                    false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Mittelfeld Defensiv
//            m_clData[i][31] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftInnerMidfield, DEFENSIVE),
//                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
//                    false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Flügel
//            m_clData[i][32] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftWinger, NORMAL),
//                    ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Flügel Nach Innen
//            m_clData[i][33] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftWinger, TOWARDS_MIDDLE),
//                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
//                    false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Flügel Offensiv
//            m_clData[i][34] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftWinger, OFFENSIVE),
//                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
//                    false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Flügel Defensiv
//            m_clData[i][35] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftWinger, DEFENSIVE),
//                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
//                    false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Sturm
//            m_clData[i][36] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftForward, NORMAL),
//                    ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Sturm Defensiv
//            m_clData[i][37] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftForward, DEFENSIVE),
//                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
//                    false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Wert Sturm Nach Aussen
//            m_clData[i][38] = new ColorLabelEntry(
//                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftForward, TOWARDS_WING),
//                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
//                    false,
//                    core.model.UserParameter.instance().nbDecimals);
//
//            //Notiz
//            m_clData[i][39] = new ColorLabelEntry(aktuellerScoutEintrag.getInfo(),
//                    ColorLabelEntry.FG_STANDARD,
//                    ColorLabelEntry.BG_STANDARD, JLabel.LEFT);
//
//            m_clData[i][40] = new ColorLabelEntry(Helper.formatCurrency(aktuellerScoutEintrag.getbaseWage() / UserParameter.instance().FXrate),
//                    ColorLabelEntry.FG_STANDARD,
//                    ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
//
//        }
//    }

}