package module.lineup;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.Weather;
import module.lineup.assistant.ILineupAssistantPanel;
import module.lineup.assistant.LineupAssistantPanel;
import module.lineup.ratings.LineupRatingPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Create the panel displaying ratings, settings and assistant
 */
public class LineupRatingAssistantPanel extends JPanel {
    private LineupRatingPanel lineupRatingPanel;
    private LineupSettingsPanel lineupSettingsPanel;
    private LineupAssistantPanel lineupAssistantPanel;

    public final LineupRatingPanel getLineupRatingPanel(){ return lineupRatingPanel;}
    public final LineupSettingsPanel getLineupSettingsPanel(){ return lineupSettingsPanel;}
    public final ILineupAssistantPanel getLineupAssistantPanel(){return lineupAssistantPanel;}

    public LineupRatingAssistantPanel() {

        initComponents();
    }


    private void initComponents() {
        lineupRatingPanel = new LineupRatingPanel();
        lineupSettingsPanel = new LineupSettingsPanel();
        final LineupsComparisonHistoryPanel lineupsComparisonHistoryPanel = new LineupsComparisonHistoryPanel();
        final Lineup aufstellung = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
        final Weather weather = aufstellung.getWeather();

        lineupAssistantPanel = new LineupAssistantPanel(weather);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("", ThemeManager.getScaledIcon(HOIconName.BALL, 13, 13),
                new JScrollPane((Component)lineupAssistantPanel));
        tabbedPane.addTab("", ThemeManager.getIcon(HOIconName.DISK),
                lineupsComparisonHistoryPanel);


        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        setLayout(layout);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        layout.setConstraints(lineupRatingPanel, gbc);
        add(lineupRatingPanel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        layout.setConstraints(lineupSettingsPanel, gbc);
        add(lineupSettingsPanel, gbc);

        gbc.gridx = 1;
        layout.setConstraints(tabbedPane, gbc);
        add(tabbedPane, gbc);

    }

    public void refresh(){
        lineupRatingPanel.setRatings();
        lineupSettingsPanel.refresh();
    }
}
