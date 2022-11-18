package module.statistics;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.ImageCheckbox;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.model.PlayerCBItem;
import core.gui.model.PlayerCBItemRenderer;
import core.util.chart.LinesChartDataModel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.Player;
import core.util.HOLogger;
import core.util.Helper;
import core.util.chart.HOLinesChart;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.text.NumberFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Panel Player in Module Statistics
 */
class PlayerStatisticsPanel extends LazyImagePanel {

	private ImageCheckbox jcbRating;
	private ImageCheckbox jcbLeadership;
	private ImageCheckbox jcbExperience;
	private ImageCheckbox jcbTSI;
	private ImageCheckbox jcbSalary;
	private ImageCheckbox jcbForm;
	private ImageCheckbox jcbStamina;
	private ImageCheckbox jcbLoyalty;
	private ImageCheckbox jcbKeeper;
	private ImageCheckbox jcbDefending;
	private ImageCheckbox jcbPlaymaking;
	private ImageCheckbox jcbPass;
	private ImageCheckbox jcbWing;
	private ImageCheckbox jcbScoring;
	private ImageCheckbox jcbSetPieces;
	private JButton jbApply;
	private JCheckBox jcbHelpLines;
	private JCheckBox jcbInscribe;

	private JComboBox<PlayerCBItem> jcbPlayer;
	private JTextField jtfNbWeeks;
	private HOLinesChart oChartPanel;


	public final void setPlayer(int playerID) {
		Helper.setComboBoxFromID(jcbPlayer, playerID);
	}

	@Override
	protected void update() {
		initSpielerCB();
		initStatistik();
	}

	@Override
	protected void initialize() {
		initComponents();
		initSpielerCB();
		addListeners();
		initStatistik();
		setNeedsRefresh(false);
		registerRefreshable(true);
	}

	private void addListeners() {
		this.jtfNbWeeks.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent focusEvent) {
				Helper.parseInt(HOMainFrame.instance(), jtfNbWeeks, false);
			}
		});

		jbApply.addActionListener(e -> changeTimeRange());

		jcbPlayer.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				initStatistik();
			}
		});

		ActionListener cbActionListener = e -> {
			if (e.getSource() == jcbHelpLines) {
				oChartPanel.setHelpLines(jcbHelpLines.isSelected());
				UserParameter.instance().statistikHilfslinien = jcbHelpLines.isSelected();
			} else if (e.getSource() == jcbInscribe) {
				oChartPanel.setLabelling(jcbInscribe.isSelected());
				UserParameter.instance().statistikAlleBeschriftung = jcbInscribe.isSelected();
			} else if (e.getSource() == jcbLeadership.getCheckbox()) {
				oChartPanel.setShow("ls.player.leadership", jcbLeadership.isSelected());
				UserParameter.instance().statistikFuehrung = jcbLeadership.isSelected();
			} else if (e.getSource() == jcbExperience.getCheckbox()) {
				oChartPanel.setShow("ls.player.experience", jcbExperience.isSelected());
				UserParameter.instance().statistikErfahrung = jcbExperience.isSelected();
			} else if (e.getSource() == jcbLoyalty.getCheckbox()) {
				oChartPanel.setShow("ls.player.loyalty", jcbLoyalty.isSelected());
				UserParameter.instance().statistikLoyalty = jcbLoyalty.isSelected();
			} else if (e.getSource() == jcbForm.getCheckbox()) {
				oChartPanel.setShow("ls.player.form", jcbForm.isSelected());
				UserParameter.instance().statistikForm = jcbForm.isSelected();
			} else if (e.getSource() == jcbStamina.getCheckbox()) {
				oChartPanel.setShow("ls.player.skill.stamina",
						jcbStamina.isSelected());
				UserParameter.instance().statistikKondition = jcbStamina.isSelected();
			} else if (e.getSource() == jcbKeeper.getCheckbox()) {
				oChartPanel.setShow("ls.player.skill.keeper", jcbKeeper.isSelected());
				UserParameter.instance().statistikTorwart = jcbKeeper.isSelected();
			} else if (e.getSource() == jcbDefending.getCheckbox()) {
				oChartPanel.setShow("ls.player.skill.defending",
						jcbDefending.isSelected());
				UserParameter.instance().statistikVerteidigung = jcbDefending.isSelected();
			} else if (e.getSource() == jcbPlaymaking.getCheckbox()) {
				oChartPanel.setShow("ls.player.skill.playmaking",
						jcbPlaymaking.isSelected());
				UserParameter.instance().statistikSpielaufbau = jcbPlaymaking.isSelected();
			} else if (e.getSource() == jcbPass.getCheckbox()) {
				oChartPanel.setShow("ls.player.skill.passing",
						jcbPass.isSelected());
				UserParameter.instance().statistikPasspiel = jcbPass.isSelected();
			} else if (e.getSource() == jcbWing.getCheckbox()) {
				oChartPanel.setShow("ls.player.skill.winger", jcbWing.isSelected());
				UserParameter.instance().statistikFluegel = jcbWing.isSelected();
			} else if (e.getSource() == jcbScoring.getCheckbox()) {
				oChartPanel.setShow("ls.player.skill.scoring",
						jcbScoring.isSelected());
				UserParameter.instance().statistikTorschuss = jcbScoring.isSelected();
			} else if (e.getSource() == jcbSetPieces.getCheckbox()) {
				oChartPanel.setShow("ls.player.skill.setpieces",
						jcbSetPieces.isSelected());
				UserParameter.instance().statistikStandards = jcbSetPieces.isSelected();
			} else if (e.getSource() == jcbRating.getCheckbox()) {
				oChartPanel.setShow("RecapPanel.Stars", jcbRating.isSelected());
				UserParameter.instance().statistikBewertung = jcbRating.isSelected();
			} else if (e.getSource() == jcbTSI.getCheckbox()) {
				oChartPanel.setShow("Marktwert", jcbTSI.isSelected());
				UserParameter.instance().statistikSpielerFinanzenMarktwert = jcbTSI
						.isSelected();
			} else if (e.getSource() == jcbSalary.getCheckbox()) {
				oChartPanel.setShow("ls.player.wage", jcbSalary.isSelected());
				UserParameter.instance().statistikSpielerFinanzenGehalt = jcbSalary
						.isSelected();
			}
		};
		jcbHelpLines.addActionListener(cbActionListener);
		jcbInscribe.addActionListener(cbActionListener);
		jcbRating.addActionListener(cbActionListener);
		jcbLeadership.addActionListener(cbActionListener);
		jcbExperience.addActionListener(cbActionListener);
		jcbTSI.addActionListener(cbActionListener);
		jcbSalary.addActionListener(cbActionListener);
		jcbForm.addActionListener(cbActionListener);
		jcbStamina.addActionListener(cbActionListener);
		jcbLoyalty.addActionListener(cbActionListener);
		jcbKeeper.addActionListener(cbActionListener);
		jcbDefending.addActionListener(cbActionListener);
		jcbPlaymaking.addActionListener(cbActionListener);
		jcbPass.addActionListener(cbActionListener);
		jcbWing.addActionListener(cbActionListener);
		jcbScoring.addActionListener(cbActionListener);
		jcbSetPieces.addActionListener(cbActionListener);
	}

	private void initComponents() {
		JLabel label;

		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(2, 0, 2, 0);

		setLayout(layout);

		final JPanel panel2 = new ImagePanel();
		final GridBagLayout layout2 = new GridBagLayout();
		final GridBagConstraints constraints2 = new GridBagConstraints();
		constraints2.fill = GridBagConstraints.HORIZONTAL;
		constraints2.weightx = 0.0;
		constraints2.weighty = 0.0;
		constraints2.insets = new Insets(2, 2, 2, 2);

		panel2.setLayout(layout2);

		constraints2.insets = new Insets(10,0,0,0);  //top padding
		label = new JLabel(getLangStr("Wochen"));
		constraints2.gridwidth = 1;
		constraints2.anchor = GridBagConstraints.WEST;
		constraints2.gridx = 0;
		constraints2.gridy = 0;
		layout2.setConstraints(label, constraints2);
		panel2.add(label);

		constraints2.insets = new Insets(10,5,0,0);  //top padding
		jtfNbWeeks = new JTextField(String.valueOf(UserParameter.instance().statistikAnzahlHRF),3);
		jtfNbWeeks.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints2.gridx = 1;
		constraints2.gridy = 0;
		layout2.setConstraints(jtfNbWeeks, constraints2);
		panel2.add(jtfNbWeeks);

		constraints2.gridx = 2;
		constraints2.gridy = 0;
		constraints2.anchor = GridBagConstraints.EAST;
		constraints2.insets = new Insets(10,20,0,0);  //top padding
		jbApply = new JButton(getLangStr("ls.button.apply"));
		jbApply.setToolTipText(getLangStr("tt_Statistik_HRFAnzahluebernehmen"));
		layout2.setConstraints(jbApply, constraints2);
		panel2.add(jbApply);

		constraints2.gridwidth = 3;
		constraints2.gridx = 0;
		constraints2.gridy = 1;
		constraints2.insets = new Insets(20,0,0,0);  //top padding
		jcbHelpLines = new JCheckBox(getLangStr("Hilflinien"), UserParameter.instance().statistikHilfslinien);
		jcbHelpLines.setOpaque(false);
		jcbHelpLines.setBackground(Color.white);
		layout2.setConstraints(jcbHelpLines, constraints2);
		panel2.add(jcbHelpLines);

		constraints2.gridy++;
		jcbInscribe = new JCheckBox(getLangStr("Beschriftung"), UserParameter.instance().statistikAlleBeschriftung);
		jcbInscribe.setOpaque(false);
		jcbInscribe.setBackground(Color.white);
		layout2.setConstraints(jcbInscribe, constraints2);
		panel2.add(jcbInscribe);

		label = new JLabel(getLangStr("Spieler"));
		label.setToolTipText(getLangStr("tt_Statistik_Spieler"));
		constraints2.gridx = 0;
		constraints2.gridy++;
		constraints2.gridwidth = 1;
		constraints2.insets = new Insets(20,0,0,0);  //top padding
		layout2.setConstraints(label, constraints2);
		panel2.add(label);

		constraints2.gridx = 1;
		constraints2.gridwidth = 2;
		jcbPlayer = new JComboBox();
		constraints2.insets = new Insets(20,5,0,0);  //top padding
		jcbPlayer.setToolTipText(getLangStr("tt_Statistik_Spieler"));
		jcbPlayer.setRenderer(new PlayerCBItemRenderer());
		jcbPlayer.setBackground(ColorLabelEntry.BG_STANDARD);
		layout2.setConstraints(jcbPlayer, constraints2);
		panel2.add(jcbPlayer);

		constraints2.gridx = 0;
		constraints2.gridy++;
		constraints2.gridwidth = 3;
		constraints2.insets = new Insets(20,0,0,0);  //top padding
		jcbForm = new ImageCheckbox(getLangStr("ls.player.form"), Colors.getColor(Colors.COLOR_PLAYER_FORM),
				UserParameter.instance().statistikForm);
		jcbForm.setOpaque(false);
		layout2.setConstraints(jcbForm, constraints2);
		panel2.add(jcbForm);

		constraints2.gridy++;
		constraints2.insets = new Insets(0,0,0,0);
		jcbStamina = new ImageCheckbox(getLangStr("ls.player.skill.stamina"),
				Colors.getColor(Colors.COLOR_PLAYER_STAMINA),
				UserParameter.instance().statistikKondition);
		jcbStamina.setOpaque(false);
		layout2.setConstraints(jcbStamina, constraints2);
		panel2.add(jcbStamina);

		constraints2.gridy++;
		jcbLoyalty = new ImageCheckbox(getLangStr("ls.player.loyalty"),
				Colors.getColor(Colors.COLOR_PLAYER_LOYALTY),
				UserParameter.instance().statistikLoyalty);
		jcbLoyalty.setOpaque(false);
		layout2.setConstraints(jcbLoyalty, constraints2);
		panel2.add(jcbLoyalty);

		constraints2.gridy++;
		jcbKeeper = new ImageCheckbox(getLangStr("ls.player.skill.keeper"),
				Colors.getColor(Colors.COLOR_PLAYER_GK),
				UserParameter.instance().statistikTorwart);
		jcbKeeper.setOpaque(false);
		layout2.setConstraints(jcbKeeper, constraints2);
		panel2.add(jcbKeeper);

		constraints2.gridy++;
		jcbDefending = new ImageCheckbox(getLangStr("ls.player.skill.defending"),
				Colors.getColor(Colors.COLOR_PLAYER_DE),
				UserParameter.instance().statistikVerteidigung);
		jcbDefending.setOpaque(false);
		layout2.setConstraints(jcbDefending, constraints2);
		panel2.add(jcbDefending);

		constraints2.gridy++;
		jcbPlaymaking = new ImageCheckbox(getLangStr("ls.player.skill.playmaking"),
				Colors.getColor(Colors.COLOR_PLAYER_PM),
				UserParameter.instance().statistikSpielaufbau);
		jcbPlaymaking.setOpaque(false);
		layout2.setConstraints(jcbPlaymaking, constraints2);
		panel2.add(jcbPlaymaking);

		constraints2.gridy++;
		jcbPass = new ImageCheckbox(getLangStr("ls.player.skill.passing"),
				Colors.getColor(Colors.COLOR_PLAYER_PS),
				UserParameter.instance().statistikPasspiel);
		jcbPass.setOpaque(false);
		layout2.setConstraints(jcbPass, constraints2);
		panel2.add(jcbPass);

		constraints2.gridy++;
		jcbWing = new ImageCheckbox(getLangStr("ls.player.skill.winger"),
				Colors.getColor(Colors.COLOR_PLAYER_WI),
				UserParameter.instance().statistikFluegel);
		jcbWing.setOpaque(false);
		layout2.setConstraints(jcbWing, constraints2);
		panel2.add(jcbWing);

		constraints2.gridy++;
		jcbScoring = new ImageCheckbox(getLangStr("ls.player.skill.scoring"),
				Colors.getColor(Colors.COLOR_PLAYER_SC),
				UserParameter.instance().statistikTorschuss);
		jcbScoring.setOpaque(false);
		layout2.setConstraints(jcbScoring, constraints2);
		panel2.add(jcbScoring);

		constraints2.gridy++;
		jcbSetPieces = new ImageCheckbox(getLangStr("ls.player.skill.setpieces"),
				Colors.getColor(Colors.COLOR_PLAYER_SP),
				UserParameter.instance().statistikStandards);
		jcbSetPieces.setOpaque(false);
		layout2.setConstraints(jcbSetPieces, constraints2);
		panel2.add(jcbSetPieces);

		constraints2.gridy++;
		constraints2.insets = new Insets(0,0,0,0);
		jcbLeadership = new ImageCheckbox(getLangStr("ls.player.leadership"),
				Colors.getColor(Colors.COLOR_PLAYER_LEADERSHIP),
				UserParameter.instance().statistikFuehrung);
		jcbLeadership.setOpaque(false);
		layout2.setConstraints(jcbLeadership, constraints2);
		panel2.add(jcbLeadership);

		constraints2.gridy++;
		jcbExperience = new ImageCheckbox(getLangStr("ls.player.experience"),
				Colors.getColor(Colors.COLOR_PLAYER_XP),
				UserParameter.instance().statistikErfahrung);
		jcbExperience.setOpaque(false);
		layout2.setConstraints(jcbExperience, constraints2);
		panel2.add(jcbExperience);

		constraints2.gridy++;
		jcbRating = new ImageCheckbox(getLangStr("RecapPanel.Stars"),
				Colors.getColor(Colors.COLOR_PLAYER_RATING),
				UserParameter.instance().statistikBewertung);
		jcbRating.setOpaque(false);
		layout2.setConstraints(jcbRating, constraints2);
		panel2.add(jcbRating);


		constraints2.insets = new Insets(20,0,0,0);  //top padding
		constraints2.gridy++;
		String textLabel = getLangStr("ls.player.tsi") + " (" + getLangStr("ls.chart.second_axis") + ")";
		jcbTSI = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_TSI), UserParameter.instance().statistikSpielerFinanzenMarktwert);
		jcbTSI.setOpaque(false);
		layout2.setConstraints(jcbTSI, constraints2);
		panel2.add(jcbTSI);

		constraints2.gridy++;
		constraints2.insets = new Insets(0,0,0,0);  //top padding
		textLabel = getLangStr("ls.player.wage") + " (" + getLangStr("ls.chart.second_axis") + ")";
		jcbSalary = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_PLAYER_WAGE), UserParameter.instance().statistikSpielerFinanzenGehalt);
		jcbSalary.setOpaque(false);
		layout2.setConstraints(jcbSalary, constraints2);
		panel2.add(jcbSalary);

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

		oChartPanel = new HOLinesChart(true, null, null, null, "#,##0", 0d, 20d);
		panel.add(oChartPanel.getPanel());

		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.weighty = 1.0;
		constraints.weightx = 1.0;
		constraints.anchor = GridBagConstraints.NORTH;
		panel.setBorder(BorderFactory.createLineBorder(ThemeManager.getColor(HOColorName.PANEL_BORDER)));
		layout.setConstraints(panel, constraints);
		add(panel);
	}

	private void changeTimeRange() {
		var selection = jcbPlayer.getSelectedItem();
		initSpielerCB();
		if ( selection != null){
			jcbPlayer.setSelectedItem(selection); // reset selection
			initStatistik();
		}
	}

	private void initSpielerCB() {
		List<Player> players = HOVerwaltung.instance().getModel().getCurrentPlayers();
		List<PlayerCBItem> playerCBItems = new ArrayList<>(players.size());
		for (Player player : players) {
			playerCBItems.add(new PlayerCBItem(player.getFullName(), 0f, player));
		}
		Collections.sort(playerCBItems);

		// date from
		var nWeeks = UserParameter.instance().statistikAnzahlHRF;
		var latestHRFDate = HOVerwaltung.instance().getModel().getBasics().getDatum();
		var fromDate = latestHRFDate.minus(7*nWeeks, ChronoUnit.DAYS);

		// former players
		List<Player> oldPlayers = HOVerwaltung.instance().getModel().getFormerPlayers().stream().filter(i->!i.getHrfDate().isBefore(fromDate)).toList();
		List<PlayerCBItem> spielerOldCBItems = new ArrayList<>(players.size());
		for (Player player : oldPlayers) {
			spielerOldCBItems.add(new PlayerCBItem(player.getFullName(), 0f, player));
		}
		Collections.sort(spielerOldCBItems);

		// Zusammenfügen
		List<PlayerCBItem> cbItems = new ArrayList<>(playerCBItems.size()
				+ spielerOldCBItems.size() + 1);
		cbItems.addAll(playerCBItems);
		// Fur die Leerzeile;
		cbItems.add(null);
		cbItems.addAll(spielerOldCBItems);

		jcbPlayer.setModel(new DefaultComboBoxModel(cbItems.toArray()));
		// Kein Player selektiert
		jcbPlayer.setSelectedItem(null);
	}

	private void initStatistik() {
		try {
			int anzahlHRF = Integer.parseInt(jtfNbWeeks.getText());

			if (anzahlHRF <= 0) {
				anzahlHRF = 1;
			}

			UserParameter.instance().statistikAnzahlHRF = anzahlHRF;

			NumberFormat format = Helper.DEFAULTDEZIMALFORMAT;
			NumberFormat format2 = Helper.getNumberFormat(true, 0);
			NumberFormat format3 = Helper.getNumberFormat(false, 0);

			if (jcbPlayer.getSelectedItem() != null) {
				final double[][] statistikWerte = DBManager.instance().getSpielerDaten4Statistik(
						((PlayerCBItem) jcbPlayer.getSelectedItem()).getPlayer()
								.getPlayerID(), anzahlHRF);
				final LinesChartDataModel[] models = new LinesChartDataModel[statistikWerte.length];

				if (statistikWerte.length > 0) {
					double maxTSI = Helper.getMaxValue(statistikWerte[0]);
					double maxWage = Helper.getMaxValue(statistikWerte[1]);

					//   TSI =========================================================================
					models[0] = new LinesChartDataModel(statistikWerte[0], "Marktwert", jcbTSI.isSelected(), Colors.getColor(Colors.COLOR_PLAYER_TSI),
							SeriesLines.DASH_DASH, SeriesMarkers.DIAMOND, format3, 19/maxTSI, true);

					models[1] = new LinesChartDataModel(statistikWerte[1], "ls.player.wage", jcbSalary.isSelected(), Colors.getColor(Colors.COLOR_PLAYER_WAGE),

							SeriesLines.DASH_DASH, SeriesMarkers.DIAMOND, format2, 16/maxWage, true);

					models[2] = new LinesChartDataModel(statistikWerte[2], "ls.player.leadership",
							jcbLeadership.isSelected(),

							Colors.getColor(Colors.COLOR_PLAYER_LEADERSHIP), format);
					models[3] = new LinesChartDataModel(statistikWerte[3], "ls.player.experience",
							jcbExperience.isSelected(),
							Colors.getColor(Colors.COLOR_PLAYER_XP), format);
					models[4] = new LinesChartDataModel(statistikWerte[4], "ls.player.form",
							jcbForm.isSelected(), Colors.getColor(Colors.COLOR_PLAYER_FORM),
							format);
					models[5] = new LinesChartDataModel(statistikWerte[5], "ls.player.skill.stamina",
							jcbStamina.isSelected(),
							Colors.getColor(Colors.COLOR_PLAYER_STAMINA), format);
					models[6] = new LinesChartDataModel(statistikWerte[6], "ls.player.skill.keeper",
							jcbKeeper.isSelected(),
							Colors.getColor(Colors.COLOR_PLAYER_GK), format);
					models[7] = new LinesChartDataModel(statistikWerte[7], "ls.player.skill.defending",
							jcbDefending.isSelected(),
							Colors.getColor(Colors.COLOR_PLAYER_DE), format);
					models[8] = new LinesChartDataModel(statistikWerte[8], "ls.player.skill.playmaking",
							jcbPlaymaking.isSelected(),
							Colors.getColor(Colors.COLOR_PLAYER_PM), format);
					models[9] = new LinesChartDataModel(statistikWerte[9], "ls.player.skill.passing",
							jcbPass.isSelected(),
							Colors.getColor(Colors.COLOR_PLAYER_PS), format);
					models[10] = new LinesChartDataModel(statistikWerte[10], "ls.player.skill.winger",
							jcbWing.isSelected(),
							Colors.getColor(Colors.COLOR_PLAYER_WI), format);
					models[11] = new LinesChartDataModel(statistikWerte[11], "ls.player.skill.scoring",
							jcbScoring.isSelected(),
							Colors.getColor(Colors.COLOR_PLAYER_SC), format);
					models[12] = new LinesChartDataModel(statistikWerte[12],
							"ls.player.skill.setpieces", jcbSetPieces.isSelected(),
							Colors.getColor(Colors.COLOR_PLAYER_SP), format);
					models[13] = new LinesChartDataModel(statistikWerte[13], "RecapPanel.Stars",
							jcbRating.isSelected(),
							Colors.getColor(Colors.COLOR_PLAYER_RATING), format);

					models[14] = new LinesChartDataModel(statistikWerte[14], "ls.player.loyalty",
							jcbLoyalty.isSelected(),
							Colors.getColor(Colors.COLOR_PLAYER_LOYALTY), format);
				}

				oChartPanel.setAllValues(models, statistikWerte[15], format, HOVerwaltung
						.instance().getLanguageString("Wochen"), "",
						jcbInscribe.isSelected(), jcbHelpLines.isSelected());
			} else {
				oChartPanel.setAllValues(null, new double[0], format, HOVerwaltung
						.instance().getLanguageString("Wochen"), "",
						jcbInscribe.isSelected(), jcbHelpLines.isSelected());
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}

	private String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}

	
}
