package core.gui.comp.table

import core.gui.model.UserColumnController.ColumnModelId
import core.model.HOVerwaltung
import core.model.player.Player

abstract class HOPlayersTableModel(id: ColumnModelId, name: String) : HOTableModel(id, name) {
    fun refresh() {
        refreshPlayers()
        initData()
    }

    open fun refreshPlayers(){
        players = HOVerwaltung.instance().model.currentPlayers
    }

    open fun getModelIndex(player: Player?) : Int {
        return players.indexOf(player)
    }

    open fun getPlayer(index : Int) : Player?{
        if (index > -1 && index < players.size) {
            return players[index]
        }
        return null
    }

    var players: List<Player> = HOVerwaltung.instance().getModel().currentPlayers

}
