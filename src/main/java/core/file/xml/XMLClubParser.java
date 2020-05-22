// %1180669679:de.hattrickorganizer.logik.xml%
/*
 * XMLClubParser.java
 *
 * Created on 4. November 2003, 14:37
 */
package core.file.xml;

import core.model.misc.Verein;
import core.util.HOLogger;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author thomas.werth
 */
public class XMLClubParser {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
    private XMLClubParser() {
    }
    

    public static Map<String, String> parseClubFromString(String inputStream) {
        return parseDetails(XMLManager.parseString(inputStream));
    }

    private static Map<String, String> parseDetails(Document doc) {
        Map<String, String> map = new MyHashtable();

        if (doc == null) {
            return map;
        }

        try {
        	Element root = doc.getDocumentElement();
            //Daten füllen
        	Element ele = (Element) root.getElementsByTagName("FetchedDate").item(0);
            map.put("FetchedDate", XMLManager.getFirstChildNodeValue(ele));

            //Root wechseln
            root = (Element) root.getElementsByTagName("Team").item(0);
            ele = (Element) root.getElementsByTagName("TeamID").item(0);
            map.put("TeamID", XMLManager.getFirstChildNodeValue(ele));
            ele = (Element) root.getElementsByTagName("TeamName").item(0);
            map.put("TeamName", XMLManager.getFirstChildNodeValue(ele));

            //nochmal root wechseln
            root = (Element) doc.getDocumentElement().getElementsByTagName("Staff").item(0);
            ele = (Element) root.getElementsByTagName("MedicLevels").item(0);
            map.put("Doctors", XMLManager.getFirstChildNodeValue(ele));
            ele = (Element) root.getElementsByTagName("SpokespersonLevels").item(0);
            map.put("PressSpokesmen", XMLManager.getFirstChildNodeValue(ele));
            ele = (Element) root.getElementsByTagName("AssistantTrainerLevels").item(0);
            map.put("AssistantTrainers", XMLManager.getFirstChildNodeValue(ele));
//            ele = (Element) root.getElementsByTagName("Physiotherapists").item(0); // Removed in v1.5
//            map.put("Physiotherapists", XMLManager.getFirstChildNodeValue(ele));
            ele = (Element) root.getElementsByTagName("SportPsychologistLevels").item(0);
            map.put("Psychologists", XMLManager.getFirstChildNodeValue(ele));
            ele = (Element) root.getElementsByTagName("FinancialDirectorLevels").item(0);
            map.put("FinancialDirectorLevels", XMLManager.getFirstChildNodeValue(ele));
            ele = (Element) root.getElementsByTagName("FormCoachLevels").item(0);
            map.put("FormCoachLevels", XMLManager.getFirstChildNodeValue(ele));
            ele = (Element) root.getElementsByTagName("TacticalAssistantLevels").item(0);
            map.put("TacticalAssistantLevels", XMLManager.getFirstChildNodeValue(ele));

            //und nochmal root wechseln
            root = (Element) doc.getDocumentElement().getElementsByTagName("YouthSquad").item(0);
            ele = (Element) root.getElementsByTagName("Investment").item(0);
            map.put("Investment", XMLManager.getFirstChildNodeValue(ele));
            ele = (Element) root.getElementsByTagName("HasPromoted").item(0);
            map.put("HasPromoted", XMLManager.getFirstChildNodeValue(ele));
            ele = (Element) root.getElementsByTagName("YouthLevel").item(0);
            map.put("YouthLevel", XMLManager.getFirstChildNodeValue(ele));
        } catch (Exception e) {
            HOLogger.instance().log(XMLClubParser.class,e);
            map = null;
        }

        return map;
    }
}
