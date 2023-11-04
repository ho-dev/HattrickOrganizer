package core.file.xml

import core.model.player.Layer
import core.model.player.PlayerAvatar
import core.util.HOLogger
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.util.ArrayList
import java.util.regex.Pattern


object XMLAvatarsParser {

    fun parseAvatarsFromString(str: String): List<PlayerAvatar> {
        return parseDetails(XMLManager.parseString(str))
    }

    private fun parseDetails(doc: Document?): List<PlayerAvatar> {

        val playerAvatars = mutableListOf<PlayerAvatar>()

        if (doc == null) {
            return playerAvatars
        }

        try {
            doc.documentElement.normalize()
            val root = doc.documentElement
            val players = root.getElementsByTagName("Player")

            var i = 0
            var layers: List<Layer>
            var player: Element
            var layer: Element
            var node: Node
            var node2: Node
            var nlLayers: NodeList
            var x: Int
            var y: Int
            var playerID: Int
            var urlImage: String
            val pattern = Pattern.compile("t[1-6]\\.png|injury\\.png|injuredbutplaying\\.png")

            while (i < players.length) {
                node = players.item(i)
                if (node.nodeType == Node.ELEMENT_NODE) {
                    player = node as Element
                    playerID = Integer.parseInt(player.getElementsByTagName("PlayerID").item(0).textContent)

                    // Get all layers ====================
                    nlLayers = player.getElementsByTagName("Layer")
                    layers = ArrayList()
                    var j: Int = 0
                    while (j < nlLayers.length) {
                        node2 = nlLayers.item(j)
                        if (node2.nodeType == Node.ELEMENT_NODE) {
                            layer = node2 as Element
                            x = Integer.parseInt(layer.getAttribute("x"))
                            y = Integer.parseInt(layer.getAttribute("y"))
                            urlImage = layer.getElementsByTagName("Image").item(0).textContent

                            if (!pattern.matcher(urlImage).find()) {
                                layers.add(Layer(x, y, urlImage))
                            }
                        }
                        j++
                    }
                    playerAvatars.add(PlayerAvatar(playerID, layers))
                    i++
                }
            }
        } catch (e: Exception) {
            HOLogger.instance().log(XMLAvatarsParser.javaClass, e)
        }

        return playerAvatars
    }
}
