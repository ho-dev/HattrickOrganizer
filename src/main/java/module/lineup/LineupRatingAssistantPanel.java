package module.lineup;

import core.gui.RefreshManager;
import core.gui.Updatable;
import core.model.HOVerwaltung;
import core.model.match.Weather;
import module.lineup.assistant.LineupAssistantPanel;
import module.lineup.ratings.LineupRatingPanel;
import javax.swing.*;
import java.awt.*;


/**
 * Create the panel displaying ratings, settings and assistant
 */
public class LineupRatingAssistantPanel extends JPanel implements core.gui.Refreshable, Updatable {

    private final LineupPanel m_clLineupPanel;
    private LineupRatingPanel lineupRatingPanel;
    private LineupSettingsPanel lineupSettingsPanel;
    private LineupAssistantPanel lineupAssistantPanel;
    public final LineupRatingPanel getLineupRatingPanel(){ return lineupRatingPanel;}
    public final LineupSettingsPanel getLineupSettingsPanel(){ return lineupSettingsPanel;}
    public final LineupAssistantPanel getLineupAssistantPanel(){return lineupAssistantPanel;}

    public LineupRatingAssistantPanel(LineupPanel parent) {
        m_clLineupPanel = parent;
        initComponents();
        RefreshManager.instance().registerRefreshable(this);
    }


    private void initComponents() {
        lineupRatingPanel = new LineupRatingPanel();
        lineupSettingsPanel = new LineupSettingsPanel(lineupRatingPanel);
        lineupAssistantPanel = new LineupAssistantPanel();


        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;

        setLayout(layout);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        layout.setConstraints(lineupRatingPanel, gbc);
        add(lineupRatingPanel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 20);
        layout.setConstraints(lineupSettingsPanel, gbc);
        add(lineupSettingsPanel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 5);
        layout.setConstraints(lineupAssistantPanel, gbc);
        add(lineupAssistantPanel, gbc);
    }

    public void refresh(){
        lineupSettingsPanel.refresh(false);
        lineupRatingPanel.refresh();
    }

    @Override
    public final void reInit() {
        refresh();
    }

    @Override
    public final void update() {
        m_clLineupPanel.update();
    }
}
