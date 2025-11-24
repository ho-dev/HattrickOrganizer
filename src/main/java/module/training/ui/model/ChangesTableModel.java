package module.training.ui.model;

import core.constants.player.PlayerAbility;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.HOPlayersTableModel;
import core.gui.comp.table.HOTableModel;
import core.gui.model.UserColumnController;
import core.model.player.Player;
import core.util.HODateTime;
import module.training.PlayerSkillChange;
import module.training.ui.TrainingLegendPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel representing skill changes for individual players.
 * 
 * @author NetHyperon
 */
public class ChangesTableModel extends HOPlayersTableModel {

	private List<PlayerSkillChange> values;
    static int nextColumnId = 0;

    public ChangesTableModel(UserColumnController.@NotNull ColumnModelId id) {
        super(id, "TrainingAnalysis");
        columns = new ArrayList<>(List.of(
                new TrainingColumn(nextColumnId++, "Week", 50) {
                    @Override
                    public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {
                        var skillChange = entry.getSkillChange();
                        return new ColorLabelEntry(HODateTime.toEpochSecond(skillChange.getDate()), String.valueOf(skillChange.getHtWeek()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT );
                    }
                },
                new TrainingColumn(nextColumnId++, "Season", 50) {
                    @Override
                    public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {
                        var skillChange = entry.getSkillChange();
                        return new ColorLabelEntry(HODateTime.toEpochSecond(skillChange.getDate()), String.valueOf(skillChange.getHtSeason()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT );
                    }
                },
                new TrainingColumn(nextColumnId++, "Spieler", 100) {
                    @Override
                    public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {
                        var player = entry.getPlayer();
                        return new ColorLabelEntry(player.getFullName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT );
                    }
                },
                new TrainingColumn(nextColumnId++, "ls.player.skill", 100) {
                    @Override
                    public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {
                        var skillChange = entry.getSkillChange();
                        return new ColorLabelEntry(skillChange.getType().getLanguageString(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT );
                    }
                },
                new TrainingColumn(nextColumnId++, "TO", 100) {
                    @Override
                    public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {
                        var skillChange = entry.getSkillChange();
                        var text = PlayerAbility.getNameForSkill(skillChange.getValue(), true);
                        var ret = new ColorLabelEntry(text, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                        ret.setIcon(TrainingLegendPanel.getSkillupTypeIcon(entry.getSkillChange().getType(), entry.getSkillChange().getChange()));
                        return ret;
                    }
                }
                )).toArray(new TrainingColumn[0]);
    }

	public void setChanges(List<PlayerSkillChange> values) {
        this.values = values;
        initData();
    }

    @Override
    protected void initData() {
        if (this.values == null || this.values.isEmpty()) return;
        m_clData = new Object[this.values.size()][getDisplayedColumns().length];
        int rownum = 0;
        for (var skillChange : this.values) {
            int colnum = 0;
            for ( var col : getDisplayedColumns()){
                m_clData[rownum][colnum] = ((TrainingColumn)col).getTableEntry(skillChange);
                colnum++;
            }
            rownum++;
        }
        fireTableDataChanged();
    }
    public List<PlayerSkillChange> getValues() {
        return values;
    }

    @Override
    public int getModelIndex(@Nullable Player player) {
        if ( player != null){
            int i = 0;
            for (var change : this.values){
                if (change.getPlayer().getPlayerId() == player.getPlayerId()){
                    return i;
                }
                i++;
            }
        }
        return -1;
    }

    @Override
    public Player getPlayer(int index){
        if (index > -1 && index < this.values.size()){
            return this.values.get(index).getPlayer();
        }
        return null;
    }
}