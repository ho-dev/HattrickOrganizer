package core.gui.language

import core.gui.HOMainFrame
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.event.ActionListener
import javax.swing.*

/**
 * Class to implement a language resource file editor.
 * @author edswifa
 */
class LanguageEditorDialog : JDialog(HOMainFrame, "Language File Editor") {
    private var toolBarPanel: JPanel? = null
    private var destinationTable: JTable? = null
    private lateinit var languageComboBox: JComboBox<Any?>
    private var saveButton: JButton? = null
    private var destinationTableModel: LanguageTableModel? = null

    init {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE)
        initComponents()
        pack()
    }

    private fun initComponents() {
        layout = BorderLayout()
        initToolBar()
        initTables()
    }

    private fun initTables() {
        destinationTable = JTable()
        destinationTable!!.setAutoCreateRowSorter(false)
        add(JScrollPane(destinationTable), BorderLayout.CENTER)
    }

    private fun initToolBar() {
        toolBarPanel = JPanel()
        toolBarPanel!!.setLayout(FlowLayout(FlowLayout.LEFT))
        add(toolBarPanel, BorderLayout.NORTH)
        languageComboBox = JComboBox(LanguageComboBoxModel())
        languageComboBox.setToolTipText("Select a language to be modified")
        languageComboBox.addActionListener(ActionListener {
            destinationTableModel = LanguageTableModel(languageComboBox.getSelectedItem() as String)
            destinationTable!!.setModel(destinationTableModel)
            saveButton!!.setEnabled(true)
        })
        toolBarPanel!!.add(languageComboBox)
        saveButton = JButton("Save")
        saveButton!!.setToolTipText("Save the modifed language file")
        saveButton!!.setEnabled(false)
        saveButton!!.addActionListener {
            if (destinationTableModel != null) {
                destinationTableModel!!.save()
            }
        }
        toolBarPanel!!.add(saveButton)
        toolBarPanel!!.add(initHintText())
    }

    private fun initHintText(): JTextPane {
        val hintText = JTextPane()
        val sb = StringBuffer()
        sb.append("1. Select language to edit using drop down box.\n")
        sb.append("2. The chosen language will appear in the right hand table. All missing keys will be added with an English value\n")
        sb.append("3. Double click in the value cell on the right hand table to edit the value.\n")
        sb.append("4. After clicking the save button then the changed properties file needs to be passed to a developer to commit into the code repository.")
        hintText.isEditable = false
        hintText.text = sb.toString()
        return hintText
    }
}
