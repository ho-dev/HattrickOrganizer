// %1126721330041:hoplugins.transfers.dao%
package module.transfer;


import core.file.xml.XMLManager;
import core.gui.HOMainFrame;
import core.net.MyConnector;
import core.util.HTDatetime;
import core.util.Helper;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    //~ Static fields/initializers -----------------------------------------------------------------

    private static final SimpleDateFormat DATETIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATE = new SimpleDateFormat("yyyy-MM-dd");
//    private static final SimpleDateFormat XMLDATETIME = new SimpleDateFormat("yyyy-MM-dd%20HH:mm");

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Private default constuctor to prevent class instantiation.
     */
    private XMLParser() {
    }

    //~ Methods ------------------------------------------------------------------------------------

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
        final Element root = doc.getDocumentElement();

        // Get tranfer info
        final NodeList containers = root.getElementsByTagName("Transfers"); //$NON-NLS-1$

        for (int cont = 0; cont < containers.getLength(); cont++) {
            try {
                final Element container = (Element) containers.item(cont);

                // Get the player info
                final Element playerElement = (Element) container.getElementsByTagName("Player").item(0); //$NON-NLS-1$
                final int playerid = Integer.parseInt(getChildNodeValue(playerElement, "PlayerID")); //$NON-NLS-1$
                final String playerName = getChildNodeValue(playerElement, "PlayerName"); //$NON-NLS-1$

                // Get the player transfers
                final NodeList transfers = container.getElementsByTagName("Transfer"); //$NON-NLS-1$

                for (int trans = 0; trans < transfers.getLength(); trans++) {
                    final Element transfer = (Element) transfers.item(trans);

                    if (transfer != null) {
                        final int transferid = Integer.parseInt(getChildNodeValue(transfer, "TransferID")); //$NON-NLS-1$
                        final PlayerTransfer playerTranfer = new PlayerTransfer(transferid, playerid);
                        playerTranfer.setPlayerName(playerName);

                        final String deadline = getChildNodeValue(transfer, "Deadline");

                        Date transferDate;

                        try {
                            transferDate = DATETIME.parse(deadline);
                        } catch (ParseException e) {
                            try {
                                transferDate = DATE.parse(deadline);
                            } catch (ParseException e0) {
                            	Helper.showMessage(HOMainFrame.instance(),
                                                                           "XML Parse error",
                                                                           "Error parsing XML: transfer '"
                                                                           + transferid
                                                                           + "' will be skipped", 0);
                                continue;
                            }
                        }
                        
                        Timestamp time = new Timestamp(transferDate.getTime());

                        playerTranfer.setDate(time);

                        var htdatetime = new HTDatetime(time);
                        playerTranfer.setSeason(htdatetime.getHTSeasonLocalized());
                        playerTranfer.setWeek(htdatetime.getHTWeekLocalized());

                        final Element buyer = (Element) transfer.getElementsByTagName("Buyer").item(0); //$NON-NLS-1$
                        final Element seller = (Element) transfer.getElementsByTagName("Seller").item(0); //$NON-NLS-1$

                        if ((buyer != null) && (seller != null)) {
                            // Get the buyer info
                            playerTranfer.setBuyerid(Integer.parseInt(getChildNodeValue(buyer, "BuyerTeamID"))); //$NON-NLS-1$
                            playerTranfer.setBuyerName(getChildNodeValue(buyer, "BuyerTeamName")); //$NON-NLS-1$
                            // Get the seller info
                            playerTranfer.setSellerid(Integer.parseInt(getChildNodeValue(seller, "SellerTeamID"))); //$NON-NLS-1$
                            playerTranfer.setSellerName(getChildNodeValue(seller, "SellerTeamName")); //$NON-NLS-1$

                            playerTranfer.setPrice(Integer.parseInt(getChildNodeValue(transfer, "Price"))); //$NON-NLS-1$
                            //playerTranfer.setMarketvalue(Integer.parseInt(getChildNodeValue(transfer, "MarketValue"))); //$NON-NLS-1$
                            playerTranfer.setMarketvalue(Integer.parseInt(getChildNodeValue(transfer, "TSI"))); //for safety
                            playerTranfer.setTsi(Integer.parseInt(getChildNodeValue(transfer, "TSI"))); //$NON-NLS-1$
                        }

                        transferList.add(playerTranfer);
                    }
                }
            } catch (Exception e) {
            }
        }

        return transferList;
    }

    /**
     * Get all player transfers of a team till a certain date.
     *
     * @param teamid the team id
     * @param endDate end date for the transfers
     */
    public static List<PlayerTransfer> getAllTeamTransfers(int teamid, Date endDate) throws Exception {
        final List<PlayerTransfer> transferList = new Vector<>();
        final String url = "/common/chppxml.axd?file=transfersTeam&teamID="+teamid+"&pageIndex=";

        // loop all pages 0 .. n until there are no more data avaliable
        boolean stop = false;
        int page = 0;
        while (!stop) {
	        final String xml = MyConnector.instance().getHattrickXMLFile(url+page); 
	        final Document doc = XMLManager.parseString(xml);

	        //get Root element ('HattrickData') :
	        final Element root = doc.getDocumentElement();

	        final Element teamElement = (Element) root.getElementsByTagName("Team").item(0); //$NON-NLS-1$
	        Date activatedDate;

	        try {
	            activatedDate = DATETIME.parse(getChildNodeValue(teamElement, "ActivatedDate"));
	        } catch (ParseException e) {
	            activatedDate = DATE.parse(getChildNodeValue(teamElement, "ActivatedDate"));
	        }

	        List<PlayerTransfer> transfers = parseTeamTransfers(doc, activatedDate, endDate);
	        if (transfers.size()<1) {
	        	stop = true;
	        } else {
	        	transferList.addAll(transfers);
	        }
	        page++;
        }

        return transferList;
    }

    /**
     * Get the value of a child node
     *
     * @param element Parent element
     * @param childnode Tag name of the childnode
     *
     * @return Value of the child node
     *
     */
    private static String getChildNodeValue(Element element, String childnode) {
        try {
            String retval = ""; //$NON-NLS-1$

            if (element != null) {
                final NodeList list = element.getElementsByTagName(childnode);

                if ((list != null) && (list.getLength() > 0)) {
                    final Element child = (Element) list.item(0);
                    core.file.xml.XMLManager.getFirstChildNodeValue(child);

                    if ((child != null) && (element.getFirstChild() != null)) {
                        retval = child.getFirstChild().getNodeValue();
                    }
                }
            }

            return retval;
        } catch (NullPointerException e) {
            return ""; //$NON-NLS-1$
        }
    }

    /**
     * Parse xml data for team transfers
     *
     * @param doc Xml document
     * @param activatedDate date when the team got activated, dont include transfers before that date
     * @param endDate end date of transfers to parse
     *
     * @return List of transfers.
     */
    public static List<PlayerTransfer> parseTeamTransfers(Document doc, Date activatedDate, Date endDate) {
        final List<PlayerTransfer> transferList = new Vector<>();

        //get Root element ('HattrickData') :
        final Element root = doc.getDocumentElement();

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
                        final int playerid = Integer.parseInt(getChildNodeValue(playerElement, "PlayerID")); //$NON-NLS-1$
                        final String playerName = getChildNodeValue(playerElement, "PlayerName"); //$NON-NLS-1$

                        final int transferid = Integer.parseInt(getChildNodeValue(transfer, "TransferID")); //$NON-NLS-1$
                        final PlayerTransfer playerTranfer = new PlayerTransfer(transferid, playerid);
                        playerTranfer.setPlayerName(playerName);

                        final String deadline = getChildNodeValue(transfer, "Deadline");

                        Date transferDate;

                        try {
                            transferDate = DATETIME.parse(deadline);
                        } catch (ParseException e) {
                            try {
                                transferDate = DATE.parse(deadline);
                            } catch (ParseException e0) {
                            	Helper.showMessage(HOMainFrame.instance(),
                                                                           "XML Parse error",
                                                                           "Error parsing XML: transfer '"
                                                                           + transferid+ "' will be skipped", 0);
                                
                                continue;
                            }
                        }
                        if (transferDate.before(activatedDate)) continue;
                        if (transferDate.after(endDate)) continue;

                        Timestamp time = new Timestamp(transferDate.getTime());

                        playerTranfer.setDate(time);
                        var htdatetime = new HTDatetime(time);
                        playerTranfer.setSeason(htdatetime.getHTSeasonLocalized());
                        playerTranfer.setWeek(htdatetime.getHTWeekLocalized());

                        final Element buyer = (Element) transfer.getElementsByTagName("Buyer").item(0); //$NON-NLS-1$
                        final Element seller = (Element) transfer.getElementsByTagName("Seller").item(0); //$NON-NLS-1$

                        if ((buyer != null) && (seller != null)) {
                            // Get the buyer info
                            playerTranfer.setBuyerid(Integer.parseInt(getChildNodeValue(buyer, "BuyerTeamID"))); //$NON-NLS-1$
                            playerTranfer.setBuyerName(getChildNodeValue(buyer, "BuyerTeamName")); //$NON-NLS-1$
                            // Get the seller info
                            playerTranfer.setSellerid(Integer.parseInt(getChildNodeValue(seller, "SellerTeamID"))); //$NON-NLS-1$
                            playerTranfer.setSellerName(getChildNodeValue(seller, "SellerTeamName")); //$NON-NLS-1$

                            playerTranfer.setPrice(Integer.parseInt(getChildNodeValue(transfer, "Price"))); //$NON-NLS-1$
                            //playerTranfer.setMarketvalue(Integer.parseInt(getChildNodeValue(transfer, "MarketValue"))); //$NON-NLS-1$
                            playerTranfer.setMarketvalue(Integer.parseInt(getChildNodeValue(transfer, "TSI"))); //for safety. not sure, if it's needed
                            playerTranfer.setTsi(Integer.parseInt(getChildNodeValue(transfer, "TSI"))); //$NON-NLS-1$
                        }

                        transferList.add(playerTranfer);
                    }
                }
            } catch (Exception e) {
            }
        }

        return transferList;
    }
}
