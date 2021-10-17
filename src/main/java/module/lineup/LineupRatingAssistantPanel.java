package module.lineup;

import core.gui.RefreshManager;
import core.gui.Updatable;
import core.util.HOLogger;
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
        lineupSettingsPanel = new LineupSettingsPanel(m_clLineupPanel);
        lineupAssistantPanel = new LineupAssistantPanel();

        // steff 1217
        setLayout(new BorderLayout());
        add(lineupRatingPanel, BorderLayout.NORTH);
        add(lineupSettingsPanel, BorderLayout.CENTER);
        add(lineupAssistantPanel, BorderLayout.SOUTH);

    }

    public void refresh(){
        HOLogger.instance().log(getClass(), " refresh() has been called");
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
