package core.gui.comp.tabbedPane;

import core.db.AbstractTable;

import java.awt.*;

public class HOTabInfo extends AbstractTable.Storable {
    private String tabbedPaneID;
    private String tabID;
    private int tabIndex;
    private Component component;

    /**
     * constructor is used by AbstractTable
     */
    public HOTabInfo() {}

    public HOTabInfo(String paneID, String tabID, int index) {
        this.tabbedPaneID = paneID;
        this.tabID = tabID;
        this.tabIndex = index;
    }

    public String getTabbedPaneID() {
        return tabbedPaneID;
    }

    public void setTabbedPaneID(String tabbedPaneID) {
        this.tabbedPaneID = tabbedPaneID;
    }

    public String getTabID() {
        return tabID;
    }

    public void setTabID(String tabID) {
        this.tabID = tabID;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

}
