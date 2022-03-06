// %2347050307:de.hattrickorganizer.logik.xml%
/*
 * xmlMatchArchivParser.java
 *
 * Created on 28. Juli 2004, 15:32
 */
package core.file.xml;

import core.db.DBManager;
import core.model.Tournament.TournamentDetails;
import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.match.MatchKurzInfo;
import core.model.enums.MatchType;
import core.util.HODateTime;
import core.util.HOLogger;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static core.net.OnlineWorker.getTournamentDetails;

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
		List<MatchKurzInfo> matches = new ArrayList<>();

		if (doc == null) {
			return matches;
		}

		Element root = doc.getDocumentElement();
		Element ele;
		Element tmp;
		MatchKurzInfo match;
		int iMatchType;
		int iCupLevel;
		int iCupLevelIndex;
		try {

			var isYouth = Boolean.parseBoolean(root.getElementsByTagName("IsYouth").item(0).getFirstChild().getNodeValue());
			NodeList  nodeList = root.getElementsByTagName("Match");

			for (int i = 0; (nodeList != null) && (i < nodeList.getLength()); i++) {
				match = new MatchKurzInfo();
				ele = (Element) nodeList.item(i);

				// Daten füllen
				tmp = (Element) ele.getElementsByTagName("MatchDate").item(0);
				match.setMatchSchedule(HODateTime.fromHT(tmp.getFirstChild().getNodeValue()));
				tmp = (Element) ele.getElementsByTagName("MatchID").item(0);
				match.setMatchID(Integer.parseInt(tmp.getFirstChild()
						.getNodeValue()));
				tmp = (Element) ele.getElementsByTagName("MatchType").item(0);
				if( tmp!=null || !isYouth)
				{
					iMatchType = Integer.parseInt(tmp.getFirstChild().getNodeValue());
				}
				else
				{
					// workaround for isyouth=true (MatchType is missing if isYouth==true)
					iMatchType = MatchType.YOUTHLEAGUE.getId();
				}

				match.setMatchType(MatchType.getById(iMatchType));

				if (iMatchType == 3) {
					tmp = (Element) ele.getElementsByTagName("CupLevel").item(0);
					iCupLevel = Integer.parseInt(tmp.getFirstChild().getNodeValue());
					match.setCupLevel(CupLevel.fromInt(iCupLevel));
					tmp = (Element) ele.getElementsByTagName("CupLevelIndex").item(0);
					iCupLevelIndex = Integer.parseInt(tmp.getFirstChild().getNodeValue());
					match.setCupLevelIndex(CupLevelIndex.fromInt(iCupLevelIndex));
				}
				else if (iMatchType == 50) {
					tmp = (Element) ele.getElementsByTagName("MatchContextId").item(0);
					int tournamentId = Integer.parseInt(tmp.getFirstChild().getNodeValue());
					match.setMatchContextId(tournamentId);

					TournamentDetails oTournamentDetails = DBManager.instance().getTournamentDetailsFromDB(tournamentId);
					if (oTournamentDetails == null)
					{
						oTournamentDetails = getTournamentDetails(tournamentId); // download info about tournament from HT
						DBManager.instance().storeTournamentDetailsIntoDB(oTournamentDetails); // store tournament details into DB
					}
					match.setTournamentTypeID(oTournamentDetails.getTournamentType());
				}

				tmp = (Element) ele.getElementsByTagName("HomeTeam").item(0);
				match.setHomeTeamID(Integer.parseInt(tmp
						.getElementsByTagName("HomeTeamID").item(0)
						.getFirstChild().getNodeValue()));
				match.setHomeTeamName(tmp.getElementsByTagName(
						"HomeTeamName").item(0).getFirstChild().getNodeValue());
				tmp = (Element) ele.getElementsByTagName("AwayTeam").item(0);
				match.setGuestTeamID(Integer.parseInt(tmp
						.getElementsByTagName("AwayTeamID").item(0)
						.getFirstChild().getNodeValue()));
				match.setGuestTeamName(tmp.getElementsByTagName(
						"AwayTeamName").item(0).getFirstChild().getNodeValue());
				tmp = (Element) ele.getElementsByTagName("HomeGoals").item(0);
				match.setHomeTeamGoals(Integer.parseInt(tmp.getFirstChild()
						.getNodeValue()));
				tmp = (Element) ele.getElementsByTagName("AwayGoals").item(0);
				match.setGuestTeamGoals(Integer.parseInt(tmp.getFirstChild()
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
