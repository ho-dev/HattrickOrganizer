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


	private static DateTimeFormatter cl_Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.from(ZoneOffset.UTC));


	/**
	 * When a value is updated:
	 *      update the value in the DataModel
	 *      update the entry in Trainings table
	 *      refresh the table
	 */
	@Override
	public void setValueAt(Object value, int iRow, int iCol) {

		o_Data[iRow][iCol] = value;

		TrainingPerWeek tpw = o_TrainingsPerWeek.get(getRowCount() - iRow - 1);

		if (iCol == 2) {
			CBItem sel = (CBItem) value;
			tpw.setTrainingType(sel.getId());
		} else if (iCol == 3) {
			Integer intense = (Integer) value;
			tpw.setTrainingIntensity(intense.intValue());
		} else if (iCol == 4) {
			Integer staminaTrainingPart = (Integer) value;
			tpw.setStaminaPart(staminaTrainingPart.intValue());
		}

		DBManager.instance().saveTraining(tpw, true);
		fireTableCellUpdated(iRow, iCol);
	}

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
