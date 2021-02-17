package module.training.ui.model;

import core.constants.TrainingType;
import core.datatype.CBItem;
import core.db.DBManager;
import core.training.TrainingPerWeek;
import core.util.HTDatetime;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ListIterator;


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
		o_Data = new Object[getRowCount()][getColumnCount()];

		if (this.o_TrainingsPerWeek == null || this.o_TrainingsPerWeek.isEmpty()) {
			return;
		}

		int iRow = 0;

		// iterate backward through TrainingPerWeek to get more recent elements on top
		for (int i = o_TrainingsPerWeek.size(); i-- > 0; ) {
			var tpw = o_TrainingsPerWeek.get(i);
			o_Data[iRow][0] = HTDatetime.getLocalizedDateString(tpw.getTrainingDate(), true);
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
