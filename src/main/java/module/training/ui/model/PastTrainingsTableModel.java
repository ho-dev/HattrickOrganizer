// %1126721451604:hoplugins.trainingExperience.ui.model%
package module.training.ui.model;

import core.constants.TrainingType;
import core.datatype.CBItem;
import core.db.DBManager;
import core.training.TrainingPerWeek;

import java.util.List;
import java.util.Vector;

/**
 * Customized table model for past trainings
 */
public class PastTrainingsTableModel extends AbstractTrainingsTableModel {

	private static final long serialVersionUID = -4741270987836161270L;

	/**
	 * When a value is updated, update the value in the right TrainingWeek in
	 * p_V_trainingsVector store the change thru HO API. Refresh the main table,
	 * and deselect any player
	 * 
	 * @param value
	 * @param row
	 * @param col
	 */
	@Override
	public void setValueAt(Object value, int row, int col) {
		Object[] aobj = (Object[]) p_V_data.get(row);

		aobj[col] = value;

		TrainingPerWeek train = this.trainings.get(p_V_data.size() - row - 1);
		if (col == 2) {
			CBItem sel = (CBItem) value;
			train.setTrainingType(sel.getId());
		} else if (col == 3) {
			Integer intense = (Integer) value;
			train.setTrainingIntensity(intense.intValue());
		} else if (col == 4) {
			Integer staminaTrainingPart = (Integer) value;
			train.setStaminaPart(staminaTrainingPart.intValue());
		}

		DBManager.instance().saveTraining((TrainingPerWeek) train);
		fireTableCellUpdated(row, col);
	}

	/**
	 * Populate the table with the old trainings week loaded from HO API
	 */
	@Override
	public void populate(List<TrainingPerWeek> trainings) {
		setTrainings(trainings);
		p_V_data = new Vector<Object[]>();

		if (this.trainings == null || this.trainings.isEmpty()) {
			return;
		}
		
		// for each training week
		for (TrainingPerWeek training : this.trainings) {
			Object[] aobj = (new Object[] {
					training.getHattrickWeek() + "",
					training.getHattrickSeason() + "",
					new CBItem(TrainingType.toString(training.getTrainingType()),
							training.getTrainingType()),
					new Integer(training.getTrainingIntensity()),
					new Integer(training.getStaminaPart()) });

			// add the data object into the table model
			p_V_data.add(0, aobj);
		}

		fireTableDataChanged();
	}
}
