package module.playerOverview;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.player.Spieler;
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


final class SpielerOffsetDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1187335231698270294L;

	//~ Instance fields ----------------------------------------------------------------------------

	private JButton m_jbAbbrechen;
	private JButton m_jbOK;
	private SliderPanel m_jpFluegelspiel;
	private SliderPanel m_jpPasspiel;
	private SliderPanel m_jpSpielaufbau;
	private SliderPanel m_jpStandard;
	private SliderPanel m_jpTorschuss;
	private SliderPanel m_jpTorwart;
	private SliderPanel m_jpVerteidigung;
	private Spieler m_clSpieler;

	//~ Constructors -------------------------------------------------------------------------------

	/**
	 * Creates a new SpielerOffsetDialog object.
	 */
	protected SpielerOffsetDialog(javax.swing.JFrame owner, Spieler spieler) {
		super(owner, true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(HOVerwaltung.instance().getLanguageString("OffsetTitle") + " " + spieler.getName());

		m_clSpieler = spieler;

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
		if (actionEvent.getSource().equals(m_jbOK)) {
			m_clSpieler.setSubskill4Pos(PlayerSkill.WINGER, m_jpFluegelspiel.getValue() / 100);
			m_clSpieler.setSubskill4Pos(PlayerSkill.PASSING, m_jpPasspiel.getValue() / 100);
			m_clSpieler.setSubskill4Pos(PlayerSkill.PLAYMAKING, m_jpSpielaufbau.getValue() / 100);
			m_clSpieler.setSubskill4Pos(PlayerSkill.SET_PIECES, m_jpStandard.getValue() / 100);
			m_clSpieler.setSubskill4Pos(PlayerSkill.SCORING, m_jpTorschuss.getValue() / 100);
			m_clSpieler.setSubskill4Pos(PlayerSkill.KEEPER, m_jpTorwart.getValue() / 100);
			m_clSpieler.setSubskill4Pos(PlayerSkill.DEFENDING, m_jpVerteidigung.getValue() / 100);

			DBManager.instance().saveSpieler(
				HOVerwaltung.instance().getModel().getID(),
				HOVerwaltung.instance().getModel().getAllSpieler(),
				HOVerwaltung.instance().getModel().getBasics().getDatum());

			//GUI aktualisieren
			core.gui.RefreshManager.instance().doReInit();

			setVisible(false);
			dispose();
		} else if (actionEvent.getSource().equals(m_jbAbbrechen)) {
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
		panel.setLayout(new GridLayout(7, 1, 4, 4));
		panel.setBorder(BorderFactory.createLineBorder(ThemeManager.getColor(HOColorName.PANEL_BORDER)));

		m_jpSpielaufbau =
			new SliderPanel(HOVerwaltung.instance().getLanguageString("ls.player.skill.playmaking"),
				100,
				0,
				1,
				1f,
				80);
		m_jpSpielaufbau.setValue((float) m_clSpieler.getSubskill4Pos(PlayerSkill.PLAYMAKING) * 100f);
		panel.add(m_jpSpielaufbau);

		m_jpFluegelspiel =
			new SliderPanel(HOVerwaltung.instance().getLanguageString("ls.player.skill.winger"),
				100,
				0,
				1,
				1f,
				80);
		m_jpFluegelspiel.setValue((float) m_clSpieler.getSubskill4Pos(PlayerSkill.WINGER) * 100f);
		panel.add(m_jpFluegelspiel);

		m_jpTorschuss =
			new SliderPanel(HOVerwaltung.instance().getLanguageString("ls.player.skill.scoring"),
				100,
				0,
				1,
				1f,
				80);
		m_jpTorschuss.setValue((float) m_clSpieler.getSubskill4Pos(PlayerSkill.SCORING) * 100f);
		panel.add(m_jpTorschuss);

		m_jpTorwart =
			new SliderPanel(HOVerwaltung.instance().getLanguageString("ls.player.skill.keeper"),
				100,
				0,
				1,
				1f,
				80);
		m_jpTorwart.setValue((float) m_clSpieler.getSubskill4Pos(PlayerSkill.KEEPER) * 100f);
		panel.add(m_jpTorwart);

		m_jpPasspiel =
			new SliderPanel(HOVerwaltung.instance().getLanguageString("ls.player.skill.passing"),
				100,
				0,
				1,
				1f,
				80);
		m_jpPasspiel.setValue((float) m_clSpieler.getSubskill4Pos(PlayerSkill.PASSING) * 100f);
		panel.add(m_jpPasspiel);

		m_jpVerteidigung =
			new SliderPanel(HOVerwaltung.instance().getLanguageString("ls.player.skill.defending"),
				100,
				0,
				1,
				1f,
				80);
		m_jpVerteidigung.setValue((float) m_clSpieler.getSubskill4Pos(PlayerSkill.DEFENDING) * 100f);
		panel.add(m_jpVerteidigung);

		m_jpStandard =
			new SliderPanel(HOVerwaltung.instance().getLanguageString("ls.player.skill.setpieces"),
				100,
				0,
				1,
				1f,
				80);
		m_jpStandard.setValue((float) m_clSpieler.getSubskill4Pos(PlayerSkill.SET_PIECES) * 100f);
		panel.add(m_jpStandard);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		layout.setConstraints(panel, constraints);
		getContentPane().add(panel);

		m_jbOK =
			new JButton(HOVerwaltung.instance().getLanguageString("ls.button.ok"));
		m_jbOK.addActionListener(this);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.NONE;
		layout.setConstraints(m_jbOK, constraints);
		getContentPane().add(m_jbOK);

		m_jbAbbrechen =
			new JButton(HOVerwaltung.instance().getLanguageString("ls.button.cancel"));
		m_jbAbbrechen.addActionListener(this);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.NONE;
		layout.setConstraints(m_jbAbbrechen, constraints);
		getContentPane().add(m_jbAbbrechen);
	}
}
