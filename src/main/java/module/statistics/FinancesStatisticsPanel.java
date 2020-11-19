package module.statistics;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.util.chart.GraphDataModel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.HOLogger;
import core.util.Helper;
import core.util.chart.LinesChart;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
 * Panel Finances in Module Statistics
 */
public class FinancesStatisticsPanel extends LazyImagePanel {

	private ImageCheckbox c_jcbMatchTakings;
	private ImageCheckbox c_jcbSponsors;

	private ImageCheckbox m_jchGesamtausgaben;
	private ImageCheckbox m_jchGesamteinnahmen;
	private ImageCheckbox m_jchGewinnVerlust;
	private ImageCheckbox m_jchJugend;
	private ImageCheckbox m_jchZinsaufwendungen;
	private ImageCheckbox m_jchKontostand;
	private ImageCheckbox m_jchSonstigeAusgaben;
	private ImageCheckbox m_jchSonstigeEinnahmen;
	private ImageCheckbox m_jchSpielergehaelter;

	private ImageCheckbox m_jchStadion;
	private ImageCheckbox m_jchTrainerstab;

	private JButton m_jbUbernehmen;
	private JCheckBox m_jchHilflinien;
	private JTextField m_jtfAnzahlHRF;
	private LinesChart c_jpChart;

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
		ActionListener checkBoxActionListener = e -> {
			if (e.getSource() == m_jchHilflinien) {
				c_jpChart.setHelpLines(m_jchHilflinien.isSelected());
				UserParameter.instance().statistikFinanzenHilfslinien = m_jchHilflinien
						.isSelected();
			} else if (e.getSource() == m_jchBeschriftung) {
				c_jpChart.setLabelling(m_jchBeschriftung.isSelected());
				UserParameter.instance().statistikFinanzenBeschriftung = m_jchBeschriftung
						.isSelected();
			} else if (e.getSource() == m_jchKontostand.getCheckbox()) {
				c_jpChart.setShow("Kontostand", m_jchKontostand.isSelected());
				UserParameter.instance().statistikKontostand = m_jchKontostand.isSelected();
			} else if (e.getSource() == m_jchGewinnVerlust.getCheckbox()) {
				c_jpChart.setShow("GewinnVerlust", m_jchGewinnVerlust.isSelected());
				UserParameter.instance().statistikGewinnVerlust = m_jchGewinnVerlust
						.isSelected();
			} else if (e.getSource() == m_jchGesamteinnahmen.getCheckbox()) {
				c_jpChart
						.setShow("Gesamteinnahmen", m_jchGesamteinnahmen.isSelected());
				UserParameter.instance().statistikGesamtEinnahmen = m_jchGesamteinnahmen
						.isSelected();
			} else if (e.getSource() == m_jchGesamtausgaben.getCheckbox()) {
				c_jpChart.setShow("Gesamtausgaben", m_jchGesamtausgaben.isSelected());
				UserParameter.instance().statistikGesamtAusgaben = m_jchGesamtausgaben
						.isSelected();
			} else if (e.getSource() == c_jcbMatchTakings.getCheckbox()) {
				c_jpChart.setShow("MatchTakings", c_jcbMatchTakings.isSelected());
				UserParameter.instance().statistikMatchTakings = c_jcbMatchTakings.isSelected();
			} else if (e.getSource() == c_jcbSponsors.getCheckbox()) {
				c_jpChart.setShow("Sponsoren", c_jcbSponsors.isSelected());
				UserParameter.instance().statistikSponsoren = c_jcbSponsors.isSelected();
			} else if (e.getSource() == m_jchSonstigeEinnahmen.getCheckbox()) {
				c_jpChart.setShow("SonstigeEinnahmen",
						m_jchSonstigeEinnahmen.isSelected());
				UserParameter.instance().statistikSonstigeEinnahmen = m_jchSonstigeEinnahmen
						.isSelected();
			} else if (e.getSource() == m_jchStadion.getCheckbox()) {
				c_jpChart.setShow("Stadion", m_jchStadion.isSelected());
				UserParameter.instance().statistikStadion = m_jchStadion.isSelected();
			} else if (e.getSource() == m_jchSpielergehaelter.getCheckbox()) {
				c_jpChart.setShow("Spielergehaelter",
						m_jchSpielergehaelter.isSelected());
				UserParameter.instance().statistikSpielergehaelter = m_jchSpielergehaelter
						.isSelected();
			} else if (e.getSource() == m_jchSonstigeAusgaben.getCheckbox()) {
				c_jpChart.setShow("SonstigeAusgaben",
						m_jchSonstigeAusgaben.isSelected());
				UserParameter.instance().statistikSonstigeAusgaben = m_jchSonstigeAusgaben
						.isSelected();
			} else if (e.getSource() == m_jchTrainerstab.getCheckbox()) {
				c_jpChart.setShow("Trainerstab", m_jchTrainerstab.isSelected());
				UserParameter.instance().statistikTrainerstab = m_jchTrainerstab.isSelected();
			} else if (e.getSource() == m_jchJugend.getCheckbox()) {
				c_jpChart.setShow("Jugend", m_jchJugend.isSelected());
				UserParameter.instance().statistikJugend = m_jchJugend.isSelected();
			} else if (e.getSource() == m_jchZinsaufwendungen.getCheckbox()) {
				c_jpChart.setShow("Zinsaufwendungen", m_jchZinsaufwendungen.isSelected());
				UserParameter.instance().statistikZinsaufwendungen = m_jchZinsaufwendungen.isSelected();
			} else if (e.getSource() == m_jchMarktwert.getCheckbox()) {
				c_jpChart.setShow("Marktwert", m_jchMarktwert.isSelected());
				UserParameter.instance().statistikMarktwert = m_jchMarktwert.isSelected();
			} else if (e.getSource() == m_jchFans.getCheckbox()) {
				c_jpChart.setShow("Fans", m_jchFans.isSelected());
				UserParameter.instance().statistikFananzahl = m_jchFans.isSelected();
			}
		};
		m_jchHilflinien.addActionListener(checkBoxActionListener);
		m_jchBeschriftung.addActionListener(checkBoxActionListener);
		m_jchKontostand.addActionListener(checkBoxActionListener);
		m_jchGewinnVerlust.addActionListener(checkBoxActionListener);
		m_jchGesamteinnahmen.addActionListener(checkBoxActionListener);
		m_jchGesamtausgaben.addActionListener(checkBoxActionListener);
		c_jcbMatchTakings.addActionListener(checkBoxActionListener);
		c_jcbSponsors.addActionListener(checkBoxActionListener);
		m_jchSonstigeEinnahmen.addActionListener(checkBoxActionListener);
		m_jchStadion.addActionListener(checkBoxActionListener);
		m_jchSpielergehaelter.addActionListener(checkBoxActionListener);
		m_jchSonstigeAusgaben.addActionListener(checkBoxActionListener);
		m_jchTrainerstab.addActionListener(checkBoxActionListener);
		m_jchJugend.addActionListener(checkBoxActionListener);
		m_jchZinsaufwendungen.addActionListener(checkBoxActionListener);
		m_jchMarktwert.addActionListener(checkBoxActionListener);
		m_jchFans.addActionListener(checkBoxActionListener);

		m_jbUbernehmen.addActionListener(e -> initStatistik());

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
		c_jcbMatchTakings = new ImageCheckbox(getLangStr("ls.module.statistics.finance.match.income"),
				ThemeManager.getColor(HOColorName.STAT_INCOMESPECTATORS),
				UserParameter.instance().statistikZuschauer);
		c_jcbMatchTakings.setOpaque(false);
		layout2.setConstraints(c_jcbMatchTakings, constraints2);
		panel2.add(c_jcbMatchTakings);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 9;
		c_jcbSponsors = new ImageCheckbox(getLangStr("Sponsoren"),
				ThemeManager.getColor(HOColorName.STAT_INCOMESPONSORS),
				UserParameter.instance().statistikSponsoren);
		m_jchStadion = new ImageCheckbox(getLangStr("Stadion"),
				ThemeManager.getColor(HOColorName.STAT_COSTARENA),
				UserParameter.instance().statistikStadion);
		c_jcbSponsors.setOpaque(false);
		layout2.setConstraints(c_jcbSponsors, constraints2);
		panel2.add(c_jcbSponsors);

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

		c_jpChart = new StatistikPanel(true);
		panel.add(c_jpChart);

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

			double[][] data = DBManager.instance().getFinanzen4Statistik(anzahlHRF);
			GraphDataModel[] models;
			models = new GraphDataModel[15];

			if (data.length > 0) {
				models[0] = new GraphDataModel(data[0], "Kontostand",
						m_jchKontostand.isSelected(), ThemeManager.getColor(HOColorName.STAT_CASH),
						format);
				models[1] = new GraphDataModel(data[1], "GewinnVerlust",
						m_jchGewinnVerlust.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_WINLOST), format);
				models[2] = new GraphDataModel(data[2], "Gesamteinnahmen",
						m_jchGesamteinnahmen.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_INCOMESUM), format);
				models[3] = new GraphDataModel(data[3], "Gesamtausgaben",
						m_jchGesamtausgaben.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_COSTSUM), format);
				models[4] = new GraphDataModel(data[4], "MatchTakings",
						c_jcbMatchTakings.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_INCOMESPECTATORS), format);
				models[5] = new GraphDataModel(data[5], "Sponsoren",
						c_jcbSponsors.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_INCOMESPONSORS), format);
				models[6] = new GraphDataModel(data[7], "SonstigeEinnahmen",
						m_jchSonstigeEinnahmen.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_INCOMETEMPORARY), format);
				models[7] = new GraphDataModel(data[8], "Stadion",
						m_jchStadion.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_COSTARENA), format);
				models[8] = new GraphDataModel(data[9], "Spielergehaelter",
						m_jchSpielergehaelter.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_COSTSPLAYERS), format);
				models[9] = new GraphDataModel(data[11], "SonstigeAusgaben",
						m_jchSonstigeAusgaben.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_COSTTEMPORARY), format);
				models[10] = new GraphDataModel(data[12], "Trainerstab",
						m_jchTrainerstab.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_COSTSTAFF), format);
				models[11] = new GraphDataModel(data[13], "Jugend",
						m_jchJugend.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_COSTSYOUTH), format);
				models[12] = new GraphDataModel(data[10], "Zinsaufwendungen",
						m_jchZinsaufwendungen.isSelected(),
						ThemeManager.getColor(HOColorName.STAT_COSTFINANCIAL), format);
			}

			String[] yBezeichnungen = core.util.Helper
					.convertTimeMillisToFormatString(data[16]);

			c_jpChart.setAllValues(models, yBezeichnungen, format, HOVerwaltung.instance()
					.getLanguageString("Wochen"), "",false,
					m_jchHilflinien.isSelected());
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}

	private String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}
}
