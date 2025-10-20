package module.training.ui.model;

import core.datatype.CBItem;
import core.db.DBManager;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.model.UserColumnController;
import core.model.enums.DBDataSource;
import core.training.TrainingManager;
import core.training.TrainingPerWeek;
import core.util.HODateTime;
import module.training.TrainingType;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;


/**
 * Basic training table model
 */
public class TrainingSettingsTableModel extends HOTableModel {

	protected List<TrainingPerWeek> o_TrainingsPerWeek;
//    protected Object[][]o_Data;
//    private final String[] o_ColumnNames;
//    private final TrainingType o_trainingType;


    /**
     * Creates a new AbstractTrainingsTableModel object.
     */
    public TrainingSettingsTableModel(UserColumnController.ColumnModelId columnModelId, String name) {
        super(columnModelId, name);

//        o_trainingType = _trainingType;
//        o_Data = new Object[][]{};
//        o_ColumnNames = new String[]{
//                Helper.getTranslation("ls.youth.player.training.date"),
//                Helper.getTranslation("Season"),
//                Helper.getTranslation("Week"),
//                Helper.getTranslation("ls.team.trainingtype"),
//                Helper.getTranslation("ls.team.trainingintensity"),
//                Helper.getTranslation("ls.team.staminatrainingshare"),
//                Helper.getTranslation("ls.team.coachingskill"),
//                Helper.getTranslation("ls.module.statistics.club.assistant_trainers_level")
//        };

        columns = new ArrayList<>(List.of(
                new TrainingColumn("ls.youth.player.training.date", 150) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        return new ColorLabelEntry(entry.getTrainingDate(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }

                    @Override
                    public boolean canBeDisabled() {
                        return false;
                    }
                },
                new TrainingColumn("Season", 60) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        var trainingDate = entry.getTrainingDate();
                        return new ColorLabelEntry(HODateTime.toEpochSecond(trainingDate), String.valueOf(trainingDate.toLocaleHTWeek().season), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("Week", 60) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        var trainingDate = entry.getTrainingDate();
                        return new ColorLabelEntry(HODateTime.toEpochSecond(trainingDate), String.valueOf(trainingDate.toLocaleHTWeek().week), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("s.team.trainingtype", 140) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        return new ColorLabelEntry(core.constants.TrainingType.toString(entry.getTrainingType()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("ls.team.trainingintensity", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        return new ColorLabelEntry(entry.getTrainingIntensity(), String.valueOf(entry.getTrainingIntensity()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("ls.team.staminatrainingshare", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        return new ColorLabelEntry(entry.getStaminaShare(), String.valueOf(entry.getStaminaShare()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("ls.team.coachingskill", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        return new ColorLabelEntry(entry.getCoachLevel(), getCoachLevelDisplayText(entry.getCoachLevel()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("ls.module.statistics.club.assistant_trainers_level", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        return new ColorLabelEntry(entry.getTrainingAssistantsLevel(), String.valueOf(entry.getCoachLevel()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                }
        )).toArray(new TrainingColumn[0]);

    }

    private String getCoachLevelDisplayText(int coachSkillLevel) {
        return String.valueOf(coachSkillLevel-3);    // transform from skill level to the new specialist level.
    }

    private void setTrainingsPerWeek(List<TrainingPerWeek> trainingsPerWeek) {
    	this.o_TrainingsPerWeek = trainingsPerWeek;
    }

//    /**
//     * Cells that are editable should be less than 2 seasons old (otherwise it could be misleading as they won't be considered in skill recalculation)
//     * also first column is not editable
//     */
//    @Override
//	public boolean isCellEditable(int row, int column) {
//        return (column > 2);
//    }
//
//    @Override
//	public Class<?> getColumnClass(int column) {
//        return getValueAt(0, column).getClass();
//    }
//
//    /**
//     * Return number of columns
//     *
//     * @return int
//     */
//    @Override
//	public int getColumnCount() {
//        return o_ColumnNames.length;
//    }
//
//    /**
//     * Return header for the specified column
//     *
//     * @param column index of column
//     *
//     * @return column header
//     */
//    @Override
//	public String getColumnName(int column) {
//        return o_ColumnNames[column];
//    }
//
//    /**
//     * Returns row number
//     *
//     * @return int
//     */
//    @Override
//	public int getRowCount() {
//        return (o_TrainingsPerWeek != null) ? o_TrainingsPerWeek.size() : 0;
//    }

    /**
     * Method to be called to populate the table with the data from HO API
     */
    public void setTrainingSettings(List<TrainingPerWeek> trainings) {
        setTrainingsPerWeek(trainings);
        fireTableDataChanged();
    }

    @Override
    protected void initData() {
        m_clData = new Object[this.o_TrainingsPerWeek.size()][getDisplayedColumns().length];
        int rownum = 0;
        for (var trainingSetting : this.o_TrainingsPerWeek) {
            int column = 0;
            for ( var col : getDisplayedColumns()){
                if ( col instanceof  TrainingColumn trainingColumn) {
                    m_clData[rownum][column] = trainingColumn.getTableEntry(trainingSetting);
                }
                column++;
            }
            rownum++;
        }
        fireTableDataChanged();
    }
}
