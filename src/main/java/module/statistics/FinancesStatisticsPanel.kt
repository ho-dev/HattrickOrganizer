package module.statistics;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.util.chart.LinesChartDataModel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.HOLogger;
import core.util.Helper;
import core.util.chart.HOLinesChart;
import core.util.chart.HODoublePieChart;
import core.util.chart.PieChartDataModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.ItemEvent;
import java.text.NumberFormat;
import java.util.Arrays;
import javax.swing.*;

/**
 * Panel Finances in Module Statistics
 */
public class FinancesStatisticsPanel extends LazyImagePanel {

	private JComboBox<String> c_jcomboChartType;
	private int iChartType;
	private JTextField c_jtfNumberOfHRF;
	private JButton c_jbFetch;
	private JCheckBox jcbHelpLines;
	private JCheckBox jcbInscribe;

	private JCheckBox c_jcbInclTransferts;
	private JPanel c_jpCharts;
	final static String BALANCE_CHART_PANEL = "Balance Chart";
	final static String DEVELOPMENT_CHART_PANEL = "Development Chart";
	final static String REVENUE_AND_EXPENSES_CHART_PANEL = "P&L Chart";
	private HOLinesChart c_jpBalanceChart;
	private HOLinesChart c_jpDevelopmentChart;
	private HODoublePieChart c_jpRevenueAndExpensesChart;
	private final String[] balanceChartPlotsNames = new String[6];
	private boolean[] balanceChartPlotsVisible = new boolean[6];
	private boolean bIncludeTransfer;


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


	private void updateCB(boolean bHelpLines, boolean doInscribe){
		switch (iChartType){
			case 0 -> {jcbHelpLines.setSelected(bHelpLines); jcbHelpLines.setEnabled(true); jcbInscribe.setSelected(doInscribe); jcbInscribe.setEnabled(true); c_jcbInclTransferts.setVisible(false);}
			case 1 -> {jcbHelpLines.setSelected(bHelpLines); jcbHelpLines.setEnabled(true); jcbInscribe.setSelected(doInscribe); jcbInscribe.setEnabled(true); c_jcbInclTransferts.setVisible(true);}
			case 2 -> {jcbHelpLines.setSelected(false); jcbHelpLines.setEnabled(false); jcbInscribe.setSelected(false); jcbInscribe.setEnabled(false); c_jcbInclTransferts.setVisible(false);}
		}
	}

	private void addListeners() {

		UserParameter gup = UserParameter.instance();

		c_jcomboChartType.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {

				iChartType = c_jcomboChartType.getSelectedIndex();

				updateCB(gup.statistikFinanzenHilfslinien, gup.statistikAlleBeschriftung);

				gup.statisticsFinanceChartType = iChartType;
				CardLayout cl = (CardLayout)(c_jpCharts.getLayout());
				cl.show(c_jpCharts, getChartCode(iChartType));
			}
		});

		ActionListener checkBoxActionListener = e -> {
			if (e.getSource() == jcbHelpLines) {
				c_jpBalanceChart.setHelpLines(jcbHelpLines.isSelected());
				c_jpDevelopmentChart.setHelpLines(jcbHelpLines.isSelected());
				gup.statistikFinanzenHilfslinien = jcbHelpLines.isSelected();
			}
			else if (e.getSource() == jcbInscribe) {
				c_jpBalanceChart.setLabelling(jcbInscribe.isSelected());
				c_jpDevelopmentChart.setLabelling(jcbInscribe.isSelected());
				gup.statistikAlleBeschriftung = jcbHelpLines.isSelected();
			}
			else if (e.getSource() == c_jcbInclTransferts) {
				if(c_jcbInclTransferts.isSelected()) {
					balanceChartPlotsVisible = new boolean[] {false, false, false, true, true, true};
					c_jpBalanceChart.setMultipleShow(balanceChartPlotsNames, balanceChartPlotsVisible);
					gup.statisticsFinanceIncludeTransfers = true;
					bIncludeTransfer = true;
				}
				else{
					balanceChartPlotsVisible = new boolean[] {true, true, true, false, false, false};
					c_jpBalanceChart.setMultipleShow(balanceChartPlotsNames, balanceChartPlotsVisible);
					gup.statisticsFinanceIncludeTransfers = false;
					bIncludeTransfer = false;
				}
			}

		};
		jcbHelpLines.addActionListener(checkBoxActionListener);
		jcbInscribe.addActionListener(checkBoxActionListener);
		c_jcbInclTransferts.addActionListener(checkBoxActionListener);
		c_jbFetch.addActionListener(e -> initStatistik());

		c_jtfNumberOfHRF.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(java.awt.event.FocusEvent focusEvent) {
				Helper.parseInt(HOMainFrame.instance(), c_jtfNumberOfHRF, false);
			}
		});
	}

	private void initComponents() {

		JLabel labelWeeks, labelChartType;

		UserParameter gup = UserParameter.instance();

		iChartType = gup.statisticsFinanceChartType;
		bIncludeTransfer = gup.statisticsFinanceIncludeTransfers;

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
		constraints2.gridy = 0;
		constraints2.gridwidth = 1;
		constraints2.insets = new Insets(10,0,0,0);  //top padding
		layout2.setConstraints(labelWeeks, constraints2);
		panel2.add(labelWeeks);

		c_jtfNumberOfHRF = new JTextField(String.valueOf(gup.statistikFinanzenAnzahlHRF), 3);
		c_jtfNumberOfHRF.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints2.gridx = 1;
		constraints2.insets = new Insets(10,5,0,0);  //top padding
		layout2.setConstraints(c_jtfNumberOfHRF, constraints2);
		panel2.add(c_jtfNumberOfHRF);

		constraints2.gridx = 2;
		constraints2.insets = new Insets(10,20,0,0);  //top padding
		c_jbFetch = new JButton(getLangStr("ls.button.apply"));
		c_jbFetch.setToolTipText(getLangStr("tt_Statistik_HRFAnzahluebernehmen"));
		layout2.setConstraints(c_jbFetch, constraints2);
		panel2.add(c_jbFetch);

		constraints2.gridwidth = 3;
		constraints2.gridx = 0;
		constraints2.gridy = 1;
		constraints2.insets = new Insets(20,0,0,0);  //top padding
		jcbHelpLines = new JCheckBox(getLangStr("Hilflinien"), gup.statistikFinanzenHilfslinien);
		jcbHelpLines.setOpaque(false);
		layout2.setConstraints(jcbHelpLines, constraints2);
		panel2.add(jcbHelpLines);

		constraints2.gridy++;
		constraints2.insets = new Insets(20,0,0,0);  //top padding
		jcbInscribe = new JCheckBox(getLangStr("Beschriftung"), gup.statistikFinanzenHilfslinien);
		jcbInscribe.setOpaque(false);
		layout2.setConstraints(jcbInscribe, constraints2);
		panel2.add(jcbInscribe);

		// Label Chart Type
		labelChartType = new JLabel(getLangStr("ls.module.statistic.finance.chart_type"));
		constraints2.fill = GridBagConstraints.HORIZONTAL;
		constraints2.anchor = GridBagConstraints.WEST;
		constraints2.gridwidth = 1;
		constraints2.gridy++;
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
		constraints2.gridwidth = 2;
		constraints2.insets = new Insets(20,5,0,0);  //top padding
		panel2.add(c_jcomboChartType, constraints2);
		
		c_jcbInclTransferts = new JCheckBox(getLangStr("ls.module.statistic.finance.l.include_transfer"), bIncludeTransfer);
		c_jcbInclTransferts.setToolTipText(getLangStr("ls.module.statistic.finance.tt.include_transfer"));
		constraints2.insets = new Insets(5, 0, 0, 0);
		constraints2.gridx = 0;
		constraints2.gridy++;
		constraints2.gridwidth = 3;
		panel2.add(c_jcbInclTransferts, constraints2);

		updateCB(gup.statistikFinanzenHilfslinien, gup.statistikAlleBeschriftung);

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
		c_jpDevelopmentChart = new HOLinesChart(true, null, null, "#,##0 " + currencySymbol, "#,##0 " + currencySymbol, true);
		c_jpBalanceChart = new HOLinesChart(true, null, null, "#,##0 " + currencySymbol, "#,##0 " + currencySymbol, true);
		c_jpRevenueAndExpensesChart = new HODoublePieChart(true);

		//Create the panel that contains the "cards" each card being a different chart
		c_jpCharts = new JPanel(new CardLayout());
		c_jpCharts.add(c_jpDevelopmentChart.getPanel(), DEVELOPMENT_CHART_PANEL);
		c_jpCharts.add(c_jpBalanceChart.getPanel(), BALANCE_CHART_PANEL);
		c_jpCharts.add(c_jpRevenueAndExpensesChart.getPanel(), REVENUE_AND_EXPENSES_CHART_PANEL);

		CardLayout cl = (CardLayout)(c_jpCharts.getLayout());
		cl.show(c_jpCharts, getChartCode(iChartType));

		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.weighty = 1.0;
		constraints.weightx = 1.0;
		constraints.anchor = GridBagConstraints.NORTH;
		layout.setConstraints(c_jpCharts, constraints);
		add(c_jpCharts);


		balanceChartPlotsNames[0] = getLangStr("ls.module.statistic.finance.income") + " (" + getLangStr("ls.chart.second_axis") + ")";
		balanceChartPlotsNames[1] = getLangStr("ls.module.statistic.finance.costs") + " (" + getLangStr("ls.chart.second_axis") + ")";
		balanceChartPlotsNames[2] = getLangStr("ls.module.statistic.finance.balance");
		balanceChartPlotsNames[3] = getLangStr("ls.module.statistic.finance.income_wo_transfers") + " (" + getLangStr("ls.chart.second_axis") + ")";
		balanceChartPlotsNames[4] = getLangStr("ls.module.statistic.finance.costs_wo_transfers") + " (" + getLangStr("ls.chart.second_axis") + ")";
		balanceChartPlotsNames[5] = getLangStr("ls.module.statistic.finance.balance_wo_transfers");

	}


	private void initStatistik() {
		try {
			int iNbHRF = Integer.parseInt(c_jtfNumberOfHRF.getText());
			if (iNbHRF <= 0) {
				iNbHRF = 1;
			}

			UserParameter.instance().statistikFinanzenAnzahlHRF = iNbHRF;

			NumberFormat format = NumberFormat.getCurrencyInstance();

			double[][] data = DBManager.instance().getDataForFinancesStatisticsPanel(iNbHRF);
			LinesChartDataModel[] modelsDevelopmentChart, modelsBalanceChart;
			PieChartDataModel[] modelsRevExpChart_Revenue, modelsRevExpChart_Expenses;

			modelsBalanceChart = new LinesChartDataModel[6];
			modelsDevelopmentChart = new LinesChartDataModel[3];
			modelsRevExpChart_Revenue = new PieChartDataModel[5];
			modelsRevExpChart_Expenses = new PieChartDataModel[5];

			if (data.length > 0) {

				modelsDevelopmentChart[0] = new LinesChartDataModel(data[1], getLangStr("ls.finance.revenue.sponsors"),	true, Colors.getColor(Colors.COLOR_FINANCE_INCOME_SPONSORS), null, 0d, false);
				modelsDevelopmentChart[1] = new LinesChartDataModel(data[2], getLangStr("ls.finance.expenses.wages"),	true, Colors.getColor(Colors.COLOR_FINANCE_COST_PLAYERS), null, 0d, false);
				modelsDevelopmentChart[2] = new LinesChartDataModel(data[0], getLangStr("ls.finance.cash") + " (" + getLangStr("ls.chart.second_axis") + ")", true, Colors.getColor(Colors.COLOR_FINANCE_CASH), null, 0d, true);

				modelsBalanceChart[0] = new LinesChartDataModel(data[3], balanceChartPlotsNames[0], !bIncludeTransfer, Colors.getColor(0), null, 0d, true);
				modelsBalanceChart[1] = new LinesChartDataModel(data[4], balanceChartPlotsNames[1], !bIncludeTransfer, Colors.getColor(1), null, 0d, true);
				modelsBalanceChart[2] = new LinesChartDataModel(data[5], balanceChartPlotsNames[2], !bIncludeTransfer, Colors.getColor(2), null, 0d, true);
				modelsBalanceChart[3] = new LinesChartDataModel(data[6], balanceChartPlotsNames[3], bIncludeTransfer, Colors.getColor(0), null, 0d, true);
				modelsBalanceChart[4] = new LinesChartDataModel(data[7], balanceChartPlotsNames[4], bIncludeTransfer, Colors.getColor(1), null, 0d, true);
				modelsBalanceChart[5] = new LinesChartDataModel(data[8], balanceChartPlotsNames[5], bIncludeTransfer, Colors.getColor(2), null, 0d, true);

				modelsRevExpChart_Revenue[0] = new PieChartDataModel(getLangStr("ls.finance.revenue.player_sales"), Arrays.stream(data[10]).sum(), true, Colors.getColor(0));
				modelsRevExpChart_Revenue[1] = new PieChartDataModel(getLangStr("ls.finance.revenue.commission"), Arrays.stream(data[11]).sum(), true, Colors.getColor(1));
				modelsRevExpChart_Revenue[2] = new PieChartDataModel(getLangStr("ls.finance.revenue.match_takings"), Arrays.stream(data[9]).sum(), true, Colors.getColor(2));
				modelsRevExpChart_Revenue[3] = new PieChartDataModel(getLangStr("ls.finance.revenue.sponsors"), Arrays.stream(data[1]).sum(), true, Colors.getColor(3));
				modelsRevExpChart_Revenue[4] = new PieChartDataModel(getLangStr("ls.finance.other"), Arrays.stream(data[12]).sum(), true, Colors.getColor(4));

				modelsRevExpChart_Expenses[0] = new PieChartDataModel(getLangStr("ls.finance.expenses.stadium_maintenance"), Arrays.stream(data[13]).sum(), true, Colors.getColor(0));
				modelsRevExpChart_Expenses[1] = new PieChartDataModel(getLangStr("ls.finance.expenses.new_signings"), Arrays.stream(data[14]).sum(), true, Colors.getColor(1));
				modelsRevExpChart_Expenses[2] = new PieChartDataModel(getLangStr("ls.finance.expenses.wages"), Arrays.stream(data[2]).sum(), true, Colors.getColor(2));
				modelsRevExpChart_Expenses[3] = new PieChartDataModel(getLangStr("ls.finance.expenses.staff"), Arrays.stream(data[15]).sum(), true, Colors.getColor(3));
				modelsRevExpChart_Expenses[4] = new PieChartDataModel(getLangStr("ls.finance.other"), Arrays.stream(data[16]).sum(), true, Colors.getColor(4));


			}


			c_jpDevelopmentChart.setAllValues(modelsDevelopmentChart, data[17], format, getLangStr("Wochen"), "",jcbInscribe.isSelected(),
					jcbHelpLines.isSelected());

			c_jpBalanceChart.setAllValues(modelsBalanceChart, data[17], format, getLangStr("Wochen"), "",jcbInscribe.isSelected(),
					jcbHelpLines.isSelected());

			c_jpRevenueAndExpensesChart.setAllValues(modelsRevExpChart_Revenue, modelsRevExpChart_Expenses);

		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}

	private String getLangStr(String key) {return HOVerwaltung.instance().getLanguageString(key);}

	private String getChartCode(int i) {

		return switch (i) {
			case 0 -> DEVELOPMENT_CHART_PANEL;
			case 1 -> BALANCE_CHART_PANEL;
			default -> REVENUE_AND_EXPENSES_CHART_PANEL;
		};
	}

}
