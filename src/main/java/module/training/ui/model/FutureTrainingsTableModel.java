// %638597353:hoplugins.trainingExperience.ui.model%
package module.training.ui.model;

import core.constants.TrainingType;
import core.datatype.CBItem;
import core.db.DBManager;
import core.training.TrainingPerWeek;

import java.util.List;
import java.util.Vector;

/**
 * Customized table model for future trainings
 */
public class FutureTrainingsTableModel extends AbstractTrainingsTableModel {

	private static final long serialVersionUID = 5448249533827333037L;	
	private final TrainingModel trainingModel;
	/**
	 * Creates a new FutureTrainingsTableModel object.
	 * 
	 * @param miniModel
	 */
	public FutureTrainingsTableModel(TrainingModel trainingModel) {
		super();
		this.trainingModel = trainingModel;
	}

	/**
	 * When a value is updated, update the value in the right TrainingWeek in
	 * p_V_trainingsVector store the change in the DB and then update the player
	 * prevision with the new training setting
	 * 
	 * @param value
	 * @param row
	 * @param col
	 */
	@Override
	public void setValueAt(Object value, int row, int col) {
		Object[] aobj = (Object[]) p_V_data.get(row);

		aobj[col] = value;

		TrainingPerWeek train = this.trainings.get(row);

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
		this.trainingModel.saveFutureTraining(train);
		fireTableCellUpdated(row, col);
	}

	/**
	 * Populate the table with the future training stored in the db, if not
	 * present, create it new and saves it
	 */
	@Override
	public void populate(List<TrainingPerWeek> trainings) {
		setTrainings(trainings);
		p_V_data = new Vector<Object[]>();
		List<TrainingPerWeek> futureTrainings = DBManager.instance().getFutureTrainingsVector();

		for (TrainingPerWeek training : this.trainings) {
			Object[] aobj = (new Object[]{
					training.getHattrickDate().getWeek() + "",
					training.getHattrickDate().getSeason() + "",
					new CBItem(TrainingType.toString(training.getTrainingType()),
							training.getTrainingType()),
							training.getTrainingIntensity(),
							training.getStaminaPart()});

			// Add object to be visualized to the table model
			p_V_data.add(aobj);
		}

		fireTableDataChanged();
	}
}
