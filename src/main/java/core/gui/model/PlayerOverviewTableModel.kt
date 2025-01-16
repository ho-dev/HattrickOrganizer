package core.gui.model

import core.db.DBManager
import core.gui.comp.table.BooleanColumn
import core.gui.comp.table.HOTableModel
import core.gui.comp.table.UserColumn
import core.gui.model.UserColumnController.ColumnModelId
import core.model.player.Player
import core.util.HODateTime
import module.playerOverview.SpielerTrainingsVergleichsPanel

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
        val basic: Array<out PlayerColumn>? = UserColumnFactory.createPlayerBasicArray()
        val columns : Array<UserColumn?> = arrayOfNulls(69)
        columns[0] = basic?.get(0)
        columns[48] = basic?.get(1)

        val skills: Array<out PlayerSkillColumn>? = UserColumnFactory.createPlayerSkillArray()
        val skillIndex = 9 // - 20
        if (skills != null) {
            System.arraycopy(skills, 0, columns, skillIndex, skills.size)
        }

        val positions: Array<out PlayerPositionColumn>? = UserColumnFactory.createPlayerPositionArray()
        val positionIndex = 23 //- 41
        if (positions != null) {
            System.arraycopy(positions, 0, columns, positionIndex, positions.size)
        }

        val goals: Array<out PlayerColumn>? = UserColumnFactory.createGoalsColumnsArray()
        val goalsIndex = 42 //-46
        if (goals != null) {
            System.arraycopy(goals, 0, columns, goalsIndex, goals.size)
        }
        val additionalArray: Array<out PlayerColumn>? = UserColumnFactory.createPlayerAdditionalArray()
        columns[1] = additionalArray?.get(0)
        columns[2] = additionalArray?.get(1)
        columns[4] = additionalArray?.get(2)
        columns[21] = additionalArray?.get(3) // best position
        columns[5] = additionalArray?.get(4)
        columns[6] = additionalArray?.get(5)
        columns[7] = additionalArray?.get(6)
        columns[58] = additionalArray?.get(7)
        columns[8] = additionalArray?.get(8) // tsi
        columns[22] = additionalArray?.get(9) // lastmatch
        columns[47] = additionalArray?.get(11)
        columns[3] = additionalArray?.get(12) // Motherclub
        columns[49] = additionalArray?.get(10)
        columns[50] = additionalArray?.get(16)
        columns[51] = additionalArray?.get(17)
        columns[52] = additionalArray?.get(18)
        columns[53] = additionalArray?.get(13)
        columns[54] = additionalArray?.get(14)
        columns[55] = additionalArray?.get(15)
        columns[56] = additionalArray?.get(19)
        columns[57] = additionalArray?.get(20)
        columns[59] = additionalArray?.get(21)
        columns[60] = additionalArray?.get(22)
        columns[61] = additionalArray?.get(23) // schum-rank
        columns[62] = additionalArray?.get(24) // schum-rank benchmark
        columns[63] = BooleanColumn(UserColumnFactory.AUTO_LINEUP, " ", "AutoAufstellung", 28)
        columns[64] = additionalArray?.get(25)
        columns[65] = additionalArray?.get(26)
        columns[66] = additionalArray?.get(27)
        columns[67] = additionalArray?.get(28)
        columns[68] = additionalArray?.get(29)

        this.columns = columns.filterNotNull().toTypedArray()
        assert(this.columns.size == columns.size)
    }

    override fun isCellEditable(row: Int, column: Int): Boolean {
        return getValueAt(row, column) is Boolean
    }

    fun getRowIndexOfPlayer(playerId: Int): Int {
        val modelIndex = getPlayerIndex(playerId)
        if (modelIndex > -1) {
            return table!!.convertRowIndexToView(modelIndex)
        }
        return -1
    }

    val selectedPlayer: Player?
        get() {
            val rowIndex = table!!.selectedRow
            if (rowIndex >= 0) {
                return players!![table!!.convertRowIndexToModel(rowIndex)]
            }
            return null
        }

    fun selectPlayer(playerId: Int) {
        val row = getRowIndexOfPlayer(playerId)
        if (row > -1) {
            table!!.setRowSelectionInterval(row, row)
        }
    }

    fun getPlayerAtRow(tableRow: Int): Player? {
        if (players != null && tableRow > -1) {
            val modelIndex = table!!.convertRowIndexToModel(tableRow)
            if (modelIndex < players!!.size) {
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
