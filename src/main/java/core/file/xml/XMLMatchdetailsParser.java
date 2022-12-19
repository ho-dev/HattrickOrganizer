package core.file.xml;

import core.db.DBManager;
import core.model.Tournament.TournamentDetails;
import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.enums.MatchType;
import core.model.match.*;
import core.util.HOLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static core.net.OnlineWorker.getTournamentDetails;


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

		Element root, ele;
		NodeList injuryList ;
		int InjuryPlayerID, InjuryTeamID, InjuryType, InjuryMinute, MatchPart;
		Matchdetails.Injury injury;

		try {
			//get Injuries element
			root = doc.getDocumentElement();
			ele = (Element) root.getElementsByTagName("Injuries").item(0);
			if ( ele != null )
			{
				injuryList = ele.getElementsByTagName("Injury");
				//now go through the injuries
				for (int n=0; n < injuryList.getLength(); n++) {
					root = (Element) injuryList.item(n);
					InjuryPlayerID = Integer.parseInt(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("InjuryPlayerID").item(0)));
					InjuryTeamID = Integer.parseInt(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("InjuryTeamID").item(0)));
					InjuryType = Integer.parseInt(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("InjuryType").item(0)));
					InjuryMinute = Integer.parseInt(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("InjuryMinute").item(0)));
					MatchPart = Integer.parseInt(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("MatchPart").item(0)));
					injury = new Matchdetails.Injury(InjuryPlayerID, InjuryTeamID, InjuryType, InjuryMinute, MatchPart);
					mdInjuries.add(injury);
				}

				md.setM_Injuries(mdInjuries);
			}
		}

		catch (Exception e) {
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
        //final Vector<Integer> broken = new Vector<>(); // TODO: I guess this one can be deleted if things are done properly (akasolace)
        Element root, ele;
        NodeList eventList;
		int iMinute, iSubjectPlayerID, iSubjectTeamID, iObjectPlayerID, iMatchEventID, iMatchPart, iEventVariation;
		String eventtext;

		try {
            //get Root element
            root = doc.getDocumentElement();
            root = (Element) root.getElementsByTagName("Match").item(0);
            //get both teams
            ele = (Element) root.getElementsByTagName("HomeTeam").item(0);
            final String homeTeamID = XMLManager.getFirstChildNodeValue((Element) ele.getElementsByTagName("HomeTeamID").item(0));
			ele = (Element) root.getElementsByTagName("EventList").item(0);

			eventList = ele.getElementsByTagName("Event");

			//now go through the match events
            for (int n=0; n < eventList.getLength(); n++) {
            	root = (Element) eventList.item(n);

            	//get values from xml
            	iMinute = Integer.parseInt(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("Minute").item(0)));
            	iSubjectPlayerID = Integer.parseInt(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("SubjectPlayerID").item(0)));
            	iSubjectTeamID = Integer.parseInt(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("SubjectTeamID").item(0)));
            	iObjectPlayerID = Integer.parseInt(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("ObjectPlayerID").item(0)));
				iMatchPart = Integer.parseInt(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("MatchPart").item(0)));
				iEventVariation = Integer.parseInt(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("EventVariation").item(0)));

            	eventtext = XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("EventText").item(0));
            	eventtext = eventtext.replaceAll("&lt;", "<");
            	eventtext = eventtext.replaceAll("&gt;", ">");
            	eventtext = eventtext.replaceAll("/>", ">");
            	eventtext = eventtext.replaceAll("&quot;", "\"");
            	eventtext = eventtext.replaceAll("&amp;", "&");

            	// Convert the ID to type and subtype.
            	iMatchEventID = Integer.parseInt(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("EventTypeID").item(0)));

            	//get players
            	boolean subHome = true;
            	boolean objHome = true;
            	MatchLineupPosition subjectPlayer=null;
				MatchLineupPosition objectPlayer=null;
            	if (iMinute > 0) {

					subjectPlayer = lineup.getHomeTeam().getPlayerByID(iSubjectPlayerID, true);
					objectPlayer = lineup.getHomeTeam().getPlayerByID(iObjectPlayerID, true);
					if ( subjectPlayer == null){
						subjectPlayer = lineup.getGuestTeam().getPlayerByID(iSubjectPlayerID, true);
						subHome=false;
					}
					if ( objectPlayer == null){
						objectPlayer = lineup.getGuestTeam().getPlayerByID(iObjectPlayerID, true);
						objHome=false;
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

            	//generate MatchHighlight and add to list
            	final MatchEvent myHighlight = new MatchEvent();
            	myHighlight.setMatchEventIndex(n+1);
            	myHighlight.setMatchEventID(iMatchEventID);
            	myHighlight.setMinute(iMinute);
            	myHighlight.setPlayerId(iSubjectPlayerID);
            	myHighlight.setPlayerName(subjectPlayer!=null?subjectPlayer.getSpielerName():"");
            	myHighlight.setSpielerHeim(subHome);
            	myHighlight.setTeamID(iSubjectTeamID);
            	myHighlight.setAssistingPlayerId(iObjectPlayerID);
            	myHighlight.setAssistingPlayerName(objectPlayer!=null?objectPlayer.getSpielerName():"");
            	myHighlight.setGehilfeHeim(objHome);
            	myHighlight.setEventText(eventtext);
            	myHighlight.setMatchPartId(MatchEvent.MatchPartId.fromMatchPartId(iMatchPart));
            	myHighlight.setEventVariation(iEventVariation);

				if ( myHighlight.getMatchEventID() == MatchEvent.MatchEventID.UNKNOWN_MATCHEVENT ){
					HOLogger.instance().warning(XMLMatchdetailsParser.class, "Unknown event id found in match " +
							md.getHomeTeamName() + "-" + md.getGuestTeamName() +
							" in minute " + myHighlight.getMinute() +
							" text: "  + myHighlight.getEventText());
				}

            	// Treat injury
				if ((iMatchEventID==90) || ((iMatchEventID==94)))
					{myHighlight.setM_eInjuryType(Matchdetails.eInjuryType.BRUISE);}
				else if ((iMatchEventID==91) || (iMatchEventID==92) || (iMatchEventID==93) || (iMatchEventID==96))
					{myHighlight.setM_eInjuryType(Matchdetails.eInjuryType.INJURY);}
				else if ((iMatchEventID>=401) && (iMatchEventID<=422))
				{
					myHighlight.setM_eInjuryType(getInjuryType(iSubjectPlayerID, md.getM_Injuries()));
				}
				else
				{
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
        Element ele;
        var root = doc.getDocumentElement();

        try {
            //Daten füllen            
            //MatchData
            root = (Element) root.getElementsByTagName("Match").item(0);
            root = (Element) root.getElementsByTagName("Arena").item(0);
            
            
            try {
            	ele = (Element) root.getElementsByTagName("ArenaID").item(0);
            	md.setArenaID(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            	ele = (Element) root.getElementsByTagName("ArenaName").item(0);
            	if (ele.getFirstChild() != null) {
					md.setArenaName(ele.getFirstChild().getNodeValue());
				}
            } catch (Exception e){
            	// This fails at tournament matches - ignore
            }
            
            ele = (Element) root.getElementsByTagName("WeatherID").item(0);
            if ( ele != null ) md.setWetterId(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("SoldTotal").item(0);
			if ( ele != null ) md.setZuschauer(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            // Get spectator distribution, if available
            if (root.getElementsByTagName("SoldTerraces").getLength() > 0) {
            	ele = (Element) root.getElementsByTagName("SoldTerraces").item(0);
            	md.setSoldTerraces(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            	ele = (Element) root.getElementsByTagName("SoldBasic").item(0);
            	md.setSoldBasic(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            	ele = (Element) root.getElementsByTagName("SoldRoof").item(0);
            	md.setSoldRoof(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            	ele = (Element) root.getElementsByTagName("SoldVIP").item(0);
            	md.setSoldVIP(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            }
        } catch (Exception e) {
            HOLogger.instance().log(XMLMatchdetailsParser.class, e);
        }
    }

    private static void readGeneral(Document doc, Matchdetails md) {
        Element ele ;
        Element root ;
        int iMatchType, iCupLevel, iCupLevelIndex;

        root = doc.getDocumentElement();

        try {
            //Daten füllen
            ele = (Element) root.getElementsByTagName("FetchedDate").item(0);
            md.setFetchDatumFromString(ele.getFirstChild().getNodeValue());

            //MatchData
            root = (Element) root.getElementsByTagName("Match").item(0);
			ele = (Element) root.getElementsByTagName("MatchType").item(0);
			iMatchType = Integer.parseInt(ele.getFirstChild().getNodeValue());

			var matchType = Objects.requireNonNull(MatchType.getById(iMatchType));
			md.setMatchType(matchType);

			if (iMatchType == 3) {
				ele = (Element) root.getElementsByTagName("CupLevel").item(0);
				iCupLevel = Integer.parseInt(ele.getFirstChild().getNodeValue());
				md.setCupLevel(CupLevel.fromInt(iCupLevel));

				ele = (Element) root.getElementsByTagName("CupLevelIndex").item(0);
				iCupLevelIndex = Integer.parseInt(ele.getFirstChild().getNodeValue());
				md.setCupLevelIndex(CupLevelIndex.fromInt(iCupLevelIndex));
			}
			else if (iMatchType == 50) {
				ele = (Element) root.getElementsByTagName("MatchContextId").item(0);
				int tournamentId = Integer.parseInt(ele.getFirstChild().getNodeValue());
				md.setMatchContextId(tournamentId);

				TournamentDetails oTournamentDetails = DBManager.instance().getTournamentDetailsFromDB(tournamentId);
				if (oTournamentDetails == null)
				{
					oTournamentDetails = getTournamentDetails(tournamentId); // download info about tournament from HT
					DBManager.instance().storeTournamentDetailsIntoDB(oTournamentDetails); // store tournament details into DB
				}
				md.setTournamentTypeID(oTournamentDetails.getTournamentType());
			}

            ele = (Element) root.getElementsByTagName("MatchID").item(0);
            md.setMatchID(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("MatchDate").item(0);
            md.setSpielDatumFromString(ele.getFirstChild().getNodeValue());
        } catch (Exception e) {
            HOLogger.instance().log(XMLMatchdetailsParser.class,e);
		}
    }

    private static void readGuestTeam(Document doc, Matchdetails md) {
        Element ele;
        Element root;

        try {
            //Daten füllen                        
            root = doc.getDocumentElement();
            root = (Element) root.getElementsByTagName("Match").item(0);
            root = (Element) root.getElementsByTagName("AwayTeam").item(0);

            NodeList formation = root.getElementsByTagName("Formation");
            if (formation.getLength() > 0) {
            	md.setAwayFormation(formation.item(0).getTextContent());
			}

            ele = (Element) root.getElementsByTagName("AwayTeamID").item(0);
			if ( ele != null ) md.setGastId(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("AwayTeamName").item(0);
			if ( ele != null ) md.setGastName(ele.getFirstChild().getNodeValue());
            ele = (Element) root.getElementsByTagName("AwayGoals").item(0);
			if ( ele != null ) md.setGuestGoals(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("TacticType").item(0);
			if ( ele != null ) md.setGuestTacticType(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("TacticSkill").item(0);
			if ( ele != null ) md.setGuestTacticSkill(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingMidfield").item(0);
			if ( ele != null ) md.setGuestMidfield(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingRightDef").item(0);
			if ( ele != null ) md.setGuestRightDef(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingMidDef").item(0);
			if ( ele != null ) md.setGuestMidDef(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingLeftDef").item(0);
			if ( ele != null ) md.setGuestLeftDef(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingRightAtt").item(0);
			if ( ele != null ) md.setGuestRightAtt(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingMidAtt").item(0);
			if ( ele != null ) md.setGuestMidAtt(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingLeftAtt").item(0);
			if ( ele != null ) md.setGuestLeftAtt(Integer.parseInt(ele.getFirstChild().getNodeValue()));

			ele = (Element) root.getElementsByTagName("RatingIndirectSetPiecesAtt").item(0);
			if ( ele != null ) md.setRatingIndirectSetPiecesAtt(Integer.parseInt(ele.getFirstChild().getNodeValue()));
			ele = (Element) root.getElementsByTagName("RatingIndirectSetPiecesDef").item(0);
			if ( ele != null ) md.setRatingIndirectSetPiecesDef(Integer.parseInt(ele.getFirstChild().getNodeValue()));

			NodeList teamAttitude = root.getElementsByTagName("TeamAttitude");
			if (teamAttitude.getLength() > 0) {
				ele = (Element) teamAttitude.item(0);
				md.setGuestEinstellung(Integer.parseInt(ele.getFirstChild().getNodeValue()));
			} else {
				md.setGuestEinstellung(Matchdetails.EINSTELLUNG_UNBEKANNT);
			}
        } catch (Exception e) {
            HOLogger.instance().log(XMLMatchdetailsParser.class, e);
        }
    }

    private static void readHomeTeam(Document doc, Matchdetails md) {
        Element ele;
        Element root;

        try {
            //Daten füllen                        
            root = doc.getDocumentElement();
            root = (Element) root.getElementsByTagName("Match").item(0);
            root = (Element) root.getElementsByTagName("HomeTeam").item(0);

            //Data
			NodeList formation = root.getElementsByTagName("Formation");
			if (formation.getLength() > 0) {
				md.setHomeFormation(formation.item(0).getTextContent());
			}

            ele = (Element) root.getElementsByTagName("HomeTeamID").item(0);
			if ( ele != null ) md.setHeimId(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("HomeTeamName").item(0);
			if ( ele != null ) md.setHeimName(ele.getFirstChild().getNodeValue());
            ele = (Element) root.getElementsByTagName("HomeGoals").item(0);
			if ( ele != null ) md.setHomeGoals(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("TacticType").item(0);
			if ( ele != null ) md.setHomeTacticType(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("TacticSkill").item(0);
			if ( ele != null ) md.setHomeTacticSkill(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingMidfield").item(0);
			if ( ele != null ) md.setHomeMidfield(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingRightDef").item(0);
			if ( ele != null ) md.setHomeRightDef(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingMidDef").item(0);
			if ( ele != null ) md.setHomeMidDef(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingLeftDef").item(0);
			if ( ele != null ) md.setHomeLeftDef(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingRightAtt").item(0);
			if ( ele != null ) md.setHomeRightAtt(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingMidAtt").item(0);
			if ( ele != null ) md.setHomeMidAtt(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingLeftAtt").item(0);
			if ( ele != null ) md.setHomeLeftAtt(Integer.parseInt(ele.getFirstChild().getNodeValue()));

			ele = (Element) root.getElementsByTagName("RatingIndirectSetPiecesAtt").item(0);
			if ( ele != null ) md.setRatingIndirectSetPiecesAtt(Integer.parseInt(ele.getFirstChild().getNodeValue()));
			ele = (Element) root.getElementsByTagName("RatingIndirectSetPiecesDef").item(0);
			if ( ele != null ) md.setRatingIndirectSetPiecesDef(Integer.parseInt(ele.getFirstChild().getNodeValue()));

			NodeList teamAttitude = root.getElementsByTagName("TeamAttitude");
			if (teamAttitude.getLength() > 0) {
				ele = (Element) teamAttitude.item(0);
				md.setHomeEinstellung(Integer.parseInt(ele.getFirstChild().getNodeValue()));
			} else {
				md.setHomeEinstellung(Matchdetails.EINSTELLUNG_UNBEKANNT);
			}

        } catch (Exception e) {
            HOLogger.instance().log(XMLMatchdetailsParser.class, e);
        }
    }

	private static Matchdetails.eInjuryType getInjuryType(int iPlayerID, ArrayList<Matchdetails.Injury> injuries)
	{
		for (Matchdetails.Injury injury : injuries )
		{
			if ( (injury.getInjuryPlayerID() == iPlayerID) && (injury.getInjuryPlayerID() == iPlayerID))
			{
				return injury.getInjuryType();
			}
		}

		HOLogger.instance().log(XMLMatchdetailsParser.class, "the injured player was not listed !!! This is not normal ");
		return Matchdetails.eInjuryType.NA;

	}

}
