package module.playeroverview;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.player.Player;
import core.option.SliderPanel;
import core.util.Helper;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

final class PlayerSubskillOffsetDialog extends JDialog implements ActionListener, ChangeListener {

	//~ Instance fields ----------------------------------------------------------------------------

	private JButton cancelButton;
	private JButton okButton;
	private SliderPanel wingerOffsetSlider;
	private SliderPanel passingOffsetSlider;
	private SliderPanel playmakingOffsetSlider;
	private SliderPanel setPiecesOffsetSlider;
	private SliderPanel scoringOffsetSlider;
	private SliderPanel keeperOffsetSlider;
	private SliderPanel defendingOffsetSlider;
	private SliderPanel experienceOffsetSlider;
	private SliderPanel staminaOffsetSlider;
	private SliderPanel formOffsetSlider;

	private JFormattedTextField tsi;

	private final Player m_clPlayer;

	//~ Constructors -------------------------------------------------------------------------------

	/**
	 * Creates a new PlayerSubskillOffsetDialog object.
	 */
	PlayerSubskillOffsetDialog(javax.swing.JFrame owner, Player player) {
		super(owner, true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(TranslationFacility.tr("OffsetTitle") + " " + player.getShortName());

		m_clPlayer = player;

		initComponents();

		pack();

		final Dimension size =
			core.gui.HOMainFrame.instance().getToolkit().getScreenSize();

		if (size.width > this.getSize().width) {
			//Mittig positionieren
			this.setLocation(
				(size.width / 2) - (this.getSize().width / 2),
				(size.height / 2) - (this.getSize().height / 2));
		}

		refresh();
	}

	//~ Methods ------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource().equals(okButton)) {
			m_clPlayer.setSubskill4PlayerSkill(PlayerSkill.WINGER, wingerOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4PlayerSkill(PlayerSkill.PASSING, passingOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4PlayerSkill(PlayerSkill.PLAYMAKING, playmakingOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4PlayerSkill(PlayerSkill.SETPIECES, setPiecesOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4PlayerSkill(PlayerSkill.SCORING, scoringOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4PlayerSkill(PlayerSkill.KEEPER, keeperOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4PlayerSkill(PlayerSkill.DEFENDING, defendingOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4PlayerSkill(PlayerSkill.EXPERIENCE, experienceOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4PlayerSkill(PlayerSkill.FORM, formOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4PlayerSkill(PlayerSkill.STAMINA, staminaOffsetSlider.getValue() / 100);

			DBManager.instance().saveSpieler(HOVerwaltung.instance().getModel().getCurrentPlayers());

			// Remove player from prediction model to force recalculation of his rating values
			var predictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
			predictionModel.removePlayer(m_clPlayer);

			core.gui.RefreshManager.instance().doReInit();
			setVisible(false);
			dispose();
		} else if (actionEvent.getSource().equals(cancelButton)) {
			setVisible(false);
			dispose();
		}
	}

	private void initComponents() {
		setContentPane(new core.gui.comp.panel.ImagePanel());

		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(4, 4, 4, 4);

		getContentPane().setLayout(layout);

		//----Slider -----------
		final JPanel panel = new ImagePanel();
		panel.setLayout(new GridLayout(10, 1, 4, 4));
		panel.setBorder(BorderFactory.createLineBorder(ThemeManager.getColor(HOColorName.PANEL_BORDER)));

		playmakingOffsetSlider = createSliderPanel(panel,"ls.player.skill.playmaking", PlayerSkill.PLAYMAKING);
		playmakingOffsetSlider.addChangeListener(this);
		wingerOffsetSlider = createSliderPanel(panel, "ls.player.skill.winger", PlayerSkill.WINGER);
		wingerOffsetSlider.addChangeListener(this);
		scoringOffsetSlider = createSliderPanel(panel, "ls.player.skill.scoring", PlayerSkill.SCORING);
		scoringOffsetSlider.addChangeListener(this);
		keeperOffsetSlider = createSliderPanel(panel, "ls.player.skill.keeper", PlayerSkill.KEEPER);
		keeperOffsetSlider.addChangeListener(this);
		passingOffsetSlider = createSliderPanel(panel, "ls.player.skill.passing", PlayerSkill.PASSING);
		passingOffsetSlider.addChangeListener(this);
		defendingOffsetSlider = createSliderPanel(panel, "ls.player.skill.defending", PlayerSkill.DEFENDING);
		defendingOffsetSlider.addChangeListener(this);
		setPiecesOffsetSlider = createSliderPanel(panel, "ls.player.skill.setpieces", PlayerSkill.SETPIECES);
		experienceOffsetSlider = createSliderPanel(panel, "ls.player.experience", PlayerSkill.EXPERIENCE);
		staminaOffsetSlider = createSliderPanel(panel, "ls.player.skill.stamina", PlayerSkill.STAMINA);
		staminaOffsetSlider.addChangeListener(this);
		formOffsetSlider = createSliderPanel(panel, "ls.player.form", PlayerSkill.FORM);
		formOffsetSlider.addChangeListener(this);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		layout.setConstraints(panel, constraints);
		getContentPane().add(panel);


		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		getContentPane().add(new JLabel(TranslationFacility.tr("ls.player.tsi")), constraints);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.gridx = 1;
		constraints.gridy = 2;
		tsi = new JFormattedTextField();
		tsi.setToolTipText(TranslationFacility.tr("ls.player.tsi.calculatedversusindicated"));
		tsi.setEnabled(false);
		getContentPane().add(tsi, constraints);

		okButton =
				new JButton(TranslationFacility.tr("ls.button.ok"));
		okButton.addActionListener(this);
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.NONE;
		layout.setConstraints(okButton, constraints);
		getContentPane().add(okButton);

		cancelButton =
			new JButton(TranslationFacility.tr("ls.button.cancel"));
		cancelButton.addActionListener(this);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.NONE;
		layout.setConstraints(cancelButton, constraints);
		getContentPane().add(cancelButton);
	}

	private SliderPanel createSliderPanel(JPanel panel, String languageString, PlayerSkill skill) {
		SliderPanel ret = new SliderPanel(Helper.getTranslation(languageString),100, 0, 1, 1f,80);
		ret.setValue((float)m_clPlayer.getSub4Skill(skill) * 100f);
		ret.setEnabled(m_clPlayer.getValue4Skill(skill)>0);
		panel.add(ret);
		return ret;
	}

	public void refresh(){
		var numberFormat = Helper.getNumberFormat( 0);
		this.tsi.setValue(numberFormat.format( calculateTSI()) + " / " + numberFormat.format(m_clPlayer.getTsi()));
	}

	private long calculateTSI() {

		var player = new Player();
		player.copySkills(m_clPlayer);
		player.setSubskill4PlayerSkill(PlayerSkill.DEFENDING, defendingOffsetSlider.getValue()/100);
		player.setSubskill4PlayerSkill(PlayerSkill.PLAYMAKING, playmakingOffsetSlider.getValue()/100);
		player.setSubskill4PlayerSkill(PlayerSkill.SCORING, scoringOffsetSlider.getValue()/100);
		player.setSubskill4PlayerSkill(PlayerSkill.PASSING, passingOffsetSlider.getValue()/100);
		player.setSubskill4PlayerSkill(PlayerSkill.WINGER, wingerOffsetSlider.getValue()/100);
		player.setSubskill4PlayerSkill(PlayerSkill.STAMINA, staminaOffsetSlider.getValue()/100);
		player.setSubskill4PlayerSkill(PlayerSkill.FORM, formOffsetSlider.getValue()/100);
		player.setSubskill4PlayerSkill(PlayerSkill.KEEPER, keeperOffsetSlider.getValue()/100);
		player.setAge(m_clPlayer.getAge());

		return player.calculateTSI();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		refresh();
	}
}