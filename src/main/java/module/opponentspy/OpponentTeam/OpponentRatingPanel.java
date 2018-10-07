//package module.opponentspy.OpponentTeam;
//
//import core.model.HOModel;
//import core.model.UserParameter;
//import module.lineup.AufstellungsAssistentPanel;
//import module.lineup.AufstellungsDetailPanel;
//import module.lineup.Lineup;
//import module.lineup.LineupPositionsPanel;
//
//import java.awt.BorderLayout;
//
//import javax.swing.JScrollPane;
//import javax.swing.JSplitPane;
//
//public class OpponentRatingPanel extends module.lineup.LineupPanel {
//
//	/**
//	 *
//	 */
//	private static final long serialVersionUID = 1L;
//
//	LineupPositionsPanel lineupPositionPanel;
//	Lineup opponentLineup = new Lineup();
//	AufstellungsDetailPanel detailPanel;
//	AufstellungsAssistentPanel assistantPanel = new AufstellungsAssistentPanel();
//	JSplitPane verticalSplitPaneLow;
//	JSplitPane horizontalLeftSplitPane;
//	JSplitPane horizontalRightSplitPane;
//	JSplitPane verticalSplitPane;
//
//	public OpponentRatingPanel()
//	{
//		initComponents();
//	}
//
//	private void initComponents()
//	{
//		setLayout(new BorderLayout());
//
//		verticalSplitPaneLow = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
//
////		JTabbedPane tabbedPane = new JTabbedPane();
////		tabbedPane.addTab("", ThemeManager.getScaledIcon(HOIconName.BALL, 13, 13), new JScrollPane(
////				(Component)aufstellungsAssistentPanel));
////		tabbedPane.addTab("", ThemeManager.getIcon(HOIconName.DISK),
////				aufstellungsVergleichHistoryPanel);
//
//		lineupPositionPanel = new LineupPositionsPanel(this);
//		horizontalLeftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
//		horizontalLeftSplitPane.setLeftComponent(new JScrollPane(lineupPositionPanel));
////		horizontalLeftSplitPane.setRightComponent(initSpielerTabelle());
//
//		detailPanel = new AufstellungsDetailPanel();
//		horizontalRightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
//		horizontalRightSplitPane.setLeftComponent(new JScrollPane(detailPanel));
////		horizontalRightSplitPane.setRightComponent(tabbedPane);
//
//		verticalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
//		verticalSplitPane.setLeftComponent(horizontalLeftSplitPane);
//		verticalSplitPane.setRightComponent(horizontalRightSplitPane);
//
//		UserParameter param = UserParameter.instance();
//		verticalSplitPaneLow.setDividerLocation(param.aufstellungsPanel_verticalSplitPaneLow);
//		horizontalLeftSplitPane.setDividerLocation(param.aufstellungsPanel_horizontalLeftSplitPane);
//		horizontalRightSplitPane
//				.setDividerLocation(param.aufstellungsPanel_horizontalRightSplitPane);
//		verticalSplitPane.setDividerLocation(param.aufstellungsPanel_verticalSplitPane);
//
//		add(verticalSplitPane, BorderLayout.CENTER);
//	}
//
//	public void setModel(HOModel model) {
//		lineupPositionPanel.setModel(model);
//		lineupPositionPanel.refresh();
//
//	}
//}
