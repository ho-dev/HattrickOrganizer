package tool

import core.gui.HOMainFrame
import core.model.HOVerwaltung
import tool.arenasizer.ArenaSizerDialog
import tool.export.CsvPlayerExport
import tool.export.XMLExporter
import tool.hrfExplorer.HrfExplorerDialog
import tool.injury.InjuryDialog
import tool.keepertool.KeeperToolDialog
import tool.notepad.NotepadDialog
import javax.swing.JMenu
import javax.swing.JMenuItem

class ToolManager {
    val toolMenu: JMenu
        get() {
            val hoAdmin = HOVerwaltung.instance()

            val jmToolsMenu = JMenu(hoAdmin.getLanguageString("ls.menu.tools"))
            val jmiArenaSizer = JMenuItem(hoAdmin.getLanguageString("ArenaSizer"))
            jmiArenaSizer.addActionListener { _ -> ArenaSizerDialog(HOMainFrame).isVisible = true }
            jmToolsMenu.add(jmiArenaSizer)

            val jmiKeeperTool = JMenuItem(hoAdmin.getLanguageString("KeeperTool"))
            jmiKeeperTool.addActionListener { _ -> KeeperToolDialog(HOMainFrame).isVisible = true }
            jmToolsMenu.add(jmiKeeperTool)

            val jmiInjuryCalculator = JMenuItem(hoAdmin.getLanguageString("InjuryCalculator"))
            jmiInjuryCalculator.addActionListener { _ -> InjuryDialog(HOMainFrame).isVisible = true }
            jmToolsMenu.add(jmiInjuryCalculator)

            val jmiExporter = JMenuItem(hoAdmin.getLanguageString("XMLExporter"))
            jmiExporter.addActionListener { _ ->
                val exporter = XMLExporter()
                exporter.doExport()
            }
            jmToolsMenu.add(jmiExporter)

            val jmiCsvPlayerExporter = JMenuItem(hoAdmin.getLanguageString("CSVExporter"))
            jmiCsvPlayerExporter.addActionListener { _ ->
                val csvExporter = CsvPlayerExport()
                csvExporter.showSaveDialog()
            }
            jmToolsMenu.add(jmiCsvPlayerExporter)

            val jmiNotepad = JMenuItem(hoAdmin.getLanguageString("Notizen"))
            jmiNotepad.addActionListener { _ ->
                val notepad = NotepadDialog(HOMainFrame, hoAdmin.getLanguageString("Notizen"))
                notepad.isVisible = true
            }
            jmToolsMenu.add(jmiNotepad)

            val jmiHrfExplorer = JMenuItem(hoAdmin.getLanguageString("Tab_HRF-Explorer"))
            jmiHrfExplorer.addActionListener { _ -> HrfExplorerDialog(HOMainFrame).isVisible = true }
            jmToolsMenu.add(jmiHrfExplorer)

            return jmToolsMenu
        }
}
