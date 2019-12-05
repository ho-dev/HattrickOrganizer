package core.file.xml;

import core.db.DBManager;
import core.model.Tournament.TournamentDetails;
import core.model.match.*;
import core.util.HOLogger;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

    public static Matchdetails parseMachtdetailsFromString(String input, MatchLineup matchLineup) {
        return createMatchdetails(XMLManager.parseString(input), matchLineup);
    }

    private static Matchdetails createMatchdetails(Document doc, MatchLineup matchLineup) {
        Matchdetails md = null;

        if (doc != null) {
        	try {
                md = new Matchdetails();

                readGeneral(doc, md);
                // Match lineup needs to be available, if not -> ignore match highlights/report
                if (matchLineup == null) {
                	// reduced log level as this happens each and every match download.
                	HOLogger.instance().debug(XMLMatchdetailsParser.class, 
                			"XMLMatchdetailsParser["+md.getMatchID()+"]: Cannot parse matchreport from matchdetails, lineup MUST be available!");
                } else {
                    readHighlights(doc, md, matchLineup);
                    parseMatchReport(md);
                }
                readArena(doc, md);
                readGuestTeam(doc, md);
                readHomeTeam(doc, md);
                md.setStatisics();
			} catch (Exception e) {
	            HOLogger.instance().log(XMLMatchdetailsParser.class, e);
	            return null;
			}
        }

        return md;
    }

    /**
     * read the match highlights from XML
     *
     * @param doc 	XML document
     * @param md	match details
     *
     */
    private static void readHighlights(Document doc, Matchdetails md, MatchLineup lineup) {
        final Vector<MatchHighlight> myHighlights = new Vector<MatchHighlight>();
        final Vector<Integer> broken = new Vector<Integer>();
        Element ele = null;
        Element eventList = null;
        Element root = null;

        try {
            //get Root element
            root = doc.getDocumentElement();
            root = (Element) root.getElementsByTagName("Match").item(0);
            //get both teams
            ele = (Element) root.getElementsByTagName("HomeTeam").item(0);
            final String homeTeamID = XMLManager.getFirstChildNodeValue((Element) ele.getElementsByTagName("HomeTeamID")
                                                                                                        .item(0));
            
            final Vector<Vector<String>> homeTeamPlayers = parseLineup (lineup.getHeim().getAufstellung());
            final Vector<Vector<String>> awayTeamPlayers = parseLineup (lineup.getGast().getAufstellung());

            //now go through the eventlist and add everything together
            eventList = (Element) root.getElementsByTagName("EventList").item(0);

            int homeGoals = 0;
            int awayGoals = 0;
            int n = 0;

            while (n < eventList.getElementsByTagName("Event").getLength()) {
            	root = (Element) eventList.getElementsByTagName("Event").item(n);

            	//get values from xml
            	final int minute = (Integer.valueOf(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("Minute")
            					.item(0))))
            					.intValue();
            	final int subjectplayerid = (Integer.valueOf(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("SubjectPlayerID")
            					.item(0))))
            					.intValue();
            	final int subjectteamid = (Integer.valueOf(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("SubjectTeamID")
            					.item(0))))
            					.intValue();
            	final int objectplayerid = (Integer.valueOf(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("ObjectPlayerID")
            					.item(0))))
            					.intValue();

            	String eventtext = XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("EventText")
            			.item(0));
            	eventtext = eventtext.replaceAll("&lt;", "<");
            	eventtext = eventtext.replaceAll("&gt;", ">");
            	eventtext = eventtext.replaceAll("/>", ">");
            	eventtext = eventtext.replaceAll("&quot;", "\"");
            	eventtext = eventtext.replaceAll("&amp;", "&");

            	// Convert the ID to type and subtype.
            	int eventKey = (Integer.valueOf(XMLManager.getFirstChildNodeValue((Element) root.getElementsByTagName("EventTypeID")
            					.item(0))))
								.intValue();
            	final int highlighttyp = (int) Math.floor(eventKey/100);
            	final int highlightsubtyp = eventKey - highlighttyp*100;

            	//determine new score
            	if (highlighttyp == IMatchHighlight.HIGHLIGHT_ERFOLGREICH) {
            		if (String.valueOf(subjectteamid).equals(homeTeamID)) {
            			homeGoals++;
            		} else {
            			awayGoals++;
            		}
            	}

            	//get names for players
            	String subjectplayername = "";
            	String objectplayername = "";
            	boolean subHome = true;
            	boolean objHome = true;

            	if (minute > 0) {
            		int i = 0;

            		while (i < homeTeamPlayers.size()) {
            			if ((!subjectplayername.equals(""))
            					&& (!objectplayername.equals(""))) {
            				break;
            			}

            			final Vector<String> tmpPlayer = homeTeamPlayers.get(i);

            			if (tmpPlayer.get(0).toString().equals(String.valueOf(subjectplayerid))) {
            				subjectplayername = tmpPlayer.get(1).toString();
            			}

            			if (tmpPlayer.get(0).toString().equals(String.valueOf(objectplayerid))) {
            				objectplayername = tmpPlayer.get(1).toString();
            			}

            			i++;
            		}

            		i = 0;

            		while (i < awayTeamPlayers.size()) {
            			if ((!subjectplayername.equals(""))
            					&& (!objectplayername.equals(""))) {
            				break;
            			}

            			final Vector<String> tmpPlayer = awayTeamPlayers.get(i);

            			if (tmpPlayer.get(0).toString().equals(String.valueOf(subjectplayerid))) {
            				subjectplayername = tmpPlayer.get(1).toString();
            				subHome = false;
            			}

            			if (tmpPlayer.get(0).toString().equals(String.valueOf(objectplayerid))) {
            				objectplayername = tmpPlayer.get(1).toString();
            				objHome = false;
            			}

            			i++;
            		}
            	}

            	//add single player
            	if (minute > 0) {
            		switch ((highlighttyp * 100) + highlightsubtyp) {
            		case 40:
            		case 45:
            		case 47:
            		case 60:
            		case 61:
            		case 62:
            		case 63:
            		case 64:
            		case 65:
            		case 68:
            		case 70:
            		case 71:
            		case 72:
            		case 75:
            		case 331:
            		case 332:
            		case 333:
            		case 334:
            		case 599:
            			break;

            		default:

            			if (subjectplayername.equals("") && (subjectplayerid != 0)) {
            				if (eventtext.indexOf(String.valueOf(subjectplayerid)) >= 0) {
            					String plname = eventtext.substring(eventtext.indexOf(String
            							.valueOf(subjectplayerid)));
            					plname = plname.substring(plname.indexOf(">") + 1);
            					plname = plname.substring(0, plname.indexOf("<"));
            					subjectplayername = plname;

            					final Vector<String> tmpplay = new Vector<String>();
            					tmpplay.add(String.valueOf(subjectplayerid));
            					tmpplay.add(plname);

            					if (homeTeamID.equals(String.valueOf(subjectteamid))) {
            						homeTeamPlayers.add(tmpplay);
            					} else {
            						awayTeamPlayers.add(tmpplay);
            						subHome = false;
            					}
            				} else {
            					subjectplayername = String.valueOf(subjectplayerid);
            					broken.add(new Integer(myHighlights.size()));
            				}
            			}

	            		if (objectplayername.equals("") && (objectplayerid != 0)) {
	            			if (eventtext.indexOf(String.valueOf(objectplayerid)) >= 0) {
	            				String plname = eventtext.substring(eventtext.indexOf(String
	            						.valueOf(objectplayerid)));
	            				plname = plname.substring(plname.indexOf(">") + 1);
	            				plname = plname.substring(0, plname.indexOf("<"));
	            				objectplayername = plname;
	
	            				final Vector<String> tmpplay = new Vector<String>();
	            				tmpplay.add(String.valueOf(objectplayerid));
	            				tmpplay.add(plname);
	
	            				//there is no easy solution to find out for which team this
	            				//players is playing. it's more possible that he's playing
	            				//in home team, so we go like this
	            				homeTeamPlayers.add(tmpplay);
	            			} else {
	            				objectplayername = String.valueOf(objectplayerid);
	            				broken.add(Integer.valueOf(myHighlights.size()));
	            			}
	            		}
            		}
            	}

            	//modify eventtext
            	if (!subjectplayername.equals("")) {
            		String subplayerColor = "#000000";

            		if (subHome) {
            			subplayerColor = "#000099";
            		} else {
            			subplayerColor = "#990000";
            		}

            		String objplayerColor = "#000000";

            		if (objHome) {
            			objplayerColor = "#000099";
            		} else {
            			objplayerColor = "#990000";
            		}

            		boolean replaceend = false;

            		if (eventtext.indexOf(String.valueOf(subjectplayerid)) >= 0) {
            			eventtext = eventtext.replaceAll("(?i)<A HREF=\"/Club/Players/Player\\.aspx\\?playerId="
            					+ subjectplayerid + ".*?>",
            					"<FONT COLOR=" + subplayerColor + "#><B>");
            			replaceend = true;
            		}

            		if (eventtext.indexOf(String.valueOf(objectplayerid)) >= 0) {
            			eventtext = eventtext.replaceAll("(?i)<A HREF=\"/Club/Players/Player\\.aspx\\?playerId="
            					+ objectplayerid + ".*?>",
            					"<FONT COLOR=" + objplayerColor + "#><B>");
            			replaceend = true;
            		}

            		if (replaceend) {
            			eventtext = eventtext.replaceAll("(?i)</A>", "</B></FONT>");
            		}
            	}

            	//generate MatchHighlight and add to list
            	final MatchHighlight myHighlight = new MatchHighlight();
            	myHighlight.setHighlightTyp(highlighttyp);
            	myHighlight.setHighlightSubTyp(highlightsubtyp);
            	myHighlight.setMinute(minute);
            	myHighlight.setHeimTore(homeGoals);
            	myHighlight.setGastTore(awayGoals);
            	myHighlight.setSpielerID(subjectplayerid);
            	myHighlight.setSpielerName(subjectplayername);
            	myHighlight.setSpielerHeim(subHome);
            	myHighlight.setTeamID(subjectteamid);
            	myHighlight.setGehilfeID(objectplayerid);
            	myHighlight.setGehilfeName(objectplayername);
            	myHighlight.setGehilfeHeim(objHome);
            	myHighlight.setEventText(eventtext);
            	myHighlights.add(myHighlight);

            	//break if end of match (due to some corrupt xmls)
            	if ((highlighttyp == IMatchHighlight.HIGHLIGHT_KARTEN)
            			&& (highlightsubtyp == IMatchHighlight.HIGHLIGHT_SUB_SPIELENDE)) {
            		break;
            	}

            	n++;
            }

            // check for redcarded highlights
            for (int i = 0; i < broken.size(); i++) {
            	final int tmpid = ((Integer) broken.get(i)).intValue();
            	final MatchHighlight tmp = (MatchHighlight) myHighlights.get(tmpid);

            	String subjectplayername = "";
            	String objectplayername = "";
            	boolean subHome = true;
            	boolean objHome = true;
            	int j = 0;

            	while (j < homeTeamPlayers.size()) {
            		if ((!subjectplayername.equals("")) && (!objectplayername.equals(""))) {
            			break;
            		}

            		final Vector<String> tmpPlayer = homeTeamPlayers.get(j);

            		if (tmpPlayer.get(0).toString().equals(String.valueOf(tmp.getSpielerID()))) {
            			subjectplayername = tmpPlayer.get(1).toString();
            		}

            		if (tmpPlayer.get(0).toString().equals(String.valueOf(tmp.getGehilfeID()))) {
            			objectplayername = tmpPlayer.get(1).toString();
            		}

            		j++;
            	}

            	j = 0;

            	while (j < awayTeamPlayers.size()) {
            		if ((!subjectplayername.equals("")) && (!objectplayername.equals(""))) {
            			break;
            		}

            		final Vector<String> tmpPlayer = awayTeamPlayers.get(j);

            		if (tmpPlayer.get(0).toString().equals(String.valueOf(tmp.getSpielerID()))) {
            			subjectplayername = tmpPlayer.get(1).toString();
            			subHome = false;
            		}

            		if (tmpPlayer.get(0).toString().equals(String.valueOf(tmp.getGehilfeID()))) {
            			objectplayername = tmpPlayer.get(1).toString();
            			objHome = false;
            		}

            		j++;
            	}

            	if (!subjectplayername.equals("")) {
            		String subplayerColor = "#000000";

            		if (subHome) {
            			subplayerColor = "#009900";
            		} else {
            			subplayerColor = "#990000";
            		}

            		String objplayerColor = "#000000";

            		if (objHome) {
            			objplayerColor = "#009900";
            		} else {
            			objplayerColor = "#990000";
            		}

            		String eventtext = tmp.getEventText();
            		boolean replaceend = false;

            		if (eventtext.indexOf(String.valueOf(tmp.getSpielerID())) >= 0) {
            			eventtext = eventtext.replaceAll("(?i)<A HREF=\"/Club/Players/Player\\.aspx\\?PlayerID="
            					+ tmp.getSpielerID() + ".*?>",
            					"<FONT COLOR=" + subplayerColor + "#><B>");
            			replaceend = true;
            		}

            		if (eventtext.indexOf(String.valueOf(tmp.getGehilfeID())) >= 0) {
            			eventtext = eventtext.replaceAll("(?i)<A HREF=\"/Club/Players/Player\\.aspx\\?playerId="
            					+ tmp.getGehilfeID() + ".*?>",
            					"<FONT COLOR=" + objplayerColor + "#><B>");
            			replaceend = true;
            		}

            		if (replaceend) {
            			eventtext = eventtext.replaceAll("(?i)</A>", "</B></FONT>");
            		}

            		tmp.setSpielerName(subjectplayername);
            		tmp.setSpielerHeim(subHome);
            		tmp.setGehilfeName(objectplayername);
            		tmp.setGehilfeHeim(objHome);
            		tmp.setEventText(eventtext);
            	}
            }
            md.setHighlights(myHighlights);
        } catch (Exception e) {
        	HOLogger.instance().log(XMLMatchdetailsParser.class, e);
        	md = null;
        }
    }

    /**
     * convert the existing team lineup into a Vector of Vectors (of playerId, playerName)
     *
     * @param lineup (of MatchLineupPlayer)		team lineup
     */
    private static Vector<Vector<String>> parseLineup (Vector<MatchLineupPlayer> lineup) {
    	Vector<Vector<String>> players = new Vector<Vector<String>>();
        MatchLineupPlayer player = null;

        for (int i = 0; (lineup != null) && (i < lineup.size()); i++) {
            player = lineup.elementAt(i);
            final Vector<String> tmpPlayer = new Vector<String>();
            tmpPlayer.add("" + player.getSpielerId());
            tmpPlayer.add(player.getSpielerName());
            players.add(tmpPlayer);
        }
    	return players;
    }
    
    /**
     * parse match report from previously parsed highlights
     *
     * @param md	match details
     */
    private static void parseMatchReport(Matchdetails md) {
        Vector<MatchHighlight> highlights = md.getHighlights();

        final StringBuffer report = new StringBuffer();

        for (int k = 0; k < highlights.size(); k++) {
            final MatchHighlight tmp = (MatchHighlight) highlights.get(k);
            report.append(tmp.getEventText()+" ");
        }

        md.setMatchreport(report.toString());
    }

    private static void readArena(Document doc, Matchdetails md) {
        Element ele = null;
        Element root = null;

        root = doc.getDocumentElement();

        try {
            //Daten füllen            
            //MatchData
            root = (Element) root.getElementsByTagName("Match").item(0);
            root = (Element) root.getElementsByTagName("Arena").item(0);
            
            
            try {
            	ele = (Element) root.getElementsByTagName("ArenaID").item(0);
            	md.setArenaID(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            	ele = (Element) root.getElementsByTagName("ArenaName").item(0);
            	md.setArenaName(ele.getFirstChild().getNodeValue());
            } catch (Exception e){
            	// This fails at tournament matches - ignore
            }
            
            ele = (Element) root.getElementsByTagName("WeatherID").item(0);
            md.setWetterId(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("SoldTotal").item(0);
            md.setZuschauer(Integer.parseInt(ele.getFirstChild().getNodeValue()));
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
            md = null;
        }
    }

	private static void readMatchType(Document doc, Matchdetails md) {
		Element ele = null;
		Element root = null;

		root = doc.getDocumentElement();

		try {
			//Daten füllen
			ele = (Element) root.getElementsByTagName("FetchedDate").item(0);
			md.setFetchDatumFromString(ele.getFirstChild().getNodeValue());

			//MatchData
			root = (Element) root.getElementsByTagName("Match").item(0);
			ele = (Element) root.getElementsByTagName("MatchID").item(0);
			md.setMatchID(Integer.parseInt(ele.getFirstChild().getNodeValue()));
			ele = (Element) root.getElementsByTagName("MatchDate").item(0);
			md.setSpielDatumFromString(ele.getFirstChild().getNodeValue());
		} catch (Exception e) {
			HOLogger.instance().log(XMLMatchdetailsParser.class,e);
			md = null;
		}
	}

    private static void readGeneral(Document doc, Matchdetails md) {
        Element ele = null;
        Element root = null;
        int iMatchType;
		int iCupLevel;
		int iCupLevelIndex;

        root = doc.getDocumentElement();

        try {
            //Daten füllen
            ele = (Element) root.getElementsByTagName("FetchedDate").item(0);
            md.setFetchDatumFromString(ele.getFirstChild().getNodeValue());

            //MatchData
            root = (Element) root.getElementsByTagName("Match").item(0);
			ele = (Element) root.getElementsByTagName("MatchType").item(0);
			iMatchType = Integer.parseInt(ele.getFirstChild().getNodeValue());
			if (iMatchType != 3) {md.setMatchType(MatchType.getById(iMatchType));}
			else{
				ele = (Element) root.getElementsByTagName("CupLevel").item(0);
				iCupLevel = Integer.parseInt(ele.getFirstChild().getNodeValue());
				ele = (Element) root.getElementsByTagName("CupLevelIndex").item(0);
				iCupLevelIndex = Integer.parseInt(ele.getFirstChild().getNodeValue());
				md.setMatchType(MatchType.getById(iMatchType, iCupLevel, iCupLevelIndex, 0));
			}
			if (iMatchType == 50) {
				ele = (Element) root.getElementsByTagName("MatchContextId").item(0);
				int tournamentId = Integer.parseInt(ele.getFirstChild().getNodeValue());

				TournamentDetails oTournamentDetails = DBManager.instance().getTournamentDetailsFromDB(tournamentId);
				if (oTournamentDetails == null)
				{
					oTournamentDetails = getTournamentDetails(tournamentId); // download info about tournament from HT
					DBManager.instance().storeTournamentDetailsIntoDB(oTournamentDetails); // store tournament details into DB
				}
				md.setMatchType(MatchType.getById(iMatchType, 0, 0, oTournamentDetails.getTournamentType()));
			}
            ele = (Element) root.getElementsByTagName("MatchID").item(0);
            md.setMatchID(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("MatchDate").item(0);
            md.setSpielDatumFromString(ele.getFirstChild().getNodeValue());
        } catch (Exception e) {
            HOLogger.instance().log(XMLMatchdetailsParser.class,e);
            md = null;
        }
    }

    private static void readGuestTeam(Document doc, Matchdetails md) {
        Element ele = null;
        Element root = null;

        try {
            //Daten füllen                        
            root = doc.getDocumentElement();
            root = (Element) root.getElementsByTagName("Match").item(0);
            root = (Element) root.getElementsByTagName("AwayTeam").item(0);

            //Data
            ele = (Element) root.getElementsByTagName("AwayTeamID").item(0);
            md.setGastId(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("AwayTeamName").item(0);
            md.setGastName(ele.getFirstChild().getNodeValue());
            ele = (Element) root.getElementsByTagName("AwayGoals").item(0);
            md.setGuestGoals(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("TacticType").item(0);
            md.setGuestTacticType(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("TacticSkill").item(0);
            md.setGuestTacticSkill(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingMidfield").item(0);
            md.setGuestMidfield(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingRightDef").item(0);
            md.setGuestRightDef(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingMidDef").item(0);
            md.setGuestMidDef(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingLeftDef").item(0);
            md.setGuestLeftDef(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingRightAtt").item(0);
            md.setGuestRightAtt(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingMidAtt").item(0);
            md.setGuestMidAtt(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingLeftAtt").item(0);
            md.setGuestLeftAtt(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            try {
                ele = (Element) root.getElementsByTagName("TeamAttitude").item(0);
                md.setGuestEinstellung(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            } catch (Exception e) {
                //nicht tragisch da Attrib nur bei eigenem Team vorhanden wäre
                md.setGuestEinstellung(Matchdetails.EINSTELLUNG_UNBEKANNT);
            }
        } catch (Exception e) {
            HOLogger.instance().log(XMLMatchdetailsParser.class, e);
            md = null;
        }
    }

    private static void readHomeTeam(Document doc, Matchdetails md) {
        Element ele = null;
        Element root = null;

        try {
            //Daten füllen                        
            root = doc.getDocumentElement();
            root = (Element) root.getElementsByTagName("Match").item(0);
            root = (Element) root.getElementsByTagName("HomeTeam").item(0);

            //Data
            ele = (Element) root.getElementsByTagName("HomeTeamID").item(0);
            md.setHeimId(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("HomeTeamName").item(0);
            md.setHeimName(ele.getFirstChild().getNodeValue());
            ele = (Element) root.getElementsByTagName("HomeGoals").item(0);
            md.setHomeGoals(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("TacticType").item(0);
            md.setHomeTacticType(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("TacticSkill").item(0);
            md.setHomeTacticSkill(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingMidfield").item(0);
            md.setHomeMidfield(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingRightDef").item(0);
            md.setHomeRightDef(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingMidDef").item(0);
            md.setHomeMidDef(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingLeftDef").item(0);
            md.setHomeLeftDef(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingRightAtt").item(0);
            md.setHomeRightAtt(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingMidAtt").item(0);
            md.setHomeMidAtt(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("RatingLeftAtt").item(0);
            md.setHomeLeftAtt(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            try {
                ele = (Element) root.getElementsByTagName("TeamAttitude").item(0);
                md.setHomeEinstellung(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            } catch (Exception e) {
                //nicht tragisch da Attrib nur bei eigenem Team vorhanden wäre, als Unbekannt markieren
                md.setHomeEinstellung(Matchdetails.EINSTELLUNG_UNBEKANNT);
            }
        } catch (Exception e) {
            HOLogger.instance().log(XMLMatchdetailsParser.class, e);
            md = null;
        }
    }
}
