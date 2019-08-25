package module.lineup;

import core.model.HOVerwaltung;
import core.gui.HOMainFrame;

import javax.swing.JFrame;

class RatingChartFrame extends JFrame {

	RatingChartFrame() {
		super(HOVerwaltung.instance().getLanguageString("RatingChartFrame"));
		this.setIconImage(HOMainFrame.instance().getIconImage());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initComponents();
	}

	private void initComponents() {
	setVisible(true);
	}
}
