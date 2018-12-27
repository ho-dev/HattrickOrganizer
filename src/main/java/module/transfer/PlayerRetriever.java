// %1126721330885:hoplugins.transfers.utils%
package module.transfer;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.player.Spieler;

import java.util.ArrayList;
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
     * Retrieve a player by his transfer details.
     *
     * @param transfer Transfer information
     *
     * @return ISpieler interface representing the found player or <code>null</code> if no player
     *         could be found.
     */
    public static Spieler getPlayer(PlayerTransfer transfer) {
        Spieler player = getPlayer(transfer.getPlayerId());

        if (player != null) return player;

        List<Spieler> players = new ArrayList<Spieler>();
        players.addAll(HOVerwaltung.instance().getModel().getAllSpieler());
        players.addAll(HOVerwaltung.instance().getModel().getAllOldSpieler());

        List<Spieler> matches = new ArrayList<Spieler>();

        for (final Iterator<Spieler> iter = players.iterator(); iter.hasNext();) {
            player = iter.next();

            if (Objects.equals(player.getName(), transfer.getPlayerName())) {
                matches.add(player);
            }
        }

        if (matches.size() == 1) return matches.get(0);

        for (final Iterator<Spieler> iter = matches.iterator(); iter.hasNext();) {
            final Spieler match = iter.next();

            player = DBManager.instance().getSpielerAtDate(match.getSpielerID(), transfer.getDate());

            if(player == null) {
                iter.remove();
            }
        }

        if (matches.size() == 1) return matches.get(0);

        return null;
    }
}
