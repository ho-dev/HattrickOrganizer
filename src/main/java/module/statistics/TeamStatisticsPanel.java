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

import java.awt.*;
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
	private ImageCheckbox jcbXP;
	private ImageCheckbox jcbWinger;
	private ImageCheckbox jcbForm;
	private ImageCheckbox jcbLeadership;
	private ImageCheckbox jcbLoyalty;
	private ImageCheckbox jcbStamina;
	private ImageCheckbox jcbPassing;
	private ImageCheckbox jcbPlaymaking;
	private ImageCheckbox jcbSetPieces;
	private ImageCheckbox jcbScoring;
	private ImageCheckbox jcbKeeper;
	private ImageCheckbox jcbDefending;
	private ImageCheckbox jcbTSI;
	private ImageCheckbox jcbWage;
	private JButton jbApply;
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
	private final String sumXP = sSum + HOVerwaltung.instance().getLanguageString("ls.player.experience");
	private final String avgXP = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.experience");
	private final String sumTSI = sSum + HOVerwaltung.instance().getLanguageString("ls.player.tsi");
	private final String avgTSI = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.tsi");
	private final String sumWage = sSum + HOVerwaltung.instance().getLanguageString("ls.player.wage");
	private final String avgWage = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.wage");
	private final String avgForm = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.form");
	private final String avgStamina = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.skill.stamina");
	private final String avgLoyalty = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.loyalty");
	private final String sumGK = sSum + HOVerwaltung.instance().getLanguageString("ls.player.skill.keeper");
	private final String avgGK = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.skill.keeper");
	private final String sumDE = sSum + HOVerwaltung.instance().getLanguageString("ls.player.skill.defending");
	private final String avgDE = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.skill.defending");
	private final String sumPM = sSum + HOVerwaltung.instance().getLanguageString("ls.player.skill.playmaking");
	private final String avgPM = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.skill.playmaking");
	private final String sumPS = sSum + HOVerwaltung.instance().getLanguageString("ls.player.skill.passing");
	private final String avgPS = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.skill.passing");
	private final String sumWI = sSum + HOVerwaltung.instance().getLanguageString("ls.player.skill.winger");
	private final String avgWI = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.skill.winger");
	private final String sumSC = sSum + HOVerwaltung.instance().getLanguageString("ls.player.skill.scoring");
	private final String avgSC = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.skill.scoring");
	private final String sumSP = sSum + HOVerwaltung.instance().getLanguageString("ls.player.skill.setpieces");
	private final String avgSP = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.skill.setpieces");

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
					bSum = true;

					jcbLeadership.setText(sumLeadership);
					m_clStatistikPanel.setShow(sumLeadership, jcbLeadership.isSelected());
					m_clStatistikPanel.setShow(avgLeadership, false);

					jcbXP.setText(sumXP);
					m_clStatistikPanel.setShow(sumXP, jcbXP.isSelected());
					m_clStatistikPanel.setShow(avgXP, false);

					jcbTSI.setText(sumTSI);
					m_clStatistikPanel.setShow(sumTSI, jcbTSI.isSelected());
					m_clStatistikPanel.setShow(avgTSI, false);

					jcbWage.setText(sumWage);
					m_clStatistikPanel.setShow(sumWage, jcbWage.isSelected());
					m_clStatistikPanel.setShow(avgWage, false);

					jcbKeeper.setText(sumGK);
					m_clStatistikPanel.setShow(sumGK, jcbKeeper.isSelected());
					m_clStatistikPanel.setShow(avgGK, false);

					jcbDefending.setText(sumDE);
					m_clStatistikPanel.setShow(sumDE, jcbDefending.isSelected());
					m_clStatistikPanel.setShow(avgDE, false);

					jcbPlaymaking.setText(sumPM);
					m_clStatistikPanel.setShow(sumPM, jcbPlaymaking.isSelected());
					m_clStatistikPanel.setShow(avgPM, false);

					jcbPassing.setText(sumPS);
					m_clStatistikPanel.setShow(sumPS, jcbPassing.isSelected());
					m_clStatistikPanel.setShow(avgPS, false);

					jcbWinger.setText(sumWI);
					m_clStatistikPanel.setShow(sumWI, jcbWinger.isSelected());
					m_clStatistikPanel.setShow(avgWI, false);

					jcbScoring.setText(sumSC);
					m_clStatistikPanel.setShow(sumSC, jcbScoring.isSelected());
					m_clStatistikPanel.setShow(avgSC, false);

					jcbSetPieces.setText(sumSP);
					m_clStatistikPanel.setShow(sumSP, jcbSetPieces.isSelected());
					m_clStatistikPanel.setShow(avgSP, false);

				}
				else
				{
					// average
					bSum = false;

					jcbLeadership.setText(avgLeadership);
					m_clStatistikPanel.setShow(avgLeadership, jcbLeadership.isSelected());
					m_clStatistikPanel.setShow(sumLeadership, false);

					jcbXP.setText(avgXP);
					m_clStatistikPanel.setShow(avgXP, jcbXP.isSelected());
					m_clStatistikPanel.setShow(sumXP, false);

					jcbTSI.setText(avgTSI);
					m_clStatistikPanel.setShow(avgTSI, jcbTSI.isSelected());
					m_clStatistikPanel.setShow(sumTSI, false);

					jcbWage.setText(avgWage);
					m_clStatistikPanel.setShow(avgWage, jcbWage.isSelected());
					m_clStatistikPanel.setShow(sumWage, false);

					jcbKeeper.setText(avgGK);
					m_clStatistikPanel.setShow(avgGK, jcbKeeper.isSelected());
					m_clStatistikPanel.setShow(sumGK, false);

					jcbDefending.setText(avgDE);
					m_clStatistikPanel.setShow(avgDE, jcbDefending.isSelected());
					m_clStatistikPanel.setShow(sumDE, false);

					jcbPlaymaking.setText(avgPM);
					m_clStatistikPanel.setShow(avgPM, jcbPlaymaking.isSelected());
					m_clStatistikPanel.setShow(sumPM, false);

					jcbPassing.setText(avgPS);
					m_clStatistikPanel.setShow(avgPS, jcbPassing.isSelected());
					m_clStatistikPanel.setShow(sumPS, false);

					jcbWinger.setText(avgWI);
					m_clStatistikPanel.setShow(avgWI, jcbWinger.isSelected());
					m_clStatistikPanel.setShow(sumWI, false);

					jcbScoring.setText(avgSC);
					m_clStatistikPanel.setShow(avgSC, jcbScoring.isSelected());
					m_clStatistikPanel.setShow(sumSC, false);

					jcbSetPieces.setText(avgSP);
					m_clStatistikPanel.setShow(avgSP, jcbSetPieces.isSelected());
					m_clStatistikPanel.setShow(sumSP, false);
				}
			}
		});


		ActionListener actionListener = e -> {
			UserParameter gup = UserParameter.instance();
			HOVerwaltung hov = HOVerwaltung.instance();
			if (e.getSource() == jbApply) {
				initStatistik();
			} else if (e.getSource() == m_jchHelpLines) {
				m_clStatistikPanel.setHilfslinien(m_jchHelpLines.isSelected());
				gup.statistikAlleHilfslinien = m_jchHelpLines.isSelected();
			} else if (e.getSource() == m_jchInscription) {
				m_clStatistikPanel.setBeschriftung(m_jchInscription.isSelected());
				gup.statistikAlleBeschriftung = m_jchInscription.isSelected();
			}
			// Leadership =========================================================
			else if (e.getSource() == jcbLeadership.getCheckbox()) {
				if (bSum) {
					m_clStatistikPanel.setShow(sumLeadership, jcbLeadership.isSelected());
					m_clStatistikPanel.setShow(avgLeadership, false);
				}
				else {
					m_clStatistikPanel.setShow(avgLeadership, jcbLeadership.isSelected());
					m_clStatistikPanel.setShow(sumLeadership, false);
				}
				gup.statistikAlleFuehrung = jcbLeadership.isSelected();
			}
			// TSI ==================================================================
			else if (e.getSource() == jcbTSI.getCheckbox()) {
				if (bSum) {
					m_clStatistikPanel.setShow(sumTSI, jcbTSI.isSelected());
					m_clStatistikPanel.setShow(avgTSI, false);
				}
				else{
					m_clStatistikPanel.setShow(avgTSI, jcbTSI.isSelected());
					m_clStatistikPanel.setShow(sumTSI, false);
				}
				gup.statistikAllTSI = jcbTSI.isSelected();
			}
			// WAGE ================================================================
			else if (e.getSource() == jcbWage.getCheckbox()) {
				if (bSum) {
					m_clStatistikPanel.setShow(sumWage, jcbWage.isSelected());
					m_clStatistikPanel.setShow(avgWage, false);
				}
				else{
					m_clStatistikPanel.setShow(avgWage, jcbWage.isSelected());
					m_clStatistikPanel.setShow(sumWage, false);
				}
				gup.statistikAllWages = jcbWage.isSelected();
			}
			// Experience =========================================================
			else if (e.getSource() == jcbXP.getCheckbox()) {
				if (bSum) {
					m_clStatistikPanel.setShow(sumXP, jcbXP.isSelected());
					m_clStatistikPanel.setShow(avgXP, false);
				}
				else{
					m_clStatistikPanel.setShow(avgXP, jcbXP.isSelected());
					m_clStatistikPanel.setShow(sumXP, false);
				}
				gup.statistikAlleErfahrung = jcbXP.isSelected();
			}
			// Form ================================================================
			else if (e.getSource() == jcbForm.getCheckbox()) {
				m_clStatistikPanel.setShow(avgForm, jcbForm.isSelected());
				gup.statistikAlleForm = jcbForm.isSelected();
			}
			// Stamina ================================================================
			else if (e.getSource() == jcbStamina.getCheckbox()) {
				m_clStatistikPanel
						.setShow(avgStamina, jcbStamina.isSelected());
				gup.statistikAlleKondition = jcbStamina.isSelected();
			}
			// Loyalty ================================================================
			else if (e.getSource() == jcbLoyalty.getCheckbox()) {
				m_clStatistikPanel.setShow(avgLoyalty, jcbLoyalty.isSelected());
				gup.statistikAllLoyalty = jcbLoyalty.isSelected();
			}
			// Keeper ============================================================================
			else if (e.getSource() == jcbKeeper.getCheckbox()) {
				if (bSum) {
					m_clStatistikPanel.setShow(sumGK, jcbKeeper.isSelected());
					m_clStatistikPanel.setShow(avgGK, false);
				}
				else{
					m_clStatistikPanel.setShow(avgGK, jcbKeeper.isSelected());
					m_clStatistikPanel.setShow(sumGK, false);
				}
				gup.statistikAlleTorwart = jcbKeeper.isSelected();
			}
			// Defending ============================================================================
			else if (e.getSource() == jcbDefending.getCheckbox()) {
				if (bSum) {
					m_clStatistikPanel.setShow(sumDE, jcbDefending.isSelected());
					m_clStatistikPanel.setShow(avgDE, false);
				}
				else{
					m_clStatistikPanel.setShow(avgDE, jcbDefending.isSelected());
					m_clStatistikPanel.setShow(sumDE, false);
				}
				gup.statistikAlleVerteidigung = jcbDefending.isSelected();
			}
			// Playmaking ============================================================================
			else if (e.getSource() == jcbPlaymaking.getCheckbox()) {
				if (bSum) {
					m_clStatistikPanel.setShow(sumPM, jcbPlaymaking.isSelected());
					m_clStatistikPanel.setShow(avgPM, false);
				}
				else{
					m_clStatistikPanel.setShow(avgPM, jcbPlaymaking.isSelected());
					m_clStatistikPanel.setShow(sumPM, false);
				}
				gup.statistikAlleSpielaufbau = jcbPlaymaking.isSelected();
			}
			// Passing ============================================================================
			else if (e.getSource() == jcbPassing.getCheckbox()) {
				if (bSum) {
					m_clStatistikPanel.setShow(sumPS, jcbPassing.isSelected());
					m_clStatistikPanel.setShow(avgPS, false);
				}
				else{
					m_clStatistikPanel.setShow(avgPS, jcbPassing.isSelected());
					m_clStatistikPanel.setShow(sumPS, false);
				}
				gup.statistikAllePasspiel = jcbPassing.isSelected();
			}
			// Winger ============================================================================
			else if (e.getSource() == jcbWinger.getCheckbox()) {
				if (bSum) {
					m_clStatistikPanel.setShow(sumWI, jcbWinger.isSelected());
					m_clStatistikPanel.setShow(avgWI, false);
				}
				else{
					m_clStatistikPanel.setShow(avgWI, jcbWinger.isSelected());
					m_clStatistikPanel.setShow(sumWI, false);
				}
				gup.statistikAlleFluegel = jcbWinger.isSelected();
			}
			// Scoring ============================================================================
			else if (e.getSource() == jcbScoring.getCheckbox()) {
				if (bSum) {
					m_clStatistikPanel.setShow(sumSC, jcbScoring.isSelected());
					m_clStatistikPanel.setShow(avgSC, false);
				}
				else{
					m_clStatistikPanel.setShow(avgSC, jcbScoring.isSelected());
					m_clStatistikPanel.setShow(sumSC, false);
				}
				gup.statistikAlleTorschuss = jcbScoring.isSelected();
			}
			// SetPieces ============================================================================
			else if (e.getSource() == jcbSetPieces.getCheckbox()) {
				if (bSum) {
					m_clStatistikPanel.setShow(sumSP, jcbSetPieces.isSelected());
					m_clStatistikPanel.setShow(avgSP, false);
				}
				else{
					m_clStatistikPanel.setShow(avgSP, jcbSetPieces.isSelected());
					m_clStatistikPanel.setShow(sumSP, false);
				}
				gup.statistikAlleStandards= jcbSetPieces.isSelected();
			}
		};

		jbApply.addActionListener(actionListener);
		m_jchHelpLines.addActionListener(actionListener);
		m_jchInscription.addActionListener(actionListener);
		jcbLeadership.addActionListener(actionListener);
		jcbXP.addActionListener(actionListener);
		jcbTSI.addActionListener(actionListener);
		jcbWage.addActionListener(actionListener);
		jcbForm.addActionListener(actionListener);
		jcbStamina.addActionListener(actionListener);
		jcbLoyalty.addActionListener(actionListener);
		jcbDefending.addActionListener(actionListener);
		jcbKeeper.addActionListener(actionListener);
		jcbPlaymaking.addActionListener(actionListener);
		jcbPassing.addActionListener(actionListener);
		jcbWinger.addActionListener(actionListener);
		jcbScoring.addActionListener(actionListener);
		jcbSetPieces.addActionListener(actionListener);
	}

	private void initComponents() {
		UserParameter gup = UserParameter.instance();
		HOVerwaltung hov = HOVerwaltung.instance();
		JLabel labelSquad, labelAggType;
		String textLabel;

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
		jbApply = new JButton(hov.getLanguageString("ls.button.apply"));
		layout2.setConstraints(jbApply, constraints2);
		jbApply.setToolTipText(hov.getLanguageString("tt_Statistik_HRFAnzahluebernehmen"));
		panel2.add(jbApply);


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
		bSum = (gup.statisticsTeamSumOrAverage == 0);

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

		// LEADERSIP =============================================================================================
		textLabel = bSum ? sumLeadership : avgLeadership;
		jcbLeadership = new ImageCheckbox(textLabel, getColor(0), gup.statistikAlleFuehrung);
		add(jcbLeadership, 7, layout2, constraints2);

		// EXPERIENCE =============================================================================================
		constraints2.insets = new Insets(0,0,0,0);
		textLabel = bSum ? sumXP : avgXP;
		jcbXP = new ImageCheckbox(textLabel, getColor(1), gup.statistikAlleErfahrung);
		add(jcbXP, 8, layout2, constraints2);

		// TSI =============================================================================================
		textLabel = bSum ? sumTSI : avgTSI;
		jcbTSI = new ImageCheckbox(textLabel, getColor(2), gup.statistikAllTSI);
		add(jcbTSI, 9, layout2, constraints2);

		// WAGE ============================================================================================
		textLabel = bSum ? sumWage : avgWage;
		jcbWage = new ImageCheckbox(textLabel, getColor(3), gup.statistikAllWages);
		add(jcbWage, 10, layout2, constraints2);

		// FORM ============================================================================================
		jcbForm = new ImageCheckbox(avgForm, getColor(4), gup.statistikAlleForm);
		add(jcbForm, 11, layout2, constraints2);

		// STAMINA ============================================================================================
		jcbStamina = new ImageCheckbox(avgStamina, getColor(5), gup.statistikAlleKondition);
		add(jcbStamina, 12, layout2, constraints2);

		// LOYALTY ============================================================================================
		jcbLoyalty = new ImageCheckbox(avgLoyalty, getColor(6), gup.statistikAllLoyalty);
		add(jcbLoyalty, 13, layout2, constraints2);

		// KEEPER ============================================================================================
		constraints2.insets = new Insets(20,0,0,0);  //top padding
		textLabel = bSum ? sumGK : avgGK;
		jcbKeeper = new ImageCheckbox(textLabel, getColor(7), gup.statistikAlleTorwart);
		add(jcbKeeper, 14, layout2, constraints2);

		// DEFENDING ============================================================================================
		constraints2.insets = new Insets(0,0,0,0);
		textLabel = bSum ? sumDE : avgDE;
		jcbDefending = new ImageCheckbox(textLabel, getColor(8), gup.statistikAlleVerteidigung);
		add(jcbDefending, 15, layout2, constraints2);

		// PLAYMAKING ============================================================================================
		textLabel = bSum ? sumPM : avgPM;
		jcbPlaymaking = new ImageCheckbox(textLabel, getColor(9), gup.statistikAlleSpielaufbau);
		add(jcbPlaymaking, 16, layout2, constraints2);

		// PASSING ============================================================================================
		textLabel = bSum ? sumPS : avgPS;
		jcbPassing = new ImageCheckbox(textLabel, getColor(10), gup.statistikAllePasspiel);
		add(jcbPassing, 17, layout2, constraints2);

		// WINGER ============================================================================================
		textLabel = bSum ? sumWI : avgWI;
		jcbWinger = new ImageCheckbox(textLabel, getColor(11), gup.statistikAlleFluegel);
		add(jcbWinger, 18, layout2, constraints2);

		// SCORING ============================================================================================
		textLabel = bSum ? sumSC : avgSC;
		jcbScoring = new ImageCheckbox(textLabel, getColor(12), gup.statistikAlleTorschuss);
		add(jcbScoring, 19, layout2, constraints2);

		// SETPIECES ============================================================================================
		textLabel = bSum ? sumSP : avgSP;
		jcbSetPieces = new ImageCheckbox(textLabel, getColor(13), gup.statistikAlleStandards);
		add(jcbSetPieces, 20, layout2, constraints2);

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
			NumberFormat fmt2 = Helper.getNumberFormat(true, 0);
			NumberFormat fmt3 = Helper.getNumberFormat(false, 0);


			double[][] statistikWerte = DBManager.instance().getDataForTeamStatisticsPanel(anzahlHRF,
							m_jcbGruppe.getSelectedItem().toString());
			StatistikModel[] models = new StatistikModel[statistikWerte.length];

			// There are 28 values - the first 14 are the sum and the next 14 are the averaged values
			if (statistikWerte.length > 0) {

				// LEADERSHIP ========================================================================
				models[0] = new StatistikModel(statistikWerte[0], sumLeadership, jcbLeadership.isSelected() && bSum,
						  getColor(0), fmt3, 5 / Helper.getMaxValue(statistikWerte[0]));

				models[14] = new StatistikModel(statistikWerte[14], avgLeadership, jcbLeadership.isSelected(),
						       getColor(0), format);


				// XP ========================================================================
				models[1] = new StatistikModel(statistikWerte[1], sumXP, jcbXP.isSelected() && bSum, getColor(1),
						fmt3, 7 / Helper.getMaxValue(statistikWerte[1]));

				models[15] = new StatistikModel(statistikWerte[15], avgXP, jcbXP.isSelected() && !bSum,
						        getColor(1), format);

				// TSI ========================================================================
				models[12] = new StatistikModel(statistikWerte[12], sumTSI, jcbTSI.isSelected() && bSum,
						getColor(2), fmt3,	19 / Helper.getMaxValue(statistikWerte[12]));

				models[26] = new StatistikModel(statistikWerte[26], avgTSI, jcbTSI.isSelected() && !bSum,
						        getColor(2), format, 19 / Helper.getMaxValue(statistikWerte[26]));

				// WAGE ========================================================================
				models[13] = new StatistikModel(statistikWerte[13], sumWage,jcbWage.isSelected() && bSum,
						getColor(3), fmt3, 15 / Helper.getMaxValue(statistikWerte[13]));

				models[27] = new StatistikModel(statistikWerte[27], avgWage, jcbWage.isSelected() && !bSum,
						     getColor(3), fmt2, 15 / Helper.getMaxValue(statistikWerte[27]));


				// FORM (only avg statistics because sum statistics is meaningless in that case) =======================
				models[16] = new StatistikModel(statistikWerte[16], avgForm, jcbForm.isSelected(),
						          getColor(4), format);

				// STAMINA (only avg statistics because sum statistics is meaningless in that case) ====================
				models[17] = new StatistikModel(statistikWerte[17], avgStamina, jcbStamina.isSelected(),
										getColor(5), format);

				// LOYALTY (only avg statistics because sum statistics is meaningless in that case) ====================
				models[25] = new StatistikModel(statistikWerte[25], avgLoyalty, jcbLoyalty.isSelected(),
						getColor(6), format);

				// KEEPER ========================================================================
				double maxSKill = Helper.getMaxValue(statistikWerte[4]);
				maxSKill = Math.max(maxSKill, Helper.getMaxValue(statistikWerte[5]));
				maxSKill = Math.max(maxSKill, Helper.getMaxValue(statistikWerte[6]));
				maxSKill = Math.max(maxSKill, Helper.getMaxValue(statistikWerte[7]));
				maxSKill = Math.max(maxSKill, Helper.getMaxValue(statistikWerte[8]));
				maxSKill = Math.max(maxSKill, Helper.getMaxValue(statistikWerte[9]));
				maxSKill = Math.max(maxSKill, Helper.getMaxValue(statistikWerte[10]));
				double factor = 19.0/maxSKill;

				models[4] = new StatistikModel(statistikWerte[4], sumGK, jcbKeeper.isSelected() && bSum,
						getColor(7), fmt3, factor);

				models[18] = new StatistikModel(statistikWerte[18], avgGK, jcbKeeper.isSelected() && !bSum,
						getColor(7), format);

				// DEFENDING ========================================================================
				models[5] = new StatistikModel(statistikWerte[5], sumDE, jcbDefending.isSelected() && bSum,
						getColor(8), fmt3, factor);
				models[19] = new StatistikModel(statistikWerte[19], avgDE, jcbDefending.isSelected() && !bSum,
						getColor(8), format);

				// PLAYMAKING ========================================================================
				models[6] = new StatistikModel(statistikWerte[6], sumPM, jcbPlaymaking.isSelected() && bSum,
						getColor(9), fmt3, factor);
				models[20] = new StatistikModel(statistikWerte[20], avgPM, jcbPlaymaking.isSelected() && !bSum,
						getColor(9), format);

				// PASSING ========================================================================
				models[7] = new StatistikModel(statistikWerte[7], sumPS, jcbPassing.isSelected() && bSum,
						getColor(10), fmt3, factor);
				models[21] = new StatistikModel(statistikWerte[21], avgPS, jcbPassing.isSelected() && !bSum,
						getColor(10), format);

				// WINGER ========================================================================
				models[8] = new StatistikModel(statistikWerte[8], sumWI, jcbWinger.isSelected() && bSum,
						getColor(11), fmt3, factor);
				models[22] = new StatistikModel(statistikWerte[22], avgWI, jcbWinger.isSelected() && !bSum,
						getColor(11), format);

				// SCORING ========================================================================
				models[9] = new StatistikModel(statistikWerte[9], sumSC, jcbScoring.isSelected() && bSum,
						getColor(12), fmt3, factor);
				models[23] = new StatistikModel(statistikWerte[23], avgSC, jcbScoring.isSelected() && !bSum,
						getColor(12), format);

				// SETPIECES ========================================================================
				models[10] = new StatistikModel(statistikWerte[10], sumSP, jcbSetPieces.isSelected() && bSum,
						getColor(13), fmt3, factor);
				models[24] = new StatistikModel(statistikWerte[24], avgSP, jcbSetPieces.isSelected() && !bSum,
						getColor(13), format);

			}

			String[] yBezeichnungen = Helper.convertTimeMillisToFormatString(statistikWerte[28]);

			m_clStatistikPanel.setAllValues(models, yBezeichnungen, format, HOVerwaltung.instance()
					.getLanguageString("Wochen"), "", m_jchInscription.isSelected(), m_jchHelpLines
					.isSelected());
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}


	private Color getColor(int i) {
		return ThemeManager.getColor(HOColorName.PALETTE15[i]);
	}

}
