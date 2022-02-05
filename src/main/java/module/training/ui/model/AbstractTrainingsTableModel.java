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
    private String[] o_ColumnNames;
    private TrainingType o_trainingType;


    /**
     * Creates a new AbstractTrainingsTableModel object.
     */
    public AbstractTrainingsTableModel(TrainingType _trainingType) {
        o_trainingType = _trainingType;
        o_Data = new Object[][]{};
        o_ColumnNames = new String[6];
        o_ColumnNames[0] = Helper.getTranslation("ls.youth.player.training.date");
        o_ColumnNames[1] = Helper.getTranslation("ls.team.trainingtype");
        o_ColumnNames[2] = Helper.getTranslation("ls.team.trainingintensity");
        o_ColumnNames[3] = Helper.getTranslation("ls.team.staminatrainingshare");
        o_ColumnNames[4] = Helper.getTranslation("ls.team.coachingskill");
        o_ColumnNames[5] = Helper.getTranslation("ls.module.statistics.club.assistant_trainers_level");
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

        TrainingPerWeek tpw;

        if(o_trainingType == TrainingType.PAST_TRAINING) {
            tpw = o_TrainingsPerWeek.get(getRowCount() - iRow - 1);
        }
        else{
            tpw = o_TrainingsPerWeek.get(iRow);
        }

        if (iCol == 1) {
            CBItem sel = (CBItem) value;
            tpw.setTrainingType(sel.getId());
        } else if (iCol == 2) {
            tpw.setTrainingIntensity((Integer) value);
        } else if (iCol == 3) {
            tpw.setStaminaPart((Integer) value);
        } else if (iCol == 4) {
            tpw.setCoachLevel((Integer) value);
        } else if (iCol == 5) {
            tpw.setTrainingAssistantLevel((Integer) value);
        }

        tpw.setSource(DBDataSource.MANUAL);
        fireTableCellUpdated(iRow, iCol);

        if (o_trainingType == TrainingType.PAST_TRAINING) {
            DBManager.instance().saveTraining(tpw, TrainingManager.instance().getLastTrainingDate(), true);
        } else{
            ((FutureTrainingsTableModel)this).getTrainingModel().saveFutureTraining(tpw);
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
        return (column > 0);
    }

    @Override
	public Class<?> getColumnClass(int column) {
        return getValueAt(0, column).getClass();
    }

    /**
     * Return number of columns
     *
     * @return
     */
    @Override
	public int getColumnCount() {
        return o_ColumnNames.length;
    }

    /**
     * Return header for the specified column
     *
     * @param column
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
     * @return
     */
    @Override
	public int getRowCount() {
        return (o_TrainingsPerWeek != null) ? o_TrainingsPerWeek.size() : 0;
    }

    /**
     * Returns the cell value
     *
     * @param row
     * @param column
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
    public abstract void populate(List<TrainingPerWeek> trainings);
}
