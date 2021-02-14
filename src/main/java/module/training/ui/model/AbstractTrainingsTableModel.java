package module.training.ui.model;

import core.training.TrainingPerWeek;
import core.util.Helper;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;


/**
 * Basic training table model
 */
public abstract class AbstractTrainingsTableModel extends AbstractTableModel {


	protected List<TrainingPerWeek> o_TrainingsPerWeek;
    protected Object[][]o_Data;
    private String[] o_ColumnNames;


    /**
     * Creates a new AbstractTrainingsTableModel object.
     */
    public AbstractTrainingsTableModel() {
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


    //TODO: isCellEditable() make sure that cells older than 2 seasons are not editable
    /**
     * Cells that are editable should be less than 2 seasons old (otherwise it could be misleading as they won't be considered in skill recalculation)
     * also first column is not editable
     */
    @Override
	public boolean isCellEditable(int row, int column) {
        return (column == 2 || column == 3 || column == 4);
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
        return (o_Data != null) ? o_Data.length : 0;
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
