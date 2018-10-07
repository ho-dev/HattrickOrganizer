// %2347050307:de.hattrickorganizer.logik.xml%
/*
 * xmlMatchArchivParser.java
 *
 * Created on 28. Juli 2004, 15:32
 */
package core.file.xml;

import core.model.match.MatchKurzInfo;
import core.model.match.MatchType;
import core.util.HOLogger;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author TheTom
 */
public class XMLMatchArchivParser {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLMatchArchivParser() {
	}

	public static List<MatchKurzInfo> parseMatchesFromString(String input) {
		return createMatches(XMLManager.parseString(input));
	}

	private static List<MatchKurzInfo> createMatches(Document doc) {
		List<MatchKurzInfo> matches = new ArrayList<MatchKurzInfo>();		

		if (doc == null) {
			return matches;
		}

		Element root = doc.getDocumentElement();
		Element ele = null;
		Element tmp = null;
		MatchKurzInfo match = null;
		try {
			NodeList  nodeList = root.getElementsByTagName("Match");

			for (int i = 0; (nodeList != null) && (i < nodeList.getLength()); i++) {
				match = new MatchKurzInfo();
				ele = (Element) nodeList.item(i);

				// Daten füllen
				tmp = (Element) ele.getElementsByTagName("MatchDate").item(0);
				match.setMatchDate(tmp.getFirstChild().getNodeValue());
				tmp = (Element) ele.getElementsByTagName("MatchID").item(0);
				match.setMatchID(Integer.parseInt(tmp.getFirstChild()
						.getNodeValue()));
				tmp = (Element) ele.getElementsByTagName("MatchType").item(0);
				match.setMatchTyp(MatchType.getById(Integer.parseInt(tmp
						.getFirstChild().getNodeValue())));
				tmp = (Element) ele.getElementsByTagName("HomeTeam").item(0);
				match.setHeimID(Integer.parseInt(((Element) tmp
						.getElementsByTagName("HomeTeamID").item(0))
						.getFirstChild().getNodeValue()));
				match.setHeimName(((Element) tmp.getElementsByTagName(
						"HomeTeamName").item(0)).getFirstChild().getNodeValue());
				tmp = (Element) ele.getElementsByTagName("AwayTeam").item(0);
				match.setGastID(Integer.parseInt(((Element) tmp
						.getElementsByTagName("AwayTeamID").item(0))
						.getFirstChild().getNodeValue()));
				match.setGastName(((Element) tmp.getElementsByTagName(
						"AwayTeamName").item(0)).getFirstChild().getNodeValue());
				tmp = (Element) ele.getElementsByTagName("HomeGoals").item(0);
				match.setHeimTore(Integer.parseInt(tmp.getFirstChild()
						.getNodeValue()));
				tmp = (Element) ele.getElementsByTagName("AwayGoals").item(0);
				match.setGastTore(Integer.parseInt(tmp.getFirstChild()
						.getNodeValue()));
				match.setOrdersGiven(true);
				match.setMatchStatus(MatchKurzInfo.FINISHED);
				matches.add(match);
			}
		} catch (Exception e) {
			matches.clear();
			HOLogger.instance().log(XMLMatchArchivParser.class, e);			
		}
		return matches;
	}
}
