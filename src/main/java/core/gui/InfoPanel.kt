package core.gui

import core.gui.comp.panel.ImagePanel
import core.gui.theme.HOColorName
import core.gui.theme.ThemeManager
import java.awt.Color

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets

import javax.swing.*
import javax.swing.border.BevelBorder


/**
 * Information panel at the bottom of the MainFrame
 */
class InfoPanel : ImagePanel() {

    init {
        initComponents()
    }

    private lateinit var progressBar:JProgressBar
    private lateinit var infoLabel:JTextField

    /**
     * set the information text and increments progress bar value
     */
    fun setInformation(information: String, progressIncrement: Int) {
        var newProgress = getProgress() + progressIncrement
        if (newProgress > 100) newProgress = 100
        setProgressbarValue(newProgress)
        setInformation(information)
    }

    /**
     * set the information text and Color
     */
    fun setInformation(text: String, color: Color) {
        infoLabel.text = text
        infoLabel.foreground = color
        paintComponentImmediately(infoLabel)
    }

    /**
     * set the information text
     */
    private fun setInformation(text: String) = setInformation(text, INFOFARBE)

    /**
     * set progress bar value
     *
     * @param value min=0, max=100
     *              values outside this range will reset the progress bar (value = 0)
     */
    fun setProgressbarValue(value: Int) {
        var localValue = value
        if (value < 0 || value > 100) {
            localValue = 0 // reset progress bar
        }
        progressBar.setValue(localValue)
        paintComponentImmediately(progressBar)
    }

    private fun getProgress(): Int = progressBar.value

    private fun paintComponentImmediately(component: JComponent) {
        val rect = component.bounds
        rect.x = 0
        rect.y = 0
        component.paintImmediately(rect)
    }

    /**
     * create the components
     */
    fun initComponents() {
        progressBar = JProgressBar(0, 100)
        infoLabel = JTextField()

        this.setBorder(BevelBorder(BevelBorder.LOWERED))

        //Constraints
        val constraint = GridBagConstraints()
        constraint.insets = Insets(4, 9, 4, 4)

        //Layout
        val layout = GridBagLayout()
        setLayout(layout)

        infoLabel.isEditable = false
        infoLabel.setOpaque(false)
        constraint.fill = GridBagConstraints.HORIZONTAL
        constraint.weightx = 6.0
        constraint.weighty = 1.0
        constraint.gridx = 0
        constraint.gridy = 0
        layout.setConstraints(infoLabel, constraint)
        add(infoLabel)

        constraint.fill = GridBagConstraints.HORIZONTAL
        constraint.weightx = 2.0
        constraint.weighty = 1.0
        constraint.gridx = 2
        constraint.gridy = 0
        layout.setConstraints(progressBar, constraint)
        add(progressBar)
    }

    companion object {
        // color for error messages
        val FEHLERFARBE = ThemeManager.getColor(HOColorName.LABEL_ERROR_FG)

        // color for info messages
        val INFOFARBE = ThemeManager.getColor(HOColorName.LABEL_FG)

        // color for success messages
        val ERFOLGSFARBE = ThemeManager.getColor(HOColorName.LABEL_SUCCESS_FG)
    }
}
