package module.teamAnalyzer.ui;

import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyPanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.module.config.ModuleConfig;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.manager.ReportManager;
import module.teamAnalyzer.ui.controller.SimButtonListener;
import module.teamAnalyzer.vo.Filter;
import module.teamAnalyzer.vo.TeamLineup;
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
		setLayout(new BorderLayout());

		JPanel buttonPanel = new ImagePanel(new BorderLayout());
		simButton = new JButton(HOVerwaltung.instance().getLanguageString("Simulate"));
		buttonPanel.add(simButton, BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ratingPanel, buttonPanel);
		splitPane.setDividerSize(1);
		splitPane.setResizeWeight(1);
		splitPane.setDividerLocation(UserParameter.instance().teamAnalyzer_LowerLefSplitPane);
		splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new DividerListener(DividerListener.teamAnalyzer_LowerLefSplitPane));

		JSplitPane splitPaneLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT, filterPanel, splitPane);
		splitPaneLeft.setDividerLocation(UserParameter.instance().teamAnalyzer_UpperLeftSplitPane);
		splitPaneLeft.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new DividerListener(DividerListener.teamAnalyzer_UpperLeftSplitPane));

		JSplitPane splitPaneUpper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPaneLeft,
				mainPanel);
		splitPaneUpper.setDividerLocation(UserParameter.instance().teamAnalyzer_MainSplitPane);
		splitPaneUpper.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new DividerListener(DividerListener.teamAnalyzer_MainSplitPane));

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

	/**
	 * Returns the recap panel
	 * 
	 * @return
	 */
	RecapPanel getRecapPanel() {
		return recapPanel;
	}

	public void reload() {
		TeamLineup lineup = ReportManager.getLineup();
		getFilterPanel().reload();

		getMainPanel().reload(lineup, 0, 0);
		getRecapPanel().reload(lineup);
		getRatingPanel().reload(lineup);

		if (ModuleConfig.instance().getBoolean(SystemManager.ISLINEUP)) {
			this.simButton.setVisible(true);
		} else {
			this.simButton.setVisible(false);
		}
	}
}
