// %1126721451244:hoplugins.trainingExperience.ui.component%
package module.training.ui.comp;

import core.constants.TrainingType;

import javax.swing.JComboBox;


/**
 * ComboBox to edit the TrainingType
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 * Seb04 - Simplified and removed General and STamina training.
 */
public class TrainingComboBox extends JComboBox {
	private static final long serialVersionUID = 303608674207819922L;
	/**
     * Creates a new TrainingComboBox object.
     */
    public TrainingComboBox() {
        super(TrainingType.ITEMS);
//        for (int i = ITeam.TA_STANDARD; i <= ITeam.TA_EXTERNALATTACK;  i++)
//        {
//        	addItem(new CBItem(Trainings.getTrainingDescription(i), i));
//        }
    }
}
