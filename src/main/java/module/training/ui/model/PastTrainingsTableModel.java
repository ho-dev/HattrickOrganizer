package module.training.ui.model;

import core.constants.TrainingType;
import core.datatype.CBItem;
import core.db.DBManager;
import core.training.TrainingPerWeek;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * Customized table model for past trainings
 */
public class PastTrainingsTableModel extends AbstractTrainingsTableModel {



	/**
	 * Populate the table with the content of TRAININGS table
	 */
	@Override
	public void populate(List<TrainingPerWeek> trainings) {
		setTrainingsPerWeek(trainings);
		o_Data = new Object[][]{};

		if (this.o_TrainingsPerWeek == null || this.o_TrainingsPerWeek.isEmpty()) {
			return;
		}

		int iRow = 0;

		// iterate each TrainingPerWeek
		for (TrainingPerWeek tpw : o_TrainingsPerWeek) {
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
