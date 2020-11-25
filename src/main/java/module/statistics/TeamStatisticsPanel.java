package module.statistics;

import static core.gui.theme.HOColorName.PANEL_BORDER;
import static core.gui.theme.HOColorName.TABLEENTRY_BG;
import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.util.chart.LinesChartDataModel;
import core.gui.theme.GroupTeamFactory;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.HOLogger;
import core.util.Helper;
import core.util.chart.HOLinesChart;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;
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
	private JCheckBox jcbHelpLines;
	private JComboBox<String> jcbAggType;
	private JComboBox<String> jcbTeam;
	private JTextField jtfNumberOfHRF;
	private HOLinesChart mChart;
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
	private final String sumForm = sSum + HOVerwaltung.instance().getLanguageString("ls.player.form");
	private final String avgStamina = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.skill.stamina");
	private final String sumStamina = sSum + HOVerwaltung.instance().getLanguageString("ls.player.skill.stamina");
	private final String avgLoyalty = sAvg + HOVerwaltung.instance().getLanguageString("ls.player.loyalty");
	private final String sumLoyalty = sSum + HOVerwaltung.instance().getLanguageString("ls.player.loyalty");
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
		jtfNumberOfHRF.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent focusEvent) {
				Helper.parseInt(HOMainFrame.instance(), jtfNumberOfHRF, false);
			}
		});

		jcbTeam.addItemListener(e -> {
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

					// if sum, no binding on left axis
					mChart.setYAxisMin(1, null);
					mChart.setYAxisMax(1, null);

					jcbLeadership.setText(sumLeadership);
					mChart.setShow(sumLeadership, jcbLeadership.isSelected());
					mChart.setShow(avgLeadership, false);

					jcbXP.setText(sumXP);
					mChart.setShow(sumXP, jcbXP.isSelected());
					mChart.setShow(avgXP, false);

					jcbForm.setText(sumForm);
					mChart.setShow(sumForm, jcbForm.isSelected());
					mChart.setShow(avgForm, false);

					jcbStamina.setText(sumStamina);
					mChart.setShow(sumStamina, jcbStamina.isSelected());
					mChart.setShow(avgStamina, false);

					jcbLoyalty.setText(sumLoyalty);
					mChart.setShow(sumLoyalty, jcbLoyalty.isSelected());
					mChart.setShow(avgLoyalty, false);

					jcbTSI.setText(sumTSI);
					mChart.setShow(sumTSI, jcbTSI.isSelected());
					mChart.setShow(avgTSI, false);

					jcbWage.setText(sumWage);
					mChart.setShow(sumWage, jcbWage.isSelected());
					mChart.setShow(avgWage, false);

					jcbKeeper.setText(sumGK);
					mChart.setShow(sumGK, jcbKeeper.isSelected());
					mChart.setShow(avgGK, false);

					jcbDefending.setText(sumDE);
					mChart.setShow(sumDE, jcbDefending.isSelected());
					mChart.setShow(avgDE, false);

					jcbPlaymaking.setText(sumPM);
					mChart.setShow(sumPM, jcbPlaymaking.isSelected());
					mChart.setShow(avgPM, false);

					jcbPassing.setText(sumPS);
					mChart.setShow(sumPS, jcbPassing.isSelected());
					mChart.setShow(avgPS, false);

					jcbWinger.setText(sumWI);
					mChart.setShow(sumWI, jcbWinger.isSelected());
					mChart.setShow(avgWI, false);

					jcbScoring.setText(sumSC);
					mChart.setShow(sumSC, jcbScoring.isSelected());
					mChart.setShow(avgSC, false);

					jcbSetPieces.setText(sumSP);
					mChart.setShow(sumSP, jcbSetPieces.isSelected());
					mChart.setShow(avgSP, false);

				}
				else
				{
					// average
					bSum = false;

					// if average left axis binded between 0 and 20
					mChart.setYAxisMin(1, 0d);
					mChart.setYAxisMax(1, 20d);

					jcbLeadership.setText(avgLeadership);
					mChart.setShow(avgLeadership, jcbLeadership.isSelected());
					mChart.setShow(sumLeadership, false);

					jcbXP.setText(avgXP);
					mChart.setShow(avgXP, jcbXP.isSelected());
					mChart.setShow(sumXP, false);

					jcbForm.setText(avgForm);
					mChart.setShow(avgForm, jcbForm.isSelected());
					mChart.setShow(sumForm, false);

					jcbStamina.setText(avgStamina);
					mChart.setShow(avgStamina, jcbStamina.isSelected());
					mChart.setShow(sumStamina, false);

					jcbLoyalty.setText(avgLoyalty);
					mChart.setShow(avgLoyalty, jcbLoyalty.isSelected());
					mChart.setShow(sumLoyalty, false);

					jcbTSI.setText(avgTSI);
					mChart.setShow(avgTSI, jcbTSI.isSelected());
					mChart.setShow(sumTSI, false);

					jcbWage.setText(avgWage);
					mChart.setShow(avgWage, jcbWage.isSelected());
					mChart.setShow(sumWage, false);

					jcbKeeper.setText(avgGK);
					mChart.setShow(avgGK, jcbKeeper.isSelected());
					mChart.setShow(sumGK, false);

					jcbDefending.setText(avgDE);
					mChart.setShow(avgDE, jcbDefending.isSelected());
					mChart.setShow(sumDE, false);

					jcbPlaymaking.setText(avgPM);
					mChart.setShow(avgPM, jcbPlaymaking.isSelected());
					mChart.setShow(sumPM, false);

					jcbPassing.setText(avgPS);
					mChart.setShow(avgPS, jcbPassing.isSelected());
					mChart.setShow(sumPS, false);

					jcbWinger.setText(avgWI);
					mChart.setShow(avgWI, jcbWinger.isSelected());
					mChart.setShow(sumWI, false);

					jcbScoring.setText(avgSC);
					mChart.setShow(avgSC, jcbScoring.isSelected());
					mChart.setShow(sumSC, false);

					jcbSetPieces.setText(avgSP);
					mChart.setShow(avgSP, jcbSetPieces.isSelected());
					mChart.setShow(sumSP, false);
				}
			}
		});


		ActionListener actionListener = e -> {
			UserParameter gup = UserParameter.instance();
			HOVerwaltung hov = HOVerwaltung.instance();
			if (e.getSource() == jbApply) {
				initStatistik();
			} else if (e.getSource() == jcbHelpLines) {
				mChart.setHelpLines(jcbHelpLines.isSelected());
				gup.statistikAlleHilfslinien = jcbHelpLines.isSelected();
			}
			// Leadership =========================================================
			else if (e.getSource() == jcbLeadership.getCheckbox()) {
				if (bSum) {
					mChart.setShow(sumLeadership, jcbLeadership.isSelected());
					mChart.setShow(avgLeadership, false);
				}
				else {
					mChart.setShow(avgLeadership, jcbLeadership.isSelected());
					mChart.setShow(sumLeadership, false);
				}
				gup.statistikAlleFuehrung = jcbLeadership.isSelected();
			}
			// TSI ==================================================================
			else if (e.getSource() == jcbTSI.getCheckbox()) {
				if (bSum) {
					mChart.setShow(sumTSI, jcbTSI.isSelected());
					mChart.setShow(avgTSI, false);
				}
				else{
					mChart.setShow(avgTSI, jcbTSI.isSelected());
					mChart.setShow(sumTSI, false);
				}
				gup.statistikAllTSI = jcbTSI.isSelected();
			}
			// WAGE ================================================================
			else if (e.getSource() == jcbWage.getCheckbox()) {
				if (bSum) {
					mChart.setShow(sumWage, jcbWage.isSelected());
					mChart.setShow(avgWage, false);
				}
				else{
					mChart.setShow(avgWage, jcbWage.isSelected());
					mChart.setShow(sumWage, false);
				}
				gup.statistikAllWages = jcbWage.isSelected();
			}
			// Experience =========================================================
			else if (e.getSource() == jcbXP.getCheckbox()) {
				if (bSum) {
					mChart.setShow(sumXP, jcbXP.isSelected());
					mChart.setShow(avgXP, false);
				}
				else{
					mChart.setShow(avgXP, jcbXP.isSelected());
					mChart.setShow(sumXP, false);
				}
				gup.statistikAlleErfahrung = jcbXP.isSelected();
			}
			// Form ================================================================
			else if (e.getSource() == jcbForm.getCheckbox()) {
				if (bSum) {
					mChart.setShow(sumForm, jcbForm.isSelected());
					mChart.setShow(avgForm, false);
				}
				else{
					mChart.setShow(avgForm, jcbForm.isSelected());
					mChart.setShow(sumForm, false);
				}
				gup.statistikAlleForm = jcbForm.isSelected();
			}
			// Stamina ================================================================
			else if (e.getSource() == jcbStamina.getCheckbox()) {
				if (bSum) {
					mChart.setShow(sumStamina, jcbStamina.isSelected());
					mChart.setShow(avgStamina, false);
				}
				else{
					mChart.setShow(avgStamina, jcbStamina.isSelected());
					mChart.setShow(sumStamina, false);
				}
				gup.statistikAlleKondition = jcbStamina.isSelected();
			}
			// Loyalty ================================================================
			else if (e.getSource() == jcbLoyalty.getCheckbox()) {
				if (bSum) {
					mChart.setShow(sumLoyalty, jcbLoyalty.isSelected());
					mChart.setShow(avgLoyalty, false);
				}
				else{
					mChart.setShow(avgLoyalty, jcbLoyalty.isSelected());
					mChart.setShow(sumLoyalty, false);
				}
				gup.statistikAllLoyalty = jcbLoyalty.isSelected();
			}
			// Keeper ============================================================================
			else if (e.getSource() == jcbKeeper.getCheckbox()) {
				if (bSum) {
					mChart.setShow(sumGK, jcbKeeper.isSelected());
					mChart.setShow(avgGK, false);
				}
				else{
					mChart.setShow(avgGK, jcbKeeper.isSelected());
					mChart.setShow(sumGK, false);
				}
				gup.statistikAlleTorwart = jcbKeeper.isSelected();
			}
			// Defending ============================================================================
			else if (e.getSource() == jcbDefending.getCheckbox()) {
				if (bSum) {
					mChart.setShow(sumDE, jcbDefending.isSelected());
					mChart.setShow(avgDE, false);
				}
				else{
					mChart.setShow(avgDE, jcbDefending.isSelected());
					mChart.setShow(sumDE, false);
				}
				gup.statistikAlleVerteidigung = jcbDefending.isSelected();
			}
			// Playmaking ============================================================================
			else if (e.getSource() == jcbPlaymaking.getCheckbox()) {
				if (bSum) {
					mChart.setShow(sumPM, jcbPlaymaking.isSelected());
					mChart.setShow(avgPM, false);
				}
				else{
					mChart.setShow(avgPM, jcbPlaymaking.isSelected());
					mChart.setShow(sumPM, false);
				}
				gup.statistikAlleSpielaufbau = jcbPlaymaking.isSelected();
			}
			// Passing ============================================================================
			else if (e.getSource() == jcbPassing.getCheckbox()) {
				if (bSum) {
					mChart.setShow(sumPS, jcbPassing.isSelected());
					mChart.setShow(avgPS, false);
				}
				else{
					mChart.setShow(avgPS, jcbPassing.isSelected());
					mChart.setShow(sumPS, false);
				}
				gup.statistikAllePasspiel = jcbPassing.isSelected();
			}
			// Winger ============================================================================
			else if (e.getSource() == jcbWinger.getCheckbox()) {
				if (bSum) {
					mChart.setShow(sumWI, jcbWinger.isSelected());
					mChart.setShow(avgWI, false);
				}
				else{
					mChart.setShow(avgWI, jcbWinger.isSelected());
					mChart.setShow(sumWI, false);
				}
				gup.statistikAlleFluegel = jcbWinger.isSelected();
			}
			// Scoring ============================================================================
			else if (e.getSource() == jcbScoring.getCheckbox()) {
				if (bSum) {
					mChart.setShow(sumSC, jcbScoring.isSelected());
					mChart.setShow(avgSC, false);
				}
				else{
					mChart.setShow(avgSC, jcbScoring.isSelected());
					mChart.setShow(sumSC, false);
				}
				gup.statistikAlleTorschuss = jcbScoring.isSelected();
			}
			// SetPieces ============================================================================
			else if (e.getSource() == jcbSetPieces.getCheckbox()) {
				if (bSum) {
					mChart.setShow(sumSP, jcbSetPieces.isSelected());
					mChart.setShow(avgSP, false);
				}
				else{
					mChart.setShow(avgSP, jcbSetPieces.isSelected());
					mChart.setShow(sumSP, false);
				}
				gup.statistikAlleStandards= jcbSetPieces.isSelected();
			}
		};

		jbApply.addActionListener(actionListener);
		jcbHelpLines.addActionListener(actionListener);
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
		JLabel labelWeeks, labelSquad, labelAggType;
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

		labelWeeks = new JLabel(hov.getLanguageString("Wochen"));
		constraints2.anchor = GridBagConstraints.WEST;
		constraints2.gridx = 0;
		constraints2.gridy = 1;
		constraints2.gridwidth = 1;
		layout2.setConstraints(labelWeeks, constraints2);
		panel2.add(labelWeeks);

		jtfNumberOfHRF = new JTextField(String.valueOf(gup.statistikAnzahlHRF));
		jtfNumberOfHRF.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints2.gridx = 1;
		constraints2.gridy = 1;
		layout2.setConstraints(jtfNumberOfHRF, constraints2);
		panel2.add(jtfNumberOfHRF);

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
		jcbTeam = new JComboBox(GroupTeamFactory.TEAMSMILIES);
		jcbTeam.setRenderer(new core.gui.comp.renderer.SmilieListCellRenderer());
		jcbTeam.setBackground(ThemeManager.getColor(TABLEENTRY_BG));
		jcbTeam.setMaximumRowCount(25);
		jcbTeam.setMaximumSize(new Dimension(200, 25));
		layout2.setConstraints(jcbTeam, constraints2);
		panel2.add(jcbTeam);

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
		jcbAggType.setToolTipText(hov.getLanguageString("ls.module.statistic.team.choose_sum_or_average"));
		constraints2.gridx = 1;
		constraints2.gridy = 4;
		panel2.add(jcbAggType, constraints2);

		constraints2.insets = new Insets(15,0,0,0);

		jcbHelpLines = new JCheckBox(hov.getLanguageString("Hilflinien"),
				gup.statistikAlleHilfslinien);
		add(jcbHelpLines, 5, layout2, constraints2);


		// LEADERSIP =============================================================================================
		constraints2.insets = new Insets(25,0,0,0);
		textLabel = bSum ? sumLeadership : avgLeadership;
		jcbLeadership = new ImageCheckbox(textLabel, getColor(Colors.COLOR_PLAYER_LEADERSHIP), gup.statistikAlleFuehrung);
		add(jcbLeadership, 6, layout2, constraints2);

		// EXPERIENCE =============================================================================================
		constraints2.insets = new Insets(0,0,0,0);
		textLabel = bSum ? sumXP : avgXP;
		jcbXP = new ImageCheckbox(textLabel, getColor(Colors.COLOR_PLAYER_XP), gup.statistikAlleErfahrung);
		add(jcbXP, 7, layout2, constraints2);

		// FORM ============================================================================================
		textLabel = bSum ? sumForm : avgForm;
		jcbForm = new ImageCheckbox(textLabel, getColor(Colors.COLOR_PLAYER_FORM), gup.statistikAlleForm);
		add(jcbForm, 8, layout2, constraints2);

		// STAMINA ============================================================================================
		textLabel = bSum ? sumStamina : avgStamina;
		jcbStamina = new ImageCheckbox(textLabel, getColor(Colors.COLOR_PLAYER_STAMINA), gup.statistikAlleKondition);
		add(jcbStamina, 9, layout2, constraints2);

		// LOYALTY ============================================================================================
		textLabel = bSum ? sumLoyalty : avgLoyalty;
		jcbLoyalty = new ImageCheckbox(textLabel, getColor(Colors.COLOR_PLAYER_LOYALTY), gup.statistikAllLoyalty);
		add(jcbLoyalty, 10, layout2, constraints2);

		// KEEPER ============================================================================================
		textLabel = bSum ? sumGK : avgGK;
		jcbKeeper = new ImageCheckbox(textLabel, getColor(Colors.COLOR_PLAYER_GK), gup.statistikAlleTorwart);
		add(jcbKeeper, 11, layout2, constraints2);

		// DEFENDING ============================================================================================
		constraints2.insets = new Insets(0,0,0,0);
		textLabel = bSum ? sumDE : avgDE;
		jcbDefending = new ImageCheckbox(textLabel, getColor(Colors.COLOR_PLAYER_DE), gup.statistikAlleVerteidigung);
		add(jcbDefending, 12, layout2, constraints2);

		// PLAYMAKING ============================================================================================
		textLabel = bSum ? sumPM : avgPM;
		jcbPlaymaking = new ImageCheckbox(textLabel, getColor(Colors.COLOR_PLAYER_PM), gup.statistikAlleSpielaufbau);
		add(jcbPlaymaking, 13, layout2, constraints2);

		// PASSING ============================================================================================
		textLabel = bSum ? sumPS : avgPS;
		jcbPassing = new ImageCheckbox(textLabel, getColor(Colors.COLOR_PLAYER_PS), gup.statistikAllePasspiel);
		add(jcbPassing, 14, layout2, constraints2);

		// WINGER ============================================================================================
		textLabel = bSum ? sumWI : avgWI;
		jcbWinger = new ImageCheckbox(textLabel, getColor(Colors.COLOR_PLAYER_WI), gup.statistikAlleFluegel);
		add(jcbWinger, 15, layout2, constraints2);

		// SCORING ============================================================================================
		textLabel = bSum ? sumSC : avgSC;
		jcbScoring = new ImageCheckbox(textLabel, getColor(Colors.COLOR_PLAYER_SC), gup.statistikAlleTorschuss);
		add(jcbScoring, 16, layout2, constraints2);

		// SETPIECES ============================================================================================
		textLabel = bSum ? sumSP : avgSP;
		jcbSetPieces = new ImageCheckbox(textLabel, getColor(Colors.COLOR_PLAYER_SP), gup.statistikAlleStandards);
		add(jcbSetPieces, 17, layout2, constraints2);


		// TSI =============================================================================================
		textLabel = bSum ? sumTSI : avgTSI;
		textLabel += " (" + hov.getLanguageString("ls.chart.second_axis") + ")";
		constraints2.insets = new Insets(20,0,0,0);
		jcbTSI = new ImageCheckbox(textLabel, getColor(Colors.COLOR_PLAYER_TSI), gup.statistikAllTSI);
		add(jcbTSI, 18, layout2, constraints2);

		// WAGE ============================================================================================
		constraints2.insets = new Insets(0,0,0,0);
		textLabel = bSum ? sumWage : avgWage;
		textLabel += " (" + hov.getLanguageString("ls.chart.second_axis") + ")";
		jcbWage = new ImageCheckbox(textLabel, getColor(Colors.COLOR_PLAYER_WAGE), gup.statistikAllWages);
		add(jcbWage, 19, layout2, constraints2);

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

		mChart = new HOLinesChart(true, null, null, null, "#,##0");
		panel.add(mChart.getPanel());

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
			int anzahlHRF = Integer.parseInt(jtfNumberOfHRF.getText());
			if (anzahlHRF <= 0) {
				anzahlHRF = 1;
			}
			UserParameter.instance().statistikAnzahlHRF = anzahlHRF;
			NumberFormat format = Helper.DEFAULTDEZIMALFORMAT;
			NumberFormat fmt2 = Helper.getNumberFormat(true, 0);
			NumberFormat fmt3 = Helper.getNumberFormat(false, 0);


			double[][] statistikWerte = DBManager.instance().getDataForTeamStatisticsPanel(anzahlHRF,
							jcbTeam.getSelectedItem().toString());
			LinesChartDataModel[] models = new LinesChartDataModel[statistikWerte.length];

			// There are 28 values - the first 14 are the sum and the next 14 are the averaged values
			if (statistikWerte.length > 0) {

				// LEADERSHIP ========================================================================
				models[0] = new LinesChartDataModel(statistikWerte[0], sumLeadership, jcbLeadership.isSelected() && bSum,
						  getColor(Colors.COLOR_PLAYER_LEADERSHIP), fmt3, 5 / Helper.getMaxValue(statistikWerte[0]));

				models[14] = new LinesChartDataModel(statistikWerte[14], avgLeadership, jcbLeadership.isSelected(),
						       getColor(Colors.COLOR_PLAYER_LEADERSHIP), format);


				// XP ========================================================================
				models[1] = new LinesChartDataModel(statistikWerte[1], sumXP, jcbXP.isSelected() && bSum, getColor(Colors.COLOR_PLAYER_XP),
						fmt3, 7 / Helper.getMaxValue(statistikWerte[1]));

				models[15] = new LinesChartDataModel(statistikWerte[15], avgXP, jcbXP.isSelected() && !bSum,
						        getColor(Colors.COLOR_PLAYER_XP), format);

				// TSI ========================================================================
				models[12] = new LinesChartDataModel(statistikWerte[12], sumTSI, jcbTSI.isSelected() && bSum,
						getColor(Colors.COLOR_PLAYER_TSI), SeriesLines.DASH_DASH, SeriesMarkers.DIAMOND, fmt3,19 / Helper.getMaxValue(statistikWerte[12]), true);

				models[26] = new LinesChartDataModel(statistikWerte[26], avgTSI, jcbTSI.isSelected() && !bSum,
						        getColor(Colors.COLOR_PLAYER_TSI), SeriesLines.DASH_DASH, SeriesMarkers.DIAMOND, format, 19 / Helper.getMaxValue(statistikWerte[26]), true);

				// WAGE ========================================================================
				models[13] = new LinesChartDataModel(statistikWerte[13], sumWage,jcbWage.isSelected() && bSum,
						getColor(Colors.COLOR_PLAYER_WAGE), SeriesLines.DASH_DASH, SeriesMarkers.DIAMOND, fmt3, 15 / Helper.getMaxValue(statistikWerte[13]), true);

				models[27] = new LinesChartDataModel(statistikWerte[27], avgWage, jcbWage.isSelected() && !bSum,
						     getColor(Colors.COLOR_PLAYER_WAGE), SeriesLines.DASH_DASH, SeriesMarkers.DIAMOND, fmt2, 15 / Helper.getMaxValue(statistikWerte[27]), true);


				// FORM  =============================================================================================
				models[2] = new LinesChartDataModel(statistikWerte[2], sumForm, jcbForm.isSelected() && bSum,
						getColor(Colors.COLOR_PLAYER_FORM), format);

				models[16] = new LinesChartDataModel(statistikWerte[16], avgForm, jcbForm.isSelected() && !bSum,
						          getColor(Colors.COLOR_PLAYER_FORM), format);

				// STAMINA =============================================================================================
				models[3] = new LinesChartDataModel(statistikWerte[3], sumStamina, jcbStamina.isSelected() && bSum,
						getColor(Colors.COLOR_PLAYER_STAMINA), format);

				models[17] = new LinesChartDataModel(statistikWerte[17], avgStamina, jcbStamina.isSelected() && !bSum,
										getColor(Colors.COLOR_PLAYER_STAMINA), format);

				// LOYALTY =============================================================================================
				models[11] = new LinesChartDataModel(statistikWerte[11], sumLoyalty, jcbLoyalty.isSelected() && bSum,
						getColor(Colors.COLOR_PLAYER_LOYALTY), format);

				models[25] = new LinesChartDataModel(statistikWerte[25], avgLoyalty, jcbLoyalty.isSelected() && !bSum,
						getColor(Colors.COLOR_PLAYER_LOYALTY), format);

				// KEEPER ========================================================================
				double maxSKill = Helper.getMaxValue(statistikWerte[4]);
				maxSKill = Math.max(maxSKill, Helper.getMaxValue(statistikWerte[5]));
				maxSKill = Math.max(maxSKill, Helper.getMaxValue(statistikWerte[6]));
				maxSKill = Math.max(maxSKill, Helper.getMaxValue(statistikWerte[7]));
				maxSKill = Math.max(maxSKill, Helper.getMaxValue(statistikWerte[8]));
				maxSKill = Math.max(maxSKill, Helper.getMaxValue(statistikWerte[9]));
				maxSKill = Math.max(maxSKill, Helper.getMaxValue(statistikWerte[10]));
				double factor = 19.0/maxSKill;

				models[4] = new LinesChartDataModel(statistikWerte[4], sumGK, jcbKeeper.isSelected() && bSum,
						getColor(Colors.COLOR_PLAYER_GK), fmt3, factor);

				models[18] = new LinesChartDataModel(statistikWerte[18], avgGK, jcbKeeper.isSelected() && !bSum,
						getColor(Colors.COLOR_PLAYER_GK), format);

				// DEFENDING ========================================================================
				models[5] = new LinesChartDataModel(statistikWerte[5], sumDE, jcbDefending.isSelected() && bSum,
						getColor(Colors.COLOR_PLAYER_DE), fmt3, factor);
				models[19] = new LinesChartDataModel(statistikWerte[19], avgDE, jcbDefending.isSelected() && !bSum,
						getColor(Colors.COLOR_PLAYER_DE), format);

				// PLAYMAKING ========================================================================
				models[6] = new LinesChartDataModel(statistikWerte[6], sumPM, jcbPlaymaking.isSelected() && bSum,
						getColor(Colors.COLOR_PLAYER_PM), fmt3, factor);
				models[20] = new LinesChartDataModel(statistikWerte[20], avgPM, jcbPlaymaking.isSelected() && !bSum,
						getColor(Colors.COLOR_PLAYER_PM), format);

				// PASSING ========================================================================
				models[7] = new LinesChartDataModel(statistikWerte[7], sumPS, jcbPassing.isSelected() && bSum,
						getColor(Colors.COLOR_PLAYER_PS), fmt3, factor);
				models[21] = new LinesChartDataModel(statistikWerte[21], avgPS, jcbPassing.isSelected() && !bSum,
						getColor(Colors.COLOR_PLAYER_PS), format);

				// WINGER ========================================================================
				models[8] = new LinesChartDataModel(statistikWerte[8], sumWI, jcbWinger.isSelected() && bSum,
						getColor(Colors.COLOR_PLAYER_WI), fmt3, factor);
				models[22] = new LinesChartDataModel(statistikWerte[22], avgWI, jcbWinger.isSelected() && !bSum,
						getColor(Colors.COLOR_PLAYER_WI), format);

				// SCORING ========================================================================
				models[9] = new LinesChartDataModel(statistikWerte[9], sumSC, jcbScoring.isSelected() && bSum,
						getColor(Colors.COLOR_PLAYER_SC), fmt3, factor);
				models[23] = new LinesChartDataModel(statistikWerte[23], avgSC, jcbScoring.isSelected() && !bSum,
						getColor(Colors.COLOR_PLAYER_SC), format);

				// SETPIECES ========================================================================
				models[10] = new LinesChartDataModel(statistikWerte[10], sumSP, jcbSetPieces.isSelected() && bSum,
						getColor(Colors.COLOR_PLAYER_SP), fmt3, factor);
				models[24] = new LinesChartDataModel(statistikWerte[24], avgSP, jcbSetPieces.isSelected() && !bSum,
						getColor(Colors.COLOR_PLAYER_SP), format);

			}

			mChart.setAllValues(models, statistikWerte[28], format, HOVerwaltung.instance()
					.getLanguageString("Wochen"), "", false, jcbHelpLines.isSelected());
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}


	private Color getColor(int i) {
		return ThemeManager.getColor(HOColorName.PALETTE15[i]);
	}

}
