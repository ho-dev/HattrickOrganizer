package core.file.xml;

import core.util.HOLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;

public class XMLRatingParser {

    private XMLRatingParser() {
    }

    public static Map<String, String> parsePredictionRatingFromString(String str) {
        return parseDetails(XMLManager.parseString(str));
    }

    private static Map<String, String> parseDetails(Document doc) {
        Map<String, String> map = new SafeInsertMap();

        if (doc == null) {
            return map;
        }

        try {
            Element root = doc.getDocumentElement();
            Element ele = (Element) root.getElementsByTagName("FetchedDate").item(0);
            map.put("FetchedDate", (XMLManager.getFirstChildNodeValue(ele)));

            // Get Match Data info
            root = (Element) root.getElementsByTagName("MatchData").item(0);
            ele = (Element) root.getElementsByTagName("TacticType").item(0);
            map.put("TacticType", (XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) root.getElementsByTagName("TacticSkill").item(0);
            map.put("TacticSkill", (XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) root.getElementsByTagName("RatingMidfield").item(0);
            map.put("RatingMidfield", (XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) root.getElementsByTagName("RatingRightDef").item(0);
            map.put("RatingRightDef", (XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) root.getElementsByTagName("RatingMidDef").item(0);
            map.put("RatingMidDef", (XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) root.getElementsByTagName("RatingLeftDef").item(0);
            map.put("RatingLeftDef", (XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) root.getElementsByTagName("RatingRightAtt").item(0);
            map.put("RatingRightAtt", (XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) root.getElementsByTagName("RatingMidAtt").item(0);
            map.put("RatingMidAtt", (XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) root.getElementsByTagName("RatingLeftAtt").item(0);
            map.put("RatingLeftAtt", (XMLManager.getFirstChildNodeValue(ele)));

        } catch (Exception e) {
            HOLogger.instance().log(XMLArenaParser.class, e);
        }

        return map;
    }
}
