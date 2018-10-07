package core.gui.comp.tabbedPane;

import core.module.IModule;
import core.module.ModuleManager;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

public final class HOTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = 1L;

	public HOTabbedPane() {
		super();
	}

	public void addTab(String title, Component component, boolean closeIcon) {
		int xOffset = 0;
		super.addTab(title, component);
		if (closeIcon) {
			// Nimbus skin is the only one to put the icon on the right, which
			// we need to move away from title text
			if (UIManager.getLookAndFeel().getName().equalsIgnoreCase("Nimbus")) {
				xOffset = 6;
			}
			setIconAt(getComponentCount() - 1, new TabCloseIcon(this, xOffset));
		}
	}

	public void showTab(int moduleId) {
		IModule module = ModuleManager.instance().getModule(moduleId);
		if (module.hasMainTab()) {
			int index = indexOfTab(module.getDescription());
			if (index == -1) {
				addTab(module.getDescription(), module.createTabPanel(), true);
				setSelectedIndex(getTabCount() - 1);
			} else {
				setSelectedIndex(index);
			}
		}
	}

	public JPanel getModulePanel(int moduleId) {
		IModule module = ModuleManager.instance().getModule(moduleId);
		if (module.hasMainTab()) {
			int index = indexOfTab(module.getDescription());
			if (index == -1) {
				addTab(module.getDescription(), module.createTabPanel(), true);
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
}
