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
                    setSelectedPlayer(null)
                }
            }
        }
        Companion.addPropertyChangeListener(this)
    }

    private var enableListSelectionListener : Boolean = true

    fun selectPlayer(player: Player?, fireEvent : Boolean = true) {
        val players = getPlayers()
        val modelIndex = players.indexOf(player)
        if (modelIndex >= 0 && modelIndex < players.size) {
            val viewIndex = this.convertRowIndexToView(modelIndex)
            enableListSelectionListener = fireEvent
            this.setRowSelectionInterval(viewIndex, viewIndex)
            enableListSelectionListener = true
        }
    }

    /**
     * Initialize the selection to the player selected by earlier created players tables
     * This function must only be called after the model's initData function was called.
     */
    fun initSelection(){
        selectPlayer(selectedPlayer, false)
    }

    override fun propertyChange(evt: PropertyChangeEvent?) {
        selectPlayer(evt?.newValue as Player?, false)
    }

}
