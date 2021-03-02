package module.training.ui.comp;

import core.datatype.CBItem;
import core.gui.RefreshManager;
import core.model.HOVerwaltung;
import core.model.constants.TrainingConstants;
import core.model.enums.DBDataSource;
import core.training.TrainingPerWeek;
import core.util.Helper;
import module.training.ui.model.FutureTrainingsTableModel;
import module.training.ui.model.TrainingModel;
import java.awt.*;
import static module.lineup.LineupPanel.TITLE_FG;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;


/**
 * Panel for editing selected future training week
 */
public class FutureTrainingsEditionPanel extends JPanel {

	private FutureTrainingsTableModel m_FutureTrainingsTableModel;
    private JComboBox m_jcbIntensity;
    private JComboBox m_jcbStaminaTrainingPart;
    private JComboBox m_jcbTrainingType;
    private JComboBox m_jcbCoachSkillEditor;
    private JComboBox m_jcbAssitantsTotalLevelEditor;
    private Set m_selectedTrainingDates;
    private final TrainingModel m_TrainingModel;


    public FutureTrainingsEditionPanel(TrainingModel _TrainingModel, FutureTrainingsTableModel fm, ListSelectionModel lsm) {
        setLayout(new BorderLayout());
        m_TrainingModel = _TrainingModel;
        m_FutureTrainingsTableModel = fm;
        m_selectedTrainingDates = new HashSet();
        for (var i : lsm.getSelectedIndices()){
            TrainingPerWeek tpw = _TrainingModel.getFutureTrainings().get(i);
            m_selectedTrainingDates.add(tpw.getTrainingDate());
        }
        initComponents();
    }


    public FutureTrainingsEditionPanel(TrainingModel _TrainingModel, FutureTrainingsTableModel fm) {
        setLayout(new BorderLayout());
        m_selectedTrainingDates = null;
        m_TrainingModel = _TrainingModel;
        m_FutureTrainingsTableModel = fm;
        initComponents();
    }


    protected void setFutureTrainings() {

        if((m_jcbTrainingType.getSelectedItem() == null) && (m_jcbIntensity.getSelectedItem() == null) &&
           (m_jcbStaminaTrainingPart.getSelectedItem() == null) && (m_jcbCoachSkillEditor.getSelectedItem() == null) &&
           (m_jcbAssitantsTotalLevelEditor.getSelectedItem() == null)){
            return;
        }

        List<TrainingPerWeek> futureTrainingsToSave = new ArrayList<TrainingPerWeek>();

        for (TrainingPerWeek train: this.m_TrainingModel.getFutureTrainings()) {

            if(m_selectedTrainingDates != null){
                if ( ! m_selectedTrainingDates.contains(train.getTrainingDate())){
                    continue;
                }
            }

            if(m_jcbTrainingType.getSelectedItem() != null){
                train.setTrainingType(((CBItem) m_jcbTrainingType.getSelectedItem()).getId());
            }

            if(m_jcbIntensity.getSelectedItem() != null) {
                train.setTrainingIntensity((Integer)m_jcbIntensity.getSelectedItem());
            }

            if(m_jcbStaminaTrainingPart.getSelectedItem() != null) {
                train.setStaminaPart((Integer)m_jcbStaminaTrainingPart.getSelectedItem());
            }

            if(m_jcbCoachSkillEditor.getSelectedItem() != null) {
                train.setCoachLevel((Integer)m_jcbCoachSkillEditor.getSelectedItem());
            }

            if(m_jcbAssitantsTotalLevelEditor.getSelectedItem() != null) {
                train.setTrainingAssistantLevel((Integer)m_jcbAssitantsTotalLevelEditor.getSelectedItem());
            }

            train.setSource(DBDataSource.MANUAL);
            futureTrainingsToSave.add(train);
        }

        m_TrainingModel.saveFutureTrainings(futureTrainingsToSave);
        m_FutureTrainingsTableModel.populate(m_TrainingModel.getFutureTrainings());
        RefreshManager.instance().doRefresh();
    }

    /**
     * Initializes the state of this instance.
     */
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel jlTrainingType = new JLabel(Helper.getTranslation("ls.team.trainingtype.short"));
        customizeLabel(jlTrainingType);
        jlTrainingType.setToolTipText(Helper.getTranslation("ls.team.trainingtype"));
        add(jlTrainingType, gbc);

        m_jcbTrainingType = new TrainingComboBox(true);
        m_jcbTrainingType.setToolTipText(Helper.getTranslation("ls.team.trainingtype"));
        gbc.gridy = 1;
        add(m_jcbTrainingType, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JLabel jlTrainingIntensity = new JLabel(Helper.getTranslation("ls.team.trainingintensity.short"));
        customizeLabel(jlTrainingIntensity);
        jlTrainingIntensity.setToolTipText(Helper.getTranslation("ls.team.trainingintensity"));
        add(jlTrainingIntensity, gbc);

        m_jcbIntensity = new trainingParametersEditor(TrainingConstants.MIN_TRAINING_INTENSITY, true);
        m_jcbIntensity.setToolTipText(Helper.getTranslation("ls.team.trainingintensity"));
        gbc.gridy = 1;
        add(m_jcbIntensity, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        JLabel jlStaminatrainingshare = new JLabel(Helper.getTranslation("ls.team.staminatrainingshare.short"));
        customizeLabel(jlStaminatrainingshare);
        jlStaminatrainingshare.setToolTipText(Helper.getTranslation("ls.team.staminatrainingshare"));
        add(jlStaminatrainingshare, gbc);

        m_jcbStaminaTrainingPart = new trainingParametersEditor(TrainingConstants.MIN_STAMINA_SHARE, true);
        m_jcbStaminaTrainingPart.setToolTipText(Helper.getTranslation("ls.team.staminatrainingshare"));
        gbc.gridy = 1;
        add(m_jcbStaminaTrainingPart, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        JLabel jlCoachingSkill = new JLabel(Helper.getTranslation("ls.team.coachingskill.short"));
        customizeLabel(jlCoachingSkill);
        jlCoachingSkill.setToolTipText(Helper.getTranslation("ls.team.coachingskill"));
        add(jlCoachingSkill, gbc);

        m_jcbCoachSkillEditor  = new trainingParametersEditor(TrainingConstants.MIN_COACH_SKILL, TrainingConstants.MAX_COACH_SKILL, true);
        m_jcbCoachSkillEditor.setToolTipText(Helper.getTranslation("ls.team.coachingskill"));
        gbc.gridy = 1;
        add(m_jcbCoachSkillEditor, gbc);

        gbc.gridx = 4;
        gbc.gridy = 0;
        JLabel jlAssistantsTrainerLevel = new JLabel(Helper.getTranslation("ls.module.statistics.club.assistant_trainers_level.short"));
        customizeLabel(jlAssistantsTrainerLevel);
        jlAssistantsTrainerLevel.setToolTipText(Helper.getTranslation("ls.module.statistics.club.assistant_trainers_level"));
        add(jlAssistantsTrainerLevel, gbc);

        m_jcbAssitantsTotalLevelEditor  = new trainingParametersEditor(TrainingConstants.MIN_ASSISTANTS_COACH_LEVEL, TrainingConstants.MAX_ASSISTANTS_COACH_LEVEL, true);
        m_jcbAssitantsTotalLevelEditor.setToolTipText(Helper.getTranslation("ls.module.statistics.club.assistant_trainers_level"));
        gbc.gridy = 1;
        add(m_jcbAssitantsTotalLevelEditor, gbc);


        JButton button = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.apply"));
        button.addActionListener(arg0 -> setFutureTrainings());
        gbc.gridx = 5;
        add(button, gbc);

    }

    private void customizeLabel(JLabel jlabel){
        jlabel.setForeground(TITLE_FG);
        jlabel.setFont(getFont().deriveFont(Font.BOLD));
        jlabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
}
