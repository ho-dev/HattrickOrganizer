package tool.keepertool;

import core.model.player.Spieler;
import module.transfer.scout.ScoutEintrag;

/**
 * A Player Item object to be used in JComboBox
 *
 * @author draghetto
 */
public class PlayerItem {
    //~ Instance fields ----------------------------------------------------------------------------

    private String name = "";
    private int form;
    private int id;
    private int tsi;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new PlayerItem object.
     *
     * @param spieler the roster player to include in the Combo
     */
    public PlayerItem(Spieler spieler) {
        tsi = spieler.getTSI();
        form = spieler.getForm();
        id = spieler.getSpielerID();
        name = spieler.getName();
    }

    /**
     * Creates a new PlayerItem object.
     *
     * @param spieler the scouted player to include in the combo
     */
    public PlayerItem(ScoutEintrag spieler) {
        tsi = spieler.getTSI();
        form = spieler.getForm();
        id = 0;
        name = spieler.getName();
    }

    /**
     * Creates a new empty PlayerItem object.
     */
    public PlayerItem() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Return the keeper form
     *
     * @return form
     */
    public final int getForm() {
        return form;
    }

    /**
     * Return the keeper id
     *
     * @return id
     */
    public final int getId() {
        return id;
    }

    /**
     * Return the keeper tsi
     *
     * @return tsi
     */
    public final int getTsi() {
        return tsi;
    }

    /**
     * Returns the name
     *
     * @return The String to show in the combo
     */
    @Override
	public final String toString() {
        return name;
    }
}
