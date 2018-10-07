// %1126721451323:hoplugins.trainingExperience.ui.model%
package module.training.ui.model;

import core.model.HOVerwaltung;
import core.training.TrainingPerWeek;

import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;


/**
 * Basic training table model
 */
public abstract class AbstractTrainingsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 5399479264645517270L;
	protected List<TrainingPerWeek> trainings;
    /** Vector of ITrainingPerPlayer object */
    protected Vector<Object[]> p_V_data;
    private String[] columnNames;


    /**
     * Creates a new AbstractTrainingsTableModel object.
     *
     * @param miniModel
     */
    public AbstractTrainingsTableModel() {
        p_V_data = new Vector<Object[]>();
        columnNames = new String[5];
        HOVerwaltung hoV = HOVerwaltung.instance();
        columnNames[0] = hoV.getLanguageString("Week");
        columnNames[1] = hoV.getLanguageString("Season");
        columnNames[2] = hoV.getLanguageString("ls.team.trainingtype");
        columnNames[3] = hoV.getLanguageString("ls.team.trainingintensity");
        columnNames[4] = hoV.getLanguageString("ls.team.staminatrainingshare");
    }

    public void setTrainings(List<TrainingPerWeek> trainings) {
    	this.trainings = trainings;
    }
    
    /**
     * Method that returns if a cell is editable or not
     *
     * @param row
     * @param column
     *
     * @return
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
        return columnNames.length;
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
        return columnNames[column];
    }

    /**
     * Returns row number
     *
     * @return
     */
    @Override
	public int getRowCount() {
        return p_V_data.size();
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
        Object[] aobj = (Object[]) p_V_data.get(row);

        return aobj[column];
    }

    /**
     * Method to be called to populate the table with the data from HO API
     */
    public abstract void populate(List<TrainingPerWeek> trainings);
}
