package core.gui.comp.tabbedPane;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.util.HOLogger;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class HOTabbedPane extends DraggableTabbedPane {

    private final String tabbedPaneID;
    private final List<Pair<HOTabInfo, Component>> tabs = new ArrayList<>();

    public HOTabbedPane(String tabbedPaneID) {
        this.tabbedPaneID = tabbedPaneID;
        var dbManager = HOVerwaltung.instance().getPersistenceManager();
        var tabs = dbManager.loadHOTabInfo(tabbedPaneID);
        for (var tab : tabs) {
            var component =  getComponent(tab.getTabID());
            if (component != null) {
                addTab(tab.getTabID(), component, tab.getTranslationKey());
            } else {
                HOLogger.instance().warning(getClass(), "Component " + tab.getTabID() + " is no longer part of HO!");
            }
        }
    }

    public void addTab(String tabID, Component tabComponent) {
        addTab(tabID, tabComponent, tabID);
    }

    public void addTab(String tabID, Component tabComponent, String titleTranslationKey) {
        if (tabs.stream().anyMatch(i -> i.getKey().getTabID().equals(tabID))) {
            // already added
            return;
        }
        tabs.add(Pair.of(new HOTabInfo(tabbedPaneID, tabID, titleTranslationKey, super.getTabCount()), tabComponent));
        super.addTab(TranslationFacility.tr(titleTranslationKey), tabComponent);
        if (isCloseIcon()) {
            var xOffset = 0;
            // Nimbus skin is the only one to put the icon on the right, which
            // we need to move away from title text
            if (UIManager.getLookAndFeel().getName().equalsIgnoreCase("Nimbus")) {
                xOffset = 6;
            }
            setIconAt(getComponentCount() - 1, new TabCloseIcon(this, xOffset));
        }

    }

    protected boolean isCloseIcon() {
        return false;
    }

    public int getIndexOfTab(String tabID) {
        for (int i = 0; i < getTabCount(); i++) {
            var component = getComponentAt(i);
            var tabInfo = tabs.stream().filter(pair -> pair.getValue().equals(component)).findFirst();
            if (tabInfo.isPresent()) {
                if (tabInfo.get().getKey().getTabID().equals(tabID)) {
                    return i;
                }
            }
        }
        return -1; // Not found
    }

    public void storeHOTabInfo() {
        tabs.forEach(i -> i.getKey().setTabIndex(-1));

        for (int i = 0; i < getTabCount(); i++) {
            var component = getComponentAt(i);
            var hoTabInfo = tabs.stream().filter(pair -> pair.getValue().equals(component)).findFirst();
            if (hoTabInfo.isPresent()) {
                hoTabInfo.get().getKey().setTabIndex(i);
            } else {
                HOLogger.instance().warning(getClass(), "Component not found " + component);
            }
        }

        // Store tab info
        DBManager.instance().storeHOTabInfo(tabs.stream().filter(i -> i.getKey().getTabIndex() != -1).map(Pair::getKey).toList());
        DBManager.instance().deleteHOTabInfo(tabs.stream().filter(i -> i.getKey().getTabIndex() == -1).map(Pair::getKey).toList());
    }

    protected abstract Component getComponent(String tabID);
}
