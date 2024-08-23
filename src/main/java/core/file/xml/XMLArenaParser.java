package core.file.xml;

import core.util.HOLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;

public class XMLArenaParser {

    private static final String ELEMENT_NAME_FETCHED_DATE = "FetchedDate";
    private static final String ELEMENT_NAME_ARENA = "Arena";
    private static final String ELEMENT_NAME_ARENA_ID = "ArenaID";
    private static final String ELEMENT_NAME_ARENA_NAME = "ArenaName";
    private static final String ELEMENT_NAME_TEAM = "Team";
    private static final String ELEMENT_NAME_TEAM_ID = "TeamID";
    private static final String ELEMENT_NAME_TEAM_NAME = "TeamName";
    private static final String ELEMENT_NAME_LEAGUE = "League";
    private static final String ELEMENT_NAME_LEAGUE_ID = "LeagueID";
    private static final String ELEMENT_NAME_LEAGUE_NAME = "LeagueName";
    private static final String ELEMENT_NAME_REGION = "Region";
    private static final String ELEMENT_NAME_REGION_ID = "RegionID";
    private static final String ELEMENT_NAME_REGION_NAME = "RegionName";
    private static final String ELEMENT_NAME_CURRENT_CAPACITY = "CurrentCapacity";
    private static final String ELEMENT_NAME_CURRENT_CAPACITY_REBUILT_DATE = "RebuiltDate";

    private static final String ELEMENT_NAME_EXPANDED_CAPACITY = "ExpandedCapacity";
    private static final String ELEMENT_NAME_EXPANDED_CAPACITY_EXPANSION_DATE = "ExpansionDate";

    private static final String ATTRIBUTE_NAME_CAPACITY_AVAILABLE = "Available";
    private static final String ELEMENT_NAME_CAPACITY_TERRACES = "Terraces";
    private static final String ELEMENT_NAME_CAPACITY_BASIC = "Basic";
    private static final String ELEMENT_NAME_CAPACITY_ROOF = "Roof";
    private static final String ELEMENT_NAME_CAPACITY_VIP = "VIP";
    private static final String ELEMENT_NAME_CAPACITY_TOTAL = "Total";

    private static final String PROPERTY_NAME_FETCHED_DATE = "FetchedDate";
    private static final String PROPERTY_NAME_ARENA_ID = "ArenaID";
    private static final String PROPERTY_NAME_ARENA_NAME = "ArenaName";
    private static final String PROPERTY_NAME_TEAM_ID = "TeamID";
    private static final String PROPERTY_NAME_TEAM_NAME = "TeamName";
    private static final String PROPERTY_NAME_LEAGUE_ID = "LeagueID";
    private static final String PROPERTY_NAME_LEAGUE_NAME = "LeagueName";
    private static final String PROPERTY_NAME_REGION_ID = "RegionID";
    private static final String PROPERTY_NAME_REGION_NAME = "RegionName";

    private static final String PROPERTY_NAME_CURRENT_CAPACITY_REBUILT_DATE = "RebuiltDate";
    private static final String PROPERTY_NAME_CURRENT_CAPACITY_TERRACES = "Terraces";
    private static final String PROPERTY_NAME_CURRENT_CAPACITY_BASIC = "Basic";
    private static final String PROPERTY_NAME_CURRENT_CAPACITY_ROOF = "Roof";
    private static final String PROPERTY_NAME_CURRENT_CAPACITY_VIP = "VIP";
    private static final String PROPERTY_NAME_CURRENT_CAPACITY_TOTAL = "Total";

    private static final String PROPERTY_NAME_EXPANDING_CAPACITY_IS_EXPANDING = "isExpanding";
    private static final String PROPERTY_NAME_EXPANDING_CAPACITY_EXPANSION_DATE = "ExpansionDate";
    private static final String PROPERTY_NAME_EXPANDING_CAPACITY_TERRACES = "ExTerraces";
    private static final String PROPERTY_NAME_EXPANDING_CAPACITY_BASIC = "ExBasic";
    private static final String PROPERTY_NAME_EXPANDING_CAPACITY_ROOF = "ExRoof";
    private static final String PROPERTY_NAME_EXPANDING_CAPACITY_VIP = "ExVIP";
    private static final String PROPERTY_NAME_EXPANDING_CAPACITY_TOTAL = "ExTotal";

    private static final String PROPERTY_VALUE_ZERO = "0";
    private static final String PROPERTY_VALUE_ONE = "1";

    private XMLArenaParser() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Map<String, String> parseArenaFromString(String str) {
        return parseDetails(XMLManager.parseString(str));
    }

    private static Map<String, String> parseDetails(Document doc) {
        Map<String, String> map = new SafeInsertMap();

        if (doc == null) {
            return map;
        }

        try {
            Element root = doc.getDocumentElement();
            Element element = (Element) root.getElementsByTagName(ELEMENT_NAME_FETCHED_DATE).item(0);
            map.put(PROPERTY_NAME_FETCHED_DATE, XMLManager.getFirstChildNodeValue(element));

            // Root wechseln
            root = (Element) root.getElementsByTagName(ELEMENT_NAME_ARENA).item(0);
            element = (Element) root.getElementsByTagName(ELEMENT_NAME_ARENA_ID).item(0);
            map.put(PROPERTY_NAME_ARENA_ID, XMLManager.getFirstChildNodeValue(element));
            element = (Element) root.getElementsByTagName(ELEMENT_NAME_ARENA_NAME).item(0);
            map.put(PROPERTY_NAME_ARENA_NAME, XMLManager.getFirstChildNodeValue(element));

            Element tmpRoot = (Element) root.getElementsByTagName(ELEMENT_NAME_TEAM).item(
                    0);
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_TEAM_ID).item(0);
            map.put(PROPERTY_NAME_TEAM_ID, XMLManager.getFirstChildNodeValue(element));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_TEAM_NAME).item(0);
            map.put(PROPERTY_NAME_TEAM_NAME, XMLManager.getFirstChildNodeValue(element));

            tmpRoot = (Element) root.getElementsByTagName(ELEMENT_NAME_LEAGUE).item(0);
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_LEAGUE_ID).item(0);
            map.put(PROPERTY_NAME_LEAGUE_ID, XMLManager.getFirstChildNodeValue(element));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_LEAGUE_NAME).item(0);
            map.put(PROPERTY_NAME_LEAGUE_NAME, XMLManager.getFirstChildNodeValue(element));

            tmpRoot = (Element) root.getElementsByTagName(ELEMENT_NAME_REGION).item(0);
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_REGION_ID).item(0);
            map.put(PROPERTY_NAME_REGION_ID, XMLManager.getFirstChildNodeValue(element));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_REGION_NAME).item(0);
            map.put(PROPERTY_NAME_REGION_NAME, XMLManager.getFirstChildNodeValue(element));

            tmpRoot = (Element) root.getElementsByTagName(ELEMENT_NAME_CURRENT_CAPACITY).item(0);
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CURRENT_CAPACITY_REBUILT_DATE).item(0);

            final boolean rebuiltDateAvailable = getXmlAttributeAsBoolean(element, ATTRIBUTE_NAME_CAPACITY_AVAILABLE);
            if (rebuiltDateAvailable) {
                map.put(PROPERTY_NAME_CURRENT_CAPACITY_REBUILT_DATE, XMLManager.getFirstChildNodeValue(element));
            }

            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_TERRACES).item(0);
            map.put(PROPERTY_NAME_CURRENT_CAPACITY_TERRACES, XMLManager.getFirstChildNodeValue(element));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_BASIC).item(0);
            map.put(PROPERTY_NAME_CURRENT_CAPACITY_BASIC, XMLManager.getFirstChildNodeValue(element));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_ROOF).item(0);
            map.put(PROPERTY_NAME_CURRENT_CAPACITY_ROOF, XMLManager.getFirstChildNodeValue(element));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_VIP).item(0);
            map.put(PROPERTY_NAME_CURRENT_CAPACITY_VIP, XMLManager.getFirstChildNodeValue(element));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_TOTAL).item(0);
            map.put(PROPERTY_NAME_CURRENT_CAPACITY_TOTAL, XMLManager.getFirstChildNodeValue(element));

            tmpRoot = (Element) root.getElementsByTagName(ELEMENT_NAME_EXPANDED_CAPACITY).item(0);

            final boolean expandedCapacityAvailable = getXmlAttributeAsBoolean(tmpRoot, ATTRIBUTE_NAME_CAPACITY_AVAILABLE);
            if (expandedCapacityAvailable) {
                map.put(PROPERTY_NAME_EXPANDING_CAPACITY_IS_EXPANDING, PROPERTY_VALUE_ONE);
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_EXPANDED_CAPACITY_EXPANSION_DATE).item(0);
                map.put(PROPERTY_NAME_EXPANDING_CAPACITY_EXPANSION_DATE, XMLManager.getFirstChildNodeValue(element));
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_TERRACES).item(0);
                map.put(PROPERTY_NAME_EXPANDING_CAPACITY_TERRACES, XMLManager.getFirstChildNodeValue(element));
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_BASIC).item(0);
                map.put(PROPERTY_NAME_EXPANDING_CAPACITY_BASIC, XMLManager.getFirstChildNodeValue(element));
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_ROOF).item(0);
                map.put(PROPERTY_NAME_EXPANDING_CAPACITY_ROOF, XMLManager.getFirstChildNodeValue(element));
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_VIP).item(0);
                map.put(PROPERTY_NAME_EXPANDING_CAPACITY_VIP, XMLManager.getFirstChildNodeValue(element));
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_TOTAL).item(0);
                map.put(PROPERTY_NAME_EXPANDING_CAPACITY_TOTAL, XMLManager.getFirstChildNodeValue(element));
            } else {
                map.put(PROPERTY_NAME_EXPANDING_CAPACITY_IS_EXPANDING, PROPERTY_VALUE_ZERO);
                map.put(PROPERTY_NAME_EXPANDING_CAPACITY_EXPANSION_DATE, PROPERTY_VALUE_ZERO);
                map.put(PROPERTY_NAME_EXPANDING_CAPACITY_TERRACES, PROPERTY_VALUE_ZERO);
                map.put(PROPERTY_NAME_EXPANDING_CAPACITY_BASIC, PROPERTY_VALUE_ZERO);
                map.put(PROPERTY_NAME_EXPANDING_CAPACITY_ROOF, PROPERTY_VALUE_ZERO);
                map.put(PROPERTY_NAME_EXPANDING_CAPACITY_VIP, PROPERTY_VALUE_ZERO);
                map.put(PROPERTY_NAME_EXPANDING_CAPACITY_TOTAL, PROPERTY_VALUE_ZERO);
            }
        } catch (Exception e) {
            HOLogger.instance().log(XMLArenaParser.class, e);
        }

        return map;
    }

    @SuppressWarnings(value = "SameParameterValue")
    private static boolean getXmlAttributeAsBoolean(Element element, String attributeName) {
        return Boolean.parseBoolean(XMLManager.getAttributeValue(element, attributeName).trim());
    }
}
