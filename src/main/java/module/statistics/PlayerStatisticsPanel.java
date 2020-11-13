package module.statistics;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.model.SpielerCBItem;
import core.gui.model.PlayerCBItemRenderer;
import core.gui.model.StatistikModel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.Player;
import core.util.HOLogger;
import core.util.Helper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.text.NumberFormat;
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
	private JCheckBox jcbDataLabels;
	private JCheckBox jcbHelpLines;
	private JComboBox<SpielerCBItem> jcbPlayer;
	private JTextField jtfNbWeeks;
	private StatistikPanel oStatisticsPanel;

	public final void setPlayer(int playerID) {
		Helper.markierenComboBox(jcbPlayer, playerID);
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
			public final void focusLost(FocusEvent focusEvent) {
				Helper.parseInt(HOMainFrame.instance(), jtfNbWeeks, false);
			}
		});

		jbApply.addActionListener(e -> initStatistik());

		jcbPlayer.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				initStatistik();
			}
		});

		ActionListener cbActionListener = e -> {
			if (e.getSource() == jcbHelpLines) {
				oStatisticsPanel.setHilfslinien(jcbHelpLines.isSelected());
				UserParameter.instance().statistikHilfslinien = jcbHelpLines.isSelected();
			} else if (e.getSource() == jcbDataLabels) {
				oStatisticsPanel.setBeschriftung(jcbDataLabels.isSelected());
				UserParameter.instance().statistikBeschriftung = jcbDataLabels.isSelected();
			} else if (e.getSource() == jcbLeadership.getCheckbox()) {
				oStatisticsPanel.setShow("ls.player.leadership", jcbLeadership.isSelected());
				UserParameter.instance().statistikFuehrung = jcbLeadership.isSelected();
			} else if (e.getSource() == jcbExperience.getCheckbox()) {
				oStatisticsPanel.setShow("ls.player.experience", jcbExperience.isSelected());
				UserParameter.instance().statistikErfahrung = jcbExperience.isSelected();
			} else if (e.getSource() == jcbLoyalty.getCheckbox()) {
				oStatisticsPanel.setShow("ls.player.loyalty", jcbLoyalty.isSelected());
				UserParameter.instance().statistikLoyalty = jcbLoyalty.isSelected();
			} else if (e.getSource() == jcbForm.getCheckbox()) {
				oStatisticsPanel.setShow("ls.player.form", jcbForm.isSelected());
				UserParameter.instance().statistikForm = jcbForm.isSelected();
			} else if (e.getSource() == jcbStamina.getCheckbox()) {
				oStatisticsPanel.setShow("ls.player.skill.stamina",
						jcbStamina.isSelected());
				UserParameter.instance().statistikKondition = jcbStamina.isSelected();
			} else if (e.getSource() == jcbKeeper.getCheckbox()) {
				oStatisticsPanel.setShow("ls.player.skill.keeper", jcbKeeper.isSelected());
				UserParameter.instance().statistikTorwart = jcbKeeper.isSelected();
			} else if (e.getSource() == jcbDefending.getCheckbox()) {
				oStatisticsPanel.setShow("ls.player.skill.defending",
						jcbDefending.isSelected());
				UserParameter.instance().statistikVerteidigung = jcbDefending.isSelected();
			} else if (e.getSource() == jcbPlaymaking.getCheckbox()) {
				oStatisticsPanel.setShow("ls.player.skill.playmaking",
						jcbPlaymaking.isSelected());
				UserParameter.instance().statistikSpielaufbau = jcbPlaymaking.isSelected();
			} else if (e.getSource() == jcbPass.getCheckbox()) {
				oStatisticsPanel.setShow("ls.player.skill.passing",
						jcbPass.isSelected());
				UserParameter.instance().statistikPasspiel = jcbPass.isSelected();
			} else if (e.getSource() == jcbWing.getCheckbox()) {
				oStatisticsPanel.setShow("ls.player.skill.winger", jcbWing.isSelected());
				UserParameter.instance().statistikFluegel = jcbWing.isSelected();
			} else if (e.getSource() == jcbScoring.getCheckbox()) {
				oStatisticsPanel.setShow("ls.player.skill.scoring",
						jcbScoring.isSelected());
				UserParameter.instance().statistikTorschuss = jcbScoring.isSelected();
			} else if (e.getSource() == jcbSetPieces.getCheckbox()) {
				oStatisticsPanel.setShow("ls.player.skill.setpieces",
						jcbSetPieces.isSelected());
				UserParameter.instance().statistikStandards = jcbSetPieces.isSelected();
			} else if (e.getSource() == jcbRating.getCheckbox()) {
				oStatisticsPanel.setShow("Rating", jcbRating.isSelected());
				UserParameter.instance().statistikBewertung = jcbRating.isSelected();
			} else if (e.getSource() == jcbTSI.getCheckbox()) {
				oStatisticsPanel.setShow("Marktwert", jcbTSI.isSelected());
				UserParameter.instance().statistikSpielerFinanzenMarktwert = jcbTSI
						.isSelected();
			} else if (e.getSource() == jcbSalary.getCheckbox()) {
				oStatisticsPanel.setShow("ls.player.wage", jcbSalary.isSelected());
				UserParameter.instance().statistikSpielerFinanzenGehalt = jcbSalary
						.isSelected();
			}
		};
		jcbHelpLines.addActionListener(cbActionListener);
		jcbDataLabels.addActionListener(cbActionListener);
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

		label = new JLabel(getLangStr("Wochen"));
		constraints2.gridwidth = 1;
		constraints2.fill = GridBagConstraints.HORIZONTAL;
		constraints2.anchor = GridBagConstraints.WEST;
		constraints2.gridx = 0;
		constraints2.gridy = 1;
		layout2.setConstraints(label, constraints2);
		panel2.add(label);
		jtfNbWeeks = new JTextField(String.valueOf(UserParameter.instance().statistikAnzahlHRF));
		jtfNbWeeks.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints2.gridx = 1;
		constraints2.gridy = 1;
		layout2.setConstraints(jtfNbWeeks, constraints2);
		panel2.add(jtfNbWeeks);

		constraints2.gridx = 0;
		constraints2.gridy = 2;
		constraints2.gridwidth = 2;
		jbApply = new JButton(getLangStr("ls.button.apply"));
		jbApply.setToolTipText(getLangStr("tt_Statistik_HRFAnzahluebernehmen"));
		layout2.setConstraints(jbApply, constraints2);
		panel2.add(jbApply);

		label = new JLabel(getLangStr("Spieler"));
		label.setToolTipText(getLangStr("tt_Statistik_Spieler"));
		constraints2.gridx = 0;
		constraints2.gridy = 3;
		constraints2.gridwidth = 2;
		layout2.setConstraints(label, constraints2);
		panel2.add(label);
		constraints2.gridx = 0;
		constraints2.gridy = 4;
		jcbPlayer = new JComboBox();
		jcbPlayer.setToolTipText(getLangStr("tt_Statistik_Spieler"));
		jcbPlayer.setRenderer(new PlayerCBItemRenderer());
		jcbPlayer.setBackground(ColorLabelEntry.BG_STANDARD);
		jcbPlayer.setMaximumRowCount(25);
		jcbPlayer.setMaximumSize(new Dimension(200, 25));
		layout2.setConstraints(jcbPlayer, constraints2);
		panel2.add(jcbPlayer);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 5;
		jcbHelpLines = new JCheckBox(getLangStr("Hilflinien"),
				UserParameter.instance().statistikHilfslinien);
		jcbHelpLines.setOpaque(false);
		jcbHelpLines.setBackground(Color.white);
		layout2.setConstraints(jcbHelpLines, constraints2);
		panel2.add(jcbHelpLines);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 6;
		jcbDataLabels = new JCheckBox(getLangStr("Beschriftung"),
				UserParameter.instance().statistikBeschriftung);
		jcbDataLabels.setOpaque(false);
		jcbDataLabels.setBackground(Color.white);
		layout2.setConstraints(jcbDataLabels, constraints2);
		panel2.add(jcbDataLabels);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 7;
		jcbRating = new ImageCheckbox(getLangStr("Rating"),
				ThemeManager.getColor(HOColorName.PALETTE15[0]),
				UserParameter.instance().statistikBewertung);
		jcbRating.setOpaque(false);
		layout2.setConstraints(jcbRating, constraints2);
		panel2.add(jcbRating);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 8;
		jcbLeadership = new ImageCheckbox(getLangStr("ls.player.leadership"),
				ThemeManager.getColor(HOColorName.PALETTE15[1]),
				UserParameter.instance().statistikFuehrung);
		jcbLeadership.setOpaque(false);
		layout2.setConstraints(jcbLeadership, constraints2);
		panel2.add(jcbLeadership);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 9;
		jcbExperience = new ImageCheckbox(getLangStr("ls.player.experience"),
				ThemeManager.getColor(HOColorName.PALETTE15[2]),
				UserParameter.instance().statistikErfahrung);
		jcbExperience.setOpaque(false);
		layout2.setConstraints(jcbExperience, constraints2);
		panel2.add(jcbExperience);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 10;
		jcbTSI = new ImageCheckbox(getLangStr("ls.player.tsi"),
				ThemeManager.getColor(HOColorName.PALETTE15[3]),
				UserParameter.instance().statistikSpielerFinanzenMarktwert);
		jcbTSI.setOpaque(false);
		layout2.setConstraints(jcbTSI, constraints2);
		panel2.add(jcbTSI);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 11;
		jcbSalary = new ImageCheckbox(getLangStr("ls.player.wage"),
				ThemeManager.getColor(HOColorName.PALETTE15[4]),
				UserParameter.instance().statistikSpielerFinanzenGehalt);
		jcbSalary.setOpaque(false);
		layout2.setConstraints(jcbSalary, constraints2);
		panel2.add(jcbSalary);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 5;
		jcbForm = new ImageCheckbox(getLangStr("ls.player.form"),
				ThemeManager.getColor(HOColorName.PALETTE15[5]),
				UserParameter.instance().statistikForm);
		jcbForm.setOpaque(false);
		layout2.setConstraints(jcbForm, constraints2);
		panel2.add(jcbForm);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 6;
		jcbStamina = new ImageCheckbox(getLangStr("ls.player.skill.stamina"),
				ThemeManager.getColor(HOColorName.PALETTE15[6]),
				UserParameter.instance().statistikKondition);
		jcbStamina.setOpaque(false);
		layout2.setConstraints(jcbStamina, constraints2);
		panel2.add(jcbStamina);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 7;
		jcbLoyalty = new ImageCheckbox(getLangStr("ls.player.loyalty"),
				ThemeManager.getColor(HOColorName.PALETTE15[7]),
				UserParameter.instance().statistikLoyalty);
		jcbLoyalty.setOpaque(false);
		layout2.setConstraints(jcbLoyalty, constraints2);
		panel2.add(jcbLoyalty);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 8;
		jcbKeeper = new ImageCheckbox(getLangStr("ls.player.skill.keeper"),
				ThemeManager.getColor(HOColorName.PALETTE15[8]),
				UserParameter.instance().statistikTorwart);
		jcbKeeper.setOpaque(false);
		layout2.setConstraints(jcbKeeper, constraints2);
		panel2.add(jcbKeeper);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 9;
		jcbDefending = new ImageCheckbox(getLangStr("ls.player.skill.defending"),
				ThemeManager.getColor(HOColorName.PALETTE15[9]),
				UserParameter.instance().statistikVerteidigung);
		jcbDefending.setOpaque(false);
		layout2.setConstraints(jcbDefending, constraints2);
		panel2.add(jcbDefending);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 10;
		jcbPlaymaking = new ImageCheckbox(getLangStr("ls.player.skill.playmaking"),
				ThemeManager.getColor(HOColorName.PALETTE15[10]),
				UserParameter.instance().statistikSpielaufbau);
		jcbPlaymaking.setOpaque(false);
		layout2.setConstraints(jcbPlaymaking, constraints2);
		panel2.add(jcbPlaymaking);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 11;
		jcbPass = new ImageCheckbox(getLangStr("ls.player.skill.passing"),
				ThemeManager.getColor(HOColorName.PALETTE15[11]),
				UserParameter.instance().statistikPasspiel);
		jcbPass.setOpaque(false);
		layout2.setConstraints(jcbPass, constraints2);
		panel2.add(jcbPass);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 12;
		jcbWing = new ImageCheckbox(getLangStr("ls.player.skill.winger"),
				ThemeManager.getColor(HOColorName.PALETTE15[12]),
				UserParameter.instance().statistikFluegel);
		jcbWing.setOpaque(false);
		layout2.setConstraints(jcbWing, constraints2);
		panel2.add(jcbWing);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 13;
		jcbScoring = new ImageCheckbox(getLangStr("ls.player.skill.scoring"),
				ThemeManager.getColor(HOColorName.PALETTE15[13]),
				UserParameter.instance().statistikTorschuss);
		jcbScoring.setOpaque(false);
		layout2.setConstraints(jcbScoring, constraints2);
		panel2.add(jcbScoring);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 14;
		jcbSetPieces = new ImageCheckbox(getLangStr("ls.player.skill.setpieces"),
				ThemeManager.getColor(HOColorName.PALETTE15[14]),
				UserParameter.instance().statistikStandards);
		jcbSetPieces.setOpaque(false);
		layout2.setConstraints(jcbSetPieces, constraints2);
		panel2.add(jcbSetPieces);

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

		oStatisticsPanel = new StatistikPanel(false);
		panel.add(oStatisticsPanel);

		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.weighty = 1.0;
		constraints.weightx = 1.0;
		constraints.anchor = GridBagConstraints.NORTH;
		panel.setBorder(BorderFactory.createLineBorder(ThemeManager
				.getColor(HOColorName.PANEL_BORDER)));
		layout.setConstraints(panel, constraints);
		add(panel);
	}

	private void initSpielerCB() {
		List<Player> players = HOVerwaltung.instance().getModel().getCurrentPlayers();
		List<SpielerCBItem> spielerCBItems = new ArrayList<SpielerCBItem>(players.size());
		for (Player player : players) {
			spielerCBItems.add(new SpielerCBItem(player.getFullName(), 0f, player));
		}
		Collections.sort(spielerCBItems);

		// Alte Player
		List<Player> oldPlayers = HOVerwaltung.instance().getModel().getFormerPlayers();
		List<SpielerCBItem> spielerOldCBItems = new ArrayList<SpielerCBItem>(players.size());
		for (Player player : oldPlayers) {
			spielerOldCBItems.add(new SpielerCBItem(player.getFullName(), 0f, player));
		}
		Collections.sort(spielerOldCBItems);

		// Zusammenfügen
		List<SpielerCBItem> cbItems = new ArrayList<SpielerCBItem>(spielerCBItems.size()
				+ spielerOldCBItems.size() + 1);
		cbItems.addAll(spielerCBItems);
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

//			NumberFormat format3 = NumberFormat.getIntegerInstance();
//			format3.setGroupingUsed(true);
			NumberFormat format3 = Helper.getNumberFormat(false, 0);

			if (jcbPlayer.getSelectedItem() != null) {
				final double[][] statistikWerte = DBManager.instance().getSpielerDaten4Statistik(
						((SpielerCBItem) jcbPlayer.getSelectedItem()).getPlayer()
								.getSpielerID(), anzahlHRF);
				final StatistikModel[] models = new StatistikModel[statistikWerte.length];

				if (statistikWerte.length > 0) {
					double maxTSI = Helper.getMaxValue(statistikWerte[0]);
					double maxWage = Helper.getMaxValue(statistikWerte[1]);

					models[0] = new StatistikModel(statistikWerte[0], "Marktwert",
							jcbTSI.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[3]), format3, 19/maxTSI);

					models[1] = new StatistikModel(statistikWerte[1], "ls.player.wage",
							jcbSalary.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[4]),
							format2, 16/maxWage);

					models[2] = new StatistikModel(statistikWerte[2], "ls.player.leadership",
							jcbLeadership.isSelected(),
							ThemeManager.getColor(HOColorName.PALETTE15[1]), format);
					models[3] = new StatistikModel(statistikWerte[3], "ls.player.experience",
							jcbExperience.isSelected(),
							ThemeManager.getColor(HOColorName.PALETTE15[1]), format);
					models[4] = new StatistikModel(statistikWerte[4], "ls.player.form",
							jcbForm.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[5]),
							format);
					models[5] = new StatistikModel(statistikWerte[5], "ls.player.skill.stamina",
							jcbStamina.isSelected(),
							ThemeManager.getColor(HOColorName.PALETTE15[6]), format);
					models[6] = new StatistikModel(statistikWerte[6], "ls.player.skill.keeper",
							jcbKeeper.isSelected(),
							ThemeManager.getColor(HOColorName.PALETTE15[8]), format);
					models[7] = new StatistikModel(statistikWerte[7], "ls.player.skill.defending",
							jcbDefending.isSelected(),
							ThemeManager.getColor(HOColorName.PALETTE15[9]), format);
					models[8] = new StatistikModel(statistikWerte[8], "ls.player.skill.playmaking",
							jcbPlaymaking.isSelected(),
							ThemeManager.getColor(HOColorName.PALETTE15[10]), format);
					models[9] = new StatistikModel(statistikWerte[9], "ls.player.skill.passing",
							jcbPass.isSelected(),
							ThemeManager.getColor(HOColorName.PALETTE15[11]), format);
					models[10] = new StatistikModel(statistikWerte[10], "ls.player.skill.winger",
							jcbWing.isSelected(),
							ThemeManager.getColor(HOColorName.PALETTE15[12]), format);
					models[11] = new StatistikModel(statistikWerte[11], "ls.player.skill.scoring",
							jcbScoring.isSelected(),
							ThemeManager.getColor(HOColorName.PALETTE15[13]), format);
					models[12] = new StatistikModel(statistikWerte[12],
							"ls.player.skill.setpieces", jcbSetPieces.isSelected(),
							ThemeManager.getColor(HOColorName.PALETTE15[14]), format);
					models[13] = new StatistikModel(statistikWerte[13], "Rating",
							jcbRating.isSelected(),
							ThemeManager.getColor(HOColorName.PALETTE15[0]), format);
					models[14] = new StatistikModel(statistikWerte[14], "ls.player.loyalty",
							jcbLoyalty.isSelected(),
							ThemeManager.getColor(HOColorName.PALETTE15[7]), format);
				}

				String[] yBezeichnungen = Helper
						.convertTimeMillisToFormatString(statistikWerte[15]);

				oStatisticsPanel.setAllValues(models, yBezeichnungen, format, HOVerwaltung
						.instance().getLanguageString("Wochen"), "",
						jcbDataLabels.isSelected(), jcbHelpLines.isSelected());
			} else {
				oStatisticsPanel.setAllValues(null, new String[0], format, HOVerwaltung
						.instance().getLanguageString("Wochen"), "",
						jcbDataLabels.isSelected(), jcbHelpLines.isSelected());
			}

		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}

	private String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}
}
