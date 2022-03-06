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

public class XMLMatchesParser {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLMatchesParser() {
	}

	public static List<MatchKurzInfo> parseMatchesFromString(String input) {
		return createMatches(XMLManager.parseString(input));
	}

	/**
	 * Wertet den StatusString aus und liefert einen INT
	 */
	private static int getStatus(String status) {
		if (status.equalsIgnoreCase("FINISHED")) {
			return MatchKurzInfo.FINISHED;
		} else if (status.equalsIgnoreCase("ONGOING")) {
			return MatchKurzInfo.ONGOING;
		} else if (status.equalsIgnoreCase("UPCOMING")) {
			return MatchKurzInfo.UPCOMING;
		}

		return -1;
	}

	/**
	 * erstellt das MAtchlineup Objekt
	 */
	private static List<MatchKurzInfo> createMatches(Document doc) {
		MatchKurzInfo match;
		List<MatchKurzInfo> liste = new ArrayList<>();
		int iMatchType;
		int iCupLevel;
		int iCupLevelIndex;

		if (doc != null) {
			Element root = doc.getDocumentElement();
			try {
				NodeList list = root.getElementsByTagName("Match");

				Element ele;
				Element tmp;
				for (int i = 0; (list != null) && (i < list.getLength()); i++) {
					match = new MatchKurzInfo();
					ele = (Element) list.item(i);

					tmp = (Element) ele.getElementsByTagName("MatchDate").item(0);
					match.setMatchSchedule(HODateTime.fromHT(tmp.getFirstChild().getNodeValue()));
					tmp = (Element) ele.getElementsByTagName("MatchID").item(0);
					match.setMatchID(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
					tmp = (Element) ele.getElementsByTagName("MatchType").item(0);
					iMatchType = Integer.parseInt(tmp.getFirstChild().getNodeValue());
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
							"HomeTeamName").item(0).getFirstChild()
							.getNodeValue());
					tmp = (Element) ele.getElementsByTagName("AwayTeam")
							.item(0);
					match.setGuestTeamID(Integer.parseInt(tmp
							.getElementsByTagName("AwayTeamID").item(0)
							.getFirstChild().getNodeValue()));
					match.setGuestTeamName(tmp.getElementsByTagName(
							"AwayTeamName").item(0).getFirstChild()
							.getNodeValue());
					tmp = (Element) ele.getElementsByTagName("Status").item(0);
					match.setMatchStatus(getStatus(tmp.getFirstChild()
							.getNodeValue()));

					if (match.getMatchStatus() == MatchKurzInfo.FINISHED) {
						tmp = (Element) ele.getElementsByTagName("HomeGoals")
								.item(0);
						match.setHomeTeamGoals(Integer.parseInt(tmp.getFirstChild()
								.getNodeValue()));
						tmp = (Element) ele.getElementsByTagName("AwayGoals")
								.item(0);
						match.setGuestTeamGoals(Integer.parseInt(tmp.getFirstChild()
								.getNodeValue()));
					} else if (match.getMatchStatus() == MatchKurzInfo.UPCOMING) {
						try {
							tmp = (Element) ele.getElementsByTagName(
									"OrdersGiven").item(0);
							match.setOrdersGiven(tmp.getFirstChild()
									.getNodeValue().equalsIgnoreCase("TRUE"));
						} catch (Exception e) {
							// We will end up here if the match is not the
							// user's
							match.setOrdersGiven(false);
						}
					}

					liste.add(match);
				}
			} catch (Exception e) {
				HOLogger.instance().log(XMLMatchesParser.class, e);
				liste.clear();
			}
		}
		return liste;
	}
}
