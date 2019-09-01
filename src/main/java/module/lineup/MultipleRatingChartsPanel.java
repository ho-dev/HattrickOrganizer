package module.lineup;

import core.model.HOVerwaltung;
import core.model.Ratings;
import core.util.Helper;
import core.gui.model.StatistikModel;
import module.statistics.StatistikPanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
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
	private Ratings ratings = hov.getModel().getLineup().getRatings();
	private SingleChart leftDefense;
	private SingleChart centralDefense;
	private SingleChart rightDefense;
	private SingleChart hatStats;
	private SingleChart midfield;
	private SingleChart loddar;
	private SingleChart leftAttack;
	private SingleChart centralAttack;
	private SingleChart rightAttack;
	private ArrayList<Double> mapKeys = null;
	private String[] chartCaptions = null;

	public MultipleRatingChartsPanel() {
		super(new BorderLayout());
		initComponents();
	}

	private void parsePrepare() {
		if(mapKeys == null) {
			mapKeys = new ArrayList(ratings.getMidfield().keySet());
			mapKeys.remove(-90d);  //remove 90' and 120'
			mapKeys.remove(-120d); //average placeholder labels
			Collections.sort(mapKeys, Collections.reverseOrder());
		}
		if(chartCaptions == null) {
			ArrayList<String> captionList = new ArrayList();
			for(Double key : mapKeys) {
				captionList.add(String.valueOf(key.intValue()));
			}
			chartCaptions = captionList.stream().toArray(String[]::new);
		}
	}

	private SingleChart parseDD(Hashtable<Double, Double> source) {
		parsePrepare();
		ArrayList<Double> valueList = new ArrayList();
		for(Double key : mapKeys) {
			valueList.add(source.get(key));
		}
		return new SingleChart(valueList.stream().mapToDouble(Double::doubleValue).toArray(), Helper.DEFAULTDEZIMALFORMAT, chartCaptions);
	}

	private SingleChart parseDI(Hashtable<Double, Integer> source) {
		parsePrepare();
		ArrayList<Double> valueList = new ArrayList();
		for(Double key : mapKeys) {
			valueList.add(source.get(key).doubleValue());
		}
		return new SingleChart(valueList.stream().mapToDouble(Double::doubleValue).toArray(), Helper.INTEGERFORMAT, chartCaptions);
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

		Hashtable<Double, Double> mapDD;

		mapDD = ratings.getLeftDefense();
		leftDefense = parseDD(mapDD);
		leftDefense.setToolTip(hov.getLanguageString("ls.match.ratingsector.leftdefence"));
		chartsPanel.add(leftDefense.getChart(), gbc);

		gbc.gridx = 1;
		mapDD = ratings.getCentralDefense();
		centralDefense = parseDD(mapDD);
		centralDefense.setToolTip(hov.getLanguageString("ls.match.ratingsector.centraldefence"));
		chartsPanel.add(centralDefense.getChart(), gbc);

		gbc.gridx = 2;
		mapDD = ratings.getRightDefense();
		rightDefense = parseDD(mapDD);
		rightDefense.setToolTip(hov.getLanguageString("ls.match.ratingsector.rightdefence"));
		chartsPanel.add(rightDefense.getChart(), gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		Hashtable<Double, Integer> mapDI = ratings.getHatStats();
		hatStats = parseDI(mapDI);
		hatStats.setToolTip(hov.getLanguageString("ls.match.ratingtype.hatstats"));
		chartsPanel.add(hatStats.getChart(), gbc);

		gbc.gridx = 1;
		mapDD = ratings.getMidfield();
		midfield = parseDD(mapDD);
		midfield.setToolTip(hov.getLanguageString("ls.match.ratingsector.midfield"));
		chartsPanel.add(midfield.getChart(), gbc);

		gbc.gridx = 2;
		mapDD = ratings.getLoddarStat();
		loddar = parseDD(mapDD);
		loddar.setToolTip(hov.getLanguageString("ls.match.ratingtype.loddarstats"));
		chartsPanel.add(loddar.getChart(), gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		mapDD = ratings.getLeftAttack();
		leftAttack = parseDD(mapDD);
		leftAttack.setToolTip(hov.getLanguageString("ls.match.ratingsector.leftattack"));
		chartsPanel.add(leftAttack.getChart(), gbc);

		gbc.gridx = 1;
		mapDD = ratings.getCentralAttack();
		centralAttack = parseDD(mapDD);
		centralAttack.setToolTip(hov.getLanguageString("ls.match.ratingsector.centralattack"));
		chartsPanel.add(centralAttack.getChart(), gbc);

		gbc.gridx = 2;
		mapDD = ratings.getRightAttack();
		rightAttack = parseDD(mapDD);
		rightAttack.setToolTip(hov.getLanguageString("ls.match.ratingsector.rightattack"));
		chartsPanel.add(rightAttack.getChart(), gbc);

		add(chartsPanel, BorderLayout.CENTER);
	}
}
