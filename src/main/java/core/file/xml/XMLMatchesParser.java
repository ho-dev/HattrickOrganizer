package core.file.xml;

import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.match.MatchKurzInfo;
import core.model.enums.MatchType;
import core.util.HODateTime;
import core.util.HOLogger;

import java.util.ArrayList;
import java.util.List;

import core.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

	private static List<MatchKurzInfo> createMatches(Document doc) {
        List<MatchKurzInfo> matches = new ArrayList<>();
        if (doc != null) {
            Element root = doc.getDocumentElement();
            try {
                NodeList list = root.getElementsByTagName("Match");
                for (int i = 0; i < list.getLength(); i++) {
                    var match = new MatchKurzInfo();
                    var matchElement = (Element) list.item(i);
                    match.setMatchSchedule(XMLUtils.getHODateTimeValue(matchElement, "MatchDate"));
                    match.setMatchID(XMLUtils.getIntValue(matchElement, "MatchID"));
                    match.setMatchType(MatchType.getById(XMLUtils.getIntValue(matchElement, "MatchType")));

                    if (match.getMatchType() == MatchType.CUP) {
                        match.setCupLevel(CupLevel.fromInt(XMLUtils.getIntValue(matchElement, "CupLevel")));
                        match.setCupLevelIndex(CupLevelIndex.fromInt(XMLUtils.getIntValue(matchElement, "CupLevelIndex")));
                    }

                    match.setMatchContextId(XMLUtils.getIntValue(matchElement, "MatchContextId"));

                    var homeTeamElement = XMLUtils.getElement(matchElement, "HomeTeam");
                    match.setHomeTeamID(XMLUtils.getIntValue(homeTeamElement, "HomeTeamID"));
                    match.setHomeTeamName(XMLUtils.getStringValue(homeTeamElement, "HomeTeamName"));

                    var awayTeamElement = XMLUtils.getElement(matchElement, "AwayTeam");
                    match.setGuestTeamID(XMLUtils.getIntValue(awayTeamElement, "AwayTeamID"));
                    match.setGuestTeamName(XMLUtils.getStringValue(awayTeamElement, "AwayTeamName"));

                    match.setMatchStatus(getStatus(XMLUtils.getStringValue(matchElement, "Status")));
                    if (match.getMatchStatus() == MatchKurzInfo.FINISHED) {
                        match.setHomeTeamGoals(XMLUtils.getIntValue(matchElement, "HomeGoals"));
                        match.setGuestTeamGoals(XMLUtils.getIntValue(matchElement, "AwayGoals"));
                    } else if (match.getMatchStatus() == MatchKurzInfo.UPCOMING) {
                        try {
                            match.setOrdersGiven(XMLUtils.getBooleanValue(matchElement, "OrdersGiven"));
                        } catch (Exception e) {
                            // We will end up here if the match is not the user's
                            match.setOrdersGiven(false);
                        }
                    }

                    matches.add(match);
                }
            } catch (Exception e) {
                HOLogger.instance().log(XMLMatchesParser.class, e);
                matches.clear();
            }
        }
        return matches;
    }
}
