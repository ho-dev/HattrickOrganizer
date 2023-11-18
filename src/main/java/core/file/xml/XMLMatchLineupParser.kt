package core.file.xml

import core.file.xml.XMLManager.getFirstChildNodeValue
import core.file.xml.XMLManager.parseString
import core.model.enums.MatchType
import core.model.match.MatchLineup
import core.model.match.MatchLineupPosition
import core.model.match.MatchLineupTeam
import core.model.player.IMatchRoleID
import core.model.player.MatchRoleID
import core.util.HOLogger
import module.lineup.substitution.model.GoalDiffCriteria
import module.lineup.substitution.model.MatchOrderType
import module.lineup.substitution.model.RedCardCriteria
import module.lineup.substitution.model.Substitution
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 *
 * @author thomas.werth
 */
object XMLMatchLineupParser {
	fun parseMatchLineupFromString(inputStream: String): MatchLineup? {
        return createLineup(parseString(inputStream))
    }

    private fun createLineup(doc: Document?): MatchLineup? {
        var ml: MatchLineup? = MatchLineup()
        if (doc == null) {
            return ml
        }

        try {
            if (ml != null) {
                val root: Element? = doc.documentElement
                var ele: Element? = root?.getElementsByTagName("MatchID")?.item(0) as Element?
                ml.matchID = ele?.firstChild?.nodeValue?.toInt() ?: -1

                ele = root?.getElementsByTagName("MatchType")?.item(0) as Element?
                ml.matchTyp = MatchType.getById(ele?.firstChild?.nodeValue?.toInt())

                ele = root?.getElementsByTagName("HomeTeam")?.item(0) as Element?
                ml.homeTeamId = ele?.getElementsByTagName("HomeTeamID")?.item(0)?.firstChild?.nodeValue?.toInt() ?: -1
                ml.homeTeamName = ele?.getElementsByTagName("HomeTeamName")?.item(0)?.firstChild?.nodeValue

                ele = root?.getElementsByTagName("AwayTeam")?.item(0) as Element?
                ml.guestTeamId = ele?.getElementsByTagName("AwayTeamID")?.item(0)?.firstChild?.nodeValue?.toInt() ?: -1
                ml.guestTeamName = ele?.getElementsByTagName("AwayTeamName")?.item(0)?.firstChild?.nodeValue

                val team = createTeam(ml.matchType, ml.matchID, root?.getElementsByTagName("Team")?.item(0) as Element?)
                if (team.teamID == ml.getHomeTeamId()) {
                    ml.homeTeam = team
                } else {
                    ml.guestTeam = team
                }
            }
        } catch (e: Exception) {
            HOLogger.instance().log(XMLMatchLineupParser::class.java, e)
            ml = null
        }
        return ml
    }

    private fun createPlayer(matchType: MatchType, ele: Element?): MatchLineupPosition {
        var roleID = -1
        var rating = -1.0
        var ratingStarsEndOfMatch = -1.0
        var name = ""
        var tmp = ele?.getElementsByTagName("PlayerID")?.item(0) as Element?
        val spielerID:Int = tmp?.firstChild?.nodeValue?.toInt() ?: -1
        var behavior:Byte = -1
        tmp = ele?.getElementsByTagName("RoleID")?.item(0) as Element?
        if (tmp != null) {
            roleID = tmp.firstChild.nodeValue.toInt()
        }

        // This is the right spot to wash the old role IDs if arrived by xml.
        // Position code is not include in 1.6 xml. It is not needed from the
        // older ones, what is necessary is to check for old reposition values in the
        // Behaviour.
        // We do move all repositions to central slot, and go happily belly up
        // if we find more than one repositioning to the same position 
        // (old setup where more than 3 forwards was possible)

        // if (roleID == 17 || roleID == 14) {
        // System.out.println("Give me somewhere to put a breakpoint");
        // }

        // HOLogger.instance().debug(getClass(),"RoleID in: " + roleID);

        // nur wenn Player existiert
        if (spielerID > 0) {
            // First- and LastName can be empty
            name = getStringValue(ele?.getElementsByTagName("FirstName")?.item(0) as Element?)
            val lastName = getStringValue(ele?.getElementsByTagName("LastName")?.item(0) as Element?)
            if (name.isNotEmpty() && lastName.isNotEmpty()) name = "$name "
            name += lastName

            // shift lineup ids to match order ids
            if (roleID >= IMatchRoleID.startReserves && roleID < IMatchRoleID.substGK1) {
                roleID += IMatchRoleID.substGK1 - IMatchRoleID.startReserves
            }

            // tactic is only set for those in the lineup (and not for the keeper).
            if (roleID == IMatchRoleID.keeper || IMatchRoleID.oldKeeper.contains(roleID)) {
                // Diese Werte sind von HT vorgegeben aber nicht garantiert
                // mitgeliefert in xml, daher selbst setzen!
                roleID = IMatchRoleID.keeper // takes care of the old
                // keeper ID.
            } else if (roleID >= 0 && roleID < IMatchRoleID.setPieces || roleID < IMatchRoleID.startReserves && roleID > IMatchRoleID.keeper) {
                tmp = ele?.getElementsByTagName("Behaviour")?.item(0) as Element?
                behavior = tmp?.firstChild?.nodeValue?.toByte() ?: -1
                when (behavior) {
                    IMatchRoleID.OLD_EXTRA_DEFENDER -> {
                        roleID = IMatchRoleID.middleCentralDefender
                        behavior = IMatchRoleID.NORMAL
                    }

                    IMatchRoleID.OLD_EXTRA_MIDFIELD -> {
                        roleID = IMatchRoleID.centralInnerMidfield
                        behavior = IMatchRoleID.NORMAL
                    }

                    IMatchRoleID.OLD_EXTRA_FORWARD -> {
                        roleID = IMatchRoleID.centralForward
                        behavior = IMatchRoleID.NORMAL
                    }

                    IMatchRoleID.OLD_EXTRA_DEFENSIVE_FORWARD -> {
                        roleID = IMatchRoleID.centralForward
                        behavior = IMatchRoleID.DEFENSIVE
                    }
                }

                // Wash the remaining old positions
                if (roleID < IMatchRoleID.setPieces) {
                    roleID = MatchRoleID.convertOldRoleToNew(roleID)
                }
            }

            // rating nur für leute die gespielt haben
            if (roleID >= IMatchRoleID.startLineup && roleID < IMatchRoleID.startReserves || roleID >= IMatchRoleID.FirstPlayerReplaced && roleID <= IMatchRoleID.ThirdPlayerReplaced) {
                tmp = ele?.getElementsByTagName("RatingStars")?.item(0) as Element?
                if (tmp != null) {
                    rating = tmp.firstChild.nodeValue.replace(",".toRegex(), ".").toDouble()
                    tmp = ele?.getElementsByTagName("RatingStarsEndOfMatch")?.item(0) as Element?
                    if (tmp != null) { // info is not available for youth players
                        ratingStarsEndOfMatch = tmp.firstChild.nodeValue.replace(",".toRegex(), ".").toDouble()
                    }
                }
            }
        }
        val player = MatchLineupPosition(roleID, spielerID, behavior.toInt(), rating, name, 0)
        player.ratingStarsEndOfMatch = ratingStarsEndOfMatch
        return player
    }

    /**
     * Get string content of ELement. If content is empty an empty string is returned.
     */
    private fun getStringValue(tmp: Element?): String {
        return if (tmp?.firstChild != null) tmp.firstChild.nodeValue else ""
    }

    private fun createTeam(matchType: MatchType, matchID: Int, ele: Element?): MatchLineupTeam {
        var tmp:Element? = ele?.getElementsByTagName("TeamID")?.item(0) as Element?
        val teamId = tmp?.firstChild?.nodeValue?.toInt() ?: -1
        tmp = ele?.getElementsByTagName("ExperienceLevel")?.item(0) as Element?
        val erfahrung = tmp?.firstChild?.nodeValue?.toInt() ?: -1
        // tmp = ele?.getElementsByTagName("StyleOfPlay")?.item(0) as Element?
        // val styleOfPlay = tmp?.firstChild?.nodeValue?.toInt()
        tmp = ele?.getElementsByTagName("TeamName")?.item(0) as Element?
        val teamName = tmp?.firstChild?.nodeValue
        val team = MatchLineupTeam(matchType, matchID, teamName, teamId, erfahrung)
        val starting = ele?.getElementsByTagName("StartingLineup")?.item(0) as Element?
        val subs = ele?.getElementsByTagName("Substitutions")?.item(0) as Element?
        tmp = ele?.getElementsByTagName("Lineup")?.item(0) as Element?

        // The normal end of match report
        // Adding entries
        var list = tmp?.getElementsByTagName("Player")
        if (list != null) {
            for (i in 0 until list.length) {

                // We want to stop an api error that has repositioned players as
                // substituted.
                // They are both shown as substituted and in a position. (hopefully)
                // substituted
                // players are always last in the API, there are at least signs of a
                // fixed order.
                val player = createPlayer(matchType, list.item(i) as Element?)
                if (team.getPlayerByID(player.playerId) != null) {
                    if (player.roleId >= IMatchRoleID.FirstPlayerReplaced && player.roleId <= IMatchRoleID.ThirdPlayerReplaced) {

                        // MatchLineup API bug, he is still on the pitch, so skip
                        continue
                    }
                }
                team.add2Lineup(player)
            }
        }

        // The starting lineup
        list = starting?.getElementsByTagName("Player")
        if (list != null) {
            for (i in 0 until list.length) {
                val startPlayer = createPlayer(matchType, list.item(i) as Element)
                startPlayer.startPosition = startPlayer.roleId // it is the role id
                startPlayer.startBehavior = startPlayer.behaviour.toInt()

                // Merge with the existing player, but ignore captain
                if (startPlayer.startPosition >= IMatchRoleID.startLineup || startPlayer.startPosition == IMatchRoleID.setPieces) {
                    val lineupPlayer = team.getPlayerByID(startPlayer.playerId)
                    if (lineupPlayer != null) {
                        if (startPlayer.startPosition == IMatchRoleID.setPieces) {
                            lineupPlayer.isStartSetPiecesTaker = true
                        } else {
                            lineupPlayer.startPosition = startPlayer.startPosition
                        }
                        lineupPlayer.startBehavior = startPlayer.startBehavior
                    } else {
                        // He was not already in the lineup, so add him
                        team.add2Lineup(startPlayer)
                        startPlayer.roleId = IMatchRoleID.UNKNOWN.toInt() // Maybe an injured player
                    }
                }
            }
        }

        // Substitutions
        list = subs?.getElementsByTagName("Substitution")
        val substitutions: MutableList<Substitution> = ArrayList()

        if (list != null) {
            for (i in 0 until list.length) {
                val s = createSubstitution(list.item(i) as Element, i)
                substitutions.add(s)
                // We need to make sure the players involved are in the team lineup
                // If missing, we only know the ID
                if (s.objectPlayerID > 0 && team.getPlayerByID(s.objectPlayerID) == null && s.orderType != MatchOrderType.MAN_MARKING) { // in case of MAN_MARKING the Object Player is an opponent player
                    team.add2Lineup(
                        MatchLineupPosition(
                            -1, -1, s.objectPlayerID, -1.0, "",
                            -1
                        )
                    )
                }
                if (s.subjectPlayerID > 0 && team.getPlayerByID(s.subjectPlayerID) == null) {
                    team.add2Lineup(
                        MatchLineupPosition(
                            -1, -1, s.subjectPlayerID, -1.0, "",
                            -1
                        )
                    )
                }
            }
        }
        team.setSubstitutions(substitutions)
        return team
    }

    private fun createSubstitution(ele: Element?, playerOrderID: Int): Substitution {
        var playerIn = -1
        var playerOut = -1
        var orderTypeId: Byte = -1
        var matchMinuteCriteria: Byte = -1
        var pos: Byte = -1
        var behaviour: Byte = -1
        var card: Byte = -1
        var standing: Byte = -1
        var tmp = ele?.getElementsByTagName("MatchMinute")?.item(0) as Element?
        if (tmp != null) {
            matchMinuteCriteria = getFirstChildNodeValue(tmp).toByte()
        }
        tmp = ele?.getElementsByTagName("GoalDiffCriteria")?.item(0) as Element?
        if (tmp != null) {
            standing = getFirstChildNodeValue(tmp).toByte()
        }
        tmp = ele?.getElementsByTagName("RedCardCriteria")?.item(0) as Element?
        if (tmp != null) {
            card = getFirstChildNodeValue(tmp).toByte()
        }
        tmp = ele?.getElementsByTagName("SubjectPlayerID")?.item(0) as Element?
        if (tmp != null) {
            playerOut = getFirstChildNodeValue(tmp).toInt()
        }
        tmp = ele?.getElementsByTagName("ObjectPlayerID")?.item(0) as Element?
        if (tmp != null) {
            playerIn = getFirstChildNodeValue(tmp).toInt()
        }
        tmp = ele?.getElementsByTagName("OrderType")?.item(0) as Element?
        if (tmp != null) {
            orderTypeId = getFirstChildNodeValue(tmp).toByte()
        }
        tmp = ele?.getElementsByTagName("NewPositionId")?.item(0) as Element?
        if (tmp != null) {
            pos = getFirstChildNodeValue(tmp).toByte()
        }
        tmp = ele?.getElementsByTagName("NewPositionBehaviour")?.item(0) as Element?
        if (tmp != null) {
            behaviour = getFirstChildNodeValue(tmp).toByte()
        }
        return Substitution(
            playerOrderID, playerIn, playerOut, orderTypeId,
            matchMinuteCriteria.toInt(), pos, behaviour, RedCardCriteria.getById(card),
            GoalDiffCriteria.getById(standing)
        )
    }
}
