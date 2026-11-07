package core.gui.comp.tabbedPane;

import core.db.AbstractTable;

public class HOTabInfo extends AbstractTable.Storable {
    private String tabbedPaneID;
    private String tabID;
    private String translationKey;
    private int tabIndex;

    public HOTabInfo(){}

    public HOTabInfo(String paneID, String tabID, String translationKey, int index){
        this.tabbedPaneID = paneID;
        this.tabID = tabID;
        this.translationKey = translationKey;
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

    public String getTranslationKey() {
        return translationKey;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }
}
