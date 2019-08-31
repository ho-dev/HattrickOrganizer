package module.lineup;

import core.model.HOVerwaltung;
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

		public StatistikPanel getChart() {
			return chart;
		}
	}

	private JPanel controlsPanel = new JPanel();
	private JPanel chartsPanel = new JPanel(new GridBagLayout());
	private JCheckBox showHelpLines = new JCheckBox(HOVerwaltung.instance().getLanguageString("Hilflinien"));
	private JCheckBox showValues = new JCheckBox(HOVerwaltung.instance().getLanguageString("Beschriftung"));
	private SingleChart leftDefense;
	private SingleChart centralDefense;
	private SingleChart rightDefense;
	// private SingleChart hatStats;
	private SingleChart midfield;
	private SingleChart loddar;
	private SingleChart leftAttack;
	private SingleChart centralAttack;
	private SingleChart rightAttack;

	public MultipleRatingChartsPanel() {
		super(new BorderLayout());
		initComponents();
	}

	private SingleChart parse(Hashtable<Double, Double> source) {
		ArrayList<Double> keys = new ArrayList(source.keySet());
		keys.remove(-90d);  //remove 90' and 120'
		keys.remove(-120d); //average placeholder labels
		Collections.sort(keys, Collections.reverseOrder());
		ArrayList<Double> valueList = new ArrayList();
		ArrayList<String> captionList = new ArrayList();
		for(Double key : keys) {
			valueList.add(source.get(key));
			captionList.add(String.valueOf(key.intValue()));
		}
		return new SingleChart(valueList.stream().mapToDouble(Double::doubleValue).toArray(), Helper.DEFAULTDEZIMALFORMAT, captionList.stream().toArray(String[]::new));
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
				// hatStats.setHelpLines(selected);
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
				// hatStats.setValues(selected);
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

		Hashtable<Double, Double> map;

		map = HOVerwaltung.instance().getModel().getLineup().getRatings().getLeftDefense();
		leftDefense = parse(map);
		chartsPanel.add(leftDefense.getChart(), gbc);

		gbc.gridx = 1;
		map = HOVerwaltung.instance().getModel().getLineup().getRatings().getCentralDefense();
		centralDefense = parse(map);
		chartsPanel.add(centralDefense.getChart(), gbc);

		gbc.gridx = 2;
		map = HOVerwaltung.instance().getModel().getLineup().getRatings().getRightDefense();
		rightDefense = parse(map);
		chartsPanel.add(rightDefense.getChart(), gbc);

		// gbc.gridx = 0;
		// gbc.gridy = 1;
		// map = HOVerwaltung.instance().getModel().getLineup().getRatings().getHatStats();
		// hatStats = parse(map);
		// chartsPanel.add(hatStats.getChart(), gbc);

		gbc.gridx = 1;
		map = HOVerwaltung.instance().getModel().getLineup().getRatings().getMidfield();
		midfield = parse(map);
		chartsPanel.add(midfield.getChart(), gbc);

		gbc.gridx = 2;
		map = HOVerwaltung.instance().getModel().getLineup().getRatings().getLoddarStat();
		loddar = parse(map);
		chartsPanel.add(loddar.getChart(), gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		map = HOVerwaltung.instance().getModel().getLineup().getRatings().getLeftAttack();
		leftAttack = parse(map);
		chartsPanel.add(leftAttack.getChart(), gbc);

		gbc.gridx = 1;
		map = HOVerwaltung.instance().getModel().getLineup().getRatings().getCentralAttack();
		centralAttack = parse(map);
		chartsPanel.add(centralAttack.getChart(), gbc);

		gbc.gridx = 2;
		map = HOVerwaltung.instance().getModel().getLineup().getRatings().getRightAttack();
		rightAttack = parse(map);
		chartsPanel.add(rightAttack.getChart(), gbc);

		add(chartsPanel, BorderLayout.CENTER);
	}
}
