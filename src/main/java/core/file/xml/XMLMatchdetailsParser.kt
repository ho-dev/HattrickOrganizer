package core.file.xml

import core.db.DBManager.getTournamentDetailsFromDB
import core.db.DBManager.storeTournamentDetailsIntoDB
import core.file.xml.XMLManager.getFirstChildNodeValue
import core.file.xml.XMLManager.parseString
import core.model.cup.CupLevel
import core.model.cup.CupLevelIndex
import core.model.enums.MatchType
import core.model.match.*
import core.model.match.Matchdetails.Injury
import core.model.match.Matchdetails.eInjuryType
import core.net.OnlineWorker
import core.util.HOLogger
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author thomas.werth
 */
object XMLMatchdetailsParser {
    @JvmStatic
	fun parseMatchDetailsFromString(input: String, matchLineup: MatchLineup?): Matchdetails? {
        return createMatchDetails(parseString(input), matchLineup)
    }

    private fun createMatchDetails(doc: Document?, matchLineup: MatchLineup?): Matchdetails? {
        var md: Matchdetails? = null
        if (doc != null) {
            try {
                md = Matchdetails()
                readGeneral(doc, md)
                readArena(doc, md)
                readGuestTeam(doc, md)
                readHomeTeam(doc, md)
                readInjuries(doc, md)
                if (matchLineup != null) {
                    // Match lineup needs to be available, if not -> ignore match highlights/report
                    readHighlights(doc, md, matchLineup)
                    parseMatchReport(md)
                    val guest = matchLineup.getGuestTeam()
                    guest.matchTeamAttitude = MatchTeamAttitude.fromInt(md.guestEinstellung)
                    guest.matchTacticType = MatchTacticType.fromInt(md.guestTacticType)
                    val home = matchLineup.getHomeTeam()
                    home.matchTeamAttitude = MatchTeamAttitude.fromInt(md.homeEinstellung)
                    home.matchTacticType = MatchTacticType.fromInt(md.homeTacticType)
                }
                md.setStatisics()
            } catch (e: Exception) {
                HOLogger.instance().log(XMLMatchdetailsParser::class.java, e)
                return null
            }
        }
        return md
    }

    /**
     * read the match injuries from XML
     *
     * @param doc    XML document
     * @param md    match details
     */
    private fun readInjuries(doc: Document, md: Matchdetails) {
        val mdInjuries = mutableListOf<Injury>()

        try {
            //get Injuries element
            var root:Element? = doc.documentElement
            val ele:Element? = root?.getElementsByTagName("Injuries")?.item(0) as Element?
            val injuryList:NodeList? = ele?.getElementsByTagName("Injury")

            //now go through the injuries
            if (injuryList != null) {
                for (n in 0 until injuryList.length) {
                    root = injuryList.item(n) as Element?
                    val injuryPlayerID =
                        getFirstChildNodeValue(root?.getElementsByTagName("InjuryPlayerID")?.item(0) as Element?).toInt()
                    val injuryTeamID =
                        getFirstChildNodeValue(root?.getElementsByTagName("InjuryTeamID")?.item(0) as Element?).toInt()
                    val injuryType =
                        getFirstChildNodeValue(root?.getElementsByTagName("InjuryType")?.item(0) as Element?).toInt()
                    val injuryMinute =
                        getFirstChildNodeValue(root?.getElementsByTagName("InjuryMinute")?.item(0) as Element?).toInt()
                    val matchPart =
                        getFirstChildNodeValue(root?.getElementsByTagName("MatchPart")?.item(0) as Element?).toInt()
                    val injury = Injury(injuryPlayerID, injuryTeamID, injuryType, injuryMinute, matchPart)
                    mdInjuries.add(injury)
                }
            }
            // TODO Remove wrapping to ArrayList once MatchDetails migrated ti Kotlin.
            md.setM_Injuries(ArrayList<Injury>(mdInjuries))
        } catch (e: Exception) {
            HOLogger.instance().log(XMLMatchdetailsParser::class.java, e)
        }
    }

    /**
     * read the match highlights from XML
     *
     * @param doc    XML document
     * @param md    match details
     */
    private fun readHighlights(doc: Document, md: Matchdetails, lineup: MatchLineup) {
        val matchEvents = ArrayList<MatchEvent>()

        val ele: Element?
        val eventList: NodeList?
        var eventtext: String
        try {
            //get Root element
            var root:Element? = doc.documentElement
            root = root?.getElementsByTagName("Match")?.item(0) as Element?
            //get both teams
            // ele = root?.getElementsByTagName("HomeTeam")?.item(0) as Element?
//            val homeTeamID = getFirstChildNodeValue(ele?.getElementsByTagName("HomeTeamID")?.item(0) as Element?)
            ele = root?.getElementsByTagName("EventList")?.item(0) as Element?
            eventList = ele?.getElementsByTagName("Event")

            //now go through the match events
            if (eventList != null) {
                for (n in 0 until eventList.length) {
                    root = eventList.item(n) as Element?

                    //get values from xml
                    val iMinute = getFirstChildNodeValue(root?.getElementsByTagName("Minute")?.item(0) as Element?).toInt()
                    val iSubjectPlayerID =
                        getFirstChildNodeValue(
                            root?.getElementsByTagName("SubjectPlayerID")?.item(0) as Element?
                        ).toInt()
                    val iSubjectTeamID =
                        getFirstChildNodeValue(root?.getElementsByTagName("SubjectTeamID")?.item(0) as Element?).toInt()
                    val iObjectPlayerID =
                        getFirstChildNodeValue(
                            root?.getElementsByTagName("ObjectPlayerID")?.item(0) as Element?
                        ).toInt()
                    val iMatchPart =
                        getFirstChildNodeValue(root?.getElementsByTagName("MatchPart")?.item(0) as Element?).toInt()
                    val iEventVariation =
                        getFirstChildNodeValue(
                            root?.getElementsByTagName("EventVariation")?.item(0) as Element?
                        ).toInt()
                    eventtext = getFirstChildNodeValue(root?.getElementsByTagName("EventText")?.item(0) as Element?)
                    eventtext = eventtext.replace("&lt;".toRegex(), "<")
                    eventtext = eventtext.replace("&gt;".toRegex(), ">")
                    eventtext = eventtext.replace("/>".toRegex(), ">")
                    eventtext = eventtext.replace("&quot;".toRegex(), "\"")
                    eventtext = eventtext.replace("&amp;".toRegex(), "&")

                    // Convert the ID to type and subtype.
                    val iMatchEventID =
                        getFirstChildNodeValue(root?.getElementsByTagName("EventTypeID")?.item(0) as Element?).toInt()

                    //get players
                    var subHome = true
                    var objHome = true
                    var subjectPlayer: MatchLineupPosition? = null
                    var objectPlayer: MatchLineupPosition? = null
                    if (iMinute > 0) {
                        subjectPlayer = lineup.getHomeTeam().getPlayerByID(iSubjectPlayerID, true)
                        objectPlayer = lineup.getHomeTeam().getPlayerByID(iObjectPlayerID, true)
                        if (subjectPlayer == null) {
                            subjectPlayer = lineup.getGuestTeam().getPlayerByID(iSubjectPlayerID, true)
                            subHome = false
                        }
                        if (objectPlayer == null) {
                            objectPlayer = lineup.getGuestTeam().getPlayerByID(iObjectPlayerID, true)
                            objHome = false
                        }
                    }

                    //modify eventtext
                    if (subjectPlayer != null) {
                        val subplayerColor: String = if (subHome) {
                            "#000099"
                        } else {
                            "#990000"
                        }
                        val objplayerColor: String = if (objHome) {
                            "#000099"
                        } else {
                            "#990000"
                        }
                        var replaceend = false
                        if (eventtext.contains(iSubjectPlayerID.toString())) {
                            eventtext = eventtext.replace(
                                ("(?i)<A HREF=\"/Club/Players/Player\\.aspx\\?playerId="
                                        + iSubjectPlayerID + ".*?>").toRegex(),
                                "<FONT COLOR=$subplayerColor#><B>"
                            )
                            replaceend = true
                        }
                        if (eventtext.contains(iObjectPlayerID.toString())) {
                            eventtext = eventtext.replace(
                                ("(?i)<A HREF=\"/Club/Players/Player\\.aspx\\?playerId="
                                        + iObjectPlayerID + ".*?>").toRegex(),
                                "<FONT COLOR=$objplayerColor#><B>"
                            )
                            replaceend = true
                        }
                        if (replaceend) {
                            eventtext = eventtext.replace("(?i)</A>".toRegex(), "</B></FONT>")
                        }
                    }

                    //generate MatchHighlight and add to list
                    val myHighlight = MatchEvent()
                    myHighlight.matchEventIndex = n + 1
                    myHighlight.setMatchEventID(iMatchEventID)
                    myHighlight.minute = iMinute
                    myHighlight.playerId = iSubjectPlayerID
                    myHighlight.playerName = if (subjectPlayer != null) subjectPlayer.getSpielerName() else ""
                    myHighlight.spielerHeim = subHome
                    myHighlight.teamID = iSubjectTeamID
                    myHighlight.assistingPlayerId = iObjectPlayerID
                    myHighlight.assistingPlayerName = if (objectPlayer != null) objectPlayer.getSpielerName() else ""
                    myHighlight.gehilfeHeim = objHome
                    myHighlight.eventText = eventtext
                    myHighlight.matchPartId = MatchEvent.MatchPartId.fromMatchPartId(iMatchPart)
                    myHighlight.eventVariation = iEventVariation
                    if (myHighlight.matchEventID == MatchEvent.MatchEventID.UNKNOWN_MATCHEVENT) {
                        HOLogger.instance().warning(
                            XMLMatchdetailsParser::class.java, "Unknown event id found in match " +
                                    md.homeTeamName + "-" + md.guestTeamName +
                                    " in minute " + myHighlight.minute +
                                    " text: " + myHighlight.eventText
                        )
                    }

                    // Treat injury
                    when (iMatchEventID) {
                        90, 94 -> {
                            myHighlight.setM_eInjuryType(eInjuryType.BRUISE)
                        }
                        91, 92, 93, 96 -> {
                            myHighlight.setM_eInjuryType(eInjuryType.INJURY)
                        }
                        in 401..422 -> {
                            myHighlight.setM_eInjuryType(getInjuryType(iSubjectPlayerID, md.getM_Injuries()))
                        }
                        else -> {
                            myHighlight.setM_eInjuryType(eInjuryType.NA)
                        }
                    }
                    matchEvents.add(myHighlight)
                }
            }
            md.setHighlights(matchEvents)
        } catch (e: Exception) {
            HOLogger.instance().log(XMLMatchdetailsParser::class.java, e)
        }
    }

    /**
     * parse match report from previously parsed highlights
     *
     * @param md    match details
     */
    private fun parseMatchReport(md: Matchdetails) {
        val highlights = md.getHighlights()
        val report = StringBuilder()
        for (highlight in highlights) {
            report.append(highlight.eventText).append(" ")
        }
        md.matchreport = report.toString()
    }

    private fun readArena(doc: Document, md: Matchdetails) {
        var ele: Element?
        var root:Element? = doc.documentElement
        try {
            root = root?.getElementsByTagName("Match")?.item(0) as Element?
            root = root?.getElementsByTagName("Arena")?.item(0) as Element?
            try {
                ele = root?.getElementsByTagName("ArenaID")?.item(0) as Element?
                md.arenaID = ele?.firstChild?.nodeValue?.toInt() ?: -1
                ele = root?.getElementsByTagName("ArenaName")?.item(0) as Element?
                if (ele?.firstChild != null) {
                    md.arenaName = ele.firstChild.nodeValue
                }
            } catch (e: Exception) {
                // This fails at tournament matches - ignore
            }
            ele = root?.getElementsByTagName("WeatherID")?.item(0) as Element?
            md.wetterId = ele?.firstChild?.nodeValue?.toInt() ?: -1
            ele = root?.getElementsByTagName("SoldTotal")?.item(0) as Element?
            md.zuschauer = ele?.firstChild?.nodeValue?.toInt() ?: -1
            // Get spectator distribution, if available
            val soldTerrace:NodeList? = root?.getElementsByTagName("SoldTerraces")
            if (soldTerrace != null && soldTerrace.length > 0) {
                ele = root?.getElementsByTagName("SoldTerraces")?.item(0) as Element?
                md.soldTerraces = ele?.firstChild?.nodeValue?.toInt() ?: -1
                ele = root?.getElementsByTagName("SoldBasic")?.item(0) as Element?
                md.soldBasic = ele?.firstChild?.nodeValue?.toInt() ?: -1
                ele = root?.getElementsByTagName("SoldRoof")?.item(0) as Element?
                md.soldRoof = ele?.firstChild?.nodeValue?.toInt() ?: -1
                ele = root?.getElementsByTagName("SoldVIP")?.item(0) as Element?
                md.soldVIP = ele?.firstChild?.nodeValue?.toInt() ?: -1
            }
        } catch (e: Exception) {
            HOLogger.instance().log(XMLMatchdetailsParser::class.java, e)
        }
    }

    private fun readGeneral(doc: Document, md: Matchdetails) {
        var ele: Element?

        var root:Element? = doc.documentElement
        try {
            //Daten füllen
            ele = root?.getElementsByTagName("FetchedDate")?.item(0) as Element?
            md.setFetchDatumFromString(ele?.firstChild?.nodeValue)

            //MatchData
            root = root?.getElementsByTagName("Match")?.item(0) as Element?
            ele = root?.getElementsByTagName("MatchType")?.item(0) as Element?
            val iMatchType = ele?.firstChild?.nodeValue?.toInt()
            val matchType = MatchType.getById(iMatchType)
            md.matchType = matchType
            if (iMatchType == 3) {
                ele = root?.getElementsByTagName("CupLevel")?.item(0) as Element?
                val iCupLevel = ele?.firstChild?.nodeValue?.toInt()
                md.cupLevel = CupLevel.fromInt(iCupLevel)
                ele = root?.getElementsByTagName("CupLevelIndex")?.item(0) as Element?
                val iCupLevelIndex = ele?.firstChild?.nodeValue?.toInt()
                md.cupLevelIndex = CupLevelIndex.fromInt(iCupLevelIndex)
            } else if (iMatchType == 50) {
                ele = root?.getElementsByTagName("MatchContextId")?.item(0) as Element?
                val tournamentId = ele?.firstChild?.nodeValue?.toInt()
                if (tournamentId != null) {
                    md.matchContextId = tournamentId
                    var oTournamentDetails = getTournamentDetailsFromDB(tournamentId)
                    if (oTournamentDetails == null) {
                        oTournamentDetails = OnlineWorker.getTournamentDetails(tournamentId) // download info about tournament from HT
                        if (oTournamentDetails != null) {
                            storeTournamentDetailsIntoDB(oTournamentDetails) // store tournament details into DB
                        }
                    }
                    md.tournamentTypeID = oTournamentDetails?.tournamentType ?: -1
                }
            }
            ele = root?.getElementsByTagName("MatchID")?.item(0) as Element?
            md.matchID = ele?.firstChild?.nodeValue?.toInt() ?: -1
            ele = root?.getElementsByTagName("MatchDate")?.item(0) as Element?
            md.setSpielDatumFromString(ele?.firstChild?.nodeValue)
        } catch (e: Exception) {
            HOLogger.instance().log(XMLMatchdetailsParser::class.java, e)
        }
    }

    private fun readGuestTeam(doc: Document, md: Matchdetails) {
        var ele: Element?

        try {

            var root:Element? = doc.documentElement
            root = root?.getElementsByTagName("Match")?.item(0) as Element?
            root = root?.getElementsByTagName("AwayTeam")?.item(0) as Element?
            val formation:NodeList? = root?.getElementsByTagName("Formation")
            if (formation != null && formation.length > 0) {
                md.setAwayFormation(formation.item(0).textContent)
            }
            ele = root?.getElementsByTagName("AwayTeamID")?.item(0) as Element?
            if (ele != null) md.setGastId(ele.firstChild.nodeValue.toInt())
            ele = root?.getElementsByTagName("AwayTeamName")?.item(0) as Element?
            if (ele != null) md.setGastName(ele.firstChild.nodeValue)
            ele = root?.getElementsByTagName("AwayGoals")?.item(0) as Element?
            if (ele != null) md.guestGoals = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("TacticType")?.item(0) as Element?
            if (ele != null) md.guestTacticType = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("TacticSkill")?.item(0) as Element?
            if (ele != null) md.guestTacticSkill = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingMidfield")?.item(0) as Element?
            if (ele != null) md.guestMidfield = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingRightDef")?.item(0) as Element?
            if (ele != null) md.guestRightDef = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingMidDef")?.item(0) as Element?
            if (ele != null) md.guestMidDef = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingLeftDef")?.item(0) as Element?
            if (ele != null) md.guestLeftDef = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingRightAtt")?.item(0) as Element?
            if (ele != null) md.guestRightAtt = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingMidAtt")?.item(0) as Element?
            if (ele != null) md.guestMidAtt = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingLeftAtt")?.item(0) as Element?
            if (ele != null) md.guestLeftAtt = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingIndirectSetPiecesAtt")?.item(0) as Element?
            if (ele != null) md.ratingIndirectSetPiecesAtt = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingIndirectSetPiecesDef")?.item(0) as Element?
            if (ele != null) md.ratingIndirectSetPiecesDef = ele.firstChild.nodeValue.toInt()
            val teamAttitude = root?.getElementsByTagName("TeamAttitude")
            if (teamAttitude != null && teamAttitude.length > 0) {
                ele = teamAttitude.item(0) as Element?
                md.guestEinstellung = ele?.firstChild?.nodeValue?.toInt() ?: -1
            } else {
                md.guestEinstellung = Matchdetails.EINSTELLUNG_UNBEKANNT
            }
        } catch (e: Exception) {
            HOLogger.instance().log(XMLMatchdetailsParser::class.java, e)
        }
    }

    private fun readHomeTeam(doc: Document, md: Matchdetails) {
        var ele: Element?
        var root: Element?
        try {
            //Daten füllen
            root = doc.documentElement
            root = root?.getElementsByTagName("Match")?.item(0) as Element?
            root = root?.getElementsByTagName("HomeTeam")?.item(0) as Element?

            //Data
            val formation = root?.getElementsByTagName("Formation")
            if (formation != null && formation.length > 0) {
                md.setHomeFormation(formation.item(0).textContent)
            }
            ele = root?.getElementsByTagName("HomeTeamID")?.item(0) as Element?
            if (ele != null) md.setHeimId(ele.firstChild.nodeValue.toInt())
            ele = root?.getElementsByTagName("HomeTeamName")?.item(0) as Element?
            if (ele != null) md.setHeimName(ele.firstChild.nodeValue)
            ele = root?.getElementsByTagName("HomeGoals")?.item(0) as Element?
            if (ele != null) md.homeGoals = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("TacticType")?.item(0) as Element?
            if (ele != null) md.homeTacticType = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("TacticSkill")?.item(0) as Element?
            if (ele != null) md.homeTacticSkill = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingMidfield")?.item(0) as Element?
            if (ele != null) md.homeMidfield = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingRightDef")?.item(0) as Element?
            if (ele != null) md.homeRightDef = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingMidDef")?.item(0) as Element?
            if (ele != null) md.homeMidDef = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingLeftDef")?.item(0) as Element?
            if (ele != null) md.homeLeftDef = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingRightAtt")?.item(0) as Element?
            if (ele != null) md.homeRightAtt = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingMidAtt")?.item(0) as Element?
            if (ele != null) md.homeMidAtt = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingLeftAtt")?.item(0) as Element?
            if (ele != null) md.homeLeftAtt = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingIndirectSetPiecesAtt")?.item(0) as Element?
            if (ele != null) md.ratingIndirectSetPiecesAtt = ele.firstChild.nodeValue.toInt()
            ele = root?.getElementsByTagName("RatingIndirectSetPiecesDef")?.item(0) as Element?
            if (ele != null) md.ratingIndirectSetPiecesDef = ele.firstChild.nodeValue.toInt()
            val teamAttitude = root?.getElementsByTagName("TeamAttitude")
            if (teamAttitude != null && teamAttitude.length > 0) {
                ele = teamAttitude.item(0) as Element
                md.homeEinstellung = ele.firstChild.nodeValue.toInt()
            } else {
                md.homeEinstellung = Matchdetails.EINSTELLUNG_UNBEKANNT
            }
        } catch (e: Exception) {
            HOLogger.instance().log(XMLMatchdetailsParser::class.java, e)
        }
    }

    private fun getInjuryType(iPlayerID: Int, injuries: ArrayList<Injury>): eInjuryType {
        for (injury in injuries) {
            if (injury.injuryPlayerID == iPlayerID && injury.injuryPlayerID == iPlayerID) {
                return injury.injuryType
            }
        }
        HOLogger.instance()
            .log(XMLMatchdetailsParser::class.java, "the injured player was not listed !!! This is not normal ")
        return eInjuryType.NA
    }
}
