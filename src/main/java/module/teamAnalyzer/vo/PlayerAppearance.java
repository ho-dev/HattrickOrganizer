// %381887291:hoplugins.teamAnalyzer.vo%
package module.teamAnalyzer.vo;

/**
 * Class for collecting all the players appearance
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class PlayerAppearance {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Player Name */
    private String name;

    /** Number of appearence in that position */
    private int apperarence = 0;

    /** Player Id */
    private int playerId;

    /** Status of the player on the team. injured, sold etc */
    private int status;

    //~ Methods ------------------------------------------------------------------------------------

    public int getAppearance() {
        return apperarence;
    }

    public void setApperarence(int i) {
        apperarence = i;
    }

    public void setName(String string) {
        name = string;
    }

    public String getName() {
        return name;
    }

    public void setPlayerId(int i) {
        playerId = i;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setStatus(int i) {
        status = i;
    }

    public int getStatus() {
        return status;
    }

    /**
     * Increase number of Appearance for the player
     */
    public void addApperarence() {
        apperarence++;
    }

    /**
     * toString methode: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
	public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("Player[");
        buffer.append("apperarence = " + apperarence);
        buffer.append("playerId = " + playerId);
        buffer.append(", name = " + name);
        buffer.append("]");

        return buffer.toString();
    }
}
