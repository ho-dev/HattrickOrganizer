package module.training.ui.model;

import core.datatype.CBItem;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.model.UserColumnController;
import core.training.TrainingPerWeek;
import core.util.HODateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Basic training table model
 */
public class TrainingSettingsTableModel extends HOTableModel {

    private static final int COLUMN_INDEX_INTENSITY = 4;
    private static final int COLUMN_INDEX_STAMINA_SHARE = 5;
    private static final int COLUMN_INDEX_COACH_LEVEL = 6;
    private static final int COLUMN_INDEX_ASSISTANT_LEVEL = 7;
    private static int nextId=0;
    protected List<TrainingPerWeek> o_TrainingsPerWeek;
    protected TrainingModel trainingModel;

    /**
     * Creates a new AbstractTrainingsTableModel object.
     */
    public TrainingSettingsTableModel(UserColumnController.ColumnModelId columnModelId, String name) {
        super(columnModelId, name);

        columns = new ArrayList<>(List.of(
                // 0
                new TrainingColumn(nextId++, "ls.youth.player.training.date", 150) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        return new ColorLabelEntry(entry.getTrainingDate(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }

                    @Override
                    public boolean canBeDisabled() {
                        return false;
                    }
                },
                // 1
                new TrainingColumn(nextId++, "Season", 60) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        var trainingDate = entry.getTrainingDate();
                        return new ColorLabelEntry(HODateTime.toEpochSecond(trainingDate), String.valueOf(trainingDate.toLocaleHTWeek().season), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                // 2
                new TrainingColumn(nextId++, "Week", 60) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        var trainingDate = entry.getTrainingDate();
                        return new ColorLabelEntry(HODateTime.toEpochSecond(trainingDate), String.valueOf(trainingDate.toLocaleHTWeek().week), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                // 3
                new TrainingColumn(nextId++, "ls.team.trainingtype", 140) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        return new ColorLabelEntry(core.constants.TrainingType.toString(entry.getTrainingType()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                    @Override
                    public boolean isEditable() {return true;}
                },
                // 4
                new TrainingColumn(nextId++, "ls.team.trainingintensity", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        return new ColorLabelEntry(entry.getTrainingIntensity(), String.valueOf(entry.getTrainingIntensity()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                    @Override
                    public boolean isEditable() {return true;}
                },
                // 5
                new TrainingColumn(nextId++, "ls.team.staminatrainingshare", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        return new ColorLabelEntry(entry.getStaminaShare(), String.valueOf(entry.getStaminaShare()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                    @Override
                    public boolean isEditable() {return true;}
                },
                // 6
                new TrainingColumn(nextId++, "ls.team.coachingskill", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        return new ColorLabelEntry(entry.getCoachLevel(), getCoachLevelDisplayText(entry.getCoachLevel()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                    @Override
                    public boolean isEditable() {return true;}
                },
                // 7
                new TrainingColumn(nextId++, "ls.module.statistics.club.assistant_trainers_level", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
                        return new ColorLabelEntry(entry.getTrainingAssistantsLevel(), String.valueOf(entry.getTrainingAssistantsLevel()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                    @Override
                    public boolean isEditable() {return true;}
                }
        )).toArray(new TrainingColumn[0]);
    }

    private String getCoachLevelDisplayText(int coachSkillLevel) {
        return String.valueOf(coachSkillLevel-3);    // transform from skill level to the new specialist level.
    }

    private void setTrainingsPerWeek(List<TrainingPerWeek> trainingsPerWeek) {
    	this.o_TrainingsPerWeek = trainingsPerWeek;
    }

    public void setTrainingModel(TrainingModel trainingModel) {
        this.trainingModel = trainingModel;
    }

    /**
     * Method to be called to populate the table with the data from HO API
     */
    public void setTrainingSettings(List<TrainingPerWeek> trainings) {
        setTrainingsPerWeek(trainings);
        initData();
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

    /**
     * Move the table values to the model objects
     * @param row       Model row index
     * @param column    Model column index
     * @return          Modified training settings entry
     *                  or null if no value is changed
     */
    public TrainingPerWeek getEditedEntry(int row, int column) {
        if ( row >= 0 && row < this.o_TrainingsPerWeek.size() && column >= 3 && column < 8){
            var entry = this.o_TrainingsPerWeek.get(row);
            var value = this.getValueAt(row, column);
            if ( value != null) {
                if ( value instanceof CBItem cbItem )
                   entry.setTrainingType(cbItem.getId());
                else {
                    switch (column) {
                        case COLUMN_INDEX_INTENSITY:
                            entry.setTrainingIntensity((int) value);
                            break;
                        case COLUMN_INDEX_STAMINA_SHARE:
                            entry.setStaminaShare((int) value);
                            break;
                        case COLUMN_INDEX_COACH_LEVEL:
                            entry.setCoachLevel((int) value + 3);
                            break;
                        case COLUMN_INDEX_ASSISTANT_LEVEL:
                            entry.setTrainingAssistantLevel((int) value);
                            break;
                        default:
                            return null;
                    }
                }
                return entry;
            }
        }
        return null; // No value edited
    }
}
