package module.playerOverview;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.option.SliderPanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;


final class PlayerSubskillOffsetDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1187335231698270294L;

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
	private Player m_clPlayer;

	//~ Constructors -------------------------------------------------------------------------------

	/**
	 * Creates a new PlayerSubskillOffsetDialog object.
	 */
	protected PlayerSubskillOffsetDialog(javax.swing.JFrame owner, Player player) {
		super(owner, true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(HOVerwaltung.instance().getLanguageString("OffsetTitle") + " " + player.getShortName());

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
	}

	//~ Methods ------------------------------------------------------------------------------------
	@Override
	public final void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource().equals(okButton)) {
			m_clPlayer.setSubskill4Pos(PlayerSkill.WINGER, wingerOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4Pos(PlayerSkill.PASSING, passingOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4Pos(PlayerSkill.PLAYMAKING, playmakingOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4Pos(PlayerSkill.SET_PIECES, setPiecesOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4Pos(PlayerSkill.SCORING, scoringOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4Pos(PlayerSkill.KEEPER, keeperOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4Pos(PlayerSkill.DEFENDING, defendingOffsetSlider.getValue() / 100);
			m_clPlayer.setSubskill4Pos(PlayerSkill.EXPERIENCE, experienceOffsetSlider.getValue() / 100);

			DBManager.instance().saveSpieler(
				HOVerwaltung.instance().getModel().getID(),
				HOVerwaltung.instance().getModel().getCurrentPlayers(),
				HOVerwaltung.instance().getModel().getBasics().getDatum());

			//GUI aktualisieren
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
		panel.setLayout(new GridLayout(8, 1, 4, 4));
		panel.setBorder(BorderFactory.createLineBorder(ThemeManager.getColor(HOColorName.PANEL_BORDER)));

		playmakingOffsetSlider = createSliderPanel(panel,"ls.player.skill.playmaking", PlayerSkill.PLAYMAKING);
		wingerOffsetSlider = createSliderPanel(panel, "ls.player.skill.winger", PlayerSkill.WINGER);
		scoringOffsetSlider = createSliderPanel(panel, "ls.player.skill.scoring", PlayerSkill.SCORING);
		keeperOffsetSlider = createSliderPanel(panel, "ls.player.skill.keeper", PlayerSkill.KEEPER);
		passingOffsetSlider = createSliderPanel(panel, "ls.player.skill.passing", PlayerSkill.PASSING);
		defendingOffsetSlider = createSliderPanel(panel, "ls.player.skill.defending", PlayerSkill.DEFENDING);
		setPiecesOffsetSlider = createSliderPanel(panel, "ls.player.skill.setpieces", PlayerSkill.SET_PIECES);
		experienceOffsetSlider = createSliderPanel(panel, "ls.player.experience", PlayerSkill.EXPERIENCE);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		layout.setConstraints(panel, constraints);
		getContentPane().add(panel);

		okButton =
			new JButton(HOVerwaltung.instance().getLanguageString("ls.button.ok"));
		okButton.addActionListener(this);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.NONE;
		layout.setConstraints(okButton, constraints);
		getContentPane().add(okButton);

		cancelButton =
			new JButton(HOVerwaltung.instance().getLanguageString("ls.button.cancel"));
		cancelButton.addActionListener(this);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.NONE;
		layout.setConstraints(cancelButton, constraints);
		getContentPane().add(cancelButton);
	}

	private SliderPanel createSliderPanel(JPanel panel, String languageString, int skill) {
		SliderPanel ret = new SliderPanel(HOVerwaltung.instance().getLanguageString(languageString),
				100,
				0,
				1,
				1f,
				80);
		ret.setValue((float) m_clPlayer.getSubskill4Pos(skill) * 100f);
		panel.add(ret);
		return ret;
	}
}
