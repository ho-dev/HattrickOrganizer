package core.model.player

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.file.Path
import javax.imageio.ImageIO

/**
 * Class to hold player avatar information
 * Avatar is represented by overlay of graphic elements available
 * on HT website
 */
class PlayerAvatar(val playerID: Int, imageLayers: List<Layer>) {
    private val layers: List<Layer>

    init {
        layers = imageLayers.map { inputLayer: Layer -> this.fixURL(inputLayer) }
    }

    private fun fixURL(inputLayer: Layer): Layer {
        val x = inputLayer.x
        val y = inputLayer.y
        val url = fixURL(inputLayer.urlElement)
        return Layer(x, y, url)
    }

    private fun fixURL(inputUrl: String): String {
        val prefixURL = "https://www84.hattrick.org"
        return if (inputUrl.startsWith("http")) {
            inputUrl
        } else prefixURL + inputUrl
    }

    @Throws(IOException::class)
    fun generateAvatar(pathAvatar: Path) {
        val firstLayer = layers[0]
        val x0: Int
        val y0: Int
        var url = URL(firstLayer.urlElement)
        var img = ImageIO.read(url.openStream())
        if (img != null) {
            val avatar = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB)
            val g = avatar.graphics
            g.drawImage(img, 0, 0, null)
            x0 = firstLayer.x
            y0 = firstLayer.y

            for (layer in layers.stream().skip(1).toList()) {
                url = URL(layer.urlElement)
                img = ImageIO.read(url.openStream())
                g.drawImage(img, layer.x - x0, layer.y - y0, null)
            }

            // Save as new image
            val pathName = pathAvatar.resolve("$playerID.png").toString()
            ImageIO.write(avatar, "PNG", File(pathName))
        }
    }
}
