// %2621114898:de.hattrickorganizer.model%
package core.model.match;

import java.util.ArrayList;
import java.util.List;


/**
 * Hattrick Lineup Object
 *
 * @author Draghetto HO
 */
@SuppressWarnings("unchecked")
public class TeamLineup {
    //~ Instance fields ----------------------------------------------------------------------------

    /** List of players divided by area */
    private List<String>[] players = new List[4];

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new Lineup object.
     */
    public TeamLineup() {
        for (int i = 0; i < 4; i++) {
            players[i] = new ArrayList<String>();
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns the list of players that plays in the specified area
     *
     * @param area
     *
     * @return list of players
     */
    public final List<String> getArea(int area) {
        return players[area];
    }

    /**
     * Add a player to the lineup
     *
     * @param playerId
     * @param area
     */
    public final void add(String playerId, int area) {
        players[area].add(playerId);
    }
}
