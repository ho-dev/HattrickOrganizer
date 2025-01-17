// %4263391236:hoplugins.trainingExperience.ui%
package module.training.ui;

import core.constants.player.PlayerAbility;
import core.constants.player.PlayerSkill;
import core.gui.RefreshManager;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.model.player.FuturePlayer;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.training.FuturePlayerTraining;
import core.training.FutureTrainingManager;
import core.training.WeeklyTrainingType;
import module.training.Skills;
import module.training.ui.comp.HTColorBar;
import module.training.ui.model.TrainingModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serial;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static core.constants.player.PlayerSkill.FORM;
import static core.constants.player.PlayerSkill.STAMINA;

/**
 * Panel where the future training predictions are shown.
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class PlayerDetailPanel extends LazyImagePanel {

    @Serial
    private static final long serialVersionUID = -6606934473344186243L;
    private static final int skillNumber = 9;
    private JLabel playerLabel;
    private JTextArea m_jtaNotes;
    private HTColorBar[] levelBar;
    private JComboBox[] trainingPlanSelection;
    private JLabel[] skillLabel;
    private final TrainingModel model;
    private Player editingPlayer = null;

    /**
     * Creates the panel and its components
     */
    public PlayerDetailPanel(TrainingModel model) {
        this.model = model;
    }

    @Override
    protected void initialize() {
        initComponents();
        addListeners();
        loadFromModel();
        setNeedsRefresh(false);
    }

    @Override
    protected void update() {
        if ( m_jtaNotes.isFocusOwner() ){
            stopEdit();
        }
        loadFromModel();
    }

    private void addListeners() {
        this.model.addModelChangeListener(change -> setNeedsRefresh(true));
    }

    /**
     * Method that populates this panel for the selected player
     */
    private void loadFromModel() {
        if (this.model.getActivePlayer() == null) {
            for (var select : trainingPlanSelection) {
                if (select != null) {
                    select.setSelectedItem(null);
                    select.setEnabled(false);
                }
            }
            playerLabel.setText(TranslationFacility.tr("PlayerSelect"));
            m_jtaNotes.setEditable(false);
            m_jtaNotes.setText("");

            for (int i = 0; i < skillNumber; i++) {
                skillLabel[i].setText("");
                levelBar[i].setSkillLevel(0f, 0);
            }
            return;
        }

        // sets player number
        String value = MatchRoleID.getNameForPosition(this.model.getActivePlayer().getIdealPosition())
                + String.format(" (%.2f)", this.model.getActivePlayer().getIdealPositionRating());
        playerLabel.setText("<html><b>" + this.model.getActivePlayer().getFullName() + "</b> - " + value + "</html>");

        m_jtaNotes.setEditable(true);
        m_jtaNotes.setText(this.model.getActivePlayer().getNote());

        // instantiate a future train manager to calculate the previsions */
        FutureTrainingManager ftm = this.model.getFutureTrainingManager();

        for (int i=0; i<skillNumber; i++) {
            var skill = Skills.getSkillAtPosition(i);
            var skillValue = this.model.getActivePlayer().getSkillValue(skill);
            skillLabel[i].setText(PlayerAbility.getNameForSkill(skillValue, true));

            FuturePlayer fp = ftm.previewPlayer(UserParameter.instance().futureWeeks);
            double finalValue = getSkillValue(fp, skill);

            float skillValueInt = (int) skillValue;
            var skillValueDecimal = skillValue - skillValueInt;

            levelBar[i].setSkillLevel(skillValueInt / getSkillMaxValue(skill), skillValueInt);
            levelBar[i].setSkillDecimalLevel((float) (skillValueDecimal / getSkillMaxValue(skill)));
            levelBar[i].setFutureSkillLevel((float) (finalValue - skillValue) / getSkillMaxValue(skill));

            if (trainingPlanSelection[i] != null) {
                trainingPlanSelection[i].setEnabled(true);
                trainingPlanSelection[i].setSelectedItem(this.model.getActivePlayer().getFuturePlayerSkillTrainingPriority(skill));
            }
        }
    }

    private boolean isFullTrainingAvailable(PlayerSkill skill) {
        return switch (skill){
            case KEEPER, DEFENDING, WINGER, PLAYMAKING, SCORING, PASSING, SETPIECES -> true;
            default -> false;
        };
    }
    private boolean isPartialTrainingAvailable(PlayerSkill skill) {
        return switch (skill){
            case WINGER, PLAYMAKING, SETPIECES -> true;
            default -> false;
        };
    }

    private boolean isOsmosisTrainingAvailable(PlayerSkill skill) {
        return switch (skill){
            case DEFENDING, WINGER, PLAYMAKING, SCORING, PASSING -> true;
            default -> false;
        };
    }


    /**
     * Get maximum value of the skill.
     *
     * @param index Player skill type
     * @return float Max value
     */
    private float getSkillMaxValue(PlayerSkill index) {
        // form 8, stamina 9
        if (index == FORM) {
            return 8f;
        } else if (index == STAMINA) {
            return 9f;
        } else {
            return 20f;
        }
    }

    private double getSkillValue(FuturePlayer spieler, PlayerSkill skillIndex) {
        return switch (skillIndex) {
            case KEEPER -> spieler.getGoalkeeping();
            case SCORING -> spieler.getAttack();
            case DEFENDING -> spieler.getDefense();
            case PASSING -> spieler.getPassing();
            case PLAYMAKING -> spieler.getPlaymaking();
            case SETPIECES -> spieler.getSetpieces();
            case STAMINA -> spieler.getStamina();
            case FORM -> spieler.getForm();
            case WINGER -> spieler.getCross();
            default -> 0;
        };
    }

    /**
     * Initialize the object layout
     */
    private void initComponents() {
        setOpaque(false);
        setLayout(new GridBagLayout());

        GridBagConstraints maingbc = new GridBagConstraints();
        maingbc.anchor = GridBagConstraints.NORTH;
        maingbc.insets = new Insets(8, 10, 8, 10);
        playerLabel = new JLabel("", SwingConstants.CENTER);
        add(playerLabel, maingbc);

        JPanel bottom = new JPanel(new GridBagLayout());
        bottom.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        levelBar = new HTColorBar[skillNumber];
        skillLabel = new JLabel[skillNumber];
        trainingPlanSelection = new JComboBox[skillNumber];

        for (int i=0; i<skillNumber; i++){
            if (i == 1) {
                gbc.insets = new Insets(2, 4, 8, 4);
            } else {
                gbc.insets = new Insets(2, 4, 2, 4);
            }

            gbc.gridy = i;
            gbc.weightx = 0.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            var skill = Skills.getSkillAtPosition(i);
            gbc.gridx = 0;
            bottom.add(new JLabel(skill.getLanguageString()), gbc);

            skillLabel[i] = new JLabel("");
            skillLabel[i].setOpaque(false);
            gbc.gridx = 1;
            bottom.add(skillLabel[i], gbc);

            int len = (int) getSkillMaxValue(skill) * 10;

            levelBar[i] = new HTColorBar(skill, 0f, len, 16);
            levelBar[i].setOpaque(false);
            levelBar[i].setMinimumSize(new Dimension(200, 16));
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = 2;
            gbc.weightx = 1.0;
            bottom.add(levelBar[i], gbc);

            gbc.gridx = 3;
            if ( isFullTrainingAvailable(skill)) {
                var prios = new ArrayList<FuturePlayerTraining.Priority>();
                prios.add(FuturePlayerTraining.Priority.FULL_TRAINING);

                if ( isPartialTrainingAvailable(skill)){
                    prios.add(FuturePlayerTraining.Priority.PARTIAL_TRAINING);
                }
                if ( isOsmosisTrainingAvailable(skill)){
                    prios.add(FuturePlayerTraining.Priority.OSMOSIS_TRAINING);
                }
                prios.add(FuturePlayerTraining.Priority.NO_TRAINING);
                prios.add(null);
                if (trainingPlanSelection[i] == null ){
                    trainingPlanSelection[i] = new JComboBox<>(prios.toArray());
                    trainingPlanSelection[i].addActionListener(e->selectTraining(e, skill));
                }
                bottom.add(trainingPlanSelection[i], gbc); // individual training
            }
            else if ( i == 0){
                bottom.add(new JLabel(TranslationFacility.tr("trainpre.plan")), gbc);
            }

        }

        maingbc.gridy = 1;
        maingbc.insets = new Insets(0, 0, 0, 0);
        maingbc.fill = GridBagConstraints.BOTH;
        maingbc.weightx = 1.0;
        maingbc.weighty = 1.0;
        add(bottom, maingbc);

        m_jtaNotes = new JTextArea(5,8);
        m_jtaNotes.setEditable(false);
        m_jtaNotes.setBackground(ColorLabelEntry.BG_STANDARD);
        m_jtaNotes.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                startEdit(model.getActivePlayer());
            }

            @Override
            public void focusLost(FocusEvent e) {
                stopEdit();
            }
        });

        JPanel panel2 = new ImagePanel();
        panel2.setLayout(new BorderLayout());
        panel2.setBorder(javax.swing.BorderFactory.createTitledBorder(TranslationFacility.tr("Notizen")));
        panel2.add(new JScrollPane(m_jtaNotes), BorderLayout.CENTER);

        maingbc.gridx = 0;
        maingbc.gridy++;
        maingbc.insets = new Insets(2, 0, 0, 0);
        maingbc.fill = GridBagConstraints.BOTH;
        add(panel2, maingbc);

        JPanel dummyPanelToConsumeAllExtraSpace = new JPanel();
        dummyPanelToConsumeAllExtraSpace.setOpaque(false);
        maingbc.gridy++;
        gbc.weighty = 1.0;
        add(dummyPanelToConsumeAllExtraSpace, maingbc);
    }

    private void startEdit(Player activePlayer) {
        if (this.editingPlayer!=null) {
            stopEdit();
        }
        this.editingPlayer = activePlayer;
    }

    private void stopEdit() {
        if (this.editingPlayer != null){
            saveNotes(m_jtaNotes.getText());
            this.editingPlayer = null;
        }
    }

    private void selectTraining(ActionEvent e, PlayerSkill skillIndex) {
        var player = this.model.getActivePlayer();
        if (player != null) {
            var combox = (JComboBox<FuturePlayerTraining.Priority>) e.getSource();
            var prio = (FuturePlayerTraining.Priority) combox.getSelectedItem();
            if (player.setFutureSkillTrainingPriority(player.getPlayerId(), skillIndex, prio)) {
                if (prio != null) {
                    // clear any individual training override by this selection
                    var futureTrainings = this.model.getFutureTrainings();
                    for (var training : futureTrainings) {
                        var trainingType = WeeklyTrainingType.instance(training.getTrainingType());
                        if (trainingType.isTraining(skillIndex)) {
                            player.setFutureTraining(null, training.getTrainingDate(), training.getTrainingDate().plus(7, ChronoUnit.DAYS));
                        }
                    }
                }
                this.model.resetFutureTrainings();
                this.setNeedsRefresh(true);
            }
        }
    }

    private void saveNotes(String notes) {
        if(editingPlayer!= null && !notes.equals(editingPlayer.getNote())){
            editingPlayer.setNote(notes);
            RefreshManager.instance().doReInit();
        }
    }
}