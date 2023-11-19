package core.gui

import core.gui.theme.HOIconName
import core.gui.theme.ImageUtilities
import core.gui.theme.ThemeManager
import core.util.ExceptionUtils
import core.util.HOLogger
import java.awt.*
import javax.swing.*

/**
 *
 * @author kruescho
 */
class ExceptionDialog(msg: String?, t: Throwable) : JDialog() {
    private var detailsPanel: JPanel? = null
    private var textArea: JTextArea? = null
    private var detailsButton: JButton? = null
    private var savedDetailsSize: Dimension? = null
    private var showDetailsImage: Icon? = null
    private var hideDetailsImage: Icon? = null
    private val throwable: Throwable

    init {
        setModal(true)
        throwable = t
        setTitle(msg)
        setDefaultCloseOperation(DISPOSE_ON_CLOSE)
        initComponents()
    }

    private fun initComponents() {
        try {
            showDetailsImage = ThemeManager.getIcon(HOIconName.CONTROL_DOUBLE_270)
            hideDetailsImage = ThemeManager.getIcon(HOIconName.CONTROL_DOUBLE_090)
            setIconImage(ImageUtilities.iconToImage(ThemeManager.getIcon(HOIconName.EXCLAMATION_RED)))
        } catch (ex: Exception) {
            HOLogger.instance().log(javaClass, ex)
        }
        layout = BorderLayout()
        val topPanel = JPanel()
        topPanel.setLayout(GridBagLayout())
        val label = JLabel("An error occurred.")
        label.setFont(label.font.deriveFont(label.font.style xor Font.BOLD))
        try {
            label.setIcon(ThemeManager.getIcon(HOIconName.EXCLAMATION_RED))
        } catch (ex: Exception) {
            HOLogger.instance().log(javaClass, ex)
        }
        var gbc = GridBagConstraints()
        gbc.insets = Insets(8, 8, 4, 4)
        gbc.anchor = GridBagConstraints.WEST
        topPanel.add(label, gbc)
        val errorLabel = JLabel(getMessage(throwable))
        gbc = GridBagConstraints()
        gbc.gridy = 1
        gbc.insets = Insets(4, 8, 8, 4)
        gbc.anchor = GridBagConstraints.WEST
        topPanel.add(errorLabel, gbc)
        detailsButton = JButton("details", showDetailsImage)
        gbc = GridBagConstraints()
        gbc.insets = Insets(4, 4, 8, 8)
        gbc.gridy = 1
        gbc.gridx = 1
        gbc.anchor = GridBagConstraints.WEST
        gbc.weightx = 1.0
        topPanel.add(detailsButton, gbc)
        detailsButton!!.addActionListener { switchDetails() }
        add(topPanel, BorderLayout.NORTH)
        pack()
    }

    private fun getMessage(throwable: Throwable): String? {
        var message = throwable.message
        if (message == null) {
            message = throwable.javaClass.getName()
        }
        return message
    }

    private fun createDetails() {
        detailsPanel = JPanel(BorderLayout())
        textArea = JTextArea()
        textArea!!.isEditable = false
        textArea!!.text = ExceptionUtils.getStackTrace(throwable)
        val scrollPane = JScrollPane(textArea)
        detailsPanel!!.add(scrollPane, BorderLayout.CENTER)
        detailsPanel!!.preferredSize = Dimension(650, 300)
        savedDetailsSize = Dimension(detailsPanel!!.getPreferredSize())
        add(detailsPanel, BorderLayout.CENTER)
    }

    private fun switchDetails() {
        if (detailsPanel != null && detailsPanel!!.isVisible) {
            savedDetailsSize = Dimension(detailsPanel!!.size)
            detailsButton!!.setIcon(showDetailsImage)
            detailsPanel!!.isVisible = false
        } else {
            if (detailsPanel == null) {
                createDetails()
            } else {
                detailsPanel!!.preferredSize = savedDetailsSize
            }
            detailsButton!!.setIcon(hideDetailsImage)
            detailsPanel!!.isVisible = true
            textArea!!.setCaretPosition(0)
        }
        pack()
    }
}
