package module.teamanalyzer.ui.controller;

import module.teamanalyzer.ht.HattrickManager;
import module.teamanalyzer.manager.TeamManager;
import module.teamanalyzer.vo.Team;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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
        for (Team element : TeamManager.getTeams()) {
            System.out.println("Downloading " + element.getName());
            HattrickManager.downloadPlayers(element.getTeamId());
        }
    }
}
