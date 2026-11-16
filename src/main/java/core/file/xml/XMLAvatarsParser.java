package core.file.xml;

import core.model.player.Layer;
import core.model.player.PlayerAvatar;
import core.util.HOLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class XMLAvatarsParser {

    /**
     * Utility class - private constructor enforces noninstantiability.
     */
    public XMLAvatarsParser() {
    }

    public static List<PlayerAvatar>  parseAvatarsFromString(String str) {
        return parseDetails(XMLManager.parseString(str), "Player", "PlayerID");
    }

    /** youthavatars 1.2: the same avatar tree under YouthPlayer/YouthPlayerID. */
    public static List<PlayerAvatar> parseYouthAvatarsFromString(String str) {
        return parseDetails(XMLManager.parseString(str), "YouthPlayer", "YouthPlayerID");
    }

    /** Status overlays (training intensity, injury) that are not part of the face. */
    private static final Pattern OVERLAYS = Pattern.compile("t[1-6]\\.png|injury\\.png|injuredbutplaying\\.png");

    private static List<PlayerAvatar> parseDetails(Document doc, String playerTag, String playerIdTag) {
        List<PlayerAvatar> playerAvatars = new ArrayList<>();
        if (doc == null) {
            return playerAvatars;
        }
        try {
            doc.getDocumentElement().normalize();
            NodeList players = doc.getDocumentElement().getElementsByTagName(playerTag);
            for (int i = 0; i < players.getLength(); i++) {
                if (players.item(i) instanceof Element player) {
                    playerAvatars.add(avatar(player, playerIdTag));
                }
            }
        } catch (Exception e) {
            HOLogger.instance().log(XMLAvatarsParser.class, e);
        }
        return playerAvatars;
    }

    private static PlayerAvatar avatar(Element player, String playerIdTag) {
        final int playerID = Integer.parseInt(player.getElementsByTagName(playerIdTag).item(0).getTextContent());
        final String bgImage = player.getElementsByTagName("BackgroundImage").item(0).getTextContent();
        final List<Layer> layers = new ArrayList<>();
        final NodeList nlLayers = player.getElementsByTagName("Layer");
        for (int j = 0; j < nlLayers.getLength(); j++) {
            if (nlLayers.item(j) instanceof Element layer) {
                final String urlImage = layer.getElementsByTagName("Image").item(0).getTextContent();
                if (!OVERLAYS.matcher(urlImage).find()) {
                    layers.add(new Layer(Integer.parseInt(layer.getAttribute("x")),
                            Integer.parseInt(layer.getAttribute("y")), urlImage));
                }
            }
        }
        return new PlayerAvatar(playerID, bgImage, layers);
    }

}
