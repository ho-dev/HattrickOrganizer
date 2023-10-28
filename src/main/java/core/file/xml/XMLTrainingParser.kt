// %3623740893:de.hattrickorganizer.logik.xml%
/*
 * XMLTrainingParser.java
 *
 * Created on 27. Mai 2004, 07:43
 */
package core.file.xml;

import core.util.HOLogger;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parser for trainig xml data.
 * 
 * @author thomas.werth
 */
public class XMLTrainingParser {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLTrainingParser() {
	}

	/**
	 * Parse the training from the given xml string.
	 */
	public static Map<String, String> parseTrainingFromString(String string) {
		return parseDetails(XMLManager.parseString(string));
	}

	/**
	 * erstellt das MAtchlineup Objekt
	 */
	private static Map<String, String> parseDetails(Document doc) {
		Map<String, String> map = new MyHashtable();

		if (doc == null) {
			return map;
		}

		try {
			Element root = doc.getDocumentElement();
			Element ele = (Element) root.getElementsByTagName("FetchedDate").item(0);
			map.put("FetchedDate", (XMLManager.getFirstChildNodeValue(ele)));

			root = (Element) root.getElementsByTagName("Team").item(0);
			ele = (Element) root.getElementsByTagName("TeamID").item(0);
			map.put("TeamID", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("TeamName").item(0);
			map.put("TeamName", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("TrainingLevel").item(0);
			map.put("TrainingLevel", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("StaminaTrainingPart").item(0);
			map.put("StaminaTrainingPart", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("NewTrainingLevel ").item(0);

			if (XMLManager.getAttributeValue(ele, "Available").trim().equalsIgnoreCase("true")) {
				map.put("NewTrainingLevel ", (XMLManager.getFirstChildNodeValue(ele)));
			}

			ele = (Element) root.getElementsByTagName("TrainingType").item(0);
			map.put("TrainingType", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("Morale").item(0);

			if (XMLManager.getAttributeValue(ele, "Available").trim().equalsIgnoreCase("true")) {
				map.put("Morale", (XMLManager.getFirstChildNodeValue(ele)));
			}

			ele = (Element) root.getElementsByTagName("SelfConfidence").item(0);

			if (XMLManager.getAttributeValue(ele, "Available").trim().equalsIgnoreCase("true")) {
				map.put("SelfConfidence", (XMLManager.getFirstChildNodeValue(ele)));
			}

			ele = (Element) root.getElementsByTagName("Experience433").item(0);
			map.put("Experience433", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("Experience451").item(0);
			map.put("Experience451", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("Experience352").item(0);
			map.put("Experience352", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("Experience532").item(0);
			map.put("Experience532", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("Experience343").item(0);
			map.put("Experience343", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("Experience541").item(0);
			map.put("Experience541", (XMLManager.getFirstChildNodeValue(ele)));

			// new formation experiences since training xml version 1.5
			try {
				ele = (Element) root.getElementsByTagName("Experience442").item(0);
				map.put("Experience442", (XMLManager.getFirstChildNodeValue(ele)));
			} catch (Exception e) {
				HOLogger.instance().log(XMLTrainingParser.class, "Err(Experience442): " + e);
			}
			try {
				ele = (Element) root.getElementsByTagName("Experience523").item(0);
				map.put("Experience523", (XMLManager.getFirstChildNodeValue(ele)));
			} catch (Exception e) {
				HOLogger.instance().log(XMLTrainingParser.class, "Err(Experience523): " + e);
			}
			try {
				ele = (Element) root.getElementsByTagName("Experience550").item(0);
				map.put("Experience550", (XMLManager.getFirstChildNodeValue(ele)));
			} catch (Exception e) {
				HOLogger.instance().log(XMLTrainingParser.class, "Err(Experience550): " + e);
			}
			try {
				ele = (Element) root.getElementsByTagName("Experience253").item(0);
				map.put("Experience253", (XMLManager.getFirstChildNodeValue(ele)));
			} catch (Exception e) {
				HOLogger.instance().log(XMLTrainingParser.class, "Err(Experience253): " + e);
			}

			root = (Element) root.getElementsByTagName("Trainer").item(0);
			ele = (Element) root.getElementsByTagName("TrainerID").item(0);
			map.put("TrainerID", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("TrainerName").item(0);
			map.put("TrainerName", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("ArrivalDate").item(0);
			map.put("ArrivalDate", (XMLManager.getFirstChildNodeValue(ele)));
		} catch (Exception e) {
			HOLogger.instance().log(XMLTrainingParser.class, e);
		}

		return map;
	}
}
