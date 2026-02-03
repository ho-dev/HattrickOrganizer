package module.playeroverview

import core.db.DBManager
import core.gui.comp.table.HOPlayersTableModel
import core.gui.comp.table.PlayerCheckBoxColumn
import core.gui.comp.table.UserColumn
import core.gui.model.*
import core.model.player.Player
import core.util.HODateTime

/**
 * Model used to display players in the Squad table.
 *
 * @author Thorsten Dietz
 * @since 1.36
 */
class PlayerOverviewTableModel(id: UserColumnController.ColumnModelId, name: String) : HOPlayersTableModel(id, name) {

    /**
     * constructor
     *
     */
    internal constructor(id: UserColumnController.ColumnModelId) : this(id, "Spieleruebersicht")

    init {
        val basic: Array<out PlayerColumn>? = UserColumnFactory.createPlayerBasicArray() // 2
        val skills: Array<out PlayerSkillColumn>? = UserColumnFactory.createPlayerSkillArray() // 12
        val positions: Array<out PlayerPositionColumn>? = UserColumnFactory.createPlayerPositionArray() // 19
        val goals: Array<out PlayerColumn>? = UserColumnFactory.createGoalsColumnsArray() // 7
        val additionalArray: Array<out PlayerColumn>? = UserColumnFactory.createPlayerAdditionalArray() // 32
        val size = basic!!.size + skills!!.size + positions!!.size + goals!!.size + additionalArray!!.size + 1
        val columns: Array<UserColumn?> = arrayOfNulls(size) // 72

        columns[0] = basic[0]
        columns[1] = additionalArray[0]
        columns[2] = additionalArray[1]
        columns[3] = additionalArray[12] // Mother club
        columns[4] = additionalArray[2]
        columns[5] = additionalArray[4]
        columns[6] = additionalArray[5]
        columns[7] = additionalArray[6]
        columns[8] = additionalArray[8] // tsi

        val skillIndex = 9 // - 20
        System.arraycopy(skills, 0, columns, skillIndex, skills.size)

        columns[21] = additionalArray[3] // best position
        columns[22] = additionalArray[9] // lastmatch

        val positionIndex = 23 // - 41
        System.arraycopy(positions, 0, columns, positionIndex, positions.size)

        val goalsIndex = 42 // -46 (the last 2 goal columns are appended at the end)
        System.arraycopy(goals, 0, columns, goalsIndex, goals.size - 2)

        var index = 47
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
        columns[index++] = additionalArray[31]
        columns[index++] = additionalArray[25]
        columns[index++] = additionalArray[26]
        columns[index++] = additionalArray[27]
        columns[index++] = additionalArray[28]
        columns[index++] = additionalArray[29]
        columns[index++] = additionalArray[30]
        columns[index++] = goals[5]
        columns[index++] = goals[6]

        this.columns = columns.filterNotNull().toTypedArray()
        assert(this.columns.size == columns.size)
    }

    val selectedPlayer: Player?
        get() {
            val rowIndex = table!!.selectedRow
            if (rowIndex >= 0 && rowIndex < this.rowCount) {
                return players[table!!.convertRowIndexToModel(rowIndex)]
            }
            return null
        }

    /**
     * Resets the data.
     */
    fun reInitData() {
        initData()
    }

    /**
     * Returns the [Player] with the same ID as the instance passed, or `null`.
     */
    private fun getPreviousPlayerDevelopmentStage(currentDevelopmentStage: Player): Player? {
        val id = currentDevelopmentStage.playerId
        if (id >= 0) {
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
        m_clData = Array(players.size) { arrayOfNulls(tmpDisplayedColumns.size) }

        for (i in players.indices) {
            val currentPlayer = players[i]
            val comparisonPlayer = getPreviousPlayerDevelopmentStage(currentPlayer)
            for (j in tmpDisplayedColumns.indices) {
                if (tmpDisplayedColumns[j] is PlayerCheckBoxColumn) {
                    m_clData!![i][j] = (tmpDisplayedColumns[j] as PlayerCheckBoxColumn).getTableEntry(currentPlayer)
                } else if (tmpDisplayedColumns[j] is PlayerColumn) {
                    m_clData!![i][j] =
                        (tmpDisplayedColumns[j] as PlayerColumn).getTableEntry(currentPlayer, comparisonPlayer)
                }
            }
        }
        fireTableDataChanged()
    }
}
