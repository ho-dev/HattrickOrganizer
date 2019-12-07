package core.file.xml;

import core.util.HOLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;

public class XMLRegionParser {
    private XMLRegionParser() {
    }


    public static Map<String, String> parseRegionDetailsFromString(String str) {
        return parseDetails(XMLManager.parseString(str));
    }

    private static Map<String, String> parseDetails(Document doc) {
        Map<String, String> map = new MyHashtable();

        if (doc == null) {
            return map;
        }

        try {
            Element root = doc.getDocumentElement();
            Element ele = (Element) root.getElementsByTagName("FetchedDate").item(0);
            map.put("FetchedDate", (XMLManager.getFirstChildNodeValue(ele)));

            // Get Region Data info
            root = (Element) root.getElementsByTagName("League").item(0);
            extract(root, map, "LeagueID");
            extract(root, map, "LeagueName");
            root = (Element) root.getElementsByTagName("Region").item(0);
            extract(root, map, "RegionID");
            extract(root, map, "RegionName");
            extract(root, map, "NumberOfUsers");
            extract(root, map, "NumberOfOnline");
            extract(root, map, "WeatherID");
            extract(root, map, "TomorrowWeatherID ");

        } catch (Exception e) {
            HOLogger.instance().log(XMLRegionParser.class, e);
        }

        return map;
    }

    private static void extract(Element root, Map<String, String> map, String key) {
        map.put(key, (XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName(key).item(0))));
    }

}
