package core.gui.comp.tabbedPane;

import core.db.DBManager;
import core.model.TranslationFacility;
import core.util.HOLogger;

import java.awt.*;
import java.util.List;

public abstract class HOTabbedPane extends DraggableTabbedPane {

    private String tabbedPaneID;
    private List<HOTabInfo> tabs;

    public HOTabbedPane(String tabbedPaneID) {
        this.tabbedPaneID = tabbedPaneID;
        tabs = DBManager.instance().loadHOTabInfo(tabbedPaneID);
        for (var tab : tabs) {
            super.add(getTabComponent(tab.getTabID()));
        }
    }

    public void addTab(String tabID, Component tabComponent){
        tabs.add(new HOTabInfo(tabbedPaneID, tabID, super.getTabCount()));
        super.addTab(TranslationFacility.tr(tabID), tabComponent);
    }

    public void storeHOTabInfo() {
        var tabs = getTabs();
        for (var tab : tabs) {
            tab.setTabIndex(-1);
        }
        var components = getComponents();
        // Update tab indexes
        int index = 0;
        for (var component : components) {
            var found = false;
            for (var tab : tabs) {
                if (getTabComponent(tab.getTabID()).equals(component)) {
                    tab.setTabIndex(index);
                    found = true;
                }
            }
            if (!found) {
                HOLogger.instance().warning(getClass(), "Component not found " + component);
            }
            index++;
        }
        // Store tab info
        DBManager.instance().storeHOTabInfo(tabs.stream().filter(i -> i.getTabIndex() != -1).toList());
        DBManager.instance().deleteHOTabInfo(tabs.stream().filter(i -> i.getTabIndex() == -1).toList());
    }

    protected abstract Component getTabComponent(String tabID);

    private List<HOTabInfo> getTabs(){ return tabs;}


}
