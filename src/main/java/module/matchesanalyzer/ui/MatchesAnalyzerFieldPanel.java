package module.matchesanalyzer.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;


public class MatchesAnalyzerFieldPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final MatchesAnalyzerLineupPanel pnlUpperHalf;
	private final MatchesAnalyzerLineupPanel pnlLowerHalf;

	public MatchesAnalyzerFieldPanel() {
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {0, 0, 0, 0, 0};
		gbl_panel.rowHeights = new int[] {0, 0, 0};
		gbl_panel.columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
		setLayout(gbl_panel);

		pnlUpperHalf = new MatchesAnalyzerLineupPanel(MatchesAnalyzerPanel.TOPDOWN);
		GridBagConstraints gbc_pnlUpperHalf = new GridBagConstraints();
		gbc_pnlUpperHalf.insets = new Insets(5, 5, 0, 5);
		gbc_pnlUpperHalf.weighty = 1.0;
		gbc_pnlUpperHalf.weightx = 1.0;
		gbc_pnlUpperHalf.fill = GridBagConstraints.BOTH;
		gbc_pnlUpperHalf.gridx = 0;
		gbc_pnlUpperHalf.gridy = 0;
		add(pnlUpperHalf, gbc_pnlUpperHalf);

		pnlLowerHalf = new MatchesAnalyzerLineupPanel(MatchesAnalyzerPanel.BOTTOMUP);
		GridBagConstraints gbc_pnlLowerHalf = new GridBagConstraints();
		gbc_pnlLowerHalf.insets = new Insets(0, 5, 5, 5);
		gbc_pnlLowerHalf.weighty = 1.0;
		gbc_pnlLowerHalf.weightx = 1.0;
		gbc_pnlLowerHalf.fill = GridBagConstraints.BOTH;
		gbc_pnlLowerHalf.gridx = 0;
		gbc_pnlLowerHalf.gridy = 1;
		add(pnlLowerHalf, gbc_pnlLowerHalf);
	}

	MatchesAnalyzerLineupPanel getUpperHalf() {
		return pnlUpperHalf;
	}

	MatchesAnalyzerLineupPanel getLowerHalf() {
		return pnlLowerHalf;
	}
}
