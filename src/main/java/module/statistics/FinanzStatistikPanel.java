// %3604286658:de.hattrickorganizer.gui.statistic%
package module.statistics;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.model.StatistikModel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Das StatistikPanel
 */
public class FinanzStatistikPanel extends LazyImagePanel {
	private static final long serialVersionUID = 5245162268414878290L;

	private ImageCheckbox m_jchFans;
	private ImageCheckbox m_jchGesamtausgaben;
	private ImageCheckbox m_jchGesamteinnahmen;
	private ImageCheckbox m_jchGewinnVerlust;
	private ImageCheckbox m_jchJugend;
	private ImageCheckbox m_jchZinsaufwendungen;
	private ImageCheckbox m_jchKontostand;
	private ImageCheckbox m_jchMarktwert;
	private ImageCheckbox m_jchSonstigeAusgaben;
	private ImageCheckbox m_jchSonstigeEinnahmen;
	private ImageCheckbox m_jchSpielergehaelter;
	private ImageCheckbox m_jchSponsoren;
	private ImageCheckbox m_jchStadion;
	private ImageCheckbox m_jchTrainerstab;
	private ImageCheckbox m_jchZuschauer;
	private JButton m_jbUbernehmen;
	private JCheckBox m_jchBeschriftung;
	private JCheckBox m_jchHilflinien;
	private JTextField m_jtfAnzahlHRF;
	private StatistikPanel m_clStatistikPanel;

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
		ActionListener checkBoxActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == m_jchHilflinien) {
					m_clStatistikPanel.setHilfslinien(m_jchHilflinien.isSelected());
					UserParameter.instance().statistikFinanzenHilfslinien = m_jchHilflinien
							.isSelected();
				} else if (e.getSource() == m_jchBeschriftung) {
					m_clStatistikPanel.setBeschriftung(m_jchBeschriftung.isSelected());
					UserParameter.instance().statistikFinanzenBeschriftung = m_jchBeschriftung
							.isSelected();
				} else if (e.getSource() == m_jchKontostand.getCheckbox()) {
					m_clStatistikPanel.setShow("Kontostand", m_jchKontostand.isSelected());
					UserParameter.instance().statistikKontostand = m_jchKontostand.isSelected();
				} else if (e.getSource() == m_jchGewinnVerlust.getCheckbox()) {
					m_clStatistikPanel.setShow("GewinnVerlust", m_jchGewinnVerlust.isSelected());
					UserParameter.instance().statistikGewinnVerlust = m_jchGewinnVerlust
							.isSelected();
				} else if (e.getSource() == m_jchGesamteinnahmen.getCheckbox()) {
					m_clStatistikPanel
							.setShow("Gesamteinnahmen", m_jchGesamteinnahmen.isSelected());
					UserParameter.instance().statistikGesamtEinnahmen = m_jchGesamteinnahmen
							.isSelected();
				} else if (e.getSource() == m_jchGesamtausgaben.getCheckbox()) {
					m_clStatistikPanel.setShow("Gesamtausgaben", m_jchGesamtausgaben.isSelected());
					UserParameter.instance().statistikGesamtAusgaben = m_jchGesamtausgaben
							.isSelected();
				} else if (e.getSource() == m_jchZuschauer.getCheckbox()) {
					m_clStatistikPanel.setShow("Zuschauer", m_jchZuschauer.isSelected());
					UserParameter.instance().statistikZuschauer = m_jchZuschauer.isSelected();
				} else if (e.getSource() == m_jchSponsoren.getCheckbox()) {
					m_clStatistikPanel.setShow("Sponsoren", m_jchSponsoren.isSelected());
					UserParameter.instance().statistikSponsoren = m_jchSponsoren.isSelected();
				} else if (e.getSource() == m_jchSonstigeEinnahmen.getCheckbox()) {
					m_clStatistikPanel.setShow("SonstigeEinnahmen",
							m_jchSonstigeEinnahmen.isSelected());
					UserParameter.instance().statistikSonstigeEinnahmen = m_jchSonstigeEinnahmen
							.isSelected();
				} else if (e.getSource() == m_jchStadion.getCheckbox()) {
					m_clStatistikPanel.setShow("Stadion", m_jchStadion.isSelected());
					UserParameter.instance().statistikStadion = m_jchStadion.isSelected();
				} else if (e.getSource() == m_jchSpielergehaelter.getCheckbox()) {
					m_clStatistikPanel.setShow("Spielergehaelter",
							m_jchSpielergehaelter.isSelected());
					UserParameter.instance().statistikSpielergehaelter = m_jchSpielergehaelter
							.isSelected();
				} else if (e.getSource() == m_jchSonstigeAusgaben.getCheckbox()) {
					m_clStatistikPanel.setShow("SonstigeAusgaben",
							m_jchSonstigeAusgaben.isSelected());
					UserParameter.instance().statistikSonstigeAusgaben = m_jchSonstigeAusgaben
							.isSelected();
				} else if (e.getSource() == m_jchTrainerstab.getCheckbox()) {
					m_clStatistikPanel.setShow("Trainerstab", m_jchTrainerstab.isSelected());
					UserParameter.instance().statistikTrainerstab = m_jchTrainerstab.isSelected();
				} else if (e.getSource() == m_jchJugend.getCheckbox()) {
					m_clStatistikPanel.setShow("Jugend", m_jchJugend.isSelected());
					UserParameter.instance().statistikJugend = m_jchJugend.isSelected();
				} else if (e.getSource() == m_jchZinsaufwendungen.getCheckbox()) {
					m_clStatistikPanel.setShow("Zinsaufwendungen", m_jchZinsaufwendungen.isSelected());
					UserParameter.instance().statistikZinsaufwendungen = m_jchZinsaufwendungen.isSelected();
				} else if (e.getSource() == m_jchMarktwert.getCheckbox()) {
					m_clStatistikPanel.setShow("Marktwert", m_jchMarktwert.isSelected());
					UserParameter.instance().statistikMarktwert = m_jchMarktwert.isSelected();
				} else if (e.getSource() == m_jchFans.getCheckbox()) {
					m_clStatistikPanel.setShow("Fans", m_jchFans.isSelected());
					UserParameter.instance().statistikFananzahl = m_jchFans.isSelected();
				}
			}
		};
		m_jchHilflinien.addActionListener(checkBoxActionListener);
		m_jchBeschriftung.addActionListener(checkBoxActionListener);
		m_jchKontostand.addActionListener(checkBoxActionListener);
		m_jchGewinnVerlust.addActionListener(checkBoxActionListener);
		m_jchGesamteinnahmen.addActionListener(checkBoxActionListener);
		m_jchGesamtausgaben.addActionListener(checkBoxActionListener);
		m_jchZuschauer.addActionListener(checkBoxActionListener);
		m_jchSponsoren.addActionListener(checkBoxActionListener);
		m_jchSonstigeEinnahmen.addActionListener(checkBoxActionListener);
		m_jchStadion.addActionListener(checkBoxActionListener);
		m_jchSpielergehaelter.addActionListener(checkBoxActionListener);
		m_jchSonstigeAusgaben.addActionListener(checkBoxActionListener);
		m_jchTrainerstab.addActionListener(checkBoxActionListener);
		m_jchJugend.addActionListener(checkBoxActionListener);
		m_jchZinsaufwendungen.addActionListener(checkBoxActionListener);
		m_jchMarktwert.addActionListener(checkBoxActionListener);
		m_jchFans.addActionListener(checkBoxActionListener);

		m_jbUbernehmen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				initStatistik();
			}
		});

		m_jtfAnzahlHRF.addFocusListener(new FocusAdapter() {
			@Override
			public final void focusLost(java.awt.event.FocusEvent focusEvent) {
				Helper.parseInt(HOMainFrame.instance(), m_jtfAnzahlHRF, false);
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
				String.valueOf(UserParameter.instance().statistikFinanzenAnzahlHRF));
		m_jtfAnzahlHRF.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints2.gridx = 1;
		constraints2.gridy = 1;
		layout2.setConstraints(m_jtfAnzahlHRF, constraints2);
		panel2.add(m_jtfAnzahlHRF);

		constraints2.gridx = 0;
		constraints2.gridy = 4;
		constraints2.gridwidth = 2;
		m_jbUbernehmen = new JButton(getLangStr("ls.button.apply"));
		m_jbUbernehmen.setToolTipText(getLangStr("tt_Statistik_HRFAnzahluebernehmen"));
		layout2.setConstraints(m_jbUbernehmen, constraints2);
		panel2.add(m_jbUbernehmen);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 5;
		m_jchHilflinien = new JCheckBox(getLangStr("Hilflinien"),
				UserParameter.instance().statistikFinanzenHilfslinien);
		m_jchHilflinien.setOpaque(false);
		layout2.setConstraints(m_jchHilflinien, constraints2);
		panel2.add(m_jchHilflinien);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 6;
		m_jchBeschriftung = new JCheckBox(getLangStr("Beschriftung"),
				UserParameter.instance().statistikFinanzenBeschriftung);
		m_jchBeschriftung.setOpaque(false);
		layout2.setConstraints(m_jchBeschriftung, constraints2);
		panel2.add(m_jchBeschriftung);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 5;
		m_jchKontostand = new ImageCheckbox(getLangStr("Kontostand"),
				ThemeManager.getColor(HOColorName.STAT_CASH),
				UserParameter.instance().statistikKontostand);
		m_jchKontostand.setOpaque(false);
		layout2.setConstraints(m_jchKontostand, constraints2);
		panel2.add(m_jchKontostand);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 6;
		m_jchGewinnVerlust = new ImageCheckbox(getLangStr("GewinnVerlust"),
				ThemeManager.getColor(HOColorName.STAT_WINLOST),
				UserParameter.instance().statistikGewinnVerlust);
		m_jchGewinnVerlust.setOpaque(false);
		layout2.setConstraints(m_jchGewinnVerlust, constraints2);
		panel2.add(m_jchGewinnVerlust);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 7;
		m_jchGesamteinnahmen = new ImageCheckbox(getLangStr("Gesamteinnahmen"),
				ThemeManager.getColor(HOColorName.STAT_INCOMESUM),
				UserParameter.instance().statistikGesamtEinnahmen);
		m_jchGesamteinnahmen.setOpaque(false);
		layout2.setConstraints(m_jchGesamteinnahmen, constraints2);
		panel2.add(m_jchGesamteinnahmen);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 7;
		m_jchGesamtausgaben = new ImageCheckbox(getLangStr("Gesamtausgaben"),
				ThemeManager.getColor(HOColorName.STAT_COSTSUM),
				UserParameter.instance().statistikGesamtAusgaben);
		m_jchGesamtausgaben.setOpaque(false);
		layout2.setConstraints(m_jchGesamtausgaben, constraints2);
		panel2.add(m_jchGesamtausgaben);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 8;
		m_jchZuschauer = new ImageCheckbox(getLangStr("Zuschauer"),
				ThemeManager.getColor(HOColorName.STAT_INCOMESPECTATORS),
				UserParameter.instance().statistikZuschauer);
		m_jchZuschauer.setOpaque(false);
		layout2.setConstraints(m_jchZuschauer, constraints2);
		panel2.add(m_jchZuschauer);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 9;
		m_jchSponsoren = new ImageCheckbox(getLangStr("Sponsoren"),
				ThemeManager.getColor(HOColorName.STAT_INCOMESPONSORS),
				UserParameter.instance().statistikSponsoren);
		m_jchStadion = new ImageCheckbox(getLangStr("Stadion"),
				ThemeManager.getColor(HOColorName.STAT_COSTARENA),
				UserParameter.instance().statistikStadion);
		m_jchSponsoren.setOpaque(false);
		layout2.setConstraints(m_jchSponsoren, constraints2);
		panel2.add(m_jchSponsoren);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 10;
		m_jchSonstigeEinnahmen = new ImageCheckbox(getLangStr("Sonstiges"),
				ThemeManager.getColor(HOColorName.STAT_INCOMETEMPORARY),
				UserParameter.instance().statistikSonstigeEinnahmen);
		m_jchSonstigeEinnahmen.setOpaque(false);
		layout2.setConstraints(m_jchSonstigeEinnahmen, constraints2);
		panel2.add(m_jchSonstigeEinnahmen);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 8;
		m_jchStadion.setOpaque(false);
		layout2.setConstraints(m_jchStadion, constraints2);
		panel2.add(m_jchStadion);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 9;
		m_jchSpielergehaelter = new ImageCheckbox(getLangStr("Spielergehaelter"),
				ThemeManager.getColor(HOColorName.STAT_COSTSPLAYERS),
				UserParameter.instance().statistikSpielergehaelter);
		m_jchSpielergehaelter.setOpaque(false);
		layout2.setConstraints(m_jchSpielergehaelter, constraints2);
		panel2.add(m_jchSpielergehaelter);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 10;
		m_jchSonstigeAusgaben = new ImageCheckbox(getLangStr("Sonstiges"),
				ThemeManager.getColor(HOColorName.STAT_COSTTEMPORARY),
				UserParameter.instance().statistikSonstigeAusgaben);
		m_jchSonstigeAusgaben.setOpaque(false);
		layout2.setConstraints(m_jchSonstigeAusgaben, constraints2);
		panel2.add(m_jchSonstigeAusgaben);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 12;
		m_jchTrainerstab = new ImageCheckbox(getLangStr("Trainerstab"),
				ThemeManager.getColor(HOColorName.STAT_COSTSTAFF),
				UserParameter.instance().statistikTrainerstab);
		m_jchTrainerstab.setOpaque(false);
		layout2.setConstraints(m_jchTrainerstab, constraints2);
		panel2.add(m_jchTrainerstab);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 13;
		m_jchJugend = new ImageCheckbox(getLangStr("Jugend"),
				ThemeManager.getColor(HOColorName.STAT_COSTSYOUTH),
				UserParameter.instance().statistikJugend);
		m_jchJugend.setOpaque(false);
		layout2.setConstraints(m_jchJugend, constraints2);
		panel2.add(m_jchJugend);

		constraints2.gridwidth = 1;
		constraints2.gridx = 1;
		constraints2.gridy = 14;
		m_jchZinsaufwendungen = new ImageCheckbox(getLangStr("Zinsaufwendungen"),
				ThemeManager.getColor(HOColorName.STAT_COSTFINANCIAL),
				UserParameter.instance().statistikZinsaufwendungen);
		m_jchZinsaufwendungen.setOpaque(false);
		layout2.setConstraints(m_jchZinsaufwendungen, constraints2);
		panel2.add(m_jchZinsaufwendungen);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 12;
		m_jchMarktwert = new ImageCheckbox(getLangStr("TotalTSI"),
				ThemeManager.getColor(HOColorName.STAT_MARKETVALUE),
				UserParameter.instance().statistikMarktwert);
		m_jchMarktwert.setOpaque(false);
		layout2.setConstraints(m_jchMarktwert, constraints2);
		panel2.add(m_jchMarktwert);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 13;
		m_jchFans = new ImageCheckbox(getLangStr("Fans"),
				ThemeManager.getColor(HOColorName.STAT_FANS),
				UserParameter.instance().statistikFananzahl);
		m_jchFans.setOpaque(false);
		layout2.setConstraints(m_jchFans, constraints2);
		panel2.add(m_jchFans);

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

		m_clStatistikPanel = new StatistikPanel(true);
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

			UserParameter.instance().statistikFinanzenAnzahlHRF = anzahlHRF;

			NumberFormat format = NumberFormat.getCurrencyInstance();
			NumberFormat format2 = NumberFormat.getInstance();

			double[][] statistikWerte = DBManager.instance().getFinanzen4Statistik(anzahlHRF);
			StatistikModel[] models = null;
			models = new StatistikModel[15];

			if (statistikWerte.length > 0) {
				models[0] = new StatistikModel(statistikWerte[0], "Kontostand",
						m_jchKontostand.isSelected(), ThemeManager.getColor(HOColorName.STAT_CASH),
						format);
				models[1] = new StatistikModel(statistikWerte[1], "GewinnVerlust",
						m_jchGewinnVerlust.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_WINLOST), format);
				models[2] = new StatistikModel(statistikWerte[2], "Gesamteinnahmen",
						m_jchGesamteinnahmen.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_INCOMESUM), format);
				models[3] = new StatistikModel(statistikWerte[3], "Gesamtausgaben",
						m_jchGesamtausgaben.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_COSTSUM), format);
				models[4] = new StatistikModel(statistikWerte[4], "Zuschauer",
						m_jchZuschauer.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_INCOMESPECTATORS), format);
				models[5] = new StatistikModel(statistikWerte[5], "Sponsoren",
						m_jchSponsoren.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_INCOMESPONSORS), format);
				models[6] = new StatistikModel(statistikWerte[7], "SonstigeEinnahmen",
						m_jchSonstigeEinnahmen.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_INCOMETEMPORARY), format);
				models[7] = new StatistikModel(statistikWerte[8], "Stadion",
						m_jchStadion.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_COSTARENA), format);
				models[8] = new StatistikModel(statistikWerte[9], "Spielergehaelter",
						m_jchSpielergehaelter.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_COSTSPLAYERS), format);
				models[9] = new StatistikModel(statistikWerte[11], "SonstigeAusgaben",
						m_jchSonstigeAusgaben.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_COSTTEMPORARY), format);
				models[10] = new StatistikModel(statistikWerte[12], "Trainerstab",
						m_jchTrainerstab.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_COSTSTAFF), format);
				models[11] = new StatistikModel(statistikWerte[13], "Jugend",
						m_jchJugend.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_COSTSYOUTH), format);
				models[12] = new StatistikModel(statistikWerte[14], "Fans", m_jchFans.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_FANS), format2, 100);
				models[13] = new StatistikModel(statistikWerte[15], "Marktwert",
						m_jchMarktwert.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_MARKETVALUE), format2, 10);
				models[14] = new StatistikModel(statistikWerte[10], "Zinsaufwendungen",
						m_jchZinsaufwendungen.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_COSTFINANCIAL), format);
			}

			String[] yBezeichnungen = core.util.Helper
					.convertTimeMillisToFormatString(statistikWerte[16]);

			m_clStatistikPanel.setAllValues(models, yBezeichnungen, format, HOVerwaltung.instance()
					.getLanguageString("Wochen"), "", m_jchBeschriftung.isSelected(),
					m_jchHilflinien.isSelected());
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}

	private String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}
}
