package module.training.ui.model;

import core.datatype.CBItem;
import core.db.DBManager;
import core.gui.RefreshManager;
import core.model.enums.DBDataSource;
import core.training.TrainingManager;
import core.training.TrainingPerWeek;
import core.util.Helper;
import module.training.TrainingType;
import java.util.List;
import javax.swing.table.AbstractTableModel;


/**
 * Basic training table model
 */
public abstract class AbstractTrainingsTableModel extends AbstractTableModel {

	protected List<TrainingPerWeek> o_TrainingsPerWeek;
    protected Object[][]o_Data;
    private final String[] o_ColumnNames;
    private final TrainingType o_trainingType;


    /**
     * Creates a new AbstractTrainingsTableModel object.
     */
    public AbstractTrainingsTableModel(TrainingType _trainingType) {
        o_trainingType = _trainingType;
        o_Data = new Object[][]{};
        o_ColumnNames = new String[]{
                Helper.getTranslation("ls.youth.player.training.date"),
                Helper.getTranslation("Season"),
                Helper.getTranslation("Week"),
                Helper.getTranslation("ls.team.trainingtype"),
                Helper.getTranslation("ls.team.trainingintensity"),
                Helper.getTranslation("ls.team.staminatrainingshare"),
                Helper.getTranslation("ls.team.coachingskill"),
                Helper.getTranslation("ls.module.statistics.club.assistant_trainers_level")
        };
    }

    public void setTrainingsPerWeek(List<TrainingPerWeek> trainingsPerWeek) {
    	this.o_TrainingsPerWeek = trainingsPerWeek;
    }

    /**
     * When a value is updated:
     *      update the value in the DataModel
     *      update the entry in Trainings table
     *      refresh the table
     */
    public void setValueAt(Object value, int iRow, int iCol) {
        o_Data[iRow][iCol] = value;
        TrainingPerWeek tpw = o_TrainingsPerWeek.get(iRow);
        switch (iCol) {
            case 3 -> {
                CBItem sel = (CBItem) value;
                tpw.setTrainingType(sel.getId());
            }
            case 4 -> tpw.setTrainingIntensity((Integer) value);
            case 5 -> tpw.setStaminaPart((Integer) value);
            case 6 -> tpw.setCoachLevel((Integer) value);
            case 7 -> tpw.setTrainingAssistantLevel((Integer) value);
        }

        tpw.setSource(DBDataSource.MANUAL);
        fireTableCellUpdated(iRow, iCol);

        if (o_trainingType == TrainingType.PAST_TRAINING) {
            DBManager.instance().saveTraining(tpw, TrainingManager.instance().getLastTrainingDate());
        } else {
            ((FutureTrainingsTableModel) this).getTrainingModel().saveFutureTraining(tpw);
            RefreshManager.instance().doRefresh();
        }
    }

    //TODO: isCellEditable() make sure that cells older than 2 seasons are not editable
    /**
     * Cells that are editable should be less than 2 seasons old (otherwise it could be misleading as they won't be considered in skill recalculation)
     * also first column is not editable
     */
    @Override
	public boolean isCellEditable(int row, int column) {
        return (column > 2);
    }

    @Override
	public Class<?> getColumnClass(int column) {
        return getValueAt(0, column).getClass();
    }

    /**
     * Return number of columns
     *
     * @return int
     */
    @Override
	public int getColumnCount() {
        return o_ColumnNames.length;
    }

    /**
     * Return header for the specified column
     *
     * @param column index of column
     *
     * @return column header
     */
    @Override
	public String getColumnName(int column) {
        return o_ColumnNames[column];
    }

    /**
     * Returns row number
     *
     * @return int
     */
    @Override
	public int getRowCount() {
        return (o_TrainingsPerWeek != null) ? o_TrainingsPerWeek.size() : 0;
    }

    /**
     * Returns the cell value
     *
     * @param row index
     * @param column index
     *
     * @return Object representing the cell value
     */
    @Override
	public Object getValueAt(int row, int column) {
        if (o_Data != null) {
            return o_Data[row][column];
        }
        return null;
    }

    /**
     * Method to be called to populate the table with the data from HO API
     */
    public void populate(List<TrainingPerWeek> trainings) {
        setTrainingsPerWeek(trainings);
        o_Data = new Object[getRowCount()][getColumnCount()];

        if (this.o_TrainingsPerWeek == null || this.o_TrainingsPerWeek.isEmpty()) {
            return;
        }

        int iRow = 0;

        for (TrainingPerWeek tpw : this.o_TrainingsPerWeek) {

            var trainingDate = tpw.getTrainingDate();
            var trainingWeek = trainingDate.toLocaleHTWeek();
            o_Data[iRow][0] = trainingDate.toLocaleDateTime();
            o_Data[iRow][1] = trainingWeek.season;
            o_Data[iRow][2] = trainingWeek.week;
            o_Data[iRow][3] = new CBItem(core.constants.TrainingType.toString(tpw.getTrainingType()),	tpw.getTrainingType());
            o_Data[iRow][4] = tpw.getTrainingIntensity();
            o_Data[iRow][5] = tpw.getStaminaShare();
            o_Data[iRow][6] = tpw.getCoachLevel();
            o_Data[iRow][7] = tpw.getTrainingAssistantsLevel();
            iRow ++;
        }

        fireTableDataChanged();
    }
}
