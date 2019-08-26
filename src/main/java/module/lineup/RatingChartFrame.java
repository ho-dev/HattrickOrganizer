package module.lineup;

import core.model.HOVerwaltung;
import core.gui.HOMainFrame;

import java.awt.Dimension;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

class RatingChartFrame extends JFrame {

	private JRadioButton singleChartButton = new JRadioButton(HOVerwaltung.instance().getLanguageString("lineup.SingleChart"));
	private JRadioButton multiChartButton = new JRadioButton(HOVerwaltung.instance().getLanguageString("lineup.MultiChart"));
	private ButtonGroup chartButtonGroup = new ButtonGroup();
	private JCheckBox etToggler = new JCheckBox(HOVerwaltung.instance().getLanguageString("lineup.ETToggler"));
	private JPanel controlsPanel = new JPanel();
	private JPanel placeholderChart = new JPanel();
	private Dimension chartSize = new Dimension(900,700);

	RatingChartFrame() {
		super(HOVerwaltung.instance().getLanguageString("RatingChartFrame"));
		this.setIconImage(HOMainFrame.instance().getIconImage());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		initComponents();
	}

	private void initComponents() {
	singleChartButton.setEnabled(false);
	multiChartButton.setEnabled(false);
	etToggler.setEnabled(false);
	chartButtonGroup.add(singleChartButton);
	chartButtonGroup.add(multiChartButton);
	placeholderChart.setPreferredSize(chartSize);
	controlsPanel.add(singleChartButton);
	controlsPanel.add(multiChartButton);
	controlsPanel.add(etToggler);
	add(controlsPanel, BorderLayout.NORTH);
	add(placeholderChart, BorderLayout.CENTER);
	pack();
	setVisible(true);
	}
}
