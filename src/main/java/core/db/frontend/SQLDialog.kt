package core.db.frontend

import core.db.DBManager
import core.gui.HOMainFrame
import core.gui.theme.HOIconName
import core.gui.theme.ThemeManager
import core.util.BrowserLauncher
import core.util.HOLogger
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.event.*
import java.util.*
import java.util.regex.Pattern
import javax.swing.*

class SQLDialog : JDialog(HOMainFrame, "Simple SQL Editor"), ActionListener {
    private var table: JTable? = null
    private var txtArea: JTextPane? = null
    private var lbl: JLabel? = null
    private var tree: JTree? = null
    private lateinit var columnNames: Array<String?>
    protected var statements: ArrayList<String>
    private var index = 0
    var CRState: Boolean
    private val butBook: JButton
    private val butExecute: JButton
    private val butprevious: JButton
    private val butnext: JButton
    private val buthelp: JButton
    private val buttables: JButton

    init {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE)
        statements = ArrayList()
        CRState = false
        butBook = JButton("History")
        butExecute = JButton(ThemeManager.getIcon(HOIconName.TOOTHEDWHEEL))
        butprevious = JButton(" < ")
        butnext = JButton(" > ")
        buthelp = JButton("HSQL Website")
        buttables = JButton(ThemeManager.getIcon(HOIconName.INFO))
        initialize()
    }

    private fun initialize() {
        val width = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds.getWidth().toInt()
        val height = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds.getHeight().toInt()
        setLocation((width - 800) / 2, (height - 480) / 2)
        setSize(800, 480)
        contentPane.setLayout(BorderLayout())
        val toolBar = JToolBar("Toolbar")
        addButtons(toolBar)
        contentPane.add(toolBar, "North")
        contentPane.add(middlePanel, "Center")
        contentPane.add(infoLabel, "South")
    }

    private val infoLabel: JLabel
        get() {
            if (lbl == null) {
                lbl = JLabel()
                lbl!!.preferredSize = Dimension(0, 20)
            }
            return lbl!!
        }
    private val middlePanel: JSplitPane
        get() {
            val split = JSplitPane(0)
            split.add(JScrollPane(textArea))
            split.add(JScrollPane(getTable()))
            return split
        }

    private fun addButtons(toolbar: JToolBar) {
        initializeButton(toolbar, buttables, "F1 - shows all tables")
        initializeButton(toolbar, butprevious, "F2 - previous statement")
        initializeButton(toolbar, butBook, "F3 - show all statements")
        initializeButton(toolbar, butnext, "F4 - next statement")
        initializeButton(toolbar, butExecute, "F5 - execute the statement")
        initializeButton(toolbar, buthelp, "HSQL Doc")
    }

    private fun initializeButton(toolbar: JToolBar, button: JButton, tooltip: String) {
        button.addActionListener(this)
        button.setToolTipText(tooltip)
        button.setBackground(Color.WHITE)
        toolbar.add(button)
    }

    private fun openHSQLDoc() {
        try {
            BrowserLauncher.openURL("http://hsqldb.sourceforge.net/web/hsqlDocsFrame.html")
        } catch (ex: Exception) {
            HOLogger.instance().log(SQLDialog::class.java, ex)
        }
    }

    protected fun showAllStatements() {
        val tmp: Array<Any> = statements.toTypedArray()
        val display = arrayOfNulls<String>(tmp.size)
        for (i in tmp.indices) {
            display[i] = tmp[i].toString()
            if (display[i]!!.length > 30) display[i] = display[i]!!.substring(0, 30) + "..."
        }
        val list: JList<*> = JList<Any?>(display)
        list.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val i = (e.source as JList<*>).selectedIndex
                textArea.setText(statements.toTypedArray()[i].toString())
            }
        })
        val scroll = JScrollPane(list)
        JOptionPane.showMessageDialog(null, scroll)
    }

    val textArea: JTextPane
        get() {
            if (txtArea == null) {
                txtArea = JTextPane()
                txtArea!!.setCaretPosition(0)
                txtArea!!.addKeyListener(HighlightingKeyListener(txtArea!!))
                txtArea!!.addKeyListener(object : KeyAdapter() {
                    override fun keyPressed(e: KeyEvent) {
                        if (112 == e.keyCode) openTablesDialog()
                        if (113 == e.keyCode) back()
                        if (114 == e.keyCode) showAllStatements()
                        if (115 == e.keyCode) forward()
                        if (116 == e.keyCode) refresh()
                    }
                })
            }
            return txtArea!!
        }

    private fun getTable(): JScrollPane {
        val model = DummyTableModel(null, null)
        table = JTable(model)
        table!!.autoResizeMode = 0
        val scroll = JScrollPane(table)
        scroll.preferredSize = Dimension(0, 150)
        return scroll
    }

    fun refresh() {
        val txt = textArea.getText().uppercase(Locale.getDefault())
        val pattern = Pattern.compile("SELECT .* FROM ")
        val matcher = pattern.matcher(txt)
        if (matcher.find()) {
            val model = DummyTableModel(values, columnNames)
            table!!.setModel(model)
        } else {
            try {
                val rows = DBManager.instance().adapter.executeUpdate(textArea.getText())
                infoLabel.setText("$rows rows updated")
            } catch (ex: Exception) {
                handleException(ex)
            }
        }
        statements.add(textArea.getText())
        index++
    }

    private fun handleException(ex: Exception) {
        ex.printStackTrace()
    }

    private val values: Array<Array<Any?>>?
        get() {
            var values = null as Array<Array<Any?>>?
            var rowCount = 0
            val txt = textArea.getText().uppercase(Locale.getDefault())
            val index1 = txt.indexOf("FROM")
            val select = txt.substring(0, index1 - 1)
            val sql = txt.substring(index1, textArea.getText().length)
            try {
                val start = System.currentTimeMillis()
                val rs = DBManager.instance().adapter.executeQuery("$select $sql")
                rs!!.last()
                rowCount = rs.row
                rs.beforeFirst()
                val metaData = rs.metaData
                values = Array(rowCount) { arrayOfNulls(metaData.columnCount) }
                columnNames = arrayOfNulls(metaData.columnCount)
                for (i in columnNames.indices) columnNames[i] = metaData.getColumnName(i + 1)
                var i = 0
                while (rs.next()) {
                    for (j in columnNames.indices) {
                        val content = rs.getString(j + 1)
                        values[i][j] = if (content != null) content else "(null)"
                    }
                    i++
                }
                rs.close()
                val ergebnis = System.currentTimeMillis() - start
                infoLabel.setText("$rowCount rows ($ergebnis ms)")
            } catch (ex: Exception) {
                JOptionPane.showMessageDialog(null, "SQL Statement is wrong!")
            }
            return values
        }

    private fun forward() {
        if (index > 0 && index < statements.size) {
            textArea.text = statements[index]
            index++
        }
    }

    private fun back() {
        if (index > 1) {
            textArea.text = statements[index - 2]
            index--
        }
    }

    override fun actionPerformed(arg0: ActionEvent) {
        if (arg0.source === butBook) showAllStatements()
        if (arg0.source === butExecute) refresh()
        if (arg0.source === butprevious) back()
        if (arg0.source === butnext) forward()
        if (arg0.source === buthelp) openHSQLDoc()
        if (arg0.source === buttables) openTablesDialog()
    }

    private fun openTablesDialog() {
        val dialog = TablesDialog(this)
        dialog.isVisible = true
    }
}