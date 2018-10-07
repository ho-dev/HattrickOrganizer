package module.teamAnalyzer.ui.controller;

import module.teamAnalyzer.ht.HattrickManager;
import module.teamAnalyzer.manager.TeamManager;
import module.teamAnalyzer.vo.Team;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;


/**
 * Action listener for the help menu item
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class ImportItemListener implements ActionListener {
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new ImportItemListener object.
     */
    public ImportItemListener() {
    }

    //~ Methods ------------------------------------------------------------------------------------
    public void actionPerformed(ActionEvent arg0) {
        for (Iterator<Team> iter = TeamManager.getTeams().iterator(); iter.hasNext();) {
            Team element = iter.next();
            System.out.println("Downloading " + element.getName());
            HattrickManager.downloadPlayers(element.getTeamId());
        }
    }
}
