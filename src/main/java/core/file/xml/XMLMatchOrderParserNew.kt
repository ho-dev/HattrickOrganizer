package core.file.xml

import core.model.player.MatchRoleID
import module.lineup.substitution.model.GoalDiffCriteria
import module.lineup.substitution.model.RedCardCriteria
import module.lineup.substitution.model.Substitution
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

class XMLMatchOrderParserNew(private val document: Document) {
    private val positions: MutableMap<Int, MatchRoleID>
    private val orders: MutableList<Substitution>

    init {
        positions = HashMap()
        orders = ArrayList()
        parse()
    }

    private fun parse() {
        val root:Element? = document.documentElement
        val lineupElement = root?.getElementsByTagName("Lineup")?.item(0) as Element?
        val players = lineupElement?.getElementsByTagName("Player")
        if (players != null) {
            for (i in 0 until players.length) {
                val position = getPosition(players.item(i))
                positions[position.id] = position
            }
        }
        val nl = root?.getElementsByTagName("PlayerOrders")
        if (nl != null && nl.length > 0) {
            val orders = (nl.item(0) as Element).getElementsByTagName("PlayerOrder")
            for (i in 0 until orders.length) {
                this.orders.add(getOrder(orders.item(i)))
            }
        }
    }

    private fun getOrder(orderNode: Node): Substitution {
        return Substitution(
            getChildValue(orderNode, "PlayerOrderID")?.toInt() ?: -1,
            getChildValue(orderNode, "ObjectPlayerID")?.toInt() ?: -1,
            getChildValue(orderNode, "SubjectPlayerID")?.toInt() ?: -1,
            getChildValue(orderNode, "OrderType")?.toByte() ?: -1,
            getChildValue(orderNode, "MatchMinuteCriteria")?.toByte()?.toInt() ?: -1,
            getChildValue(orderNode, "NewPositionId")?.toByte() ?: -1,
            getChildValue(orderNode, "NewPositionBehaviour")?.toByte() ?: -1,
            RedCardCriteria.getById(getChildValue(orderNode, "RedCardCriteria")?.toByte() ?: -1),
            GoalDiffCriteria.getById(getChildValue(orderNode, "GoalDiffCriteria")?.toByte() ?: -1)
        )
    }

    private fun getPosition(playerNode: Node): MatchRoleID {
        val playerId = getChildValue(playerNode, "PlayerID")?.toInt() ?: -1
        val roleId = getChildValue(playerNode, "RoleID")?.toInt() ?: -1
        var behaviourId: Byte = 0

        // Behaviour is optional
        val behaviour = getChildValue(playerNode, "Behaviour")
        if (behaviour != null) {
            behaviourId = behaviour.toByte()
        }

        return MatchRoleID(roleId, playerId, behaviourId)
    }

    private fun getChildValue(parent: Node, childTagName: String): String? {
        var value: String? = null
        val node = getChild(parent, childTagName)
        if (node != null) {
            val valNode = node.firstChild
            if (valNode != null) {
                value = valNode.nodeValue
            }
        }
        return value
    }

    private fun getChild(node: Node, tagName: String): Node? {
        var child: Node? = null
        val nl = (node as Element?)?.getElementsByTagName(tagName)
        if (nl != null && nl.length > 0) {
            child = nl.item(0)
        }
        return child
    }
}
