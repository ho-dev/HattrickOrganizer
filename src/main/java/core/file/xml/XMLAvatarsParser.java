package core.file.xml;

import core.model.player.Layer;
import core.model.player.PlayerAvatar;
import core.util.HOLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.List;


public class XMLAvatarsParser {

    /**
     * Utility class - private constructor enforces noninstantiability.
     */
    public XMLAvatarsParser() {
    }

    public static List<PlayerAvatar>  parseAvatarsFromString(String str) {
        return parseDetails(XMLManager.parseString(str));
    }

    private static List<PlayerAvatar> parseDetails(Document doc) {

        List<PlayerAvatar> playerAvatars = new ArrayList<>();;

        if (doc == null) {
            return playerAvatars;
        }

        try {
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            NodeList players = root.getElementsByTagName("Player");

            if (players != null) {
                int i = 0;
                String bgImage = "";
                List<Layer> layers = new ArrayList<>();
                Element player, layer;
                Node node, node2;
                NodeList nlLayers;
                int x, y, playerID;
                String urlImage;

                while (i < players.getLength()) {
                    node = players.item(i);
                    System.out.println("");
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        //Print each employee's detail
                        player = (Element) node;
//                        System.out.println("Employee id : "    + player.getAttribute("PlayerID"));
                        System.out.println("Player ID: "  + player.getElementsByTagName("PlayerID").item(0).getTextContent());
                        playerID = Integer.parseInt(player.getElementsByTagName("PlayerID").item(0).getTextContent());

                        System.out.println("Background image: "  + player.getElementsByTagName("BackgroundImage").item(0).getTextContent());
                        bgImage = player.getElementsByTagName("BackgroundImage").item(0).getTextContent();

                        // Get all layers ====================
                        nlLayers = player.getElementsByTagName("Layer");
                        int j = 0;
                        while (j < nlLayers.getLength()) {
                            node2 = nlLayers.item(j);
                            if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                layer = (Element) node2;
                                System.out.println("x: "  + layer.getAttribute("x"));
                                System.out.println("y: "  + layer.getAttribute("y"));
                                System.out.println("img: "  + layer.getElementsByTagName("Image").item(0).getTextContent());
                                x = Integer.parseInt(layer.getAttribute("x"));
                                y = Integer.parseInt(layer.getAttribute("y"));
                                urlImage = layer.getElementsByTagName("Image").item(0).getTextContent();
                                layers.add(new Layer(x, y, urlImage));
                            }
                            j++;
                        }
                        playerAvatars.add(new PlayerAvatar(playerID, bgImage, layers));
                        i++;
                    }
                }
            }
        } catch (Exception e) {
            HOLogger.instance().log(XMLAvatarsParser.class, e);
        }

        return playerAvatars;
    }

}
