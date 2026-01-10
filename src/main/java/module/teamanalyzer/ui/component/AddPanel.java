// %3790006497:hoplugins.teamAnalyzer.ui.component%
package module.teamanalyzer.ui.component;

import core.db.DBManager;
import core.model.TranslationFacility;
import module.teamanalyzer.ht.HattrickManager;
import module.teamanalyzer.manager.TeamManager;
import module.teamanalyzer.ui.NumberTextField;
import module.teamanalyzer.ui.controller.FavoriteItemListener;
import module.teamanalyzer.vo.Team;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



/**
 * A panel that allows the user to add a new favourite team
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class AddPanel extends JPanel {
    //~ Instance fields ----------------------------------------------------------------------------

    /**
	 *
	 */
	private static final long serialVersionUID = 7424042069787091891L;

	/** The Favourite Menu itself */
    FavouriteMenu menu;

    /** The add button */
    JButton addButton = new JButton(TranslationFacility.tr("ls.button.add"));

    /** The ID-Label */
    JLabel idlabel = new JLabel(TranslationFacility.tr("ls.team.id") + ": ");

    /** A status label */
    JLabel status = new JLabel();

    /** The text field */
    NumberTextField teamId = new NumberTextField(8);

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new AddPanel object.
     *
     * @param me the favourite menu, for reference
     */
    public AddPanel(FavouriteMenu me) {
        jbInit();
        menu = me;
    }

    /**
     * Constructs a new instance.
     */
    public AddPanel() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Initializes the state of this instance.
     */
    private void jbInit() {
        setLayout(new BorderLayout());

        //add(name, BorderLayout.CENTER);
        add(addButton, BorderLayout.EAST);
        add(idlabel, BorderLayout.WEST);
        add(teamId, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Team team = new Team();

                    team.setTeamId(teamId.getValue());

                    if (TeamManager.isTeamInList(teamId.getValue())) {
                        status.setText(TranslationFacility.tr("Favourite.InList"));
                        teamId.setText("");

                        return;
                    }

                    if (DBManager.instance().isTAFavourite(teamId.getValue())) {
                        status.setText(TranslationFacility.tr("Favourite.Already"));
                        teamId.setText("");

                        return;
                    }

                    try {
                        String teamName = HattrickManager.downloadTeamName(teamId.getValue());

                        team.setName(teamName);
                    } catch (Exception e1) {
                        status.setText(TranslationFacility.tr("Favourite.Error"));
                        teamId.setText("");

                        return;
                    }

                    status.setText(team.getName() + " "
                                   + TranslationFacility.tr("hinzugefuegt"));
                    menu.teams.add(team);

                    JMenuItem item = new JMenuItem(team.getName());

                    item.addActionListener(new FavoriteItemListener(team));
                    menu.items.add(item);
                    menu.add(item, 0);
                    DBManager.instance().addTAFavoriteTeam(team);
                    menu.itemDelete.setVisible(true);
                    teamId.setText("");
                }
            });
    }
}
