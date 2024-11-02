package module.matches.statistics;

import core.db.DBManager;
import core.gui.model.UserColumnController;
import core.model.UserParameter;
import core.model.match.MatchesOverviewRow;
import module.matches.MatchLocation;
import module.matches.MatchesPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class MatchesOverviewTable extends JTable {

    private final MatchesOverviewColumnModel tableModel;

    public MatchesOverviewTable(int iMatchType) {
        super();
        setOpaque(false);
        tableModel = UserColumnController.instance().getMatchesOverview1ColumnModel();
        if (iMatchType == MatchesPanel.ALL_GAMES || iMatchType == MatchesPanel.OTHER_TEAM_GAMES) {
            MatchesOverviewRow[] tmp = new MatchesOverviewRow[0];
            tableModel.setValues(tmp);
        } else {
            tableModel.setValues(DBManager.instance().getMatchesOverviewValues(iMatchType, UserParameter.instance().matchLocation));
        }

        tableModel.initTable(this);
        setDefaultRenderer(Object.class, new MatchesOverviewRenderer());
        setDefaultRenderer(Integer.class, new MatchesOverviewRenderer());
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    public final void storeUserSettings() {
        tableModel.storeUserSettings();
    }

    public void refresh(int iMatchType, MatchLocation matchLocation) {
        if (iMatchType == MatchesPanel.ALL_GAMES || iMatchType == MatchesPanel.OTHER_TEAM_GAMES) {
            MatchesOverviewRow[] tmp = new MatchesOverviewRow[0];
            tableModel.setValues(tmp);
        } else {
            tableModel.setValues(DBManager.instance().getMatchesOverviewValues(iMatchType, matchLocation));
            tableModel.fireTableDataChanged();
        }
    }
}
