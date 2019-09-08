package module.lineup;

import core.model.HOVerwaltung;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JCheckBox;

public final class MultipleRatingChartsPanel extends JPanel {

	private JPanel controlsPanel = new JPanel();
	private JCheckBox showHelpLines = new JCheckBox(HOVerwaltung.instance().getLanguageString("Hilflinien"));
	private JCheckBox showValues = new JCheckBox(HOVerwaltung.instance().getLanguageString("Beschriftung"));

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
	}
}
