package tool;

import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import tool.arenasizer.ArenaSizerDialog;
import tool.export.CsvPlayerExport;
import tool.hrfExplorer.HrfExplorerDialog;
import tool.injury.InjuryDialog;
import tool.keepertool.KeeperToolDialog;
import tool.notepad.NotepadDialog;

import javax.swing.*;


public class ToolManager {
    private static final HOVerwaltung m_hov = HOVerwaltung.instance();
    private final JMenu m_jmToolsMenu = new JMenu(m_hov.getLanguageString("ls.menu.tools"));
    private final JMenuItem m_jmiInjuryCalculator = new JMenuItem(m_hov.getLanguageString("InjuryCalculator"));
    private final JMenuItem m_jmiKeeperTool = new JMenuItem(m_hov.getLanguageString("KeeperTool"));
    private final JMenuItem m_jmiNotepad = new JMenuItem(m_hov.getLanguageString("Notizen"));
    private final JMenuItem m_jmiCsvPlayerExporter = new JMenuItem(m_hov.getLanguageString("CSVExporter"));
    private final JMenuItem m_jmiArenaSizer = new JMenuItem(m_hov.getLanguageString("ArenaSizer"));
    private final JMenuItem m_jmiHrfExplorer = new JMenuItem(m_hov.getLanguageString("Tab_HRF-Explorer"));

    public JMenu getToolMenu() {
        m_jmiArenaSizer.addActionListener(e -> new ArenaSizerDialog(HOMainFrame.instance()).setVisible(true));
        m_jmToolsMenu.add(m_jmiArenaSizer);

        m_jmiKeeperTool.addActionListener(e -> new KeeperToolDialog(HOMainFrame.instance()).setVisible(true));
        m_jmToolsMenu.add(m_jmiKeeperTool);

        m_jmiInjuryCalculator.addActionListener(e -> new InjuryDialog(HOMainFrame.instance()).setVisible(true));
        m_jmToolsMenu.add(m_jmiInjuryCalculator);

        m_jmiCsvPlayerExporter.addActionListener(e -> {
            CsvPlayerExport csvExporter = new CsvPlayerExport();
            csvExporter.showSaveDialog();
        });
        m_jmToolsMenu.add(m_jmiCsvPlayerExporter);

        m_jmiNotepad.addActionListener(e -> {
            NotepadDialog notepad = new NotepadDialog(HOMainFrame.instance(), m_hov.getLanguageString("Notizen"));
            notepad.setVisible(true);
        });
        m_jmToolsMenu.add(m_jmiNotepad);

        m_jmiHrfExplorer.addActionListener(e -> new HrfExplorerDialog(HOMainFrame.instance()).setVisible(true));
        m_jmToolsMenu.add(m_jmiHrfExplorer);

        return m_jmToolsMenu;
    }
}
