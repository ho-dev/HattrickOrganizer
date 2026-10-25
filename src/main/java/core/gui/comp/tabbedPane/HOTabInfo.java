package core.gui.comp.tabbedPane;

import core.db.AbstractTable;

public class HOTabInfo extends AbstractTable.Storable {
    private String tabbedPaneID;
    private String tabID;
    private int tabIndex;

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
