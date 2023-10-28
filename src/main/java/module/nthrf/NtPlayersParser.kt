package module.nthrf;

import core.file.xml.MyHashtable;
import core.file.xml.XMLManager;
import core.net.MyConnector;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static core.file.xml.XMLManager.xmlValue;
import static core.file.xml.XMLPlayersParser.createPlayerDetails;

class NtPlayersParser {

	/**
	 * Parse player details and store the IDs and Players in local objects.
	 */
	private static List<MyHashtable>  parsePlayerDetails(List<String> playerIds) {
		List<MyHashtable> ret = new ArrayList<>();
		try {
			for (var playerId : playerIds) {
				String xmlData = MyConnector.instance().downloadPlayerDetails(playerId);
				Document doc = XMLManager.parseString(xmlData);
				Element root = doc.getDocumentElement();
				Element ele = (Element) root.getElementsByTagName("Player").item(0);
				ret.add(createPlayerDetails(ele));
			}
		} catch (Exception e) {
        	e.printStackTrace();
        }
		return ret;
	}

	private static List<String> parseBasics(Document doc) {
		var ret = new ArrayList<String>();
        if (doc != null) {
			try {
				Element root = doc.getDocumentElement();
//
//				var fetchedDate = xmlValue(root,"FetchedDate" );
//				var teamId = xmlValue(root,"TeamID" );
//				var teamName = xmlValue(root,"TeamName" );
//
				// players
				root = (Element) root.getElementsByTagName("Players").item(0);
				NodeList playersNode = root.getElementsByTagName("Player");
				for (int m = 0; m < playersNode.getLength(); m++) {
					var ele = (Element) playersNode.item(m);
					ret.add(xmlValue(ele, "PlayerID"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public static List<MyHashtable> parsePlayersFromString(String xml) {
			var ids = parseBasics(XMLManager.parseString(xml));
			return parsePlayerDetails(ids);
	}
}
