package module.training.ui.model;

import core.constants.player.PlayerAbility;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.model.UserColumnController;
import core.model.TranslationFacility;
import core.util.HODateTime;
import module.training.PlayerSkillChange;
import module.training.ui.TrainingLegendPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.awt.SystemColor.text;

/**
 * TableModel representing skill changes for individual players.
 * 
 * @author NetHyperon
 */
public class ChangesTableModel extends HOTableModel {


    //	public static final int COL_PLAYER_ID = 6;
	private List<PlayerSkillChange> values;
//	private final String[] colNames = new String[7];

    static int nextColumnId = 0;

    public ChangesTableModel(UserColumnController.@NotNull ColumnModelId id) {
        super(id, "TrainingAnalysis");
        columns = new ArrayList<>(List.of(
                new TrainingColumn(nextColumnId++, "Week", 50) {
                    @Override
                    public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {
                        var skillChange = entry.getSkillChange();
                        return new ColorLabelEntry(HODateTime.toEpochSecond(skillChange.getDate()), String.valueOf(skillChange.getHtWeek()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT );
                    }
                },
                new TrainingColumn(nextColumnId++, "Season", 50) {
                    @Override
                    public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {
                        var skillChange = entry.getSkillChange();
                        return new ColorLabelEntry(HODateTime.toEpochSecond(skillChange.getDate()), String.valueOf(skillChange.getHtSeason()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT );
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

//		super();
//		this.colNames[0] = TranslationFacility.tr("Week");
//		this.colNames[1] = TranslationFacility.tr("Season");
//		this.colNames[2] = TranslationFacility.tr("Spieler");
//		this.colNames[3] = TranslationFacility.tr("ls.player.skill");
//		this.colNames[4] = TranslationFacility.tr("TO");
//		this.colNames[5] = "isOld";
//		this.colNames[COL_PLAYER_ID] = "playerId";
//
//		this.values = values;
//	}

//	/**
//	 * @see javax.swing.table.TableModel#getColumnCount()
//	 */
//	@Override
//	public int getColumnCount() {
//		return colNames.length;
//	}
//
//	/**
//	 * @see javax.swing.table.TableModel#getColumnName(int)
//	 */
//	@Override
//	public String getColumnName(int column) {
//		return colNames[column];
//	}
//
//	/**
//	 * @see javax.swing.table.TableModel#getRowCount()
//	 */
//	@Override
//	public int getRowCount() {
//		return values.size();
//	}
//
//	/**
//	 * @see javax.swing.table.TableModel#getValueAt(int, int)
//	 */
//	@Override
//	public Object getValueAt(int rowIndex, int columnIndex) {
//		PlayerSkillChange change = values.get(rowIndex);
//		return switch (columnIndex) {
//            case 0 -> Integer.toString(change.getSkillChange().getHtWeek());
//            case 1 -> Integer.toString(change.getSkillChange().getHtSeason());
//            case 2 -> change.getPlayer().getFullName();
//            case 3 -> change.getSkillChange().getType().toInt();
//            case 4 -> PlayerAbility.getNameForSkill(change.getSkillChange().getValue(), true);
//            case 5 -> change.getPlayer().isGoner();
//            case COL_PLAYER_ID -> Integer.toString(change.getPlayer().getPlayerId());
//            default -> "";
//        };
//	}
}