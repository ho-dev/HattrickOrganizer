package module.specialEvents;

import static module.specialEvents.SpecialEventsTableModel.MATCH_DATE_TYPE_COLUMN;
import static module.specialEvents.SpecialEventsTableModel.AWAYTACTICCOLUMN;
import static module.specialEvents.SpecialEventsTableModel.AWAYTEAMCOLUMN;
import static module.specialEvents.SpecialEventsTableModel.EVENTTYPCOLUMN;
import static module.specialEvents.SpecialEventsTableModel.HOMETACTICCOLUMN;
import static module.specialEvents.SpecialEventsTableModel.HOMETEAMCOLUMN;
import static module.specialEvents.SpecialEventsTableModel.MINUTECOLUMN;
import static module.specialEvents.SpecialEventsTableModel.PLAYER_NAME_COLUMN;
import static module.specialEvents.SpecialEventsTableModel.EVENTTEXTCOLUMN;
import core.gui.CursorToolkit;
import core.gui.HOMainFrame;
import core.gui.comp.panel.LazyImagePanel;
import module.specialEvents.filter.Filter;
import module.specialEvents.filter.FilterHelper;
import module.specialEvents.table.DefaultSETableCellRenderer;
import module.specialEvents.table.EventTypeTableCellRenderer;
import module.specialEvents.table.MatchDateTypeTableCellRenderer;
import module.specialEvents.table.PlayerNameTableCellRenderer;
import module.specialEvents.table.TacticsTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class SpecialEventsPanel extends LazyImagePanel {

	private static SpecialEventsTable specialEventsTable;
	private Filter filter;

	@Override
	protected void initialize() {
		initComponents();
		registerRefreshable(true);
		setNeedsRefresh(true);
	}

	@Override
	protected void update() {
		setTableData();
		updateRowHeights();
	}

	private void initComponents() {
		this.filter = new Filter();
		FilterHelper.loadSettings(this.filter);
		setLayout(new BorderLayout());

		this.filter.addFilterChangeListener(evt -> update());

		HOMainFrame.instance().addApplicationClosingListener(() -> FilterHelper.saveSettings(filter));

		JPanel filterPanel = new FilterPanel(filter);
		specialEventsTable = new SpecialEventsTable();
		specialEventsTable.getTableHeader().setReorderingAllowed(false);
		SpecialEventsTableModel tblModel = new SpecialEventsTableModel();

		specialEventsTable.setModel(tblModel);
		TableColumnModel columnModel = specialEventsTable.getColumnModel();
		specialEventsTable.setDefaultRenderer(Object.class, new DefaultSETableCellRenderer());
		TacticsTableCellRenderer tacticsTableCellRenderer = new TacticsTableCellRenderer();

		TableColumn matchDateTypeColumn = columnModel.getColumn(MATCH_DATE_TYPE_COLUMN);
		matchDateTypeColumn.setPreferredWidth(125);
		matchDateTypeColumn.setCellRenderer(new MatchDateTypeTableCellRenderer());

		TableColumn homeTacticColumn = columnModel.getColumn(HOMETACTICCOLUMN);
		homeTacticColumn.setPreferredWidth(37);
		homeTacticColumn.setCellRenderer(tacticsTableCellRenderer);

		TableColumn homeTeamColumn = columnModel.getColumn(HOMETEAMCOLUMN);
		homeTeamColumn.setPreferredWidth(150);

		TableColumn resultColumn = columnModel.getColumn(SpecialEventsTableModel.RESULTCOLUMN);
		resultColumn.setPreferredWidth(40);

		TableColumn awayTeamColumn = columnModel.getColumn(AWAYTEAMCOLUMN);
		awayTeamColumn.setPreferredWidth(150);

		TableColumn awayTacticColumn = columnModel.getColumn(AWAYTACTICCOLUMN);
		awayTacticColumn.setPreferredWidth(37);
		awayTacticColumn.setCellRenderer(tacticsTableCellRenderer);

		TableColumn minuteColumn = columnModel.getColumn(MINUTECOLUMN);
		minuteColumn.setPreferredWidth(27);

		TableColumn eventTypeColumn = columnModel.getColumn(EVENTTYPCOLUMN);
		eventTypeColumn.setMaxWidth(23);
		eventTypeColumn.setPreferredWidth(23);
		eventTypeColumn.setCellRenderer(new EventTypeTableCellRenderer());

		TableColumn settExtColumn = columnModel.getColumn(EVENTTEXTCOLUMN);
		settExtColumn.setPreferredWidth(270);

		TableColumn nameColumn = columnModel.getColumn(PLAYER_NAME_COLUMN);
		nameColumn.setPreferredWidth(200);
		nameColumn.setCellRenderer(new PlayerNameTableCellRenderer());

		specialEventsTable.addMouseListener(new MouseAdapter() {
								  public void mousePressed(MouseEvent me) {
									  JTable table = (JTable) me.getSource();
									  Point p = me.getPoint();
									  int col = table.columnAtPoint(p);
									  if (col == MATCH_DATE_TYPE_COLUMN) {
										  // your valueChanged overridden method
										  int matchID = 18113459; // find how to get it for real
										  HOMainFrame.instance().showMatch(matchID); // only if match ID not null
											  }
										  }
									  });

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, filterPanel, new JScrollPane(specialEventsTable));
		splitPane.setDividerSize(5);
		splitPane.setContinuousLayout(true);
		add(splitPane, BorderLayout.CENTER);
	}

	private void setTableData() {
		CursorToolkit.startWaitCursor(this);
		try {
			SpecialEventsDM specialEventsDM = new SpecialEventsDM();
			((SpecialEventsTableModel) specialEventsTable.getModel()).setData(specialEventsDM.getRows(this.filter));
		} finally {
			CursorToolkit.stopWaitCursor(this);
		}
	}

	private void updateRowHeights()
	{
		int rowHeight = 20;

		for (int iRow = 0; iRow < specialEventsTable.getRowCount(); iRow++)
		{
			TableCellRenderer renderer = specialEventsTable.getCellRenderer(iRow, EVENTTYPCOLUMN);
			Component comp = specialEventsTable.prepareRenderer(renderer, iRow, EVENTTYPCOLUMN);
			rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
			specialEventsTable.setRowHeight(iRow, rowHeight);
		}
	}

}
