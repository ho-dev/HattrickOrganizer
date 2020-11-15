package module.statistics;

import static core.gui.theme.HOColorName.PANEL_BORDER;
import static core.gui.theme.HOColorName.TABLEENTRY_BG;
import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.model.StatistikModel;
import core.gui.theme.GroupTeamFactory;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.HOLogger;
import core.util.Helper;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.text.NumberFormat;
import javax.swing.*;

/**
 * The Team statistics panel
 */
public class TeamStatisticsPanel extends LazyImagePanel {
	private static final long serialVersionUID = -6588840565958987842L;
	private ImageCheckbox m_jchExperience;
	private ImageCheckbox m_jchWinger;
	private ImageCheckbox m_jchForm;
	private ImageCheckbox m_jchLeadership;
	private ImageCheckbox m_jchLoyalty;
	private ImageCheckbox m_jchStamina;
	private ImageCheckbox m_jchPassing;
	private ImageCheckbox m_jchPlaymaking;
	private ImageCheckbox m_jchSetPieces;
	private ImageCheckbox m_jchScoring;
	private ImageCheckbox m_jchKeeper;
	private ImageCheckbox m_jchVerteidigung;
	private ImageCheckbox m_jchTSI;
	private ImageCheckbox m_jchWages;
	private JButton m_jbUbernehmen;
	private JCheckBox m_jchInscription;
	private JCheckBox m_jchHelpLines;
	private JComboBox jcbAggType;
	private JComboBox m_jcbGruppe;
	private JTextField m_jtfNumberOfHRF;
	private StatistikPanel m_clStatistikPanel;
	private JPanel panel2;
	private boolean bSum = true;

	private final String sSum = "\u03A3 ";
	private final String sAvg = "\u00D8 ";

	private final String sumLeadership = sSum + HOVerwaltung.instance().getLanguageString("ls.player.leadership");
	private final String avgLeadership = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.leadership");

	@Override
	protected void initialize() {
		initComponents();
		addListeners();
		setNeedsRefresh(true);
		registerRefreshable(true);
	}

	@Override
	protected void update() {
		initStatistik();
	}

	private void addListeners() {
		m_jtfNumberOfHRF.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent focusEvent) {
				Helper.parseInt(HOMainFrame.instance(), m_jtfNumberOfHRF, false);
			}
		});

		m_jcbGruppe.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				initStatistik();
			}
		});

		jcbAggType.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				UserParameter gup = UserParameter.instance();
				HOVerwaltung hov = HOVerwaltung.instance();
				int selectedIndex = jcbAggType.getSelectedIndex();
				gup.statisticsTeamSumOrAverage = selectedIndex;
				if (selectedIndex == 0)
				{
					//sum
					m_jchLeadership.setText(sumLeadership);
					m_clStatistikPanel.setShow(sumLeadership, m_jchLeadership.isSelected());
					m_clStatistikPanel.setShow(avgLeadership, false);
				}
				else
				{
					// average
					m_jchLeadership.setText(avgLeadership);
					m_clStatistikPanel.setShow(avgLeadership, m_jchLeadership.isSelected());
					m_clStatistikPanel.setShow(sumLeadership, false);
				}
			}
		});


		ActionListener actionListener = e -> {
			UserParameter gup = UserParameter.instance();
			HOVerwaltung hov = HOVerwaltung.instance();
			if (e.getSource() == m_jbUbernehmen) {
				initStatistik();
			} else if (e.getSource() == m_jchHelpLines) {
				m_clStatistikPanel.setHilfslinien(m_jchHelpLines.isSelected());
				gup.statistikAlleHilfslinien = m_jchHelpLines.isSelected();
			} else if (e.getSource() == m_jchInscription) {
				m_clStatistikPanel.setBeschriftung(m_jchInscription.isSelected());
				gup.statistikAlleBeschriftung = m_jchInscription.isSelected();
			} else if (e.getSource() == m_jchLeadership.getCheckbox()) {
				if (bSum) {
					m_clStatistikPanel.setShow(sumLeadership, m_jchLeadership.isSelected());
					m_clStatistikPanel.setShow(avgLeadership, false);
				}
				else {
					m_clStatistikPanel.setShow(avgLeadership, m_jchLeadership.isSelected());
					m_clStatistikPanel.setShow(sumLeadership, false);
				}
				gup.statistikAlleFuehrung = m_jchLeadership.isSelected();
			} else if (e.getSource() == m_jchTSI.getCheckbox()) {
				m_clStatistikPanel.setShow("Marktwert", m_jchTSI.isSelected());
				gup.statistikAllTSI = m_jchTSI.isSelected();
			} else if (e.getSource() == m_jchWages.getCheckbox()) {
				m_clStatistikPanel.setShow("ls.player.wage", m_jchWages.isSelected());
				gup.statistikAllWages = m_jchWages.isSelected();
			} else if (e.getSource() == m_jchExperience.getCheckbox()) {
				m_clStatistikPanel.setShow("DurchschnittErfahrung",
						m_jchExperience.isSelected());
				gup.statistikAlleErfahrung = m_jchExperience.isSelected();
			} else if (e.getSource() == m_jchForm.getCheckbox()) {
				m_clStatistikPanel.setShow("DurchschnittForm", m_jchForm.isSelected());
				gup.statistikAlleForm = m_jchForm.isSelected();
			} else if (e.getSource() == m_jchStamina.getCheckbox()) {
				m_clStatistikPanel
						.setShow("ls.player.skill.stamina", m_jchStamina.isSelected());
				gup.statistikAlleKondition = m_jchStamina.isSelected();
			} else if (e.getSource() == m_jchLoyalty.getCheckbox()) {
				m_clStatistikPanel.setShow("ls.player.loyalty", m_jchLoyalty.isSelected());
				gup.statistikAllLoyalty = m_jchLoyalty.isSelected();
			} else if (e.getSource() == m_jchKeeper.getCheckbox()) {
				m_clStatistikPanel.setShow("ls.player.skill.keeper", m_jchKeeper.isSelected());
				gup.statistikAlleTorwart = m_jchKeeper.isSelected();
			} else if (e.getSource() == m_jchVerteidigung.getCheckbox()) {
				m_clStatistikPanel.setShow("ls.player.skill.defending",
						m_jchVerteidigung.isSelected());
				gup.statistikAlleVerteidigung = m_jchVerteidigung.isSelected();
			} else if (e.getSource() == m_jchPlaymaking.getCheckbox()) {
				m_clStatistikPanel.setShow("ls.player.skill.playmaking",
						m_jchPlaymaking.isSelected());
				gup.statistikAlleSpielaufbau = m_jchPlaymaking.isSelected();
			} else if (e.getSource() == m_jchPassing.getCheckbox()) {
				m_clStatistikPanel
						.setShow("ls.player.skill.passing", m_jchPassing.isSelected());
				gup.statistikAllePasspiel = m_jchPassing.isSelected();
			} else if (e.getSource() == m_jchWinger.getCheckbox()) {
				m_clStatistikPanel.setShow("ls.player.skill.winger", m_jchWinger.isSelected());
				gup.statistikAlleFluegel = m_jchWinger.isSelected();
			} else if (e.getSource() == m_jchScoring.getCheckbox()) {
				m_clStatistikPanel
						.setShow("ls.player.skill.scoring", m_jchScoring.isSelected());
				gup.statistikAlleTorschuss = m_jchScoring.isSelected();
			} else if (e.getSource() == m_jchSetPieces.getCheckbox()) {
				m_clStatistikPanel.setShow("ls.player.skill.setpieces",
						m_jchSetPieces.isSelected());
				gup.statistikAlleStandards = m_jchSetPieces.isSelected();
			}
		};

		m_jbUbernehmen.addActionListener(actionListener);
		m_jchHelpLines.addActionListener(actionListener);
		m_jchInscription.addActionListener(actionListener);
		m_jchLeadership.addActionListener(actionListener);
		m_jchExperience.addActionListener(actionListener);
		m_jchTSI.addActionListener(actionListener);
		m_jchWages.addActionListener(actionListener);
		m_jchForm.addActionListener(actionListener);
		m_jchStamina.addActionListener(actionListener);
		m_jchLoyalty.addActionListener(actionListener);
		m_jchVerteidigung.addActionListener(actionListener);
		m_jchKeeper.addActionListener(actionListener);
		m_jchPlaymaking.addActionListener(actionListener);
		m_jchPassing.addActionListener(actionListener);
		m_jchWinger.addActionListener(actionListener);
		m_jchScoring.addActionListener(actionListener);
		m_jchSetPieces.addActionListener(actionListener);
	}

	private void initComponents() {
		UserParameter gup = UserParameter.instance();
		HOVerwaltung hov = HOVerwaltung.instance();
		JLabel labelSquad, labelAggType;

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(2, 0, 2, 0);

		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints constraints2 = new GridBagConstraints();
		constraints2.fill = GridBagConstraints.HORIZONTAL;
		constraints2.weightx = 0.0;
		constraints2.weighty = 0.0;
		constraints2.insets = new Insets(2, 2, 2, 2);

		GridBagLayout layout2 = new GridBagLayout();
		panel2 = new ImagePanel();
		panel2.setLayout(layout2);

		labelSquad = new JLabel(hov.getLanguageString("Wochen"));
		constraints2.anchor = GridBagConstraints.WEST;
		constraints2.gridx = 0;
		constraints2.gridy = 1;
		constraints2.gridwidth = 1;
		layout2.setConstraints(labelSquad, constraints2);
		panel2.add(labelSquad);

		m_jtfNumberOfHRF = new JTextField(String.valueOf(gup.statistikAnzahlHRF));
		m_jtfNumberOfHRF.setHorizontalAlignment(SwingConstants.RIGHT);
		Dimension size = new Dimension(40, (int) m_jtfNumberOfHRF.getPreferredSize().getHeight());
		m_jtfNumberOfHRF.setPreferredSize(size);
		m_jtfNumberOfHRF.setMinimumSize(size);
		constraints2.gridx = 1;
		constraints2.gridy = 1;
		constraints2.fill = GridBagConstraints.NONE;
		layout2.setConstraints(m_jtfNumberOfHRF, constraints2);
		panel2.add(m_jtfNumberOfHRF);

		constraints2.gridx = 0;
		constraints2.gridy = 2;
		constraints2.gridwidth = 2;
		m_jbUbernehmen = new JButton(hov.getLanguageString("ls.button.apply"));
		layout2.setConstraints(m_jbUbernehmen, constraints2);
		m_jbUbernehmen.setToolTipText(hov.getLanguageString("tt_Statistik_HRFAnzahluebernehmen"));
		panel2.add(m_jbUbernehmen);


		labelSquad = new JLabel(hov.getLanguageString("Gruppe"));
		constraints2.gridx = 0;
		constraints2.gridy = 3;
		constraints2.gridwidth = 1;
		layout2.setConstraints(labelSquad, constraints2);
		panel2.add(labelSquad);
		constraints2.gridx = 1;
		constraints2.gridy = 3;
		m_jcbGruppe = new JComboBox(GroupTeamFactory.TEAMSMILIES);
		m_jcbGruppe.setRenderer(new core.gui.comp.renderer.SmilieListCellRenderer());
		m_jcbGruppe.setBackground(ThemeManager.getColor(TABLEENTRY_BG));
		m_jcbGruppe.setMaximumRowCount(25);
		m_jcbGruppe.setMaximumSize(new Dimension(200, 25));
		layout2.setConstraints(m_jcbGruppe, constraints2);
		panel2.add(m_jcbGruppe);

		labelAggType = new JLabel(hov.getLanguageString("ls.agg"));
		constraints2.gridx = 0;
		constraints2.gridy = 4;
		constraints2.gridwidth = 1;
		panel2.add(labelAggType, constraints2);
		String[] sAggType = { hov.getLanguageString("Gesamt"), hov.getLanguageString("Durchschnitt")};
		jcbAggType = new JComboBox<>(sAggType);
		jcbAggType.setSelectedIndex(gup.statisticsTeamSumOrAverage);
		layout2.setConstraints(jcbAggType, constraints2);
		jcbAggType.setToolTipText(hov.getLanguageString("choose_sum_or_average"));
		constraints2.gridx = 1;
		constraints2.gridy = 4;
		panel2.add(jcbAggType, constraints2);

		m_jchHelpLines = new JCheckBox(hov.getLanguageString("Hilflinien"),
				gup.statistikAlleHilfslinien);
		add(m_jchHelpLines, 5, layout2, constraints2);

		m_jchInscription = new JCheckBox(hov.getLanguageString("Beschriftung"),
				gup.statistikAlleBeschriftung);
		m_jchInscription.setOpaque(false);
		add(m_jchInscription, 6, layout2, constraints2);

		constraints2.insets = new Insets(20,0,0,0);  //top padding
		constraints2.weightx = 0.0;
		m_jchLeadership = new ImageCheckbox(hov.getLanguageString("ls.player.leadership"),
				ThemeManager.getColor(HOColorName.PALETTE15[0]), gup.statistikAlleFuehrung);
		add(m_jchLeadership, 7, layout2, constraints2);

		constraints2.insets = new Insets(0,0,0,0);
		m_jchExperience = new ImageCheckbox(hov.getLanguageString("ls.player.experience"),
				ThemeManager.getColor(HOColorName.PALETTE15[1]), gup.statistikAlleErfahrung);
		add(m_jchExperience, 8, layout2, constraints2);

		m_jchTSI = new ImageCheckbox(hov.getLanguageString("ls.player.tsi"),
				ThemeManager.getColor(HOColorName.PALETTE15[2]), gup.statistikAllTSI);
		add(m_jchTSI, 9, layout2, constraints2);

		m_jchWages = new ImageCheckbox(hov.getLanguageString("ls.player.wage"),
				ThemeManager.getColor(HOColorName.PALETTE15[3]), gup.statistikAllWages);
		add(m_jchWages, 10, layout2, constraints2);

		m_jchForm = new ImageCheckbox(hov.getLanguageString("ls.player.form"),
				ThemeManager.getColor(HOColorName.PALETTE15[4]), gup.statistikAlleForm);
		add(m_jchForm, 11, layout2, constraints2);

		m_jchStamina = new ImageCheckbox(hov.getLanguageString("ls.player.skill.stamina"),
				ThemeManager.getColor(HOColorName.PALETTE15[5]), gup.statistikAlleKondition);
		add(m_jchStamina, 12, layout2, constraints2);

		m_jchLoyalty = new ImageCheckbox(hov.getLanguageString("ls.player.loyalty"),
				ThemeManager.getColor(HOColorName.PALETTE15[6]), gup.statistikAllLoyalty);
		add(m_jchLoyalty, 13, layout2, constraints2);

		constraints2.insets = new Insets(20,0,0,0);  //top padding
		m_jchKeeper = new ImageCheckbox(hov.getLanguageString("ls.player.skill.keeper"),
				ThemeManager.getColor(HOColorName.PALETTE15[7]), gup.statistikAlleTorwart);
		add(m_jchKeeper, 14, layout2, constraints2);

		constraints2.insets = new Insets(0,0,0,0);
		m_jchVerteidigung = new ImageCheckbox(hov.getLanguageString("ls.player.skill.defending"),
				ThemeManager.getColor(HOColorName.PALETTE15[8]), gup.statistikAlleVerteidigung);
		add(m_jchVerteidigung, 15, layout2, constraints2);

		m_jchPlaymaking = new ImageCheckbox(hov.getLanguageString("ls.player.skill.playmaking"),
				ThemeManager.getColor(HOColorName.PALETTE15[9]), gup.statistikAlleSpielaufbau);
		add(m_jchPlaymaking, 16, layout2, constraints2);

		m_jchPassing = new ImageCheckbox(hov.getLanguageString("ls.player.skill.passing"),
				ThemeManager.getColor(HOColorName.PALETTE15[10]), gup.statistikAllePasspiel);
		add(m_jchPassing, 17, layout2, constraints2);

		m_jchWinger = new ImageCheckbox(hov.getLanguageString("ls.player.skill.winger"),
				ThemeManager.getColor(HOColorName.PALETTE15[11]), gup.statistikAlleFluegel);
		add(m_jchWinger, 18, layout2, constraints2);

		m_jchScoring = new ImageCheckbox(hov.getLanguageString("ls.player.skill.scoring"),
				ThemeManager.getColor(HOColorName.PALETTE15[12]), gup.statistikAlleTorschuss);
		add(m_jchScoring, 19, layout2, constraints2);

		m_jchSetPieces = new ImageCheckbox(hov.getLanguageString("ls.player.skill.setpieces"),
				ThemeManager.getColor(HOColorName.PALETTE15[13]), gup.statistikAlleStandards);
		add(m_jchSetPieces, 20, layout2, constraints2);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.weightx = 0.01;
		constraints.weighty = 0.001;
		constraints.anchor = GridBagConstraints.NORTH;
		layout.setConstraints(panel2, constraints);
		add(panel2);

		// Table
		final JPanel panel = new ImagePanel();
		panel.setLayout(new BorderLayout());

		m_clStatistikPanel = new StatistikPanel(false);
		panel.add(m_clStatistikPanel);

		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.weighty = 1.0;
		constraints.weightx = 1.0;
		constraints.anchor = GridBagConstraints.NORTH;
		panel.setBorder(BorderFactory.createLineBorder(ThemeManager.getColor(PANEL_BORDER)));
		layout.setConstraints(panel, constraints);
		add(panel);
	}

	private void add(JComponent comp, int y, GridBagLayout layout, GridBagConstraints constraints) {
		constraints.gridwidth = 2;
		constraints.gridx = 0;
		constraints.gridy = y;
		layout.setConstraints(comp, constraints);
		panel2.add(comp);
	}

	private void initStatistik() {
		try {
			int anzahlHRF = Integer.parseInt(m_jtfNumberOfHRF.getText());
			if (anzahlHRF <= 0) {
				anzahlHRF = 1;
			}
			UserParameter.instance().statistikAnzahlHRF = anzahlHRF;
			NumberFormat format = Helper.DEFAULTDEZIMALFORMAT;
			NumberFormat format2 = NumberFormat.getCurrencyInstance();

			double[][] statistikWerte = DBManager.instance().getDataForTeamStatisticsPanel(anzahlHRF,
							m_jcbGruppe.getSelectedItem().toString());
			StatistikModel[] models = new StatistikModel[statistikWerte.length];
			double[] data;
			// There are 28 values - the first 14 are the sum and the next 14 are the averaged values
			if (statistikWerte.length > 0) {
				models[0] = new StatistikModel(statistikWerte[0], sumLeadership,
						m_jchLeadership.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[0]),
						format, 5 / Helper.getMaxValue(statistikWerte[0]));

				models[14] = new StatistikModel(statistikWerte[14], avgLeadership,
						m_jchLeadership.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[0]),
						format);


				data = bSum ? statistikWerte[1] : statistikWerte[15];
				models[1] = new StatistikModel(data, "DurchschnittErfahrung",
						m_jchExperience.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[1]),
						format);

				data = bSum ? statistikWerte[2] : statistikWerte[16];
				models[2] = new StatistikModel(data, "DurchschnittForm",
						m_jchForm.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[4]), format);

				data = bSum ? statistikWerte[3] : statistikWerte[17];
				models[3] = new StatistikModel(data, "ls.player.skill.stamina",
						m_jchStamina.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[5]), format);

				data = bSum ? statistikWerte[4] : statistikWerte[18];
				models[4] = new StatistikModel(data, "ls.player.skill.keeper",
						m_jchKeeper.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[7]), format);

				data = bSum ? statistikWerte[5] : statistikWerte[19];
				models[5] = new StatistikModel(data, "ls.player.skill.defending",
						m_jchVerteidigung.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[8]),
						format);

				data = bSum ? statistikWerte[6] : statistikWerte[20];
				models[6] = new StatistikModel(data, "ls.player.skill.playmaking",
						m_jchPlaymaking.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[9]),
						format);

				data = bSum ? statistikWerte[7] : statistikWerte[21];
				models[7] = new StatistikModel(data, "ls.player.skill.passing",
						m_jchPassing.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[10]), format);

				data = bSum ? statistikWerte[8] : statistikWerte[22];
				models[8] = new StatistikModel(data, "ls.player.skill.winger",
						m_jchWinger.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[11]), format);

				data = bSum ? statistikWerte[9] : statistikWerte[23];
				models[9] = new StatistikModel(data, "ls.player.skill.scoring",
						m_jchScoring.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[12]), format);

				data = bSum ? statistikWerte[10] : statistikWerte[24];
				models[10] = new StatistikModel(data, "ls.player.skill.setpieces",
						m_jchSetPieces.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[13]), format);

				data = bSum ? statistikWerte[11] : statistikWerte[25];
				models[11] = new StatistikModel(data, "ls.player.loyalty",
						m_jchLoyalty.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[6]), format);

				double faktor = 20 / Helper.getMaxValue(statistikWerte[12]);
				data = bSum ? statistikWerte[12] : statistikWerte[26];
				models[12] = new StatistikModel(data, "Marktwert",
						m_jchTSI.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[2]), format,
						faktor);

				faktor = 20 / Helper.getMaxValue(statistikWerte[13]);
				data = bSum ? statistikWerte[13] : statistikWerte[27];
				models[13] = new StatistikModel(data, "ls.player.wage",
						m_jchTSI.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[3]), format2, faktor);
			}

			String[] yBezeichnungen = Helper.convertTimeMillisToFormatString(statistikWerte[28]);

			m_clStatistikPanel.setAllValues(models, yBezeichnungen, format, HOVerwaltung.instance()
					.getLanguageString("Wochen"), "", m_jchInscription.isSelected(), m_jchHelpLines
					.isSelected());
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}
}
