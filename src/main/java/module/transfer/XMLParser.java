// %1126721330041:hoplugins.transfers.dao%
package module.transfer;


import core.db.DBManager;
import core.file.xml.XMLManager;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.net.MyConnector;
import core.util.HODateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * XML Parser used to parse HT xml for tansfer information.
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
public final class XMLParser {

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Private default constuctor to prevent class instantiation.
     */
    private XMLParser() {
    }

    /**
     * Gets all transfers for a player.
     *
     * @param playerId Player ID
     *
     * @return List of transfers.
     *
     */
    public static List<PlayerTransfer> getAllPlayerTransfers(int playerId) {

    	final String xml = MyConnector.instance().getTransfersForPlayer(playerId);
        final List<PlayerTransfer> transferList = new Vector<>();

        final Document doc = XMLManager.parseString(xml);

        //get Root element ('HattrickData') :
        assert doc != null;
        final Element root = doc.getDocumentElement();

        // Get tranfer info
        final NodeList containers = root.getElementsByTagName("Transfers"); //$NON-NLS-1$

        for (int cont = 0; cont < containers.getLength(); cont++) {
            try {
                final Element container = (Element) containers.item(cont);

                // Get the player info
                final Element playerElement = (Element) container.getElementsByTagName("Player").item(0); //$NON-NLS-1$
                final int playerid = XMLManager.xmlIntValue(playerElement, "PlayerID"); //$NON-NLS-1$
                final String playerName = XMLManager.xmlValue(playerElement, "PlayerName"); //$NON-NLS-1$

                // Get the player transfers
                final NodeList transfers = container.getElementsByTagName("Transfer"); //$NON-NLS-1$

                for (int trans = 0; trans < transfers.getLength(); trans++) {
                    final Element transfer = (Element) transfers.item(trans);

                    if (transfer != null) {
                        final int transferid = XMLManager.xmlIntValue(transfer, "TransferID"); //$NON-NLS-1$
                        final PlayerTransfer playerTranfer = new PlayerTransfer(transferid, playerid);
                        playerTranfer.setPlayerName(playerName);

                        final String deadline = XMLManager.xmlValue(transfer, "Deadline");

                        HODateTime transferDate = HODateTime.fromHT(deadline);
                        playerTranfer.setDate(transferDate);

                        var htweek = transferDate.toLocaleHTWeek();
                        playerTranfer.setSeason(htweek.season);
                        playerTranfer.setWeek(htweek.week);

                        final Element buyer = (Element) transfer.getElementsByTagName("Buyer").item(0); //$NON-NLS-1$
                        final Element seller = (Element) transfer.getElementsByTagName("Seller").item(0); //$NON-NLS-1$

                        if ((buyer != null) && (seller != null)) {
                            // Get the buyer info
                            playerTranfer.setBuyerid(XMLManager.xmlIntValue(buyer, "BuyerTeamID")); //$NON-NLS-1$
                            playerTranfer.setBuyerName(XMLManager.xmlValue(buyer, "BuyerTeamName")); //$NON-NLS-1$
                            // Get the seller info
                            playerTranfer.setSellerid(XMLManager.xmlIntValue(seller, "SellerTeamID")); //$NON-NLS-1$
                            playerTranfer.setSellerName(XMLManager.xmlValue(seller, "SellerTeamName")); //$NON-NLS-1$

                            playerTranfer.setPrice(XMLManager.xmlIntValue(transfer, "Price")); //$NON-NLS-1$
                            playerTranfer.setTsi(XMLManager.xmlIntValue(transfer, "TSI")); //$NON-NLS-1$
                        }
                        transferList.add(playerTranfer);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return transferList;
    }

    /**
     * Get all player transfers of a team till a certain date.
     *
     * @param teamId the team id
     * @param endDate end date for the transfers
     */
    public static List<PlayerTransfer> getAllTeamTransfers(int teamId, HODateTime endDate) {
        final List<PlayerTransfer> transferList = new Vector<>();
        // loop all pages 0 .. n until there are no more data available
        int page = 0;
        while (true) {
            var transfers = downloadTeamTransfers(teamId, page++);
	        if (transfers.isEmpty()) {
                break;
	        } else {
                for ( var transfer : transfers){
                    if (!transfer.getDate().isBefore(PlayerTransfer.activatedDate) && !transfer.getDate().isAfter(endDate)){
                        transferList.add(transfer);
                    }
                }
	        }
        }
        return transferList;
    }

    /**
     * Download one page of team's transfers
     * Static player transfer information about activated date, price sum and count are updated
     * @param teamId Team id
     * @param page Page number [1..], 0 is last page
     * @return List of transfers (max 25)
     */
    public static List<PlayerTransfer> downloadTeamTransfers(int teamId, int page){
        var url = "/common/chppxml.axd?file=transfersTeam&teamID="+teamId+"&pageIndex="+page;
        var xml = MyConnector.instance().getHattrickXMLFile(url);
        var doc = XMLManager.parseString(xml);
        if (doc != null){
            return parseTeamTransfers(doc);
        }
        return new ArrayList<>();
    }

    /**
     * Parse xml data for team transfers
     *
     * @param doc Xml document
     * @return List of transfers.
     */
    public static List<PlayerTransfer> parseTeamTransfers(Document doc) {
        final List<PlayerTransfer> transferList = new Vector<>();

        //get Root element ('HattrickData') :
        final Element root = doc.getDocumentElement();

        // Team
        var team = (Element) root.getElementsByTagName("Team").item(0);
        PlayerTransfer.teamId = XMLManager.xmlIntegerValue(team, "TeamID");
        PlayerTransfer.activatedDate = HODateTime.fromHT(XMLManager.xmlValue(team, "ActivatedDate"));

        // Stats
        var stats = (Element) root.getElementsByTagName("Stats").item(0);
        PlayerTransfer.totalSumOfBuys = XMLManager.xmlLongValue(stats, "TotalSumOfBuys");
        PlayerTransfer.totalSumOfSales = XMLManager.xmlLongValue(stats, "TotalSumOfSales");
        PlayerTransfer.numberOfBuys = XMLManager.xmlLongValue(stats, "NumberOfBuys");
        PlayerTransfer.numberOfSales = XMLManager.xmlLongValue(stats, "NumberOfSales");

        // Get tranfer info
        final NodeList containers = root.getElementsByTagName("Transfers"); //$NON-NLS-1$

        for (int cont = 0; cont < containers.getLength(); cont++) {
            try {
                final Element container = (Element) containers.item(cont);

                // Get the player transfers
                final NodeList transfers = container.getElementsByTagName("Transfer"); //$NON-NLS-1$

                for (int trans = 0; trans < transfers.getLength(); trans++) {
                    final Element transfer = (Element) transfers.item(trans);

                    if (transfer != null) {
                        // Get the player info
                        final Element playerElement = (Element) transfer.getElementsByTagName("Player").item(0); //$NON-NLS-1$
                        final int playerid = XMLManager.xmlIntValue(playerElement, "PlayerID"); //$NON-NLS-1$
                        final String playerName = XMLManager.xmlValue(playerElement, "PlayerName"); //$NON-NLS-1$

                        final int transferid = XMLManager.xmlIntValue(transfer, "TransferID"); //$NON-NLS-1$
                        final PlayerTransfer playerTranfer = new PlayerTransfer(transferid, playerid);
                        playerTranfer.setPlayerName(playerName);

                        final String deadline = XMLManager.xmlValue(transfer, "Deadline");

                        HODateTime transferDate = HODateTime.fromHT(deadline);
                        playerTranfer.setDate(transferDate);
                        var htweek = transferDate.toLocaleHTWeek();
                        playerTranfer.setSeason(htweek.season);
                        playerTranfer.setWeek(htweek.week);

                        final Element buyer = (Element) transfer.getElementsByTagName("Buyer").item(0); //$NON-NLS-1$
                        final Element seller = (Element) transfer.getElementsByTagName("Seller").item(0); //$NON-NLS-1$

                        if ((buyer != null) && (seller != null)) {
                            // Get the buyer info
                            playerTranfer.setBuyerid(XMLManager.xmlIntValue(buyer, "BuyerTeamID")); //$NON-NLS-1$
                            playerTranfer.setBuyerName(XMLManager.xmlValue(buyer, "BuyerTeamName")); //$NON-NLS-1$
                            // Get the seller info
                            playerTranfer.setSellerid(XMLManager.xmlIntValue(seller, "SellerTeamID")); //$NON-NLS-1$
                            playerTranfer.setSellerName(XMLManager.xmlValue(seller, "SellerTeamName")); //$NON-NLS-1$

                            playerTranfer.setPrice(XMLManager.xmlIntValue(transfer, "Price")); //$NON-NLS-1$
                            playerTranfer.setTsi(XMLManager.xmlIntValue(transfer, "TSI")); //$NON-NLS-1$
                            var transferType = XMLManager.xmlValue(transfer, "TransferType"); //$NON-NLS-1$
                            if ( transferType.equals("S")){
                                playerTranfer.setType(PlayerTransfer.SELL);
                            }
                            else {
                                playerTranfer.setType(PlayerTransfer.BUY);
                            }
                        }

                        if (playerTranfer.getPlayerId() == 0){ // fired player
                            // try to get player info from HO database
                            playerTranfer.loadPLayerInfo(false);
                        }

                        transferList.add(playerTranfer);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return transferList;
    }

    public static boolean updateTeamTransfers(int teamId) {
        var transfers = getAllTeamTransfers(teamId, HODateTime.now().plus(1, ChronoUnit.DAYS));
        var players =  updateTeamTransfers(transfers);
        if ( players != null) {
            for (var player : players) {
                updatePlayerTransfers(player.getPlayerID());
            }
            return true;
        }
        return false;
    }

    /**
     * Update transfer data for a team from the HT xml.
     * @param transfers player transfers
     */
    private static List<Player> updateTeamTransfers(List<PlayerTransfer> transfers) {
        final List<Player> players = new ArrayList<>();
        for (PlayerTransfer transfer : transfers) {
            final Player player = PlayerRetriever.getPlayer(transfer);

            if (player != null) {
                if (!players.contains(player)) players.add(player);
                if (transfer.getPlayerId() == 0) {
                    int playerIdFound = player.getPlayerID();
                    transfer.setPlayerId(playerIdFound);
                    player.setIsFired(true);
                }
            } else {
                PlayerTransfer alreadyInDB = DBManager.instance().loadPlayerTransfer(transfer.getTransferId());
                if (alreadyInDB != null) {
                    if (transfer.getPlayerId() == 0) {
                        var pl = PlayerRetriever.getPlayer(alreadyInDB);
                        if (pl != null) pl.setIsFired(true);
                    } else {
                        Player dummy = new Player();
                        dummy.setPlayerID(transfer.getPlayerId());
                        if (!players.contains(dummy)) players.add(dummy);
                    }
                }
            }
            DBManager.instance().storePlayerTransfer(transfer);
        }
        return players.stream().filter(i -> !i.isFired()).toList();
    }

    public static void updatePlayerTransfers(int playerID) {
        var transfers = getAllPlayerTransfers(playerID);
        if (!transfers.isEmpty()) {
            var firstTransfer = transfers.get(transfers.size()-1);
            var isHomegrown = firstTransfer.getSellerid() == HOVerwaltung.instance().getModel().getBasics().getTeamId();
            for ( var transfer : transfers){
                transfer.getPlayerInfo().setHomeGrown(isHomegrown);
                DBManager.instance().storePlayerTransfer(transfer);
            }
        }
        else {
            var notes = DBManager.instance().loadPlayerNotes(playerID);
            if ( notes != null){
                notes.setIsFired(true);
                DBManager.instance().storePlayerNotes(notes);
            }
        }
    }
}
