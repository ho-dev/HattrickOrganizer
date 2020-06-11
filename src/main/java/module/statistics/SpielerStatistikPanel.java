// %634087379:de.hattrickorganizer.gui.statistic%
package module.statistics;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.model.SpielerCBItem;
import core.gui.model.SpielerCBItemRenderer;
import core.gui.model.StatistikModel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
 * Das StatistikPanel
 */
class SpielerStatistikPanel extends LazyImagePanel {

	private static final long serialVersionUID = -5003282359250534295L;
	private ImageCheckbox m_jchBewertung;
	private ImageCheckbox m_jchErfahrung;
	private ImageCheckbox m_jchFluegel;
	private ImageCheckbox m_jchForm;
	private ImageCheckbox m_jchFuehrung;
	private ImageCheckbox m_jchLoyalty;
	private ImageCheckbox m_jchGehalt;
	private ImageCheckbox m_jchKondition;
	private ImageCheckbox m_jchMarktwert;
	private ImageCheckbox m_jchPasspiel;
	private ImageCheckbox m_jchSpielaufbau;
	private ImageCheckbox m_jchStandards;
	private ImageCheckbox m_jchTorschuss;
	private ImageCheckbox m_jchTorwart;
	private ImageCheckbox m_jchVerteidigung;
	private JButton m_jbDrucken;
	private JButton m_jbUbernehmen;
	private JCheckBox m_jchBeschriftung;
	private JCheckBox m_jchHilflinien;
	private JComboBox m_jcbSpieler;
	private JTextField m_jtfAnzahlHRF;
	private StatistikPanel m_clStatistikPanel;
	private boolean m_bInitialisiert;

	public final void setAktuelleSpieler(int spielerid) {
		Helper.markierenComboBox(m_jcbSpieler, spielerid);
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
		this.m_jtfAnzahlHRF.addFocusListener(new FocusAdapter() {
			@Override
			public final void focusLost(FocusEvent focusEvent) {
				Helper.parseInt(HOMainFrame.instance(), m_jtfAnzahlHRF, false);
			}
		});

		m_jbUbernehmen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				initStatistik();
			}
		});

		m_jbDrucken.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (m_jcbSpieler.getSelectedItem() != null) {
					String name = ((SpielerCBItem) m_jcbSpieler.getSelectedItem()).getSpieler()
							.getFullName();
					m_clStatistikPanel.doPrint(name);
				}
			}
		});

		m_jcbSpieler.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					initStatistik();
				}
			}
		});

		ActionListener cbActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == m_jchHilflinien) {
					m_clStatistikPanel.setHilfslinien(m_jchHilflinien.isSelected());
					UserParameter.instance().statistikHilfslinien = m_jchHilflinien.isSelected();
				} else if (e.getSource() == m_jchBeschriftung) {
					m_clStatistikPanel.setBeschriftung(m_jchBeschriftung.isSelected());
					UserParameter.instance().statistikBeschriftung = m_jchBeschriftung.isSelected();
				} else if (e.getSource() == m_jchFuehrung.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.player.leadership", m_jchFuehrung.isSelected());
					UserParameter.instance().statistikFuehrung = m_jchFuehrung.isSelected();
				} else if (e.getSource() == m_jchErfahrung.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.player.experience", m_jchErfahrung.isSelected());
					UserParameter.instance().statistikErfahrung = m_jchErfahrung.isSelected();
				} else if (e.getSource() == m_jchLoyalty.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.player.loyalty", m_jchLoyalty.isSelected());
					UserParameter.instance().statistikLoyalty = m_jchLoyalty.isSelected();
				} else if (e.getSource() == m_jchForm.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.player.form", m_jchForm.isSelected());
					UserParameter.instance().statistikForm = m_jchForm.isSelected();
				} else if (e.getSource() == m_jchKondition.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.player.skill.stamina",
							m_jchKondition.isSelected());
					UserParameter.instance().statistikKondition = m_jchKondition.isSelected();
				} else if (e.getSource() == m_jchTorwart.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.player.skill.keeper", m_jchTorwart.isSelected());
					UserParameter.instance().statistikTorwart = m_jchTorwart.isSelected();
				} else if (e.getSource() == m_jchVerteidigung.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.player.skill.defending",
							m_jchVerteidigung.isSelected());
					UserParameter.instance().statistikVerteidigung = m_jchVerteidigung.isSelected();
				} else if (e.getSource() == m_jchSpielaufbau.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.player.skill.playmaking",
							m_jchSpielaufbau.isSelected());
					UserParameter.instance().statistikSpielaufbau = m_jchSpielaufbau.isSelected();
				} else if (e.getSource() == m_jchPasspiel.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.player.skill.passing",
							m_jchPasspiel.isSelected());
					UserParameter.instance().statistikPasspiel = m_jchPasspiel.isSelected();
				} else if (e.getSource() == m_jchFluegel.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.player.skill.winger", m_jchFluegel.isSelected());
					UserParameter.instance().statistikFluegel = m_jchFluegel.isSelected();
				} else if (e.getSource() == m_jchTorschuss.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.player.skill.scoring",
							m_jchTorschuss.isSelected());
					UserParameter.instance().statistikTorschuss = m_jchTorschuss.isSelected();
				} else if (e.getSource() == m_jchStandards.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.player.skill.setpieces",
							m_jchStandards.isSelected());
					UserParameter.instance().statistikStandards = m_jchStandards.isSelected();
				} else if (e.getSource() == m_jchBewertung.getCheckbox()) {
					m_clStatistikPanel.setShow("Rating", m_jchBewertung.isSelected());
					UserParameter.instance().statistikBewertung = m_jchBewertung.isSelected();
				} else if (e.getSource() == m_jchMarktwert.getCheckbox()) {
					m_clStatistikPanel.setShow("Marktwert", m_jchMarktwert.isSelected());
					UserParameter.instance().statistikSpielerFinanzenMarktwert = m_jchMarktwert
							.isSelected();
				} else if (e.getSource() == m_jchGehalt.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.player.wage", m_jchGehalt.isSelected());
					UserParameter.instance().statistikSpielerFinanzenGehalt = m_jchGehalt
							.isSelected();
				}
			}
		};
		m_jchHilflinien.addActionListener(cbActionListener);
		m_jchBeschriftung.addActionListener(cbActionListener);
		m_jchBewertung.addActionListener(cbActionListener);
		m_jchFuehrung.addActionListener(cbActionListener);
		m_jchErfahrung.addActionListener(cbActionListener);
		m_jchMarktwert.addActionListener(cbActionListener);
		m_jchGehalt.addActionListener(cbActionListener);
		m_jchForm.addActionListener(cbActionListener);
		m_jchKondition.addActionListener(cbActionListener);
		m_jchLoyalty.addActionListener(cbActionListener);
		m_jchTorwart.addActionListener(cbActionListener);
		m_jchVerteidigung.addActionListener(cbActionListener);
		m_jchSpielaufbau.addActionListener(cbActionListener);
		m_jchPasspiel.addActionListener(cbActionListener);
		m_jchFluegel.addActionListener(cbActionListener);
		m_jchTorschuss.addActionListener(cbActionListener);
		m_jchStandards.addActionListener(cbActionListener);
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

		constraints2.gridx = 0;
		constraints2.gridy = 0;
		constraints2.gridwidth = 2;
		constraints2.fill = GridBagConstraints.NONE;
		constraints2.anchor = GridBagConstraints.WEST;
		m_jbDrucken = new JButton(ThemeManager.getIcon(HOIconName.PRINTER));
		m_jbDrucken.setToolTipText(HOVerwaltung.instance()
				.getLanguageString("tt_Statistik_drucken"));
		m_jbDrucken.setPreferredSize(new Dimension(25, 25));
		layout2.setConstraints(m_jbDrucken, constraints2);
		panel2.add(m_jbDrucken);

		label = new JLabel(getLangStr("Wochen"));
		constraints2.gridwidth = 1;
		constraints2.fill = GridBagConstraints.HORIZONTAL;
		constraints2.anchor = GridBagConstraints.WEST;
		constraints2.gridx = 0;
		constraints2.gridy = 1;
		layout2.setConstraints(label, constraints2);
		panel2.add(label);
		m_jtfAnzahlHRF = new JTextField(String.valueOf(UserParameter.instance().statistikAnzahlHRF));
		m_jtfAnzahlHRF.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints2.gridx = 1;
		constraints2.gridy = 1;
		layout2.setConstraints(m_jtfAnzahlHRF, constraints2);
		panel2.add(m_jtfAnzahlHRF);

		constraints2.gridx = 0;
		constraints2.gridy = 2;
		constraints2.gridwidth = 2;
		m_jbUbernehmen = new JButton(getLangStr("ls.button.apply"));
		m_jbUbernehmen.setToolTipText(getLangStr("tt_Statistik_HRFAnzahluebernehmen"));
		layout2.setConstraints(m_jbUbernehmen, constraints2);
		panel2.add(m_jbUbernehmen);

		label = new JLabel(getLangStr("Spieler"));
		label.setToolTipText(getLangStr("tt_Statistik_Spieler"));
		constraints2.gridx = 0;
		constraints2.gridy = 3;
		constraints2.gridwidth = 2;
		layout2.setConstraints(label, constraints2);
		panel2.add(label);
		constraints2.gridx = 0;
		constraints2.gridy = 4;
		m_jcbSpieler = new JComboBox();
		m_jcbSpieler.setToolTipText(getLangStr("tt_Statistik_Spieler"));
		m_jcbSpieler.setRenderer(new SpielerCBItemRenderer());
		m_jcbSpieler.setBackground(ColorLabelEntry.BG_STANDARD);
		m_jcbSpieler.setMaximumRowCount(25);
		m_jcbSpieler.setMaximumSize(new Dimension(200, 25));
		layout2.setConstraints(m_jcbSpieler, constraints2);
		panel2.add(m_jcbSpieler);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 5;
		m_jchHilflinien = new JCheckBox(getLangStr("Hilflinien"),
				UserParameter.instance().statistikHilfslinien);
		m_jchHilflinien.setOpaque(false);
		m_jchHilflinien.setBackground(Color.white);
		layout2.setConstraints(m_jchHilflinien, constraints2);
		panel2.add(m_jchHilflinien);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 6;
		m_jchBeschriftung = new JCheckBox(getLangStr("Beschriftung"),
				UserParameter.instance().statistikBeschriftung);
		m_jchBeschriftung.setOpaque(false);
		m_jchBeschriftung.setBackground(Color.white);
		layout2.setConstraints(m_jchBeschriftung, constraints2);
		panel2.add(m_jchBeschriftung);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 7;
		m_jchBewertung = new ImageCheckbox(getLangStr("Rating"),
				ThemeManager.getColor(HOColorName.STAT_RATING),
				UserParameter.instance().statistikBewertung);
		m_jchBewertung.setOpaque(false);
		layout2.setConstraints(m_jchBewertung, constraints2);
		panel2.add(m_jchBewertung);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 8;
		m_jchFuehrung = new ImageCheckbox(getLangStr("ls.player.leadership"),
				ThemeManager.getColor(HOColorName.STAT_LEADERSHIP),
				UserParameter.instance().statistikFuehrung);
		m_jchFuehrung.setOpaque(false);
		layout2.setConstraints(m_jchFuehrung, constraints2);
		panel2.add(m_jchFuehrung);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 9;
		m_jchErfahrung = new ImageCheckbox(getLangStr("ls.player.experience"),
				ThemeManager.getColor(HOColorName.STAT_EXPERIENCE),
				UserParameter.instance().statistikErfahrung);
		m_jchErfahrung.setOpaque(false);
		layout2.setConstraints(m_jchErfahrung, constraints2);
		panel2.add(m_jchErfahrung);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 10;
		m_jchMarktwert = new ImageCheckbox(getLangStr("ls.player.tsi"),
				ThemeManager.getColor(HOColorName.STAT_MARKETVALUE),
				UserParameter.instance().statistikSpielerFinanzenMarktwert);
		m_jchMarktwert.setOpaque(false);
		layout2.setConstraints(m_jchMarktwert, constraints2);
		panel2.add(m_jchMarktwert);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 11;
		m_jchGehalt = new ImageCheckbox(getLangStr("ls.player.wage"),
				ThemeManager.getColor(HOColorName.STAT_WAGE),
				UserParameter.instance().statistikSpielerFinanzenGehalt);
		m_jchGehalt.setOpaque(false);
		layout2.setConstraints(m_jchGehalt, constraints2);
		panel2.add(m_jchGehalt);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 5;
		m_jchForm = new ImageCheckbox(getLangStr("ls.player.form"),
				ThemeManager.getColor(HOColorName.STAT_FORM),
				UserParameter.instance().statistikForm);
		m_jchForm.setOpaque(false);
		layout2.setConstraints(m_jchForm, constraints2);
		panel2.add(m_jchForm);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 6;
		m_jchKondition = new ImageCheckbox(getLangStr("ls.player.skill.stamina"),
				ThemeManager.getColor(HOColorName.STAT_STAMINA),
				UserParameter.instance().statistikKondition);
		m_jchKondition.setOpaque(false);
		layout2.setConstraints(m_jchKondition, constraints2);
		panel2.add(m_jchKondition);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 7;
		m_jchLoyalty = new ImageCheckbox(getLangStr("ls.player.loyalty"),
				ThemeManager.getColor(HOColorName.STAT_LOYALTY),
				UserParameter.instance().statistikLoyalty);
		m_jchLoyalty.setOpaque(false);
		layout2.setConstraints(m_jchLoyalty, constraints2);
		panel2.add(m_jchLoyalty);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 8;
		m_jchTorwart = new ImageCheckbox(getLangStr("ls.player.skill.keeper"),
				ThemeManager.getColor(HOColorName.STAT_KEEPER),
				UserParameter.instance().statistikTorwart);
		m_jchTorwart.setOpaque(false);
		layout2.setConstraints(m_jchTorwart, constraints2);
		panel2.add(m_jchTorwart);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 9;
		m_jchVerteidigung = new ImageCheckbox(getLangStr("ls.player.skill.defending"),
				ThemeManager.getColor(HOColorName.STAT_DEFENDING),
				UserParameter.instance().statistikVerteidigung);
		m_jchVerteidigung.setOpaque(false);
		layout2.setConstraints(m_jchVerteidigung, constraints2);
		panel2.add(m_jchVerteidigung);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 10;
		m_jchSpielaufbau = new ImageCheckbox(getLangStr("ls.player.skill.playmaking"),
				ThemeManager.getColor(HOColorName.STAT_PLAYMAKING),
				UserParameter.instance().statistikSpielaufbau);
		m_jchSpielaufbau.setOpaque(false);
		layout2.setConstraints(m_jchSpielaufbau, constraints2);
		panel2.add(m_jchSpielaufbau);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 11;
		m_jchPasspiel = new ImageCheckbox(getLangStr("ls.player.skill.passing"),
				ThemeManager.getColor(HOColorName.STAT_PASSING),
				UserParameter.instance().statistikPasspiel);
		m_jchPasspiel.setOpaque(false);
		layout2.setConstraints(m_jchPasspiel, constraints2);
		panel2.add(m_jchPasspiel);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 12;
		m_jchFluegel = new ImageCheckbox(getLangStr("ls.player.skill.winger"),
				ThemeManager.getColor(HOColorName.STAT_WINGER),
				UserParameter.instance().statistikFluegel);
		m_jchFluegel.setOpaque(false);
		layout2.setConstraints(m_jchFluegel, constraints2);
		panel2.add(m_jchFluegel);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 13;
		m_jchTorschuss = new ImageCheckbox(getLangStr("ls.player.skill.scoring"),
				ThemeManager.getColor(HOColorName.STAT_SCORING),
				UserParameter.instance().statistikTorschuss);
		m_jchTorschuss.setOpaque(false);
		layout2.setConstraints(m_jchTorschuss, constraints2);
		panel2.add(m_jchTorschuss);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 14;
		m_jchStandards = new ImageCheckbox(getLangStr("ls.player.skill.setpieces"),
				ThemeManager.getColor(HOColorName.STAT_SET_PIECES),
				UserParameter.instance().statistikStandards);
		m_jchStandards.setOpaque(false);
		layout2.setConstraints(m_jchStandards, constraints2);
		panel2.add(m_jchStandards);

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

		m_jcbSpieler.setModel(new DefaultComboBoxModel(cbItems.toArray()));
		// Kein Player selektiert
		m_jcbSpieler.setSelectedItem(null);
	}

	private void initStatistik() {
		try {
			int anzahlHRF = Integer.parseInt(m_jtfAnzahlHRF.getText());

			if (anzahlHRF <= 0) {
				anzahlHRF = 1;
			}

			UserParameter.instance().statistikAnzahlHRF = anzahlHRF;

			NumberFormat format = Helper.DEFAULTDEZIMALFORMAT;
			NumberFormat format2 = NumberFormat.getCurrencyInstance();

			if (m_jcbSpieler.getSelectedItem() != null) {
				final double[][] statistikWerte = DBManager.instance().getSpielerDaten4Statistik(
						((SpielerCBItem) m_jcbSpieler.getSelectedItem()).getSpieler()
								.getSpielerID(), anzahlHRF);
				final StatistikModel[] models = new StatistikModel[statistikWerte.length];

				// Es sind 15 Werte!
				if (statistikWerte.length > 0) {
					double faktor = 20 / Helper.getMaxValue(statistikWerte[0]);
					models[0] = new StatistikModel(statistikWerte[0], "Marktwert",
							m_jchMarktwert.isSelected(),
							ThemeManager.getColor(HOColorName.STAT_MARKETVALUE), format, faktor);
					faktor = 20 / Helper.getMaxValue(statistikWerte[1]);
					models[1] = new StatistikModel(statistikWerte[1], "ls.player.wage",
							m_jchGehalt.isSelected(), ThemeManager.getColor(HOColorName.STAT_WAGE),
							format2, faktor);
					models[2] = new StatistikModel(statistikWerte[2], "ls.player.leadership",
							m_jchFuehrung.isSelected(),
							ThemeManager.getColor(HOColorName.STAT_LEADERSHIP), format);
					models[3] = new StatistikModel(statistikWerte[3], "ls.player.experience",
							m_jchErfahrung.isSelected(),
							ThemeManager.getColor(HOColorName.STAT_EXPERIENCE), format);
					models[4] = new StatistikModel(statistikWerte[4], "ls.player.form",
							m_jchForm.isSelected(), ThemeManager.getColor(HOColorName.STAT_FORM),
							format);
					models[5] = new StatistikModel(statistikWerte[5], "ls.player.skill.stamina",
							m_jchKondition.isSelected(),
							ThemeManager.getColor(HOColorName.STAT_STAMINA), format);
					models[6] = new StatistikModel(statistikWerte[6], "ls.player.skill.keeper",
							m_jchTorwart.isSelected(),
							ThemeManager.getColor(HOColorName.STAT_KEEPER), format);
					models[7] = new StatistikModel(statistikWerte[7], "ls.player.skill.defending",
							m_jchVerteidigung.isSelected(),
							ThemeManager.getColor(HOColorName.STAT_DEFENDING), format);
					models[8] = new StatistikModel(statistikWerte[8], "ls.player.skill.playmaking",
							m_jchSpielaufbau.isSelected(),
							ThemeManager.getColor(HOColorName.STAT_PLAYMAKING), format);
					models[9] = new StatistikModel(statistikWerte[9], "ls.player.skill.passing",
							m_jchPasspiel.isSelected(),
							ThemeManager.getColor(HOColorName.STAT_PASSING), format);
					models[10] = new StatistikModel(statistikWerte[10], "ls.player.skill.winger",
							m_jchFluegel.isSelected(),
							ThemeManager.getColor(HOColorName.STAT_WINGER), format);
					models[11] = new StatistikModel(statistikWerte[11], "ls.player.skill.scoring",
							m_jchTorschuss.isSelected(),
							ThemeManager.getColor(HOColorName.STAT_SCORING), format);
					models[12] = new StatistikModel(statistikWerte[12],
							"ls.player.skill.setpieces", m_jchStandards.isSelected(),
							ThemeManager.getColor(HOColorName.STAT_SET_PIECES), format);
					models[13] = new StatistikModel(statistikWerte[13], "Rating",
							m_jchBewertung.isSelected(),
							ThemeManager.getColor(HOColorName.STAT_RATING), format);
					models[14] = new StatistikModel(statistikWerte[14], "ls.player.loyalty",
							m_jchLoyalty.isSelected(),
							ThemeManager.getColor(HOColorName.STAT_LOYALTY), format);
				}

				String[] yBezeichnungen = Helper
						.convertTimeMillisToFormatString(statistikWerte[15]);

				m_clStatistikPanel.setAllValues(models, yBezeichnungen, format, HOVerwaltung
						.instance().getLanguageString("Wochen"), "",
						m_jchBeschriftung.isSelected(), m_jchHilflinien.isSelected());
			} else {
				m_clStatistikPanel.setAllValues(null, new String[0], format, HOVerwaltung
						.instance().getLanguageString("Wochen"), "",
						m_jchBeschriftung.isSelected(), m_jchHilflinien.isSelected());
			}

		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}

	private String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}
}
