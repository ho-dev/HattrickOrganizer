package module.matchesanalyzer.ui;

import core.gui.comp.panel.LazyPanel;
import core.util.HOLogger;
import module.matchesanalyzer.data.MatchesAnalyzerTeam;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;


public class MatchesAnalyzerPanel extends LazyPanel {
	private static final long serialVersionUID = 1L;

	public static final int TOPDOWN = 0;
	public static final int BOTTOMUP = 1;

	private final MatchesAnalyzerTeam team;

	public MatchesAnalyzerPanel(MatchesAnalyzerTeam team) {
		this.team = team;
	}

	@Override
	protected void initialize() {
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {0, 0, 0, 0, 0};
		gbl_panel.rowHeights = new int[] {0, 0, 0};
		gbl_panel.columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
		setLayout(gbl_panel);

		MatchesAnalyzerTeamPanel pnlUpperTeam = new MatchesAnalyzerTeamPanel(team, MatchesAnalyzerPanel.TOPDOWN);
		GridBagConstraints gbc_pnlUpperTeam = new GridBagConstraints();
		gbc_pnlUpperTeam.insets = new Insets(5, 5, 5, 5);
		gbc_pnlUpperTeam.weighty = 1.0;
		gbc_pnlUpperTeam.weightx = 0.0;
		gbc_pnlUpperTeam.fill = GridBagConstraints.BOTH;
		gbc_pnlUpperTeam.gridx = 0;
		gbc_pnlUpperTeam.gridy = 0;
		add(pnlUpperTeam, gbc_pnlUpperTeam);

		MatchesAnalyzerTeamPanel pnlLowerTeam = new MatchesAnalyzerTeamPanel(team, MatchesAnalyzerPanel.BOTTOMUP);
		GridBagConstraints gbc_pnlLowerTeam = new GridBagConstraints();
		gbc_pnlLowerTeam.insets = new Insets(0, 5, 5, 5);
		gbc_pnlLowerTeam.weighty = 1.0;
		gbc_pnlLowerTeam.weightx = 0.0;
		gbc_pnlLowerTeam.fill = GridBagConstraints.BOTH;
		gbc_pnlLowerTeam.gridx = 0;
		gbc_pnlLowerTeam.gridy = 1;
		add(pnlLowerTeam, gbc_pnlLowerTeam);

		MatchesAnalyzerFieldPanel pnlField = new MatchesAnalyzerFieldPanel();
		GridBagConstraints gbc_pnlField = new GridBagConstraints();
		gbc_pnlField.insets = new Insets(5, 5, 5, 5);
		gbc_pnlField.gridheight = 2;
		gbc_pnlField.weighty = 1.0;
		gbc_pnlField.weightx = 1.0;
		gbc_pnlField.fill = GridBagConstraints.BOTH;
		gbc_pnlField.gridx = 1;
		gbc_pnlField.gridy = 0;
		add(pnlField, gbc_pnlField);

		pnlUpperTeam.addObserver(pnlField.getUpperHalf());
		pnlLowerTeam.addObserver(pnlField.getLowerHalf());
		HOLogger.instance().debug(MatchesAnalyzerPanel.class, "[initialize] Added observers");
		
		registerRefreshable(true);
		setNeedsRefresh(false);
	}

	@Override
	protected void update() {}

}
