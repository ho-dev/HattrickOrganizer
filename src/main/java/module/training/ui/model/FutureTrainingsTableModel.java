package module.training.ui.model;

import core.constants.TrainingType;
import core.datatype.CBItem;
import core.db.DBManager;
import core.training.TrainingPerWeek;
import java.util.List;


/**
 * Customized table model for future trainings
 */
public class FutureTrainingsTableModel extends AbstractTrainingsTableModel {

	private final TrainingModel trainingModel;

	public FutureTrainingsTableModel(TrainingModel trainingModel) {
		super();
		this.trainingModel = trainingModel;
	}



	/**
	 * Populate the table with the future training stored in the db, if not
	 * present, create it new and saves it
	 */
	@Override
	public void populate(List<TrainingPerWeek> trainings) {
		setTrainingsPerWeek(trainings);
		o_Data = new Object[getRowCount()][getColumnCount()];

		int iRow = 0;

		for (TrainingPerWeek tpw : this.o_TrainingsPerWeek) {
			o_Data[iRow][0] = cl_Formatter.format(tpw.getTrainingDate());
			o_Data[iRow][1] = new CBItem(TrainingType.toString(tpw.getTrainingType()),	tpw.getTrainingType());
			o_Data[iRow][2] = tpw.getTrainingIntensity();
			o_Data[iRow][3] = tpw.getStaminaShare();
			o_Data[iRow][4] = tpw.getCoachLevel();
			o_Data[iRow][5] = tpw.getTrainingAssistantsLevel();
			iRow ++;
		}

		fireTableDataChanged();

	}
}
