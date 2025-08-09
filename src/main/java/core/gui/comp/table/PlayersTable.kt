package core.gui.comp.table

import core.model.HOVerwaltung
import core.model.player.Player
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener


class PlayersTable @JvmOverloads constructor(tableModel: HOTableModel, fixedColumnsCount: Int = 1) :
    FixedColumnsTable(tableModel, fixedColumnsCount) {

        companion object {

            var registeredPlayersTables : List<PlayersTable> = mutableListOf<PlayersTable>()

            private val propertyChangeSupport : PropertyChangeSupport = PropertyChangeSupport(this)

            fun addPropertyChangeListener(listener: PropertyChangeListener?) {
                propertyChangeSupport.addPropertyChangeListener(listener)
            }

            fun removePropertyChangeListener(listener: PropertyChangeListener?) {
                propertyChangeSupport.removePropertyChangeListener(listener)
            }

            fun firePropertyChanged(oldSelection: Player?, newSelection: Player?){
                propertyChangeSupport.firePropertyChange("SelectedPlayer", oldSelection, newSelection);
            }

        }

    var players: MutableList<Player?> = HOVerwaltung.instance().getModel().getCurrentPlayers()

    var selectedPlayer: Player? = null
        private set

    init {
        this.addListSelectionListener(ListSelectionListener { e: ListSelectionEvent? ->
            if (!e!!.getValueIsAdjusting() && enableListSelectionListener) {
                val selectedRow = this.getSelectedRow()
                if (selectedRow != -1) {
                    val modelIndex = this.convertRowIndexToModel(selectedRow)
                    if (modelIndex >= 0 && modelIndex < players.size) {
                        var oldSelection = selectedPlayer;
                        selectedPlayer = players.get(modelIndex)
                        for ( table in registeredPlayersTables){
                            table.selectPlayer(selectedPlayer, false)
                        }
                        firePropertyChanged(oldSelection, selectedPlayer)
                    }
                }
            }
        })
    }

    private var enableListSelectionListener : Boolean = true

    fun selectPlayer(player: Player?, fireEvent : Boolean = true) {
        val modelIndex = players.indexOf(player)
        if (modelIndex >= 0 && modelIndex < players.size) {
            val viewIndex = this.convertRowIndexToView(modelIndex)
            enableListSelectionListener = fireEvent
            this.setRowSelectionInterval(viewIndex, viewIndex)
            enableListSelectionListener = true
        }
    }
}
