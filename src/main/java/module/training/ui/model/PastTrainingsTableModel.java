package module.training.ui.model;

import core.constants.TrainingType;
import core.datatype.CBItem;
import core.training.TrainingPerWeek;

import java.util.List;


/**
 * Customized table model for past trainings
 */
public class PastTrainingsTableModel extends AbstractTrainingsTableModel {


	public PastTrainingsTableModel() {
		super(module.training.TrainingType.PAST_TRAINING);
	}


}
