package core.gui.comp.tabbedPane;

import core.module.IModule;
import core.module.ModuleManager;
import java.awt.Component;
import java.util.Optional;
import javax.swing.JPanel;

public final class HOModulesTabbedPane extends HOTabbedPane {

    public HOModulesTabbedPane() {
        super("Modules");
    }

    @Override
    protected boolean haveTabsCloseIcons() {
        return true;
    }

    public int showTab(int moduleId) {
        IModule module = ModuleManager.instance().getModule(moduleId);
        if (module.hasMainTab()) {
            var index = getIndexOfTab(module.getKey());
            if (index.isEmpty()) {
                addTab(module.getKey(), module.createTabPanel());
                index = Optional.of(getTabCount() - 1);
            }
            setSelectedIndex(index.get());
            return index.get();
        }
        return -1;
    }

    public JPanel getModulePanel(int moduleId) {
        var index = showTab(moduleId);
        if (index != -1) {
            return (JPanel) getComponent(index);
        }
        return null;
    }

    public boolean isModuleTabVisible(int moduleId) {
        IModule module = ModuleManager.instance().getModule(moduleId);
        return getIndexOfTab(module.getKey()).isPresent();
    }

    @Override
    protected Optional<Component> findComponent(String tabID) {
        var module = ModuleManager.instance().findModule(tabID);
        return module.map(IModule::getTabPanel);
    }
}
