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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel representing the effect of training.
 *
 * @author NetHyperon
 */
public class EffectTableModel extends HOTableModel {
//    private static final NumberFormat FORMATTER = NumberFormat.getInstance();
//    private List<TrainWeekEffect> values;
//    private final String[] colNames = new String[9];

    /**
     * Creates a new EffectTableModel object.
     *
     * @param columnModelId UserColumnController.ColumnModelId.
     */
    public EffectTableModel(UserColumnController.ColumnModelId columnModelId) {
        super(columnModelId, "training.effect");
//        FORMATTER.setMaximumFractionDigits(2);
//        FORMATTER.setMinimumFractionDigits(2);
//        this.colNames[0] = TranslationFacility.tr("Week"); //$NON-NLS-1$
//        this.colNames[1] = TranslationFacility.tr("Season"); //$NON-NLS-1$
//        this.colNames[2] = TranslationFacility.tr("TotalTSI"); //$NON-NLS-1$
//        this.colNames[3] = TranslationFacility.tr("AverageTSI"); //$NON-NLS-1$
//        this.colNames[4] = TranslationFacility.tr("ls.player.tsi") + " +/-"; //$NON-NLS-1$ //$NON-NLS-2$
//        this.colNames[5] = TranslationFacility.tr("DurchschnittForm"); //$NON-NLS-1$
//        this.colNames[6] = TranslationFacility.tr("ls.player.form") + " +/-"; //$NON-NLS-1$ //$NON-NLS-2$
//        this.colNames[7] = TranslationFacility.tr("Skillups"); //$NON-NLS-1$
//        this.colNames[8] = TranslationFacility.tr("ls.player.skill"); //$NON-NLS-1$
//        this.values = values;

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
                        return new ColorLabelEntry(entry.getTSIIncrease(), String.format("+%d/-%d", entry.getTSIIncrease(), entry.getTSIDecrease()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("DurchschnittForm", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainWeekEffect entry) {
                        return new ColorLabelEntry(entry.getAverageForm(), String.valueOf(entry.getAverageForm()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("ls.player.form", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainWeekEffect entry) {
                        return new ColorLabelEntry(entry.getFormIncrease(), String.format("+%d/-%d", entry.getFormIncrease(), entry.getFormDecrease()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
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

//    public void setValues(List<TrainWeekEffect> values) {
//        this.values = values;
//    }
//
//    /**
//     * @see javax.swing.table.TableModel#getColumnCount()
//     */
//    public int getColumnCount() {
//        return colNames.length;
//    }
//
//    /**
//     * @see javax.swing.table.TableModel#getColumnName(int)
//     */
//    @Override
//	public String getColumnName(int column) {
//        return colNames[column];
//    }
//
//    /**
//     * @see javax.swing.table.TableModel#getRowCount()
//     */
//    public int getRowCount() {
//        return values.size();
//    }
//
//    /**
//     * @see javax.swing.table.TableModel#getValueAt(int, int)
//     */
//    public Object getValueAt(int rowIndex, int columnIndex) {
//        TrainWeekEffect effect = values.get(rowIndex);
//
//        return switch (columnIndex) {
//            case 0 -> Integer.toString(effect.getHattrickWeek());
//            case 1 -> Integer.toString(effect.getHattrickSeason());
//            case 2 -> Integer.toString(effect.getTotalTSI());
//            case 3 -> Integer.toString(effect.getAverageTSI());
//            case 4 -> "+" + effect.getTSIIncrease() + " / " //$NON-NLS-1$ //$NON-NLS-2$
//                    + effect.getTSIDecrease();
//            case 5 -> FORMATTER.format(effect.getAverageForm());
//            case 6 -> "+" + effect.getFormIncrease() + " / " //$NON-NLS-1$ //$NON-NLS-2$
//                    + effect.getFormDecrease();
//            case 7 -> Integer.toString(effect.getAmountSkillups());
//            case 8 -> effect.getTrainingType();
//            default -> ""; //$NON-NLS-1$
//        };
//    }

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
