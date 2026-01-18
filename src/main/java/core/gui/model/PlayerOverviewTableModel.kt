package core.gui.model

import core.db.DBManager
import core.gui.comp.table.BooleanColumn
import core.gui.comp.table.HOTableModel
import core.gui.comp.table.UserColumn
import core.gui.model.UserColumnController.ColumnModelId
import core.model.player.Player
import core.util.HODateTime
import module.playeroverview.SpielerTrainingsVergleichsPanel

/**
 * Model used to display players in the Squad table.
 *
 * @author Thorsten Dietz
 * @since 1.36
 */
class PlayerOverviewTableModel(id: ColumnModelId, name: String) : HOTableModel(id, name) {
    /** all players  */
    var players: List<Player>? = null
        private set

    /**
     * constructor
     *
     */
    internal constructor(id: ColumnModelId) : this(id, "Spieleruebersicht")

    init {
        val basic: Array<out PlayerColumn>? = UserColumnFactory.createPlayerBasicArray() // 2
        val skills: Array<out PlayerSkillColumn>? = UserColumnFactory.createPlayerSkillArray() // 12
        val positions: Array<out PlayerPositionColumn>? = UserColumnFactory.createPlayerPositionArray() // 19
        val goals: Array<out PlayerColumn>? = UserColumnFactory.createGoalsColumnsArray() // 7
        val additionalArray: Array<out PlayerColumn>? = UserColumnFactory.createPlayerAdditionalArray() // 31
        val size = basic!!.size + skills!!.size + positions!!.size + goals!!.size + additionalArray!!.size + 1
        val columns: Array<UserColumn?> = arrayOfNulls(size) // 72

        columns[0] = basic[0]
        columns[1] = additionalArray[0]
        columns[2] = additionalArray[1]
        columns[3] = additionalArray[12] // Motherclub
        columns[4] = additionalArray[2]
        columns[5] = additionalArray[4]
        columns[6] = additionalArray[5]
        columns[7] = additionalArray[6]
        columns[8] = additionalArray[8] // tsi

        val skillIndex = 9 // - 20
        System.arraycopy(skills, 0, columns, skillIndex, skills.size)

        columns[21] = additionalArray[3] // best position
        columns[22] = additionalArray[9] // lastmatch

        val positionIndex = 23 //- 41
        System.arraycopy(positions, 0, columns, positionIndex, positions.size)

        val goalsIndex = 42 //-48
        System.arraycopy(goals, 0, columns, goalsIndex, goals.size)

        var index = 49
        columns[index++] = additionalArray[11]
        columns[index++] = basic[1]
        columns[index++] = additionalArray[10]
        columns[index++] = additionalArray[16]
        columns[index++] = additionalArray[17]
        columns[index++] = additionalArray[18]
        columns[index++] = additionalArray[13]
        columns[index++] = additionalArray[14]
        columns[index++] = additionalArray[15]
        columns[index++] = additionalArray[19]
        columns[index++] = additionalArray[20]
        columns[index++] = additionalArray[7]
        columns[index++] = additionalArray[21]
        columns[index++] = additionalArray[22]
        columns[index++] = additionalArray[23] // schum-rank
        columns[index++] = additionalArray[24] // schum-rank benchmark
        columns[index++] = BooleanColumn(UserColumnFactory.AUTO_LINEUP, " ", "AutoAufstellung", 28)
        columns[index++] = additionalArray[25]
        columns[index++] = additionalArray[26]
        columns[index++] = additionalArray[27]
        columns[index++] = additionalArray[28]
        columns[index++] = additionalArray[29]
        columns[index++] = additionalArray[30]

        this.columns = columns.filterNotNull().toTypedArray()
        assert(this.columns.size == columns.size)
    }

    // TODO: table column model should control isEditable
    // Refactoring player overview table model should replace the class BooleanColumn
    override fun isCellEditable(row: Int, column: Int): Boolean {
        return getValueAt(row, column) is Boolean
    }

    fun getRowIndexOfPlayer(playerId: Int): Int {
        val modelIndex = getPlayerIndex(playerId)
        if (modelIndex > -1 && modelIndex < this.rowCount) {
            return table!!.convertRowIndexToView(modelIndex)
        }
        return -1
    }

    val selectedPlayer: Player?
        get() {
            val rowIndex = table!!.selectedRow
            if (rowIndex >= 0 && rowIndex < this.rowCount) {
                return players!![table!!.convertRowIndexToModel(rowIndex)]
            }
            return null
        }

    fun selectPlayer(playerId: Int) {
        val row = getRowIndexOfPlayer(playerId)
        if (row > -1 && row < this.rowCount) {
            table!!.setRowSelectionInterval(row, row)
        }
    }

    fun getPlayerAtRow(tableRow: Int): Player? {
        if (players != null && tableRow > -1 && tableRow < this.rowCount) {
            val modelIndex = table!!.convertRowIndexToModel(tableRow)
            if (modelIndex > -1 && modelIndex < this.rowCount) {
                return players!![modelIndex]
            }
        }
        return null
    }

    fun getPlayer(playerId: Int): Player? {
        // Can be negative for temp player
        if (playerId != 0) {
            for (m_vPlayer in players!!) {
                if (m_vPlayer.playerId == playerId) {
                    return m_vPlayer
                }
            }
        }

        return null
    }

    fun getPlayerIndex(playerId: Int): Int {
        var i = 0
        for (m_vPlayer in players!!) {
            if (m_vPlayer.playerId == playerId) {
                return i
            }
            i++
        }
        return -1
    }

    /**
     * Sets the new list of players.
     */
    fun setValues(player: List<Player>?) {
        players = player
        initData()
    }

    /**
     * Resets the data for an HRF comparison.
     */
    fun reInitDataHRFComparison() {
        initData()
    }

    /**
     * Returns the [Player] with the same ID as the instance passed, or `null`.
     */
    private fun getPreviousPlayerDevelopmentStage(currentDevelopmentStage: Player): Player? {
        val id = currentDevelopmentStage.playerId
        if ( id >= 0 ) {
            // not a temporary player
            val selectedPlayerDevelopmentStage = SpielerTrainingsVergleichsPanel.getSelectedPlayerDevelopmentStage()
            var i = 0
            while ((selectedPlayerDevelopmentStage != null) && (i < selectedPlayerDevelopmentStage.size)) {
                val selectedDevelopmentStage = selectedPlayerDevelopmentStage[i]

                if (selectedDevelopmentStage.playerId == id) {
                    return selectedDevelopmentStage
                }
                i++
            }
            if (SpielerTrainingsVergleichsPanel.isDevelopmentStageSelected()) {
                val hrf = SpielerTrainingsVergleichsPanel.getSelectedHrfId()
                return getFirstPlayerDevelopmentStageAfterSelected(currentDevelopmentStage, hrf)
            }
        }
        return null
    }

    /**
     * Returns the [Player] from the first HRF in which he appears.
     */
    private fun getFirstPlayerDevelopmentStageAfterSelected(vorlage: Player, hrfId: Int?): Player {
        var after: HODateTime? = null
        if (hrfId != null) {
            val hrf = DBManager.instance().loadHRF(hrfId)
            if (hrf != null) {
                after = hrf.datum
            }
        }
        return DBManager.instance().loadPlayerFirstHRF(vorlage.playerId, after)
    }

    /**
     * create a data[][] from player-Vector
     */
    override fun initData() {
        val tmpDisplayedColumns = getDisplayedColumns()
        m_clData = Array(players!!.size) { arrayOfNulls(tmpDisplayedColumns.size) }

        for (i in players!!.indices) {
            val currentPlayer = players!![i]
            val comparisonPlayer = getPreviousPlayerDevelopmentStage(currentPlayer)
            for (j in tmpDisplayedColumns.indices) {
                if (tmpDisplayedColumns[j] is PlayerColumn) {
                    m_clData!!.get(i)[j] = (tmpDisplayedColumns[j] as PlayerColumn).getTableEntry(currentPlayer, comparisonPlayer)
                } else if (tmpDisplayedColumns[j] is BooleanColumn) {
                    m_clData!!.get(i)[j] = (tmpDisplayedColumns[j] as BooleanColumn).getValue(currentPlayer)
                }
            }
        }
        fireTableDataChanged()
    }

    /**
     * Initializes the lineup only
     */
    fun reInitData() {
        val tmpDisplayedColumns = getDisplayedColumns()
        for (i in players!!.indices) {
            val currentPlayer = players!![i]
            for (j in tmpDisplayedColumns.indices) {
                if (tmpDisplayedColumns[j].id == UserColumnFactory.NAME ||
                    tmpDisplayedColumns[j].id == UserColumnFactory.LINEUP ||
                    tmpDisplayedColumns[j].id == UserColumnFactory.BEST_POSITION ||
                    tmpDisplayedColumns[j].id == UserColumnFactory.SCHUM_RANK_BENCHMARK ||
                    tmpDisplayedColumns[j].id == UserColumnFactory.GROUP) {
                    m_clData!![i][j] = (tmpDisplayedColumns[j] as PlayerColumn).getTableEntry(currentPlayer, null)
                } else if (tmpDisplayedColumns[j].id == UserColumnFactory.AUTO_LINEUP) {
                    m_clData!![i][j] = (tmpDisplayedColumns[j] as BooleanColumn).getValue(currentPlayer)
                }
            }
        }
    }
}
