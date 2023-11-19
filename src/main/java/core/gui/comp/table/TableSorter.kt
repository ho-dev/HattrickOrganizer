package core.gui.comp.table

import core.gui.comp.entry.ColorLabelEntry
import core.gui.comp.entry.IHOTableEntry
import core.gui.model.PlayerOverviewModel
import core.gui.model.UserColumnFactory
import core.model.player.Player
import core.util.HOLogger
import module.lineup.LineupTableModel
import module.transfer.scout.ScoutEintrag
import module.transfer.scout.TransferTableModel
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.Serial
import javax.swing.JTable
import javax.swing.event.TableModelEvent
import javax.swing.table.TableModel

/**
 * Decorator for the [TableModel] class that ensures that the table data is sorted.
 *
 *
 * The order of the sorted rows are maintained in the `indexes` instance variable,
 * which is then used to retrieve the *i*th row by using the mapping between the index
 * in `indexes` and the index of the row.  For example, if `indexes` is
 * `{ 4, 2, 0, 3, 1 }`, the first row is at index 4 in the model, the second is at
 * index 2, etc.  Sorting happens on the values returned by
 * [HOTableModel.getValueAt].
 *
 *
 * TableSorter supports a “three-way” sorting approach, based on the number of clicks.
 * If the `thirdColSort` is set (i.e. equal to or greater than 0), the behaviour is
 * as follows when sorting column with index `thirdColSort`:
 *
 *  * First click: reverts order,
 *  * Second click: back to original order,
 *  * Third click: sort by `thirdColSort` column.
 *
 */
class TableSorter : TableMap {
    private val sortingColumns: MutableList<Int>
    private lateinit var indexes: IntArray
    private var ascending: Boolean
    private var currentColumn: Int

    // index of the column representing the ID
    private val idColumn: Int
    private val m_iInitSortColumnIndex: Int
    private var thirdColSort = -1 // index of the column using “special” sorting on third click.
    private var iThirdSort = 0 // click count to detect if third click.
    var isThirdSort = false

    constructor(tablemodel: TableModel, idColumn: Int, initsortcolumnindex: Int) {
        this.idColumn = idColumn
        m_iInitSortColumnIndex = initsortcolumnindex
        sortingColumns = ArrayList()
        ascending = isAscending(tablemodel, initsortcolumnindex)
        currentColumn = -1
        setModel(tablemodel)
    }

    constructor(tablemodel: TableModel, idColumn: Int, initsortcolumnindex: Int, thirdColSort: Int) {
        this.idColumn = idColumn
        m_iInitSortColumnIndex = initsortcolumnindex
        sortingColumns = ArrayList()
        this.thirdColSort = thirdColSort
        ascending = isAscending(tablemodel, initsortcolumnindex)
        currentColumn = -1
        setModel(tablemodel)
    }

    private fun isAscending(tablemodel: TableModel, initsortcolumnindex: Int): Boolean {
        return tablemodel is HOTableModel &&
                tablemodel.getPositionInArray(UserColumnFactory.BEST_POSITION) != initsortcolumnindex
    }

    override fun setModel(tablemodel: TableModel) {
        super.setModel(tablemodel)
        reallocateIndexes()
    }

    /**
     * Maps a match id to the row that contains it.
     *
     * @param matchId ID of the match to find.
     * @return int – Row in the table containing the match's details.
     * Returns -1 if the match `matchid` cannot be found.
     */
    fun getRow4Match(matchId: Int): Int {
        if (matchId > 0) {
            for (i in 0 until rowCount) {
                try {
                    val entry = getValueAt(i, idColumn) as ColorLabelEntry
                    if (matchId == entry.number.toInt()) {
                        return indexes[i]
                    }
                } catch (e: Exception) {
                    HOLogger.instance().log(javaClass, "TableSorter.getRow4Match: $e")
                }
            }
        }
        return -1
    }

    /**
     * Maps a player id to the row that contains his entry.
     *
     * @param playerId ID of the player to find.
     * @return int – Row in the table containing the player's details.
     * Returns -1 if the player with id `playerId` cannot be found.
     */
    fun getRow4Player(playerId: Int): Int {
        // Can be negative if the player is a temporary player (for ex. in transfer scout).
        if (playerId != 0) {
            for (i in 0 until rowCount) {
                try {
                    val entry = getValueAt(i, idColumn) as ColorLabelEntry
                    if (playerId == entry.text.toInt()) {
                        return i
                    }
                } catch (e: Exception) {
                    HOLogger.instance().log(javaClass, "TableSorter.getRow4Player: $e")
                }
            }
        }
        return -1
    }

    fun getScoutEintrag(row: Int): ScoutEintrag? {
        if (row > -1) {
            return try {
                val entry = getValueAt(row, idColumn) as ColorLabelEntry
                (model as TransferTableModel).getScoutEintrag(entry.text.toInt())
            } catch (e: Exception) {
                HOLogger.instance().log(javaClass, "TableSorter.getScoutEintrag: $e")
                null
            }
        }
        return null
    }

    fun getSpieler(row: Int): Player? {
        if (row > -1) {
            try {
                val entry = getValueAt(row, idColumn) as ColorLabelEntry?
                val text = entry?.text
                if (!text.isNullOrEmpty()) {
                    val id = text.toInt()
                    return when (model) {
                        is PlayerOverviewModel -> {
                            (model as PlayerOverviewModel).getPlayer(id)
                        }

                        is LineupTableModel -> {
                            (model as LineupTableModel).getPlayer(id)
                        }

                        else -> {
                            throw Exception("Tablemodel unbekannt!")
                        }
                    }
                }
            } catch (e: Exception) {
                HOLogger.instance().log(javaClass, e)
                return null
            }
        }
        return null
    }

    override fun setValueAt(obj: Any, i: Int, j: Int) {
        model!!.setValueAt(obj, indexes[i], j)
    }

    override fun getValueAt(i: Int, j: Int): Any? {
        if (indexes.size <= i || i < 0) {
            return null
        }
        if (rowCount <= indexes[i] || columnCount <= j) {
            return null
        }
        return if (j < 0) {
            null
        } else model?.getValueAt(indexes[i], j)
    }

    /**
     * Registers mouse listener on `jtable` to handle sorting.
     *
     * @param jtable Table to add listener to.
     */
    fun addMouseListenerToHeaderInTable(jtable: JTable) {
        val jtableheader = jtable.tableHeader

        // Listener already present.
        if (jtableheader.componentListeners.size > 0) {
            return
        }
        jtable.setColumnSelectionAllowed(false)
        val mouseadapter: MouseAdapter = object : MouseAdapter() {
            override fun mouseClicked(mouseevent: MouseEvent) {
                val tablecolumnmodel = jtable.columnModel
                val i = tablecolumnmodel.getColumnIndexAtX(mouseevent.x)
                if (i > -1) {
                    val j = jtable.convertColumnIndexToModel(i)
                    if (mouseevent.clickCount == 1 && j != -1) {
                        var flag = ascending

                        // If column sorted is different to the current one, sort by natural order.
                        if (currentColumn != j) {
                            flag = isAscending(model!!, j)
                        }
                        if (thirdColSort == i) {
                            isThirdSort = false
                            iThirdSort++
                            if (iThirdSort == 3) {
                                isThirdSort = true
                                flag = true
                                iThirdSort = 0
                            }
                        }
                        if (currentColumn == j) {
                            flag = !flag
                        }
                        sortByColumn(j, flag)
                    }
                }
            }
        }
        jtableheader.addMouseListener(mouseadapter)
    }

    fun compare(i: Int, j: Int): Int {
        for (integer in sortingColumns) {
            val l = compareRowsByColumn(i, j, integer)
            if (l != 0) {
                return if (ascending) l else -l
            }
        }
        return 0
    }

    private fun compareRowsByColumn(i: Int, j: Int, k: Int): Int {
        val obj = model!!.getValueAt(i, k)
        val obj1 = model!!.getValueAt(j, k)
        if (obj == null && obj1 == null) {
            return 0
        }
        if (obj == null) {
            return -1
        }
        if (obj1 == null) {
            return 1
        }
        if (obj is IHOTableEntry
            && obj1 is IHOTableEntry
        ) {
            val colorLabelentry1 = model!!.getValueAt(i, k) as IHOTableEntry
            val colorLabelentry2 = model!!.getValueAt(j, k) as IHOTableEntry
            return if (isThirdSort) colorLabelentry1.compareToThird(colorLabelentry2) else colorLabelentry1.compareTo(
                colorLabelentry2
            )
        }
        val obj2 = model!!.getValueAt(i, k)
        val s2 = obj2.toString()
        val obj3 = model!!.getValueAt(j, k)
        val s3 = obj3.toString()
        val i2 = s2.compareTo(s3)
        if (i2 < 0) {
            return -1
        }
        return if (i2 <= 0) 0 else 1
    }

    fun initsort() {
        // Sort, including when m_iInitSortColumnIndex is first column
        if (m_iInitSortColumnIndex >= 0) {
            val flag = ascending
            sortByColumn(m_iInitSortColumnIndex, flag)
        }
    }

    fun reallocateIndexes() {
        val i = model!!.rowCount
        indexes = IntArray(i)
        for (j in 0 until i) {
            indexes[j] = j
        }
    }

    /**
     * Sorts the data from the model based on the values stored in `sortingColumns`
     * (using [.compare]), and stores the resulting order of rows in `indexes`.
     */
    fun shuttlesort(ai: IntArray, ai1: IntArray, i: Int, j: Int) {
        if (j - i < 2) {
            return
        }
        val k = (i + j) / 2
        shuttlesort(ai1, ai, i, k)
        shuttlesort(ai1, ai, k, j)
        var l = i
        var i1 = k
        if (j - i >= 4 && compare(ai[k - 1], ai[k]) <= 0) {
            if (j - i >= 0) System.arraycopy(ai, i, ai1, i, j - i)
            return
        }
        for (k1 in i until j) {
            if (i1 >= j || l < k && compare(ai[l], ai[i1]) <= 0) {
                ai1[k1] = ai[l++]
            } else {
                ai1[k1] = ai[i1++]
            }
        }
    }

    private fun sortByColumn(i: Int, flag: Boolean) {
        ascending = flag
        currentColumn = i
        sortingColumns.clear()
        sortingColumns.add(i)
        shuttlesort(indexes.clone(), indexes, 0, indexes.size)
        super.tableChanged(TableModelEvent(this))
    }

    override fun tableChanged(tablemodelevent: TableModelEvent) {
        reallocateIndexes()
        super.tableChanged(tablemodelevent)
    }
}
