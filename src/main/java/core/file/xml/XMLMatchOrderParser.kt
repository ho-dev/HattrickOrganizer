/*
 * xmlMatchOrderParser.java
 *
 * Created on 14. Juni 2004, 18:18
 */
package core.file.xml

import core.file.xml.XMLManager.getAttributeValue
import core.file.xml.XMLManager.getFirstChildNodeValue
import core.file.xml.XMLManager.parseString
import core.model.player.IMatchRoleID
import core.util.HOLogger
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList

/**
 * Parser for the matchorders.
 *
 * @author TheTom
 */
object XMLMatchOrderParser {
    @JvmStatic
	fun parseMatchOrderFromString(xmlData: String?): Map<String, String> {
        return parseDetails(parseString(xmlData!!))
    }

    /**
     * Create a player from the given XML.
     */
    private fun addPlayer(ele: Element?, map: SafeInsertMap) {
        var tmp: Element?
        var roleID = -1
        var behavior = "-1"

        tmp = ele?.getElementsByTagName("PlayerID")?.item(0) as Element?
        var spielerID = getFirstChildNodeValue(tmp)
        if (spielerID.trim { it <= ' ' } == "") {
            spielerID = "-1"
        }
        tmp = ele?.getElementsByTagName("RoleID")?.item(0) as Element?
        if (tmp != null) {
            roleID = getFirstChildNodeValue(tmp).toInt()
        } else if (ele?.tagName.equals("SetPieces", ignoreCase = true)) {
            roleID = IMatchRoleID.setPieces
        } else if (ele?.tagName.equals("Captain", ignoreCase = true)) {
            roleID = IMatchRoleID.captain
        }
        tmp = ele?.getElementsByTagName("LastName")?.item(0) as Element?
        val name = getFirstChildNodeValue(tmp)

        // individual orders only for the 10 players in the lineup (i.e. starting11 excluding keeper)
        if (roleID != IMatchRoleID.keeper && IMatchRoleID.aFieldMatchRoleID.contains(roleID)) {
            tmp = ele?.getElementsByTagName("Behaviour")?.item(0) as Element?
            behavior = getFirstChildNodeValue(tmp)
        }
        when (roleID) {
            IMatchRoleID.keeper -> {
                map.insert("KeeperID", spielerID)
                map.insert("KeeperName", name)
                map.insert("KeeperOrder", "0")
            }

            IMatchRoleID.rightBack -> {
                map.insert("RightBackID", spielerID)
                map.insert("RightBackName", name)
                map.insert("RightBackOrder", behavior)
            }

            IMatchRoleID.rightCentralDefender -> {
                if (!map.containsKey("RightCentralDefenderID")) {
                    map.insert("RightCentralDefenderID", spielerID)
                    map.insert("RightCentralDefenderName", name)
                    map.insert("RightCentralDefenderOrder", behavior)
                } else {
                    addAdditionalPlayer(map, roleID, spielerID, name, behavior)
                }
            }

            IMatchRoleID.middleCentralDefender -> {
                if (!map.containsKey("MiddleCentralDefenderID")) {
                    map.insert("MiddleCentralDefenderID", spielerID)
                    map.insert("MiddleCentralDefenderName", name)
                    map.insert("MiddleCentralDefenderOrder", behavior)
                } else {
                    addAdditionalPlayer(map, roleID, spielerID, name, behavior)
                }
            }

            IMatchRoleID.leftCentralDefender -> {
                map.insert("LeftCentralDefenderID", spielerID)
                map.insert("LeftCentralDefenderName", name)
                map.insert("LeftCentralDefenderOrder", behavior)
            }

            IMatchRoleID.leftBack -> {
                map.insert("LeftBackID", spielerID)
                map.insert("LeftBackName", name)
                map.insert("LeftBackOrder", behavior)
            }

            IMatchRoleID.leftWinger -> {
                if (!map.containsKey("LeftWingerID")) {
                    map.insert("LeftWingerID", spielerID)
                    map.insert("LeftWingerName", name)
                    map.insert("LeftWingerOrder", behavior)
                } else {
                    addAdditionalPlayer(map, roleID, spielerID, name, behavior)
                }
            }

            IMatchRoleID.leftInnerMidfield -> {
                if (!map.containsKey("LeftInnerMidfieldID")) {
                    map.insert("LeftInnerMidfieldID", spielerID)
                    map.insert("LeftInnerMidfieldName", name)
                    map.insert("LeftInnerMidfieldOrder", behavior)
                } else {
                    addAdditionalPlayer(map, roleID, spielerID, name, behavior)
                }
            }

            IMatchRoleID.centralInnerMidfield -> {
                if (!map.containsKey("CentralInnerMidfieldID")) {
                    map.insert("CentralInnerMidfieldID", spielerID)
                    map.insert("CentralInnerMidfieldName", name)
                    map.insert("CentralInnerMidfieldOrder", behavior)
                } else {
                    addAdditionalPlayer(map, roleID, spielerID, name, behavior)
                }
            }

            IMatchRoleID.rightInnerMidfield -> {
                map.insert("RightInnerMidfieldID", spielerID)
                map.insert("RightInnerMidfieldName", name)
                map.insert("RightInnerMidfieldOrder", behavior)
            }

            IMatchRoleID.rightWinger -> {
                if (!map.containsKey("RightWingerID")) {
                    map.insert("RightWingerID", spielerID)
                    map.insert("RightWingerName", name)
                    map.insert("RightWingerOrder", behavior)
                } else {
                    addAdditionalPlayer(map, roleID, spielerID, name, behavior)
                }
            }

            IMatchRoleID.rightForward -> {
                if (!map.containsKey("RightForward")) {
                    map.insert("RightForwardID", spielerID)
                    map.insert("RightForwardName", name)
                    map.insert("RightForwardOrder", behavior)
                } else {
                    addAdditionalPlayer(map, roleID, spielerID, name, behavior)
                }
            }

            IMatchRoleID.centralForward -> {
                if (!map.containsKey("CentralForward")) {
                    map.insert("CentralForwardID", spielerID)
                    map.insert("CentralForwardName", name)
                    map.insert("CentralForwardOrder", behavior)
                } else {
                    addAdditionalPlayer(map, roleID, spielerID, name, behavior)
                }
            }

            IMatchRoleID.leftForward -> {
                if (!map.containsKey("LeftForward")) {
                    map.insert("LeftForwardID", spielerID)
                    map.insert("LeftForwardName", name)
                    map.insert("LeftForwardOrder", behavior)
                } else {
                    addAdditionalPlayer(map, roleID, spielerID, name, behavior)
                }
            }

            IMatchRoleID.substGK1 -> {
                map.insert("substGK1ID", spielerID)
                map.insert("substGK1Name", name)
            }

            IMatchRoleID.substGK2 -> {
                map.insert("substGK2ID", spielerID)
                map.insert("substGK2Name", name)
            }

            IMatchRoleID.substCD1 -> {
                map.insert("substCD1ID", spielerID)
                map.insert("substCD1Name", name)
            }

            IMatchRoleID.substCD2 -> {
                map.insert("substCD2ID", spielerID)
                map.insert("substCD2Name", name)
            }

            IMatchRoleID.substWB1 -> {
                map.insert("substWB1ID", spielerID)
                map.insert("substWB1Name", name)
            }

            IMatchRoleID.substWB2 -> {
                map.insert("substWB2ID", spielerID)
                map.insert("substWB2Name", name)
            }

            IMatchRoleID.substIM1 -> {
                map.insert("substIM1ID", spielerID)
                map.insert("substIM1Name", name)
            }

            IMatchRoleID.substIM2 -> {
                map.insert("substIM2ID", spielerID)
                map.insert("substIM2Name", name)
            }

            IMatchRoleID.substWI1 -> {
                map.insert("substWI1ID", spielerID)
                map.insert("substWI1Name", name)
            }

            IMatchRoleID.substWI2 -> {
                map.insert("substWI2ID", spielerID)
                map.insert("substWI2Name", name)
            }

            IMatchRoleID.substFW1 -> {
                map.insert("substFW1ID", spielerID)
                map.insert("substFW1Name", name)
            }

            IMatchRoleID.substFW2 -> {
                map.insert("substFW2ID", spielerID)
                map.insert("substFW2Name", name)
            }

            IMatchRoleID.substXT1 -> {
                map.insert("substXT1ID", spielerID)
                map.insert("substXT1Name", name)
            }

            IMatchRoleID.substXT2 -> {
                map.insert("substXT2ID", spielerID)
                map.insert("substXT2Name", name)
            }

            IMatchRoleID.setPieces -> {
                map.insert("KickerID", spielerID)
                map.insert("KickerName", name)
            }

            IMatchRoleID.captain -> {
                map.insert("CaptainID", spielerID)
                map.insert("CaptainName", name)
            }
        }

        // Penalty positions
        for (i in IMatchRoleID.penaltyTaker1..IMatchRoleID.penaltyTaker11) {
            if (roleID == i) {
                map.insert("PenaltyTaker" + (i - IMatchRoleID.penaltyTaker1), spielerID)
            }
        }
    }

    private fun addAdditionalPlayer(
        map: SafeInsertMap, roleID: Int, spielerID: String,
        name: String, behavior: String
    ) {
        var key = "Additional1"
        if (!map.containsKey(key + "ID")) {
            map.insert(key + "ID", spielerID)
            map.insert(key + "Role", roleID.toString())
            map.insert(key + "Name", name)
            map.insert(key + "Behaviour", behavior)
            return
        }
        key = "Additional2"
        if (!map.containsKey(key + "ID")) {
            map.insert(key + "ID", spielerID)
            map.insert(key + "Role", roleID.toString())
            map.insert(key + "Name", name)
            map.insert(key + "Behaviour", behavior)
            return
        }
        key = "Additional3"
        if (!map.containsKey(key + "ID")) {
            map.insert(key + "ID", spielerID)
            map.insert(key + "Role", roleID.toString())
            map.insert(key + "Name", name)
            map.insert(key + "Behaviour", behavior)
            return
        }
        key = "Additional4"
        if (!map.containsKey(key + "ID")) {
            map.insert(key + "ID", spielerID)
            map.insert(key + "Role", roleID.toString())
            map.insert(key + "Name", name)
            map.insert(key + "Behaviour", behavior)
        }
        // max. 4 additional/repositioned players in the new lineup?
    }

    private fun addPlayerOrder(ele: Element?, map: SafeInsertMap, num: Int) {
        val playerOrderID = "" + num
        var playerIn = "-1"
        var playerOut = "-1"
        var orderType = "-1"
        val minute = "-1"
        var matchMinuteCriteria = "-1"
        var pos = "-1"
        var behaviour = "-1"
        var card = "-1"
        var standing = "-1"
        var tmp: Element? = ele?.getElementsByTagName("MatchMinuteCriteria")?.item(0) as Element?
        if (tmp != null) {
            matchMinuteCriteria = getFirstChildNodeValue(tmp)
        }
        tmp = ele?.getElementsByTagName("GoalDiffCriteria")?.item(0) as Element?
        if (tmp != null) {
            standing = getFirstChildNodeValue(tmp)
        }
        tmp = ele?.getElementsByTagName("RedCardCriteria")?.item(0) as Element?
        if (tmp != null) {
            card = getFirstChildNodeValue(tmp)
        }
        tmp = ele?.getElementsByTagName("SubjectPlayerID")?.item(0) as Element?
        if (tmp != null) {
            playerOut = getFirstChildNodeValue(tmp)
        }
        tmp = ele?.getElementsByTagName("ObjectPlayerID")?.item(0) as Element?
        if (tmp != null) {
            playerIn = getFirstChildNodeValue(tmp)
        }
        tmp = ele?.getElementsByTagName("OrderType")?.item(0) as Element?
        if (tmp != null) {
            orderType = getFirstChildNodeValue(tmp)
        }
        tmp = ele?.getElementsByTagName("NewPositionId")?.item(0) as Element?
        if (tmp != null) {
            pos = getFirstChildNodeValue(tmp)
        }
        tmp = ele?.getElementsByTagName("NewPositionBehaviour")?.item(0) as Element?
        if (tmp != null) {
            behaviour = getFirstChildNodeValue(tmp)
        }
        map.insert("subst" + num + "playerOrderID", playerOrderID)
        map.insert("subst" + num + "playerIn", playerIn)
        map.insert("subst" + num + "playerOut", playerOut)
        map.insert("subst" + num + "orderType", orderType)
        map.insert("subst" + num + "minute", minute)
        map.insert("subst" + num + "matchMinuteCriteria", matchMinuteCriteria)
        map.insert("subst" + num + "pos", pos)
        map.insert("subst" + num + "behaviour", behaviour)
        map.insert("subst" + num + "card", card)
        map.insert("subst" + num + "standing", standing)
    }

    /**
     * Creates the Matchlineup object
     * parsing of xml adapted to version 3.0 of match orders
     */
    private fun parseDetails(doc: Document?): Map<String, String> {
        val hash = SafeInsertMap()
        if (doc == null) {
            return hash
        }

        var ele: Element?

        var list: NodeList?
        var root:Element? = doc.documentElement
        try {
            ele = root?.getElementsByTagName("FetchedDate")?.item(0) as Element?
            hash.insert("FetchedDate", getFirstChildNodeValue(ele))
            ele = root?.getElementsByTagName("MatchID")?.item(0) as Element?
            hash.insert("MatchID", getFirstChildNodeValue(ele))

            // change root to Match data
            root = root?.getElementsByTagName("MatchData")?.item(0) as Element?
            if (!getAttributeValue(root, "Available").trim { it <= ' ' }.equals("true", ignoreCase = true)) {
                return hash
            }
            ele = root?.getElementsByTagName("Attitude")?.item(0) as Element?
            if (getAttributeValue(ele, "Available").trim { it <= ' ' }.equals("true", ignoreCase = true)) {
                hash.insert("Attitude", getFirstChildNodeValue(ele))
            } else {
                // in case attitude is not available TODO: check if this is really mandatory
                hash.insert("Attitude", "0")
            }
            ele = root?.getElementsByTagName("CoachModifier")?.item(0) as Element?

            // 'coachModifier' is called 'styleOfPlay' in HT
            if (getAttributeValue(ele, "Available").trim { it <= ' ' }.equals("true", ignoreCase = true)) {
                hash.insert("StyleOfPlay", getFirstChildNodeValue(ele))
            } else {
                hash.insert("StyleOfPlay", "0")
            }
            ele = root?.getElementsByTagName("HomeTeamID")?.item(0) as Element?
            hash.insert("HomeTeamID", getFirstChildNodeValue(ele))
            ele = root?.getElementsByTagName("HomeTeamName")?.item(0) as Element?
            hash.insert("HomeTeamName", getFirstChildNodeValue(ele))
            ele = root?.getElementsByTagName("AwayTeamID")?.item(0) as Element?
            hash.insert("AwayTeamID", getFirstChildNodeValue(ele))
            ele = root?.getElementsByTagName("AwayTeamName")?.item(0) as Element?
            hash.insert("AwayTeamName", getFirstChildNodeValue(ele))
            ele = root?.getElementsByTagName("MatchDate")?.item(0) as Element?
            hash.insert("MatchDate", getFirstChildNodeValue(ele))
            ele = root?.getElementsByTagName("MatchType")?.item(0) as Element?
            hash.insert("MatchType", getFirstChildNodeValue(ele))
            ele = root?.getElementsByTagName("ArenaID")?.item(0) as Element?
            hash.insert("ArenaID", getFirstChildNodeValue(ele))
            ele = root?.getElementsByTagName("ArenaName")?.item(0) as Element?
            hash.insert("ArenaName", getFirstChildNodeValue(ele))
            ele = root?.getElementsByTagName("TacticType")?.item(0) as Element?
            hash.insert("TacticType", getFirstChildNodeValue(ele))

            // Root wechseln
//			Node Lineup = doc.getElementsByTagName("Lineup").item(0);

            // Treatment of Players in starting Lineup
            val positions = doc.getElementsByTagName("Positions")?.item(0) as Element?
            list = positions?.getElementsByTagName("Player")
            if (list != null) {
                for (i in 0 until list.length) {
                    addPlayer(list.item(i) as Element, hash)
                }
            }

            // Treatment of Players on the bench
            val bench = doc.getElementsByTagName("Bench").item(0) as Element?
            list = bench?.getElementsByTagName("Player")
            if (list != null) {
                for (i in 0 until list.length) {
                    addPlayer(list.item(i) as Element, hash)
                }
            }

            // Treatment of Penalty Takers
            val kickers = doc.getElementsByTagName("Kickers").item(0) as Element?
            list = kickers?.getElementsByTagName("Player")
            if (list != null) {
                for (i in 0 until list.length) {
                    addPlayer(list.item(i) as Element, hash)
                }
            }

            // Treatment of SP taker and Captain
            val setPieces = doc.getElementsByTagName("SetPieces").item(0) as Element?
            if (setPieces != null) {
                addPlayer(setPieces, hash)
            }

            val captain = doc.getElementsByTagName("Captain").item(0) as Element?
            if (captain != null) {
                addPlayer(captain, hash)
            }

            // Treatment of Players Orders
            val child = root?.getElementsByTagName("PlayerOrders")?.item(0) as Element?
            list = child?.getElementsByTagName("PlayerOrder")
            if (list != null) {
                for (i in 0 until list.length) {
                    addPlayerOrder(list.item(i) as Element, hash, i)
                }
            }
        } catch (e: Exception) {
            HOLogger.instance().log(
                XMLMatchOrderParser::class.java,
                "XMLMatchOrderParser.parseDetails Exception gefangen: $e"
            )
            HOLogger.instance().log(XMLMatchOrderParser::class.java, e)
        }
        return hash
    }
}
