package core.file.xml;

import core.model.misc.TrainingEvent;
import core.util.HOLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XMLTrainingEventsParser {

    private static void extract(Element root, Map<String, String> map, String key) {
        map.put(key, (XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName(key).item(0))));
    }

    public static List<TrainingEvent> parseTrainingEvents(String xml) {
        return parseDetails(XMLManager.parseString(xml));
    }

    private static List<TrainingEvent> parseDetails(Document doc) {
        try {
            Element root = doc.getDocumentElement();
            Element ele = (Element) root.getElementsByTagName("Player").item(0);
            ele = (Element) ele.getElementsByTagName("TrainingEvents").item(0);
            NodeList elements = ele.getElementsByTagName("TrainingEvent");

            ArrayList<TrainingEvent> ret = new ArrayList<TrainingEvent>();
            for (int i = 0; i < elements.getLength(); i++) {
                ret.add(new TrainingEvent(parseElementDetails((Element) elements.item(i))));
            }

            return ret;

        } catch (Exception e) {
            HOLogger.instance().log(XMLTrainingEventsParser.class, e);
        }

        return null;
    }

    private static Map<String, String> parseElementDetails(Element item) {

        Map<String, String> map = new MyHashtable();

        extract(item, map, "SkillID");
        extract(item, map, "OldLevel");
        extract(item, map, "NewLevel");
        extract(item, map, "Season");
        extract(item, map, "MatchRound");
        extract(item, map, "DayNumber");
        extract(item, map, "EventDate");

        return map;
    }

}
