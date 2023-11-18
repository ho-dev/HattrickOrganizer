package core.gui.comp.table

import core.model.player.Player

class BooleanColumn(id: Int, name: String?, tooltip: String?, minWidth: Int) : UserColumn(id, name, tooltip) {
    init {
        this.minWidth = minWidth
        preferredWidth = minWidth
    }

    fun getValue(player: Player): Boolean {
        return player.canBeSelectedByAssistant
    }
}
