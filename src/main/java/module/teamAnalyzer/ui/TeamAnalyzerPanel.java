package module.teamAnalyzer.ui;

import core.gui.comp.panel.LazyPanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.module.config.ModuleConfig;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.report.TeamReport;
import module.teamAnalyzer.ui.controller.SimButtonListener;
import module.teamAnalyzer.vo.Filter;
import module.training.ui.comp.DividerListener;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class TeamAnalyzerPanel extends LazyPanel {

	/** The filters */
	public static Filter filter = new Filter();
	private static final long serialVersionUID = 1L;
	private JButton simButton;
	private RecapPanel recapPanel;
	private MainPanel mainPanel;
	private FilterPanel filterPanel;
	private RatingPanel ratingPanel;
	private SpecialEventsPanel specialEventsPanel;

	@Override
	protected void initialize() {
		SystemManager.initialize(this);
		initComponents();
		addListeners();
		registerRefreshable(true);
		SystemManager.refreshData();
		setNeedsRefresh(false);
	}

	@Override
	protected void update() {
		SystemManager.refreshData();
	}

	private void addListeners() {
		simButton.addActionListener(new SimButtonListener(mainPanel.getMyTeamLineupPanel(),
				mainPanel.getOpponentTeamLineupPanel(), recapPanel));
	}

	private void initComponents() {
		filterPanel = new FilterPanel();
		recapPanel = new RecapPanel();
		mainPanel = new MainPanel();
		ratingPanel = new RatingPanel();
		specialEventsPanel = new SpecialEventsPanel();

		setLayout(new BorderLayout());

		simButton = new JButton(HOVerwaltung.instance().getLanguageString("Simulate"));

		JSplitPane splitPaneSub = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ratingPanel, specialEventsPanel);
		splitPaneSub.setDividerLocation(UserParameter.instance().teamAnalyzer_RatingPanelSplitPane);
		splitPaneSub.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new DividerListener(DividerListener.teamAnalyzer_RatingPanelSplitPane));

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, filterPanel, splitPaneSub);
		splitPane.setDividerLocation(UserParameter.instance().teamAnalyzer_FilterPanelSplitPane);
		splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new DividerListener(DividerListener.teamAnalyzer_FilterPanelSplitPane));

		JPanel mainLeftPanel = new JPanel();
		mainLeftPanel.setLayout(new BorderLayout());
		mainLeftPanel.add(splitPane, BorderLayout.CENTER);
		mainLeftPanel.add(simButton, BorderLayout.SOUTH);

		JSplitPane splitPaneUpper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainLeftPanel,
				mainPanel);
		splitPaneUpper.setDividerLocation(UserParameter.instance().teamAnalyzer_MainPanelSplitPane);
		splitPaneUpper.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new DividerListener(DividerListener.teamAnalyzer_MainPanelSplitPane));

		JSplitPane splitPaneMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPaneUpper,
				recapPanel);
		splitPaneMain.setDividerLocation(UserParameter.instance().teamAnalyzer_BottomSplitPane);
		splitPaneMain.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new DividerListener(DividerListener.teamAnalyzer_BottomSplitPane));
		add(splitPaneMain, BorderLayout.CENTER);
	}

	public MainPanel getMainPanel() {
		return mainPanel;
	}

	/**
	 * Returns the Filter Panel
	 * 
	 * @return
	 */
	public FilterPanel getFilterPanel() {
		return filterPanel;
	}

	/**
	 * Returns the rating panel
	 * 
	 * @return
	 */
	public RatingPanel getRatingPanel() {
		return ratingPanel;
	}

	public SpecialEventsPanel getSpecialEventsPanel(){
		return specialEventsPanel;
	}

	/**
	 * Returns the recap panel
	 * 
	 * @return
	 */
	RecapPanel getRecapPanel() {
		return recapPanel;
	}

	public void reload() {
		getFilterPanel().reload();
		TeamReport teamReport = SystemManager.getTeamReport();
		getMainPanel().reload(teamReport.getLineup(0), 0, 0);
		getRecapPanel().reload(teamReport);
		getRatingPanel().reload(teamReport.getLineup(0));

		getSpecialEventsPanel().reload(teamReport.getLineup(0));

		if (ModuleConfig.instance().getBoolean(SystemManager.ISLINEUP)) {
			this.simButton.setVisible(true);
		} else {
			this.simButton.setVisible(false);
		}
	}
}
