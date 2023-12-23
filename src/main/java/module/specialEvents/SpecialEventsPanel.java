package module.specialEvents;

import core.gui.CursorToolkit;
import core.gui.HOMainFrame;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.model.UserColumnController;
import module.specialEvents.filter.Filter;
import module.specialEvents.filter.FilterHelper;
import java.awt.*;
import javax.swing.*;

public class SpecialEventsPanel extends LazyImagePanel {

	private SpecialEventsTable specialEventsTable;
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
//		updateRowHeights();
	}

	public void storeUserSettings() {
		if (this.specialEventsTable != null) specialEventsTable.storeUserSettings();
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
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, filterPanel, new JScrollPane(specialEventsTable));
		splitPane.setDividerSize(5);
		splitPane.setContinuousLayout(true);
		add(splitPane, BorderLayout.CENTER);
	}

	private void setTableData() {
		CursorToolkit.startWaitCursor(this);
		try {
			SpecialEventsDM specialEventsDM = new SpecialEventsDM();
			UserColumnController.instance().getSpecialEventsTableModel().setData(specialEventsDM.getRows(this.filter));
		} finally {
			CursorToolkit.stopWaitCursor(this);
		}
	}
}
