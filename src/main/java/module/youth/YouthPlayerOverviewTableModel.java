package module.youth;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnController;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.HODateTime;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


public class YouthPlayerOverviewTableModel extends HOTableModel {

    public YouthPlayerOverviewTableModel(UserColumnController.ColumnModelId id) {
        super(id, "YouthPlayerOverview");
        columns = initColumns();
    }

    private YouthPlayerColumn[] initColumns() {
        var tmp = new ArrayList<>(List.of(
                new YouthPlayerColumn("ls.player.name") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getFullName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.player.age") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getAgeYears() * 112 + player.getAgeDays(), Player.getAgeWithDaysAsString(player.getAgeYears(), player.getAgeDays(), HODateTime.now()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.arrival") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(HODateTime.toEpochSecond(player.getArrivalDate()), HODateTime.toLocaleDateTime(player.getArrivalDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.lastmatchdate") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(HODateTime.toEpochSecond(player.getYouthMatchDate()), HODateTime.toLocaleDateTime(player.getYouthMatchDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.canBePromotedIn") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getCanBePromotedIn(), "" + player.getCanBePromotedInAtDate(HODateTime.now()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                // TODO: Specialty column should include the specialty icon
                new YouthPlayerColumn("ls.youth.player.Specialty") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getSpecialtyString(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.potential") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getPotential(), "" + player.getPotential(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.average") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getAverageSkillLevel(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.matchcount") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getMatchCount(), "" + player.getMatchCount(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.trainingsum") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getTrainedSkillSum(), String.format("%.2f", player.getTrainedSkillSum()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.trainingprogress") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getProgressLastMatch(), String.format("%.2f", player.getProgressLastMatch()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                }
        ));

        for (var skillId : YouthPlayer.skillIds) {
            tmp.add(new YouthPlayerColumn("ls.youth.player." + skillId.toString(), 200) {
                @Override
                public IHOTableEntry getTableEntry(YouthPlayer player) {
                    return new YouthSkillInfoColumn(player.getSkillInfo(skillId));
                }
            });
        }

        return tmp.toArray(new YouthPlayerColumn[0]);
    }

    @Override
    protected void initData() {
        UserColumn[] displayedColumns = getDisplayedColumns();
        var youthplayers = HOVerwaltung.instance().getModel().getCurrentYouthPlayers();
        m_clData = new Object[youthplayers.size()][columns.length];
        int playernum = 0;
        for (var player : youthplayers) {
            int columnnum = 0;
            for (var col : displayedColumns) {
                m_clData[playernum][columnnum] = ((YouthPlayerColumn) col).getTableEntry(player);
                columnnum++;
            }
            playernum++;
        }
        fireTableDataChanged();
    }
}
