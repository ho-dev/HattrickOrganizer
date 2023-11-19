package core.gui

import core.HO
import core.util.HOLogger

import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.MediaTracker
import java.awt.RenderingHints
import java.net.URL

import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.plaf.FontUIResource


/**
 * Shows an info frame centered in the middle of the screen.
 *
 * @author Volker Fischer
 * @version 0.2a 28.08.01
 */
class SplashFrame: JFrame() {

	private lateinit var background: Image
	private var infoText: String = ""
	private var versionText: String = HO.getVersionString()
	private var step: Int = 0
	private var maxStep: Int = 9

	private val fontText: FontUIResource = FontUIResource("SansSerif", Font.PLAIN, 12)
	private val fontVersion: FontUIResource = FontUIResource("SansSerif", Font.BOLD, 16)

	private var progressColor: Color = Color(255,255,255)

    /**
     * Creates a new InterruptionWindow object.
     */
	init {
		val tracker = MediaTracker(this)

		try {
			val resource:URL? = if (HO.development) {
				javaClass.getClassLoader().getResource("gui/bilder/splashscreen_dev.png")
			} else if (HO.beta) {
				javaClass.getClassLoader().getResource("gui/bilder/splashscreen_beta.png")
			} else {
				javaClass.getClassLoader().getResource("gui/bilder/splashscreen_stable.png")
			}

			background = ImageIO.read(resource)
			tracker.addImage(background, 1)

			try {
				tracker.waitForAll()
			} catch (_: InterruptedException) {
			}

			setSize(background.getWidth(null), background.getHeight(null))
			setLocation((toolkit.screenSize.width / 2) - (size.width / 2), //
					(toolkit.screenSize.height / 2) - (size.height / 2))

			isUndecorated = true
			isVisible = true
		} catch (e: Exception) {
			HOLogger.instance().log(javaClass, "InterruptionWindow.<init> : $e")
			HOLogger.instance().log(javaClass, e)
		}
    }

    /**
     * Set text info (e.g. progress).
     */
	fun setInfoText(step: Int, text: String) {
        infoText = text
        this.step = if (step > maxStep) maxStep else step

        repaint()
    }

    /**
     * Manually implemented paint() method.
     */
	override fun paint(g: Graphics) {
        val g2d = g as Graphics2D
        //enable antialiasing
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        //draw background image
        g2d.drawImage(background, 0, 0, null)

		g2d.color = progressColor
        g2d.fillRect(110, 200, (step * ((size.width - 70) / (maxStep)))
				.coerceAtMost(size.width - 70), 5)

        //infotext / progress
		g2d.color = Color.WHITE
		g2d.font = fontText
        g2d.drawString(infoText, 110,187)
		g2d.font = fontVersion
        g2d.drawString(versionText, 15, 198)
    }

}
