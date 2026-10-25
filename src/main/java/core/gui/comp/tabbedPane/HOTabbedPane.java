package core.gui.comp.tabbedPane;

import core.db.DBManager;
import core.db.HOTabInfoTable;

import java.awt.*;
import java.util.List;

public abstract class HOTabbedPane extends DraggableTabbedPane {

    private List<HOTabInfo> tabs;

    public HOTabbedPane(String tabbedPaneID) {
        tabs = DBManager.instance().loadHOTabInfo(tabbedPaneID);
        for ( var tab : tabs ) {
            this.add(getTabComponent(tab.getTabID()));
        }
    }

    public void storeHOTabInfo(){
        var tabs = getTabs()
    }

    protected abstract Component getTabComponent(String tabID);

    private List<HOTabInfo> getTabs(){ return tabs;}


}
