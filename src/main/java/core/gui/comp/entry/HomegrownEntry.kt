package core.gui.comp.entry

import core.gui.theme.HOIconName
import core.gui.theme.ThemeManager
import core.model.player.Player
import javax.swing.JComponent
import javax.swing.SwingConstants

class HomegrownEntry : AbstractHOTableEntry() {
    private var icon = ColorLabelEntry(
        "", ColorLabelEntry.FG_STANDARD,
        ColorLabelEntry.BG_STANDARD,
        SwingConstants.CENTER
    )
    private var player: Player? = null

    fun setPlayer(player: Player?) {
        this.player = player
        updateComponent()
    }

    fun getPlayer(): Player? {
        return player
    }

    override fun compareTo(other: IHOTableEntry): Int {
        if (other is HomegrownEntry) {
            val entry = other
            if (entry.getPlayer() != null && getPlayer() != null) {
                if (entry.getPlayer()!!.homeGrown != getPlayer()!!.homeGrown) {
                    return if (getPlayer()!!.homeGrown) {
                        1
                    } else {
                        -1
                    }
                }
            }
        }
        return 0
    }

    override fun updateComponent() {
        if (player != null) {
            if (player!!.homeGrown) {
                icon.setIcon(ThemeManager.getIcon(HOIconName.HOMEGROWN))
            } else {
                icon.clear()
            }
        } else {
            icon.clear()
        }
    }

    override fun getComponent(isSelected: Boolean): JComponent {
        return icon.getComponent(isSelected)
    }

    override fun clear() {
        player = null
        updateComponent()
    }

    override fun createComponent() {
        icon = ColorLabelEntry(
            "", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_STANDARD,
            SwingConstants.CENTER
        )
    }
}
