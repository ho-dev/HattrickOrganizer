package core.file.xml;

import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.enums.MatchType;
import core.model.match.*;
import core.util.HOLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import core.util.XMLUtils;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author thomas.werth
 */
public class XMLMatchdetailsParser {
	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
    private XMLMatchdetailsParser() {
    }

    public static Matchdetails parseMatchdetailsFromString(String input, @Nullable MatchLineup matchLineup) {
        return createMatchdetails(XMLManager.parseString(input), matchLineup);
    }

    private static Matchdetails createMatchdetails(Document doc, MatchLineup matchLineup) {
        Matchdetails md = null;

        if (doc != null) {
        	try {
                md = new Matchdetails();

                readGeneral(doc, md);
                readArena(doc, md);
                readGuestTeam(doc, md);
                readHomeTeam(doc, md);
				readInjuries(doc, md);

				if (matchLineup != null) {
					// Match lineup needs to be available, if not -> ignore match highlights/report
					readHighlights(doc, md, matchLineup);
					parseMatchReport(md);

					var guest = matchLineup.getGuestTeam();
					guest.setMatchTeamAttitude(MatchTeamAttitude.fromInt(md.getGuestEinstellung()));
					guest.setMatchTacticType(MatchTacticType.fromInt(md.getGuestTacticType()));

					var home = matchLineup.getHomeTeam();
					home.setMatchTeamAttitude(MatchTeamAttitude.fromInt(md.getHomeEinstellung()));
					home.setMatchTacticType(MatchTacticType.fromInt(md.getHomeTacticType()));
				}

                md.setStatisics();

			} catch (Exception e) {
	            HOLogger.instance().log(XMLMatchdetailsParser.class, e);
	            return null;
			}
        }

        return md;
    }

	/**
	 * read the match injuries from XML
	 *
	 * @param doc 	XML document
	 * @param md	match details
	 *
	 */
	private static void readInjuries(Document doc, Matchdetails md) {
        final ArrayList<Matchdetails.Injury> mdInjuries = new ArrayList<>();
        try {
            var root = doc.getDocumentElement();
            if (XMLUtils.hasElement(root, "Injuries")) {
                var injuriesElement = XMLUtils.getElement(root, "Injuries");
                var injuryList = injuriesElement.getElementsByTagName("Injury");
                for (int n = 0; n < injuryList.getLength(); n++) {
                    var injuryELement = (Element) injuryList.item(n);
                    var injuryPlayerID = XMLUtils.getIntValue(injuryELement, "InjuryPlayerID");
                    var injuryTeamID = XMLUtils.getIntValue(injuryELement, "InjuryTeamID");
                    var injuryType = XMLUtils.getIntValue(injuryELement, "InjuryType");
                    var injuryMinute = XMLUtils.getIntValue(injuryELement, "InjuryMinute");
                    var matchPart = XMLUtils.getIntValue(injuryELement, "MatchPart");
                    var injury = new Matchdetails.Injury(injuryPlayerID, injuryTeamID, injuryType, injuryMinute, matchPart);
                    mdInjuries.add(injury);
                }
                md.setInjuries(mdInjuries);
            }
        } catch (Exception e) {
            HOLogger.instance().log(XMLMatchdetailsParser.class, e);
        }
    }

    /**
     * read the match highlights from XML
     *
     * @param doc 	XML document
     * @param md	match details
     *
     */
    private static void readHighlights(Document doc, Matchdetails md, MatchLineup lineup) {
        final ArrayList<MatchEvent> matchEvents = new ArrayList<>();
        try {
            //get Root element
            var root = doc.getDocumentElement();
            var matchElement = XMLUtils.getElement(root, "Match");
            var eventListElement = XMLUtils.getElement(matchElement, "EventList");
            var eventNodeList = eventListElement.getElementsByTagName("Event");

            //now go through the match events
            for (int n = 0; n < eventNodeList.getLength(); n++) {
                var eventNode = (Element) eventNodeList.item(n);

                //get values from xml
                int iMinute = XMLUtils.getIntValue(eventNode, "Minute");
                var iSubjectPlayerID = XMLUtils.getIntValue(eventNode, "SubjectPlayerID");
                var iSubjectTeamID = XMLUtils.getIntValue(eventNode, "SubjectTeamID");
                var iObjectPlayerID = XMLUtils.getIntValue(eventNode, "ObjectPlayerID");
                var iMatchPart = XMLUtils.getIntValue(eventNode, "MatchPart");
                var iEventVariation = XMLUtils.getIntValue(eventNode, "EventVariation");

                var eventtext = XMLUtils.getValue(eventNode, "EventText", "");
                eventtext = eventtext.replaceAll("&lt;", "<");
                eventtext = eventtext.replaceAll("&gt;", ">");
                eventtext = eventtext.replaceAll("/>", ">");
                eventtext = eventtext.replaceAll("&quot;", "\"");
                eventtext = eventtext.replaceAll("&amp;", "&");

                // Convert the ID to type and subtype.
                int iMatchEventID = XMLUtils.getIntValue(eventNode, "EventTypeID");

                //get players
                boolean subHome = true;
                boolean objHome = true;
                MatchLineupPosition subjectPlayer = null;
                MatchLineupPosition objectPlayer = null;
                if (iMinute > 0) {

                    subjectPlayer = lineup.getHomeTeam().getPlayerByID(iSubjectPlayerID, true);
                    objectPlayer = lineup.getHomeTeam().getPlayerByID(iObjectPlayerID, true);
                    if (subjectPlayer == null) {
                        subjectPlayer = lineup.getGuestTeam().getPlayerByID(iSubjectPlayerID, true);
                        subHome = false;
                    }
                    if (objectPlayer == null) {
                        objectPlayer = lineup.getGuestTeam().getPlayerByID(iObjectPlayerID, true);
                        objHome = false;
                    }
                }

                //modify eventtext
                if (subjectPlayer != null) {
                    String subplayerColor;

                    if (subHome) {
                        subplayerColor = "#000099";
                    } else {
                        subplayerColor = "#990000";
                    }

                    String objplayerColor;

                    if (objHome) {
                        objplayerColor = "#000099";
                    } else {
                        objplayerColor = "#990000";
                    }

                    boolean replaceend = false;

                    if (eventtext.contains(String.valueOf(iSubjectPlayerID))) {
                        eventtext = eventtext.replaceAll("(?i)<A HREF=\"/Club/Players/Player\\.aspx\\?playerId="
                                + iSubjectPlayerID + ".*?>",
                            "<FONT COLOR=" + subplayerColor + "#><B>");
                        replaceend = true;
                    }

                    if (eventtext.contains(String.valueOf(iObjectPlayerID))) {
                        eventtext = eventtext.replaceAll("(?i)<A HREF=\"/Club/Players/Player\\.aspx\\?playerId="
                                + iObjectPlayerID + ".*?>",
                            "<FONT COLOR=" + objplayerColor + "#><B>");
                        replaceend = true;
                    }

                    if (replaceend) {
                        eventtext = eventtext.replaceAll("(?i)</A>", "</B></FONT>");
                    }
                }

                //generate MatchEvent and add to list
                final MatchEvent myHighlight = new MatchEvent();
                myHighlight.setMatchEventIndex(n + 1);
                myHighlight.setMatchEventID(iMatchEventID);
                myHighlight.setMinute(iMinute);
                myHighlight.setPlayerId(iSubjectPlayerID);
                myHighlight.setPlayerName(subjectPlayer != null ? subjectPlayer.getSpielerName() : "");
                myHighlight.setSpielerHeim(subHome);
                myHighlight.setTeamID(iSubjectTeamID);
                myHighlight.setAssistingPlayerId(iObjectPlayerID);
                myHighlight.setAssistingPlayerName(objectPlayer != null ? objectPlayer.getSpielerName() : "");
                myHighlight.setGehilfeHeim(objHome);
                myHighlight.setEventText(eventtext);
                myHighlight.setMatchPartId(MatchEvent.MatchPartId.fromMatchPartId(iMatchPart));
                myHighlight.setEventVariation(iEventVariation);

                if (myHighlight.getMatchEventID() == MatchEvent.MatchEventID.UNKNOWN_MATCHEVENT) {
                    HOLogger.instance().warning(XMLMatchdetailsParser.class, "Unknown event id found in match " +
                        md.getHomeTeamName() + "-" + md.getGuestTeamName() +
                        " in minute " + myHighlight.getMinute() +
                        " text: " + myHighlight.getEventText());
                }

                // Treat injury
                if ((iMatchEventID == 90) || ((iMatchEventID == 94))) {
                    myHighlight.setM_eInjuryType(Matchdetails.eInjuryType.BRUISE);
                } else if ((iMatchEventID == 91) || (iMatchEventID == 92) || (iMatchEventID == 93) || (iMatchEventID == 96)) {
                    myHighlight.setM_eInjuryType(Matchdetails.eInjuryType.INJURY);
                } else if ((iMatchEventID >= 401) && (iMatchEventID <= 422)) {
                    myHighlight.setM_eInjuryType(getInjuryType(iSubjectPlayerID, md.getInjuries()));
                } else {
                    myHighlight.setM_eInjuryType(Matchdetails.eInjuryType.NA);
                }

                matchEvents.add(myHighlight);
            }
            md.setHighlights(matchEvents);
        } catch (Exception e) {
            HOLogger.instance().log(XMLMatchdetailsParser.class, e);
        }
    }

    /**
     * parse match report from previously parsed highlights
     *
     * @param md	match details
     */
    private static void parseMatchReport(Matchdetails md) {
        List<MatchEvent> highlights = md.getHighlights();
        final StringBuilder report = new StringBuilder();
		for (MatchEvent highlight : highlights) {
			report.append(highlight.getEventText()).append(" ");
		}
        md.setMatchreport(report.toString());
    }

    private static void readArena(Document doc, Matchdetails md) {
        var root = doc.getDocumentElement();
        try {
            //Match data
            var matchElement = XMLUtils.getElement(root, "Match");
            var arenaElement = XMLUtils.getElement(matchElement, "Arena");

            try {
                md.setArenaID(XMLUtils.getIntValue(arenaElement, "ArenaID"));
                md.setArenaName(XMLUtils.getValue(arenaElement, "ArenaName"));
            } catch (Exception e) {
                // This fails at tournament matches - ignore
            }

            var integerValue = XMLUtils.getIntegerValue(arenaElement, "WeatherID");
            if (integerValue != null) md.setWetterId(integerValue);
            integerValue = XMLUtils.getIntegerValue(arenaElement, "SoldTotal");
            if (integerValue != null) md.setZuschauer(integerValue);
            // Get spectator distribution, if available
            var soldTerraces = XMLUtils.getIntegerValue(arenaElement, "SoldTerraces");
            if (soldTerraces != null) {
                md.setSoldTerraces(soldTerraces);
                md.setSoldBasic(XMLUtils.getIntValue(arenaElement, "SoldBasic"));
                md.setSoldRoof(XMLUtils.getIntValue(arenaElement, "SoldRoof"));
                md.setSoldVIP(XMLUtils.getIntValue(arenaElement, "SoldVIP"));
            }
        } catch (Exception e) {
            HOLogger.instance().log(XMLMatchdetailsParser.class, e);
        }
    }

    private static void readGeneral(Document doc, Matchdetails md) {
        Element root = doc.getDocumentElement();

        try {
            md.setFetchDatumFromString(XMLUtils.getValue(root, "FetchedDate"));

            var matchElement = XMLUtils.getElement(root, "Match");
            var iMatchType = XMLUtils.getIntValue(matchElement, "MatchType");
            var matchType = Objects.requireNonNull(MatchType.getById(iMatchType));
            md.setMatchType(matchType);

            if (matchType == MatchType.CUP) {
                md.setCupLevel(CupLevel.fromInt(XMLUtils.getIntValue(matchElement, "CupLevel")));
                md.setCupLevelIndex(CupLevelIndex.fromInt(XMLUtils.getIntValue(matchElement, "CupLevelIndex")));
            }
            md.setMatchID(XMLUtils.getIntValue(matchElement, "MatchID"));
            md.setSpielDatumFromString(XMLUtils.getValue(matchElement, "MatchDate"));
        } catch (Exception e) {
            HOLogger.instance().log(XMLMatchdetailsParser.class, e);
        }
    }

    private static void readGuestTeam(Document doc, Matchdetails md) {
        try {
            var root = doc.getDocumentElement();
            var matchElement = core.util.XMLUtils.getElement(root, "Match");
            var awayTeamElement = XMLUtils.getElement(matchElement, "AwayTeam");
            md.setAwayFormation(XMLUtils.getValue(awayTeamElement, "Formation", ""));
            md.setGastId(XMLUtils.getIntValue(awayTeamElement, "AwayTeamID"));
            md.setGastName(XMLUtils.getValue(awayTeamElement, "AwayTeamName"));
            md.setGuestGoals(XMLUtils.getIntValue(awayTeamElement, "AwayGoals", -1));
            md.setGuestTacticType(XMLUtils.getIntValue(awayTeamElement, "TacticType", -1));
            md.setGuestTacticSkill(XMLUtils.getIntValue(awayTeamElement, "TacticSkill", -1));
            md.setGuestMidfield(XMLUtils.getIntValue(awayTeamElement, "RatingMidfield", -1));
            md.setGuestRightDef(XMLUtils.getIntValue(awayTeamElement, "RatingRightDef", -1));
            md.setGuestMidDef(XMLUtils.getIntValue(awayTeamElement, "RatingMidDef", -1));
            md.setGuestLeftDef(XMLUtils.getIntValue(awayTeamElement, "RatingLeftDef", -1));
            md.setGuestRightAtt(XMLUtils.getIntValue(awayTeamElement, "RatingRightAtt", -1));
            md.setGuestMidAtt(XMLUtils.getIntValue(awayTeamElement, "RatingMidAtt", -1));
            md.setGuestLeftAtt(XMLUtils.getIntValue(awayTeamElement, "RatingLeftAtt", -1));
            md.setGuestRatingIndirectSetPiecesAtt(XMLUtils.getIntValue(awayTeamElement, "RatingIndirectSetPiecesAtt", -1));
            md.setGuestRatingIndirectSetPiecesDef(XMLUtils.getIntValue(awayTeamElement, "RatingIndirectSetPiecesDef", -1));
            md.setGuestEinstellung(XMLUtils.getIntValue(awayTeamElement, "TeamAttitude", Matchdetails.EINSTELLUNG_UNBEKANNT));
        } catch (Exception e) {
            HOLogger.instance().log(XMLMatchdetailsParser.class, e);
        }
    }

    private static void readHomeTeam(Document doc, Matchdetails md) {
        try {
            var root = doc.getDocumentElement();
            var matchElement = core.util.XMLUtils.getElement(root, "Match");
            var homeTeamElement = XMLUtils.getElement(matchElement, "HomeTeam");
            md.setHomeFormation(XMLUtils.getValue(homeTeamElement, "Formation", ""));
            md.setHeimId(XMLUtils.getIntValue(homeTeamElement, "HomeTeamID"));
            md.setHeimName(XMLUtils.getValue(homeTeamElement, "HomeTeamName"));
            md.setHomeGoals(XMLUtils.getIntValue(homeTeamElement, "HomeGoals", -1));
            md.setHomeTacticType(XMLUtils.getIntValue(homeTeamElement, "TacticType", -1));
            md.setHomeTacticSkill(XMLUtils.getIntValue(homeTeamElement, "TacticSkill", -1));
            md.setHomeMidfield(XMLUtils.getIntValue(homeTeamElement, "RatingMidfield", -1));
            md.setHomeRightDef(XMLUtils.getIntValue(homeTeamElement, "RatingRightDef", -1));
            md.setHomeMidDef(XMLUtils.getIntValue(homeTeamElement, "RatingMidDef", -1));
            md.setHomeLeftDef(XMLUtils.getIntValue(homeTeamElement, "RatingLeftDef", -1));
            md.setHomeRightAtt(XMLUtils.getIntValue(homeTeamElement, "RatingRightAtt", -1));
            md.setHomeMidAtt(XMLUtils.getIntValue(homeTeamElement, "RatingMidAtt", -1));
            md.setHomeLeftAtt(XMLUtils.getIntValue(homeTeamElement, "RatingLeftAtt", -1));
            md.setHomeRatingIndirectSetPiecesAtt(XMLUtils.getIntValue(homeTeamElement, "RatingIndirectSetPiecesAtt", -1));
            md.setHomeRatingIndirectSetPiecesDef(XMLUtils.getIntValue(homeTeamElement, "RatingIndirectSetPiecesDef", -1));
            md.setHomeEinstellung(XMLUtils.getIntValue(homeTeamElement, "TeamAttitude", Matchdetails.EINSTELLUNG_UNBEKANNT));
        } catch (Exception e) {
            HOLogger.instance().log(XMLMatchdetailsParser.class, e);
        }
    }

	private static Matchdetails.eInjuryType getInjuryType(int iPlayerID, ArrayList<Matchdetails.Injury> injuries) {
        for (Matchdetails.Injury injury : injuries) {
            if (injury.getInjuryPlayerID() == iPlayerID) {
                return injury.getInjuryType();
            }
        }
        HOLogger.instance().log(XMLMatchdetailsParser.class, "the injured player was not listed !!! This is not normal ");
        return Matchdetails.eInjuryType.NA;
    }
}
