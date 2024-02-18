package module.lineup.ratings;

import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.Helper;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.util.chart.LinesChartDataModel;
import core.util.chart.HOLinesChart;
import core.gui.comp.ImageCheckbox;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;
import java.awt.*;
import java.awt.event.ItemEvent;
import javax.swing.JPanel;
import javax.swing.JCheckBox;

public final class CombinedRatingChartPanel extends JPanel {

	private final class Datum {
		private final ImageCheckbox checkbox;
		private final Color m_ColorSerie;
		private final String paramName;
		private final String m_SerieName;
		private final Boolean m_bSecondAxis;
		private final double m_Ymax;

		public Datum(String text, Color colorSerie, String userParamName, boolean second_axis, double d_max) {
			paramName = userParamName;
			m_SerieName = text;
			checkbox = new ImageCheckbox(text, colorSerie, getUserParameter());
			m_ColorSerie = colorSerie;
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
			catch(NoSuchFieldException | IllegalAccessException ignored) {}
            return false;
		}

		private void setUserParameter(Boolean selected) {
			try {
				userParameter.getClass().getField(paramName).set(userParameter, selected);
				}
			catch(NoSuchFieldException | IllegalAccessException ignored) {}
        }

		private void init() {
			checkbox.addActionListener(e -> {
				oChartPanel.setShow(m_SerieName, checkbox.isSelected());
				setUserParameter(checkbox.isSelected());
			});
		}


		LinesChartDataModel getChartModel(double[] values) {
			LinesChartDataModel model;
			if (! m_bSecondAxis) {
				model = new LinesChartDataModel(values, m_SerieName, checkbox.isSelected(), m_ColorSerie);
			}
			else{
				double maxValue = Helper.getMaxValue(values);
				model = new LinesChartDataModel(values, m_SerieName, checkbox.isSelected(), m_ColorSerie, SeriesLines.DASH_DASH, SeriesMarkers.DIAMOND, m_Ymax/maxValue, true);
			}
			return model;
		}

		ImageCheckbox getCheckbox() {
			return checkbox;
		}
	}
	private final HOVerwaltung hov = HOVerwaltung.instance();
	private final UserParameter userParameter = UserParameter.instance();
	private final RatingChartData chartData;
	private final JPanel controlsPanel = new JPanel();
	private HOLinesChart oChartPanel;
	private final JCheckBox showHelpLines = new JCheckBox(hov.getLanguageString("Hilflinien"), userParameter.CombinedRatingChartPanel_HelpLines);
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
		data[0] = leftDefense.getChartModel(chartData.getLeftDefence());
		data[1] = centralDefense.getChartModel(chartData.getCentralDefence());
		data[2] = rightDefense.getChartModel(chartData.getRightDefence());
		data[3] = midfield.getChartModel(chartData.getMidfield());
		data[4] = leftAttack.getChartModel(chartData.getLeftAttack());
		data[5] = centralAttack.getChartModel(chartData.getCentralAttack());
		data[6] = rightAttack.getChartModel(chartData.getRightAttack());
		data[7] = hatStats.getChartModel(chartData.getHatStats());
		data[8] = loddar.getChartModel(chartData.getLoddar());

		double maxValueY1 = Helper.getMaxValue(chartData.getLoddar());
		if(maxValueY1 > 20d) {
			oChartPanel.setYAxisMax(1, maxValueY1);
		}

		oChartPanel.setAllValues(data, chartData.getCaptions(), false, showHelpLines.isSelected());
	}

	private void initComponents() {
		controlsPanel.setLayout(new GridBagLayout());

		oChartPanel = new HOLinesChart(true, null, null, null, "#,##0",0d, 20d,null, null, false);

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
		gbc.insets = new Insets(10,0,0,0);  //top padding
		leftDefense = new Datum(hov.getLanguageString("ls.match.ratingsector.leftdefence"),
				ThemeManager.getColor(HOColorName.PALETTE13_0), "CombinedRatingChartPanel_LeftDefense");
		controlsPanel.add(leftDefense.getCheckbox(), gbc);

		gbc.gridy++;
		gbc.insets = new Insets(0,0,0,0);  //top padding
		centralDefense = new Datum(hov.getLanguageString("ls.match.ratingsector.centraldefence"),
									ThemeManager.getColor(HOColorName.PALETTE13_1),
									"CombinedRatingChartPanel_CentralDefense");
		controlsPanel.add(centralDefense.getCheckbox(), gbc);

		gbc.gridy++;
		rightDefense = new Datum(hov.getLanguageString("ls.match.ratingsector.rightdefence"),
								ThemeManager.getColor(HOColorName.PALETTE13_2),
								"CombinedRatingChartPanel_RightDefense");
		controlsPanel.add(rightDefense.getCheckbox(), gbc);

		gbc.gridy++;
		gbc.insets = new Insets(10,0,0,0);  //top padding
		midfield = new Datum(hov.getLanguageString("ls.match.ratingsector.midfield"),
							ThemeManager.getColor(HOColorName.PALETTE13_3),
							"CombinedRatingChartPanel_Midfield");
		controlsPanel.add(midfield.getCheckbox(), gbc);

		gbc.gridy++;
		leftAttack = new Datum(hov.getLanguageString("ls.match.ratingsector.leftattack"),
								ThemeManager.getColor(HOColorName.PALETTE13_4),
								"CombinedRatingChartPanel_LeftAttack");
		controlsPanel.add(leftAttack.getCheckbox(), gbc);

		gbc.gridy++;
		gbc.insets = new Insets(0,0,0,0);  //top padding
		centralAttack = new Datum(hov.getLanguageString("ls.match.ratingsector.centralattack"),
									ThemeManager.getColor(HOColorName.PALETTE13_5),
									"CombinedRatingChartPanel_CentralAttack");
		controlsPanel.add(centralAttack.getCheckbox(), gbc);

		gbc.gridy++;
		rightAttack = new Datum(hov.getLanguageString("ls.match.ratingsector.rightattack"),
								ThemeManager.getColor(HOColorName.PALETTE13_6),
								"CombinedRatingChartPanel_RightAttack");
		controlsPanel.add(rightAttack.getCheckbox(), gbc);

		gbc.gridy++;
		gbc.insets = new Insets(10,0,0,0);  //top padding
		loddar = new Datum(hov.getLanguageString("ls.match.ratingtype.loddarstats"),
				ThemeManager.getColor(HOColorName.PALETTE13_8),
				"CombinedRatingChartPanel_Loddar");
		controlsPanel.add(loddar.getCheckbox(), gbc);

		gbc.gridy++;
		gbc.insets = new Insets(0,0,0,0);  //top padding
		String textLabel = hov.getLanguageString("ls.match.ratingtype.hatstats") + " (" + hov.getLanguageString("ls.chart.second_axis") + ")";
		hatStats = new Datum(textLabel, ThemeManager.getColor(HOColorName.PALETTE13_7),
							"CombinedRatingChartPanel_HatStats",  true, 19.0d);
		controlsPanel.add(hatStats.getCheckbox(), gbc);



		add(controlsPanel, BorderLayout.WEST);

		prepareChart();
		add(oChartPanel.getPanel(), BorderLayout.CENTER);
	}
}
