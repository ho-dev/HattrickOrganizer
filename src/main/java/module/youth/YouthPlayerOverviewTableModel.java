package module.youth;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.YouthPlayerColumn;
import core.model.HOVerwaltung;
import core.model.player.Player;
import module.training.Skills;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class YouthPlayerOverviewTableModel extends HOTableModel {

    public YouthPlayerOverviewTableModel(int id) {
        super(id, "YouthPlayerOverview");
        columns = initColumns();
    }

    private YouthPlayerColumn[] initColumns() {
        return new YouthPlayerColumn[]{
                new YouthPlayerColumn("ls.player.name") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new ColorLabelEntry(player.getFullName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.player.age") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new ColorLabelEntry(player.getAgeYears() * 112 + player.getAgeDays(), Player.getAgeWithDaysAsString(player.getAgeYears(), player.getAgeDays(), new Date().getTime()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.arrival") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new ColorLabelEntry(new SimpleDateFormat("yyyy-MM-dd hh:mm").format(player.getArrivalDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.canBePromotedIn") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new ColorLabelEntry(player.getCanBePromotedIn(), "" + player.getCanBePromotedInAtDate(new Date().getTime()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                // TODO double clicking cell should invoke SkillEditorDialog to edit start and or current values
                new YouthPlayerColumn("ls.youth.player.Keeper", 200) {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new YouthSkillInfoColumn(player.getSkillInfo(Skills.HTSkillID.Keeper));
                    }
                },
                new YouthPlayerColumn("ls.youth.player.Defender", 200) {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new YouthSkillInfoColumn(player.getSkillInfo(Skills.HTSkillID.Defender));
                    }
                },
                new YouthPlayerColumn("ls.youth.player.Playmaker", 200) {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new YouthSkillInfoColumn(player.getSkillInfo(Skills.HTSkillID.Playmaker));
                    }
                },
                new YouthPlayerColumn("ls.youth.player.Winger", 200) {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new YouthSkillInfoColumn(player.getSkillInfo(Skills.HTSkillID.Winger));
                    }
                },
                new YouthPlayerColumn("ls.youth.player.Passing", 200) {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new YouthSkillInfoColumn(player.getSkillInfo(Skills.HTSkillID.Passing));
                    }
                },
                new YouthPlayerColumn("ls.youth.player.Scorer", 200) {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new YouthSkillInfoColumn(player.getSkillInfo(Skills.HTSkillID.Scorer));
                    }
                },
                new YouthPlayerColumn("ls.youth.player.SetPieces", 200) {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new YouthSkillInfoColumn(player.getSkillInfo(Skills.HTSkillID.SetPieces));
                    }
                },
                // TODO: Specialty column should include the specialty icon
                new YouthPlayerColumn("ls.youth.player.Specialty") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new ColorLabelEntry(player.getSpecialtyString(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.potential") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new ColorLabelEntry(player.getPotential(), "" + player.getPotential(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.average") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new ColorLabelEntry(player.getAverageSkillLevel(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.matchcount") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new ColorLabelEntry(player.getMatchCount(), "" + player.getMatchCount(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.trainingsum") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
                        return new ColorLabelEntry(player.getTrainedSkillSum(), String.format("%.2f", player.getTrainedSkillSum()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                // TODO: scout information
                new YouthPlayerColumn("ls.player.id", 0) {
                    @Override
                    public boolean isDisplay() {
                        return false;
                    }
                }
        };
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
                m_clData[playernum][columnnum] = ((YouthPlayerColumn) col).getTableEntry(player, null);
                columnnum++;
            }
            playernum++;
        }
        fireTableDataChanged();
    }

}
