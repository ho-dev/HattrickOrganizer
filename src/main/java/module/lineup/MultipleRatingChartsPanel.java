package module.lineup;

import core.model.HOVerwaltung;
import core.util.Helper;
import core.gui.model.StatistikModel;
import module.statistics.StatistikPanel;

import java.text.NumberFormat;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.JPanel;
import javax.swing.JCheckBox;

public final class MultipleRatingChartsPanel extends JPanel {

	private final class SingleChart {

		private StatistikPanel chart = new StatistikPanel(true);
		private StatistikModel[] data = new StatistikModel[1];
		private String[] captions;
		private double[] values;
		private NumberFormat format;

		public SingleChart(double[] values, NumberFormat format, String[] captions) {
			this.values = values;
			this.format = format;
			this.captions = captions;
			initComponents();
		}

		private void initComponents() {
			data[0] = new StatistikModel(values, null, true, java.awt.Color.black, format);
			data[0].setDataBasedBoundaries(true);
			chart.setDataBasedBoundaries(true);
			chart.setAllValues(data, captions, format, "", "", showValues.isSelected(), showHelpLines.isSelected());
		}

		public void setHelpLines(boolean state) {
			chart.setHilfslinien(state);
		}

		public void setValues(boolean state) {
			chart.setBeschriftung(state);
		}

		public void setToolTip(String text) {
			chart.setToolTipText(text);
		}

		public StatistikPanel getChart() {
			return chart;
		}
	}

	private HOVerwaltung hov = HOVerwaltung.instance();
	private JPanel controlsPanel = new JPanel();
	private JPanel chartsPanel = new JPanel(new GridBagLayout());
	private JCheckBox showHelpLines = new JCheckBox(hov.getLanguageString("Hilflinien"));
	private JCheckBox showValues = new JCheckBox(hov.getLanguageString("Beschriftung"));
	private RatingChartData chartData;
	private String[] chartCaptions;
	private SingleChart leftDefense;
	private SingleChart centralDefense;
	private SingleChart rightDefense;
	private SingleChart hatStats;
	private SingleChart midfield;
	private SingleChart loddar;
	private SingleChart leftAttack;
	private SingleChart centralAttack;
	private SingleChart rightAttack;

	public MultipleRatingChartsPanel(RatingChartData data) {
		super(new BorderLayout());
		chartData = data;
		chartCaptions = chartData.getCaptions();
		initComponents();
	}

	private void initComponents() {
		showHelpLines.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected;
				if (e.getStateChange() == ItemEvent.SELECTED) selected = true;
				else if (e.getStateChange() == ItemEvent.DESELECTED) selected = false;
				else return;
				leftDefense.setHelpLines(selected);
				centralDefense.setHelpLines(selected);
				rightDefense.setHelpLines(selected);
				hatStats.setHelpLines(selected);
				midfield.setHelpLines(selected);
				loddar.setHelpLines(selected);
				leftAttack.setHelpLines(selected);
				centralAttack.setHelpLines(selected);
				rightAttack.setHelpLines(selected);
			}
		});
		showValues.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected;
				if (e.getStateChange() == ItemEvent.SELECTED) selected = true;
				else if (e.getStateChange() == ItemEvent.DESELECTED) selected = false;
				else return;
				leftDefense.setValues(selected);
				centralDefense.setValues(selected);
				rightDefense.setValues(selected);
				hatStats.setValues(selected);
				midfield.setValues(selected);
				loddar.setValues(selected);
				leftAttack.setValues(selected);
				centralAttack.setValues(selected);
				rightAttack.setValues(selected);
			}
		});
		controlsPanel.add(showHelpLines);
		controlsPanel.add(showValues);
		add(controlsPanel, BorderLayout.SOUTH);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill =  GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;

		leftDefense = new SingleChart(chartData.getLeftDefense(), Helper.DEFAULTDEZIMALFORMAT, chartCaptions);
		leftDefense.setToolTip(hov.getLanguageString("ls.match.ratingsector.leftdefence"));
		chartsPanel.add(leftDefense.getChart(), gbc);

		gbc.gridx = 1;
		centralDefense = new SingleChart(chartData.getCentralDefense(), Helper.DEFAULTDEZIMALFORMAT, chartCaptions);
		centralDefense.setToolTip(hov.getLanguageString("ls.match.ratingsector.centraldefence"));
		chartsPanel.add(centralDefense.getChart(), gbc);

		gbc.gridx = 2;
		rightDefense = new SingleChart(chartData.getRightDefense(), Helper.DEFAULTDEZIMALFORMAT, chartCaptions);
		rightDefense.setToolTip(hov.getLanguageString("ls.match.ratingsector.rightdefence"));
		chartsPanel.add(rightDefense.getChart(), gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		hatStats = new SingleChart(chartData.getHatStats(), Helper.INTEGERFORMAT, chartCaptions);
		hatStats.setToolTip(hov.getLanguageString("ls.match.ratingtype.hatstats"));
		chartsPanel.add(hatStats.getChart(), gbc);

		gbc.gridx = 1;
		midfield = new SingleChart(chartData.getMidfield(), Helper.DEFAULTDEZIMALFORMAT, chartCaptions);
		midfield.setToolTip(hov.getLanguageString("ls.match.ratingsector.midfield"));
		chartsPanel.add(midfield.getChart(), gbc);

		gbc.gridx = 2;
		loddar = new SingleChart(chartData.getLoddar(), Helper.DEFAULTDEZIMALFORMAT, chartCaptions);
		loddar.setToolTip(hov.getLanguageString("ls.match.ratingtype.loddarstats"));
		chartsPanel.add(loddar.getChart(), gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		leftAttack = new SingleChart(chartData.getLeftAttack(), Helper.DEFAULTDEZIMALFORMAT, chartCaptions);
		leftAttack.setToolTip(hov.getLanguageString("ls.match.ratingsector.leftattack"));
		chartsPanel.add(leftAttack.getChart(), gbc);

		gbc.gridx = 1;
		centralAttack = new SingleChart(chartData.getCentralAttack(), Helper.DEFAULTDEZIMALFORMAT, chartCaptions);
		centralAttack.setToolTip(hov.getLanguageString("ls.match.ratingsector.centralattack"));
		chartsPanel.add(centralAttack.getChart(), gbc);

		gbc.gridx = 2;
		rightAttack = new SingleChart(chartData.getRightAttack(), Helper.DEFAULTDEZIMALFORMAT, chartCaptions);
		rightAttack.setToolTip(hov.getLanguageString("ls.match.ratingsector.rightattack"));
		chartsPanel.add(rightAttack.getChart(), gbc);

		add(chartsPanel, BorderLayout.CENTER);
	}
}
