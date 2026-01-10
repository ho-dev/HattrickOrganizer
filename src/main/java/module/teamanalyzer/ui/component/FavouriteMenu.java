// %2153978627:hoplugins.teamAnalyzer.ui.component%
package module.teamanalyzer.ui.component;

import core.db.DBManager;
import core.model.TranslationFacility;
import module.teamanalyzer.SystemManager;
import module.teamanalyzer.ui.controller.FavoriteItemListener;
import module.teamanalyzer.vo.Team;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;



/**
 * A Favourite Menu
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class FavouriteMenu extends JMenu {
    //~ Instance fields ----------------------------------------------------------------------------

    /**
	 *
	 */
	private static final long serialVersionUID = -3404254214435543226L;

    /** Add menu item */
    public JMenuItem itemAdd = new JMenuItem(TranslationFacility.tr("ls.button.add"));

    /** Delete menu item */
    public JMenuItem itemDelete = new JMenuItem(TranslationFacility.tr("ls.button.delete"));

    /** List of favourite team menu items */
    public List<JMenuItem> items;

    /** List of favourite team objects */
    public List<Team> teams;

    /** Reference to itself */
    private FavouriteMenu me;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new FavouriteMenu object.
     */
    public FavouriteMenu() {
        super(TranslationFacility.tr("Favourite"));
        jbInit();
        me = this;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Initiates the gui
     */
    private void jbInit() {
        teams = DBManager.instance().getTAFavoriteTeams();
        items = new ArrayList<>();

        for (Team element : teams) {
            JMenuItem item = new JMenuItem(element.getName());

            item.addActionListener(new FavoriteItemListener(element));
            add(item);
            items.add(item);
        }

        add(new JSeparator());
        add(itemAdd);
        add(itemDelete);

        if (teams.isEmpty()) {
            itemDelete.setVisible(false);
        }

        itemDelete.addActionListener(arg0 -> {
            JOptionPane.showMessageDialog(SystemManager.getPlugin(),
                                          new DeletePanel(me),
                                          TranslationFacility.tr("ls.button.delete")
                                          + " "
                                          + TranslationFacility.tr("Verein"),
                                          JOptionPane.PLAIN_MESSAGE);
        });

        itemAdd.addActionListener(arg0 -> {
            JOptionPane.showMessageDialog(SystemManager.getPlugin(),
                                          new AddPanel(me),
                                          TranslationFacility.tr("ls.button.add")
                                          + " "
                                          + TranslationFacility.tr("Verein"),
                                          JOptionPane.PLAIN_MESSAGE);
        });
    }
}
