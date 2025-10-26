package module.training.ui;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.model.UserColumnController;
import core.util.HODateTime;
import module.training.EffectDAO;
import module.training.TrainWeekEffect;
import module.training.ui.model.TrainingColumn;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel representing the effect of training.
 *
 * @author NetHyperon
 */
public class EffectTableModel extends HOTableModel {

    /**
     * Creates a new EffectTableModel object.
     *
     * @param columnModelId UserColumnController.ColumnModelId.
     */
    public EffectTableModel(UserColumnController.ColumnModelId columnModelId) {
        super(columnModelId, "ls.module.training.effect");

        columns = new ArrayList<>(List.of(
                new TrainingColumn("Season", 60) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainWeekEffect entry) {
                        var htWeek = new HODateTime.HTWeek(entry.getHattrickSeason(), entry.getHattrickWeek());
                        var trainingDate = HODateTime.fromHTWeek(htWeek);
                        return new ColorLabelEntry(HODateTime.toEpochSecond(trainingDate), String.valueOf(trainingDate.toLocaleHTWeek().season), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("Week", 60) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainWeekEffect entry) {
                        var htWeek = new HODateTime.HTWeek(entry.getHattrickSeason(), entry.getHattrickWeek());
                        var trainingDate = HODateTime.fromHTWeek(htWeek);
                        return new ColorLabelEntry(HODateTime.toEpochSecond(trainingDate), String.valueOf(trainingDate.toLocaleHTWeek().week), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("TotalTSI", 60) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainWeekEffect entry) {
                        return new ColorLabelEntry(entry.getTotalTSI(), String.valueOf(entry.getTotalTSI()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("AverageTSI", 60) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainWeekEffect entry) {
                        return new ColorLabelEntry(entry.getAverageTSI(), String.valueOf(entry.getAverageTSI()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("ls.player.tsi", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainWeekEffect entry) {
                        return new ColorLabelEntry(entry.getTSIIncrease(), String.format("+%d / %d", entry.getTSIIncrease(), entry.getTSIDecrease()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("DurchschnittForm", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainWeekEffect entry) {
                        return new ColorLabelEntry(entry.getAverageForm(), String.format("%.2f", entry.getAverageForm()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("ls.player.form", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainWeekEffect entry) {
                        return new ColorLabelEntry(entry.getFormIncrease(), String.format("+%d / %d", entry.getFormIncrease(), entry.getFormDecrease()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("Skillups", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainWeekEffect entry) {
                        var icon  = TrainingLegendPanel.getSkillupTypeIcon(entry.getTrainingType(), entry.getAmountSkillups());
                        return new ColorLabelEntry(icon, entry.getAmountSkillups(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                }
        )).toArray(new TrainingColumn[0]);

    }

    @Override
    protected void initData() {
        var values = EffectDAO.getTrainEffect();
        m_clData = new Object[values.size()][columns.length];
        int rownum=0;
        for ( var effects: values) {
            int column=0;
            for ( var col : getDisplayedColumns()){
                if ( col instanceof  TrainingColumn trainingColumn) {
                    m_clData[rownum][column] = trainingColumn.getTableEntry(effects);
                }
                column++;
            }
            rownum++;
        }
        fireTableDataChanged();
    }
}
