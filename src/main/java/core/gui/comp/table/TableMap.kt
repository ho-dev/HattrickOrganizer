package core.gui.comp.table

import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableModel

/**
 * TODO The purpose of this class is unclear to me (kruescho, 06/10/2012)
 *
 */
open class TableMap : AbstractTableModel(), TableModelListener {
    var model: TableModel? = null
        private set

    override fun isCellEditable(i: Int, j: Int): Boolean {
        return model!!.isCellEditable(i, j)
    }

    override fun getColumnClass(i: Int): Class<*> {
        return model!!.getColumnClass(i)
    }

    override fun getColumnCount(): Int {
        return if (model != null) model!!.columnCount else 0
    }

    override fun getColumnName(i: Int): String {
        return model!!.getColumnName(i)
    }

    open fun setModel(tablemodel: TableModel) {
        model = tablemodel
        tablemodel.addTableModelListener(this)
    }

    override fun getRowCount(): Int {
        return if (model != null) model!!.rowCount else 0
    }

    override fun setValueAt(obj: Any, i: Int, j: Int) {
        model!!.setValueAt(obj, i, j)
    }

    override fun getValueAt(i: Int, j: Int): Any? {
        return model?.getValueAt(i, j)
    }

    override fun tableChanged(tablemodelevent: TableModelEvent) {
        fireTableChanged(tablemodelevent)
    }

    companion object {
        private const val serialVersionUID = 5022212679370349761L
    }
}
