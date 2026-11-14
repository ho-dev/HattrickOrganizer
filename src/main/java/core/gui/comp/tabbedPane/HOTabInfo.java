package core.gui.comp.tabbedPane;

import core.db.AbstractTable;

import java.awt.*;

public class HOTabInfo extends AbstractTable.Storable {
    private String tabbedPaneId;
    private String tabId;
    private int tabIndex;
    private Component component;

    /**
     * constructor is used by AbstractTable
     */
    public HOTabInfo() {}

    public HOTabInfo(String tabbedPaneId, String tabId, int index) {
        this.tabbedPaneId = tabbedPaneId;
        this.tabId = tabId;
        this.tabIndex = index;
    }

    public String getTabbedPaneId() {
        return tabbedPaneId;
    }

    public void setTabbedPaneId(String tabbedPaneId) {
        this.tabbedPaneId = tabbedPaneId;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

}
