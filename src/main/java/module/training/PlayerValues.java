// %1126721452166:hoplugins.trainingExperience.vo%
package module.training;

/**
 * This value object represents the skill values for a player at some point.
 *
 * @author NetHyperon
 */
public class PlayerValues {
    //~ Instance fields ----------------------------------------------------------------------------

    private int form;
    private int playerID;
    private int tsi;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new PlayerValues object.
     *
     * @param tsi Value for Total Skill Index
     * @param form Value for player form
     */
    public PlayerValues(int tsi, int form) {
        this.tsi = tsi;
        this.form = form;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Get the form value
     *
     * @return Form value
     */
    public int getForm() {
        return form;
    }

    /**
     * Get the player ID
     *
     * @return id
     */
    public int getPlayerID() {
        return playerID;
    }

    /**
     * Get the TSI value
     *
     * @return TSI value
     */
    public int getTsi() {
        return tsi;
    }
}
