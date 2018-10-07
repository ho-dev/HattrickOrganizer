// %1176270089:hoplugins.teamAnalyzer.ui.component%
package module.teamAnalyzer.ui.component;

import core.db.DBManager;
import core.model.HOVerwaltung;
import module.teamAnalyzer.vo.Team;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;



/**
 * A panel that allows the user to remove a favourite team
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class DeletePanel extends JPanel {
    //~ Instance fields ----------------------------------------------------------------------------

    /**
	 *
	 */
	private static final long serialVersionUID = 3360500719618041012L;

	/** The Favourite Menu itself */
    FavouriteMenu menu;

    /** The add button */
    JButton addButton = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.add"));

    /** The delete button */
    JButton deletebutton = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.delete"));

    /** ComboBox with the list of favourite teams */
    JComboBox teams = new JComboBox();

    /** A status label */
    JLabel status = new JLabel();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Constructs a new instance.
     *
     * @param me the favourite menu for reference
     */
    public DeletePanel(FavouriteMenu me) {
        menu = me;
        jbInit();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Methods that fill the combo with  the favourite teams
     */
    private void fillCombo() {
        teams.removeAllItems();

        for (Iterator<Team> iter = menu.teams.iterator(); iter.hasNext();) {
            Team element = iter.next();

            teams.addItem(element);
        }

        teams.setEnabled(true);
        deletebutton.setEnabled(true);

        if (teams.getItemCount() == 0) {
            teams.setEnabled(false);
            deletebutton.setEnabled(false);
        }
    }

    /**
     * Initializes the state of this instance.
     */
    private void jbInit() {
        setLayout(new BorderLayout());
        add(deletebutton, BorderLayout.CENTER);
        add(teams, BorderLayout.WEST);
        add(status, BorderLayout.SOUTH);

        fillCombo();

        deletebutton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Team team = (Team) teams.getSelectedItem();

                    if (team == null) {
                        status.setText(HOVerwaltung.instance().getLanguageString("Favourite.SelectTeam"));

                        return;
                    }

                    int pos = menu.teams.indexOf(team);

                    menu.teams.remove(pos);

                    JMenuItem item = menu.items.get(pos);

                    menu.remove(item);
                    menu.items.remove(pos);
                    DBManager.instance().removeTAFavoriteTeam(team.getTeamId());
                    fillCombo();

                    if (menu.teams.size() == 0) {
                        menu.itemDelete.setVisible(false);
                    }
                }
            });
    }
}
