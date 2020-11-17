package module.statistics;

import core.constants.player.PlayerAbility;
import core.datatype.CBItem;
import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.util.chart.GraphDataModel;
import core.gui.theme.HOColorName;
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
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
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
 * Panel Matches in Module Statistics
 */
public class MatchesStatisticsPanel extends LazyImagePanel {

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
	private JButton m_jbUbernehmen;
	private JCheckBox m_jchBeschriftung;
	private JCheckBox m_jchHilflinien;
	private JComboBox m_jcbSpieleFilter;
	private JTextField m_jtfAnzahlHRF;
	private StatistikPanel m_clStatistikPanel;

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

		ActionListener checkBoxActionListener = e -> {
			if (e.getSource() == m_jchHilflinien) {
				m_clStatistikPanel.setHelpLines(m_jchHilflinien.isSelected());
				UserParameter.instance().statistikSpielerFinanzenHilfslinien = m_jchHilflinien
						.isSelected();
			} else if (e.getSource() == m_jchBeschriftung) {
				m_clStatistikPanel.setLabelling(m_jchBeschriftung.isSelected());
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

		m_jbUbernehmen.addActionListener(e -> initStatistik());

		m_jtfAnzahlHRF.addFocusListener(new FocusAdapter() {

			@Override
			public final void focusLost(FocusEvent focusEvent) {
				Helper.parseInt(HOMainFrame.instance(), m_jtfAnzahlHRF, false);
			}
		});

		m_jcbSpieleFilter.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				initStatistik();
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
		constraints2.insets = new Insets(20,0,0,0);  //top padding
		m_jchBewertung = new ImageCheckbox(getLangStr("Rating"),
				ThemeManager.getColor(HOColorName.PALETTE15[0]),
				UserParameter.instance().statistikSpieleBewertung);
		m_jchBewertung.setOpaque(false);
		layout2.setConstraints(m_jchBewertung, constraints2);
		panel2.add(m_jchBewertung);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 8;
		constraints2.insets = new Insets(0,0,0,0);  //top padding
		m_jchHatStats = new ImageCheckbox(getLangStr("ls.match.ratingtype.hatstats"),
				ThemeManager.getColor(HOColorName.PALETTE15[1]),
				UserParameter.instance().statistikSpieleHatStats);
		m_jchHatStats.setOpaque(false);
		layout2.setConstraints(m_jchHatStats, constraints2);
		panel2.add(m_jchHatStats);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 9;
		m_jchLoddarStats = new ImageCheckbox(getLangStr("ls.match.ratingtype.loddarstats"),
				ThemeManager.getColor(HOColorName.PALETTE15[2]),
				UserParameter.instance().statistikSpieleLoddarStats);
		m_jchLoddarStats.setOpaque(false);
		layout2.setConstraints(m_jchLoddarStats, constraints2);
		panel2.add(m_jchLoddarStats);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 10;
		m_jchGesamt = new ImageCheckbox(getLangStr("Gesamtstaerke"),
				ThemeManager.getColor(HOColorName.PALETTE15[3]),
				UserParameter.instance().statistikSpieleGesamt);
		m_jchGesamt.setOpaque(false);
		layout2.setConstraints(m_jchGesamt, constraints2);
		panel2.add(m_jchGesamt);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 11;
		constraints2.insets = new Insets(20,0,0,0);  //top padding
		m_jchMittelfeld = new ImageCheckbox(getLangStr("ls.match.ratingsector.midfield"),
				ThemeManager.getColor(HOColorName.PALETTE15[4]),
				UserParameter.instance().statistikSpieleMittelfeld);
		m_jchMittelfeld.setOpaque(false);
		layout2.setConstraints(m_jchMittelfeld, constraints2);
		panel2.add(m_jchMittelfeld);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 12;
		constraints2.insets = new Insets(0,0,0,0);  //top padding
		m_jchRechteAbwehr = new ImageCheckbox(getLangStr("ls.match.ratingsector.rightdefence"),
				ThemeManager.getColor(HOColorName.PALETTE15[5]),
				UserParameter.instance().statistikSpieleRechteAbwehr);
		m_jchRechteAbwehr.setOpaque(false);
		layout2.setConstraints(m_jchRechteAbwehr, constraints2);
		panel2.add(m_jchRechteAbwehr);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 13;
		m_jchAbwehrzentrum = new ImageCheckbox(getLangStr("ls.match.ratingsector.centraldefence"),
				ThemeManager.getColor(HOColorName.PALETTE15[6]),
				UserParameter.instance().statistikSpieleAbwehrzentrum);
		m_jchAbwehrzentrum.setOpaque(false);
		layout2.setConstraints(m_jchAbwehrzentrum, constraints2);
		panel2.add(m_jchAbwehrzentrum);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 14;
		m_jchLinkeAbwehr = new ImageCheckbox(getLangStr("ls.match.ratingsector.leftdefence"),
				ThemeManager.getColor(HOColorName.PALETTE15[7]),
				UserParameter.instance().statistikSpieleLinkeAbwehr);
		m_jchLinkeAbwehr.setOpaque(false);
		layout2.setConstraints(m_jchLinkeAbwehr, constraints2);
		panel2.add(m_jchLinkeAbwehr);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 15;
		m_jchRechterAngriff = new ImageCheckbox(getLangStr("ls.match.ratingsector.rightattack"),
				ThemeManager.getColor(HOColorName.PALETTE15[8]),
				UserParameter.instance().statistikSpieleRechterAngriff);
		m_jchRechterAngriff.setOpaque(false);
		layout2.setConstraints(m_jchRechterAngriff, constraints2);
		panel2.add(m_jchRechterAngriff);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 16;
		m_jchAngriffszentrum = new ImageCheckbox(getLangStr("ls.match.ratingsector.centralattack"),
				ThemeManager.getColor(HOColorName.PALETTE15[9]),
				UserParameter.instance().statistikSpieleAngriffszentrum);
		m_jchAngriffszentrum.setOpaque(false);
		layout2.setConstraints(m_jchAngriffszentrum, constraints2);
		panel2.add(m_jchAngriffszentrum);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 17;
		m_jchLinkerAngriff = new ImageCheckbox(getLangStr("ls.match.ratingsector.leftattack"),
				ThemeManager.getColor(HOColorName.PALETTE15[10]),
				UserParameter.instance().statistikSpieleLinkerAngriff);
		m_jchLinkerAngriff.setOpaque(false);
		layout2.setConstraints(m_jchLinkerAngriff, constraints2);
		panel2.add(m_jchLinkerAngriff);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 18;
		m_jchStimmung = new ImageCheckbox(getLangStr("ls.team.teamspirit"),
				ThemeManager.getColor(HOColorName.PALETTE15[11]),
				UserParameter.instance().statistikSpieleStimmung);
		m_jchStimmung.setOpaque(false);
		layout2.setConstraints(m_jchStimmung, constraints2);
		panel2.add(m_jchStimmung);

		constraints2.gridwidth = 2;
		constraints2.gridx = 0;
		constraints2.gridy = 19;
		m_jchSelbstvertrauen = new ImageCheckbox(getLangStr("ls.team.confidence"),
				ThemeManager.getColor(HOColorName.PALETTE15[12]),
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
				Matchdetails details = matchkurzinfos[matchkurzinfos.length - i - 1].getMatchdetails();

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
					final MatchLineupPlayer player = team.get(j);

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

			GraphDataModel[] models = new GraphDataModel[statistikWerte.length];

			if (statistikWerte.length > 0) {
				double maxRating = Helper.getMaxValue(statistikWerte[0]);
				models[0] = new GraphDataModel(statistikWerte[0], "Bewertung",
						m_jchBewertung.isSelected(),
						ThemeManager.getColor(HOColorName.PALETTE15[0]),
						Helper.DEFAULTDEZIMALFORMAT, 17/maxRating);
				models[1] = new GraphDataModel(statistikWerte[1], "ls.match.ratingsector.midfield",
						m_jchMittelfeld.isSelected(),
						ThemeManager.getColor(HOColorName.PALETTE15[4]),
						Helper.DEFAULTDEZIMALFORMAT);
				models[2] = new GraphDataModel(statistikWerte[2],
						"ls.match.ratingsector.rightdefence", m_jchRechteAbwehr.isSelected(),
						ThemeManager.getColor(HOColorName.PALETTE15[5]),
						Helper.DEFAULTDEZIMALFORMAT);
				models[3] = new GraphDataModel(statistikWerte[3],
						"ls.match.ratingsector.centraldefence", m_jchAbwehrzentrum.isSelected(),
						ThemeManager.getColor(HOColorName.PALETTE15[6]),
						Helper.DEFAULTDEZIMALFORMAT);
				models[4] = new GraphDataModel(statistikWerte[4],
						"ls.match.ratingsector.leftdefence", m_jchRechteAbwehr.isSelected(),
						ThemeManager.getColor(HOColorName.PALETTE15[7]),
						Helper.DEFAULTDEZIMALFORMAT);
				models[5] = new GraphDataModel(statistikWerte[5],
						"ls.match.ratingsector.rightattack", m_jchRechterAngriff.isSelected(),
						ThemeManager.getColor(HOColorName.PALETTE15[8]),
						Helper.DEFAULTDEZIMALFORMAT);
				models[6] = new GraphDataModel(statistikWerte[6],
						"ls.match.ratingsector.centralattack", m_jchAngriffszentrum.isSelected(),
						ThemeManager.getColor(HOColorName.PALETTE15[9]),
						Helper.DEFAULTDEZIMALFORMAT);
				models[7] = new GraphDataModel(statistikWerte[7],
						"ls.match.ratingsector.leftattack", m_jchLinkerAngriff.isSelected(),
						ThemeManager.getColor(HOColorName.PALETTE15[10]),
						Helper.DEFAULTDEZIMALFORMAT);
				models[8] = new GraphDataModel(statistikWerte[8], "Gesamtstaerke",
						m_jchGesamt.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[3]),
						Helper.DEZIMALFORMAT_2STELLEN);
				models[9] = new GraphDataModel(statistikWerte[9], "ls.team.teamspirit",
						m_jchStimmung.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[11]),
						Helper.INTEGERFORMAT);
				models[10] = new GraphDataModel(statistikWerte[10], "ls.team.confidence",
						m_jchSelbstvertrauen.isSelected(),
						ThemeManager.getColor(HOColorName.PALETTE15[12]), Helper.INTEGERFORMAT);

				double maxHatStats = Helper.getMaxValue(statistikWerte[11]);
				models[11] = new GraphDataModel(statistikWerte[11], "ls.match.ratingtype.hatstats",
						m_jchHatStats.isSelected(),
						ThemeManager.getColor(HOColorName.PALETTE15[1]), Helper.INTEGERFORMAT,
						19/maxHatStats);

				double maxLoddar = Helper.getMaxValue(statistikWerte[12]);
				models[12] = new GraphDataModel(statistikWerte[12],
						"ls.match.ratingtype.loddarstats", m_jchLoddarStats.isSelected(),
						ThemeManager.getColor(HOColorName.PALETTE15[2]),
						Helper.DEZIMALFORMAT_2STELLEN, 15/maxLoddar);
			}

			String[] yBezeichnungen = Helper.convertTimeMillisToFormatString(statistikWerte[13]);

			m_clStatistikPanel.setAllValues(models, yBezeichnungen, Helper.DEFAULTDEZIMALFORMAT,
					HOVerwaltung.instance().getLanguageString("Spiele"), "",
					m_jchBeschriftung.isSelected(), m_jchHilflinien.isSelected());

			boolean needsRefresh = false;
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
