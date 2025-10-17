package module.transfer.scout;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.HomegrownEntry;
import core.gui.comp.entry.IHOTableCellEntry;
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

        abstract public IHOTableCellEntry getTableEntry(ScoutEintrag scoutEintrag);
    }

    private Vector<ScoutEintrag> m_vScoutEintraege;

    public TransferScoutingTableModel() {
        super(UserColumnController.ColumnModelId.TRANSFERSCOUT, "TransferScout");
        int id = 0;
        columns = new ArrayList<>(List.of(
                new TransferScoutTableColumn(id++, "ls.player.id") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(String.valueOf(scouting.getPlayerID()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.name") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferScoutTableColumn(id++, "scout_price") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPrice(), ColorLabelEntry.BG_STANDARD);
                    }
                },
                new TransferScoutTableColumn(id++, "Ablaufdatum") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getDeadline().getTime(),
                                java.text.DateFormat.getDateTimeInstance()
                                        .format(scouting.getDeadline()),
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "BestePosition") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
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
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getAlterWithAgeDays(),
                                scouting.getAlterWithAgeDaysAsString(),
                                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.tsi") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getTSI(), String.valueOf(scouting.getTSI()),
                                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.short_motherclub", "ls.player.motherclub") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        HomegrownEntry home = new HomegrownEntry();
                        home.setPlayer(scouting.getPlayer());
                        return home;
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.leadership") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getLeadership(), String.valueOf(scouting.getLeadership()),
                                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.short_experience", "ls.player.experience") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getExperience(), String.valueOf(scouting.getPlayer().getExperience()),
                                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.short_form", "ls.player.form") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getForm() + "",
                                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.stamina", "ls.player.skill.stamina") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getStamina() + "",
                                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.short_loyalty", "ls.player.loyalty") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getLoyalty() + "",
                                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.keeper", "ls.player.skill.keeper") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getGoalkeeperSkill() + "",
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.defending", "ls.player.skill.defending") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getDefendingSkill() + "",
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.playmaking", "ls.player.skill.playmaking") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getPlaymakingSkill() + "",
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.passing", "ls.player.skill.passing") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getPassingSkill() + "",
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.winger", "ls.player.skill.winger") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getWingerSkill() + "",
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.scoring", "ls.player.skill.scoring") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getScoringSkill() + "",
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.skill_short.setpieces", "ls.player.skill.setpieces") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getPlayer().getSetPiecesSkill() + "",
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                SwingConstants.RIGHT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.keeper", "ls.player.position.keeper") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(keeper, NORMAL),
                                ColorLabelEntry.BG_PLAYERSPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.centraldefender", "ls.player.position.centraldefender") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftCentralDefender, NORMAL),
                                ColorLabelEntry.BG_PLAYERSPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.centraldefendertowardswing", "ls.player.position.centraldefendertowardswing") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftCentralDefender, TOWARDS_WING),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.centraldefenderoffensive", "ls.player.position.centraldefenderoffensive") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftCentralDefender, OFFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.wingback", "ls.player.position.wingback") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftBack, NORMAL),
                                ColorLabelEntry.BG_PLAYERSPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.wingbacktowardsmiddle", "ls.player.position.wingbacktowardsmiddle") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftBack, TOWARDS_MIDDLE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.wingbackoffensive", "ls.player.position.wingbackoffensive") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftBack, OFFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.wingbackdefensive", "ls.player.position.wingbackdefensive") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftBack, DEFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.innermidfielder", "ls.player.position.innermidfielder") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftInnerMidfield, NORMAL),
                                ColorLabelEntry.BG_PLAYERSPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.innermidfieldertowardswing", "ls.player.position.innermidfieldertowardswing") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftInnerMidfield, TOWARDS_WING),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.innermidfielderoffensive", "ls.player.position.innermidfielderoffensive") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftInnerMidfield, OFFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.innermidfielderdefensive", "ls.player.position.innermidfielderdefensive") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftInnerMidfield, DEFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.winger", "ls.player.position.winger") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftWinger, NORMAL),
                                ColorLabelEntry.BG_PLAYERSPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.wingertowardsmiddle", "ls.player.position.wingertowardsmiddle") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftWinger, TOWARDS_MIDDLE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.wingeroffensive", "ls.player.position.wingeroffensive") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftWinger, OFFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.wingerdefensive", "ls.player.position.wingerdefensive") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftWinger, DEFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.forward", "ls.player.position.forward") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftForward, NORMAL),
                                ColorLabelEntry.BG_PLAYERSPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.forwarddefensive", "ls.player.position.forwarddefensive") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftForward, DEFENSIVE),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.position_short.forwardtowardswing", "ls.player.position.forwardtowardswing") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(
                                scouting.getPlayer().getMatchAverageRating(leftForward, TOWARDS_WING),
                                ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                core.model.UserParameter.instance().nbDecimals);
                    }
                }, new TransferScoutTableColumn(id++, "Notizen") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getInfo(),
                                ColorLabelEntry.FG_STANDARD,
                                ColorLabelEntry.BG_STANDARD, JLabel.LEFT);
                    }
                },
                new TransferScoutTableColumn(id++, "ls.player.wage") {
                    @Override
                    public IHOTableCellEntry getTableEntry(ScoutEintrag scouting) {
                        return new ColorLabelEntry(scouting.getbaseWage().toLocaleString(),
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
}