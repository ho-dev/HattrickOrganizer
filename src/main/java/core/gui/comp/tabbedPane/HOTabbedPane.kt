package core.gui.comp.tabbedPane

import core.module.ModuleManager
import java.awt.Component
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.UIManager

class HOTabbedPane : JTabbedPane() {
    fun addTab(title: String?, component: Component?, closeIcon: Boolean) {
        var xOffset = 0
        super.addTab(title, component)
        if (closeIcon) {
            // Nimbus skin is the only one to put the icon on the right, which
            // we need to move away from title text
            if (UIManager.getLookAndFeel().name.equals("Nimbus", ignoreCase = true)) {
                xOffset = 6
            }
            setIconAt(componentCount - 1, TabCloseIcon(this, xOffset))
        }
    }

    fun showTab(moduleId: Int) {
        val module = ModuleManager.instance().getModule(moduleId)
        if (module.hasMainTab()) {
            val index = indexOfTab(module.getDescription())
            if (index == -1) {
                addTab(module.getDescription(), module.createTabPanel(), true)
                setSelectedIndex(tabCount - 1)
            } else {
                setSelectedIndex(index)
            }
        }
    }

    fun getModulePanel(moduleId: Int): JPanel? {
        val module = ModuleManager.instance().getModule(moduleId)
        if (module.hasMainTab()) {
            var index = indexOfTab(module.getDescription())
            if (index == -1) {
                addTab(module.getDescription(), module.createTabPanel(), true)
                setSelectedIndex(tabCount - 1)
                index = tabCount - 1
            }
            return getComponent(index) as JPanel
        }
        return null
    }

    fun isModuleTabVisible(moduleId: Int): Boolean {
        val module = ModuleManager.instance().getModule(moduleId)
        return indexOfTab(module.getDescription()) != -1
    }
}
