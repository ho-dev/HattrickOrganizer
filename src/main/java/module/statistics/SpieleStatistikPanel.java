// %119582289:de.hattrickorganizer.gui.statistic%
package module.statistics;

import core.constants.player.PlayerAbility;
import core.datatype.CBItem;
import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.model.StatistikModel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchLineupPlayer;
import core.model.match.Matchdetails;
import core.model.player.IMatchRoleID;
import core.util.HOLogger;
import core.util.Helper;
import module.matches.SpielePanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
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
public class SpieleStatistikPanel extends LazyImagePanel {

	private static final long serialVersionUID = 3954095099686666846L;
	private ImageCheckbox m_jchAbwehrzentrum;
	private ImageCheckbox m_jchAngriffszentrum;
	private ImageCheckbox m_jchBewertung;
	private ImageCheckbox m_jchGesamt;
	private ImageCheckbox m_jchLinkeAbwehr;
	private ImageCheckbox m_jchLinkerAngriff;
	private ImageCheckbox m_jchMittelfeld;
	private ImageCheckbox m_jchRechteAbwehr;
	private ImageCheckbox m_jchRechterAngriff;
	private ImageCheckbox m_jchSelbstvertrauen;
	private ImageCheckbox m_jchStimmung;
	private ImageCheckbox m_jchHatStats;
	private ImageCheckbox m_jchLoddarStats;
	private JButton m_jbDrucken;
	private JButton m_jbUbernehmen;
	private JCheckBox m_jchBeschriftung;
	private JCheckBox m_jchHilflinien;
	private JComboBox m_jcbSpieleFilter;
	private JTextField m_jtfAnzahlHRF;
	private StatistikPanel m_clStatistikPanel;
	private boolean initialized = false;
	private boolean needsRefresh = false;

	@Override
	protected void initialize() {
		initComponents();
		addListeners();
		initStatistik();
		setNeedsRefresh(false);
		registerRefreshable(true);
	}

	@Override
	protected void update() {
		initStatistik();
	}

	private void addListeners() {

		ActionListener checkBoxActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == m_jchHilflinien) {
					m_clStatistikPanel.setHilfslinien(m_jchHilflinien.isSelected());
					UserParameter.instance().statistikSpielerFinanzenHilfslinien = m_jchHilflinien
							.isSelected();
				} else if (e.getSource() == m_jchBeschriftung) {
					m_clStatistikPanel.setBeschriftung(m_jchBeschriftung.isSelected());
					UserParameter.instance().statistikSpielerFinanzenBeschriftung = m_jchBeschriftung
							.isSelected();
				} else if (e.getSource() == m_jchBewertung.getCheckbox()) {
					m_clStatistikPanel.setShow("Bewertung", m_jchBewertung.isSelected());
					UserParameter.instance().statistikSpieleBewertung = m_jchBewertung.isSelected();
				} else if (e.getSource() == m_jchGesamt.getCheckbox()) {
					m_clStatistikPanel.setShow("Gesamtstaerke", m_jchGesamt.isSelected());
					UserParameter.instance().statistikSpieleGesamt = m_jchGesamt.isSelected();
				} else if (e.getSource() == m_jchMittelfeld.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.match.ratingsector.midfield",
							m_jchMittelfeld.isSelected());
					UserParameter.instance().statistikSpieleMittelfeld = m_jchMittelfeld
							.isSelected();
				} else if (e.getSource() == m_jchRechteAbwehr.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.match.ratingsector.rightdefence",
							m_jchRechteAbwehr.isSelected());
					UserParameter.instance().statistikSpieleRechteAbwehr = m_jchRechteAbwehr
							.isSelected();
				} else if (e.getSource() == m_jchAbwehrzentrum.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.match.ratingsector.centraldefence",
							m_jchAbwehrzentrum.isSelected());
					UserParameter.instance().statistikSpieleAbwehrzentrum = m_jchAbwehrzentrum
							.isSelected();
				} else if (e.getSource() == m_jchLinkeAbwehr.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.match.ratingsector.leftdefence",
							m_jchLinkeAbwehr.isSelected());
					UserParameter.instance().statistikSpieleLinkeAbwehr = m_jchLinkeAbwehr
							.isSelected();
				} else if (e.getSource() == m_jchRechterAngriff.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.match.ratingsector.rightattack",
							m_jchRechterAngriff.isSelected());
					UserParameter.instance().statistikSpieleRechterAngriff = m_jchRechterAngriff
							.isSelected();
				} else if (e.getSource() == m_jchAngriffszentrum.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.match.ratingsector.centralattack",
							m_jchAngriffszentrum.isSelected());
					UserParameter.instance().statistikSpieleAngriffszentrum = m_jchAngriffszentrum
							.isSelected();
				} else if (e.getSource() == m_jchLinkerAngriff.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.match.ratingsector.leftattack",
							m_jchLinkerAngriff.isSelected());
					UserParameter.instance().statistikSpieleLinkerAngriff = m_jchLinkerAngriff
							.isSelected();
				} else if (e.getSource() == m_jchStimmung.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.team.teamspirit", m_jchStimmung.isSelected());
					UserParameter.instance().statistikSpieleStimmung = m_jchStimmung.isSelected();
				} else if (e.getSource() == m_jchHatStats.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.match.ratingtype.hatstats",
							m_jchHatStats.isSelected());
					UserParameter.instance().statistikSpieleHatStats = m_jchHatStats.isSelected();
				} else if (e.getSource() == m_jchLoddarStats.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.match.ratingtype.loddarstats",
							m_jchLoddarStats.isSelected());
					UserParameter.instance().statistikSpieleLoddarStats = m_jchLoddarStats
							.isSelected();
				} else if (e.getSource() == m_jchSelbstvertrauen.getCheckbox()) {
					m_clStatistikPanel.setShow("ls.team.confidence",
							m_jchSelbstvertrauen.isSelected());
					UserParameter.instance().statistikSpieleSelbstvertrauen = m_jchSelbstvertrauen
							.isSelected();
				}
			}
		};
		m_jchHilflinien.addActionListener(checkBoxActionListener);
		m_jchBeschriftung.addActionListener(checkBoxActionListener);
		m_jchBewertung.addActionListener(checkBoxActionListener);
		m_jchHatStats.addActionListener(checkBoxActionListener);
		m_jchLoddarStats.addActionListener(checkBoxActionListener);
		m_jchGesamt.addActionListener(checkBoxActionListener);
		m_jchMittelfeld.addActionListener(checkBoxActionListener);
		m_jchRechteAbwehr.addActionListener(checkBoxActionListener);
		m_jchAbwehrzentrum.addActionListener(checkBoxActionListener);
		m_jchLinkeAbwehr.addActionListener(checkBoxActionListener);
		m_jchRechterAngriff.addActionListener(checkBoxActionListener);
		m_jchAngriffszentrum.addActionListener(checkBoxActionListener);
		m_jchLinkerAngriff.addActionListener(checkBoxActionListener);
		m_jchStimmung.addActionListener(checkBoxActionListener);
		m_jchSelbstvertrauen.addActionListener(checkBoxActionListener);

		m_jbDrucken.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				m_clStatistikPanel.doPrint(getLangStr("Spiele"));
			}
		});

		m_jbUbernehmen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				initStatistik();
			}
		});

		m_jtfAnzahlHRF.addFocusListener(new FocusAdapter() {

			@Override
			public final void focusLost(FocusEvent focusEvent) {
				Helper.parseInt(HOMainFrame.instance(), m_jtfAnzahlHRF, false);
			}
		});

		m_jcbSpieleFilter.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					initStatistik();
				}
			}
		});
	}

	private void initComponents() {
		JLabel label;

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(2, 0, 2, 0);

		setLayout(layout);

		JPanel panel2 = new ImagePanel();
		GridBagLayout layout2 = new GridBagLayout();
		GridBagConstraints constraints2 = new GridBagConstraints();
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
		m_jbDrucken = new JButton(ImageUtilities.getSvgIcon(HOIconName.PRINTER));
		m_jbDrucken.setToolTipText(HOVerwaltung.instance()
				.getLanguageString("tt_Statistik_drucken"));
		m_jbDrucken.setPreferredSize(new Dimension(25, 25));
		layout2.setConstraints(m_jbDrucken, constraints2);
		panel2.add(m_jbDrucken);

		label = new JLabel(getLangStr("Wochen"));
		constraints2.fill = GridBagConstraints.HORIZONTAL;
		constraints2.anchor = GridBagConstraints.WEST;
		constraints2.gridx = 0;
		constraints2.gridy = 1;
		constraints2.gridwidth = 1;
		layout2.setConstraints(label, constraints2);
		panel2.add(label);
		m_jtfAnzahlHRF = new JTextField(
				String.valueOf(UserParameter.instance().statistikSpielerFinanzenAnzahlHRF), 5);
		m_jtfAnzahlHRF.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints2.gridx = 1;
		constraints2.gridy = 1;
		layout2.setConstraints(m_jtfAnzahlHRF, constraints2);
		panel2.add(m_jtfAnzahlHRF);

		constraints2.gridx = 0;
		constraints2.gridy = 2;
		constraints2.gridwidth = 2;
		m_jbUbernehmen = new JButton(getLangStr("ls.button.apply"));
		layout2.setConstraints(m_jbUbernehmen, constraints2);
		m_jbUbernehmen.setToolTipText(getLangStr("tt_Statistik_HRFAnzahluebernehmen"));
		panel2.add(m_jbUbernehmen);

		constraints2.gridx = 0;
		constraints2.gridy = 3;
		constraints2.gridwidth = 2;
		m_jcbSpieleFilter = new JComboBox(getMatchFilterItems());
		m_jcbSpieleFilter.setPreferredSize(new Dimension(150, 25));
		Helper.markierenComboBox(m_jcbSpieleFilter, UserParameter.instance().statistikSpieleFilter);
		m_jcbSpieleFilter.setFont(m_jcbSpieleFilter.getFont().deriveFont(Font.BOLD));
		layout2.setConstraints(m_jcbSpieleFilter, constraints2);
		panel2.add(m_jcbSpieleFilter);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 5;
		m_jchHilflinien = new JCheckBox(getLangStr("Hilflinien"),
				UserParameter.instance().statistikSpielerFinanzenHilfslinien);
		m_jchHilflinien.setOpaque(false);
		m_jchHilflinien.setBackground(Color.white);
		layout2.setConstraints(m_jchHilflinien, constraints2);
		panel2.add(m_jchHilflinien);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 6;
		m_jchBeschriftung = new JCheckBox(getLangStr("Beschriftung"),
				UserParameter.instance().statistikSpielerFinanzenBeschriftung);
		m_jchBeschriftung.setOpaque(false);
		m_jchBeschriftung.setBackground(Color.white);
		layout2.setConstraints(m_jchBeschriftung, constraints2);
		panel2.add(m_jchBeschriftung);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 7;
		m_jchBewertung = new ImageCheckbox(getLangStr("Rating"),
				ThemeManager.getColor(HOColorName.STAT_RATING2),
				UserParameter.instance().statistikSpieleBewertung);
		m_jchBewertung.setOpaque(false);
		layout2.setConstraints(m_jchBewertung, constraints2);
		panel2.add(m_jchBewertung);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 8;
		m_jchHatStats = new ImageCheckbox(getLangStr("ls.match.ratingtype.hatstats"),
				ThemeManager.getColor(HOColorName.STAT_HATSTATS),
				UserParameter.instance().statistikSpieleHatStats);
		m_jchHatStats.setOpaque(false);
		layout2.setConstraints(m_jchHatStats, constraints2);
		panel2.add(m_jchHatStats);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 9;
		m_jchLoddarStats = new ImageCheckbox(getLangStr("ls.match.ratingtype.loddarstats"),
				ThemeManager.getColor(HOColorName.STAT_LODDAR),
				UserParameter.instance().statistikSpieleLoddarStats);
		m_jchLoddarStats.setOpaque(false);
		layout2.setConstraints(m_jchLoddarStats, constraints2);
		panel2.add(m_jchLoddarStats);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 10;
		m_jchGesamt = new ImageCheckbox(getLangStr("Gesamtstaerke"),
				ThemeManager.getColor(HOColorName.STAT_TOTAL),
				UserParameter.instance().statistikSpieleGesamt);
		m_jchGesamt.setOpaque(false);
		layout2.setConstraints(m_jchGesamt, constraints2);
		panel2.add(m_jchGesamt);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 11;
		m_jchMittelfeld = new ImageCheckbox(getLangStr("ls.match.ratingsector.midfield"),
				ThemeManager.getColor(HOColorName.SHIRT_MIDFIELD),
				UserParameter.instance().statistikSpieleMittelfeld);
		m_jchMittelfeld.setOpaque(false);
		layout2.setConstraints(m_jchMittelfeld, constraints2);
		panel2.add(m_jchMittelfeld);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 12;
		m_jchRechteAbwehr = new ImageCheckbox(getLangStr("ls.match.ratingsector.rightdefence"),
				ThemeManager.getColor(HOColorName.SHIRT_WINGBACK).darker(),
				UserParameter.instance().statistikSpieleRechteAbwehr);
		m_jchRechteAbwehr.setOpaque(false);
		layout2.setConstraints(m_jchRechteAbwehr, constraints2);
		panel2.add(m_jchRechteAbwehr);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 13;
		m_jchAbwehrzentrum = new ImageCheckbox(getLangStr("ls.match.ratingsector.centraldefence"),
				ThemeManager.getColor(HOColorName.SHIRT_CENTRALDEFENCE),
				UserParameter.instance().statistikSpieleAbwehrzentrum);
		m_jchAbwehrzentrum.setOpaque(false);
		layout2.setConstraints(m_jchAbwehrzentrum, constraints2);
		panel2.add(m_jchAbwehrzentrum);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 14;
		m_jchLinkeAbwehr = new ImageCheckbox(getLangStr("ls.match.ratingsector.leftdefence"),
				ThemeManager.getColor(HOColorName.SHIRT_WINGBACK).brighter(),
				UserParameter.instance().statistikSpieleLinkeAbwehr);
		m_jchLinkeAbwehr.setOpaque(false);
		layout2.setConstraints(m_jchLinkeAbwehr, constraints2);
		panel2.add(m_jchLinkeAbwehr);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 15;
		m_jchRechterAngriff = new ImageCheckbox(getLangStr("ls.match.ratingsector.rightattack"),
				ThemeManager.getColor(HOColorName.SHIRT_WING).darker(),
				UserParameter.instance().statistikSpieleRechterAngriff);
		m_jchRechterAngriff.setOpaque(false);
		layout2.setConstraints(m_jchRechterAngriff, constraints2);
		panel2.add(m_jchRechterAngriff);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 16;
		m_jchAngriffszentrum = new ImageCheckbox(getLangStr("ls.match.ratingsector.centralattack"),
				ThemeManager.getColor(HOColorName.SHIRT_FORWARD),
				UserParameter.instance().statistikSpieleAngriffszentrum);
		m_jchAngriffszentrum.setOpaque(false);
		layout2.setConstraints(m_jchAngriffszentrum, constraints2);
		panel2.add(m_jchAngriffszentrum);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 17;
		m_jchLinkerAngriff = new ImageCheckbox(getLangStr("ls.match.ratingsector.leftattack"),
				ThemeManager.getColor(HOColorName.SHIRT_WING).brighter(),
				UserParameter.instance().statistikSpieleLinkerAngriff);
		m_jchLinkerAngriff.setOpaque(false);
		layout2.setConstraints(m_jchLinkerAngriff, constraints2);
		panel2.add(m_jchLinkerAngriff);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 18;
		m_jchStimmung = new ImageCheckbox(getLangStr("ls.team.teamspirit"),
				ThemeManager.getColor(HOColorName.STAT_MOOD),
				UserParameter.instance().statistikSpieleStimmung);
		m_jchStimmung.setOpaque(false);
		layout2.setConstraints(m_jchStimmung, constraints2);
		panel2.add(m_jchStimmung);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 19;
		m_jchSelbstvertrauen = new ImageCheckbox(getLangStr("ls.team.confidence"),
				ThemeManager.getColor(HOColorName.STAT_CONFIDENCE),
				UserParameter.instance().statistikSpieleSelbstvertrauen);
		m_jchSelbstvertrauen.setOpaque(false);
		layout2.setConstraints(m_jchSelbstvertrauen, constraints2);
		panel2.add(m_jchSelbstvertrauen);

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
		JPanel panel = new ImagePanel();
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

	private void initStatistik() {
		try {
			int anzahlHRF = Integer.parseInt(m_jtfAnzahlHRF.getText());

			if (anzahlHRF <= 0) {
				anzahlHRF = 1;
			}

			UserParameter.instance().statistikSpielerFinanzenAnzahlHRF = anzahlHRF;
			UserParameter.instance().statistikSpieleFilter = ((CBItem) m_jcbSpieleFilter
					.getSelectedItem()).getId();

			MatchKurzInfo[] matchkurzinfos = DBManager.instance().getMatchesKurzInfo(
					HOVerwaltung.instance().getModel().getBasics().getTeamId(),
					((CBItem) m_jcbSpieleFilter.getSelectedItem()).getId(), true);

			int anzahl = Math.min(matchkurzinfos.length, anzahlHRF);
			int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();

			double[][] statistikWerte = new double[14][anzahl];

			// Infos zusammenstellen
			for (int i = 0; i < anzahl; i++) {
				Matchdetails details = DBManager.instance().getMatchDetails(
						matchkurzinfos[matchkurzinfos.length - i - 1].getMatchID());

				int bewertungwert = 0;
				double loddarStats = 0;
				// Für match
				int sublevel = 0;

				// Für gesamtstärke
				double temp = 0d;

				if (details.getHeimId() == teamid) {
					sublevel = (details.getHomeMidfield()) % 4;

					bewertungwert = ((details.getHomeMidfield() - 1) / 4) + 1;
					statistikWerte[1][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = (details.getHomeRightDef()) % 4;

					bewertungwert = ((details.getHomeRightDef() - 1) / 4) + 1;
					statistikWerte[2][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = (details.getHomeMidDef()) % 4;

					bewertungwert = ((details.getHomeMidDef() - 1) / 4) + 1;
					statistikWerte[3][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = (details.getHomeLeftDef()) % 4;

					bewertungwert = ((details.getHomeLeftDef() - 1) / 4) + 1;
					statistikWerte[4][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = (details.getHomeRightAtt()) % 4;

					bewertungwert = ((details.getHomeRightAtt() - 1) / 4) + 1;
					statistikWerte[5][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = (details.getHomeMidAtt()) % 4;

					bewertungwert = ((details.getHomeMidAtt() - 1) / 4) + 1;
					statistikWerte[6][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = (details.getHomeLeftAtt()) % 4;

					bewertungwert = ((details.getHomeLeftAtt() - 1) / 4) + 1;
					statistikWerte[7][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					temp = details.getHomeGesamtstaerke(false);
					sublevel = ((int) temp) % 4;
					statistikWerte[8][i] = (((int) temp - 1) / 4) + 1
							+ PlayerAbility.getValue4Sublevel(sublevel);
					statistikWerte[11][i] = details.getHomeHatStats();
					// Calculate and return the LoddarStats rating
					statistikWerte[12][i] = details.getHomeLoddarStats();
				} else {
					sublevel = (details.getGuestMidfield()) % 4;

					bewertungwert = ((details.getGuestMidfield() - 1) / 4) + 1;
					statistikWerte[1][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = (details.getGuestRightDef()) % 4;

					bewertungwert = ((details.getGuestRightDef() - 1) / 4) + 1;
					statistikWerte[2][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = (details.getGuestMidDef()) % 4;

					bewertungwert = ((details.getGuestMidDef() - 1) / 4) + 1;
					statistikWerte[3][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = (details.getGuestLeftDef()) % 4;

					bewertungwert = ((details.getGuestLeftDef() - 1) / 4) + 1;
					statistikWerte[4][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = (details.getGuestRightAtt()) % 4;

					bewertungwert = ((details.getGuestRightAtt() - 1) / 4) + 1;
					statistikWerte[5][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = (details.getGuestMidAtt()) % 4;

					bewertungwert = ((details.getGuestMidAtt() - 1) / 4) + 1;
					statistikWerte[6][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = (details.getGuestLeftAtt()) % 4;

					bewertungwert = ((details.getGuestLeftAtt() - 1) / 4) + 1;
					statistikWerte[7][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					temp = details.getGuestGesamtstaerke(false);
					sublevel = ((int) temp) % 4;
					statistikWerte[8][i] = (((int) temp - 1) / 4) + 1
							+ PlayerAbility.getValue4Sublevel(sublevel);
					statistikWerte[11][i] = details.getAwayHatStats();
					// Calculate and return the LoddarStats rating
					statistikWerte[12][i] = details.getAwayLoddarStats();
				}

				// Stimmung, Selbstvertrauen
				int hrfid = DBManager.instance().getHRFID4Date(
						matchkurzinfos[matchkurzinfos.length - i - 1].getMatchDateAsTimestamp());
				int[] stimmungSelbstvertrauen = DBManager.instance()
						.getStimmmungSelbstvertrauenValues(hrfid);

				statistikWerte[9][i] = stimmungSelbstvertrauen[0];
				statistikWerte[10][i] = stimmungSelbstvertrauen[1];

				statistikWerte[13][i] = matchkurzinfos[matchkurzinfos.length - i - 1]
						.getMatchDateAsTimestamp().getTime();

				List<MatchLineupPlayer> team = DBManager.instance().getMatchLineupPlayers(
						matchkurzinfos[matchkurzinfos.length - i - 1].getMatchID(), teamid);
				float sterne = 0;

				// Sterne
				for (int j = 0; j < team.size(); j++) {
					final MatchLineupPlayer player = (MatchLineupPlayer) team.get(j);

					if (player.getId() < IMatchRoleID.startReserves
							&& player.getId() >= IMatchRoleID.startLineup) {
						float rating = (float) player.getRating();

						if (rating > 0) {
							sterne += rating;
						}
					}
				}

				statistikWerte[0][i] = sterne;
			}

			StatistikModel[] models = new StatistikModel[statistikWerte.length];

			// Es sind 13 Werte!
			if (statistikWerte.length > 0) {
				double faktor = 20 / Helper.getMaxValue(statistikWerte[0]);
				models[0] = new StatistikModel(statistikWerte[0], "Bewertung",
						m_jchBewertung.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_RATING2),
						Helper.DEFAULTDEZIMALFORMAT, faktor);
				models[1] = new StatistikModel(statistikWerte[1], "ls.match.ratingsector.midfield",
						m_jchMittelfeld.isSelected(),
						ThemeManager.getColor(HOColorName.SHIRT_MIDFIELD),
						Helper.DEFAULTDEZIMALFORMAT);
				models[2] = new StatistikModel(statistikWerte[2],
						"ls.match.ratingsector.rightdefence", m_jchRechteAbwehr.isSelected(),
						ThemeManager.getColor(HOColorName.SHIRT_WINGBACK).darker(),
						Helper.DEFAULTDEZIMALFORMAT);
				models[3] = new StatistikModel(statistikWerte[3],
						"ls.match.ratingsector.centraldefence", m_jchAbwehrzentrum.isSelected(),
						ThemeManager.getColor(HOColorName.SHIRT_CENTRALDEFENCE),
						Helper.DEFAULTDEZIMALFORMAT);
				models[4] = new StatistikModel(statistikWerte[4],
						"ls.match.ratingsector.leftdefence", m_jchRechteAbwehr.isSelected(),
						ThemeManager.getColor(HOColorName.SHIRT_WINGBACK).brighter(),
						Helper.DEFAULTDEZIMALFORMAT);
				models[5] = new StatistikModel(statistikWerte[5],
						"ls.match.ratingsector.rightattack", m_jchRechterAngriff.isSelected(),
						ThemeManager.getColor(HOColorName.SHIRT_WING).darker(),
						Helper.DEFAULTDEZIMALFORMAT);
				models[6] = new StatistikModel(statistikWerte[6],
						"ls.match.ratingsector.centralattack", m_jchAngriffszentrum.isSelected(),
						ThemeManager.getColor(HOColorName.SHIRT_FORWARD),
						Helper.DEFAULTDEZIMALFORMAT);
				models[7] = new StatistikModel(statistikWerte[7],
						"ls.match.ratingsector.leftattack", m_jchLinkerAngriff.isSelected(),
						ThemeManager.getColor(HOColorName.SHIRT_WING).brighter(),
						Helper.DEFAULTDEZIMALFORMAT);
				models[8] = new StatistikModel(statistikWerte[8], "Gesamtstaerke",
						m_jchGesamt.isSelected(), ThemeManager.getColor(HOColorName.STAT_TOTAL),
						Helper.DEZIMALFORMAT_2STELLEN);
				models[9] = new StatistikModel(statistikWerte[9], "ls.team.teamspirit",
						m_jchStimmung.isSelected(), ThemeManager.getColor(HOColorName.STAT_MOOD),
						Helper.INTEGERFORMAT);
				models[10] = new StatistikModel(statistikWerte[10], "ls.team.confidence",
						m_jchSelbstvertrauen.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_CONFIDENCE), Helper.INTEGERFORMAT);
				faktor = 20 / Helper.getMaxValue(statistikWerte[11]);
				models[11] = new StatistikModel(statistikWerte[11], "ls.match.ratingtype.hatstats",
						m_jchHatStats.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_HATSTATS), Helper.INTEGERFORMAT,
						faktor);
				faktor = 20 / Helper.getMaxValue(statistikWerte[12]);
				models[12] = new StatistikModel(statistikWerte[12],
						"ls.match.ratingtype.loddarstats", m_jchLoddarStats.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_LODDAR),
						Helper.DEZIMALFORMAT_2STELLEN, faktor);
			}

			String[] yBezeichnungen = Helper.convertTimeMillisToFormatString(statistikWerte[13]);

			m_clStatistikPanel.setAllValues(models, yBezeichnungen, Helper.DEFAULTDEZIMALFORMAT,
					HOVerwaltung.instance().getLanguageString("Spiele"), "",
					m_jchBeschriftung.isSelected(), m_jchHilflinien.isSelected());

			this.needsRefresh = false;
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}

	private CBItem[] getMatchFilterItems() {
		CBItem[] items = {
				new CBItem(getLangStr("NurEigeneSpiele"), SpielePanel.NUR_EIGENE_SPIELE
						+ SpielePanel.NUR_GESPIELTEN_SPIELE),
				new CBItem(getLangStr("NurEigenePflichtspiele"),
						SpielePanel.NUR_EIGENE_PFLICHTSPIELE + SpielePanel.NUR_GESPIELTEN_SPIELE),
				new CBItem(getLangStr("NurEigenePokalspiele"), SpielePanel.NUR_EIGENE_POKALSPIELE
						+ SpielePanel.NUR_GESPIELTEN_SPIELE),
				new CBItem(HOVerwaltung.instance().getLanguageString("OnlySecondaryCup"),
						SpielePanel.ONLY_SECONDARY_CUP),
				new CBItem(getLangStr("NurEigeneLigaspiele"), SpielePanel.NUR_EIGENE_LIGASPIELE
						+ SpielePanel.NUR_GESPIELTEN_SPIELE),
				new CBItem(HOVerwaltung.instance().getLanguageString("OnlyQualificationMatches"),
						SpielePanel.ONLY_QUALIF_MATCHES),
				new CBItem(HOVerwaltung.instance()
						.getLanguageString("NurEigeneFreundschaftsspiele"),
						SpielePanel.NUR_EIGENE_FREUNDSCHAFTSSPIELE
								+ SpielePanel.NUR_GESPIELTEN_SPIELE),
				new CBItem(getLangStr("NurEigeneTournamentsspiele"),
						SpielePanel.NUR_EIGENE_TOURNAMENTSPIELE + SpielePanel.NUR_GESPIELTEN_SPIELE) };
		return items;
	}

	private String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}
}
