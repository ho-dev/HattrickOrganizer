package tool;

import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.HOLogger;
import module.lineup.Lineup;
import module.teamAnalyzer.vo.MatchRating;
import tool.arenasizer.ArenaSizerDialog;
import tool.export.CsvPlayerExport;
import tool.export.XMLExporter;
import tool.hrfExplorer.HrfExplorerDialog;
import tool.injury.InjuryDialog;
import tool.keepertool.KeeperToolDialog;
import tool.notepad.NotepadDialog;
import tool.pluginFeedback.PluginFeedback;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private final JMenuItem m_jmiPluginFeedback = new JMenuItem("PluginFeedback");


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

        m_jmiPluginFeedback.addActionListener(this);
        m_jmToolsMenu.add(m_jmiPluginFeedback);

        return m_jmToolsMenu;
    }

    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem) e.getSource();
        if (source == m_jmiKeeperTool) {
            new KeeperToolDialog(HOMainFrame.instance()).setVisible(true);
        } else if (source.equals(m_jmiNotepad)) {
            NotepadDialog notepad = new NotepadDialog(HOMainFrame.instance(), m_hov.getLanguageString("Notizen"));
            notepad.setVisible(true);
        } else if (source.equals(m_jmiExporter)) {
            XMLExporter exporter = new XMLExporter();
            exporter.doExport();
        } else if (source.equals(m_jmiCsvPlayerExporter)) {
            CsvPlayerExport csvExporter = new CsvPlayerExport();
            csvExporter.showSaveDialog();
        } else if (source.equals(m_jmiInjuryCalculator)) {
            new InjuryDialog(HOMainFrame.instance()).setVisible(true);
        } else if (source.equals(m_jmiArenaSizer)) {
            new ArenaSizerDialog(HOMainFrame.instance()).setVisible(true);
        } else if (source.equals(m_jmiHrfExplorer)) {
            new HrfExplorerDialog(HOMainFrame.instance()).setVisible(true);
        } else if (source.equals(m_jmiPluginFeedback)) {
            // todo Remove after integration with real PluginFeedback called
            // Create example data
            Lineup lineup = HOVerwaltung.instance().getModel().getLineup();

            MatchRating rating = new MatchRating();
            rating.setLeftDefense(1.11);
            rating.setCentralDefense(2.22);
            rating.setRightDefense(3.33);
            rating.setMidfield(4.44);
            rating.setLeftAttack(5.55);
            rating.setCentralAttack(6.66);
            rating.setRightAttack(7.77);

            PluginFeedback pluginFeedback = new PluginFeedback();
            String message = "[" + pluginFeedback.getHoToken() + "]";
            try {
                String result = pluginFeedback.sendFeedbackToServer(lineup, rating);
                message += " Dati inviati al server, grazie! (Risultato:" + result + ")";
                HOLogger.instance().info(getClass(), message);
                if (message.length() > 200) {
                    message = message.substring(0, 200);
                }
                JOptionPane.showMessageDialog(null, message);
            } catch (IOException ex) {
                message += " Errore nell'invio dei dati al server. (Error Message:" + ex.getMessage() + ")"; //todo remove getmessage
                HOLogger.instance().error(getClass(), ex);
                ex.printStackTrace();
                if (message.length() > 200) {
                    message = message.substring(0, 200);
                }
                JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                message += " Errore negli input. (Error Message:" + ex.getMessage() + ")"; //todo remove getmessage
                HOLogger.instance().error(getClass(), ex);
                ex.printStackTrace();
                if (message.length() > 200) {
                    message = message.substring(0, 200);
                }
                JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
