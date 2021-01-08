package module.lineup.ratings;

import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.Helper;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.util.chart.LinesChartDataModel;
import core.util.chart.HOLinesChart;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;
import java.text.NumberFormat;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import javax.swing.JPanel;
import javax.swing.JCheckBox;

public final class CombinedRatingChartPanel extends JPanel {

	private final class Datum {
		private JCheckBox checkbox;
		private Color bg;
		private LinesChartDataModel model;
		private String paramName;
		private String m_SerieName;
		private Boolean m_bSecondAxis;
		private double m_Ymax;

		public Datum(String text, Color background, String userParamName, boolean second_axis, double d_max) {
			paramName = userParamName;
			m_SerieName = text;
			checkbox = new JCheckBox(text, getUserParameter());
			bg = background;
			m_bSecondAxis = second_axis;
			m_Ymax = d_max;
			init();
		}

		public Datum(String text, Color background, String userParamName) {
			this(text, background, userParamName, false, 0d);
		}

		private boolean getUserParameter() {
			try {
				return (boolean) userParameter.getClass().getField(paramName).get(userParameter);
				}
			catch(java.lang.NoSuchFieldException e) {}
			catch(java.lang.IllegalAccessException e) {}
			return false;
		}

		private void setUserParameter(Boolean selected) {
			try {
				userParameter.getClass().getField(paramName).set(userParameter, selected);
				}
			catch(java.lang.NoSuchFieldException e) {}
			catch(java.lang.IllegalAccessException e) {}
		}

		private void init() {
			checkbox.addItemListener(e -> {
				oChartPanel.setShow(m_SerieName, checkbox.isSelected());
				setUserParameter(checkbox.isSelected());
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

		LinesChartDataModel getChartModel(double[] values, NumberFormat format) {
			if (! m_bSecondAxis) {
				model = new LinesChartDataModel(values, m_SerieName, checkbox.isSelected(), bg, format);
			}
			else{
				double maxValue = Helper.getMaxValue(values);
				model = new LinesChartDataModel(values, m_SerieName, checkbox.isSelected(), bg, SeriesLines.DASH_DASH, SeriesMarkers.DIAMOND, format, m_Ymax/maxValue, true);
			}
			return model;
		}

		JCheckBox getCheckbox() {
			return checkbox;
		}
	}
	private HOVerwaltung hov = HOVerwaltung.instance();
	private UserParameter userParameter = UserParameter.instance();
	private RatingChartData chartData;
	private JPanel controlsPanel = new JPanel();
	private HOLinesChart oChartPanel;
	private JCheckBox showHelpLines = new JCheckBox(hov.getLanguageString("Hilflinien"), userParameter.CombinedRatingChartPanel_HelpLines);
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

	void prepareChart() {
		LinesChartDataModel[] data = new LinesChartDataModel[9];
		data[0] = leftDefense.getChartModel(chartData.getLeftDefense(), Helper.DEFAULTDEZIMALFORMAT);
		data[1] = centralDefense.getChartModel(chartData.getCentralDefense(), Helper.DEFAULTDEZIMALFORMAT);
		data[2] = rightDefense.getChartModel(chartData.getRightDefense(), Helper.DEFAULTDEZIMALFORMAT);
		data[3] = midfield.getChartModel(chartData.getMidfield(), Helper.DEFAULTDEZIMALFORMAT);
		data[4] = leftAttack.getChartModel(chartData.getLeftAttack(), Helper.DEFAULTDEZIMALFORMAT);
		data[5] = centralAttack.getChartModel(chartData.getCentralAttack(), Helper.DEFAULTDEZIMALFORMAT);
		data[6] = rightAttack.getChartModel(chartData.getRightAttack(), Helper.DEFAULTDEZIMALFORMAT);
		data[7] = hatStats.getChartModel(chartData.getHatStats(), Helper.INTEGERFORMAT);
		data[8] = loddar.getChartModel(chartData.getLoddar(), Helper.DEFAULTDEZIMALFORMAT);

		double maxValueY1 = Helper.getMaxValue(chartData.getLoddar());
		if(maxValueY1 > 20d) {
			oChartPanel.setYAxisMax(1, maxValueY1);
		}

		oChartPanel.setAllValues(data, chartData.getCaptions(), false, showHelpLines.isSelected());
	}

	private void initComponents() {
		controlsPanel.setLayout(new GridBagLayout());

		oChartPanel = new HOLinesChart(true, true, null, null, null, null, "#,##0","#,##0", 0d, 20d, null, null,null,null,false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill =  GridBagConstraints.HORIZONTAL;
		gbc.gridy = 0;

		showHelpLines.addItemListener(e -> {
			boolean selected;
			if (e.getStateChange() == ItemEvent.SELECTED) selected = true;
			else if (e.getStateChange() == ItemEvent.DESELECTED) selected = false;
			else return;
			oChartPanel.setHelpLines(selected);
			userParameter.CombinedRatingChartPanel_HelpLines = selected;
		});
		controlsPanel.add(showHelpLines, gbc);


		gbc.gridy++;
		leftDefense = new Datum(hov.getLanguageString("ls.match.ratingsector.leftdefence"),
								ThemeManager.getColor(HOColorName.SHIRT_WINGBACK).brighter(),
								"CombinedRatingChartPanel_LeftDefense");
		controlsPanel.add(leftDefense.getCheckbox(), gbc);

		gbc.gridy++;
		centralDefense = new Datum(hov.getLanguageString("ls.match.ratingsector.centraldefence"),
									ThemeManager.getColor(HOColorName.SHIRT_CENTRALDEFENCE),
									"CombinedRatingChartPanel_CentralDefense");
		controlsPanel.add(centralDefense.getCheckbox(), gbc);

		gbc.gridy++;
		rightDefense = new Datum(hov.getLanguageString("ls.match.ratingsector.rightdefence"),
								ThemeManager.getColor(HOColorName.SHIRT_WINGBACK).darker(),
								"CombinedRatingChartPanel_RightDefense");
		controlsPanel.add(rightDefense.getCheckbox(), gbc);

		gbc.gridy++;
		midfield = new Datum(hov.getLanguageString("ls.match.ratingsector.midfield"),
							ThemeManager.getColor(HOColorName.SHIRT_MIDFIELD),
							"CombinedRatingChartPanel_Midfield");
		controlsPanel.add(midfield.getCheckbox(), gbc);

		gbc.gridy++;
		leftAttack = new Datum(hov.getLanguageString("ls.match.ratingsector.leftattack"),
								ThemeManager.getColor(HOColorName.SHIRT_WING).brighter(),
								"CombinedRatingChartPanel_LeftAttack");
		controlsPanel.add(leftAttack.getCheckbox(), gbc);

		gbc.gridy++;
		centralAttack = new Datum(hov.getLanguageString("ls.match.ratingsector.centralattack"),
									ThemeManager.getColor(HOColorName.SHIRT_FORWARD),
									"CombinedRatingChartPanel_CentralAttack");
		controlsPanel.add(centralAttack.getCheckbox(), gbc);

		gbc.gridy++;
		rightAttack = new Datum(hov.getLanguageString("ls.match.ratingsector.rightattack"),
								ThemeManager.getColor(HOColorName.SHIRT_WING).darker(),
								"CombinedRatingChartPanel_RightAttack");
		controlsPanel.add(rightAttack.getCheckbox(), gbc);

		gbc.gridy++;
		hatStats = new Datum(hov.getLanguageString("ls.match.ratingtype.hatstats"),
							ThemeManager.getColor(HOColorName.STAT_HATSTATS),
							"CombinedRatingChartPanel_HatStats",  true, 19.0d);
		controlsPanel.add(hatStats.getCheckbox(), gbc);

		gbc.gridy++;
		loddar = new Datum(hov.getLanguageString("ls.match.ratingtype.loddarstats"),
							ThemeManager.getColor(HOColorName.STAT_LODDAR),
							"CombinedRatingChartPanel_Loddar");
		controlsPanel.add(loddar.getCheckbox(), gbc);

		add(controlsPanel, BorderLayout.WEST);

		prepareChart();
		add(oChartPanel.getPanel(), BorderLayout.CENTER);
	}
}
