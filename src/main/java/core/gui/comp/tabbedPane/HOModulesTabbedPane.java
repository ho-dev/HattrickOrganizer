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

    private Optional<Integer> getTab(int moduleId) {
        IModule module = ModuleManager.instance().getModule(moduleId);
        if (module.hasMainTab()) {
            var index = getIndexOfTab(module.getKey());
            if (index.isEmpty()) {
                addTab(module.getKey(), module.createTabPanel());
                index = Optional.of(getTabCount() - 1);
            }
            return index;
        }
        return Optional.empty();
    }

    public void showTab(int moduleId) {
        var index = getTab(moduleId);
        index.ifPresent(this::setSelectedIndex);
    }

    public JPanel getModulePanel(int moduleId) {
        var index = getTab(moduleId);
        return index.map(integer -> (JPanel) getComponent(integer)).orElse(null);
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
