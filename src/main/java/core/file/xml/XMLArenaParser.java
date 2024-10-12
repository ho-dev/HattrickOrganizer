package core.file.xml;

import core.util.HODateTime;
import core.util.HOLogger;
import hattrickdata.*;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLArenaParser {

    private static final String ELEMENT_NAME_FILE_NAME = "FileName";
    private static final String ELEMENT_NAME_VERSION = "Version";
    private static final String ELEMENT_NAME_USER_ID = "UserID";
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

    private XMLArenaParser() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Pair<HattrickDataInfo, Arena> parseArenaFromString(String str) {
        return parseDetails(XMLManager.parseString(str));
    }

    private static Pair<HattrickDataInfo, Arena> parseDetails(Document doc) {
        if (doc == null) {
            return null;
        }

        try {
            var hattrickDataInfoBuilder = HattrickDataInfo.builder();

            Element root = doc.getDocumentElement();

            // FileName
            Element element = (Element) root.getElementsByTagName(ELEMENT_NAME_FILE_NAME).item(0);
            hattrickDataInfoBuilder.fileName(XMLManager.getFirstChildNodeValue(element));
            // Version
            element = (Element) root.getElementsByTagName(ELEMENT_NAME_VERSION).item(0);
            hattrickDataInfoBuilder.version(XMLManager.getFirstChildNodeValue(element));
            // UserId
            element = (Element) root.getElementsByTagName(ELEMENT_NAME_USER_ID).item(0);
            hattrickDataInfoBuilder.userId(Integer.parseInt(XMLManager.getFirstChildNodeValue(element)));
            // FetchedDate
            element = (Element) root.getElementsByTagName(ELEMENT_NAME_FETCHED_DATE).item(0);
            hattrickDataInfoBuilder.fetchedDate(HODateTime.fromHT(XMLManager.getFirstChildNodeValue(element)));

            final var hattrickDataInfo = hattrickDataInfoBuilder.build();

            // Root wechseln
            root = (Element) root.getElementsByTagName(ELEMENT_NAME_ARENA).item(0);
            element = (Element) root.getElementsByTagName(ELEMENT_NAME_ARENA_ID).item(0);

            var arenaBuilder = Arena.builder();
            arenaBuilder.id(Integer.parseInt(XMLManager.getFirstChildNodeValue(element)));
            element = (Element) root.getElementsByTagName(ELEMENT_NAME_ARENA_NAME).item(0);
            arenaBuilder.name(XMLManager.getFirstChildNodeValue(element));

            // Team
            Element tmpRoot = (Element) root.getElementsByTagName(ELEMENT_NAME_TEAM).item(0);
            var teamBuilder = Team.builder();
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_TEAM_ID).item(0);
            teamBuilder.id(Integer.parseInt(XMLManager.getFirstChildNodeValue(element)));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_TEAM_NAME).item(0);
            teamBuilder.name(XMLManager.getFirstChildNodeValue(element));
            arenaBuilder.team(teamBuilder.build());

            // League
            var leagueBuilder = League.builder();
            tmpRoot = (Element) root.getElementsByTagName(ELEMENT_NAME_LEAGUE).item(0);
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_LEAGUE_ID).item(0);
            leagueBuilder.id(Integer.parseInt(XMLManager.getFirstChildNodeValue(element)));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_LEAGUE_NAME).item(0);
            leagueBuilder.name(XMLManager.getFirstChildNodeValue(element));
            arenaBuilder.league(leagueBuilder.build());

            // Region
            var regionBuilder = Region.builder();
            tmpRoot = (Element) root.getElementsByTagName(ELEMENT_NAME_REGION).item(0);
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_REGION_ID).item(0);
            regionBuilder.id(Integer.parseInt(XMLManager.getFirstChildNodeValue(element)));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_REGION_NAME).item(0);
            regionBuilder.name(XMLManager.getFirstChildNodeValue(element));
            arenaBuilder.region(regionBuilder.build());

            // Current Capacity
            tmpRoot = (Element) root.getElementsByTagName(ELEMENT_NAME_CURRENT_CAPACITY).item(0);
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CURRENT_CAPACITY_REBUILT_DATE).item(0);

            var currentCapacityBuilder = CurrentCapacity.builder();
            final boolean rebuiltDateAvailable = getXmlAttributeAsBoolean(element, ATTRIBUTE_NAME_CAPACITY_AVAILABLE);
            if (rebuiltDateAvailable) {
                currentCapacityBuilder.rebuildDate(HODateTime.fromHT(XMLManager.getFirstChildNodeValue(element)));
            }

            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_TERRACES).item(0);
            currentCapacityBuilder.terraces(Integer.parseInt(XMLManager.getFirstChildNodeValue(element)));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_BASIC).item(0);
            currentCapacityBuilder.basic(Integer.parseInt(XMLManager.getFirstChildNodeValue(element)));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_ROOF).item(0);
            currentCapacityBuilder.roof(Integer.parseInt(XMLManager.getFirstChildNodeValue(element)));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_VIP).item(0);
            currentCapacityBuilder.vip(Integer.parseInt(XMLManager.getFirstChildNodeValue(element)));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_TOTAL).item(0);
            currentCapacityBuilder.total(Integer.parseInt(XMLManager.getFirstChildNodeValue(element)));

            arenaBuilder.currentCapacity(currentCapacityBuilder.build());

            tmpRoot = (Element) root.getElementsByTagName(ELEMENT_NAME_EXPANDED_CAPACITY).item(0);

            final boolean expandedCapacityAvailable = getXmlAttributeAsBoolean(tmpRoot, ATTRIBUTE_NAME_CAPACITY_AVAILABLE);
            if (expandedCapacityAvailable) {
                var expandedCapacityBuilder = ExpandedCapacity.builder();
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_EXPANDED_CAPACITY_EXPANSION_DATE).item(0);
                expandedCapacityBuilder.expansionDate(HODateTime.fromHT(XMLManager.getFirstChildNodeValue(element)));
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_TERRACES).item(0);
                expandedCapacityBuilder.terraces(Integer.parseInt(XMLManager.getFirstChildNodeValue(element)));
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_BASIC).item(0);
                expandedCapacityBuilder.basic(Integer.parseInt(XMLManager.getFirstChildNodeValue(element)));
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_ROOF).item(0);
                expandedCapacityBuilder.roof(Integer.parseInt(XMLManager.getFirstChildNodeValue(element)));
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_VIP).item(0);
                expandedCapacityBuilder.vip(Integer.parseInt(XMLManager.getFirstChildNodeValue(element)));
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_TOTAL).item(0);
                expandedCapacityBuilder.total(Integer.parseInt(XMLManager.getFirstChildNodeValue(element)));

                arenaBuilder.expandedCapacity(expandedCapacityBuilder.build());
            }

            return Pair.of(hattrickDataInfo, arenaBuilder.build());
        } catch (Exception e) {
            HOLogger.instance().log(XMLArenaParser.class, e);
        }

        return null;
    }

    @SuppressWarnings(value = "SameParameterValue")
    private static boolean getXmlAttributeAsBoolean(Element element, String attributeName) {
        return Boolean.parseBoolean(XMLManager.getAttributeValue(element, attributeName).trim());
    }
}
