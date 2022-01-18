package core.file.xml;

import core.model.match.MatchKurzInfo;
import core.util.HOLogger;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;

public class XMLAvatarsParser {

    /**
     * Utility class - private constructor enforces noninstantiability.
     */
    private XMLAvatarsParser() {
    }

    public static Map<String, String> parseAvatarsFromString(String str) {
        return parseDetails(XMLManager.parseString(str));
    }

    private static Map<String, String> parseDetails(Document doc) {

        Map<String, String> map = new MyHashtable();

        if (doc == null) {
            return map;
        }

        try {
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            NodeList players = root.getElementsByTagName("Player");

            if (players != null) {
                int i = 0;
                Element player, layer, avatar;
                Node node, node2;
                NodeList layers;
                while (i < players.getLength()) {
                    node = players.item(i);
                    System.out.println("");
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        //Print each employee's detail
                        player = (Element) node;
//                        System.out.println("Employee id : "    + player.getAttribute("PlayerID"));
                        System.out.println("Player ID: "  + player.getElementsByTagName("PlayerID").item(0).getTextContent());


                        System.out.println("Background image: "  + player.getElementsByTagName("BackgroundImage").item(0).getTextContent());

                        // Get all layers ====================
                        layers = player.getElementsByTagName("Layer");
                        int j = 0;
                        while (j < layers.getLength()) {
                            node2 = layers.item(j);
                            if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                layer = (Element) node2;
                                System.out.println("x: "  + layer.getAttribute("x"));
                                System.out.println("y: "  + layer.getAttribute("y"));
                                System.out.println("img: "  + layer.getElementsByTagName("Image").item(0).getTextContent());
                            }
                            j++;
                        }
//                        ((Element)player.getElementsByTagName("Layer").item(0)).getAttribute("x")

//                        System.out.println("Player ID: "  + player.getElementsByTagName("layers").item(0).getTextContent());
//                        System.out.println("Last Name : "   + player.getElementsByTagName("lastName").item(0).getTextContent());
//                        System.out.println("Location : "    + player.getElementsByTagName("")
                        i++;
                    }
                }
            }
        } catch (Exception e) {
            HOLogger.instance().log(XMLAvatarsParser.class, e);
        }

        return map;
    }

}
