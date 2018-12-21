// %1126721330885:hoplugins.transfers.utils%
package module.transfer;


import core.model.HOVerwaltung;
import core.model.player.Spieler;

import java.util.Iterator;
import java.util.Objects;
import java.util.List;



/**
 * Utility to retrieve a player by an id, even if it is an old-player.
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
public final class PlayerRetriever {
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Private default constuctor to prevent class instantiation. 
     */
    private PlayerRetriever() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Retrieve a player by his ID.
     *
     * @param id Player ID
     *
     * @return ISpieler interface representing the found player or <code>null</code> if no player
     *         could be found.
     */
    public static Spieler getPlayer(int id) {
        final Spieler player = HOVerwaltung.instance().getModel().getSpieler(id);

        if (player == null) {
            final List<Spieler> oldPlayers = HOVerwaltung.instance().getModel().getAllOldSpieler();

            for (final Iterator<Spieler> iter = oldPlayers.iterator(); iter.hasNext();) {
                final Spieler oldPlayer = iter.next();

                if (oldPlayer.getSpielerID() == id) {
                    return oldPlayer;
                }
            }

            return null;
        } else {
            return player;
        }
    }
    /**
     * Retrieve a player by his name.
     *
     * @param name Player name
     *
     * @return ISpieler interface representing the found player or <code>null</code> if no player
     *         could be found.
     */
    public static Spieler getPlayer(String name) {
        final List<Spieler> players = HOVerwaltung.instance().getModel().getAllSpieler();

        for (final Iterator<Spieler> iter = players.iterator(); iter.hasNext();) {
            final Spieler player = iter.next();

            if (Objects.equals(player.getName(), name)) {
                return player;
            }
        }

        final List<Spieler> oldPlayers = HOVerwaltung.instance().getModel().getAllOldSpieler();

        for (final Iterator<Spieler> iter = oldPlayers.iterator(); iter.hasNext();) {
            final Spieler oldPlayer = iter.next();

            if (Objects.equals(oldPlayer.getName(), name)) {
                return oldPlayer;
            }
        }

        return null;
    }
}
