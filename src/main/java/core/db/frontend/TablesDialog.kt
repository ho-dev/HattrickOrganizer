package core.db.frontend

import core.db.DBManager
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.event.*
import javax.swing.JDialog
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.JTable

internal class TablesDialog(owner: SQLDialog?) : JDialog(owner, "Tables"), MouseListener {
    private var tablelist: JList<*>? = null
    private var tableColumns: JTable? = null
    private fun initialize() {
        val width = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds.getWidth().toInt()
        val height = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds.getHeight().toInt()
        setLocation((width - 450) / 2, (height - 410) / 2)
        setSize(450, 410)
        contentPane.setLayout(BorderLayout())
        contentPane.add(middlePanel, BorderLayout.WEST)
        contentPane.add(tablePanel, BorderLayout.CENTER)
    }

    private val middlePanel: JScrollPane
        get() = JScrollPane(list)
    private val tablePanel: JScrollPane
        get() = JScrollPane(table)
    private val list: JList<*>
        get() {
            if (tablelist == null) {
                tablelist = JList(DBManager.jdbcAdapter.getAllTableNames())
                tablelist!!.addMouseListener(this)
            }
            return tablelist!!
        }
    private val table: JScrollPane
        get() {
            tableColumns = JTable(DummyTableModel(null, null))
            tableColumns!!.addMouseListener(this)
            tableColumns!!.autoResizeMode = 0
            val scroll = JScrollPane(tableColumns)
            scroll.preferredSize = Dimension(0, 150)
            return scroll
        }

    @Throws(Exception::class)
    private fun setTable(tableName: String): Array<Array<Any?>> {
        val rs = DBManager.jdbcAdapter.executeQuery("SELECT * FROM $tableName where 1 = 2")
        val columns = rs!!.metaData.columnCount
        val columnData = Array(columns) { arrayOfNulls<Any>(4) }
        for (i in 0 until columns) {
            columnData[i][0] = rs.metaData.getColumnName(i + 1)
            columnData[i][1] = rs.metaData.getColumnTypeName(i + 1)
            columnData[i][2] = rs.metaData.getColumnDisplaySize(i + 1)
        }
        rs.close()
        return columnData
    }

    override fun mouseClicked(e: MouseEvent) {
        val area = (owner as SQLDialog).textArea
        if (e.source is JList<*>) if (e.clickCount == 2)
            area.text = area.getText() + " " + list.getSelectedValue()
        else
            refresh()
        if (e.source is JTable && e.clickCount == 2)
            area.text = area.getText() + " " + tableColumns!!.getValueAt(tableColumns!!.selectedRow, 0)
    }

    override fun mousePressed(mouseevent: MouseEvent) {}
    override fun mouseReleased(mouseevent: MouseEvent) {}
    override fun mouseEntered(mouseevent: MouseEvent) {}
    override fun mouseExited(mouseevent: MouseEvent) {}
    protected fun refresh() {
        val tableName = list.getSelectedValue().toString()
        try {
            val model1 = DummyTableModel(setTable(tableName), COLUMNNAMES)
            tableColumns!!.setModel(model1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE)
        initialize()
    }

    companion object {
        private const val serialVersionUID = -1584823279333655850L
        private val COLUMNNAMES = arrayOf<String?>(
            "NAME", "TYP", "SIZE"
        )
    }
}