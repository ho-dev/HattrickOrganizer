package core.gui.comp.table

import core.gui.model.UserColumnController.ColumnModelId
import core.model.HOVerwaltung
import core.model.player.Player

abstract class HOPlayersTableModel(id: ColumnModelId, name: String) : HOTableModel(id, name) {
    var players: List<Player> = HOVerwaltung.instance().getModel().getCurrentPlayers()
}
