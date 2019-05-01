// %1956788941:hoplugins.trainingExperience.ui.component%
package module.training.ui.comp;

import core.constants.TrainingType;
import core.datatype.CBItem;
import core.db.DBManager;
import core.gui.RefreshManager;
import core.model.HOVerwaltung;
import core.training.TrainingPerWeek;
import module.training.ui.model.FutureTrainingsTableModel;
import module.training.ui.model.TrainingModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;


/**
 * Panel for Settings all the future training week
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class FutureSettingPanel extends JPanel {

	private static final long serialVersionUID = 4872598003436712955L;
	private FutureTrainingsTableModel futureModel;
    private JComboBox intensity;
    private JComboBox staminaTrainingPart;
    private JComboBox training;
    private final TrainingModel model;

    /**
     * Creates a new FutureSettingPanel object.
     *
     * @param fm The futureTraining table model, used to update it when needed
     */
    public FutureSettingPanel(TrainingModel model, FutureTrainingsTableModel fm) {
        super();
        this.model = model;
        futureModel = fm;
        jbInit();
    }

    /**
     * Populate the Future training table with the future training
     */
    protected void resetFutureTrainings() {
        List<TrainingPerWeek> futureTrainingsToSave = new ArrayList<TrainingPerWeek>();

        for (TrainingPerWeek train: this.model.getFutureTrainings()) {
            train.setTrainingIntensity(intensity.getSelectedIndex());
            train.setStaminaPart(staminaTrainingPart.getSelectedIndex() + 5);
            train.setTrainingType(((CBItem)training.getSelectedItem()).getId());
            futureTrainingsToSave.add(train);
        }

        this.model.saveFutureTrainings(futureTrainingsToSave);
        futureModel.populate(this.model.getFutureTrainings());
        RefreshManager.instance().doRefresh();
    }

    /**
     * Initializes the state of this instance.
     */
    private void jbInit() {

        List<TrainingPerWeek> futureTrainings =  DBManager.instance().getFutureTrainingsVector();
        TrainingPerWeek firstFutureTraining = futureTrainings.get(0);
        training = new TrainingComboBox();
        final int ttyp = firstFutureTraining.getTrainingType();
        training.setSelectedItem(new CBItem(TrainingType.toString(ttyp), ttyp));
        intensity = new IntensityComboBox(0);
        intensity.setSelectedIndex(firstFutureTraining.getTrainingIntensity());
        staminaTrainingPart = new IntensityComboBox(5);
        staminaTrainingPart.setSelectedIndex(firstFutureTraining.getStaminaPart() - 5);

        JButton button = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.apply")); //$NON-NLS-1$

        button.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent arg0) {
                    resetFutureTrainings();
                }
            });
        add(training);
        add(intensity);
        add(staminaTrainingPart);
        add(button);
    }
}
