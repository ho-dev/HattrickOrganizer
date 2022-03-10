package module.training.ui.model;

import core.constants.TrainingType;
import core.datatype.CBItem;
import core.training.TrainingPerWeek;


import java.util.List;


/**
 * Customized table model for future trainings
 */
public class FutureTrainingsTableModel extends AbstractTrainingsTableModel {


	private final TrainingModel trainingModel;

	public FutureTrainingsTableModel(TrainingModel _trainingModel) {
		super(module.training.TrainingType.FUTURE_TRAINING);
		trainingModel = _trainingModel;
	}

	public TrainingModel getTrainingModel() {
		return trainingModel;
	}

}
