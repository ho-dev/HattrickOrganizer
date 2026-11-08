package core.gui.comp.tabbedPane;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.util.HOLogger;
import core.util.StreamUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class HOTabbedPane extends DraggableTabbedPane {

    private final String tabbedPaneID;
    private record HOTab(HOTabInfo info, Component component) {}
    private final List<HOTab> tabs = new ArrayList<>();

    public HOTabbedPane(String tabbedPaneID) {
        this.tabbedPaneID = tabbedPaneID;
        var dbManager = HOVerwaltung.instance().getPersistenceManager();
        var tabs = dbManager.loadHOTabInfo(tabbedPaneID);
        for (var tab : tabs) {
            var component =  findComponent(tab.getTabID());
            if (component.isPresent()) {
                addTab(tab.getTabID(), component.get(), tab.getTranslationKey());
            } else {
                HOLogger.instance().warning(getClass(), "Component " + tab.getTabID() + " is no longer part of HO!");
            }
        }
    }

    public void addTab(String tabID, Component tabComponent) {
        addTab(tabID, tabComponent, tabID);
    }

    public void addTab(String tabID, Component tabComponent, String titleTranslationKey) {
        if (tabs.stream().anyMatch(i -> i.info().getTabID().equals(tabID))) {
            // already added
            return;
        }
        tabs.add(new HOTab(new HOTabInfo(tabbedPaneID, tabID, titleTranslationKey, super.getTabCount()), tabComponent));
        super.addTab(TranslationFacility.tr(titleTranslationKey), tabComponent);
        if (haveTabsCloseIcons()) {
            var xOffset = 0;
            // Nimbus skin is the only one to put the icon on the right, which
            // we need to move away from title text
            if (UIManager.getLookAndFeel().getName().equalsIgnoreCase("Nimbus")) {
                xOffset = 6;
            }
            setIconAt(getComponentCount() - 1, new TabCloseIcon(this, xOffset));
        }
    }

    protected boolean haveTabsCloseIcons() {
        return false;
    }

    public int getIndexOfTab(String tabID) {
        for (int i = 0; i < getTabCount(); i++) {
            var component = getComponentAt(i);
            var tabInfo = StreamUtils.findOne(tabs.stream().filter(tab -> tab.component() == component));
            if (tabInfo.isPresent()) {
                if (tabInfo.get().info().getTabID().equals(tabID)) {
                    return i;
                }
            }
        }
        return -1; // Not found
    }

    public void storeHOTabInfo() {
        // Reset index settings
        tabs.forEach(tab -> tab.info().setTabIndex(-1));

        // Find current index settings
        for (int i = 0; i < getTabCount(); i++) {
            var component = getComponentAt(i);
            var hoTabInfo = StreamUtils.findOne(tabs.stream().filter(tab -> tab.component() == component));
            if (hoTabInfo.isPresent()) {
                hoTabInfo.get().info().setTabIndex(i);
            } else {
                HOLogger.instance().warning(getClass(), "Component not found " + component);
            }
        }

        // Store tab info
        DBManager.instance().storeHOTabInfo(tabs.stream().map(HOTab::info).filter(info -> info.getTabIndex() != -1).toList());
        DBManager.instance().deleteHOTabInfo(tabs.stream().map(HOTab::info).filter(info -> info.getTabIndex() == -1).toList());
    }

    protected abstract Optional<Component> findComponent(String tabID);
}
