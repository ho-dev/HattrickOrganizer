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

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.ItemEvent;
import java.text.NumberFormat;

import javax.swing.*;

/**
 * Panel Finances in Module Statistics
 */
public class FinancesStatisticsPanel extends LazyImagePanel {

//	private ImageCheckbox c_jcbMatchTakings;
//	private ImageCheckbox c_jcbSponsors;
//	private ImageCheckbox c_jcbPlayerSales;
//	private ImageCheckbox c_jcbCommission;
//	private ImageCheckbox c_jcbOther;
//	private ImageCheckbox c_jcbStadiumMaintenance;
//	private ImageCheckbox c_jcbNewSigning;
//	private ImageCheckbox c_jcbWages;
//	private ImageCheckbox c_jcbStaff;
//	private ImageCheckbox c_jcbOther;
	private JComboBox<String> c_jcomboChartType;
	private int iChartType;
	private JTextField c_jtfNumberOfHRF;
	private JButton c_jbFetch;
	private JCheckBox jcbHelpLines;
	private JPanel chartPanel;
	private JPanel c_jpCharts;
	final static String BALANCE_CHART_PANEL = "Balance Chart";
	final static String DEVELOPMENT_CHART_PANEL = "Development Chart";
	final static String REVENUE_AND_EXPENSES_CHART_PANEL = "P&L Chart";
	private LinesChart c_jpBalanceChart;
	private LinesChart c_jpDevelopmentChart;
	private LinesChart c_jpRevenueAndExpensesChart; //TODO make a new kind of graph (camembert)

	// TODO: start with Development GRAPH which is the closer of what I got now

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

		UserParameter userParameter = UserParameter.instance();

		c_jcomboChartType.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				iChartType = c_jcomboChartType.getSelectedIndex();
				userParameter.statisticsFinanceChartType = iChartType;
				CardLayout cl = (CardLayout)(c_jpCharts.getLayout());
				cl.show(c_jpCharts, getChartCode(iChartType));
				iChartType = c_jcomboChartType.getSelectedIndex();
			}
		});

		ActionListener checkBoxActionListener = e -> {
			if (e.getSource() == jcbHelpLines) {
				c_jpBalanceChart.setHelpLines(jcbHelpLines.isSelected());
				c_jpDevelopmentChart.setHelpLines(jcbHelpLines.isSelected());
				userParameter.statistikFinanzenHilfslinien = jcbHelpLines.isSelected();
			}
//			else if (e.getSource() == c_jcbOther.getCheckbox()) {
//				c_jpBalanceChart.setShow("GewinnVerlust", c_jcbOther.isSelected());
//				UserParameter.instance().statistikGewinnVerlust = c_jcbOther
//						.isSelected();
//			} else if (e.getSource() == c_jcbCommission.getCheckbox()) {
//				c_jpBalanceChart
//						.setShow("Gesamteinnahmen", c_jcbCommission.isSelected());
//				UserParameter.instance().statistikGesamtEinnahmen = c_jcbCommission
//						.isSelected();
//			} else if (e.getSource() == c_jcbPlayerSales.getCheckbox()) {
//				c_jpBalanceChart.setShow("Gesamtausgaben", c_jcbPlayerSales.isSelected());
//				UserParameter.instance().statistikGesamtAusgaben = c_jcbPlayerSales
//						.isSelected();
//			} else if (e.getSource() == c_jcbMatchTakings.getCheckbox()) {
//				c_jpBalanceChart.setShow("MatchTakings", c_jcbMatchTakings.isSelected());
//				UserParameter.instance().statistikMatchTakings = c_jcbMatchTakings.isSelected();
//			} else if (e.getSource() == c_jcbSponsors.getCheckbox()) {
//				c_jpBalanceChart.setShow("Sponsoren", c_jcbSponsors.isSelected());
//				UserParameter.instance().statistikSponsoren = c_jcbSponsors.isSelected();
//			} else if (e.getSource() == c_jcbOther.getCheckbox()) {
//				c_jpBalanceChart.setShow("SonstigeEinnahmen",
//						c_jcbOther.isSelected());
//				UserParameter.instance().statistikSonstigeEinnahmen = c_jcbOther
//						.isSelected();
//			} else if (e.getSource() == m_jchStadion.getCheckbox()) {
//				c_jpBalanceChart.setShow("Stadion", m_jchStadion.isSelected());
//				UserParameter.instance().statistikStadion = m_jchStadion.isSelected();
//			} else if (e.getSource() == m_jchSpielergehaelter.getCheckbox()) {
//				c_jpBalanceChart.setShow("Spielergehaelter",
//						m_jchSpielergehaelter.isSelected());
//				UserParameter.instance().statistikSpielergehaelter = m_jchSpielergehaelter
//						.isSelected();
//			} else if (e.getSource() == c_jcbStaff.getCheckbox()) {
//				c_jpBalanceChart.setShow("SonstigeAusgaben",
//						c_jcbStaff.isSelected());
//				UserParameter.instance().statistikSonstigeAusgaben = c_jcbStaff
//						.isSelected();
//			} else if (e.getSource() == m_jchTrainerstab.getCheckbox()) {
//				c_jpBalanceChart.setShow("Trainerstab", m_jchTrainerstab.isSelected());
//				UserParameter.instance().statistikTrainerstab = m_jchTrainerstab.isSelected();
//			} else if (e.getSource() == c_jcbStadiumMaintenance.getCheckbox()) {
//				c_jpBalanceChart.setShow("Jugend", c_jcbStadiumMaintenance.isSelected());
//				UserParameter.instance().statistikJugend = c_jcbStadiumMaintenance.isSelected();
//			} else if (e.getSource() == c_jcbNewSigning.getCheckbox()) {
//				c_jpBalanceChart.setShow("Zinsaufwendungen", c_jcbNewSigning.isSelected());
//				UserParameter.instance().statistikZinsaufwendungen = c_jcbNewSigning.isSelected();
//			} else if (e.getSource() == m_jchMarktwert.getCheckbox()) {
//				c_jpBalanceChart.setShow("Marktwert", m_jchMarktwert.isSelected());
//				UserParameter.instance().statistikMarktwert = m_jchMarktwert.isSelected();
//			} else if (e.getSource() == m_jchFans.getCheckbox()) {
//				c_jpBalanceChart.setShow("Fans", m_jchFans.isSelected());
//				UserParameter.instance().statistikFananzahl = m_jchFans.isSelected();
//			}
		};
		jcbHelpLines.addActionListener(checkBoxActionListener);
//		m_jchBeschriftung.addActionListener(checkBoxActionListener);
//		c_jcbWages.addActionListener(checkBoxActionListener);
//		c_jcbOther.addActionListener(checkBoxActionListener);
//		c_jcbCommission.addActionListener(checkBoxActionListener);
//		c_jcbPlayerSales.addActionListener(checkBoxActionListener);
//		c_jcbMatchTakings.addActionListener(checkBoxActionListener);
//		c_jcbSponsors.addActionListener(checkBoxActionListener);
//		c_jcbOther.addActionListener(checkBoxActionListener);
//		m_jchStadion.addActionListener(checkBoxActionListener);
//		m_jchSpielergehaelter.addActionListener(checkBoxActionListener);
//		c_jcbStaff.addActionListener(checkBoxActionListener);
//		m_jchTrainerstab.addActionListener(checkBoxActionListener);
//		c_jcbStadiumMaintenance.addActionListener(checkBoxActionListener);
//		c_jcbNewSigning.addActionListener(checkBoxActionListener);
//		m_jchMarktwert.addActionListener(checkBoxActionListener);
//		m_jchFans.addActionListener(checkBoxActionListener);

		c_jbFetch.addActionListener(e -> initStatistik());

		c_jtfNumberOfHRF.addFocusListener(new FocusAdapter() {
			@Override
			public final void focusLost(java.awt.event.FocusEvent focusEvent) {
				Helper.parseInt(HOMainFrame.instance(), c_jtfNumberOfHRF, false);
			}
		});
	}

	private void initComponents() {
		UserParameter gup = UserParameter.instance();

		JLabel labelWeeks, labelChartType;

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

		labelWeeks = new JLabel(getLangStr("Wochen"));
		constraints2.fill = GridBagConstraints.HORIZONTAL;
		constraints2.anchor = GridBagConstraints.WEST;
		constraints2.gridx = 0;
		constraints2.gridy = 1;
		constraints2.gridwidth = 1;
		layout2.setConstraints(labelWeeks, constraints2);
		panel2.add(labelWeeks);

		c_jtfNumberOfHRF = new JTextField(String.valueOf(UserParameter.instance().statistikFinanzenAnzahlHRF));
		c_jtfNumberOfHRF.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints2.gridx = 1;
		constraints2.gridy = 1;
		layout2.setConstraints(c_jtfNumberOfHRF, constraints2);
		panel2.add(c_jtfNumberOfHRF);

		constraints2.gridx = 0;
		constraints2.gridy = 4;
		constraints2.gridwidth = 2;
		c_jbFetch = new JButton(getLangStr("ls.button.apply"));
		c_jbFetch.setToolTipText(getLangStr("tt_Statistik_HRFAnzahluebernehmen"));
		layout2.setConstraints(c_jbFetch, constraints2);
		panel2.add(c_jbFetch);

		constraints2.gridwidth = 1;
		constraints2.gridx = 0;
		constraints2.gridy = 5;
		jcbHelpLines = new JCheckBox(getLangStr("Hilflinien"),
				UserParameter.instance().statistikFinanzenHilfslinien);
		jcbHelpLines.setOpaque(false);
		layout2.setConstraints(jcbHelpLines, constraints2);
		panel2.add(jcbHelpLines);

		// Label Chart Type
		labelChartType = new JLabel(getLangStr("ls.module.statistic.finance.chart_type"));
		constraints2.fill = GridBagConstraints.HORIZONTAL;
		constraints2.anchor = GridBagConstraints.WEST;
		constraints2.gridx = 0;
		constraints2.gridy = 6;
		layout2.setConstraints(labelChartType, constraints2);
		panel2.add(labelChartType);

		// ComboBox Chart Type
		String[] sChartType = { getLangStr("ls.module.statistics.finance.chart_type.development"),
				getLangStr("ls.module.statistics.finance.chart_type.balance"),
				getLangStr("ls.module.statistics.finance.chart_type.p&l")};

		iChartType = gup.statisticsFinanceChartType;
		c_jcomboChartType = new JComboBox<>(sChartType);
		c_jcomboChartType.setSelectedIndex(iChartType);
		layout2.setConstraints(c_jcomboChartType, constraints2);
		c_jcomboChartType.setToolTipText(getLangStr("ls.module.statistic.finance.choose_chart_type"));
		constraints2.gridx = 1;
		constraints2.gridy = 6;
		panel2.add(c_jcomboChartType, constraints2);

//		constraints2.gridwidth = 1;
//		constraints2.gridx = 1;
//		constraints2.gridy = 5;
//		c_jcbWages = new ImageCheckbox(getLangStr("Kontostand"),
//				ThemeManager.getColor(HOColorName.STAT_CASH),
//				UserParameter.instance().statistikKontostand);
//		c_jcbWages.setOpaque(false);
//		layout2.setConstraints(c_jcbWages, constraints2);
//		panel2.add(c_jcbWages);
//
//		constraints2.gridwidth = 1;
//		constraints2.gridx = 1;
//		constraints2.gridy = 6;
//		c_jcbOther = new ImageCheckbox(getLangStr("GewinnVerlust"),
//				ThemeManager.getColor(HOColorName.STAT_WINLOST),
//				UserParameter.instance().statistikGewinnVerlust);
//		c_jcbOther.setOpaque(false);
//		layout2.setConstraints(c_jcbOther, constraints2);
//		panel2.add(c_jcbOther);
//
//		constraints2.gridwidth = 1;
//		constraints2.gridx = 0;
//		constraints2.gridy = 7;
//		c_jcbCommission = new ImageCheckbox(getLangStr("Gesamteinnahmen"),
//				ThemeManager.getColor(HOColorName.STAT_INCOMESUM),
//				UserParameter.instance().statistikGesamtEinnahmen);
//		c_jcbCommission.setOpaque(false);
//		layout2.setConstraints(c_jcbCommission, constraints2);
//		panel2.add(c_jcbCommission);
//
//		constraints2.gridwidth = 1;
//		constraints2.gridx = 1;
//		constraints2.gridy = 7;
//		c_jcbPlayerSales = new ImageCheckbox(getLangStr("Gesamtausgaben"),
//				ThemeManager.getColor(HOColorName.STAT_COSTSUM),
//				UserParameter.instance().statistikGesamtAusgaben);
//		c_jcbPlayerSales.setOpaque(false);
//		layout2.setConstraints(c_jcbPlayerSales, constraints2);
//		panel2.add(c_jcbPlayerSales);
//
//		constraints2.gridwidth = 1;
//		constraints2.gridx = 0;
//		constraints2.gridy = 8;
//		c_jcbMatchTakings = new ImageCheckbox(getLangStr("ls.module.statistics.finance.match.income"),
//				ThemeManager.getColor(HOColorName.STAT_INCOMESPECTATORS),
//				UserParameter.instance().statistikZuschauer);
//		c_jcbMatchTakings.setOpaque(false);
//		layout2.setConstraints(c_jcbMatchTakings, constraints2);
//		panel2.add(c_jcbMatchTakings);
//
//		constraints2.gridwidth = 1;
//		constraints2.gridx = 0;
//		constraints2.gridy = 9;
//		c_jcbSponsors = new ImageCheckbox(getLangStr("Sponsoren"),
//				ThemeManager.getColor(HOColorName.STAT_INCOMESPONSORS),
//				UserParameter.instance().statistikSponsoren);
//		m_jchStadion = new ImageCheckbox(getLangStr("Stadion"),
//				ThemeManager.getColor(HOColorName.STAT_COSTARENA),
//				UserParameter.instance().statistikStadion);
//		c_jcbSponsors.setOpaque(false);
//		layout2.setConstraints(c_jcbSponsors, constraints2);
//		panel2.add(c_jcbSponsors);
//
//		constraints2.gridwidth = 1;
//		constraints2.gridx = 0;
//		constraints2.gridy = 10;
//		c_jcbOther = new ImageCheckbox(getLangStr("Sonstiges"),
//				ThemeManager.getColor(HOColorName.STAT_INCOMETEMPORARY),
//				UserParameter.instance().statistikSonstigeEinnahmen);
//		c_jcbOther.setOpaque(false);
//		layout2.setConstraints(c_jcbOther, constraints2);
//		panel2.add(c_jcbOther);
//
//		constraints2.gridwidth = 1;
//		constraints2.gridx = 1;
//		constraints2.gridy = 8;
//		m_jchStadion.setOpaque(false);
//		layout2.setConstraints(m_jchStadion, constraints2);
//		panel2.add(m_jchStadion);
//
//		constraints2.gridwidth = 1;
//		constraints2.gridx = 1;
//		constraints2.gridy = 9;
//		m_jchSpielergehaelter = new ImageCheckbox(getLangStr("Spielergehaelter"),
//				ThemeManager.getColor(HOColorName.STAT_COSTSPLAYERS),
//				UserParameter.instance().statistikSpielergehaelter);
//		m_jchSpielergehaelter.setOpaque(false);
//		layout2.setConstraints(m_jchSpielergehaelter, constraints2);
//		panel2.add(m_jchSpielergehaelter);
//
//		constraints2.gridwidth = 1;
//		constraints2.gridx = 1;
//		constraints2.gridy = 10;
//		c_jcbStaff = new ImageCheckbox(getLangStr("Sonstiges"),
//				ThemeManager.getColor(HOColorName.STAT_COSTTEMPORARY),
//				UserParameter.instance().statistikSonstigeAusgaben);
//		c_jcbStaff.setOpaque(false);
//		layout2.setConstraints(c_jcbStaff, constraints2);
//		panel2.add(c_jcbStaff);
//
//		constraints2.gridwidth = 1;
//		constraints2.gridx = 1;
//		constraints2.gridy = 12;
//		m_jchTrainerstab = new ImageCheckbox(getLangStr("Trainerstab"),
//				ThemeManager.getColor(HOColorName.STAT_COSTSTAFF),
//				UserParameter.instance().statistikTrainerstab);
//		m_jchTrainerstab.setOpaque(false);
//		layout2.setConstraints(m_jchTrainerstab, constraints2);
//		panel2.add(m_jchTrainerstab);
//
//		constraints2.gridwidth = 1;
//		constraints2.gridx = 1;
//		constraints2.gridy = 13;
//		c_jcbStadiumMaintenance = new ImageCheckbox(getLangStr("Jugend"),
//				ThemeManager.getColor(HOColorName.STAT_COSTSYOUTH),
//				UserParameter.instance().statistikJugend);
//		c_jcbStadiumMaintenance.setOpaque(false);
//		layout2.setConstraints(c_jcbStadiumMaintenance, constraints2);
//		panel2.add(c_jcbStadiumMaintenance);
//
//		constraints2.gridwidth = 1;
//		constraints2.gridx = 1;
//		constraints2.gridy = 14;
//		c_jcbNewSigning = new ImageCheckbox(getLangStr("Zinsaufwendungen"),
//				ThemeManager.getColor(HOColorName.STAT_COSTFINANCIAL),
//				UserParameter.instance().statistikZinsaufwendungen);
//		c_jcbNewSigning.setOpaque(false);
//		layout2.setConstraints(c_jcbNewSigning, constraints2);
//		panel2.add(c_jcbNewSigning);
//
//		constraints2.gridwidth = 1;
//		constraints2.gridx = 0;
//		constraints2.gridy = 12;
//		m_jchMarktwert = new ImageCheckbox(getLangStr("TotalTSI"),
//				ThemeManager.getColor(HOColorName.STAT_MARKETVALUE),
//				UserParameter.instance().statistikMarktwert);
//		m_jchMarktwert.setOpaque(false);
//		layout2.setConstraints(m_jchMarktwert, constraints2);
//		panel2.add(m_jchMarktwert);
//
//		constraints2.gridwidth = 1;
//		constraints2.gridx = 0;
//		constraints2.gridy = 13;
//		m_jchFans = new ImageCheckbox(getLangStr("Fans"),
//				ThemeManager.getColor(HOColorName.STAT_FANS),
//				UserParameter.instance().statistikFananzahl);
//		m_jchFans.setOpaque(false);
//		layout2.setConstraints(m_jchFans, constraints2);
//		panel2.add(m_jchFans);
//




		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.weightx = 0.01;
		constraints.weighty = 0.001;
		constraints.anchor = GridBagConstraints.NORTH;
		layout.setConstraints(panel2, constraints);
		add(panel2);

		var toto = Helper.getNumberFormat(true, 0).getCurrency().getSymbol();

		// initialize Development Chart
		c_jpDevelopmentChart = new LinesChart(true, null, null, "#,##0 " + toto, "#,##0 " + toto, true);
		c_jpBalanceChart = new LinesChart(true, null, null, null, "#,##0", true);
		c_jpRevenueAndExpensesChart = new LinesChart(true, null, null, null, "#,##0", true);

		//Create the panel that contains the "cards" each card being a different chart
		c_jpCharts = new JPanel(new CardLayout());
		c_jpCharts.add(c_jpDevelopmentChart.getPanel(), DEVELOPMENT_CHART_PANEL);
		c_jpCharts.add(c_jpBalanceChart.getPanel(), BALANCE_CHART_PANEL);
		c_jpCharts.add(c_jpRevenueAndExpensesChart.getPanel(), REVENUE_AND_EXPENSES_CHART_PANEL);

		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.weighty = 1.0;
		constraints.weightx = 1.0;
		constraints.anchor = GridBagConstraints.NORTH;
//		panel.setBorder(BorderFactory.createLineBorder(ThemeManager
//				.getColor(HOColorName.PANEL_BORDER)));
		layout.setConstraints(c_jpCharts, constraints);
		add(c_jpCharts);
	}

//	private void setChartPanel(){
//		chartPanel =  switch (this.iChartType) {
//			case 0 -> c_jpDevelopmentChart.getPanel();
//			case 1 -> c_jpBalanceChart.getPanel();
//			default -> c_jpRevenueAndExpensesChart.getPanel();
//		};
//		chartPanel.repaint();
//	}


	private void initStatistik() {
		try {
			int iNbHRF = Integer.parseInt(c_jtfNumberOfHRF.getText());
			if (iNbHRF <= 0) {
				iNbHRF = 1;
			}

			UserParameter.instance().statistikFinanzenAnzahlHRF = iNbHRF;

			NumberFormat format = NumberFormat.getCurrencyInstance();
			NumberFormat format2 = NumberFormat.getInstance();

			double[][] data = DBManager.instance().getDataForFinancesStatisticsPanel(iNbHRF);
			GraphDataModel[] modelsDevelopmentChart, modelsBalanceChart, modelsRevenueAndExpensesChart;

			modelsBalanceChart = new GraphDataModel[3];
			modelsRevenueAndExpensesChart = new GraphDataModel[3];
			modelsDevelopmentChart = new GraphDataModel[3];

			if (data.length > 0) {
				modelsDevelopmentChart[0] = new GraphDataModel(data[1], getLangStr("ls.finance.revenue.sponsors"),	true, getColor5(Colors.COLOR_FINANCE_INCOME_SPONSORS), null, 1E-3, false);
				modelsDevelopmentChart[1] = new GraphDataModel(data[2], getLangStr("ls.finance.expenses.wages"),	true, getColor5(Colors.COLOR_FINANCE_COST_PLAYERS), null, 1E-3, false);
				modelsDevelopmentChart[2] = new GraphDataModel(data[0], getLangStr("ls.finance.cash") + " (" + getLangStr("ls.chart.second_axis") + ")", true, getColor5(Colors.COLOR_FINANCE_CASH), null, 1E-6, true);
			}


			c_jpDevelopmentChart.setAllValues(modelsDevelopmentChart, data[14], format, getLangStr("Wochen"), "",false,
					jcbHelpLines.isSelected());

			c_jpBalanceChart.setAllValues(modelsBalanceChart, data[14], format, getLangStr("Wochen"), "",false,
					jcbHelpLines.isSelected());

			c_jpRevenueAndExpensesChart.setAllValues(modelsRevenueAndExpensesChart, data[14], format, getLangStr("Wochen"), "",false,
					jcbHelpLines.isSelected());

		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}

	private String getLangStr(String key) {return HOVerwaltung.instance().getLanguageString(key);}

	private Color getColor5(int i) {return ThemeManager.getColor(HOColorName.PALETTE5[i]);}

	private String getChartCode(int i) {

		return switch (i) {
			case 0 -> DEVELOPMENT_CHART_PANEL;
			case 1 -> BALANCE_CHART_PANEL;
			default -> REVENUE_AND_EXPENSES_CHART_PANEL;
		};
	}

}
