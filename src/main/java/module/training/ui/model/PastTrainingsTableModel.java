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

	/**
	 * Populate the table with the content of TRAININGS table
	 */
	@Override
	public void populate(List<TrainingPerWeek> trainings) {
		setTrainingsPerWeek(trainings);
		o_Data = new Object[getRowCount()][getColumnCount()];

		if (this.o_TrainingsPerWeek == null || this.o_TrainingsPerWeek.isEmpty()) {
			return;
		}

		int iRow = 0;

		// iterate backward through TrainingPerWeek to get more recent elements on top
		for (int i = o_TrainingsPerWeek.size(); i-- > 0; ) {
			var tpw = o_TrainingsPerWeek.get(i);
			o_Data[iRow][0] = tpw.getTrainingDate();
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
