package tool;

import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import tool.arenasizer.ArenaSizerDialog;
import tool.export.CsvPlayerExport;
import tool.export.XMLExporter;
import tool.hrfExplorer.HrfExplorerDialog;
import tool.injury.InjuryDialog;
import tool.keepertool.KeeperToolDialog;
import tool.notepad.NotepadDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class ToolManager implements ActionListener {
    private static HOVerwaltung m_hov = HOVerwaltung.instance();
    private final JMenu m_jmToolsMenu = new JMenu(m_hov.getLanguageString("ls.menu.tools"));
    private final JMenuItem m_jmiInjuryCalculator = new JMenuItem(m_hov.getLanguageString("InjuryCalculator"));
    private final JMenuItem m_jmiKeeperTool = new JMenuItem(m_hov.getLanguageString("KeeperTool"));
    private final JMenuItem m_jmiNotepad = new JMenuItem(m_hov.getLanguageString("Notizen"));
    private final JMenuItem m_jmiExporter = new JMenuItem(m_hov.getLanguageString("XMLExporter"));
    private final JMenuItem m_jmiCsvPlayerExporter = new JMenuItem(m_hov.getLanguageString("CSVExporter"));
    private final JMenuItem m_jmiArenaSizer = new JMenuItem(m_hov.getLanguageString("ArenaSizer"));
    private final JMenuItem m_jmiHrfExplorer = new JMenuItem(m_hov.getLanguageString("Tab_HRF-Explorer"));

    public JMenu getToolMenu() {
        m_jmiArenaSizer.addActionListener(this);
        m_jmToolsMenu.add(m_jmiArenaSizer);

        m_jmiKeeperTool.addActionListener(this);
        m_jmToolsMenu.add(m_jmiKeeperTool);

        m_jmiInjuryCalculator.addActionListener(this);
        m_jmToolsMenu.add(m_jmiInjuryCalculator);

        m_jmiExporter.addActionListener(this);
        m_jmToolsMenu.add(m_jmiExporter);

        m_jmiCsvPlayerExporter.addActionListener(this);
        m_jmToolsMenu.add(m_jmiCsvPlayerExporter);

        m_jmiNotepad.addActionListener(this);
        m_jmToolsMenu.add(m_jmiNotepad);

        m_jmiHrfExplorer.addActionListener(this);
        m_jmToolsMenu.add(m_jmiHrfExplorer);

        return m_jmToolsMenu;
    }

    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem) e.getSource();
        if (source == m_jmiKeeperTool) {
            new KeeperToolDialog(HOMainFrame.INSTANCE).setVisible(true);
        } else if (source.equals(m_jmiNotepad)) {
            NotepadDialog notepad = new NotepadDialog(HOMainFrame.INSTANCE, m_hov.getLanguageString("Notizen"));
            notepad.setVisible(true);
        } else if (source.equals(m_jmiExporter)) {
            XMLExporter exporter = new XMLExporter();
            exporter.doExport();
        } else if (source.equals(m_jmiCsvPlayerExporter)) {
            CsvPlayerExport csvExporter = new CsvPlayerExport();
            csvExporter.showSaveDialog();
        } else if (source.equals(m_jmiInjuryCalculator)) {
            new InjuryDialog(HOMainFrame.INSTANCE).setVisible(true);
        } else if (source.equals(m_jmiArenaSizer)) {
            new ArenaSizerDialog(HOMainFrame.INSTANCE).setVisible(true);
        } else if (source.equals(m_jmiHrfExplorer)) {
            new HrfExplorerDialog(HOMainFrame.INSTANCE).setVisible(true);
        }
    }
}
