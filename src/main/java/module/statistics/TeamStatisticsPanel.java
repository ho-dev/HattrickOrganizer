package module.statistics;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.ImageCheckbox;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.GroupTeamFactory;
import core.gui.theme.ThemeManager;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.util.HOLogger;
import core.util.Helper;
import core.util.chart.HOLinesChart;
import core.util.chart.LinesChartDataModel;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.io.Serial;
import java.text.NumberFormat;

import static core.gui.theme.HOColorName.PANEL_BORDER;
import static core.gui.theme.HOColorName.TABLEENTRY_BG;

/**
 * The Team statistics panel
 */
public class TeamStatisticsPanel extends LazyImagePanel {
	@Serial
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
	private JCheckBox jcbInscribe;
	private JComboBox<String> jcbAggType;
	private JComboBox<String> jcbTeam;
	private JTextField jtfNumberOfHRF;
	private HOLinesChart mChart;
	private JPanel panel2;
	private boolean bSum = true;

	private final String sSum = "\u03A3 ";
	private final String sAvg = "\u00D8 ";

	private final String sumLeadership = sSum + TranslationFacility.tr("ls.player.leadership");
	private final String avgLeadership = sAvg + TranslationFacility.tr("ls.player.leadership");
	private final String sumXP = sSum + TranslationFacility.tr("ls.player.experience");
	private final String avgXP = sAvg + TranslationFacility.tr("ls.player.experience");
	private final String sumTSI = sSum + TranslationFacility.tr("ls.player.tsi");
	private final String avgTSI = sAvg + TranslationFacility.tr("ls.player.tsi");
	private final String sumWage = sSum + TranslationFacility.tr("ls.player.wage");
	private final String avgWage = sAvg + TranslationFacility.tr("ls.player.wage");
	private final String avgForm = sAvg + TranslationFacility.tr("ls.player.form");
	private final String sumForm = sSum + TranslationFacility.tr("ls.player.form");
	private final String avgStamina = sAvg + TranslationFacility.tr("ls.player.skill.stamina");
	private final String sumStamina = sSum + TranslationFacility.tr("ls.player.skill.stamina");
	private final String avgLoyalty = sAvg + TranslationFacility.tr("ls.player.loyalty");
	private final String sumLoyalty = sSum + TranslationFacility.tr("ls.player.loyalty");
	private final String sumGK = sSum + TranslationFacility.tr("ls.player.skill.keeper");
	private final String avgGK = sAvg + TranslationFacility.tr("ls.player.skill.keeper");
	private final String sumDE = sSum + TranslationFacility.tr("ls.player.skill.defending");
	private final String avgDE = sAvg + TranslationFacility.tr("ls.player.skill.defending");
	private final String sumPM = sSum + TranslationFacility.tr("ls.player.skill.playmaking");
	private final String avgPM = sAvg + TranslationFacility.tr("ls.player.skill.playmaking");
	private final String sumPS = sSum + TranslationFacility.tr("ls.player.skill.passing");
	private final String avgPS = sAvg + TranslationFacility.tr("ls.player.skill.passing");
	private final String sumWI = sSum + TranslationFacility.tr("ls.player.skill.winger");
	private final String avgWI = sAvg + TranslationFacility.tr("ls.player.skill.winger");
	private final String sumSC = sSum + TranslationFacility.tr("ls.player.skill.scoring");
	private final String avgSC = sAvg + TranslationFacility.tr("ls.player.skill.scoring");
	private final String sumSP = sSum + TranslationFacility.tr("ls.player.skill.setpieces");
	private final String avgSP = sAvg + TranslationFacility.tr("ls.player.skill.setpieces");

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
			if (e.getSource() == jbApply) {
				initStatistik();
			} else if (e.getSource() == jcbHelpLines) {
				mChart.setHelpLines(jcbHelpLines.isSelected());
				gup.statistikAlleHilfslinien = jcbHelpLines.isSelected();
			}
			else if ( e.getSource() == jcbInscribe){
				mChart.setLabelling(jcbInscribe.isSelected());
				gup.statistikAlleBeschriftung = jcbInscribe.isSelected();
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
		jcbInscribe.addActionListener(actionListener);
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

		int gridy = 0;
		labelWeeks = new JLabel(TranslationFacility.tr("Wochen"));
		constraints2.anchor = GridBagConstraints.WEST;
		constraints2.gridx = 0;
		constraints2.gridy = ++gridy;
		constraints2.gridwidth = 1;
		constraints2.insets = new Insets(10,0,0,0);  //top padding
		layout2.setConstraints(labelWeeks, constraints2);
		panel2.add(labelWeeks);

		jtfNumberOfHRF = new JTextField(String.valueOf(gup.statistikAnzahlHRF), 3);
		jtfNumberOfHRF.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints2.gridx = 1;
		constraints2.insets = new Insets(10,5,0,0);  //top padding
		layout2.setConstraints(jtfNumberOfHRF, constraints2);
		panel2.add(jtfNumberOfHRF);

		constraints2.gridx = 2;
		jbApply = new JButton(TranslationFacility.tr("ls.button.apply"));
		constraints2.insets = new Insets(10,20,0,0);  //top padding
		layout2.setConstraints(jbApply, constraints2);
		jbApply.setToolTipText(TranslationFacility.tr("tt_Statistik_HRFAnzahluebernehmen"));
		panel2.add(jbApply);


		constraints2.insets = new Insets(20,0,0,0);
		constraints2.gridwidth = 3;
		jcbHelpLines = new JCheckBox(TranslationFacility.tr("Hilflinien"), gup.statistikAlleHilfslinien);
		add(jcbHelpLines, ++gridy, layout2, constraints2);

		constraints2.insets = new Insets(20,0,0,0);
		constraints2.gridwidth = 3;
		jcbInscribe = new JCheckBox(TranslationFacility.tr("Beschriftung"), gup.statistikAlleBeschriftung);
		add(jcbInscribe, ++gridy, layout2, constraints2);


		labelSquad = new JLabel(TranslationFacility.tr("Gruppe"));
		constraints2.gridx = 0;
		constraints2.gridy = ++gridy;
		constraints2.gridwidth = 1;
		constraints2.insets = new Insets(20,0,0,0);  //top padding
		layout2.setConstraints(labelSquad, constraints2);
		panel2.add(labelSquad);

		constraints2.gridx = 1;
		constraints2.gridwidth = 2;
		constraints2.insets = new Insets(20,5,0,0);  //top padding
		jcbTeam = new JComboBox(GroupTeamFactory.TEAMS_GROUPS);
		jcbTeam.setRenderer(new core.gui.comp.renderer.SmilieListCellRenderer());
		jcbTeam.setBackground(ThemeManager.getColor(TABLEENTRY_BG));
		layout2.setConstraints(jcbTeam, constraints2);
		panel2.add(jcbTeam);

		labelAggType = new JLabel(TranslationFacility.tr("ls.agg"));
		constraints2.gridx = 0;
		constraints2.gridy = ++gridy;
		constraints2.gridwidth = 1;
		constraints2.insets = new Insets(0,0,0,0);  //top padding
		panel2.add(labelAggType, constraints2);

		String[] sAggType = { TranslationFacility.tr("Gesamt"), TranslationFacility.tr("Durchschnitt")};
		jcbAggType = new JComboBox<>(sAggType);
		jcbAggType.setSelectedIndex(gup.statisticsTeamSumOrAverage);
		bSum = (gup.statisticsTeamSumOrAverage == 0);
		constraints2.insets = new Insets(0,5,0,0);  //top padding
		layout2.setConstraints(jcbAggType, constraints2);

		jcbAggType.setToolTipText(TranslationFacility.tr("ls.module.statistic.team.choose_sum_or_average"));
		constraints2.gridx = 1;
		constraints2.gridwidth = 2;
		panel2.add(jcbAggType, constraints2);


		// LEADERSIP =============================================================================================
		constraints2.insets = new Insets(20,0,0,0);
		textLabel = bSum ? sumLeadership : avgLeadership;
		jcbLeadership = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_LEADERSHIP), gup.statistikAlleFuehrung);
		add(jcbLeadership, ++gridy, layout2, constraints2);

		// EXPERIENCE =============================================================================================
		constraints2.insets = new Insets(0,0,0,0);
		textLabel = bSum ? sumXP : avgXP;
		jcbXP = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_XP), gup.statistikAlleErfahrung);
		add(jcbXP, ++gridy, layout2, constraints2);

		// FORM ============================================================================================
		textLabel = bSum ? sumForm : avgForm;
		jcbForm = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_FORM), gup.statistikAlleForm);
		add(jcbForm, ++gridy, layout2, constraints2);

		// STAMINA ============================================================================================
		textLabel = bSum ? sumStamina : avgStamina;
		jcbStamina = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_STAMINA), gup.statistikAlleKondition);
		add(jcbStamina, ++gridy, layout2, constraints2);

		// LOYALTY ============================================================================================
		textLabel = bSum ? sumLoyalty : avgLoyalty;
		jcbLoyalty = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_LOYALTY), gup.statistikAllLoyalty);
		add(jcbLoyalty, ++gridy, layout2, constraints2);

		// KEEPER ============================================================================================
		textLabel = bSum ? sumGK : avgGK;
		jcbKeeper = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_GK), gup.statistikAlleTorwart);
		add(jcbKeeper, ++gridy, layout2, constraints2);

		// DEFENDING ============================================================================================
		constraints2.insets = new Insets(0,0,0,0);
		textLabel = bSum ? sumDE : avgDE;
		jcbDefending = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_DE), gup.statistikAlleVerteidigung);
		add(jcbDefending, ++gridy, layout2, constraints2);

		// PLAYMAKING ============================================================================================
		textLabel = bSum ? sumPM : avgPM;
		jcbPlaymaking = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_PM), gup.statistikAlleSpielaufbau);
		add(jcbPlaymaking, ++gridy, layout2, constraints2);

		// PASSING ============================================================================================
		textLabel = bSum ? sumPS : avgPS;
		jcbPassing = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_PS), gup.statistikAllePasspiel);
		add(jcbPassing, ++gridy, layout2, constraints2);

		// WINGER ============================================================================================
		textLabel = bSum ? sumWI : avgWI;
		jcbWinger = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_WI), gup.statistikAlleFluegel);
		add(jcbWinger, ++gridy, layout2, constraints2);

		// SCORING ============================================================================================
		textLabel = bSum ? sumSC : avgSC;
		jcbScoring = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_SC), gup.statistikAlleTorschuss);
		add(jcbScoring, ++gridy, layout2, constraints2);

		// SETPIECES ============================================================================================
		textLabel = bSum ? sumSP : avgSP;
		jcbSetPieces = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_SP), gup.statistikAlleStandards);
		add(jcbSetPieces, ++gridy, layout2, constraints2);


		// TSI =============================================================================================
		textLabel = bSum ? sumTSI : avgTSI;
		textLabel += " (" + TranslationFacility.tr("ls.chart.second_axis") + ")";
		constraints2.insets = new Insets(20,0,0,0);
		jcbTSI = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_TSI), gup.statistikAllTSI);
		add(jcbTSI, ++gridy, layout2, constraints2);

		// WAGE ============================================================================================
		constraints2.insets = new Insets(0,0,0,0);
		textLabel = bSum ? sumWage : avgWage;
		textLabel += " (" + TranslationFacility.tr("ls.chart.second_axis") + ")";
		jcbWage = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_WAGE), gup.statistikAllWages);
		add(jcbWage, ++gridy, layout2, constraints2);

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
		constraints.gridwidth = 3;
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

			double[][] statistikWerte = DBManager.instance().getDataForTeamStatisticsPanel(anzahlHRF,
							jcbTeam.getSelectedItem().toString());
			LinesChartDataModel[] models = new LinesChartDataModel[statistikWerte.length];

			// There are 28 values - the first 14 are the sum and the next 14 are the averaged values
			if (statistikWerte.length > 0) {

				// LEADERSHIP ========================================================================
				models[0] = new LinesChartDataModel(statistikWerte[0], sumLeadership, jcbLeadership.isSelected() && bSum,
						  Colors.getColor(Colors.COLOR_PLAYER_LEADERSHIP), 5 / Helper.getMaxValue(statistikWerte[0]));

				models[14] = new LinesChartDataModel(statistikWerte[14], avgLeadership, jcbLeadership.isSelected(),
						       Colors.getColor(Colors.COLOR_PLAYER_LEADERSHIP));


				// XP ========================================================================
				models[1] = new LinesChartDataModel(statistikWerte[1], sumXP, jcbXP.isSelected() && bSum, Colors.getColor(Colors.COLOR_PLAYER_XP), 7 / Helper.getMaxValue(statistikWerte[1]));

				models[15] = new LinesChartDataModel(statistikWerte[15], avgXP, jcbXP.isSelected() && !bSum,
						        Colors.getColor(Colors.COLOR_PLAYER_XP));

				// TSI ========================================================================
				models[12] = new LinesChartDataModel(statistikWerte[12], sumTSI, jcbTSI.isSelected() && bSum,
						Colors.getColor(Colors.COLOR_PLAYER_TSI), SeriesLines.DASH_DASH, SeriesMarkers.DIAMOND,19 / Helper.getMaxValue(statistikWerte[12]), true);

				models[26] = new LinesChartDataModel(statistikWerte[26], avgTSI, jcbTSI.isSelected() && !bSum,
						        Colors.getColor(Colors.COLOR_PLAYER_TSI), SeriesLines.DASH_DASH, SeriesMarkers.DIAMOND, 19 / Helper.getMaxValue(statistikWerte[26]), true);

				// WAGE ========================================================================
				models[13] = new LinesChartDataModel(statistikWerte[13], sumWage,jcbWage.isSelected() && bSum,
						Colors.getColor(Colors.COLOR_PLAYER_WAGE), SeriesLines.DASH_DASH, SeriesMarkers.DIAMOND, 15 / Helper.getMaxValue(statistikWerte[13]), true);

				models[27] = new LinesChartDataModel(statistikWerte[27], avgWage, jcbWage.isSelected() && !bSum,
						     Colors.getColor(Colors.COLOR_PLAYER_WAGE), SeriesLines.DASH_DASH, SeriesMarkers.DIAMOND, 15 / Helper.getMaxValue(statistikWerte[27]), true);



				// FORM  =============================================================================================
				models[2] = new LinesChartDataModel(statistikWerte[2], sumForm, jcbForm.isSelected() && bSum,
						Colors.getColor(Colors.COLOR_PLAYER_FORM));

				models[16] = new LinesChartDataModel(statistikWerte[16], avgForm, jcbForm.isSelected() && !bSum,
						          Colors.getColor(Colors.COLOR_PLAYER_FORM));

				// STAMINA =============================================================================================
				models[3] = new LinesChartDataModel(statistikWerte[3], sumStamina, jcbStamina.isSelected() && bSum,
						Colors.getColor(Colors.COLOR_PLAYER_STAMINA));

				models[17] = new LinesChartDataModel(statistikWerte[17], avgStamina, jcbStamina.isSelected() && !bSum,
										Colors.getColor(Colors.COLOR_PLAYER_STAMINA));

				// LOYALTY =============================================================================================
				models[11] = new LinesChartDataModel(statistikWerte[11], sumLoyalty, jcbLoyalty.isSelected() && bSum,
						Colors.getColor(Colors.COLOR_PLAYER_LOYALTY));

				models[25] = new LinesChartDataModel(statistikWerte[25], avgLoyalty, jcbLoyalty.isSelected() && !bSum,
						Colors.getColor(Colors.COLOR_PLAYER_LOYALTY));


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
						Colors.getColor(Colors.COLOR_PLAYER_GK), factor);

				models[18] = new LinesChartDataModel(statistikWerte[18], avgGK, jcbKeeper.isSelected() && !bSum,
						Colors.getColor(Colors.COLOR_PLAYER_GK));

				// DEFENDING ========================================================================
				models[5] = new LinesChartDataModel(statistikWerte[5], sumDE, jcbDefending.isSelected() && bSum,
						Colors.getColor(Colors.COLOR_PLAYER_DE), factor);
				models[19] = new LinesChartDataModel(statistikWerte[19], avgDE, jcbDefending.isSelected() && !bSum,
						Colors.getColor(Colors.COLOR_PLAYER_DE));

				// PLAYMAKING ========================================================================
				models[6] = new LinesChartDataModel(statistikWerte[6], sumPM, jcbPlaymaking.isSelected() && bSum,
						Colors.getColor(Colors.COLOR_PLAYER_PM), factor);
				models[20] = new LinesChartDataModel(statistikWerte[20], avgPM, jcbPlaymaking.isSelected() && !bSum,
						Colors.getColor(Colors.COLOR_PLAYER_PM));

				// PASSING ========================================================================
				models[7] = new LinesChartDataModel(statistikWerte[7], sumPS, jcbPassing.isSelected() && bSum,
						Colors.getColor(Colors.COLOR_PLAYER_PS), factor);
				models[21] = new LinesChartDataModel(statistikWerte[21], avgPS, jcbPassing.isSelected() && !bSum,
						Colors.getColor(Colors.COLOR_PLAYER_PS));

				// WINGER ========================================================================
				models[8] = new LinesChartDataModel(statistikWerte[8], sumWI, jcbWinger.isSelected() && bSum,
						Colors.getColor(Colors.COLOR_PLAYER_WI), factor);
				models[22] = new LinesChartDataModel(statistikWerte[22], avgWI, jcbWinger.isSelected() && !bSum,
						Colors.getColor(Colors.COLOR_PLAYER_WI));

				// SCORING ========================================================================
				models[9] = new LinesChartDataModel(statistikWerte[9], sumSC, jcbScoring.isSelected() && bSum,
						Colors.getColor(Colors.COLOR_PLAYER_SC), factor);
				models[23] = new LinesChartDataModel(statistikWerte[23], avgSC, jcbScoring.isSelected() && !bSum,
						Colors.getColor(Colors.COLOR_PLAYER_SC));

				// SETPIECES ========================================================================
				models[10] = new LinesChartDataModel(statistikWerte[10], sumSP, jcbSetPieces.isSelected() && bSum,
						Colors.getColor(Colors.COLOR_PLAYER_SP), factor);
				models[24] = new LinesChartDataModel(statistikWerte[24], avgSP, jcbSetPieces.isSelected() && !bSum,
						Colors.getColor(Colors.COLOR_PLAYER_SP));
			}

			mChart.setAllValues(models, statistikWerte[28], format,
					TranslationFacility.tr("Wochen"), "", jcbInscribe.isSelected(), jcbHelpLines.isSelected());
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}

	

}
