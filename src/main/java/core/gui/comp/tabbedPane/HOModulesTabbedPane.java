package core.gui.comp.tabbedPane;

import core.module.IModule;
import core.module.ModuleManager;
import java.awt.Component;
import javax.swing.JPanel;

public final class HOModulesTabbedPane extends HOTabbedPane {

	public HOModulesTabbedPane() {
		super("Modules");
	}

    @Override
    protected boolean isCloseIcon(){
        return true;
    }

	public void showTab(int moduleId) {
		IModule module = ModuleManager.instance().getModule(moduleId);
		if (module.hasMainTab()) {
            var index = getIndexOfTab(module.getKey());
 			if (index == -1) {
				addTab(module.getKey(), module.createTabPanel());
				setSelectedIndex(getTabCount() - 1);
			} else {
				setSelectedIndex(index);
			}
		}
	}

    public JPanel getModulePanel(int moduleId) {
		IModule module = ModuleManager.instance().getModule(moduleId);
		if (module.hasMainTab()) {
            var index = getIndexOfTab(module.getKey());
			if (index == -1) {
				addTab(module.getKey(), module.createTabPanel());
				setSelectedIndex(getTabCount() - 1);
				index = getTabCount() - 1;
			}
			return (JPanel) getComponent(index);
		}
		return null;
	}

	public boolean isModuleTabVisible(int moduleId) {
		IModule module = ModuleManager.instance().getModule(moduleId);
		return indexOfTab(module.getDescription()) != -1;
	}

    @Override
    protected Component getComponent(String tabID) {
        var module = ModuleManager.instance().findModule(tabID);
        return module.<Component>map(IModule::getTabPanel).orElse(null);
    }
}
