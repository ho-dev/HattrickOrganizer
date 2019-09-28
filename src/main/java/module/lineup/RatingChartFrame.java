package module.lineup;

import core.model.HOVerwaltung;
import core.gui.HOMainFrame;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

class RatingChartFrame extends JFrame {

	private HOVerwaltung hov = HOVerwaltung.instance();
	private JRadioButton combinedChartButton = new JRadioButton(hov.getLanguageString("lineup.CombinedChart"));
	private JRadioButton multiChartButton = new JRadioButton(hov.getLanguageString("lineup.MultiChart"));
	private ButtonGroup chartButtonGroup = new ButtonGroup();
	private JCheckBox etToggler = new JCheckBox(hov.getLanguageString("lineup.ETToggler"));
	private JPanel controlsPanel = new JPanel();
	private JPanel placeholderChart = new JPanel();
	private RatingChartData chartData = new RatingChartData();
	private CombinedRatingChartPanel[] combinedChart = new CombinedRatingChartPanel[1];
	private MultipleRatingChartsPanel[] multiChart = new MultipleRatingChartsPanel[1];
	private Dimension chartSize = new Dimension(900,700);

	class ChartButtonHandler implements ItemListener {
		JPanel[] source;

		public ChartButtonHandler(JPanel[] source) {
			this.source = source;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
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
				remove(source[0]);
			}
		}
	};

	RatingChartFrame() {
		super(HOVerwaltung.instance().getLanguageString("RatingChartFrame"));
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
	etToggler.addItemListener(new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) chartData.setET(true);
			else if (e.getStateChange() == ItemEvent.DESELECTED) chartData.setET(false);
			if(combinedChart[0] != null) combinedChart[0].prepareChart();
			if(multiChart[0] != null) multiChart[0].prepareCharts();
		}
	});
	controlsPanel.add(combinedChartButton);
	controlsPanel.add(multiChartButton);
	controlsPanel.add(etToggler);
	add(controlsPanel, BorderLayout.NORTH);
	add(placeholderChart, BorderLayout.CENTER);
	pack();
	setVisible(true);
	}
}
