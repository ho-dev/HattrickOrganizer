// %4263391236:hoplugins.trainingExperience.ui%
package module.training.ui;

import core.constants.player.PlayerAbility;
import core.constants.player.PlayerSkill;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.FuturePlayer;
import core.model.player.MatchRoleID;
import core.training.FutureTrainingManager;
import module.training.Skills;
import module.training.ui.comp.HTColorBar;
import module.training.ui.model.TrainingModel;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serial;

import javax.swing.*;

/**
 * Panel where the future training predictions are shown.
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class PlayerDetailPanel extends LazyImagePanel implements FocusListener {

    @Serial
    private static final long serialVersionUID = -6606934473344186243L;
    private static final int skillNumber = 9;
    private JLabel playerLabel;
    private JTextArea m_jtaNotes;
    private HTColorBar[] levelBar;
    private JLabel[] skillLabel;
    private final TrainingModel model;

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
            // Notes could be changed
            saveNotes();
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
            playerLabel.setText(HOVerwaltung.instance().getLanguageString("PlayerSelect"));
            m_jtaNotes.setEditable(false);
            m_jtaNotes.setText("");

            for (int i = 0; i < skillNumber; i++) {
                skillLabel[i].setText("");
                levelBar[i].setSkillLevel(0f, 0);
            }
            return;
        }

        // sets player number
        String value = MatchRoleID.getNameForPosition(this.model.getActivePlayer().getIdealPosition()) + " ("
                + this.model.getActivePlayer().getIdealPositionRating() + ")";
        playerLabel.setText("<html><b>" + this.model.getActivePlayer().getFullName() + "</b> - " + value + "</html>");

        m_jtaNotes.setEditable(true);
        m_jtaNotes.setText(this.model.getActivePlayer().getNote());

        // instantiate a future train manager to calculate the previsions */
        FutureTrainingManager ftm = this.model.getFutureTrainingManager();

        for (int i = 0; i < skillNumber; i++) {
            int skillIndex = Skills.getSkillAtPosition(i);
            var skillValue = Skills.getSkillValue(this.model.getActivePlayer(), skillIndex);
            skillLabel[i].setText(PlayerAbility.getNameForSkill(skillValue, true));

            FuturePlayer fp = ftm.previewPlayer(UserParameter.instance().futureWeeks);
            double finalValue = getSkillValue(fp, skillIndex);

            float skillValueInt = (int) skillValue;
            var skillValueDecimal = skillValue - skillValueInt;

            levelBar[i].setSkillLevel(skillValueInt / getSkillMaxValue(i), skillValueInt);
            levelBar[i].setSkillDecimalLevel((float) (skillValueDecimal / getSkillMaxValue(i)));
            levelBar[i].setFutureSkillLevel((float) (finalValue - skillValue) / getSkillMaxValue(i));
        }
    }

    /**
     * Get maximum value of the skill.
     *
     * @param index
     * @return float Max value
     */
    private float getSkillMaxValue(int index) {
        // form 8, stamina 9
        if (index == 0) {
            return 8f;
        } else if (index == 1) {
            return 9f;
        } else {
            return 20f;
        }
    }

    private double getSkillValue(FuturePlayer spieler, int skillIndex) {
        return switch (skillIndex) {
            case PlayerSkill.KEEPER -> spieler.getGoalkeeping();
            case PlayerSkill.SCORING -> spieler.getAttack();
            case PlayerSkill.DEFENDING -> spieler.getDefense();
            case PlayerSkill.PASSING -> spieler.getPassing();
            case PlayerSkill.PLAYMAKING -> spieler.getPlaymaking();
            case PlayerSkill.SET_PIECES -> spieler.getSetpieces();
            case PlayerSkill.STAMINA -> spieler.getStamina();
            case PlayerSkill.FORM -> spieler.getForm();
            case PlayerSkill.WINGER -> spieler.getCross();
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

        for (int i = 0; i < skillNumber; i++) {
            if (i == 1) {
                gbc.insets = new Insets(2, 4, 8, 4);
            } else {
                gbc.insets = new Insets(2, 4, 2, 4);
            }

            gbc.gridy = i;
            gbc.weightx = 0.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            int skillIndex = Skills.getSkillAtPosition(i);
            gbc.gridx = 0;
            bottom.add(new JLabel(PlayerSkill.toString(skillIndex)), gbc);

            skillLabel[i] = new JLabel("");
            skillLabel[i].setOpaque(false);
            gbc.gridx = 1;
            bottom.add(skillLabel[i], gbc);

            int len = (int) getSkillMaxValue(i) * 10;

            levelBar[i] = new HTColorBar(skillIndex, 0f, len, 16);
            levelBar[i].setOpaque(false);
            levelBar[i].setMinimumSize(new Dimension(200, 16));
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = 2;
            gbc.weightx = 1.0;
            bottom.add(levelBar[i], gbc);
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
        m_jtaNotes.addFocusListener(this);

        JPanel panel2 = new ImagePanel();
        panel2.setLayout(new BorderLayout());
        panel2.setBorder(javax.swing.BorderFactory.createTitledBorder(HOVerwaltung.instance().getLanguageString("Notizen")));
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

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {
        saveNotes();
    }

    private void saveNotes() {
        var player = this.model.getActivePlayer();
        if( player!= null){
            player.setNote(m_jtaNotes.getText());
        }
    }


}
