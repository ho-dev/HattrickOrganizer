package core.db.backup

import core.db.user.UserManager
import core.file.ExampleFileFilter
import core.file.ZipHelper
import core.gui.comp.panel.ImagePanel
import core.util.HOLogger
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GraphicsEnvironment
import java.io.File
import javax.swing.*

/**
 * Backup management dialog
 *
 * @author Thorsten Dietz
 */
class BackupDialog : JDialog() {
    private val okButton = JButton("Restore")
    private val cancelButton = JButton("Cancel")
    private var list: JList<File>? = null

    init {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE)
        initialize()
    }

    private fun initialize() {
        setTitle("Restore database")
        val dialogWidth = 320
        val dialogHeight = 320

        val windowBounds =  GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds
        val width = windowBounds.getWidth().toInt()
        val height = windowBounds.getHeight().toInt()

        setLocation((width - dialogWidth) / 2, (height - dialogHeight) / 2)
        setSize(dialogWidth, dialogHeight)

        val contentPane = contentPane
        contentPane.add(topPanel, BorderLayout.NORTH)
        contentPane.add(getList(), BorderLayout.CENTER)
        contentPane.add(createButtons(), BorderLayout.SOUTH)
    }

    private val topPanel: JPanel
        get() {
            val panel: JPanel = ImagePanel()
            panel.add(JLabel("Select a database (ZIP-file) to restore from:"))
            return panel
        }

    private fun createButtons(): JPanel {
        val buttonPanel: JPanel = ImagePanel()
        (buttonPanel.layout as FlowLayout).setAlignment(FlowLayout.RIGHT)
        okButton.addActionListener {
            try {
                ZipHelper.unzip(list?.getSelectedValue() as File, File(UserManager.instance().currentUser.dbFolder))
            } catch (e1: Exception) {
                HOLogger.instance().log(javaClass, e1)
            }
            close()
        }
        cancelButton.addActionListener { close() }
        buttonPanel.add(okButton)
        buttonPanel.add(cancelButton)
        return buttonPanel
    }

    private fun getList(): JScrollPane {
        val dbDirectory = File(UserManager.instance().currentUser.dbFolder)
        val filter = ExampleFileFilter("zip")
        filter.isIgnoreDirectories = true
        val files = dbDirectory.listFiles(filter)
        list = JList<File>(files)
        return JScrollPane(list)
    }

    private fun close() {
        isVisible = false
        dispose()
    }
}
