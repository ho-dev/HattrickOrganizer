package module.lineup;

import core.model.HOVerwaltung;
import core.util.Helper;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.gui.model.StatistikModel;
import module.statistics.StatistikPanel;

import java.text.NumberFormat;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.JPanel;
import javax.swing.JCheckBox;

public final class CombinedRatingChartPanel extends JPanel {

	private final class Datum {
		private JCheckBox checkbox;
		private Color bg;
		private StatistikModel model;

		public Datum(String text, Color background) {
			checkbox = new JCheckBox(text);
			bg = background;
			init();
		}

		private void init() {
			checkbox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) model.setShow(true);
					else if (e.getStateChange() == ItemEvent.DESELECTED) model.setShow(false);
					chart.repaint();
				}
			});
			checkbox.setOpaque(true);
			checkbox.setBackground(bg);
			if(getRelativeLuminance(bg) < 0.179) checkbox.setForeground(Color.WHITE);
		}

		double getRelativeLuminance(Color color) {
			double[] rgb = {color.getRed(), color.getGreen(), color.getBlue()};
			for(int i = 0; i < 3; i++) {
				rgb[i] /= 255.0;
				if(rgb[i] <= 0.03928) rgb[i] /= 12.92;
				else rgb[i] = Math.pow( (rgb[i] + 0.055) / 1.055, 2.4);
			}
			return (0.2126 * rgb[0] + 0.7152 * rgb[1] + 0.0722 * rgb[2]);
		}

		StatistikModel getChartModel(double[] values, NumberFormat format) {
			model = new StatistikModel(values, null, checkbox.isSelected(), bg, format);
			model.setDataBasedBoundaries(true);
			return model;
		}

		JCheckBox getCheckbox() {
			return checkbox;
		}
	}

	private HOVerwaltung hov = HOVerwaltung.instance();
	private RatingChartData chartData;
	private JPanel controlsPanel = new JPanel();
	private StatistikPanel chart = new StatistikPanel(true);
	private JCheckBox showHelpLines = new JCheckBox(hov.getLanguageString("Hilflinien"));
	private JCheckBox showValues = new JCheckBox(hov.getLanguageString("Beschriftung"));
	private Datum leftDefense;
	private Datum centralDefense;
	private Datum rightDefense;
	private Datum midfield;
	private Datum leftAttack;
	private Datum centralAttack;
	private Datum rightAttack;
	private Datum hatStats;
	private Datum loddar;

	public CombinedRatingChartPanel(RatingChartData data) {
		setLayout(new BorderLayout());
		chartData = data;
		initComponents();
	}

	StatistikPanel getChart() {
		return chart;
	}

	void prepareChart() {
		StatistikModel[] data = new StatistikModel[9];
		data[0] = leftDefense.getChartModel(chartData.getLeftDefense(), Helper.DEFAULTDEZIMALFORMAT);
		data[1] = centralDefense.getChartModel(chartData.getCentralDefense(), Helper.DEFAULTDEZIMALFORMAT);
		data[2] = rightDefense.getChartModel(chartData.getRightDefense(), Helper.DEFAULTDEZIMALFORMAT);
		data[3] = midfield.getChartModel(chartData.getMidfield(), Helper.DEFAULTDEZIMALFORMAT);
		data[4] = leftAttack.getChartModel(chartData.getLeftAttack(), Helper.DEFAULTDEZIMALFORMAT);
		data[5] = centralAttack.getChartModel(chartData.getCentralAttack(), Helper.DEFAULTDEZIMALFORMAT);
		data[6] = rightAttack.getChartModel(chartData.getRightAttack(), Helper.DEFAULTDEZIMALFORMAT);
		data[7] = hatStats.getChartModel(chartData.getHatStats(), Helper.INTEGERFORMAT);
		data[8] = loddar.getChartModel(chartData.getLoddar(), Helper.DEFAULTDEZIMALFORMAT);
		chart.setDataBasedBoundaries(true);
		chart.setAllValues(data, chartData.getCaptions(), Helper.DEFAULTDEZIMALFORMAT, "", "", showValues.isSelected(), showHelpLines.isSelected());
	}

	private void initComponents() {
		controlsPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill =  GridBagConstraints.HORIZONTAL;
		gbc.gridy = 0;

		showHelpLines.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) getChart().setHilfslinien(true);
				else if (e.getStateChange() == ItemEvent.DESELECTED) getChart().setHilfslinien(false);
			}
		});
		controlsPanel.add(showHelpLines, gbc);

		gbc.gridy++;
		showValues.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) getChart().setBeschriftung(true);
				else if (e.getStateChange() == ItemEvent.DESELECTED) getChart().setBeschriftung(false);
			}
		});
		controlsPanel.add(showValues, gbc);

		gbc.gridy++;
		leftDefense = new Datum(hov.getLanguageString("ls.match.ratingsector.leftdefence"),
								ThemeManager.getColor(HOColorName.SHIRT_WINGBACK).brighter());
		controlsPanel.add(leftDefense.getCheckbox(), gbc);

		gbc.gridy++;
		centralDefense = new Datum(hov.getLanguageString("ls.match.ratingsector.centraldefence"),
									ThemeManager.getColor(HOColorName.SHIRT_CENTRALDEFENCE));
		controlsPanel.add(centralDefense.getCheckbox(), gbc);

		gbc.gridy++;
		rightDefense = new Datum(hov.getLanguageString("ls.match.ratingsector.rightdefence"),
								ThemeManager.getColor(HOColorName.SHIRT_WINGBACK).darker());
		controlsPanel.add(rightDefense.getCheckbox(), gbc);

		gbc.gridy++;
		midfield = new Datum(hov.getLanguageString("ls.match.ratingsector.midfield"),
							ThemeManager.getColor(HOColorName.SHIRT_MIDFIELD));
		controlsPanel.add(midfield.getCheckbox(), gbc);

		gbc.gridy++;
		leftAttack = new Datum(hov.getLanguageString("ls.match.ratingsector.leftattack"),
								ThemeManager.getColor(HOColorName.SHIRT_WING).brighter());
		controlsPanel.add(leftAttack.getCheckbox(), gbc);

		gbc.gridy++;
		centralAttack = new Datum(hov.getLanguageString("ls.match.ratingsector.centralattack"),
									ThemeManager.getColor(HOColorName.SHIRT_FORWARD));
		controlsPanel.add(centralAttack.getCheckbox(), gbc);

		gbc.gridy++;
		rightAttack = new Datum(hov.getLanguageString("ls.match.ratingsector.rightattack"),
								ThemeManager.getColor(HOColorName.SHIRT_WING).darker());
		controlsPanel.add(rightAttack.getCheckbox(), gbc);

		gbc.gridy++;
		hatStats = new Datum(hov.getLanguageString("ls.match.ratingtype.hatstats"),
							ThemeManager.getColor(HOColorName.STAT_HATSTATS));
		controlsPanel.add(hatStats.getCheckbox(), gbc);

		gbc.gridy++;
		loddar = new Datum(hov.getLanguageString("ls.match.ratingtype.loddarstats"),
							ThemeManager.getColor(HOColorName.STAT_LODDAR));
		controlsPanel.add(loddar.getCheckbox(), gbc);

		add(controlsPanel, BorderLayout.WEST);

		prepareChart();
		add(chart, BorderLayout.CENTER);
	}
}
