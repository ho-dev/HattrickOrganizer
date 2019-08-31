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
			chart.setAllValues(data, captions, format, "", "", false, true);
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

	public MultipleRatingChartsPanel() {
		super(new BorderLayout());
		initComponents();
	}

	private void initComponents() {
		showHelpLines.setEnabled(false);
		showValues.setEnabled(false);
		controlsPanel.add(showHelpLines);
		controlsPanel.add(showValues);
		add(controlsPanel, BorderLayout.SOUTH);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill =  GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;

		Hashtable<Double, Double> map = HOVerwaltung.instance().getModel().getLineup().getRatings().getLeftDefense();

		ArrayList<Double> keys = new ArrayList(map.keySet());
		keys.remove(-90d);  //remove 90' and 120'
		keys.remove(-120d); //average placeholder labels
		Collections.sort(keys, Collections.reverseOrder());
		ArrayList<Double> valueList = new ArrayList();
		ArrayList<String> captionList = new ArrayList();
		for(Double key : keys) {
			valueList.add(map.get(key));
			captionList.add(String.valueOf(key.intValue()));
		}
		leftDefense = new SingleChart(valueList.stream().mapToDouble(Double::doubleValue).toArray(), Helper.DEFAULTDEZIMALFORMAT, captionList.stream().toArray(String[]::new));

		chartsPanel.add(leftDefense.getChart(), gbc);

		add(chartsPanel, BorderLayout.CENTER);
	}
}
