package module.playerOverview

import core.db.DBManager
import core.gui.comp.table.PlayerCheckBoxColumn
import core.gui.comp.table.HOPlayersTableModel
import core.gui.comp.table.UserColumn
import core.gui.model.PlayerColumn
import core.gui.model.PlayerPositionColumn
import core.gui.model.PlayerSkillColumn
import core.gui.model.UserColumnController
import core.gui.model.UserColumnFactory
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
        val basic: Array<out PlayerColumn>? = UserColumnFactory.createPlayerBasicArray()
        val columns: Array<UserColumn?> = arrayOfNulls(70)
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
        columns[63] = additionalArray?.get(31)
        columns[64] = additionalArray?.get(25)
        columns[65] = additionalArray?.get(26)
        columns[66] = additionalArray?.get(27)
        columns[67] = additionalArray?.get(28)
        columns[68] = additionalArray?.get(29)
        columns[69] = additionalArray?.get(30)

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
                    m_clData!!.get(i)[j] = (tmpDisplayedColumns[j] as PlayerCheckBoxColumn).getTableEntry(currentPlayer)
                } else if (tmpDisplayedColumns[j] is PlayerColumn) {
                    m_clData!!.get(i)[j] =
                        (tmpDisplayedColumns[j] as PlayerColumn).getTableEntry(currentPlayer, comparisonPlayer)
                }
            }
        }
        fireTableDataChanged()
    }
}