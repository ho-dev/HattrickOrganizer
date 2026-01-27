package core.gui.comp.table

import core.model.player.Player
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import javax.swing.event.ListSelectionEvent

class PlayersTable @JvmOverloads constructor(tableModel: HOPlayersTableModel, fixedColumnsCount: Int = 1) :
    FixedColumnsTable(tableModel, fixedColumnsCount), PropertyChangeListener {

        companion object {

            private val propertyChangeSupport : PropertyChangeSupport = PropertyChangeSupport(this)

            fun addPropertyChangeListener(listener: PropertyChangeListener?) {
                propertyChangeSupport.addPropertyChangeListener(listener)
            }

            fun removePropertyChangeListener(listener: PropertyChangeListener?) {
                propertyChangeSupport.removePropertyChangeListener(listener)
            }

            fun firePropertyChanged(oldSelection: Player?, newSelection: Player?){
                propertyChangeSupport.firePropertyChange("SelectedPlayer", oldSelection, newSelection)
            }

            private var selectedPlayer: Player? = null

            fun setSelectedPlayer(player: Player?){
                if ( player != selectedPlayer){
                    val oldSelection = selectedPlayer
                    selectedPlayer = player
                    firePropertyChanged(oldSelection, player)
                }
            }

            fun getSelectedPlayer() : Player?{
                return selectedPlayer
            }
        }

    private fun getPlayers() : List<Player>{
        val hoPlayersTableModel = this.model as HOPlayersTableModel
        return hoPlayersTableModel.players
    }

    init {
        this.addListSelectionListener { _: ListSelectionEvent? ->
            if (enableListSelectionListener) {
                val selectedRow = this.getSelectedRow()
                if (selectedRow != -1) {
                    val modelIndex = this.convertRowIndexToModel(selectedRow)
                    val players = getPlayers()
                    if (modelIndex >= 0 && modelIndex < players.size) {
                        setSelectedPlayer(players.get(modelIndex))
                    }
                } else if (selectedPlayer != null) {
                    setSelectedPlayer(selectedPlayer)
                }
            }
        }
        Companion.addPropertyChangeListener(this)
    }

    fun getSelectedPlayers() : List<Player>{
        val players = mutableListOf<Player>()
        val allPLayers = getPlayers()
        for (viewRow in this.selectedRows){
            players.add(allPLayers.get(this.convertRowIndexToModel(viewRow)))
        }
        return players
    }

    private var enableListSelectionListener : Boolean = true

    fun selectPlayer(player: Player?, fireEvent : Boolean = true) {
        val tableModel = this.getModel() as HOPlayersTableModel
        val modelIndex = tableModel.getModelIndex(player)
        if (modelIndex >= 0) {
            val viewIndex = this.convertRowIndexToView(modelIndex)
            if (!this.isRowSelected(viewIndex)) {
                enableListSelectionListener = fireEvent
                this.setRowSelectionInterval(viewIndex, viewIndex)
                enableListSelectionListener = true
            }
        }
    }
    override fun propertyChange(evt: PropertyChangeEvent?) {
        selectPlayer(evt?.newValue as Player?, false)
    }

    fun refresh() {
        val tableModel = this.getModel() as HOPlayersTableModel
        val selected = selectedPlayer
        tableModel.refresh()
        this.selectPlayer(selected, false)
    }

}
