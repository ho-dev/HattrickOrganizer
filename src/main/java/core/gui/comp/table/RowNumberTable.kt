package core.gui.comp.table

import java.awt.Component
import java.awt.Font
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.JViewport
import javax.swing.UIManager
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableColumn

/*
*	Use a JTable as a renderer for row numbers of a given main table.
*  This table must be added to the row header of the scrollpane that
*  contains the main table.
*  
*  source: http://tips4java.wordpress.com/2008/11/18/row-number-table/
*/
class RowNumberTable(private val main: JTable) : JTable(), ChangeListener, PropertyChangeListener {
    init {
        main.addPropertyChangeListener(this)
        setFocusable(false)
        setAutoCreateColumnsFromModel(false)
        setModel(main.model)
        setSelectionModel(main.selectionModel)
        val column = TableColumn()
        column.setHeaderValue(" ")
        addColumn(column)
        column.setCellRenderer(RowNumberRenderer())
        getColumnModel().getColumn(0).setPreferredWidth(50)
        preferredScrollableViewportSize = getPreferredSize()
    }

    override fun addNotify() {
        super.addNotify()
        val c: Component = parent

        // Keep scrolling of the row table in sync with the main table.
        (c as? JViewport)?.addChangeListener(this)
    }

    /*
	 * Delegate method to main table
	 */
    override fun getRowCount(): Int {
        return main.getRowCount()
    }

    override fun getRowHeight(row: Int): Int {
        return main.getRowHeight(row)
    }

    /*
	 * This table does not use any data from the main TableModel, so just return
	 * a value based on the row parameter.
	 */
    override fun getValueAt(row: Int, column: Int): Any {
        return (row + 1).toString()
    }

    /*
	 * Don't edit data in the main TableModel by mistake
	 */
    override fun isCellEditable(row: Int, column: Int): Boolean {
        return false
    }

    //
    // Implement the ChangeListener
    //
    override fun stateChanged(e: ChangeEvent) {
        // Keep the scrolling of the row table in sync with main table
        val viewport = e.source as JViewport
        val scrollPane = viewport.parent as JScrollPane
        scrollPane.verticalScrollBar
            .setValue(viewport.getViewPosition().y)
    }

    //
    // Implement the PropertyChangeListener
    //
    override fun propertyChange(e: PropertyChangeEvent) {
        // Keep the row table in sync with the main table
        if ("selectionModel" == e.propertyName) {
            setSelectionModel(main.selectionModel)
        }
        if ("model" == e.propertyName) {
            setModel(main.model)
        }
    }

    /*
	 * Borrow the renderer from JDK1.4.2 table header
	 */
    private class RowNumberRenderer : DefaultTableCellRenderer() {
        init {
            setHorizontalAlignment(CENTER)
        }

        override fun getTableCellRendererComponent(
            table: JTable,
            value: Any, isSelected: Boolean, hasFocus: Boolean, row: Int,
            column: Int
        ): Component {
            val header = table.tableHeader
            if (header != null) {
                setForeground(header.getForeground())
                setBackground(header.getBackground())
                setFont(header.font)
            }
            if (isSelected) {
                setFont(font.deriveFont(Font.BOLD))
            }
            setText(value.toString() ?: "")
            setBorder(UIManager.getBorder("TableHeader.cellBorder"))
            return this
        }
    }
}
