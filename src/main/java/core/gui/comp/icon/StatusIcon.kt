package core.gui.comp.icon

import core.gui.theme.HOColorName
import core.gui.theme.HOIconName
import core.gui.theme.ImageUtilities
import core.gui.theme.ThemeManager
import core.model.player.Player
import java.awt.Component
import java.awt.Graphics
import javax.swing.Icon

class StatusIcon @JvmOverloads constructor(player: Player, isLarge: Boolean = false) : Icon {
    private var ICON_SIZE = 12
    private var ICON_SPACE = 2
    private var TRANSFERLISTED_ICON =
        ImageUtilities.getSvgIcon(HOIconName.TRANSFERLISTED_TINY, TRANSFERLISTED_ICON_COLOR_MAP, 14, 14)
    private var SUSPENDED_ICON = ImageUtilities.getSvgIcon(HOIconName.SUSPENDED_TINY, ICON_SIZE, ICON_SIZE)
    private var TWO_YELLOW_ICON = ImageUtilities.getSvgIcon(HOIconName.TWOYELLOW_TINY, ICON_SIZE, ICON_SIZE)
    private var ONE_YELLOW_ICON = ImageUtilities.getSvgIcon(HOIconName.ONEYELLOW_TINY, ICON_SIZE, ICON_SIZE)
    private val icons: MutableList<Icon> = ArrayList()

    init {
        if (isLarge) {
            ICON_SIZE = LARGE_ICON_SIZE
            ICON_SPACE = LARGE_ICON_SPACE
            TRANSFERLISTED_ICON = LARGE_TRANSFERLISTED_ICON
            SUSPENDED_ICON = LARGE_SUSPENDED_ICON
            TWO_YELLOW_ICON = LARGE_TWO_YELLOW_ICON
            ONE_YELLOW_ICON = LARGE_ONE_YELLOW_ICON
        }
        setPlayer(player)
    }

    fun setPlayer(player: Player) {
        if (player.transferListed > 0) {
            icons.add(TRANSFERLISTED_ICON)
        }
        if (player.isRedCarded()) {
            icons.add(SUSPENDED_ICON)
        } else if (player.totalCards == 2) {
            icons.add(TWO_YELLOW_ICON)
        } else if (player.totalCards == 1) {
            icons.add(ONE_YELLOW_ICON)
        }
    }

    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        for (i in icons.indices) {
            val cur = icons[i]
            var offset = x + ICON_SIZE * i
            if (i > 0) {
                offset += ICON_SPACE * i
            }
            cur.paintIcon(c, g, offset, y)
        }
    }

    override fun getIconWidth(): Int {
        return if (icons.size == 0) 0 else ICON_SIZE * icons.size + ICON_SPACE * (icons.size - 1)
    }

    override fun getIconHeight(): Int {
        return ICON_SIZE
    }

    companion object {
        private val TRANSFERLISTED_ICON_COLOR_MAP =
            mapOf<Any, Any>("foregroundColor" to ThemeManager.getColor(HOColorName.PLAYER_SPECIALTY_COLOR))
        private const val LARGE_ICON_SIZE = 16
        private const val LARGE_ICON_SPACE = 3
        private val LARGE_TRANSFERLISTED_ICON =
            ImageUtilities.getSvgIcon(HOIconName.TRANSFERLISTED_TINY, TRANSFERLISTED_ICON_COLOR_MAP, 19, 19)
        private val LARGE_SUSPENDED_ICON =
            ImageUtilities.getSvgIcon(HOIconName.SUSPENDED_TINY, LARGE_ICON_SIZE, LARGE_ICON_SIZE)
        private val LARGE_TWO_YELLOW_ICON =
            ImageUtilities.getSvgIcon(HOIconName.TWOYELLOW_TINY, LARGE_ICON_SIZE, LARGE_ICON_SIZE)
        private val LARGE_ONE_YELLOW_ICON =
            ImageUtilities.getSvgIcon(HOIconName.ONEYELLOW_TINY, LARGE_ICON_SIZE, LARGE_ICON_SIZE)
    }
}
