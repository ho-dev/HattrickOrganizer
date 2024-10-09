package module.lineup.ratings;

import core.gui.HOMainFrame;
import core.model.TranslationFacility;
import core.model.UserParameter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class RatingChartFrame extends JFrame {

	private final UserParameter userParameter = UserParameter.instance();
	private final JRadioButton combinedChartButton = new JRadioButton(TranslationFacility.tr("lineup.CombinedChart"));
	private final JRadioButton multiChartButton = new JRadioButton(TranslationFacility.tr("lineup.MultiChart"));
	private final ButtonGroup chartButtonGroup = new ButtonGroup();
	private final JCheckBox etToggler = new JCheckBox(TranslationFacility.tr("lineup.ETToggler"), userParameter.RatingChartFrame_ET);
	private final JPanel controlsPanel = new JPanel();
	private final JPanel placeholderChart = new JPanel();
	private final RatingChartData chartData = new RatingChartData();
	private final CombinedRatingChartPanel[] combinedChart = new CombinedRatingChartPanel[1];
	private final MultipleRatingChartsPanel[] multiChart = new MultipleRatingChartsPanel[1];
	private final Dimension chartSize = new Dimension(900,700);

	class ChartButtonHandler implements ItemListener {
		JPanel[] source;

		public ChartButtonHandler(JPanel[] source) {
			this.source = source;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			boolean selected;
			if (e.getStateChange() == ItemEvent.SELECTED) {
				selected = true;
				if(placeholderChart.getParent() != null) remove(placeholderChart);
				if(source[0] == null) {
					if(e.getSource() == combinedChartButton) source[0] = new CombinedRatingChartPanel(chartData);
					else if(e.getSource() == multiChartButton) source[0] = new MultipleRatingChartsPanel(chartData);
					source[0].setPreferredSize(chartSize);
				}
				add(source[0], BorderLayout.CENTER);
				revalidate();
				repaint();
			}
			else if (e.getStateChange() == ItemEvent.DESELECTED) {
				selected = false;
				remove(source[0]);
			}
			else return;
			if(e.getSource() == combinedChartButton) userParameter.RatingChartFrame_Combined = selected;
			else if(e.getSource() == multiChartButton) userParameter.RatingChartFrame_Multiple = selected;
		}
	}

	RatingChartFrame() {
		super(TranslationFacility.tr("RatingChartFrame"));
		this.setIconImage(HOMainFrame.instance().getIconImage());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		initComponents();
	}

	private void initComponents() {
	chartButtonGroup.add(combinedChartButton);
	chartButtonGroup.add(multiChartButton);
	placeholderChart.setPreferredSize(chartSize);
	combinedChartButton.addItemListener(new ChartButtonHandler(combinedChart));
	multiChartButton.addItemListener(new ChartButtonHandler(multiChart));
	etToggler.addItemListener(e -> {
        boolean selected;
        if (e.getStateChange() == ItemEvent.SELECTED) selected = true;
        else if (e.getStateChange() == ItemEvent.DESELECTED) selected = false;
        else return;
        chartData.setExtraTime(selected);
        userParameter.RatingChartFrame_ET = selected;
        if(combinedChart[0] != null) combinedChart[0].prepareChart();
        if(multiChart[0] != null) multiChart[0].prepareCharts();
    });
	controlsPanel.add(combinedChartButton);
	controlsPanel.add(multiChartButton);
	controlsPanel.add(etToggler);
	add(controlsPanel, BorderLayout.NORTH);
	if(userParameter.RatingChartFrame_Combined) combinedChartButton.doClick();
	else if(userParameter.RatingChartFrame_Multiple) multiChartButton.doClick();
	else add(placeholderChart, BorderLayout.CENTER);
	pack();
	setVisible(true);
	}
}
