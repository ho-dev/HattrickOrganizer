/*
 * xmlMatchArchivParser.java
 *
 * Created on 28. Juli 2004, 15:32
 */
package core.file.xml;

import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.match.MatchKurzInfo;
import core.model.enums.MatchType;
import core.util.HOLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import core.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author TheTom
 */
public final class XMLMatchArchiveParser {

    private XMLMatchArchiveParser() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static List<MatchKurzInfo> parseMatchesFromString(String input) {
        return createMatches(XMLManager.parseString(input));
    }

    private static List<MatchKurzInfo> createMatches(Document doc) {
        List<MatchKurzInfo> matches = new ArrayList<>();

        if (doc == null) {
            return matches;
        }

        try {
            var root = doc.getDocumentElement();
            var isYouth = XMLUtils.getBooleanValue(root, "IsYouth");
            NodeList nodeList = root.getElementsByTagName("Match");
            for (int i = 0; i < nodeList.getLength(); i++) {
                var match = new MatchKurzInfo();
                var matchElement = (Element) nodeList.item(i);
                match.setMatchSchedule(XMLUtils.getHODateTimeValue(matchElement, "MatchDate"));
                match.setMatchID(XMLUtils.getIntValue(matchElement, "MatchID"));
                var matchType = XMLUtils.getIntValue(matchElement, "MatchType", MatchType.YOUTHLEAGUE.getId());
                if (matchType == MatchType.YOUTHLEAGUE.getId() && !isYouth) {
                    throw new NoSuchElementException("Element not found: MatchType");
                }
                match.setMatchType(MatchType.getById(matchType));
                match.setMatchContextId(XMLUtils.getIntValue(matchElement, "MatchContextId"));

                if (matchType == MatchType.CUP.getId()) {
                    match.setCupLevel(CupLevel.fromInt(XMLUtils.getIntValue(matchElement, "CupLevel")));
                    match.setCupLevelIndex(CupLevelIndex.fromInt(XMLUtils.getIntValue(matchElement, "CupLevelIndex")));
                }

                var homeTeamElement = XMLUtils.getElement(matchElement, "HomeTeam");
                match.setHomeTeamID(XMLUtils.getIntValue(homeTeamElement, "HomeTeamID"));
                match.setHomeTeamName(XMLUtils.getStringValue(homeTeamElement, "HomeTeamName"));

                var awayTeamElement = XMLUtils.getElement(matchElement, "AwayTeam");
                match.setGuestTeamID(XMLUtils.getIntValue(awayTeamElement, "AwayTeamID"));
                match.setGuestTeamName(XMLUtils.getStringValue(awayTeamElement, "AwayTeamName"));
                match.setHomeTeamGoals(XMLUtils.getIntValue(matchElement, "HomeGoals"));
                match.setGuestTeamGoals(XMLUtils.getIntValue(matchElement, "AwayGoals"));
                match.setOrdersGiven(true);
                match.setMatchStatus(MatchKurzInfo.FINISHED);
                matches.add(match);
            }
        } catch (Exception e) {
            matches.clear();
            HOLogger.instance().log(XMLMatchArchiveParser.class, e);
        }
        return matches;
    }
}
