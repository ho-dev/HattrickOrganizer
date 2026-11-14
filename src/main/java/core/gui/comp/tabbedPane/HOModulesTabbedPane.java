package core.gui.comp.tabbedPane;

import core.module.IModule;
import core.module.ModuleManager;

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

    private int getTab(int moduleId) {
        IModule module = ModuleManager.instance().getModule(moduleId);
        if (module.hasMainTab()) {
            var index = getIndexOfTab(module.getKey());
            if (index.isEmpty()) {
                addTab(module.getKey(), module.createTabPanel());
                index = Optional.of(getTabCount() - 1);
            }
            return index.get();
        }
        return -1;
    }

    public void showTab(int moduleId) {
        var index = getTab(moduleId);
        if (index != -1) {
            setSelectedIndex(index);
        }
    }

    public JPanel getModulePanel(int moduleId) {
        var index = getTab(moduleId);
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
    protected Optional<HOTabComponent> findComponent(String tabId) {
        var module = ModuleManager.instance().findModule(tabId);
        return module.map(iModule -> new HOTabComponent(iModule.getDescription(), iModule.getTabPanel()));
    }
}
