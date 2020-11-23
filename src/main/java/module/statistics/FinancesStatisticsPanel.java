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

	//TODO: add chart name
	//TODO: ensure the graph and the chart combox are in sync when panel is launched
	//TODO: when line is hidden it should not appear in Legend either (Finance - Balance Graph)
	//TODO: for camemebert chart: have this week, last week and 2 weeks graph ?
	//TODO: add format y-axis (€ symbol)
	//TODO: add translation string

	private JComboBox<String> c_jcomboChartType;
	private int iChartType;
	private JTextField c_jtfNumberOfHRF;
	private JButton c_jbFetch;
	private JCheckBox jcbHelpLines;
	private JCheckBox c_jcbInclTransferts;
	private JPanel c_jpCharts;
	final static String BALANCE_CHART_PANEL = "Balance Chart";
	final static String DEVELOPMENT_CHART_PANEL = "Development Chart";
	final static String REVENUE_AND_EXPENSES_CHART_PANEL = "P&L Chart";
	private LinesChart c_jpBalanceChart;
	private LinesChart c_jpDevelopmentChart;
	private LinesChart c_jpRevenueAndExpensesChart; //TODO make a new kind of graph (camembert)
	private String[] balanceChartPlotsNames = new String[6];
	private boolean[] balanceChartPlotsVisible = new boolean[6];


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

		UserParameter gup = UserParameter.instance();

		c_jcomboChartType.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {


				iChartType = c_jcomboChartType.getSelectedIndex();

				if(iChartType == 1){
					c_jcbInclTransferts.setVisible(true);
				}
				else{
					c_jcbInclTransferts.setVisible(false);
				}

				gup.statisticsFinanceChartType = iChartType;
				CardLayout cl = (CardLayout)(c_jpCharts.getLayout());
				cl.show(c_jpCharts, getChartCode(iChartType));
				iChartType = c_jcomboChartType.getSelectedIndex();
			}
		});

		ActionListener checkBoxActionListener = e -> {
			if (e.getSource() == jcbHelpLines) {
				c_jpBalanceChart.setHelpLines(jcbHelpLines.isSelected());
				c_jpDevelopmentChart.setHelpLines(jcbHelpLines.isSelected());
				gup.statistikFinanzenHilfslinien = jcbHelpLines.isSelected();
			}
			else if (e.getSource() == c_jcbInclTransferts) {
				if(c_jcbInclTransferts.isSelected()) {
					balanceChartPlotsVisible = new boolean[] {false, false, false, true, true, true};
					c_jpBalanceChart.setMultipleShow(balanceChartPlotsNames, balanceChartPlotsVisible);
					gup.statistikFinanzenIncludeTransfers = true;
				}
				else{
					balanceChartPlotsVisible = new boolean[] {true, true, true, false, false, false};
					c_jpBalanceChart.setMultipleShow(balanceChartPlotsNames, balanceChartPlotsVisible);
					gup.statistikFinanzenIncludeTransfers = false;
				}
			}

		};
		jcbHelpLines.addActionListener(checkBoxActionListener);
		c_jcbInclTransferts.addActionListener(checkBoxActionListener);
		c_jbFetch.addActionListener(e -> initStatistik());

		c_jtfNumberOfHRF.addFocusListener(new FocusAdapter() {
			@Override
			public final void focusLost(java.awt.event.FocusEvent focusEvent) {
				Helper.parseInt(HOMainFrame.instance(), c_jtfNumberOfHRF, false);
			}
		});
	}

	private void initComponents() {

		JLabel labelWeeks, labelChartType;

		UserParameter gup = UserParameter.instance();

		iChartType = gup.statisticsFinanceChartType;

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

		c_jtfNumberOfHRF = new JTextField(String.valueOf(gup.statistikFinanzenAnzahlHRF));
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
		jcbHelpLines = new JCheckBox(getLangStr("Hilflinien"), gup.statistikFinanzenHilfslinien);
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

		c_jcomboChartType = new JComboBox<>(sChartType);
		c_jcomboChartType.setSelectedIndex(iChartType);
		layout2.setConstraints(c_jcomboChartType, constraints2);
		c_jcomboChartType.setToolTipText(getLangStr("ls.module.statistic.finance.choose_chart_type"));
		constraints2.gridx = 1;
		constraints2.gridy = 6;
		panel2.add(c_jcomboChartType, constraints2);



		c_jcbInclTransferts = new JCheckBox(getLangStr("ls.module.statistic.finance.l.include_transfer"), gup.statistikFinanzenIncludeTransfers);
		c_jcbInclTransferts.setToolTipText(getLangStr("ls.module.statistic.finance.tt.include_transfer"));
		if (iChartType == 1){
			c_jcbInclTransferts.setVisible(true);
		}
		else{
			c_jcbInclTransferts.setVisible(false);
		}

		constraints2.insets = new Insets(5, 0, 0, 0);
		constraints2.gridx = 0;
		constraints2.gridy = 7;
		panel2.add(c_jcbInclTransferts, constraints2);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.weightx = 0.01;
		constraints.weighty = 0.001;
		constraints.anchor = GridBagConstraints.NORTH;
		layout.setConstraints(panel2, constraints);
		add(panel2);


		// initialize Development Chart
		String currencySymbol = Helper.getNumberFormat(true, 0).getCurrency().getSymbol();
		c_jpDevelopmentChart = new LinesChart(true, null, null, "#,##0 " + currencySymbol, "#,##0 " + currencySymbol, true);
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
		layout.setConstraints(c_jpCharts, constraints);
		add(c_jpCharts);


		balanceChartPlotsNames[0] = getLangStr("IncomeSum") + " (" + getLangStr("ls.chart.second_axis") + ")";
		balanceChartPlotsNames[1] = getLangStr("CostsSum") + " (" + getLangStr("ls.chart.second_axis") + ")";
		balanceChartPlotsNames[2] = getLangStr("Balance");
		balanceChartPlotsNames[3] = getLangStr("IncomeSumWithoutTransfert") + " (" + getLangStr("ls.chart.second_axis") + ")";
		balanceChartPlotsNames[4] = getLangStr("CostsSumWithoutTransfert") + " (" + getLangStr("ls.chart.second_axis") + ")";
		balanceChartPlotsNames[5] = getLangStr("BalanceWithoutTransfert");

	}


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

			modelsBalanceChart = new GraphDataModel[6];
			modelsRevenueAndExpensesChart = new GraphDataModel[3];
			modelsDevelopmentChart = new GraphDataModel[3];

			if (data.length > 0) {
				modelsDevelopmentChart[0] = new GraphDataModel(data[1], getLangStr("ls.finance.revenue.sponsors"),	true, getColor5(Colors.COLOR_FINANCE_INCOME_SPONSORS), null, 1E-3, false);
				modelsDevelopmentChart[1] = new GraphDataModel(data[2], getLangStr("ls.finance.expenses.wages"),	true, getColor5(Colors.COLOR_FINANCE_COST_PLAYERS), null, 1E-3, false);
				modelsDevelopmentChart[2] = new GraphDataModel(data[0], getLangStr("ls.finance.cash") + " (" + getLangStr("ls.chart.second_axis") + ")", true, getColor5(Colors.COLOR_FINANCE_CASH), null, 1E-6, true);

				// TODO set show depending of gup.statistikFinanzenIncludeTransfers
				// TODO put correct color color5 for both
				modelsBalanceChart[0] = new GraphDataModel(data[3], balanceChartPlotsNames[0], true, getColor5(0), null, 1E-6, true);
				modelsBalanceChart[1] = new GraphDataModel(data[4], balanceChartPlotsNames[1], true, getColor5(1), null, 1E-6, true);
				modelsBalanceChart[2] = new GraphDataModel(data[5], balanceChartPlotsNames[2], true, getColor5(2), null, 1E-6, true);
				modelsBalanceChart[3] = new GraphDataModel(data[6], balanceChartPlotsNames[3], true, getColor5(0), null, 1E-6, true);
				modelsBalanceChart[4] = new GraphDataModel(data[7], balanceChartPlotsNames[4], true, getColor5(1), null, 1E-6, true);
				modelsBalanceChart[5] = new GraphDataModel(data[8], balanceChartPlotsNames[5], true, getColor5(2), null, 1E-6, true);
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
