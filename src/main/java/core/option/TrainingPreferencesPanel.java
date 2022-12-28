package core.option;

import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.training.SkillDrops;
import core.util.Helper;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import static module.lineup.LineupPanel.TITLE_FG;

/**
 * Training options panel
 */
final class TrainingPreferencesPanel extends ImagePanel implements ChangeListener, ActionListener {

//	private TrainingAdjustmentPanel m_tapAgeFactor;
//    private TrainingAdjustmentPanel m_jtapAssisstantFactor;
//    private TrainingAdjustmentPanel m_jtapIntensityFactor;
//    private TrainingAdjustmentPanel m_jtapCoachFactor;
//    private TrainingAdjustmentPanel m_jtapWinger;
//    private TrainingAdjustmentPanel m_jtapPassing;
//    private TrainingAdjustmentPanel m_jtapPlaymaking;
//    private TrainingAdjustmentPanel m_jtapSetPieces;
//    private TrainingAdjustmentPanel m_jtapScoring;
//    private TrainingAdjustmentPanel m_jtapGoalkeeping;
//    private TrainingAdjustmentPanel m_jtapDefending;
//    private TrainingAdjustmentPanel m_jtapOsmosis;
    private SliderPanel m_jslFutureWeeks;
    private final JCheckBox m_jcSkillDrops = new JCheckBox(HOVerwaltung.instance().getLanguageString("skillDrops"));
    private final JCheckBox m_jcSkillDropsInPrediction = new JCheckBox(HOVerwaltung.instance().getLanguageString("ls.options.training.showSkillDrops"));


    TrainingPreferencesPanel() {
        initComponents();
    }

    public void refresh() {
//        UserParameter.temp().TRAINING_OFFSET_GOALKEEPING =  m_jtapGoalkeeping.getValue();
//        UserParameter.temp().TRAINING_OFFSET_DEFENDING =  m_jtapDefending.getValue();
//        UserParameter.temp().TRAINING_OFFSET_PLAYMAKING = m_jtapPlaymaking.getValue();
//        UserParameter.temp().TRAINING_OFFSET_PASSING =  m_jtapPassing.getValue();
//        UserParameter.temp().TRAINING_OFFSET_WINGER =  m_jtapWinger.getValue();
//        UserParameter.temp().TRAINING_OFFSET_SCORING =  m_jtapScoring.getValue();
//        UserParameter.temp().TRAINING_OFFSET_SETPIECES = m_jtapSetPieces.getValue();
//        UserParameter.temp().TRAINING_OFFSET_OSMOSIS =  m_jtapOsmosis.getValue();
//        UserParameter.temp().TRAINING_OFFSET_AGE = m_tapAgeFactor.getValue();
//        UserParameter.temp().TrainerFaktor = m_jtapCoachFactor.getValue();
//        UserParameter.temp().TRAINING_OFFSET_ASSISTANTS = m_jtapAssisstantFactor.getValue();
//        UserParameter.temp().TRAINING_OFFSET_INTENSITY = m_jtapIntensityFactor.getValue();
        UserParameter.temp().futureWeeks = (int) m_jslFutureWeeks.getValue();
        UserParameter.temp().TRAINING_SHOW_SKILLDROPS = this.m_jcSkillDropsInPrediction.isSelected();
        SkillDrops.instance().setActive(m_jcSkillDrops.isSelected());

        OptionManager.instance().setReInitNeeded();
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        UserParameter.temp().futureWeeks = (int) m_jslFutureWeeks.getValue();

        if (core.model.UserParameter.temp().futureWeeks != core.model.UserParameter.instance().futureWeeks) {
            OptionManager.instance().setRestartNeeded();
        }
    }

    private void initComponents() {
    	setLayout(new GridLayout(18, 1, 3, 0));

//        JLabel label = new JLabel(Helper.getTranslation("VoraussichtlicheTrainingwochen"));
//        label.setForeground(TITLE_FG);
//        label.setFont(getFont().deriveFont(Font.BOLD));
//        add(label);


//        m_jtapGoalkeeping = new TrainingAdjustmentPanel(HOVerwaltung.instance().getLanguageString("ls.team.trainingtype.goalkeeping"),
//                WeeklyTrainingType.instance(TrainingType.GOALKEEPING).getBaseTrainingLength(), UserParameter.temp().TRAINING_OFFSET_GOALKEEPING, this);
//        add(m_jtapGoalkeeping);
//
//        m_jtapDefending = new TrainingAdjustmentPanel(HOVerwaltung.instance().getLanguageString("ls.team.trainingtype.defending"),
//        		WeeklyTrainingType.instance(TrainingType.DEFENDING).getBaseTrainingLength(),UserParameter.temp().TRAINING_OFFSET_DEFENDING, this);
//        add(m_jtapDefending);
//
//        m_jtapPlaymaking = new TrainingAdjustmentPanel(HOVerwaltung.instance().getLanguageString("ls.team.trainingtype.playmaking"),
//        		WeeklyTrainingType.instance(TrainingType.PLAYMAKING).getBaseTrainingLength(), UserParameter.temp().TRAINING_OFFSET_PLAYMAKING, this);
//        add(m_jtapPlaymaking);
//
//        m_jtapPassing = new TrainingAdjustmentPanel(HOVerwaltung.instance().getLanguageString("ls.team.trainingtype.shortpasses"),
//        		WeeklyTrainingType.instance(TrainingType.SHORT_PASSES).getBaseTrainingLength(), UserParameter.temp().TRAINING_OFFSET_PASSING, this);
//        add(m_jtapPassing);
//
//        m_jtapWinger = new TrainingAdjustmentPanel(HOVerwaltung.instance().getLanguageString("ls.team.trainingtype.crossing"),
//        		WeeklyTrainingType.instance(TrainingType.CROSSING_WINGER).getBaseTrainingLength(), UserParameter.temp().TRAINING_OFFSET_WINGER, this);
//        add(m_jtapWinger);
//
//        m_jtapScoring = new TrainingAdjustmentPanel(HOVerwaltung.instance().getLanguageString("ls.team.trainingtype.scoring"),
//        		WeeklyTrainingType.instance(TrainingType.SCORING).getBaseTrainingLength(), UserParameter.temp().TRAINING_OFFSET_SCORING, this);
//        add(m_jtapScoring);
//
//        m_jtapSetPieces = new TrainingAdjustmentPanel(HOVerwaltung.instance().getLanguageString("ls.team.trainingtype.setpieces"),
//        		WeeklyTrainingType.instance(TrainingType.SET_PIECES).getBaseTrainingLength(), UserParameter.temp().TRAINING_OFFSET_SETPIECES, this);
//        add(m_jtapSetPieces);

//        label = new JLabel(Helper.getTranslation("TrainingFaktoren"));
//        label.setForeground(TITLE_FG);
//        label.setFont(getFont().deriveFont(Font.BOLD));
//        add(label);

//        m_jtapOsmosis = new TrainingAdjustmentPanel(HOVerwaltung.instance().getLanguageString("training.osmosis"),
//        		WeeklyTrainingType.OSMOSIS_BASE_PERCENTAGE, UserParameter.temp().TRAINING_OFFSET_OSMOSIS, this);
//        add(m_jtapOsmosis);
//
//        m_tapAgeFactor = new TrainingAdjustmentPanel(HOVerwaltung.instance().getLanguageString("ls.player.age"),
//        		WeeklyTrainingType.BASE_AGE_FACTOR, UserParameter.temp().TRAINING_OFFSET_AGE, this);
//        add(m_tapAgeFactor);
//
//        m_jtapCoachFactor = new TrainingAdjustmentPanel(HOVerwaltung.instance().getLanguageString("ls.team.coachingskill"),
//        		WeeklyTrainingType.BASE_COACH_FACTOR, UserParameter.temp().TrainerFaktor, this);
//        add(m_jtapCoachFactor);
//
//        m_jtapAssisstantFactor = new TrainingAdjustmentPanel(HOVerwaltung.instance().getLanguageString("FaktorCoTraineranzahl"),
//        		WeeklyTrainingType.BASE_ASSISTANT_COACH_FACTOR, UserParameter.temp().TRAINING_OFFSET_ASSISTANTS, this);
//        add(m_jtapAssisstantFactor);
//
//        m_jtapIntensityFactor = new TrainingAdjustmentPanel(HOVerwaltung.instance().getLanguageString("ls.team.trainingintensity"),
//        		WeeklyTrainingType.BASE_INTENSITY_FACTOR, UserParameter.temp().TRAINING_OFFSET_INTENSITY, this);
//        add(m_jtapIntensityFactor);

        var label = new JLabel(core.util.StringUtils.capitalizeWord(Helper.getTranslation("ls.general_label.miscellaneous")));
        label.setForeground(TITLE_FG);
        label.setFont(getFont().deriveFont(Font.BOLD));
        add(label);

        m_jslFutureWeeks = new SliderPanel(HOVerwaltung.instance().getLanguageString("futureWeeks"), 80, 0, 1, 1f, 120);
        m_jslFutureWeeks.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Optionen_futureWeeks"));
        m_jslFutureWeeks.setValue(core.model.UserParameter.temp().futureWeeks);
        m_jslFutureWeeks.addChangeListener(this);
        add(m_jslFutureWeeks);

        m_jcSkillDrops.setSelected(SkillDrops.instance().isActive());
        m_jcSkillDrops.addActionListener(this);
        add(m_jcSkillDrops);

        m_jcSkillDropsInPrediction.setSelected(UserParameter.temp().TRAINING_SHOW_SKILLDROPS);
        m_jcSkillDropsInPrediction.addActionListener(this);
        add(m_jcSkillDropsInPrediction);
    }

	@Override
	public void actionPerformed(ActionEvent event) {
		if ((event.getSource() == m_jcSkillDrops)
				&& (m_jcSkillDrops.isSelected() != SkillDrops.instance().isActive())) {
			SkillDrops.instance().setActive(m_jcSkillDrops.isSelected());
		}
		else if ( event.getSource() == m_jcSkillDropsInPrediction){
		    UserParameter.temp().TRAINING_SHOW_SKILLDROPS = m_jcSkillDropsInPrediction.isSelected();
        }

	}
}
